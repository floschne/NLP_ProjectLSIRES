package knowledgebase;

public class DatabaseModelException extends Exception {
	private static final long serialVersionUID = 1L;

	public DatabaseModelException() {
		super();
	}

	public DatabaseModelException(String message) {
		super(message);
	}

	public DatabaseModelException(Throwable cause) {
		super(cause);
	}

	public DatabaseModelException(String message, Throwable cause) {
		super(message, cause);
	}

	public DatabaseModelException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}
}
