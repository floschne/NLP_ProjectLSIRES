package data.input;

import java.io.IOException;
import java.util.MissingResourceException;
import java.util.Set;

public interface WikiArticleLoader {
    /**
     * Downloads the article from Wikipedia with title @title in the language @language and returns it as a @{@link WikiArticle}
     * @param title the title of the article
     * @param language the language of the article
     * @return a @{@link WikiArticle}
     * @throws MissingResourceException if the article was not available
     */
    WikiArticle loadArticle(String title, WikiArticle.Language language) throws MissingResourceException, IOException;
}

