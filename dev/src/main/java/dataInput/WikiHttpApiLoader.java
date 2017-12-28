package dataInput;

import org.apache.http.client.fluent.Request;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class downloads @{@link WikiArticle}s via the Wikipedia Http API available via https://en.wikipedia.org/w/api.php
 * Please note that the first part of the subdomain (en) specifies the language of the articles that'll be downloaded.
 * <p>
 * Help for API: https://en.wikipedia.org/w/api.php?action=help&modules=query
 * <p>
 * Example API call: https://de.wikipedia.org/w/api.php?action=query&prop=extracts&explaintext&format=xml&titles=Pizza
 */
public class WikiHttpApiLoader implements WikiArticleLoader {

    //TODO think of alternative content to return (such as categories of article etc)

    private static WikiHttpApiLoader singleton = null;

    private WikiHttpApiLoader() {
    }

    public static WikiHttpApiLoader getInstance() {
        if (singleton == null)
            singleton = new WikiHttpApiLoader();
        return singleton;
    }

    private static String WIKI_API_BASE_URL_LANGUAGE_TOKEN = "<lang>";
    private static String WIKI_API_BASE_URL_TITLE_TOKEN = "<title>";
    private static String WIKI_API_BASE_URL_FORMAT_TOKEN = "<format>";
    private static String DEFAULT_FORMAT = "xml";
    private static String API_RESPONSE_HEADING_REGEX_PATTERN = "={2,4} [.\\w\\s]+ ={2,4}";


    private static String WIKI_API_BASE_URL = "https://" + WIKI_API_BASE_URL_LANGUAGE_TOKEN +
            ".wikipedia.org/w/api.php?action=query&prop=extracts&explaintext" +
            "&format=" + WIKI_API_BASE_URL_FORMAT_TOKEN +
            "&titles=" + WIKI_API_BASE_URL_TITLE_TOKEN;

    /**
     * Generates the API call in form of a String containing the URL
     *
     * @param l     the language of the Wikipedia article
     * @param title the title of the Wikipedia article
     * @return a String representing the Wikipedia API URL of the specified query
     */
    private String generateApiCallForQuery(WikiArticle.Language l, String title) {
        return WIKI_API_BASE_URL
                .replace(WIKI_API_BASE_URL_LANGUAGE_TOKEN, l.toString())
                .replace(WIKI_API_BASE_URL_FORMAT_TOKEN, DEFAULT_FORMAT)
                .replace(WIKI_API_BASE_URL_TITLE_TOKEN, title);
    }

    void addApiResponseAsContentToArticle(WikiArticle article, String apiResponse) {
        //get contents as list
        List<String> contents = Arrays.asList(apiResponse.split(API_RESPONSE_HEADING_REGEX_PATTERN));

        //get the headings as list
        List<String> headings = new ArrayList<>();
        Matcher m = Pattern.compile(API_RESPONSE_HEADING_REGEX_PATTERN).matcher(apiResponse);
        while (m.find()) headings.add(m.group());

        //every content needs a heading
        assert contents.size() == headings.size();

        for (int i = 0; i < contents.size(); ++i) {
            //first content never has an explicit heading in Wikipedia.. So we just give it a name
            if (i == 0)
                article.addContent(WikiArticle.WIKI_ARTICLE_INTRO_HEADING, contents.get(i));
            else
                //last heading never has content due to API response (because of the contents layout and the API definition)
                //so we have to shift the index by -1 to get the correct content..
                article.addContent(headings.get(i - 1), contents.get(i));
        }
    }

    /**
     * Downloads the article from Wikipedia with title @title in the language @language and returns it as a @{@link WikiArticle}
     *
     * @param title    the title of the article
     * @param language the language of the article
     * @return a @{@link WikiArticle}
     * @throws MissingResourceException if the article was not available
     */
    @Override
    public WikiArticle loadArticle(String title, WikiArticle.Language language) throws MissingResourceException, IOException {
        //execute HTTP GET via Apache Fluent HC
        String response = Request.Get(generateApiCallForQuery(language, title)).execute().returnContent().asString();

        WikiArticle article = new WikiArticle(title, language);

        addApiResponseAsContentToArticle(article, response);

        return article;
    }

    /**
     * Downloads the articles from Wikipedia with title @title in the languages @languages and returns it as a set of @{@link WikiArticle}
     *
     * @param title     the title of the article
     * @param languages the languages of the article
     * @return a set containing the @{@link WikiArticle}s. If an article cannot be found in the specific language it'll be ignored
     */
    @Override
    public Set<WikiArticle> loadArticleInMultipleLanguages(String title, Set<WikiArticle.Language> languages) {
        //TODO do we really need this? 2lazy2code..
        return null;
    }
}
