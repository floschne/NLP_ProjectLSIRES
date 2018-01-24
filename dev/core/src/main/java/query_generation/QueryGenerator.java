package query_generation;

import data.input.Language;
import data.input.LeipzigSentencesCorporaReader;
import de.tudarmstadt.ukp.dkpro.core.tokit.LineBasedSentenceSegmenter;
import org.apache.uima.UIMAException;
import org.apache.uima.UIMA_IllegalStateException;
import org.apache.uima.UimaContext;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.component.JCasCollectionReader_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import java.io.IOException;
import java.util.List;

/**
 * QueryGenerator that can be used both inside and outside of UIMA Framework
 */
public class QueryGenerator extends JCasCollectionReader_ImplBase {

    private class QueryGeneratorNotReadyException extends Exception {
        public QueryGeneratorNotReadyException(String msg) {
            super(msg);
        }
    }

    private Integer currentQueryIndex;
    private Boolean isInitializedForUima;
    private boolean queriesGenerated;

    public static final String PARAM_QUERY_LANGUAGES = "LanguagesOfQueries";
    @ConfigurationParameter(name = PARAM_QUERY_LANGUAGES, description = "A comma-separated list of languages of the generated queries. Possible Languages: DE,EN,ES", mandatory = false, defaultValue = "DE,EN,ES")
    private String queryLanguages;

    public static final String PARAM_NUMBER_OF_QUERIES = "NumberOfQueries";
    @ConfigurationParameter(name = PARAM_NUMBER_OF_QUERIES, description = "The number of queries that should be generated per language", mandatory = false, defaultValue = "10000")
    private Integer numOfQueries;

    public static final String PARAM_GEN_WIKI_QUERIES = "GenerateWikiQueries";
    @ConfigurationParameter(name = PARAM_GEN_WIKI_QUERIES, description = "Boolean Flag: If true generate queries from wiki articles data source", mandatory = false, defaultValue = "True")
    private Boolean wikiQueries;

    public static final String PARAM_GEN_NEWS_QUERIES = "GenerateNewsQueries";
    @ConfigurationParameter(name = PARAM_GEN_NEWS_QUERIES, description = "Boolean Flag: If true generate queries from news articles data source", mandatory = false, defaultValue = "False")
    private Boolean newsQueries;

    /**
     * Standard ctor to make QueryGenerator usable with @{@link AnalysisEngineFactory} or to use default parameters:
     * genNewsQueries = false, genWikiQueries = true, numOfQueries = 10000, queryLanguages = "de,en,es
     */
    public QueryGenerator() {
        init(false, true, 10000, "de,en,es");
    }

    /**
     * @param genNewsQueries Boolean Flag: If true generate queries from news articles data source
     * @param genWikiQueries Boolean Flag: If true generate queries from wiki articles data source
     * @param numOfQueries   The number of queries that should be generated per language
     * @param queryLanguages A comma-separated list of languages of the generated queries. Possible Languages: DE,EN,ES
     */
    public QueryGenerator(Boolean genNewsQueries, Boolean genWikiQueries, Integer numOfQueries, String queryLanguages) {
        init(genNewsQueries, genWikiQueries, numOfQueries, queryLanguages);
    }

    /**
     * Method to initialize the QueryGenerator an reset it.
     *
     * @param genNewsQueries Boolean Flag: If true generate queries from news articles data source
     * @param genWikiQueries Boolean Flag: If true generate queries from wiki articles data source
     * @param numOfQueries   The number of queries that should be generated per language
     * @param queryLanguages A comma-separated list of languages of the generated queries. Possible Languages: DE,EN,ES
     */
    private void init(Boolean genNewsQueries, Boolean genWikiQueries, Integer numOfQueries, String queryLanguages) {
        this.newsQueries = genNewsQueries;
        this.wikiQueries = genWikiQueries;
        this.numOfQueries = numOfQueries;
        this.queryLanguages = queryLanguages;
        this.currentQueryIndex = 0;
        this.isInitializedForUima = false;
        this.queriesGenerated = false;

        if (!genNewsQueries && !genWikiQueries)
            throw new IllegalArgumentException("Either PARAM_GEN_NEWS_QUERIES, PARAM_GEN_WIKI_QUERIES or both must be true!");

        if (queryLanguages == null || queryLanguages.isEmpty())
            throw new IllegalArgumentException("Languages must not be null or empty!");

        QueryStore.getInstance().reset();
    }

