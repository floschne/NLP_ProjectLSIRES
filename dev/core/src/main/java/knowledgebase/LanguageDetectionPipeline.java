package knowledgebase;

import java.util.LinkedList;
import java.util.Stack;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;

import data.input.Language;
import de.tudarmstadt.ukp.dkpro.core.tokit.RegexTokenizer;

import static knowledgebase.DatabaseHandler.DEFAULT_LOCATION;
import static knowledgebase.DatabaseHandler.DEFAULT_USER;
import static knowledgebase.DatabaseHandler.DEFAULT_PASSWORD;
import static knowledgebase.LanguageDetector.*;

public class LanguageDetectionPipeline {
	private static final Stack<Pair<String, Language>> DETECTED_LANGUAGE = new Stack<>();

	/**
	 * Runs the pipeline detecting a language.
	 * @param args command line arguments (not used)
	 * @throws UIMAException if there is a problem initializing the pipeline or processing the data
	 */
	public static void main(String[] args) throws UIMAException {
		LinkedList<Pair<String, Language>> queryLanguages = runLanguageDetectionPipeline(DEFAULT_LOCATION, DEFAULT_USER, DEFAULT_PASSWORD);
		for (Pair<String, Language> queryLanguage : queryLanguages) {
			// TODO @ Flo: Do something with results...
			String query = queryLanguage.getLeft();
			query = query.length() <= 24 ? query : query.substring(0, 21) + "...";
			System.out.println(queryLanguage.getRight().toString() + " | " + query);
		}
	}
	
	/**
	 * Runs the pipeline for language detection using the database at the specified location.
	 * @param databaseLocation the location of the database
	 * @param databaseUser the user name of the database user
	 * @param databasePassword the password of the database user
	 * @return a list of queries with the respective detected languages
	 * @throws UIMAException if there is a problem initializing or running the pipeline
	 */
	// Intentionally package private
	static LinkedList<Pair<String, Language>> runLanguageDetectionPipeline(String databaseLocation, String databaseUser, String databasePassword) throws UIMAException {
		// TODO @ Flo: Replace by query provider
		JCas jcas = JCasFactory.createJCas();
		jcas.setDocumentText("idiota");
		
		AnalysisEngine tokenizer = AnalysisEngineFactory.createEngine(RegexTokenizer.class, RegexTokenizer.PARAM_WRITE_SENTENCE, false, RegexTokenizer.PARAM_TOKEN_BOUNDARY_REGEX, DatabaseUpdatePipeline.TOKEN_BOUNDARY_REGEX);
		AnalysisEngine languageDetector = AnalysisEngineFactory.createEngine(LanguageDetector.class, PARAM_DATABASE, databaseLocation, PARAM_USER, databaseUser, PARAM_PASSWORD, databasePassword);
		SimplePipeline.runPipeline(jcas, tokenizer, languageDetector);
		LinkedList<Pair<String, Language>> results = new LinkedList<>();
		while (!DETECTED_LANGUAGE.isEmpty())
			results.addFirst(DETECTED_LANGUAGE.pop());
		return results;
	}
	
	/**
	 * Pushes a query-language pair to a stack of queries with detected languages.
	 * @param query the query for which a language was detected
	 * @param language the language that was detected
	 */
	// Intentionally package private
	static void pushDetectedLanguage(String query, Language language) {
		DETECTED_LANGUAGE.push(Pair.of(query, language));
	}
}
