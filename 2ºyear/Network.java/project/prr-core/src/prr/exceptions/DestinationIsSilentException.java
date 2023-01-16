package prr.exceptions;

public class DestinationIsSilentException extends Exception {
	private String _key;

	public DestinationIsSilentException(String key) {
		_key = key;
	}

	public String getKey() { return _key; }
}
