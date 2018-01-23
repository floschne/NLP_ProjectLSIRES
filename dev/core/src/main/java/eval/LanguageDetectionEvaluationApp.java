package eval;

import data.input.Language;
import edu.stanford.nlp.util.ConfusionMatrix;
import eval.util.DummyLanguageDetector;
import org.apache.commons.codec.language.bm.Lang;
import org.apache.uima.UIMAException;
import query_generation.QueryGenerator;

import java.io.IOException;
import java.util.List;

public class LanguageDetectionEvaluationApp {

    public static void runEvaluationForWikiQueries() throws IOException, UIMAException {
        ConfusionMatrix<Language> confusionMatrix = new ConfusionMatrix<>();

        Boolean news = false;
        Boolean wiki = true;
        int numQueries = 100;
        String language = "DE, EN, ES";
        QueryGenerator queryGenerator = new QueryGenerator(news, wiki, numQueries, language);
        queryGenerator.generateQueries();

        List<String> deQueries = queryGenerator.getQueryListOfLanguage(Language.DE);
        List<String> enQueries = queryGenerator.getQueryListOfLanguage(Language.EN);
        List<String> esQueries = queryGenerator.getQueryListOfLanguage(Language.ES);

        DummyLanguageDetector languageDetector = new DummyLanguageDetector();

        for(String deQuery : deQueries) {
            Language detectedLang = languageDetector.detectLanguageOfQuery(deQuery);
            confusionMatrix.add(detectedLang, Language.DE);
        }

        for(String enQuery : enQueries) {
            Language detectedLang = languageDetector.detectLanguageOfQuery(enQuery);
            confusionMatrix.add(detectedLang, Language.EN);
        }

        for(String esQuery : esQueries) {
            Language detectedLang = languageDetector.detectLanguageOfQuery(esQuery);
            confusionMatrix.add(detectedLang, Language.ES);
        }

        System.out.println(confusionMatrix.printTable());
    }

    public static void main(String[] args) throws IOException, UIMAException {
        // generate queries for DE, ES, EN
        // for each lang
        //  for each query
        //      detect lang for query
        //      count truePos, trueNeg, falsePos, falseNeg for being lang or not (binary)
        //  -> build confusion matrix
        //
        // calc precision & recall for each lang
        //

        runEvaluationForWikiQueries();

    }

}
