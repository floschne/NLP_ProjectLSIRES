package client;

import data.input.WikiArticle;
import java.util.List;

public interface ElasticsearchClientInterface {

    String[] findArticleTitlesByLanguageCodeAndQuery(String languageCode, String query);

    boolean indexArticle(WikiArticle wikiArticle);

    boolean indexArticles(List<WikiArticle> wikiArticles);

}