    /**
     * Generates the queries (specified with the ctor of @{@link QueryGenerator}). To get the results call @getQueriesOfAllLanguages
     * or getQueriesListOfLanguage
     *
     * @throws UIMAException
     * @throws IOException
     */
    public void generateQueries() throws UIMAException, IOException {
        CollectionReader leipzigReader = CollectionReaderFactory.createReader(LeipzigSentencesCorporaReader.class,
                LeipzigSentencesCorporaReader.PARAM_CORPORA_LANGUAGES, queryLanguages,
                LeipzigSentencesCorporaReader.PARAM_LOAD_NEWS_CORPORA, newsQueries,
                LeipzigSentencesCorporaReader.PARAM_LOAD_WIKI_CORPORA, wikiQueries);

        AnalysisEngine sentenceSegmenter = AnalysisEngineFactory.createEngine(LineBasedSentenceSegmenter.class);

        AnalysisEngine queryConsumer = AnalysisEngineFactory.createEngine(StoreQueriesConsumer.class,
                StoreQueriesConsumer.PARAM_NUMBER_OF_QUERIES_PER_LANGUAGE, numOfQueries);

        SimplePipeline.runPipeline(
                leipzigReader,
                sentenceSegmenter,
                queryConsumer
        );
        this.queriesGenerated = true;
    }


    /**
     * @return Returns the queries in all languages as a map from a language to the list of queries in that specific language
     */
    public List<Query> getQueriesOfAllLanguages() throws IOException, UIMAException {
        if (!this.queriesGenerated)
            this.generateQueries();
        return QueryStore.getInstance().getQueriesOfAllLanguages();
    }

    /**
     * Returns the list containing the queries of the specified language
     *
     * @param lang the language of the queries that'll be returned
     * @return the list containing the queries of the specified language or null if there are no queries of the language
     */
    public List<Query> getQueriesListOfLanguage(Language lang) throws IOException, UIMAException {
        if (!this.queriesGenerated)
            this.generateQueries();
        return QueryStore.getInstance().getQueryListOfLanguage(lang);
    }

    /**
     * This method should be overwritten by subclasses.
     *
     * @param context the UIMA context the component is running in
     * @throws ResourceInitializationException if a failure occurs during initialization.
     */
    @Override
    public void initialize(UimaContext context) throws ResourceInitializationException {
        super.initialize(context);
        // call init method to perform check of UIMA parameters and general init of other parameters
        this.init(this.newsQueries, this.wikiQueries, this.numOfQueries, this.queryLanguages);

        try {
            this.generateQueries();
        } catch (UIMAException | IOException e) {
            e.printStackTrace();
        }

        this.isInitializedForUima = true;
    }

    /**
     * Subclasses should implement this method rather than {@link #getNext(CAS)}
     *
     * @param jCas the {@link JCas} to store the read data to
     * @throws IOException         if there was a low-level I/O problem
     * @throws CollectionException if there was another problem
     */
    @Override
    public void getNext(JCas jCas) throws IOException, CollectionException {
        if (!isInitializedForUima)
            throw new UIMA_IllegalStateException(new QueryGeneratorNotReadyException("QueryGenerator was not initialized for use in UIMA!"));

        Query query = QueryStore.getInstance().getQueriesOfAllLanguages().get(this.currentQueryIndex++);
        jCas.setDocumentLanguage(query.getLanguage().toString());
        jCas.setDocumentText(query.getQueryText());
    }

    /**
     * Gets whether there are any elements remaining to be read from this
     * <code>CollectionReader</code>.
     *
     * @return true if and only if there are more elements available from this
     * <code>CollectionReader</code>.
     * @throws IOException         if an I/O failure occurs
     * @throws CollectionException if there is some other problem with reading from the Collection
     */
    @Override
    public boolean hasNext() throws IOException, CollectionException {
        return this.currentQueryIndex < QueryStore.getInstance().getQueriesOfAllLanguages().size();
    }

    /**
     * Gets information about the number of entities and/or amount of data that has been read from
     * this <code>CollectionReader</code>, and the total amount that remains (if that information
     * is available).
     * <p>
     * This method returns an array of <code>Progress</code> objects so that results can be reported
     * using different units. For example, the CollectionReader could report progress in terms of the
     * number of documents that have been read and also in terms of the number of bytes that have been
     * read. In many cases, it will be sufficient to return just one <code>Progress</code> object.
     *
     * @return an array of <code>Progress</code> objects. Each object may have different units (for
     * example number of entities or bytes).
     */
    @Override
    public Progress[] getProgress() {
        return new Progress[]{
                new ProgressImpl(currentQueryIndex, QueryStore.getInstance().getQueriesOfAllLanguages().size(), Progress.ENTITIES)
        };
    }
}
