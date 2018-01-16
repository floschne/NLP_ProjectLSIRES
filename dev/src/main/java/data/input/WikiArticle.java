package data.input;

import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

/**
 * Represents an article from Wikipedia
 */
public class WikiArticle {

    public enum Language {
        EN("en"),
        DE("de"),
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

    public static final String WIKI_ARTICLE_INTRO_HEADING = "= Introduction =\n";

    private String title;
    private Language language;
    //List of pairs <heading, content> representing the sections of an article
    private List<Pair<String, String>> content;

    public WikiArticle(String t, Language l) {
        if (t == null || t.isEmpty())
            throw new IllegalArgumentException("Title of a Wikipedia Article must not be null or empty!");
        if (l == null)
            throw new IllegalArgumentException("Language of a Wikipedia Article must not be null!");

        this.title = t;
        this.language = l;
        this.content = new ArrayList<>();
    }

    public void addContent(String heading, String content) {
        this.content.add(Pair.of(heading, content));
    }

    public List<String> getHeadings() {
        List<String> headings = new ArrayList<>();
        for (Pair<String, String> section : content)
            headings.add(section.getKey());

        return headings;
    }

    public List<Pair<String, String>> getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public Language getLanguage() {
        return language;
    }

    public String getContentAsString() {
        StringBuilder sb = new StringBuilder();
        for (Pair<String, String> section : content)
            sb.append(section.getKey()).append(section.getValue());

        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("WikiArticle{\ntitle='").append(title).append("',\n");
        sb.append("language=").append(language).append(",\n");
        sb.append("content=\n");

        for (Pair<String, String> section : content)
            sb.append(section.getKey()).append(section.getValue());

        sb.append("\n}");

        return sb.toString();
    }
}
