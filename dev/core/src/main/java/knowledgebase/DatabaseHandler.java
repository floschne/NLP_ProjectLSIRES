package knowledgebase;

import org.h2.*;

import java.sql.*;
import java.io.File;

import data.input.Language;

/**
 * Manages the database storing language information.
 *
 * @author Julian Betz
 * @version 1.0
 */
@SuppressWarnings("unused")
public class DatabaseHandler implements AutoCloseable {
	// Default database location
	public static final String DEFAULT_LOCATION = "./res/main/data/database";
	// Default database access data
	public static final String DEFAULT_USER = "auto";
	public static final String DEFAULT_PASSWORD = "";
	// Database table names
	private static final String TABLE_LANGFREQ = "languageFrequency";
	private static final String TABLE_TOKENFREQ = "tokenFrequencyPerLanguage";
	// Database column names
	private static final String COLUMN_LANGFREQ_LANGUAGE = "language";
	private static final String COLUMN_LANGFREQ_COUNT = "count";
	private static final String COLUMN_TOKENFREQ_TOKEN = "token";
	
	private Connection con = null;
	
	/**
	 * <p>Establishes a connection to the database.</p>
	 * <p>If there is no database at the specified location yet, a new database is created.</p>
	 * @throws ClassNotFoundException if the database driver was not found
	 * @throws SQLException if the database cannot be accessed
	 * @throws DatabaseAccessException if the database was closed prematurely
	 */
	public DatabaseHandler(String location, String user, String password) throws ClassNotFoundException, SQLException, DatabaseAccessException {
		Class.forName("org.h2.Driver");
		if (new File(location + ".mv.db").exists())
			con = DriverManager.getConnection("jdbc:h2:" + location, user, password); // Assume that the specified file is indeed a database
		else {
			con = DriverManager.getConnection("jdbc:h2:" + location, user, password); // Create a new database
			createTables();
		}
	}
	
	/**
	 * <p>Creates the necessary tables in the database.</p> 
	 * <p>A table is created that stores the frequency of tokens of a specific language.
	 * In this table, a record is inserted for each language as given by {@link Language} and its token counter initialized to {@code 0}.
	 * A second table is created that stores the frequency of specific tokens on a per-language basis.</p>
	 * @throws SQLException if an exception occurs while accessing the database
	 * @throws DatabaseAccessException if the database is already closed
	 */
	private void createTables() throws SQLException, DatabaseAccessException {
		if (isClosed())
			throw new DatabaseAccessException("Database already closed");
		try (Statement stm = con.createStatement()) {
			// Create a table that stores the total of seen tokens for each language
			stm.executeUpdate("CREATE TABLE " + TABLE_LANGFREQ + " (" + COLUMN_LANGFREQ_LANGUAGE + " VARCHAR PRIMARY KEY NOT NULL, " + COLUMN_LANGFREQ_COUNT + " INT NOT NULL);");
			// Create a table that stores the number of seen instances of a token in each language
			StringBuffer s = new StringBuffer("CREATE TABLE " + TABLE_TOKENFREQ + " (" + COLUMN_TOKENFREQ_TOKEN + " VARCHAR PRIMARY KEY NOT NULL");
			for (Language l : Language.values())
				s.append(", " + l.toString() + " INT NOT NULL");
			s.append(");");
			stm.executeUpdate(s.toString());
		}
		try (PreparedStatement pstm = con.prepareStatement("INSERT INTO " + TABLE_LANGFREQ + " (" + COLUMN_LANGFREQ_LANGUAGE + ", " + COLUMN_LANGFREQ_COUNT + ") VALUES (?, ?);")) {
			pstm.setInt(2, 0);
			for (Language l : Language.values()) {
				pstm.setString(1, l.toString());
				pstm.executeUpdate();
			}
		}
	}

