package data.util;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import data.input.WikiArticle;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.http.client.fluent.Request;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.InputMismatchException;
import java.util.List;

/**
 * Utility class to get the list of the most popular Wikipedia Articles (based on pageviews)
 * See https://wikimedia.org/api/rest_v1/#!/Pageviews_data/get_metrics_pageviews_top_project_access_year_month_day for more detailed info
 */
public class PopularWikiArticlesListBuilder {

    private static final String API_URL = "https://wikimedia.org/api/rest_v1/metrics/pageviews/top/<lang>.wikipedia.org/all-access/<year>/<month>/<day>";
    private static final String API_URL_LANG_TOKEN = "<lang>";
    private static final String API_URL_YEAR_TOKEN = "<year>";
    private static final String API_URL_MONTH_TOKEN = "<month>";
    private static final String API_URL_DAY_TOKEN = "<day>";
    private static final String API_RESPONSE_ARTICLE_CATEGORY_INDICATOR_TOKEN = ":";
    //TODO add translations for other languages
    // if an article title contains one of the words, it'll get ignored and not added to the list
    private static final String IGNORED_ARTICLE_CATEGORIES = "special,spezial,especial,wikipedia,hauptseite,portada,main_page,anexo,anhang,file,datei,user,benutzer,usuario,";

    private static final Integer DEFAULT_YEAR = 2017;
    private static final Integer DEFAULT_MONTH = 12;
    private static final Integer DEFAULT_DAY = 10;

    /**
     * generates the API Call URL based on the parameters
     *
     * @param lang  the Language of the Wikipedia Articles
     * @param year  the year
     * @param month the month
     * @param day   the day from which the ranking for the most popular pages gets loaded
     * @return the API Call URL as a String
     */
    private static String generateApiCallFromParameters(WikiArticle.Language lang, Integer year, Integer month, Integer day) {
        if (lang == null)
            throw new IllegalArgumentException("Language must not be null!");
        if (year == null)
            year = DEFAULT_YEAR;
        if (month == null)
            month = DEFAULT_MONTH;
        if (day == null)
            day = DEFAULT_DAY;

        String url = API_URL;

        url = url.replace(API_URL_LANG_TOKEN, lang.toString().toLowerCase());
        url = url.replace(API_URL_YEAR_TOKEN, year.toString());
        String dayStr = day.toString();
        String monthStr = month.toString();
        if (month < 10)
            monthStr = "0" + monthStr;
        if (day < 10)
            dayStr = "0" + dayStr;

        url = url.replace(API_URL_MONTH_TOKEN, monthStr);
        url = url.replace(API_URL_DAY_TOKEN, dayStr);

        return url;
    }

    /**
     * Filters the JSON encoded response for the articles
     *
     * @param response the JSON encoded response from the API Call
     * @param limit    the limit of articles that should be loaded
     * @return the titles of the articles as a list of Strings
     */
    private static List<String> filterResponseForArticles(String response, Integer limit) {
        List<String> popularArticles = new ArrayList<>();

        JsonElement jElement = new JsonParser().parse(response);
        JsonObject jObject = jElement.getAsJsonObject();
        JsonArray articles = jObject.getAsJsonArray("items").get(0).getAsJsonObject().getAsJsonArray("articles");

        List<String> ignoredArticleTitles = Arrays.asList(IGNORED_ARTICLE_CATEGORIES.split(","));
        Boolean ignoreArticle = false;
        for (JsonElement a : articles) {
            String articleTitle = a.getAsJsonObject().get("article").toString().replace("\"", "");
            for (String ign : ignoredArticleTitles)
                if (articleTitle.toLowerCase().contains(ign + API_RESPONSE_ARTICLE_CATEGORY_INDICATOR_TOKEN)) {
                    ignoreArticle = true;
                    break;
                }
            if (ignoreArticle) {
                ignoreArticle = false;
                continue;
            }
            if (popularArticles.size() < limit)
                popularArticles.add(articleTitle);
            else
                return popularArticles;
        }

        return popularArticles;
    }

    /**
     * Returns the list of titles of the most popular Wikipedia Articles
     *
     * @param lang  the Language of the Wikipedia Articles
     * @param year  the year
     * @param month the month
     * @param day   the day from which the ranking for the most popular pages gets loaded
     * @param limit the limit of articles that should be returned
     * @return the titles of the most popular Wikipedia Articles as a list of Strings
     * @throws IOException
     */
    public static List<String> getListOfMostPopularWikiArticles(WikiArticle.Language lang, Integer year, Integer month, Integer day, Integer limit) throws IOException {
        String apiCall = generateApiCallFromParameters(lang, year, month, day);
        String response = Request.Get(apiCall).execute().returnContent().asString();

        return filterResponseForArticles(response, limit);
    }

