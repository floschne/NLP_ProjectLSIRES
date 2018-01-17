package knowledgebase;

import data.input.Language;

import java.sql.SQLException;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasConsumer_ImplBase;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

import static knowledgebase.DatabaseHandler.DEFAULT_LOCATION;
import static knowledgebase.DatabaseHandler.DATABASE_USER;
import static knowledgebase.DatabaseHandler.DATABASE_PASSWORD;

public class LanguageDetector extends JCasConsumer_ImplBase {
	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		// TODO Use ConfigurationParameter
		try (DatabaseHandler handler = new DatabaseHandler(DEFAULT_LOCATION, DATABASE_USER, DATABASE_PASSWORD)) {
			Language[] languages = Language.values();
			double[] sumProbability = new double[languages.length];
			// TODO Use proper annotation type
			for (Token t : JCasUtil.select(aJCas, Token.class))
				for (int i = 0; i < languages.length; i++)
					sumProbability[i] += handler.languageLikelihoodGivenToken(t.getCoveredText(), languages[i]);
			Language maxScoreLanguage = maxScoreLanguage(languages, sumProbability); // TODO Store in pipeline class
		}
		catch (ClassNotFoundException | SQLException | DatabaseAccessException exc) {
			throw new AnalysisEngineProcessException(exc.getMessage(), null, exc.getCause());
		}
	}
	
	private Language maxScoreLanguage(Language[] languages, double[] score) {
		Language maxScoreLanguage = null;
		double maxScore = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < languages.length && i < score.length; i++) {
			if (maxScore <= score[i]) {
				maxScore = score[i];
				maxScoreLanguage = languages[i];
			}
		}
		return maxScoreLanguage;
	}
}
