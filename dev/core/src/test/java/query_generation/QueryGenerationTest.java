package query_generation;

import data.input.Language;
import org.apache.uima.UIMAException;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class QueryGenerationTest {

    @Test
    public void queryGenerationSingleLanguage() throws IOException, UIMAException {
        Boolean news = false;
        Boolean wiki = true;
        int numQueries = 100;
        String language = "DE";
        QueryGenerator queryGenerator = new QueryGenerator(news, wiki, numQueries, language);
        queryGenerator.generateQueries();

        Assert.assertNotNull(queryGenerator.getQueriesListOfLanguage(Language.DE));
        Assert.assertEquals(queryGenerator.getQueriesListOfLanguage(Language.DE).size(), numQueries);

        news = true;
        wiki = false;
        numQueries = 200;
        queryGenerator = new QueryGenerator(news, wiki, numQueries, language);
        queryGenerator.generateQueries();

        Assert.assertNotNull(queryGenerator.getQueriesListOfLanguage(Language.DE));
        Assert.assertEquals(queryGenerator.getQueriesListOfLanguage(Language.DE).size(), numQueries);

    }

    @Test
    public void queryGenerationMultipleLanguages() throws IOException, UIMAException {
        Boolean news = false;
        Boolean wiki = true;
        int numQueries = 100;
        String language = "DE, EN, ES";
        QueryGenerator queryGenerator = new QueryGenerator(news, wiki, numQueries, language);
        queryGenerator.generateQueries();

        Assert.assertNotNull(queryGenerator.getQueriesListOfLanguage(Language.DE));
        Assert.assertEquals(queryGenerator.getQueriesListOfLanguage(Language.DE).size(), numQueries);

        Assert.assertNotNull(queryGenerator.getQueriesListOfLanguage(Language.EN));
        Assert.assertEquals(queryGenerator.getQueriesListOfLanguage(Language.EN).size(), numQueries);

        Assert.assertNotNull(queryGenerator.getQueriesListOfLanguage(Language.ES));
        Assert.assertEquals(queryGenerator.getQueriesListOfLanguage(Language.ES).size(), numQueries);
    }


}
