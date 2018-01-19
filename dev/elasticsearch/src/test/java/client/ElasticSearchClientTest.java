package client;

import data.input.WikiArticle;
import data.input.WikiHttpApiLoader;
import org.junit.*;

import java.io.IOException;
import java.net.UnknownHostException;

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
    public void checkClusterHealthTest() throws IOException {

        String title = "Pizza";
        WikiArticle.Language lang = WikiArticle.Language.DE;
        WikiHttpApiLoader loader = WikiHttpApiLoader.getInstance();
        WikiArticle article = loader.loadArticle(title, lang);


        this.client.indexArticle(article);
    }

}
