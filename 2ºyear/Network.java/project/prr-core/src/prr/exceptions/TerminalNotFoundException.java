package prr.exceptions;

public class TerminalNotFoundException extends Exception{
	private String _key;

	public TerminalNotFoundException(String key) {
		_key = key;
	}

	public String getKey() { return _key; }
}