package client;

import data.input.WikiArticle;
import data.input.WikiHttpApiLoader;
import org.junit.*;

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
        WikiArticle.Language lang = WikiArticle.Language.DE;
        WikiHttpApiLoader loader = WikiHttpApiLoader.getInstance();
        WikiArticle article = loader.loadArticle(title, lang);


        this.client.indexArticle(article);
    }

    @Test
    @Ignore
    public void indexArticlesViaBulkTest() throws IOException {

        WikiArticle.Language lang = WikiArticle.Language.DE;
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
        List<String> ret = this.client.findArticleTitlesByLanguageCodeAndQuery("de", "Pizza macht freude");

        for (String s : ret) {
            System.out.println(s);
        }
    }

}
