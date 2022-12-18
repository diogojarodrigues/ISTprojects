package prr.exceptions;

/** Exception thrown when a terminal key is duplicated. */
public class DuplicateTerminalIdException extends Exception {
	private String _key;

	public DuplicateTerminalIdException(String key) {
		_key = key;
	}

	public String getKey() { return _key; }
}