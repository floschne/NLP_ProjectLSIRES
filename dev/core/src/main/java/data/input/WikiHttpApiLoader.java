package data.input;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.fluent.Request;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.*;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.concurrent.*;
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
public class WikiHttpApiLoader implements IWikiArticleLoader {

    // 'high number' of threads since it's not a big deal firing http requests
    private static final Integer MAX_PARALLEL_THREADS = Runtime.getRuntime().availableProcessors() * 4;

    /**
     * Agent to load a single @{@link WikiArticle}. Implemented for parallel loading of multiple articles
     */
    private class ArticleLoaderAgent implements Callable<WikiArticle> {

        private Language language;
        private String title;
        private static final boolean VERBOSE = false;

        public ArticleLoaderAgent(Language lang, String t) {
            if (lang == null || t == null || t.isEmpty())
                throw new IllegalArgumentException("Language and Title of the WikiArticle that should be loaded must not be null or empty!");
            this.language = lang;
            this.title = t;
        }

        /**
         * Computes a result, or throws an exception if unable to do so.
         *
         * @return computed result
         * @throws Exception if unable to compute a result
         */
        @Override
        public WikiArticle call() throws Exception {
            if (VERBOSE)
                System.out.println("Downloading WikiArticle '" + title + "' in from '" + language + "'.wikipedia.org");
            //execute HTTP GET via Apache Fluent HC
            String apiCall = generateApiCallForQuery(this.language, this.title);
            String response = Request.Get(apiCall).execute().returnContent().asString();


            try {
                if (VERBOSE)
                    System.out.println("Finished downloading WikiArticle '" + title + "' in from '" + language + "'.wikipedia.org");
                return createArticleFromApiResponse(this.title, this.language, response);
            } catch (SAXException | ParserConfigurationException | XPathExpressionException e) {
                e.printStackTrace();
            }
            return null;
        }
    }

    private static final String WIKI_API_BASE_URL_LANGUAGE_TOKEN = "<lang>";
    private static final String WIKI_API_BASE_URL_TITLE_TOKEN = "<title>";
    private static final String WIKI_API_BASE_URL_FORMAT_TOKEN = "<format>";
    private static final String DEFAULT_FORMAT = "xml";
    private static String WIKI_API_BASE_URL = "https://" + WIKI_API_BASE_URL_LANGUAGE_TOKEN +
            ".wikipedia.org/w/api.php?action=query&prop=extracts&explaintext" +
            "&format=" + WIKI_API_BASE_URL_FORMAT_TOKEN +
            "&titles=" + WIKI_API_BASE_URL_TITLE_TOKEN;


    private static final String WIKI_ARTICLE_BASE_URL = "https://" + WIKI_API_BASE_URL_LANGUAGE_TOKEN +
            ".wikipedia.org/wiki/" + WIKI_API_BASE_URL_TITLE_TOKEN;

    private static final String API_RESPONSE_HEADING_REGEX_PATTERN = "={2,6} [.\\w\\s]+ ={2,6}";
    private static final String API_RESPONSE_ARTICLE_CONTENT_XPATH = "/api/query/pages/page/extract/text()";

    //TODO think of alternative content to return (such as categories of article etc)

    private static WikiHttpApiLoader singleton = null;

    private WikiHttpApiLoader() {
    }

    public static WikiHttpApiLoader getInstance() {
        if (singleton == null)
            singleton = new WikiHttpApiLoader();
        return singleton;
    }

    /**
     * Generates the API call in form of a String containing the URL
     *
     * @param l     the language of the Wikipedia article
     * @param title the title of the Wikipedia article
     * @return a String representing the Wikipedia API URL of the specified query
     */
    private String generateApiCallForQuery(Language l, String title) {
        return WIKI_API_BASE_URL
                .replace(WIKI_API_BASE_URL_LANGUAGE_TOKEN, l.toString())
                .replace(WIKI_API_BASE_URL_FORMAT_TOKEN, DEFAULT_FORMAT)
                .replace(WIKI_API_BASE_URL_TITLE_TOKEN, title);
    }

