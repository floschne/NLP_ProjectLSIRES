package dataInput;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents an article from Wikipedia
 */
public class WikiArticle {

    public enum Language {
        EN("en"),
        DE("de"),
        FR("fr"),
        IT("it"),
        ES("es");

        private final String languageCode;

        Language(final String languageCode) {
            this.languageCode = languageCode;
        }

        @Override
        public String toString() {
            return languageCode;
        }
    }

    public static String WIKI_ARTICLE_INTRO_HEADING = "= Introduction =";

    private String title;
    private Language language;
    //map from heading to content
    private Map<String, String> content;

    public WikiArticle(String t, Language l) {
        if (t == null || t.isEmpty())
            throw new IllegalArgumentException("Title of a Wikipedia Article must not be null or empty!");
        if (l == null)
            throw new IllegalArgumentException("Language of a Wikipedia Article must not be null!");

        this.title = t;
        this.language = l;
        this.content = new HashMap<>();
    }

    public void addContent(String heading, String content) {
        this.content.put(heading, content);
    }
}