    /**
     * Returns the list of titles of the most popular Wikipedia Articles
     *
     * @param lang  the Language of the Wikipedia Articles
     * @param year  the year
     * @param month the month
     * @param day   the day from which the ranking for the most popular pages gets loaded
     * @return the titles of the most popular Wikipedia Articles as a list of Strings
     * @throws IOException
     */
    public static List<String> getListOfMostPopularWikiArticles(WikiArticle.Language lang, Integer year, Integer month, Integer day) throws IOException {
        return getListOfMostPopularWikiArticles(lang, year, month, day, 1000);
    }


    /**
     * Returns the list of titles of the most popular Wikipedia Articles
     *
     * @param lang the Language of the Wikipedia Articles
     * @throws IOException
     */
    public static List<String> getListOfMostPopularWikiArticles(WikiArticle.Language lang) throws IOException {
        return getListOfMostPopularWikiArticles(lang, null, null, null, 1000);
    }



    /**
     * Returns the list of titles of the most popular Wikipedia Articles
     *
     * @param lang the Language of the Wikipedia Articles
     * @param limit the limit of articles that should be returned
     * @throws IOException
     */
    public static List<String> getListOfMostPopularWikiArticles(WikiArticle.Language lang, Integer limit) throws IOException {
        return getListOfMostPopularWikiArticles(lang, null, null, null, limit);
    }

    /**
     * serializes a list of most popular Wikipedia Articles to a file
     *
     * @param fileName         the name of the file
     * @param lang             the language of the Wikipedia Articles in the list
     * @param articles         the list of popular Wikipedia Articles
     * @param writeToResources TODO flag to write file to resource folder
     */
    public static void serializeListOfMostPopularWikiArticlesToCsvFile(String fileName, WikiArticle.Language lang, List<String> articles, Boolean writeToResources) {
        try {
            Writer writer = new FileWriter(new File(fileName));
            /* TODO make it possible to create file in resource folder without hard coding path..
            if(writeToResources) {
                writer = new PrintWriter(new File(PopularWikiArticlesListBuilder.class.getResource(fileName).getPath()));
            }
            else {
                writer = new FileWriter(new File(fileName));
            }
            */

            StringBuilder sb = new StringBuilder();
            for (String article : articles) {
                sb.append(lang.toString()).append(',').append(article).append('\n');
                writer.write(sb.toString());
                sb.setLength(0);
            }

            writer.flush();
            writer.close();

            System.out.println("Generated file for language '" + lang.toString() + ": " + fileName);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Deserializes a list of files containing a list of the most popular Wikipedia Articles in a specific language.
     * Files  have to be in CSV format: "<lang>,<title>"
     *
     * @param fileNames the file names
     * @return a list of pairs "<lang>,<title>" containing the language and title of the popular article
     * @throws IOException
     */
    public static List<Pair<WikiArticle.Language, String>> deserializeListOfMostPopularWikiArticlesFromCsvFile(String... fileNames) throws IOException {
        List<Pair<WikiArticle.Language, String>> popularArticles = new ArrayList<>();
        BufferedReader reader;
        String line;
        String[] entry;
        for (String file : fileNames) {
            reader = new BufferedReader(new FileReader(new File(file)));
            while ((line = reader.readLine()) != null) {
                entry = line.split(",");
                if (entry.length != 2)
                    throw new InputMismatchException("Each entry i.e. line in the file has to be in the format <lang>,<title>");
                popularArticles.add(Pair.of(WikiArticle.Language.valueOf(entry[0]), entry[1]));
            }
        }

        return popularArticles;
    }

    public static void main(String[] args) {
        System.out.println("Generating CSV files containing list of the most popular Wikipedia Articles!");

        //ONLY WORKS FOR UNIX SYSTEMS DUE TO PATH IN FILE NAMES!
        String deFileName = "/tmp/popularWikiArticles_de.csv";
        String enFileName = "/tmp/popularWikiArticles_en.csv";
        String esFileName = "/tmp/popularWikiArticles_es.csv";

        try {
            List<String> de = getListOfMostPopularWikiArticles(WikiArticle.Language.DE);
            List<String> en = getListOfMostPopularWikiArticles(WikiArticle.Language.EN);
            List<String> es = getListOfMostPopularWikiArticles(WikiArticle.Language.ES);

            serializeListOfMostPopularWikiArticlesToCsvFile(deFileName, WikiArticle.Language.DE, de, true);
            serializeListOfMostPopularWikiArticlesToCsvFile(enFileName, WikiArticle.Language.EN, en, true);
            serializeListOfMostPopularWikiArticlesToCsvFile(esFileName, WikiArticle.Language.ES, es, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

