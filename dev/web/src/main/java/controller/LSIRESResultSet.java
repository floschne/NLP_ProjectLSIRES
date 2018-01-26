package controller;

import data.input.Language;
import data.input.WikiArticle;
import data.input.WikiHttpApiLoader;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LSIRESResultSet {

    private class LSIRESResult {

        private String language;
        private String score;
        private String title;
        private String articleUrl;
        private String introText;

        LSIRESResult(Float score, WikiArticle article) {
            this.score = "Score: " + score.toString();
            this.title = "Title: " + article.getTitle();
            this.articleUrl = article.getUrl();
            this.introText = article.getContent().get(0).getRight();
            this.language = article.getLanguage().toString();
        }

        public String getScore() {
            return score;
        }

        public String getTitle() {
            return title;
        }

        public String getArticleUrl() {
            return articleUrl;
        }

        public String getIntroText() {
            return introText;
        }

        public String getLanguage() {
            return language;
        }
    }

    private List<LSIRESResult> results;
    private String language;

    LSIRESResultSet(List<Pair<String, Float>> wikiArticleId, Language lang) throws IOException {
        this.results = new ArrayList<>();
        this.language = lang.toString();

        WikiHttpApiLoader wikiHttpApiLoader = WikiHttpApiLoader.getInstance();

        String articleTitle;
        Language articleLang;
        WikiArticle article;

        for (Pair<String, Float> p : wikiArticleId) {
            articleLang = Language.valueOf(WikiArticle.getLanguageFromId(p.getLeft().trim().toUpperCase()));
            articleTitle = WikiArticle.getTitleFromId(p.getLeft());
            article = wikiHttpApiLoader.loadArticle(articleTitle, articleLang);

            results.add(new LSIRESResult(p.getRight(), article));
        }
    }

    public List<LSIRESResult> getResults() {
        return results;
    }

    public String getLanguage() {
        return language;
    }
}
