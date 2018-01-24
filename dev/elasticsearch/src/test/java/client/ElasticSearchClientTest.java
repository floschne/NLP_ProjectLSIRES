package client;

import data.input.WikiHttpApiLoader;
import data.input.Language;
import data.input.WikiArticle;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class ElasticSearchClientTest {

    private ElasticsearchClient client = null;

    @Before
    public void init() throws UnknownHostException {
        this.client = new ElasticsearchClient();
    }

    @After
    public void closeConnection() {
        this.client.closeConnection();
    }

    @Test
    @Ignore
    public void indexSingleArticleTest() throws IOException {

        String title = "Pizza";
        Language lang = Language.DE;
        WikiHttpApiLoader loader = WikiHttpApiLoader.getInstance();
        WikiArticle article = loader.loadArticle(title, lang);


        this.client.indexArticle(article);
    }

    @Test
    @Ignore
    public void indexArticlesViaBulkTest() throws IOException {

        Language lang = Language.DE;
        WikiHttpApiLoader loader = WikiHttpApiLoader.getInstance();

        WikiArticle article = loader.loadArticle("Pizza", lang);
        WikiArticle article2 = loader.loadArticle("Backofenstein", lang);

        ArrayList<WikiArticle> articles = new ArrayList<WikiArticle>();

        articles.add(article);
        articles.add(article2);


        try {
            this.client.indexArticles(articles);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @Ignore
    public void searchTest() throws IOException {
        List<Pair<String, Float>> ret = this.client.findArticleTitlesByLanguageCodeAndQuery("de", "Pizza macht freude");

        for (Pair<String, Float> p : ret) {
            System.out.println("Article ID: " + p.getLeft() + " - Score: " + p.getRight());
        }
    }

}
