package client;

import data.input.WikiArticle;

import java.io.IOException;
import java.util.List;

public interface ElasticsearchClientInterface {

    /**
     * @param languageCode Language code that determines the used elasticsearch index. E.g. 'de'
     * @param query Query string. E.g. 'How old is Donald Trump'
     *
     * @return List<String> Returns a list of the IDs from the search results.
     */
    List<String> findArticleTitlesByLanguageCodeAndQuery(String languageCode, String query);

    /**
     * @param wikiArticle Article to index
     */
    void indexArticle(WikiArticle wikiArticle) throws IOException;

    /**
     * @param wikiArticles List of the articles to index
     */
    void indexArticles(List<WikiArticle> wikiArticles);

}
