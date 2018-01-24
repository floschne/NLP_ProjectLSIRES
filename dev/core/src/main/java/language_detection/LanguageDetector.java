package language_detection;

import data.input.Language;
import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;
import knowledgebase.DatabaseAccessException;
import knowledgebase.DatabaseHandler;
import language_detection.type.QueryLanguageAnnotation;
import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasAnnotator_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;

import java.sql.SQLException;
import java.util.Collection;

public class LanguageDetector extends JCasAnnotator_ImplBase {
    public static final String PARAM_DATABASE = "Database";
    public static final String PARAM_USER = "User";
    public static final String PARAM_PASSWORD = "Password";

    @ConfigurationParameter(name = PARAM_DATABASE, description = "The database file location", mandatory = true)
    private String databaseLocation;
    @ConfigurationParameter(name = PARAM_USER, description = "The database user name", mandatory = true)
    private String databaseUser;
    @ConfigurationParameter(name = PARAM_PASSWORD, description = "The database user's password", mandatory = true)
    private String databasePassword;

    @Override
    public void process(JCas aJCas) throws AnalysisEngineProcessException {

        // detect language
        Language detectedLanguage = detectLanguage(JCasUtil.select(aJCas, Token.class));

        // create annotation
        QueryLanguageAnnotation languageAnnotation = new QueryLanguageAnnotation(aJCas);
        languageAnnotation.setBegin(0);
        languageAnnotation.setEnd(aJCas.getDocumentText().length());
        languageAnnotation.setLanguageCode(detectedLanguage.toString());

        languageAnnotation.addToIndexes();

    }

    private Language detectLanguage(Collection<Token> tokens) throws AnalysisEngineProcessException {

        try (DatabaseHandler handler = new DatabaseHandler(databaseLocation, databaseUser, databasePassword)) {
            Language[] languages = Language.values();
            double[] sumProbability = new double[languages.length];
            for (Token t : tokens)
                for (int i = 0; i < languages.length; i++)
                    sumProbability[i] += handler.languageLikelihoodGivenToken(t.getCoveredText(), languages[i]);

            return maxScoreLanguage(languages, sumProbability);

        } catch (ClassNotFoundException | SQLException | DatabaseAccessException exc) {
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
