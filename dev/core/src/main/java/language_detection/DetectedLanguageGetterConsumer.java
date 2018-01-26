package language_detection;

import data.input.Language;
import eval.LanguageDetectionEvaluator;
import language_detection.type.QueryLanguageAnnotation;
import org.apache.uima.analysis_component.AnalysisComponent;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasConsumer_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

public class DetectedLanguageGetterConsumer extends JCasConsumer_ImplBase {

    public static class LanguageNotYetDetectedException extends Exception {
        public LanguageNotYetDetectedException() {
            super("Language not yet detected!");
        }
    }

    private static class UnableToDetectLanguageException extends Exception {
        public UnableToDetectLanguageException() {
            super("Unable to resolve the detected Language! (Missing QueryLanguageAnnotation!)");
        }
    }

    private static Language DETECTED_LANGUAGE = null;
    private static Boolean LANGUAGE_IS_DETECTED = false;

    /**
     * This method should be overriden by subclasses. Inputs a JCAS to the AnalysisComponent. The
     * AnalysisComponent "owns" this JCAS until such time as {@link #hasNext()} is called and returns
     * false (see {@link AnalysisComponent} for details).
     *
     * @param aJCas a JCAS that this AnalysisComponent should process.
     * @throws AnalysisEngineProcessException if a problem occurs during processing
     */
    @Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException {

        if (JCasUtil.select(aJCas, QueryLanguageAnnotation.class).size() == 0)
            throw new AnalysisEngineProcessException(new UnableToDetectLanguageException());
        if (JCasUtil.select(aJCas, QueryLanguageAnnotation.class).size() != 1)
            throw new AnalysisEngineProcessException(new LanguageDetectionEvaluator.AmbiguousLanguageAnnotationsException());

        Language detected = null;
        for (QueryLanguageAnnotation qla : JCasUtil.select(aJCas, QueryLanguageAnnotation.class))
            detected = Language.valueOf(qla.getLanguageCode().toUpperCase().trim());

        DETECTED_LANGUAGE = detected;
        LANGUAGE_IS_DETECTED = true;
    }

    public static Language getDetectedLanguage() throws LanguageNotYetDetectedException {
        if(!LANGUAGE_IS_DETECTED || DETECTED_LANGUAGE == null)
            throw new LanguageNotYetDetectedException();
        return DETECTED_LANGUAGE;
    }
}
