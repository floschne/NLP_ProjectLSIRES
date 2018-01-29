package eval;

import data.input.Language;
import edu.stanford.nlp.util.ConfusionMatrix;
import language_detection.type.QueryLanguageAnnotation;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_component.AnalysisComponent;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasConsumer_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;

public class LanguageDetectionEvaluator extends JCasConsumer_ImplBase {
    private Logger logger = null;
    private Integer numOfProcessedQueries;

    public static class AmbiguousLanguageAnnotationsException extends Exception {
        public AmbiguousLanguageAnnotationsException() {
            super("Ambiguous Languages for Query!");
        }
    }

    private class NoGoldLanguageSetException extends Exception {
        NoGoldLanguageSetException() {
            super("No gold language was set for the query! (Has to be done in Reader by setting jCas language)");
        }
    }

    private ConfusionMatrix<Language> confusionMatrix;

    @Override
    public void initialize(UimaContext context) throws ResourceInitializationException {
        super.initialize(context);
        this.confusionMatrix = new ConfusionMatrix<>();
        this.logger = context.getLogger();
        this.numOfProcessedQueries = 0;
    }

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
        if (aJCas.getDocumentLanguage() == null || aJCas.getDocumentLanguage().isEmpty())
            throw new AnalysisEngineProcessException(new NoGoldLanguageSetException());

        if (JCasUtil.select(aJCas, QueryLanguageAnnotation.class).size() != 1)
            throw new AnalysisEngineProcessException(new AmbiguousLanguageAnnotationsException());

        Language gold = Language.valueOf(aJCas.getDocumentLanguage().toUpperCase());
        Language detected = null;
        for (QueryLanguageAnnotation qla : JCasUtil.select(aJCas, QueryLanguageAnnotation.class)) {
            detected = Language.valueOf(qla.getLanguageCode().toUpperCase());
            this.confusionMatrix.add(detected, gold);
        }

        this.numOfProcessedQueries++;
    }

    /**
     * This has to get called explicitly by calling the destroy method of the {@link org.apache.uima.analysis_engine.AnalysisEngine} that holds this consumer!
     */
    @Override
    public void destroy() {
        super.destroy();

        StringBuilder sb = new StringBuilder();
        sb.append("\n").append("####### Results of Evaluation #######").append("\n");
        sb.append("Number of processed Queries: " + numOfProcessedQueries).append("\n");
        sb.append("Confusion Matrix with Scores: ").append("\n");
        sb.append(this.confusionMatrix.printTable());

        logger.log(Level.INFO, sb.toString());
    }
}
