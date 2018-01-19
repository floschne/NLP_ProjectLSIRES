package knowledgebase;

import static org.junit.Assert.*;

import java.io.File;
import java.sql.SQLException;

import org.junit.*;

import knowledgebase.DatabaseAccessException;
import knowledgebase.DatabaseHandler;
import knowledgebase.DatabaseModelException;

public class DatabaseTest {
	private static final double DELTA = 0.000001;
	private static final String DATABASE_LOCATION = "./res/test/data/database";
	private static File database;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		database = new File(DATABASE_LOCATION + ".mv.db");
		if (database.exists()) {
			System.err.println("Tests cannot be performed because " + DATABASE_LOCATION + ".mv.db would be overwritten");
			System.exit(1);
		}
	}
	
	@After
	public void tearDown() {
		database.delete();
	}

	@Test
	public void testDatabaseCreation() {
		try (DatabaseHandler handler = new DatabaseHandler(DATABASE_LOCATION, DatabaseHandler.DATABASE_USER, DatabaseHandler.DATABASE_PASSWORD);) {}
		catch (ClassNotFoundException exc) {
			System.err.println(exc.getClass().getSimpleName() + ": " + exc.getMessage());
			fail("Unable to find the database package");
		}
		catch (SQLException | DatabaseAccessException exc) {
			System.err.println(exc.getClass().getSimpleName() + ": " + exc.getMessage());
			fail("Unable to create tables");
		}
	}
	
	@Test
	public void testAccessExistingDatabase() {
		try (DatabaseHandler handler = new DatabaseHandler(DATABASE_LOCATION, DatabaseHandler.DATABASE_USER, DatabaseHandler.DATABASE_PASSWORD);) {}
		catch (ClassNotFoundException exc) {
			System.err.println(exc.getClass().getSimpleName() + ": " + exc.getMessage());
			fail("Unable to find the database package");
		}
		catch (SQLException | DatabaseAccessException exc) {
			System.err.println(exc.getClass().getSimpleName() + ": " + exc.getMessage());
			fail("Unable to create tables");
		}
		try (DatabaseHandler handler = new DatabaseHandler(DATABASE_LOCATION, DatabaseHandler.DATABASE_USER, DatabaseHandler.DATABASE_PASSWORD);) {}
		catch (ClassNotFoundException exc) {
			System.err.println(exc.getClass().getSimpleName() + ": " + exc.getMessage());
			fail("Unable to find the database package");
		}
		catch (SQLException | DatabaseAccessException exc) {
			System.err.println(exc.getClass().getSimpleName() + ": " + exc.getMessage());
			fail("Unable to manage database");
		}
	}
	
	@Test
	public void testDatabaseUpdate() {
		try (DatabaseHandler handler = new DatabaseHandler(DATABASE_LOCATION, DatabaseHandler.DATABASE_USER, DatabaseHandler.DATABASE_PASSWORD);) {
			System.out.println("Database state before insertion:");
			handler.printLanguageFrequencyTable();
			System.out.println();
			handler.printTokenFrequencyPerLanguageTable();
			System.out.println();
			System.out.println("--------------------");
			System.out.println();
			
			// XXX Use WikiArticle.Language instead of DatabaseHandler.Language
			handler.updateStatisticsForToken("hello", DatabaseHandler.Language.EN);
			handler.updateStatisticsForToken("in", DatabaseHandler.Language.EN);
			handler.updateStatisticsForToken("in", DatabaseHandler.Language.DE);
			handler.updateStatisticsForToken("muchacho", DatabaseHandler.Language.ES);
			handler.updateStatisticsForToken("muchacho", DatabaseHandler.Language.ES);
			handler.updateStatisticsForToken("hello", DatabaseHandler.Language.EN);
			
			System.out.println("Database state after insertion:");
			handler.printLanguageFrequencyTable();
			System.out.println();
			handler.printTokenFrequencyPerLanguageTable();
			System.out.println();
			System.out.println("====================");
			System.out.println();
		}
		catch (ClassNotFoundException exc) {
			System.err.println(exc.getClass().getSimpleName() + ": " + exc.getMessage());
			fail("Unable to find the database package");
		}
		catch (SQLException | DatabaseAccessException exc) {
			System.err.println(exc.getClass().getSimpleName() + ": " + exc.getMessage());
			fail("Unable to manage database");
		}
	}
	
	@Test
	public void testTotalNumberOfTokens() {
		try (DatabaseHandler handler = new DatabaseHandler(DATABASE_LOCATION, DatabaseHandler.DATABASE_USER, DatabaseHandler.DATABASE_PASSWORD);) {
			// XXX Use WikiArticle.Language instead of DatabaseHandler.Language
			handler.updateStatisticsForToken("hello", DatabaseHandler.Language.EN);
			handler.updateStatisticsForToken("in", DatabaseHandler.Language.EN);
			handler.updateStatisticsForToken("in", DatabaseHandler.Language.DE);
			handler.updateStatisticsForToken("muchacho", DatabaseHandler.Language.ES);
			handler.updateStatisticsForToken("muchacho", DatabaseHandler.Language.ES);
			handler.updateStatisticsForToken("hello", DatabaseHandler.Language.EN);
			assertEquals(6, handler.totalNumberOfTokens());
		}
		catch (ClassNotFoundException exc) {
			System.err.println(exc.getClass().getSimpleName() + ": " + exc.getMessage());
			fail("Unable to find the database package");
		}
		catch (SQLException | DatabaseAccessException exc) {
			System.err.println(exc.getClass().getSimpleName() + ": " + exc.getMessage());
			fail("Unable to manage database");
		}
	}
	
	@Test
	public void testTokenPriors() {
		try (DatabaseHandler handler = new DatabaseHandler(DATABASE_LOCATION, DatabaseHandler.DATABASE_USER, DatabaseHandler.DATABASE_PASSWORD);) {
			// XXX Use WikiArticle.Language instead of DatabaseHandler.Language
			handler.updateStatisticsForToken("hello", DatabaseHandler.Language.EN);
			handler.updateStatisticsForToken("in", DatabaseHandler.Language.EN);
			handler.updateStatisticsForToken("in", DatabaseHandler.Language.DE);
			handler.updateStatisticsForToken("muchacho", DatabaseHandler.Language.ES);
			handler.updateStatisticsForToken("muchacho", DatabaseHandler.Language.ES);
			handler.updateStatisticsForToken("hello", DatabaseHandler.Language.EN);
			
			// Positive examples
			assertEquals(1. / 3, handler.tokenLikelihood("hello"), DELTA);
			assertEquals(1. / 3, handler.tokenLikelihood("in"), DELTA);
			assertEquals(1. / 3, handler.tokenLikelihood("muchacho"), DELTA);
			
			// Negative examples
			assertEquals(0, handler.tokenLikelihood("llamado"), DELTA);
			
			assertEquals(0, handler.tokenLikelihood("IN"), DELTA);
			assertEquals(0, handler.tokenLikelihood("In"), DELTA);
			assertEquals(0, handler.tokenLikelihood(" in"), DELTA);
			assertEquals(0, handler.tokenLikelihood("in "), DELTA);
			assertEquals(0, handler.tokenLikelihood(" in "), DELTA);
			assertEquals(0, handler.tokenLikelihood("in,"), DELTA);
			assertEquals(0, handler.tokenLikelihood("in."), DELTA);			
		}
		catch (ClassNotFoundException exc) {
			System.err.println(exc.getClass().getSimpleName() + ": " + exc.getMessage());
			fail("Unable to find the database package");
		}
		catch (SQLException | DatabaseAccessException exc) {
			System.err.println(exc.getClass().getSimpleName() + ": " + exc.getMessage());
			fail("Unable to manage database");
		}
	}
	
	@Test
	public void testLanguagePriors() {
		try (DatabaseHandler handler = new DatabaseHandler(DATABASE_LOCATION, DatabaseHandler.DATABASE_USER, DatabaseHandler.DATABASE_PASSWORD);) {
			// XXX Use WikiArticle.Language instead of DatabaseHandler.Language
			handler.updateStatisticsForToken("hello", DatabaseHandler.Language.EN);
			handler.updateStatisticsForToken("in", DatabaseHandler.Language.EN);
			handler.updateStatisticsForToken("in", DatabaseHandler.Language.DE);
			handler.updateStatisticsForToken("muchacho", DatabaseHandler.Language.ES);
			handler.updateStatisticsForToken("muchacho", DatabaseHandler.Language.ES);
			handler.updateStatisticsForToken("hello", DatabaseHandler.Language.EN);
			
			assertEquals(0.5, handler.languageLikelihood(DatabaseHandler.Language.EN), DELTA);
			assertEquals(1. / 6, handler.languageLikelihood(DatabaseHandler.Language.DE), DELTA);
			assertEquals(1. / 3, handler.languageLikelihood(DatabaseHandler.Language.ES), DELTA);
		}
		catch (ClassNotFoundException exc) {
			System.err.println(exc.getClass().getSimpleName() + ": " + exc.getMessage());
			fail("Unable to find the database package");
		}
		catch (SQLException | DatabaseAccessException exc) {
			System.err.println(exc.getClass().getSimpleName() + ": " + exc.getMessage());
			fail("Unable to manage database");
		}
	}
	
	@Test
	public void testTokenPosteriors() {
		try (DatabaseHandler handler = new DatabaseHandler(DATABASE_LOCATION, DatabaseHandler.DATABASE_USER, DatabaseHandler.DATABASE_PASSWORD);) {
			// XXX Use WikiArticle.Language instead of DatabaseHandler.Language
			handler.updateStatisticsForToken("hello", DatabaseHandler.Language.EN);
			handler.updateStatisticsForToken("in", DatabaseHandler.Language.EN);
			handler.updateStatisticsForToken("in", DatabaseHandler.Language.DE);
			handler.updateStatisticsForToken("muchacho", DatabaseHandler.Language.ES);
			handler.updateStatisticsForToken("muchacho", DatabaseHandler.Language.ES);
			handler.updateStatisticsForToken("hello", DatabaseHandler.Language.EN);
			
			// Positive examples
			assertEquals(2. / 3, handler.tokenLikelihoodGivenLanguage("hello", DatabaseHandler.Language.EN), DELTA);
			assertEquals(1. / 3, handler.tokenLikelihoodGivenLanguage("in", DatabaseHandler.Language.EN), DELTA);
			assertEquals(1, handler.tokenLikelihoodGivenLanguage("in", DatabaseHandler.Language.DE), DELTA);
			assertEquals(1, handler.tokenLikelihoodGivenLanguage("muchacho", DatabaseHandler.Language.ES), DELTA);

			// Negative examples
			assertEquals(0, handler.tokenLikelihoodGivenLanguage("muchacho", DatabaseHandler.Language.EN), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage("muchacho", DatabaseHandler.Language.DE), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage("hello", DatabaseHandler.Language.DE), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage("hello", DatabaseHandler.Language.ES), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage("in", DatabaseHandler.Language.ES), DELTA);
			
			assertEquals(0, handler.tokenLikelihoodGivenLanguage("llamado", DatabaseHandler.Language.EN), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage("llamado", DatabaseHandler.Language.DE), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage("llamado", DatabaseHandler.Language.ES), DELTA);
			
			assertEquals(0, handler.tokenLikelihoodGivenLanguage("IN", DatabaseHandler.Language.EN), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage("In", DatabaseHandler.Language.EN), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage(" in", DatabaseHandler.Language.EN), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage("in ", DatabaseHandler.Language.EN), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage(" in ", DatabaseHandler.Language.EN), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage("in,", DatabaseHandler.Language.EN), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage("in.", DatabaseHandler.Language.EN), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage("IN", DatabaseHandler.Language.DE), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage("In", DatabaseHandler.Language.DE), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage(" in", DatabaseHandler.Language.DE), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage("in ", DatabaseHandler.Language.DE), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage(" in ", DatabaseHandler.Language.DE), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage("in,", DatabaseHandler.Language.DE), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage("in.", DatabaseHandler.Language.DE), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage("IN", DatabaseHandler.Language.ES), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage("In", DatabaseHandler.Language.ES), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage(" in", DatabaseHandler.Language.ES), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage("in ", DatabaseHandler.Language.ES), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage(" in ", DatabaseHandler.Language.ES), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage("in,", DatabaseHandler.Language.ES), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage("in.", DatabaseHandler.Language.ES), DELTA);
		}
		catch (ClassNotFoundException exc) {
			System.err.println(exc.getClass().getSimpleName() + ": " + exc.getMessage());
			fail("Unable to find the database package");
		}
		catch (SQLException | DatabaseAccessException exc) {
			System.err.println(exc.getClass().getSimpleName() + ": " + exc.getMessage());
			fail("Unable to manage database");
		}
		catch (DatabaseModelException exc) {
			System.err.println(exc.getClass().getSimpleName() + ": " + exc.getMessage());
			fail("Database model corrupted");
		}
	}
	
	@Test
	public void testLanguagePosteriors() {
		try (DatabaseHandler handler = new DatabaseHandler(DATABASE_LOCATION, DatabaseHandler.DATABASE_USER, DatabaseHandler.DATABASE_PASSWORD);) {
			// XXX Use WikiArticle.Language instead of DatabaseHandler.Language
			handler.updateStatisticsForToken("hello", DatabaseHandler.Language.EN);
			handler.updateStatisticsForToken("in", DatabaseHandler.Language.EN);
			handler.updateStatisticsForToken("in", DatabaseHandler.Language.DE);
			handler.updateStatisticsForToken("muchacho", DatabaseHandler.Language.ES);
			handler.updateStatisticsForToken("muchacho", DatabaseHandler.Language.ES);
			handler.updateStatisticsForToken("hello", DatabaseHandler.Language.EN);
			
			// Positive examples
			assertEquals(1, handler.languageLikelihoodGivenToken("hello", DatabaseHandler.Language.EN), DELTA);
			assertEquals(0.5, handler.languageLikelihoodGivenToken("in", DatabaseHandler.Language.EN), DELTA);
			assertEquals(0.5, handler.languageLikelihoodGivenToken("in", DatabaseHandler.Language.DE), DELTA);
			assertEquals(1, handler.languageLikelihoodGivenToken("muchacho", DatabaseHandler.Language.ES), DELTA);
			
			// Negative examples
			assertEquals(0, handler.languageLikelihoodGivenToken("hello", DatabaseHandler.Language.DE), DELTA);
			assertEquals(0, handler.languageLikelihoodGivenToken("hello", DatabaseHandler.Language.ES), DELTA);
			assertEquals(0, handler.languageLikelihoodGivenToken("in", DatabaseHandler.Language.ES), DELTA);
			assertEquals(0, handler.languageLikelihoodGivenToken("muchacho", DatabaseHandler.Language.EN), DELTA);
			assertEquals(0, handler.languageLikelihoodGivenToken("muchacho", DatabaseHandler.Language.DE), DELTA);
			
			assertEquals(1. / DatabaseHandler.Language.values().length, handler.languageLikelihoodGivenToken("llamado", DatabaseHandler.Language.EN), DELTA);
			assertEquals(1. / DatabaseHandler.Language.values().length, handler.languageLikelihoodGivenToken("llamado", DatabaseHandler.Language.DE), DELTA);
			assertEquals(1. / DatabaseHandler.Language.values().length, handler.languageLikelihoodGivenToken("llamado", DatabaseHandler.Language.ES), DELTA);
			
			assertEquals(1. / DatabaseHandler.Language.values().length, handler.languageLikelihoodGivenToken("IN", DatabaseHandler.Language.EN), DELTA);
			assertEquals(1. / DatabaseHandler.Language.values().length, handler.languageLikelihoodGivenToken("In", DatabaseHandler.Language.EN), DELTA);
			assertEquals(1. / DatabaseHandler.Language.values().length, handler.languageLikelihoodGivenToken(" in", DatabaseHandler.Language.EN), DELTA);
			assertEquals(1. / DatabaseHandler.Language.values().length, handler.languageLikelihoodGivenToken("in ", DatabaseHandler.Language.EN), DELTA);
			assertEquals(1. / DatabaseHandler.Language.values().length, handler.languageLikelihoodGivenToken(" in ", DatabaseHandler.Language.EN), DELTA);
			assertEquals(1. / DatabaseHandler.Language.values().length, handler.languageLikelihoodGivenToken("in,", DatabaseHandler.Language.EN), DELTA);
			assertEquals(1. / DatabaseHandler.Language.values().length, handler.languageLikelihoodGivenToken("in.", DatabaseHandler.Language.EN), DELTA);
			assertEquals(1. / DatabaseHandler.Language.values().length, handler.languageLikelihoodGivenToken("IN", DatabaseHandler.Language.DE), DELTA);
			assertEquals(1. / DatabaseHandler.Language.values().length, handler.languageLikelihoodGivenToken("In", DatabaseHandler.Language.DE), DELTA);
			assertEquals(1. / DatabaseHandler.Language.values().length, handler.languageLikelihoodGivenToken(" in", DatabaseHandler.Language.DE), DELTA);
			assertEquals(1. / DatabaseHandler.Language.values().length, handler.languageLikelihoodGivenToken("in ", DatabaseHandler.Language.DE), DELTA);
			assertEquals(1. / DatabaseHandler.Language.values().length, handler.languageLikelihoodGivenToken(" in ", DatabaseHandler.Language.DE), DELTA);
			assertEquals(1. / DatabaseHandler.Language.values().length, handler.languageLikelihoodGivenToken("in,", DatabaseHandler.Language.DE), DELTA);
			assertEquals(1. / DatabaseHandler.Language.values().length, handler.languageLikelihoodGivenToken("in.", DatabaseHandler.Language.DE), DELTA);
			assertEquals(1. / DatabaseHandler.Language.values().length, handler.languageLikelihoodGivenToken("IN", DatabaseHandler.Language.ES), DELTA);
			assertEquals(1. / DatabaseHandler.Language.values().length, handler.languageLikelihoodGivenToken("In", DatabaseHandler.Language.ES), DELTA);
			assertEquals(1. / DatabaseHandler.Language.values().length, handler.languageLikelihoodGivenToken(" in", DatabaseHandler.Language.ES), DELTA);
			assertEquals(1. / DatabaseHandler.Language.values().length, handler.languageLikelihoodGivenToken("in ", DatabaseHandler.Language.ES), DELTA);
			assertEquals(1. / DatabaseHandler.Language.values().length, handler.languageLikelihoodGivenToken(" in ", DatabaseHandler.Language.ES), DELTA);
			assertEquals(1. / DatabaseHandler.Language.values().length, handler.languageLikelihoodGivenToken("in,", DatabaseHandler.Language.ES), DELTA);
			assertEquals(1. / DatabaseHandler.Language.values().length, handler.languageLikelihoodGivenToken("in.", DatabaseHandler.Language.ES), DELTA);
		}
		catch (ClassNotFoundException exc) {
			System.err.println(exc.getClass().getSimpleName() + ": " + exc.getMessage());
			fail("Unable to find the database package");
		}
		catch (SQLException | DatabaseAccessException exc) {
			System.err.println(exc.getClass().getSimpleName() + ": " + exc.getMessage());
			fail("Unable to manage database");
		}
	}
}
