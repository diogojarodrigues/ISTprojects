package prr.notification;
import prr.client.Client;

public class Notification {
	private String _nature;
	private String _terminalId;

	public Notification(String terminalId, String nature) {
		_terminalId = terminalId;
		_nature = nature;
	}

	public String toString() {
		return _nature + "|" + _terminalId;
	}

	@Override
	public int hashCode() {
		return (_nature + _terminalId).hashCode();
	}

    @Override
    public boolean equals(Object o) {
        if (o instanceof Notification) {
            Notification comm = (Notification) o;
            return _nature.equals(comm._nature) && _terminalId.equals(comm._terminalId);
        }
        return false; 
    }
}