    /**
     * Creates an @{@link WikiArticle} from an API response
     *
     * @param title       the title of the @{@link WikiArticle}
     * @param language    the language of  @{@link WikiArticle}
     * @param apiResponse the XML formatted response from the API call
     * @return the resulting @{@link WikiArticle} or null if the article doesn't contain any content or is not available
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws XPathExpressionException
     * @throws IOException
     */
    // explicit package private scope for testing since using reflection is not really necessary here in my opinion
    WikiArticle createArticleFromApiResponse(String title, Language language, String apiResponse) throws SAXException, ParserConfigurationException, XPathExpressionException, IOException {
        String articleUrl = this.getUrlOfArticleByTitleAndLanguage(title, language);
        WikiArticle article = new WikiArticle(title, language, articleUrl);

        String articleContent = extractArticleContentFromApiResponse(apiResponse);
        if (articleContent == null)
            return null;
        //get contents as list
        List<String> contents = Arrays.asList(articleContent.split(API_RESPONSE_HEADING_REGEX_PATTERN));

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
        return article;
    }

    /**
     * Extracts the textual content from the XML formatted API response via XPath
     *
     * @param apiResponse the API response in XML format
     * @return the textual content from the XML formatted API response via XPath or null of there is no content in the article
     * @throws IOException
     * @throws SAXException
     * @throws ParserConfigurationException
     * @throws XPathExpressionException
     */
    private String extractArticleContentFromApiResponse(String apiResponse) throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
        //parse response as XML
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(new InputSource(new StringReader(apiResponse)));

        //extract content via XPath
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
        XPathExpression expression = xpath.compile(API_RESPONSE_ARTICLE_CONTENT_XPATH);
        NodeList nodes = (NodeList) expression.evaluate(document, XPathConstants.NODESET);
        assert (nodes.getLength() == 1);

        if (nodes.item(0) == null)
            return null;
        return nodes.item(0).getTextContent();
    }

    /**
     * Downloads the article from Wikipedia with title @title in the language @language and returns it as a @{@link WikiArticle}
     *
     * @param title    the title of the article
     * @param language the language of the article
     * @return a @{@link WikiArticle}  or null if the article doesn't contain any content or is not available
     * @throws IOException if there was any error while fetching the article
     */
    @Override
    public WikiArticle loadArticle(String title, Language language) throws MissingResourceException, IOException {
        //execute HTTP GET via Apache Fluent HC
        String apiCall = generateApiCallForQuery(language, title);
        String response = Request.Get(apiCall).execute().returnContent().asString();


        try {
            return new ArticleLoaderAgent(language, title).call();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Downloads the Wikipedia Articles in the articlesToLoad list in parallel
     *
     * @param articlesToLoad List of tuples of title and language of the articles that should be loaded
     * @return a list of articles
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @Override
    public List<WikiArticle> loadArticles(List<Pair<Language, String>> articlesToLoad) throws ExecutionException, InterruptedException {
        // create a managed thread pool with fixed size
        ExecutorService pool = Executors.newFixedThreadPool(MAX_PARALLEL_THREADS);

        Set<Future<WikiArticle>> articleFutures = new HashSet<>();
        // for each article in the list of articles that should be downloaded, create a new ArticleLoaderAgent and submit it to the thread pool
        for (Pair<Language, String> article : articlesToLoad) {
            // thread pool will create a future that'll be added to the futures set
            Future<WikiArticle> articleFuture = pool.submit(new ArticleLoaderAgent(article.getLeft(), article.getRight()));
            articleFutures.add(articleFuture);
        }

        // get the results from the futures
        List<WikiArticle> loadedArticles = Collections.synchronizedList(new ArrayList<>());
        for (Future<WikiArticle> future : articleFutures) {
            WikiArticle article = future.get();
            if (article == null)
                continue;
            loadedArticles.add(article);
        }

        return loadedArticles;
    }

    public String getUrlOfArticleByTitleAndLanguage(String title, Language lang) {
        return WIKI_ARTICLE_BASE_URL.replaceAll(WIKI_API_BASE_URL_TITLE_TOKEN, title).replaceAll(WIKI_API_BASE_URL_LANGUAGE_TOKEN, lang.toString().toLowerCase());
    }
}