	/**
	 * <p>Updates the statistics based on the specified token in the specified language.</p>
	 * <p>The tables in the database (as created in {@link #createTables()}) are assumed to be existing.</p>
	 * @param token the token based on which the statistics will be updated
	 * @param language the language the token is used in
	 * @throws SQLException if an exception occurs while accessing the database
	 * @throws DatabaseAccessException if the database is already closed
	 */
	public void updateStatisticsForToken(String token, Language language) throws SQLException, DatabaseAccessException {
		if (isClosed())
			throw new DatabaseAccessException("Database already closed");
		try (Statement stm = con.createStatement();
				ResultSet rs = stm.executeQuery("SELECT * FROM " + TABLE_TOKENFREQ + " WHERE (" + COLUMN_TOKENFREQ_TOKEN + " = '" + escapeString(token) + "');")) {
			if (rs.next()) // If there is a record
				stm.executeUpdate("UPDATE " + TABLE_TOKENFREQ + " SET " + language.toString() + " = " + language.toString() + " + 1 WHERE " + COLUMN_TOKENFREQ_TOKEN + " = '" + escapeString(token) + "';"); // Being the primary key, the token can be assumed to be unique
			else {
				StringBuffer s = new StringBuffer("INSERT INTO " + TABLE_TOKENFREQ + " (" + COLUMN_TOKENFREQ_TOKEN);
				for (Language l : Language.values())
					s.append(", " + l.toString());
				s.append(") VALUES ('" + escapeString(token) + "'");
				for (Language l : Language.values()) // The values() method of the Enum type returns an array with a consistent order for each call
					s.append(", " + (l.equals(language) ? 1 : 0));
				s.append(");");
				stm.executeUpdate(s.toString());
			}
			stm.executeUpdate("UPDATE " + TABLE_LANGFREQ + " SET " + COLUMN_LANGFREQ_COUNT + " = " + COLUMN_LANGFREQ_COUNT + " + 1 WHERE " + COLUMN_LANGFREQ_LANGUAGE + " = '" + escapeString(language.toString()) + "';");
		}
	}
	
	/**
	 * Returns the number of tokens that contributed to the statistics in the database.
	 * @return the number of tokens that contributed to the statistics in the database
	 * @throws SQLException if an exception occurs while accessing the database
	 * @throws DatabaseAccessException if the database is already closed
	 */
	public int totalNumberOfTokens() throws SQLException, DatabaseAccessException {
		if (isClosed())
			throw new DatabaseAccessException("Database already closed");
		try (Statement stm = con.createStatement();
				ResultSet rs = stm.executeQuery("SELECT * FROM " + TABLE_LANGFREQ + ";");) {
			int total = 0;
			while (rs.next())
				total += rs.getInt(COLUMN_LANGFREQ_COUNT);
			return total;
		}
	}
	
	/**
	 * Returns the probability under the model in the database that a token is randomly drawn from a text.
	 * This is the prior distribution of the token under the model in the database.
	 * @param token the token to determine the likelihood for
	 * @return the probability under the model in the database that a token is randomly drawn form a text
	 * @throws SQLException if an exception occurs while accessing the database
	 * @throws DatabaseAccessException if the database is already closed
	 */
	public double tokenLikelihood(String token) throws SQLException, DatabaseAccessException {
		if (isClosed())
			throw new DatabaseAccessException("Database already closed");
		try (Statement stm = con.createStatement();
				ResultSet rs = stm.executeQuery("SELECT * FROM " + TABLE_TOKENFREQ + " WHERE (" + COLUMN_TOKENFREQ_TOKEN + " = '" + escapeString(token) + "');");) {
			double tokenFrequency = 0;
			if (rs.next())
				for (Language l : Language.values())
					tokenFrequency += rs.getInt(l.toString());
			return tokenFrequency / totalNumberOfTokens();
		}
	}
	
	/**
	 * Returns the probability under the model in the database that a token, randomly drawn from a text, stems from the specified language.
	 * This is the prior distribution of the language under the model in the database.
	 * @param language the language to determine the likelihood for
	 * @return the probability under the model in the database that a token, randomly drawn from a text, stems from the specified language
	 * @throws SQLException if an exception occurs while accessing the database
	 * @throws DatabaseAccessException if the database is already closed
	 */
	public double languageLikelihood(Language language) throws SQLException, DatabaseAccessException {
		if (isClosed())
			throw new DatabaseAccessException("Database already closed");
		try (Statement stm = con.createStatement();
				ResultSet rs = stm.executeQuery("SELECT * FROM " + TABLE_LANGFREQ + " WHERE (" + COLUMN_LANGFREQ_LANGUAGE + " = '" + escapeString(language.toString()) + "');");) {
			double languageFrequency = 0;
			if (rs.next())
				languageFrequency = rs.getInt(COLUMN_LANGFREQ_COUNT);
			return languageFrequency / totalNumberOfTokens();
		}
	}
	
	/**
	 * Returns the probability under the model in the database that the specified token is drawn randomly from texts in the specified language.
	 * This is the posterior distribution of the token under the model in the database.
	 * @param token the token to determine the likelihood for
	 * @param language the language the token is assumed to occur in
	 * @return the probability under the model in the database that the specified token is drawn randomly from texts in the specified language
	 * @throws SQLException if an exception occurs while accessing the database
	 * @throws DatabaseAccessException if the database is already closed
	 * @throws DatabaseModelException if there is no data for the specified language in the database
	 */
	public double tokenLikelihoodGivenLanguage(String token, Language language) throws SQLException, DatabaseAccessException, DatabaseModelException {
		if (isClosed())
			throw new DatabaseAccessException("Database already closed");
		try (Statement stm = con.createStatement()) {
			double tokenFrequencyGivenLanguage = 0;
			try (ResultSet rs = stm.executeQuery("SELECT * FROM " + TABLE_TOKENFREQ + " WHERE (" + COLUMN_TOKENFREQ_TOKEN + " = '" + escapeString(token) + "');")) {
				if (rs.next()) // If there is a record
					tokenFrequencyGivenLanguage = rs.getInt(language.toString());
			}
			try (ResultSet rs = stm.executeQuery("SELECT * FROM " + TABLE_LANGFREQ + " WHERE (" + COLUMN_LANGFREQ_LANGUAGE + " = '" + escapeString(language.toString()) + "');")) {
				if (rs.next())
					return tokenFrequencyGivenLanguage / rs.getInt(COLUMN_LANGFREQ_COUNT);
				else // Database corrupted
					throw new DatabaseModelException("The specified language was not found in the database");
			}
		}
	}
	
