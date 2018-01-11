package data.input;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.uima.UimaContext;
import org.apache.uima.collection.CollectionException;
import org.apache.uima.fit.component.JCasCollectionReader_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.jcas.JCas;
import org.apache.uima.resource.ResourceInitializationException;
import org.apache.uima.util.Progress;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Reads @{@link WikiArticle}s and adds it to the JCas
 */
public class WikiArticleReader extends JCasCollectionReader_ImplBase {
    public static final String PARAM_LIST_OF_ARTICLES_FILE = "ListOfArticlesFile";
    @ConfigurationParameter(name = PARAM_LIST_OF_ARTICLES_FILE, description="Sets text file encoding for i/o", mandatory=true, defaultValue="utf-8")

    //the WikiArticleLoader that'll be used to provide the articles
    private WikiArticleLoader wikiArticleLoader;
    //will contain the articles that are already loaded
    private List<WikiArticle> wikiArticles;
    //List of tuples of title and language of the articles that should be added to the JCAS (aka processed by the reader)
    private List<Pair<WikiArticle.Language, String>> wikiArticlesToProcess;

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

        this.wikiArticleLoader = WikiHttpApiLoader.getInstance();


    }

    private List<Pair<WikiArticle.Language, String>> readArticleListFile() {
        List<Pair<WikiArticle.Language, String>> articles = new ArrayList<>();



        return articles;
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
        return false;
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
        return new Progress[0];
    }
}
