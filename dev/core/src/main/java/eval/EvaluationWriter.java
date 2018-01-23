package eval;

import org.apache.uima.analysis_component.AnalysisComponent;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasConsumer_ImplBase;
import org.apache.uima.jcas.JCas;

public class EvaluationWriter extends JCasConsumer_ImplBase {

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

    }
}
