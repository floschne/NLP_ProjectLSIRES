package knowledgebase;

import data.input.PopularWikiArticlesReader;

import de.tudarmstadt.ukp.dkpro.core.tokit.RegexTokenizer;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import static knowledgebase.DatabaseUpdater.*;
import static knowledgebase.DatabaseHandler.DEFAULT_LOCATION;
import static knowledgebase.DatabaseHandler.DEFAULT_USER;
import static knowledgebase.DatabaseHandler.DEFAULT_PASSWORD;

public class DatabaseUpdatePipeline {
	/**
	 * Regular expression describing the boundary text between tokens.
	 */
	public static final String TOKEN_BOUNDARY_REGEX =
			"([.,;:!?\"'“»)\\]]|\\[[0-9]+\\])*"
			+ "([\\s\n]*(--+|==+|~~+|–+)|)"
			+ "[\\s\n]+(-[\\s\n]+|)"
			+ "((--+|==+|~~+|–+)[\\s\n]*|)"
			+ "[¡¿\"'„«(\\[]*";
	
	/**
	 * Runs the pipeline updating the language knowledge base.
	 * @param args the command line arguments (not used)
	 * @throws UIMAException if there is a problem initializing the pipeline or processing the data
	 * @throws IOException if there is an I/O probelm while reading from the web
	 */
	public static void main(String[] args) throws UIMAException, IOException {

        System.out.println("Number of Wikipedia Articles to load per language: [1, 1000]");
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        Integer numArticlesPerLanguage = Integer.valueOf(br.readLine());
        if (numArticlesPerLanguage > 1000)
            numArticlesPerLanguage = 1000;
        if (numArticlesPerLanguage < 1)
            numArticlesPerLanguage = 1;

		runUpdatePipeline(DEFAULT_LOCATION, DEFAULT_USER, DEFAULT_PASSWORD, numArticlesPerLanguage); // XXX Only loads <= 100 for debugging and testing
	}
	
	/**
	 * Runs the pipeline for database input with {@code numberOfArticles} Wikipedia articles.
	 * @param numberOfArticles the number of Wikipedia articles to process
	 * @throws UIMAException if there is a problem initializing or running the pipeline
	 * @throws IOException if there is an I/O problem while reading from the web
	 */
	// Intentionally package private
	static void runUpdatePipeline(String databaseLocation, String databaseUser, String databasePassword, int numberOfArticles) throws UIMAException, IOException {
		CollectionReader wikiReader = CollectionReaderFactory.createReader(PopularWikiArticlesReader.class, PopularWikiArticlesReader.PARAM_NUMBER_OF_POPULAR_ARTICLES, numberOfArticles);
		AnalysisEngine tokenizer = AnalysisEngineFactory.createEngine(RegexTokenizer.class, RegexTokenizer.PARAM_WRITE_SENTENCE, false, RegexTokenizer.PARAM_TOKEN_BOUNDARY_REGEX, TOKEN_BOUNDARY_REGEX);
		AnalysisEngine databaseUpdater = AnalysisEngineFactory.createEngine(DatabaseUpdater.class, PARAM_DATABASE, databaseLocation, PARAM_USER, databaseUser, PARAM_PASSWORD, databasePassword);
		SimplePipeline.runPipeline(
				wikiReader,
				tokenizer,
				databaseUpdater
		);
	}
}
