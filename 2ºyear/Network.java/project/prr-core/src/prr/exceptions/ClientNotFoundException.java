package prr.exceptions;

public class ClientNotFoundException extends Exception{
	private String _key;

	public ClientNotFoundException(String key) {
		_key = key;
	}

	public String getKey() { return _key; }
}
