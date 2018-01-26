package data.input;

import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

/**
 * Represents an article from Wikipedia
 */
public class WikiArticle {

	public static final String WIKI_ARTICLE_INTRO_HEADING = "";

    private String title;
    private Language language;
    private String url;
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

    public WikiArticle(String t, Language l, String url) {
        this(t, l);
        if (url == null || url.isEmpty())
            throw new IllegalArgumentException("URL of a Wikipedia Article must not be null or empty!");
        this.url = url;
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

    public String getArticleId() {
        return language.toString() + ";" + title;
    }

    public String getUrl() {
        return url;
    }

    public static String getTitleFromId(String id) {
        if (id == null || id.isEmpty() || !id.contains(";")) {
            throw new IllegalArgumentException("Given String doesn't seem to be an ID of an WikiArticle!");
        }
        return id.split(";")[1];
    }

    public static String getLanguageFromId(String id) {
        if (id == null || id.isEmpty() || !id.contains(";")) {
            throw new IllegalArgumentException("Given String doesn't seem to be an ID of an WikiArticle!");
        }
        return id.split(";")[0];
    }

}
