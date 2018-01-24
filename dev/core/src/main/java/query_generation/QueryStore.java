package query_generation;

import data.input.Language;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Singleton Query Store for global access to queries that are generated.
 * Note: This is a hack since we (don't know or) cannot parametrize the constructors of UIMA Pipeline components directly..
 */
public class QueryStore {
    private static QueryStore singleton;

    public static QueryStore getInstance() {
        if (singleton == null)
            singleton = new QueryStore();
        return singleton;
    }

    private QueryStore() {
        queries = new HashMap<>();
    }

    // queries in all languages as a map from a language to the list of queries in that specific language
    private Map<Language, List<Query>> queries;

    /**
     * Adds a query of a specific {@link Language} to the repo.
     *
     * @param query the query as a Pair from the language of the query to query string
     */
    public void addQuery(Query query) {
        // add a new array list if there is no list of queries for that language
        queries.computeIfAbsent(query.getLanguage(), k -> new ArrayList<>());
        queries.get(query.getLanguage()).add(query);
    }

    /**
     * Returns the list containing the queries of all languages
     */
    public List<Query> getQueriesOfAllLanguages() {
        List<Query> allLanguages = new ArrayList<>();
        for (List<Query> queryList : queries.values())
            allLanguages.addAll(queryList);
        return allLanguages;
    }

    /**
     * Returns the list containing the queries of the specified language
     *
     * @param lang the language of the queries that'll be returned
     * @return the list containing the queries of the specified language or null if there are no queries of the language
     */
    public List<Query> getQueryListOfLanguage(Language lang) {
        return queries.get(lang);
    }


    /**
     * Resets the store. This deletes all the generated Queries!
     */
    public void reset() {
        queries = new HashMap<>();
        System.gc();
    }
}
