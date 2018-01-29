package data.input;

import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.List;
import java.util.MissingResourceException;
import java.util.concurrent.ExecutionException;

public interface IWikiArticleLoader {
    /**
     * Downloads the article from Wikipedia with title @title in the language @language and returns it as a @{@link WikiArticle}
     * @param title the title of the article
     * @param language the language of the article
     * @return a @{@link WikiArticle}
     * @throws MissingResourceException if the article was not available
     */
    WikiArticle loadArticle(String title, Language language) throws MissingResourceException, IOException;

    /**
     * Downloads the Wikipedia Articles in the articlesToLoad list in parallel
     * @param articlesToLoad List of tuples of title and language of the articles that should be loaded
     * @return a list of articles
     * @throws ExecutionException
     * @throws InterruptedException
     */
    List<WikiArticle> loadArticles(List<Pair<Language, String>> articlesToLoad) throws ExecutionException, InterruptedException;
}

