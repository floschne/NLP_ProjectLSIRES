package query_generation;

import data.input.Language;

/**
 * Represents a query. Basically only a wrapper class
 */
public class Query {
    private Language language;
    private String queryText;

    public Query(Language language, String queryText) {

        if (queryText == null || queryText.isEmpty()) {
            throw new IllegalArgumentException("Query Text must not be null or empty!");
        }

        if (language == null) {
            throw new IllegalArgumentException("Language must not be null!");
        }

        this.language = language;
        this.queryText = queryText;
    }

    public Language getLanguage() {
        return language;
    }

    public String getQueryText() {
        return queryText;
    }

    @Override
    public String toString() {
        String res = queryText;
        res = res.length() <= 24 ? res : res.substring(0, 21) + "...";
        return res + " | " + language.toString();
    }
}
