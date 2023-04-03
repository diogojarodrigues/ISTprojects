package prr.exceptions;

/** Exception for unknown terminals. */
public class InvalidTerminalIdException extends Exception {
	private String _key;

	public InvalidTerminalIdException(String key) {
		_key = key;
	}

	public String getKey() { return _key; }

}