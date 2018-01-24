package data.util;

import data.input.Language;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

public class PopularWikiArticleTitlesBuilderTest {

    @Test
    public void getListOfMostPopularWikiArticlesTest() throws IOException {
        int limit = 257;
        List<String> popularArticles = PopularWikiArticleTitlesBuilder.getInstance().getListOfMostPopularWikiArticles(Language.DE, 2017, 12, 1, limit, PopularWikiArticleTitlesBuilder.ListOrdering.ASC);
        Assert.assertEquals(popularArticles.size(), limit);
    }

    @Test
    public void getListOfMostPopularWikiArticlesReverseTest() throws IOException {
        int limit = 257;
        List<String> popularArticles = PopularWikiArticleTitlesBuilder.getInstance().getListOfMostPopularWikiArticles(Language.DE, 2017, 12, 1, limit, PopularWikiArticleTitlesBuilder.ListOrdering.ASC);
        List<String> popularArticlesReverse = PopularWikiArticleTitlesBuilder.getInstance().getListOfMostPopularWikiArticles(Language.DE, 2017, 12, 1, limit, PopularWikiArticleTitlesBuilder.ListOrdering.DESC);
        Assert.assertEquals(popularArticles.size(), limit);
        Assert.assertEquals(popularArticlesReverse.size(), limit);
        for (int i = 0, j = popularArticlesReverse.size() - 1; i < popularArticles.size(); i++, j--) {
            Assert.assertEquals(popularArticles.get(i), popularArticlesReverse.get(j));
        }
    }

    @Test
    public void getListOfMostPopularWikiArticlesLimitDETest() throws IOException {
        int limit = 257;
        List<String> popularArticles = PopularWikiArticleTitlesBuilder.getInstance().getListOfMostPopularWikiArticles(Language.DE, 2017, 12, 1, 257, PopularWikiArticleTitlesBuilder.ListOrdering.ASC);
        Assert.assertEquals(popularArticles.size(), limit);
    }


    @Test
    public void getListOfMostPopularWikiArticlesLimitENTest() throws IOException {
        int limit = 257;
        List<String> popularArticles = PopularWikiArticleTitlesBuilder.getInstance().getListOfMostPopularWikiArticles(Language.EN, 2017, 12, 1, 257, PopularWikiArticleTitlesBuilder.ListOrdering.ASC);
        Assert.assertEquals(popularArticles.size(), limit);
    }


    @Test
    public void getListOfMostPopularWikiArticlesLimitESTest() throws IOException {
        int limit = 257;
        List<String> popularArticles = PopularWikiArticleTitlesBuilder.getInstance().getListOfMostPopularWikiArticles(Language.ES, 2017, 12, 1, 257, PopularWikiArticleTitlesBuilder.ListOrdering.ASC);
        Assert.assertEquals(popularArticles.size(), limit);
    }
}
