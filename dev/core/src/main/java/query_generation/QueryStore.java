package query_generation;

import data.input.Language;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

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
        //initialize the query lists in the specified languages
        queries = new ArrayList<>();
    }

    // list of queries in all languages as a list of pairs of a language to the list of queries in that specific language
    private List<Pair<Language, List<String>>> queries;

    /**
     * Adds a query of a specific {@link Language} to the repo.
     *
     * @param query the query as a Pair from the language of the query to query string
     */
    public void addQuery(Pair<Language, String> query) {
        boolean addedQuery = false;
        for (Pair<Language, List<String>> p : queries) {
            if (p.getLeft().equals(query.getLeft())) {
                p.getRight().add(query.getRight());
                addedQuery = true;
                break;
            }
        }
        if (!addedQuery) {
            queries.add(Pair.of(query.getLeft(), new ArrayList<>()));
            addQuery(query);
        }
    }

    /**
     * @return Returns list of queries in all languages as a list of pairs of a language to the list of queries in that specific language
     */
    public List<Pair<Language, List<String>>> getQueriesOfAllLanguages() {
        return queries;
    }

    /**
     * Returns the list containing the queries of the specified language
     *
     * @param lang the language of the queries that'll be returned
     * @return the list containing the queries of the specified language or null if there are no queries of the language
     */
    public List<String> getQueryListOfLanguage(Language lang) {
        for (Pair<Language, List<String>> p : queries)
            if (p.getLeft().equals(lang))
                return p.getRight();
        return null;
    }


    /**
     * Resets the store. This deletes all the generated Queries!
     */
    public void reset() {
        queries = new ArrayList<>();
        System.gc();
    }
}
