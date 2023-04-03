package prr;

import java.util.Map;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.List;
import java.util.LinkedList;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Pattern;

import java.util.regex.Matcher;
import java.io.Serializable;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import prr.client.Client;
import prr.communication.Communication;
import prr.exceptions.*;
import prr.notification.Notification;
import prr.terminals.BasicTerminal;
import prr.terminals.FancyTerminal;
import prr.terminals.Terminal;

/*
 * Class Network implements a network.
 */
public class Network implements Serializable {

	/** Serial number for serialization. */
	private static final long serialVersionUID = 202208091753L;

	/** Network object has been changed. */
	private boolean _hasChanged = false;

	/** Clients. */
	private Map<String, Client> _clients = new TreeMap<String, Client>(String.CASE_INSENSITIVE_ORDER);

	/** Terminals. */
	private Map<String, Terminal> _terminals = new TreeMap<String, Terminal>();

	/** Communications. */
	private List<Communication> _comms = new LinkedList<Communication>();

	private double _globalDebts = 0;
	private double _globalPayments = 0;
	private int _communicationID = 1;

	
	/**
	 * Read text input file and create corresponding domain entities.
	 * 
	 * @param filename name of the text input file
	 * @throws DuplicateTerminalIdException if the terminal already exists
     * @throws UnrecognizedEntryException if some entry is not correct
	 * @throws InvalidTerminalIdException if the terminalId is not valid
	 * @throws ClientNotFoundException if the client does not exist (when adding a terminal)
	 * @throws DuplicateClientIdException if the client already exist
	 * @throws IOException if there is an IO error while processing the text file
	 * @throws ImportFileException if there is an error loading the program
	 */
	void importFile(String filename) throws DuplicateTerminalIdException, UnrecognizedEntryException, 
	InvalidTerminalIdException, ClientNotFoundException, DuplicateClientIdException,
	IOException, ImportFileException {
		try (BufferedReader r = new BufferedReader(new FileReader(filename))) {
			String line;
			while((line = r.readLine()) != null) {
				String[] fields = line.split("\\|");
				registerEntryFields(fields);
			}		
		}	
    }


	/**
	 * Read a line from the file and decide which object to create.
	 *
     * @param fields
     * @throws DuplicateTerminalIdException if the terminal already exists
     * @throws UnrecognizedEntryException if the entry is not valid class
     * @throws InvalidTerminalIdException if the terminalId is not valid
     * @throws ClientNotFoundException if the client does not exist (when adding a terminal)
     * @throws DuplicateClientIdException if the client already exist
     */	
	public void registerEntryFields(String... fields) throws DuplicateTerminalIdException, UnrecognizedEntryException, 
	InvalidTerminalIdException, ClientNotFoundException, DuplicateClientIdException { 
		switch (fields[0]) {
			
			case "CLIENT" -> registerClient(fields[1], fields[2], fields[3]);
			case "BASIC", "FANCY" -> registerTerminal(fields[0], fields[1], fields[2], fields[3]);
			case "FRIENDS" -> registerTerminalFriends(fields[1], fields[2].split(","));
			default -> throw new UnrecognizedEntryException(fields[0]);
		}
	}


	/**
	 * Create a client.
	 *
     * @param clientId
	 * @param name
	 * @param fiscalId
     * @throws DuplicateClientIdException if the client already exists
     */	
	public void registerClient(String clientId, String name, String fiscalId) throws DuplicateClientIdException {
		if (_clients.get(clientId) != null) { throw new DuplicateClientIdException(clientId); }
		Client c = new Client(clientId, name, Integer.parseInt(fiscalId));
		_clients.put(c.getClientId(), c);
		setChanged(true);
	}


	/**
	 * Validate the terminal id.
	 *
     * @param terminalId
     */	
	private boolean validateTerminalId(String terminalId) {
			Pattern pattern = Pattern.compile("\\d{6}");
			Matcher matcher = pattern.matcher(terminalId);
			return matcher.matches();
	}
	

