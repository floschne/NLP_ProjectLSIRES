package controller;

import client.ElasticsearchClient;
import data.input.Language;
import de.tudarmstadt.ukp.dkpro.core.tokit.RegexTokenizer;
import knowledgebase.DatabaseUpdatePipeline;
import language_detection.DetectedLanguageGetterConsumer;
import language_detection.LanguageDetector;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import static knowledgebase.DatabaseHandler.DEFAULT_LOCATION;
import static knowledgebase.DatabaseHandler.DEFAULT_PASSWORD;
import static knowledgebase.DatabaseHandler.DEFAULT_USER;
import static language_detection.LanguageDetector.*;

@Controller
public class AppController {

    @GetMapping(RouteConfiguration.SEARCH_MAPPING_ROUTE)
    public String searchFrom(Model model) {
        model.addAttribute("query", new LSIRESQuery());
        return "search";
    }

    @PostMapping(RouteConfiguration.RESULTS_MAPPING_ROUTE)
    public String searchSubmit(@ModelAttribute LSIRESQuery query, Model model) throws UIMAException, DetectedLanguageGetterConsumer.LanguageNotYetDetectedException, IOException {

        Language detectedLanguage = detectLanguage(query.getValue());

        List<Pair<String, Float>> ret = new ElasticsearchClient().findArticleTitlesByLanguageCodeAndQuery(detectedLanguage.toString().toLowerCase(), query.getValue());

        model.addAttribute("resultSet", new LSIRESResultSet(ret, detectedLanguage));
        return "results";
    }

    private Language detectLanguage(String query) throws UIMAException, DetectedLanguageGetterConsumer.LanguageNotYetDetectedException {

        JCas jcas = JCasFactory.createJCas();
        jcas.setDocumentText(query);

        AnalysisEngine tokenizer = AnalysisEngineFactory.createEngine(RegexTokenizer.class,
                RegexTokenizer.PARAM_WRITE_SENTENCE, false,
                RegexTokenizer.PARAM_TOKEN_BOUNDARY_REGEX, DatabaseUpdatePipeline.TOKEN_BOUNDARY_REGEX);


        AnalysisEngine languageDetector = AnalysisEngineFactory.createEngine(LanguageDetector.class,
                PARAM_DATABASE, DEFAULT_LOCATION,
                PARAM_USER, DEFAULT_USER,
                PARAM_PASSWORD, DEFAULT_PASSWORD,
                PARAM_USE_DUMMY_LANGUAGE_DETECTOR, false);

        AnalysisEngine detectLanguageGetter = AnalysisEngineFactory.createEngine(DetectedLanguageGetterConsumer.class);

        SimplePipeline.runPipeline(jcas, tokenizer, languageDetector, detectLanguageGetter);

        return DetectedLanguageGetterConsumer.getDetectedLanguage();
    }
}
