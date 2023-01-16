package prr.exceptions;

public class UnsupportedAtOriginException extends Exception {
	private String _key;
	private String _type;

	public UnsupportedAtOriginException(String key, String type) {
		_key = key;
		_type = type;
	}

	public String getKey() { return _key; }
	public String getType() { return _type; }
}
