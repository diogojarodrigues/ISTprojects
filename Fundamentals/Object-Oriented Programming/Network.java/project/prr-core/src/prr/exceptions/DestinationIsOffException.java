package prr.exceptions;

public class DestinationIsOffException extends Exception {
	private String _key;

	public DestinationIsOffException(String key) {
		_key = key;
	}

	public String getKey() { return _key; }
}
