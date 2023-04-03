package prr.exceptions;

public class UnsupportedAtDestinationException extends Exception {
	private String _key;
	private String _type;

	public UnsupportedAtDestinationException(String key, String type) {
		_key = key;
		_type = type;
	}

	public String getKey() { return _key; }
	public String getType() { return _type; }
}
