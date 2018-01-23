package knowledgebase;

import data.input.Language;

import java.sql.SQLException;

import org.apache.uima.analysis_engine.AnalysisEngineProcessException;
import org.apache.uima.fit.component.JCasConsumer_ImplBase;
import org.apache.uima.fit.descriptor.ConfigurationParameter;
import org.apache.uima.fit.util.JCasUtil;
import org.apache.uima.jcas.JCas;
import org.apache.uima.util.Level;

import de.tudarmstadt.ukp.dkpro.core.api.segmentation.type.Token;

public class DatabaseUpdater extends JCasConsumer_ImplBase {
	public static final String PARAM_DATABASE = "Database";
	public static final String PARAM_USER = "User";
	public static final String PARAM_PASSWORD = "Password";
	
	@ConfigurationParameter(name = PARAM_DATABASE,
			description = "The database file location",
			mandatory = true)
	private String databaseLocation;
	@ConfigurationParameter(name = PARAM_USER,
			description = "The database user name",
			mandatory = true)
	private String databaseUser;
	@ConfigurationParameter(name = PARAM_PASSWORD,
			description = "The database user's password",
			mandatory = true)
	private String databasePassword;

	@Override
	public void process(JCas aJCas) throws AnalysisEngineProcessException {
		getContext().getLogger().log(Level.INFO, "Updating database...");
		try (DatabaseHandler dbHandler = new DatabaseHandler(databaseLocation, databaseUser, databasePassword)) {
			for (Token t : JCasUtil.select(aJCas, Token.class))
				dbHandler.updateStatisticsForToken(t.getCoveredText(), Language.valueOf(aJCas.getDocumentLanguage().toUpperCase()));
		}
		catch (ClassNotFoundException | SQLException | DatabaseAccessException exc) {
			throw new AnalysisEngineProcessException(exc.getMessage(), null, exc.getCause());
		}
		getContext().getLogger().log(Level.INFO, "Database updated");
	}
}