	/**
	 * Create a terminal.
	 *
     * @param type
	 * @param terminalId
	 * @param clientId
	 * @param state
	 * @throws InvalidTerminalIdException if the terminal id is invalid
	 * @throws DuplicateTerminalIdException if the terminal id already exist
	 * @throws UnrecognizedEntryException if the entry does not match with a type of a terminal
	 * @throws ClientNotFoundException if the client associated with the terminal does not exist
     */
	public void registerTerminal(String type, String terminalID, String clientID, String state) throws 
	InvalidTerminalIdException, DuplicateTerminalIdException, UnrecognizedEntryException, ClientNotFoundException { 
		Client client = findClient(clientID); 
		if (client == null) { throw new ClientNotFoundException(clientID); } 	
		if (validateTerminalId(terminalID) == false) { throw new InvalidTerminalIdException(terminalID); }
		if (_terminals.get(terminalID) != null) { throw new DuplicateTerminalIdException(terminalID); }
		if (state.equals("ON")) { state = "IDLE"; } 
		Terminal t = switch(type) {
			case "BASIC" -> new BasicTerminal(terminalID, client, state);
			case "FANCY" -> new FancyTerminal(terminalID, client, state);
			default -> throw new UnrecognizedEntryException(terminalID); 
		};
		client.addTerminal(t.getID(), t);
		_terminals.put(t.getID(), t);
		setChanged(true);
	}


	/**
	 * Register friends of a terminal.
	 * 
	 * @param originTerminal
	 * @param terminalIds
	 */
	public void registerTerminalFriends(String originID, String... terminalIds) {
		boolean occuredChange = false;
		Terminal originTerminal = _terminals.get(originID);
		if (_terminals.get(originID) == null) { return; }
		for (String friendID: terminalIds) { 
			Terminal friend = _terminals.get(friendID);
			if (friend != null) { 
				originTerminal.registerFriend(friendID, friend); 
				occuredChange = true;
			}
		}
		if (occuredChange == true) { setChanged(true); }
	} 


	public double getGlobalPayments() { return _globalPayments; }
	public double getGlobalDebts() { return _globalDebts; }
	

	public void updateDebts(double commCost) { _globalDebts += commCost; }
	public void informPaidComm(double price) {
		_globalPayments += price;	
		_globalDebts -= price;
	}

	/**
	 * @return network changed?
	 */
	public boolean networkChanged() { return _hasChanged; }

	public void addCommunication(Communication comm) { 
		_comms.add(comm); 
	}


	/**
	 * @return all terminals as an unmodifiable collection.
	 */
	public Collection<Terminal> selectAllTerminals() {
		return _terminals.values();
	}

	/**
	 * @return all clients as an unmodifiable collection
	 */
	public Collection<Client> selectAllClients() {
		return _clients.values();
	}
	
	/**
	 * 
	 * @param value
	 */
	public void setChanged(boolean value) { _hasChanged = value; }


	/**
	 * 
	 * @param clientId
	 * @return a client with the associated clientId
	 * @throws ClientNotFoundException if the client does not exist
	 */
	public Client findClient(String clientId) throws ClientNotFoundException {
		Client c = _clients.get(clientId);
		if (c == null) { throw new ClientNotFoundException(clientId); }
		return c;
	}

	public Collection<Notification> getClientNotifications(String clientId) throws ClientNotFoundException {
		Client c = findClient(clientId);
		return c.processNotifications();
	}

	/**
	 * 
	 * @param terminalId
	 * @return a terminal with the associated terminalId
	 * @throws TerminalNotFoundException if the terminal does not exist
	 */
	public Terminal findTerminal(String terminalId) throws TerminalNotFoundException {
		Terminal t = _terminals.get(terminalId);
		if (t == null) { throw new TerminalNotFoundException(terminalId); }
		return t;
	}

	
	public void disableClientNotifications(String clientId) throws ClientNotFoundException, ClientNotificationsAlreadyDisabledException {
		Client c = findClient(clientId);
		c.disableNotifications();
	}

	public void enableClientNotifications(String clientId) throws ClientNotFoundException, ClientNotificationsAlreadyEnabledException {
		Client c = findClient(clientId);
		c.enableNotifications();
	}

	public double fetchClientPayments(String clientKey) throws ClientNotFoundException {
		Client c = _clients.get(clientKey);
		if (c == null) { throw new ClientNotFoundException(clientKey); }
		return c.getPayments();
	}

	
	public double fetchClientDebts(String clientKey) throws ClientNotFoundException {
		Client c = _clients.get(clientKey);
		return c.getDebts();
	}

	public int communicationNumber() {
		return _communicationID++;
	}

	public Collection<Communication> showAllCommunications() {
		return _comms;
	}

	public Collection<Communication> fetchCommunicationsFromClient(String clientId) throws ClientNotFoundException {
		Client c = findClient(clientId);	
		return c.getSentComms();
	}

	public Collection<Communication> fetchCommunicationsToClient(String clientId) throws ClientNotFoundException {
		Client c = findClient(clientId);
		return c.getReceivedComms();
	}
}