	/**
	 * <p>Returns the probability under the model in the database that the specified language is used given the occurrence of the specified token.
	 * This is the posterior distribution of the language under the model in the database.</p>
	 * <p>For unknown tokens, the result is uniformly distributed over the languages.</p>
	 * @param token the token that occurred
	 * @param language the language to estimate the likelihood for
	 * @return the probability under the model in the database that the specified language is used given the occurrence of the specified token
	 * @throws SQLException if an exception occurs while accessing the database
	 * @throws DatabaseAccessException if the database is already closed
	 * @throws DatabaseModelException if there is no data for the specified language in the database
	 */
	public double languageLikelihoodGivenToken(String token, Language language) throws SQLException, DatabaseAccessException {
		if (isClosed())
			throw new DatabaseAccessException("Database already closed");
		try (Statement stm = con.createStatement()) {
			try (ResultSet rs = stm.executeQuery("SELECT * FROM " + TABLE_TOKENFREQ + " WHERE (" + COLUMN_TOKENFREQ_TOKEN + " = '" + escapeString(token) + "');")) {
				if (rs.next()) { // If there is a record
					double tokenAndLanguageFrequency = rs.getInt(language.toString());
					int tokenFrequency = 0;
					for (Language l : Language.values())
						tokenFrequency += rs.getInt(l.toString());
					return tokenAndLanguageFrequency / tokenFrequency;
				}
				else
					return 1. / Language.values().length; // Token unknown: indecisive, assign equal probability to all languages
			}
		}
	}
	
	/**
	 * Prints the language-frequency table to standard output.
	 * @throws SQLException if an exception occurs while accessing the database
	 * @throws DatabaseAccessException if the database is already closed
	 */
	void printLanguageFrequencyTable() throws SQLException, DatabaseAccessException {
		if (isClosed())
			throw new DatabaseAccessException("Database already closed");
		try (Statement stm = con.createStatement();
				ResultSet rs = stm.executeQuery("SELECT * FROM " + TABLE_LANGFREQ + ";")) {
			while (rs.next())
				System.out.println(rs.getString(COLUMN_LANGFREQ_LANGUAGE) + ", " + rs.getInt(COLUMN_LANGFREQ_COUNT) + ", ");
		}
	}
	
	/**
	 * Prints the token-frequency-per-language table to standard output.
	 * @throws SQLException if an exception occurs while accessing the database
	 * @throws DatabaseAccessException if the database is already closed
	 */
	void printTokenFrequencyPerLanguageTable() throws SQLException, DatabaseAccessException {
		if (isClosed())
			throw new DatabaseAccessException("Database already closed");
		try (Statement stm = con.createStatement();
				ResultSet rs = stm.executeQuery("SELECT * FROM " + TABLE_TOKENFREQ + ";")) {
			while (rs.next()) {
				StringBuffer s = new StringBuffer(rs.getString(COLUMN_TOKENFREQ_TOKEN) + ", ");
				for (Language l : Language.values())
					s.append(rs.getInt(l.toString()) + ", ");
				System.out.println(s.toString());
			}
		}
	}
	
	/**
	 * Escapes the specified {@code String} according to the conventions of SQL in order to use it in SQL statements.
	 * @param s the {@code String} to escape
	 * @return the specified {@code String}, escaped according to the conventions of SQL
	 */
	public static String escapeString(String s) {
		return s.replaceAll("'", "''");
	}
	
	/**
	 * Identifies if this {@code DatabaseHandler} object is already closed.
	 * @return {@code true} if this {@code DatabaseHandler} object is already closed; {@code false} otherwise
	 */
	public boolean isClosed() {
		return con == null;
	}
	
	/**
	 * <p>Closes the database connection.</p>
	 * <p>Calling {@code close()} on a {@code DatabaseHandler} object that is already closed is a no-op.</p>
	 * @throws SQLException if a database access error occurs
	 */
	@Override
	public void close() throws SQLException {
		if (con != null)
			con.close();
		con = null;
	}
}
