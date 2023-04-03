package prr.client;

import java.lang.Math;
import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import prr.terminals.Terminal;
import prr.communication.*;
import prr.notification.Notification;
import prr.exceptions.InvalidCommunicationException;
import prr.exceptions.ClientNotificationsAlreadyEnabledException;
import prr.exceptions.ClientNotificationsAlreadyDisabledException;


public class Client implements Serializable {
	
	private ClientStatus _status = new NormalClient(this);
	private String _id;
	private String _name;
	private int _fiscalId;
	private Map<String, Terminal> _terminals = new HashMap<String, Terminal>();	
	private List<Communication> _receivedComms = new LinkedList<>();
	private Map<Integer, Communication> _sentComms = new HashMap<>();
	private Set<Notification> _notifications = new LinkedHashSet<>();
	private DeliveryMethod _deliveryMethod = new DeliveryByApp();
	private boolean _notificationsOn = true;
	private String _receiveNotifs = "YES";
	private double _payments = 0;
	private double _debts = 0;

	private class DeliveryByApp extends DeliveryMethod {

		public void delivery(Notification notification) {
			_notifications.add(notification);
		}
	}

	public Client(String id, String name, int fiscalId) {
		_id = id;
		_name = name;
		_fiscalId = fiscalId;
	}

	public String getClientId() { return _id; }
	public double getPayments() { return _payments; }
	public double getDebts() { return _debts; }
	public double getBalance() { return _payments - _debts; }
	public boolean getNotificationsOn() { return _notificationsOn; }
	public Collection<Communication> getSentComms() { return _sentComms.values(); }
	public List<Communication> getReceivedComms() { return _receivedComms; }

	public void setStatus(ClientStatus status) { _status = status; }
	
	public void informEndComm(double commCost) { 
		_debts += commCost; 
		_status.update();
	}

	public double calculateVideoCost(Video comm) { return _status.calculateVideoCost(comm); }
	public double calculateVoiceCost(Voice comm) { return _status.calculateVoiceCost(comm); }
	public double calculateTextCost(Text comm) { return _status.calculateTextCost(comm); }

	public int fetchTerminalQuantity() { return _terminals.size(); }

	public void addTerminal(String terminalId, Terminal t) { _terminals.put(terminalId, t); }
	public void addSentCommunication(Communication comm) { _sentComms.put(comm.getId(), comm); }
	public void addReceivedCommunication(Communication comm) { _receivedComms.add(comm); }

	public void addNotification(Notification notification) {
		_deliveryMethod.delivery(notification);
	}

	public Collection<Notification> processNotifications() {
		Collection<Notification> notifications = _notifications;
		_notifications = new LinkedHashSet<>();
		return notifications;
	}

	public void disableNotifications() throws ClientNotificationsAlreadyDisabledException {
		if (_notificationsOn) {
			_notificationsOn = false;
			_receiveNotifs = "NO";
		} else {
			throw new ClientNotificationsAlreadyDisabledException();
		}
	}

	public void enableNotifications() throws ClientNotificationsAlreadyEnabledException {
		if (!_notificationsOn) {
			_notificationsOn = true;
			_receiveNotifs = "YES";
		} else {
			throw new ClientNotificationsAlreadyEnabledException();
		}
	}

	public double payCommunication(int commKey, String terminalId) throws InvalidCommunicationException {
		Communication sentComm = _sentComms.get(commKey);
		if (sentComm == null) { throw new InvalidCommunicationException(); }
		if (!sentComm.getOriginTerminal().getID().equals(terminalId)) { throw new InvalidCommunicationException(); }
		if (!sentComm.hasFinished()) { throw new InvalidCommunicationException(); }
		if (sentComm.isPaid()) { throw new InvalidCommunicationException(); }
		double price = sentComm.getCost();
		_payments += price;
		_debts -= price;
		sentComm.setAsPaid();
		_status.update();
		return price;
	}

	@Override
	public String toString() {
		return "CLIENT" + "|" + _id + "|" + _name + "|" + _fiscalId + "|" + _status.toString()
		+ "|" + _receiveNotifs + "|" + fetchTerminalQuantity() + "|" + Math.round(_payments) 
		+ "|" + Math.round(_debts); 
	} 

}