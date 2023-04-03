package prr.exceptions;

public class DestinationIsBusyException extends Exception {
	private String _key;

	public DestinationIsBusyException(String key) {
		_key = key;
	}

	public String getKey() { return _key; }
}
