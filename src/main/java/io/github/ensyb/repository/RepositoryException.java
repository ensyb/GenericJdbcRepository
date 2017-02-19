package io.github.ensyb.repository;

public class RepositoryException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public RepositoryException(String message) {
		super(message);
	}

	public RepositoryException(Throwable cause) {
		super(cause);
	}

	public RepositoryException(Throwable cause, String massage) {
		super(massage,cause);
	}
}
