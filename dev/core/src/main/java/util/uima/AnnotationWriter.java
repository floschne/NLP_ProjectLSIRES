package util.uima;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasConsumer_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.jcas.tcas.Annotation;
import org.apache.uima.util.Level;

public class AnnotationWriter extends JCasConsumer_ImplBase {

    public static final String LF = System.getProperty("line.separator");

    public void process(JCas jcas) throws AnalysisEngineProcessException {
        StringBuilder sb = new StringBuilder();
        sb.append("=== CAS ===");
        sb.append(LF);
        sb.append("-- Document Text --");
        sb.append(LF);
        sb.append(jcas.getDocumentText());
        sb.append(LF);
        sb.append("-- Annotations --");
        sb.append(LF);

        for (Annotation a : JCasUtil.select(jcas, Annotation.class)) {
            sb.append("[" + a.getType().getShortName() + "] ");
            sb.append("(" + a.getBegin() + ", " + a.getEnd() + ") ");
            sb.append(a.getCoveredText());
            sb.append(LF);
        }

        sb.append(LF);

        getContext().getLogger().log(Level.INFO, sb.toString());
    }
}
