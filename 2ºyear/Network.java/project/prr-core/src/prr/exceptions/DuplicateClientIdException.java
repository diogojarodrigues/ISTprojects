package prr.exceptions;

public class DuplicateClientIdException extends Exception{
	private String _key;

	public DuplicateClientIdException(String key) {
		_key = key;
	}

	public String getKey() { return _key; }
}
