package query_generation;

import data.input.Language;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Sentence;
import org.apache.uima.analysis_component.AnalysisComponent;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasConsumer_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

/**
 * Consumer that stores the generated queries (Sentence Annotations) in the QueryStore
 */
public class StoreQueriesConsumer extends JCasConsumer_ImplBase {

    public static final String PARAM_NUMBER_OF_QUERIES_PER_LANGUAGE = "NumberOfQueriesPerLanguage";
    @ConfigurationParameter(name = PARAM_NUMBER_OF_QUERIES_PER_LANGUAGE, description = "The number of Queries that will be generated per language", mandatory = false, defaultValue = "100")
    private Integer numberOfQueriesPerLanguage;

    QueryStore queryStore = QueryStore.getInstance();

    /**
     * This method should be overriden by subclasses. Inputs a JCAS to the AnalysisComponent. The
     * AnalysisComponent "owns" this JCAS until such time as {@link #hasNext()} is called and returns
     * false (see {@link AnalysisComponent} for details).
     *
     * @param jCas a JCAS that this AnalysisComponent should process.
     * @throws AnalysisEngineProcessException if a problem occurs during processing
     */
    @Override
    public void process(JCas jCas) throws AnalysisEngineProcessException {
        Language lang = Language.valueOf(jCas.getDocumentLanguage().toUpperCase());
        for (Sentence s : JCasUtil.select(jCas, Sentence.class)) {
            if(queryStore.getQueryListOfLanguage(lang) == null)
                queryStore.addQuery(new Query(lang, s.getCoveredText()));
            else {
                if (queryStore.getQueryListOfLanguage(lang).size() >= numberOfQueriesPerLanguage)
                    break;
                queryStore.addQuery(new Query(lang, s.getCoveredText()));
            }
        }
    }
}
