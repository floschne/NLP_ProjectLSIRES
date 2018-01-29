package data.input;

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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LeipzigSentencesCorporaReader extends JCasCollectionReader_ImplBase {

    private static final String GERMAN_WIKI_SENTENCES_CORPORA_NAME = "deu_wikipedia_2016_10K";
    private static final String GERMAN_NEWS_SENTENCES_CORPORA_NAME = "deu_news_2015_10K";

    private static final String SPANISH_WIKI_SENTENCES_CORPORA_NAME = "spa_wikipedia_2016_10K";
    private static final String SPANISH_NEWS_SENTENCES_CORPORA_NAME = "spa_newscrawl_2015_10K";

    private static final String ENGLISH_WIKI_SENTENCES_CORPORA_NAME = "eng_wikipedia_2016_10K";
    private static final String ENGLISH_NEWS_SENTENCES_CORPORA_NAME = "eng_news_2015_10K";

    private static final String SENTENCES_CORPORA_SUFFIX = "-sentences.txt";

    private static final String RELATIVE_PATH_TO_CORPORA_ROOT_DIRECTORY = "/NLP_ProjectLSIRES/data/leipzigCorpora/";

    private Logger logger = null;

    public static final String PARAM_LOAD_NEWS_CORPORA = "LoadNewsCorpora";
    @ConfigurationParameter(name = PARAM_LOAD_NEWS_CORPORA, description = "Boolean flag. If true News corpora should be loaded and false if not.")
    private Boolean loadNewsCorpora;

    public static final String PARAM_LOAD_WIKI_CORPORA = "LoadWikiCorpora";
    @ConfigurationParameter(name = PARAM_LOAD_WIKI_CORPORA, description = "Boolean flag. If true Wikipedia corpora should be loaded and false if not.")
    private Boolean loadWikiCorpora;

    public static final String PARAM_CORPORA_LANGUAGES = "ListOfLanguages";
    @ConfigurationParameter(name = PARAM_CORPORA_LANGUAGES, description = "A comma-separated list of language (codes {EN, ES, DE}) of the corpora that will be read", mandatory = false, defaultValue = "EN,ES,DE")
    private String corporaLanguages;

    private List<String> sentencesCorporaFilesNames;
    private Integer currentSentencesCorporaIdx;

    private String getPathToCorporaRootDirectory() {
        String path = new File("").getAbsolutePath() + RELATIVE_PATH_TO_CORPORA_ROOT_DIRECTORY;
        return path.replaceAll("NLP_ProjectLSIRES(.*)\\/data\\/leipzigCorpora", RELATIVE_PATH_TO_CORPORA_ROOT_DIRECTORY);
    }

    /**
     * This method should be overwritten by subclasses.
     *
     * @param context the UIMA context the component is running in
     * @throws ResourceInitializationException if a failure occurs during initialization.
     */
    @Override
    public void initialize(UimaContext context) throws ResourceInitializationException {
        super.initialize(context);

        if (!loadWikiCorpora && !loadNewsCorpora)
            throw new IllegalArgumentException("Either PARAM_LOAD_NEWS_CORPORA, PARAM_LOAD_WIKI_CORPORA or both must be true!");
        if (!Files.isDirectory(Paths.get(getPathToCorporaRootDirectory())))
            throw new IllegalArgumentException("Path ('" + getPathToCorporaRootDirectory() + "') to the root directory of the corpora doesn't exist!");

        logger = context.getLogger();

        currentSentencesCorporaIdx = 0;

        sentencesCorporaFilesNames = generateCorporaFileNames(loadNewsCorpora, loadWikiCorpora);
        StringBuilder sb = new StringBuilder();
        sb.append("Loaded the following corpora: \n");
        for (String c : sentencesCorporaFilesNames)
            sb.append("\t").append(c).append("\n");
        logger.log(Level.INFO, sb.toString());
    }

    private List<String> generateCorporaFileNames(Boolean loadNewsCorpora, Boolean loadWikiCorpora) {
        List<String> corporaFileNames = new ArrayList<>();
        String corporaFileName = null;
        String rootPath = getPathToCorporaRootDirectory();
        for (String langCode : corporaLanguages.split(",")) {
            switch (langCode.trim().toUpperCase()) {
                case "DE":
                    if (loadWikiCorpora) {
                        corporaFileName = rootPath + GERMAN_WIKI_SENTENCES_CORPORA_NAME + "/" + GERMAN_WIKI_SENTENCES_CORPORA_NAME + SENTENCES_CORPORA_SUFFIX;
                        corporaFileNames.add(corporaFileName);
                    }
                    if (loadNewsCorpora) {
                        corporaFileName = rootPath + GERMAN_NEWS_SENTENCES_CORPORA_NAME + "/" + GERMAN_NEWS_SENTENCES_CORPORA_NAME + SENTENCES_CORPORA_SUFFIX;
                        corporaFileNames.add(corporaFileName);
                    }
                    break;
                case "EN":
                    if (loadWikiCorpora) {
                        corporaFileName = rootPath + ENGLISH_WIKI_SENTENCES_CORPORA_NAME + "/" + ENGLISH_WIKI_SENTENCES_CORPORA_NAME + SENTENCES_CORPORA_SUFFIX;
                        corporaFileNames.add(corporaFileName);
                    }
                    if (loadNewsCorpora) {
                        corporaFileName = rootPath + ENGLISH_NEWS_SENTENCES_CORPORA_NAME + "/" + ENGLISH_NEWS_SENTENCES_CORPORA_NAME + SENTENCES_CORPORA_SUFFIX;
                        corporaFileNames.add(corporaFileName);
                    }
                    break;
                case "ES":
                    if (loadWikiCorpora) {
                        corporaFileName = rootPath + SPANISH_WIKI_SENTENCES_CORPORA_NAME + "/" + SPANISH_WIKI_SENTENCES_CORPORA_NAME + SENTENCES_CORPORA_SUFFIX;
                        corporaFileNames.add(corporaFileName);
                    }
                    if (loadNewsCorpora) {
                        corporaFileName = rootPath + SPANISH_NEWS_SENTENCES_CORPORA_NAME + "/" + SPANISH_NEWS_SENTENCES_CORPORA_NAME + SENTENCES_CORPORA_SUFFIX;
                        corporaFileNames.add(corporaFileName);
                    }
                    break;
                default:
                    throw new IllegalArgumentException("Language Code '" + langCode.trim().toUpperCase() + "' is not supported! Supported: {EN, DE, ES}");
            }
        }
        return corporaFileNames;
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
        /*
            Reads the corpora with name corporaFileName, removes the line number, concatenates every sentence,
             shuffles the sentences and finally sets all the sentences as the jCas document
         */
        String corporaFileName = sentencesCorporaFilesNames.get(currentSentencesCorporaIdx++);

        if (corporaFileName.contains("eng_"))
            jCas.setDocumentLanguage(Language.EN.toString());
        else if (corporaFileName.contains("deu_"))
            jCas.setDocumentLanguage(Language.DE.toString());
        else if (corporaFileName.contains("spa_"))
            jCas.setDocumentLanguage(Language.ES.toString());
        else
            throw new IllegalArgumentException("Cannot process corpora since it's language is not supported or not encoded in the corporaFileName name ('" + corporaFileName + "')");

        BufferedReader reader = new BufferedReader(new FileReader(new File(corporaFileName)));
        List<String> sentences = new ArrayList<>();
        String line;
        StringBuilder sb = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            String[] sentence;
            sentence = line.split("^\\d*\\s*"); //removes the line number and only keeps the sentence
            assert sentence.length == 2; // has to contain only two elements: an empty string (from split) and the sentence itself
            assert sentence[0].equals(""); //
            sentences.add(sentence[1].concat("\n")); // add the newline again
        }
        Collections.shuffle(sentences); // shuffle the sentences
        for (String sentence : sentences)
            sb.append(sentence); // concatenate the shuffled sentences
        jCas.setDocumentText(sb.toString());
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
        return currentSentencesCorporaIdx < sentencesCorporaFilesNames.size();
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
        return new Progress[]{new ProgressImpl(currentSentencesCorporaIdx, sentencesCorporaFilesNames.size(), Progress.ENTITIES)};
    }
}
