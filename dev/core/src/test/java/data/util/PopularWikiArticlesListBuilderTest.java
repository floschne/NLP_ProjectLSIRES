package data.util;

import data.input.Language;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import static data.util.PopularWikiArticlesListBuilder.getListOfMostPopularWikiArticles;

public class PopularWikiArticlesListBuilderTest {

    @Test
    public void getListOfMostPopularWikiArticlesTest() throws IOException {
        int limit = 257;
        List<String> popularArticles = getListOfMostPopularWikiArticles(Language.DE, 2017, 12, 1, limit, PopularWikiArticlesListBuilder.ListOrdering.ASC);
        Assert.assertEquals(popularArticles.size(), limit);
    }

    @Test
    public void getListOfMostPopularWikiArticlesReverseTest() throws IOException {
        int limit = 257;
        List<String> popularArticles = getListOfMostPopularWikiArticles(Language.DE, 2017, 12, 1, limit, PopularWikiArticlesListBuilder.ListOrdering.ASC);
        List<String> popularArticlesReverse = getListOfMostPopularWikiArticles(Language.DE, 2017, 12, 1, limit, PopularWikiArticlesListBuilder.ListOrdering.DESC);
        Assert.assertEquals(popularArticles.size(), limit);
        Assert.assertEquals(popularArticlesReverse.size(), limit);
        for (int i = 0, j = popularArticlesReverse.size()-1; i < popularArticles.size(); i++, j--) {
            Assert.assertEquals(popularArticles.get(i), popularArticlesReverse.get(j));
        }
    }
}
