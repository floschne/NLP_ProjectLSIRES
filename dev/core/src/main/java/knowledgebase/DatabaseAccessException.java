package knowledgebase;

public class DatabaseAccessException extends Exception {
	private static final long serialVersionUID = 1L;

	public DatabaseAccessException() {
		super();
	}

	public DatabaseAccessException(String message) {
		super(message);
	}

	public DatabaseAccessException(Throwable cause) {
		super(cause);
	}

	public DatabaseAccessException(String message, Throwable cause) {
		super(message, cause);
	}

	public DatabaseAccessException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
