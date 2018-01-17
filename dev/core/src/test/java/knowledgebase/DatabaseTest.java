package knowledgebase;

import static org.junit.Assert.*;

import java.io.File;
import java.sql.SQLException;

import org.junit.*;

import knowledgebase.DatabaseAccessException;
import knowledgebase.DatabaseHandler;
import knowledgebase.DatabaseModelException;

import data.input.Language;
import static data.input.Language.*;

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
			
			handler.updateStatisticsForToken("hello", EN);
			handler.updateStatisticsForToken("in", EN);
			handler.updateStatisticsForToken("in", DE);
			handler.updateStatisticsForToken("muchacho", ES);
			handler.updateStatisticsForToken("muchacho", ES);
			handler.updateStatisticsForToken("hello", EN);
			
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
			handler.updateStatisticsForToken("hello", EN);
			handler.updateStatisticsForToken("in", EN);
			handler.updateStatisticsForToken("in", DE);
			handler.updateStatisticsForToken("muchacho", ES);
			handler.updateStatisticsForToken("muchacho", ES);
			handler.updateStatisticsForToken("hello", EN);
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
			handler.updateStatisticsForToken("hello", EN);
			handler.updateStatisticsForToken("in", EN);
			handler.updateStatisticsForToken("in", DE);
			handler.updateStatisticsForToken("muchacho", ES);
			handler.updateStatisticsForToken("muchacho", ES);
			handler.updateStatisticsForToken("hello", EN);
			
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
			handler.updateStatisticsForToken("hello", EN);
			handler.updateStatisticsForToken("in", EN);
			handler.updateStatisticsForToken("in", DE);
			handler.updateStatisticsForToken("muchacho", ES);
			handler.updateStatisticsForToken("muchacho", ES);
			handler.updateStatisticsForToken("hello", EN);
			
			assertEquals(0.5, handler.languageLikelihood(EN), DELTA);
			assertEquals(1. / 6, handler.languageLikelihood(DE), DELTA);
			assertEquals(1. / 3, handler.languageLikelihood(ES), DELTA);
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
			handler.updateStatisticsForToken("hello", EN);
			handler.updateStatisticsForToken("in", EN);
			handler.updateStatisticsForToken("in", DE);
			handler.updateStatisticsForToken("muchacho", ES);
			handler.updateStatisticsForToken("muchacho", ES);
			handler.updateStatisticsForToken("hello", EN);
			
			// Positive examples
			assertEquals(2. / 3, handler.tokenLikelihoodGivenLanguage("hello", EN), DELTA);
			assertEquals(1. / 3, handler.tokenLikelihoodGivenLanguage("in", EN), DELTA);
			assertEquals(1, handler.tokenLikelihoodGivenLanguage("in", DE), DELTA);
			assertEquals(1, handler.tokenLikelihoodGivenLanguage("muchacho", ES), DELTA);

			// Negative examples
			assertEquals(0, handler.tokenLikelihoodGivenLanguage("muchacho", EN), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage("muchacho", DE), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage("hello", DE), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage("hello", ES), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage("in", ES), DELTA);
			
			assertEquals(0, handler.tokenLikelihoodGivenLanguage("llamado", EN), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage("llamado", DE), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage("llamado", ES), DELTA);
			
			assertEquals(0, handler.tokenLikelihoodGivenLanguage("IN", EN), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage("In", EN), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage(" in", EN), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage("in ", EN), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage(" in ", EN), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage("in,", EN), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage("in.", EN), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage("IN", DE), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage("In", DE), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage(" in", DE), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage("in ", DE), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage(" in ", DE), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage("in,", DE), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage("in.", DE), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage("IN", ES), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage("In", ES), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage(" in", ES), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage("in ", ES), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage(" in ", ES), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage("in,", ES), DELTA);
			assertEquals(0, handler.tokenLikelihoodGivenLanguage("in.", ES), DELTA);
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
			handler.updateStatisticsForToken("hello", EN);
			handler.updateStatisticsForToken("in", EN);
			handler.updateStatisticsForToken("in", DE);
			handler.updateStatisticsForToken("muchacho", ES);
			handler.updateStatisticsForToken("muchacho", ES);
			handler.updateStatisticsForToken("hello", EN);
			
			// Positive examples
			assertEquals(1, handler.languageLikelihoodGivenToken("hello", EN), DELTA);
			assertEquals(0.5, handler.languageLikelihoodGivenToken("in", EN), DELTA);
			assertEquals(0.5, handler.languageLikelihoodGivenToken("in", DE), DELTA);
			assertEquals(1, handler.languageLikelihoodGivenToken("muchacho", ES), DELTA);
			
			// Negative examples
			assertEquals(0, handler.languageLikelihoodGivenToken("hello", DE), DELTA);
			assertEquals(0, handler.languageLikelihoodGivenToken("hello", ES), DELTA);
			assertEquals(0, handler.languageLikelihoodGivenToken("in", ES), DELTA);
			assertEquals(0, handler.languageLikelihoodGivenToken("muchacho", EN), DELTA);
			assertEquals(0, handler.languageLikelihoodGivenToken("muchacho", DE), DELTA);
			
			assertEquals(1. / Language.values().length, handler.languageLikelihoodGivenToken("llamado", EN), DELTA);
			assertEquals(1. / Language.values().length, handler.languageLikelihoodGivenToken("llamado", DE), DELTA);
			assertEquals(1. / Language.values().length, handler.languageLikelihoodGivenToken("llamado", ES), DELTA);
			
			assertEquals(1. / Language.values().length, handler.languageLikelihoodGivenToken("IN", EN), DELTA);
			assertEquals(1. / Language.values().length, handler.languageLikelihoodGivenToken("In", EN), DELTA);
			assertEquals(1. / Language.values().length, handler.languageLikelihoodGivenToken(" in", EN), DELTA);
			assertEquals(1. / Language.values().length, handler.languageLikelihoodGivenToken("in ", EN), DELTA);
			assertEquals(1. / Language.values().length, handler.languageLikelihoodGivenToken(" in ", EN), DELTA);
			assertEquals(1. / Language.values().length, handler.languageLikelihoodGivenToken("in,", EN), DELTA);
			assertEquals(1. / Language.values().length, handler.languageLikelihoodGivenToken("in.", EN), DELTA);
			assertEquals(1. / Language.values().length, handler.languageLikelihoodGivenToken("IN", DE), DELTA);
			assertEquals(1. / Language.values().length, handler.languageLikelihoodGivenToken("In", DE), DELTA);
			assertEquals(1. / Language.values().length, handler.languageLikelihoodGivenToken(" in", DE), DELTA);
			assertEquals(1. / Language.values().length, handler.languageLikelihoodGivenToken("in ", DE), DELTA);
			assertEquals(1. / Language.values().length, handler.languageLikelihoodGivenToken(" in ", DE), DELTA);
			assertEquals(1. / Language.values().length, handler.languageLikelihoodGivenToken("in,", DE), DELTA);
			assertEquals(1. / Language.values().length, handler.languageLikelihoodGivenToken("in.", DE), DELTA);
			assertEquals(1. / Language.values().length, handler.languageLikelihoodGivenToken("IN", ES), DELTA);
			assertEquals(1. / Language.values().length, handler.languageLikelihoodGivenToken("In", ES), DELTA);
			assertEquals(1. / Language.values().length, handler.languageLikelihoodGivenToken(" in", ES), DELTA);
			assertEquals(1. / Language.values().length, handler.languageLikelihoodGivenToken("in ", ES), DELTA);
			assertEquals(1. / Language.values().length, handler.languageLikelihoodGivenToken(" in ", ES), DELTA);
			assertEquals(1. / Language.values().length, handler.languageLikelihoodGivenToken("in,", ES), DELTA);
			assertEquals(1. / Language.values().length, handler.languageLikelihoodGivenToken("in.", ES), DELTA);
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
