package client;

import data.input.WikiArticle;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.List;

public interface ElasticsearchClientInterface {

    /**
     * @param languageCode Language code that determines the used elasticsearch index. E.g. 'de'
     * @param query Query string. E.g. 'How old is Donald Trump'
     *
     * @return List<Pair<String, Float>> Returns a list of pairs of the IDs and their're Score from the search results.
     */
    List<Pair<String, Float>> findArticleTitlesByLanguageCodeAndQuery(String languageCode, String query);

    /**
     * @param wikiArticle Article to index
     */
    void indexArticle(WikiArticle wikiArticle) throws IOException;

    /**
     * @param wikiArticles List of the articles to index
     */
    void indexArticles(List<WikiArticle> wikiArticles) throws Exception;

}
