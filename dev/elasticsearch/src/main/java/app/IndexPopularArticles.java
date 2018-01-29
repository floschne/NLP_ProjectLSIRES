package app;

import client.ElasticsearchClient;
import data.input.*;
import data.input.IWikiArticleLoader;
import data.input.WikiHttpApiLoader;
import data.util.PopularWikiArticleTitlesBuilder;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class IndexPopularArticles {

    public static void main(String[] args) {

        // basic config
        Integer numberOfPopularArticles = 1000;
        List<Pair<Language, String>> wikiArticlesToProcess;
        List<WikiArticle> wikiArticles = new ArrayList<>();
        IWikiArticleLoader IWikiArticleLoader = WikiHttpApiLoader.getInstance();

        // profiling
        System.out.println("Starting to index " + numberOfPopularArticles*3 + " wikipedia articles");
        long startTime = System.currentTimeMillis();

        try {

            List<String> popularArticleTitlesDe = PopularWikiArticleTitlesBuilder.getInstance().getListOfMostPopularWikiArticles(Language.DE, numberOfPopularArticles, PopularWikiArticleTitlesBuilder.ListOrdering.DESC);
            List<String> popularArticleTitlesEn = PopularWikiArticleTitlesBuilder.getInstance().getListOfMostPopularWikiArticles(Language.EN, numberOfPopularArticles, PopularWikiArticleTitlesBuilder.ListOrdering.DESC);
            List<String> popularArticleTitlesEs = PopularWikiArticleTitlesBuilder.getInstance().getListOfMostPopularWikiArticles(Language.ES, numberOfPopularArticles, PopularWikiArticleTitlesBuilder.ListOrdering.DESC);

            wikiArticlesToProcess = new ArrayList<>();
            for (String title : popularArticleTitlesDe)
                wikiArticlesToProcess.add(Pair.of(Language.DE, title));
            for (String title : popularArticleTitlesEn)
                wikiArticlesToProcess.add(Pair.of(Language.EN, title));
            for (String title : popularArticleTitlesEs)
                wikiArticlesToProcess.add(Pair.of(Language.ES, title));

            // profiling
            long endTime   = System.currentTimeMillis();
            long totalTime = endTime - startTime;
            System.out.println("\t -> titles loaded in: " + totalTime/1000 + "s \t (" + (numberOfPopularArticles*3)/(totalTime/1000) + " articles/s)");
            startTime = System.currentTimeMillis();


            wikiArticles = IWikiArticleLoader.loadArticles(wikiArticlesToProcess);

            // profiling
            endTime   = System.currentTimeMillis();
            totalTime = endTime - startTime;
            System.out.println("\t -> articles loaded in: " + totalTime/1000 + "s \t (" + (numberOfPopularArticles*3)/(totalTime/1000) + " articles/s)");
            startTime = System.currentTimeMillis();

            ElasticsearchClient esClient = new ElasticsearchClient();

            esClient.indexArticles(wikiArticles);

            // profiling
            endTime   = System.currentTimeMillis();
            totalTime = endTime - startTime;
            System.out.println("\t -> articles indexes in: " + totalTime/1000 + "s \t (" + (numberOfPopularArticles*3)/(totalTime/1000) + " articles/s)");


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
