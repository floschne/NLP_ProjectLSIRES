package eval;

import de.tudarmstadt.ukp.dkpro.core.tokit.RegexTokenizer;
import knowledgebase.DatabaseUpdatePipeline;
import language_detection.LanguageDetector;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import query_generation.QueryGenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static knowledgebase.DatabaseHandler.*;
import static language_detection.LanguageDetector.*;

public class LanguageDetectionEvaluationPipeline {

    public static void runPipeline() throws UIMAException, IOException {
        System.out.println("Generate queries from Wikipedia Articles Datasource (n, y)?");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        Boolean wiki = br.readLine().equals("y");

        System.out.println("Generate queries from News Articles Datasource (n, y)?");
        br = new BufferedReader(new InputStreamReader(System.in));
        Boolean news = br.readLine().equals("y");

        System.out.println("Use Dummy Language Detector to detect a language randomly instead of actual Language Detection Algorithm? (n, y)");
        br = new BufferedReader(new InputStreamReader(System.in));
        Boolean dummy = br.readLine().equals("y");

        System.out.println("Number of Queries per Language [1, 60000]?");
        br = new BufferedReader(new InputStreamReader(System.in));
        Integer numQueriesPerLanguage = Integer.valueOf(br.readLine());
        if (numQueriesPerLanguage > 60000)
            numQueriesPerLanguage = 60000;
        if (numQueriesPerLanguage < 1)
            numQueriesPerLanguage = 1;

        CollectionReader queryGenerator = CollectionReaderFactory.createReader(QueryGenerator.class,
                QueryGenerator.PARAM_NUMBER_OF_QUERIES, numQueriesPerLanguage,
                QueryGenerator.PARAM_QUERY_LANGUAGES, "DE,EN,ES",
                QueryGenerator.PARAM_GEN_NEWS_QUERIES, news,
                QueryGenerator.PARAM_GEN_WIKI_QUERIES, wiki);

        AnalysisEngine tokenizer = AnalysisEngineFactory.createEngine(RegexTokenizer.class,
                RegexTokenizer.PARAM_WRITE_SENTENCE, false,
                RegexTokenizer.PARAM_TOKEN_BOUNDARY_REGEX, DatabaseUpdatePipeline.TOKEN_BOUNDARY_REGEX);

        AnalysisEngine languageDetector = AnalysisEngineFactory.createEngine(LanguageDetector.class,
                PARAM_DATABASE, DEFAULT_LOCATION,
                PARAM_USER, DEFAULT_USER,
                PARAM_PASSWORD, DEFAULT_PASSWORD,
                PARAM_USE_DUMMY_LANGUAGE_DETECTOR, dummy);

        AnalysisEngine evaluationWriter = AnalysisEngineFactory.createEngine(LanguageDetectionEvaluator.class);

        SimplePipeline.runPipeline(queryGenerator, tokenizer, languageDetector, evaluationWriter);

        evaluationWriter.destroy();
    }

    public static void main(String[] args) throws IOException, UIMAException {
        runPipeline();
    }

}
