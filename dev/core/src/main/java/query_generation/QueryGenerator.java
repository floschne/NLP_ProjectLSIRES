package query_generation;

import data.input.Language;
import data.input.LeipzigSentencesCorporaReader;
import de.tudarmstadt.ukp.dkpro.core.tokit.LineBasedSentenceSegmenter;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class QueryGenerator {

    private Boolean newsQueries;
    private Boolean wikiQueries;
    private Integer numOfQueries;
    private String languages;


    public QueryGenerator(Boolean newsQueries, Boolean wikiQueries, Integer numOfQueries, String languages) {
        this.newsQueries = newsQueries;
        this.wikiQueries = wikiQueries;
        this.numOfQueries = numOfQueries;
        this.languages = languages;

        if (languages == null || languages.isEmpty())
            throw new IllegalArgumentException("Languages must not be null or empty!");

        QueryStore.getInstance().reset();
    }

    public void generateQueries() throws UIMAException, IOException {
        String basePath = new File("").getAbsolutePath();
        CollectionReader leipzigReader = CollectionReaderFactory.createReader(LeipzigSentencesCorporaReader.class,
                LeipzigSentencesCorporaReader.PARAM_CORPORA_LANGUAGES, languages,
                LeipzigSentencesCorporaReader.PARAM_LOAD_NEWS_CORPORA, newsQueries,
                LeipzigSentencesCorporaReader.PARAM_LOAD_WIKI_CORPORA, wikiQueries,
                LeipzigSentencesCorporaReader.PARAM_PATH_TO_CORPORA_ROOT_DIRECTORY, basePath + "/../../data/leipzigCorpora/");

        AnalysisEngine sentenceSeqmenter = AnalysisEngineFactory.createEngine(LineBasedSentenceSegmenter.class);

        AnalysisEngine queryConsumer = AnalysisEngineFactory.createEngine(QueryGenerationConsumer.class,
                QueryGenerationConsumer.PARAM_NUMBER_OF_QUERIES_PER_LANGUAGE, numOfQueries);

        SimplePipeline.runPipeline(
                leipzigReader,
                sentenceSeqmenter,
                queryConsumer
        );
    }

    /**
     * Wrapper function to make the code more readable and semantically reasonable
     *
     * @return Returns list of queries in all languages as a list of pairs of a language to the list of queries in that specific language
     */
    public List<Pair<Language, List<String>>> getQueriesOfAllLanguages() {
        return QueryStore.getInstance().getQueriesOfAllLanguages();
    }

    /**
     * Wrapper function to make the code more readable and semantically reasonable
     *
     * @param lang the language of the queries that'll be returned
     * @return the list containing the queries of the specified language or null if there are no queries of the language
     */
    public List<String> getQueryListOfLanguage(Language lang) {
        return QueryStore.getInstance().getQueryListOfLanguage(lang);
    }
}
