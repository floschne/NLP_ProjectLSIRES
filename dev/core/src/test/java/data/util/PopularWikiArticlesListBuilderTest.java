package data.util;

import data.input.WikiArticle;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static data.util.PopularWikiArticlesListBuilder.getListOfMostPopularWikiArticles;

public class PopularWikiArticlesListBuilderTest {

    @Test
    public void getListOfMostPopularWikiArticlesTest() throws IOException {
        //only dummy test.. if no exception is thrown basic functionality should be guaranteed
        List<String> popularArticles = getListOfMostPopularWikiArticles(WikiArticle.Language.DE, 2017, 12, 1);
    }

    //TODO more testing (if necessary?!)
}
