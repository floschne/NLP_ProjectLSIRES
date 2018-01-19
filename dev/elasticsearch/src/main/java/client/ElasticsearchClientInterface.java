package client;

import data.input.WikiArticle;
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
     * @return void
     */
    boolean indexArticle(WikiArticle wikiArticle);

    /**
     * @param wikiArticles List of the articles to index
     * @return void
     */
    boolean indexArticles(List<WikiArticle> wikiArticles);

}
