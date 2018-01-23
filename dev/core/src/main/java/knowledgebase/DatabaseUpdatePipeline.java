package knowledgebase;

import data.input.WikiArticleReader;
import util.uima.AnnotationWriter;

import de.tudarmstadt.ukp.dkpro.core.tokit.RegexTokenizer;
import org.apache.uima.UIMAException;
import org.apache.uima.analysis_engine.AnalysisEngine;
import org.apache.uima.collection.CollectionReader;
import org.apache.uima.fit.factory.AnalysisEngineFactory;
import org.apache.uima.fit.factory.CollectionReaderFactory;
import org.apache.uima.fit.factory.JCasFactory;
import org.apache.uima.fit.pipeline.SimplePipeline;
import org.apache.uima.jcas.JCas;

import java.io.IOException;

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
		runUpdatePipeline(DEFAULT_LOCATION, DEFAULT_USER, DEFAULT_PASSWORD, 10); // XXX Only loads <= 100 for debugging and testing
	}
	
	/**
	 * Runs the pipeline for database input with {@code numberOfArticles} Wikipedia articles.
	 * @param numberOfArticles the number of Wikipedia articles to process
	 * @throws UIMAException if there is a problem initializing or running the pipeline
	 * @throws IOException if there is an I/O problem while reading from the web
	 */
	// Intentionally package private
	static void runUpdatePipeline(String databaseLocation, String databaseUser, String databasePassword, int numberOfArticles) throws UIMAException, IOException {
//		JCas jcas = JCasFactory.createJCas();
//		jcas.setDocumentLanguage("en");
//		jcas.setDocumentText("foo bar qoox");
		CollectionReader wikiReader = CollectionReaderFactory.createReader(WikiArticleReader.class, WikiArticleReader.PARAM_NUMBER_OF_POPULAR_ARTICLES, numberOfArticles);
		AnalysisEngine tokenizer = AnalysisEngineFactory.createEngine(RegexTokenizer.class, RegexTokenizer.PARAM_WRITE_SENTENCE, false, RegexTokenizer.PARAM_TOKEN_BOUNDARY_REGEX, TOKEN_BOUNDARY_REGEX);
		AnalysisEngine databaseUpdater = AnalysisEngineFactory.createEngine(DatabaseUpdater.class, PARAM_DATABASE, databaseLocation, PARAM_USER, databaseUser, PARAM_PASSWORD, databasePassword);
//		AnalysisEngine writer = AnalysisEngineFactory.createEngine(AnnotationWriter.class);
		SimplePipeline.runPipeline(
//				jcas,
				wikiReader,
				tokenizer,
				databaseUpdater/*,
				writer*/
		);
	}
}
