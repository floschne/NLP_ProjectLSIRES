package data.input;

import data.util.PopularWikiArticleTitlesBuilder;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.uima.UimaContext;
import org.apache.uima.cas.CAS;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.component.JCasCollectionReader_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Level;
import org.apache.uima.util.Logger;
import org.apache.uima.util.Progress;
import org.apache.uima.util.ProgressImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * Reads the most popular Wikipedia Articles and adds them one by one to the JCas
 */
public class PopularWikiArticlesReader extends JCasCollectionReader_ImplBase {
    public static final String PARAM_ARTICLE_TITLE_FILES = "ListOfArticlesFile";
    @ConfigurationParameter(name = PARAM_ARTICLE_TITLE_FILES, description = "A comma-separated list of files containing a list of titles of Wikipedia Articles that get processed in this reader", mandatory = false, defaultValue = "utf-8")
    private String listOfArticleTitleFiles;


    public static final String PARAM_NUMBER_OF_POPULAR_ARTICLES = "NumberOfPopularArticles";
    @ConfigurationParameter(name = PARAM_NUMBER_OF_POPULAR_ARTICLES, description = "The number of popular Wikipedia Articles that should be processed by the PopularWikiArticlesReader", mandatory = false)
    private Integer numberOfPopularArticles;

    private static final Boolean useFilesOfArticles = false;

    private Logger logger = null;

    //the wikiArticleLoader that'll be used to provide the articles
    private IWikiArticleLoader wikiArticleLoader;
    //will contain the articles that are already loaded
    private List<WikiArticle> wikiArticles;
    //List of tuples of title and language of the articles that should be added to the JCAS (aka processed by the reader)
    private List<Pair<Language, String>> wikiArticlesToProcess;

    private Integer currentArticleIdx;

    /**
     * This method should be overwritten by subclasses.
     *
     * @param context the UIMA context the component is running in
     * @throws ResourceInitializationException if a failure occurs during initialization.
     */
    @Override
    public void initialize(UimaContext context) throws ResourceInitializationException {
        super.initialize(context);
        logger = context.getLogger();
        this.wikiArticleLoader = WikiHttpApiLoader.getInstance();
        wikiArticles = new ArrayList<>();
        currentArticleIdx = 0;

        try {

            if (useFilesOfArticles) {
                // deserialize the Wikipedia Article Files and initialize wikiArticlesToProcess
                String[] titleFiles = listOfArticleTitleFiles.split(",");
                logger.log(Level.INFO, "Deserializing Wikipedia Article Titles from files: '" + listOfArticleTitleFiles.replace(",", " ") + "' !");
                wikiArticlesToProcess = PopularWikiArticleTitlesBuilder.getInstance().deserializeListOfMostPopularWikiArticlesFromCsvFile(titleFiles);
            } else {
                //TODO ugly code.. could reduce redundancy but quick n dirty first!
                if (numberOfPopularArticles == null || numberOfPopularArticles > 1000)
                    numberOfPopularArticles = 1000;
                // get popular article titles of german, english and spanish wikipedia articles
                List<String> popularArticleTitlesDe = PopularWikiArticleTitlesBuilder.getInstance().getListOfMostPopularWikiArticles(Language.DE, numberOfPopularArticles, PopularWikiArticleTitlesBuilder.ListOrdering.ASC);
                List<String> popularArticleTitlesEn = PopularWikiArticleTitlesBuilder.getInstance().getListOfMostPopularWikiArticles(Language.EN, numberOfPopularArticles, PopularWikiArticleTitlesBuilder.ListOrdering.ASC);
                List<String> popularArticleTitlesEs = PopularWikiArticleTitlesBuilder.getInstance().getListOfMostPopularWikiArticles(Language.ES, numberOfPopularArticles, PopularWikiArticleTitlesBuilder.ListOrdering.ASC);
                wikiArticlesToProcess = new ArrayList<>();
                for (String title : popularArticleTitlesDe)
                    wikiArticlesToProcess.add(Pair.of(Language.DE, title));
                for (String title : popularArticleTitlesEn)
                    wikiArticlesToProcess.add(Pair.of(Language.EN, title));
                for (String title : popularArticleTitlesEs)
                    wikiArticlesToProcess.add(Pair.of(Language.ES, title));
            }


            // download the articles and save them in the wikiArticles list
            logger.log(Level.INFO, "Starting to download " + wikiArticlesToProcess.size() + " Wikipedia Articles!");
            wikiArticles = wikiArticleLoader.loadArticles(wikiArticlesToProcess);
            logger.log(Level.INFO, "Finished downloading the Wikipedia Articles!");
        } catch (IOException | ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Subclasses should implement this method rather than {@link #getNext(CAS)}
     *
     * @param jCas the {@link JCas} to store the read data to
     * @throws IOException         if there was a low-level I/O problem
     * @throws CollectionException if there was another problem
     */
    @Override
    public void getNext(JCas jCas) throws IOException, CollectionException {
        WikiArticle currentArticle = wikiArticles.get(currentArticleIdx++);
        jCas.setDocumentLanguage(currentArticle.getLanguage().toString());
        jCas.setDocumentText(currentArticle.getContentAsString());
    }

    /**
     * Gets whether there are any elements remaining to be read from this
     * <code>CollectionReader</code>.
     *
     * @return true if and only if there are more elements available from this
     * <code>CollectionReader</code>.
     * @throws IOException         if an I/O failure occurs
     * @throws CollectionException if there is some other problem with reading from the Collection
     */
    @Override
    public boolean hasNext() throws IOException, CollectionException {
        return currentArticleIdx < wikiArticles.size();
    }

    /**
     * Gets information about the number of entities and/or amount of data that has been read from
     * this <code>CollectionReader</code>, and the total amount that remains (if that information
     * is available).
     * <p>
     * This method returns an array of <code>Progress</code> objects so that results can be reported
     * using different units. For example, the CollectionReader could report progress in terms of the
     * number of documents that have been read and also in terms of the number of bytes that have been
     * read. In many cases, it will be sufficient to return just one <code>Progress</code> object.
     *
     * @return an array of <code>Progress</code> objects. Each object may have different units (for
     * example number of entities or bytes).
     */
    @Override
    public Progress[] getProgress() {
        return new Progress[]{new ProgressImpl(currentArticleIdx, wikiArticles.size(), Progress.ENTITIES)};
    }
}
