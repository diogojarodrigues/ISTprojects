package prr.terminals;

import java.io.Serializable;
import java.lang.Math;
import java.util.List;
import java.util.LinkedList;
import java.util.ArrayList;
import java.util.Map;

import java.util.HashMap;

import prr.Network;
import prr.exceptions.*;
import prr.client.Client;
import prr.communication.*;
import prr.notification.Notification;

import java.util.Collections;

/**
 * Abstract terminal.
 */
abstract public class Terminal implements Serializable {

/** Serial number for serialization. */
private static final long serialVersionUID = 202208091753L;

    private TerminalState _state;
    private String _terminalID;
    private Client _client;
    private double _payments = 0;
    private double _debts = 0;
    private boolean _isActive = false;
    private boolean _originatedComm = false;
    private Communication _currentComm;
    private List<Client> _failedComms = new LinkedList<>();
    private Map<String, Terminal> _friends = new HashMap<String, Terminal>();

    abstract public boolean supportsVideoComm();
    
    public Terminal(String terminalID, Client client, String state) {
        _terminalID = terminalID;
        _client = client;
        stateCreate(state);
    }
    
    public void stateCreate(String state) {
        switch (state) {
            case "SILENCE" -> _state = new SilenceState(this);
            case "OFF" -> _state = new OffState(this);
            default -> _state = new IdleState(this); // do ficheiro apenas vêm "SILENCE", "OFF" e "IDLE" logo default é "IDLE"
        };
    }
    
    public String getID() { return _terminalID; }
    public Client getClient() { return _client; }
    public double getPayments() { return _payments; }
    public double getDebts() { return _debts; }
    public double getBalance() { return _payments - _debts; }
    //public List<Client> getFailedComms() { return _failedComms; }  
    public boolean hasStartedComm() { return _originatedComm; }  
    public boolean hasActivity() { return _isActive; } 
    public boolean isFriend(String terminalID) { return _friends.get(terminalID) != null; }

    public void setState(TerminalState state) { _state = state; } 
    public void setAsActive() { _isActive = true; }

    public void turnOff(Network network) throws TerminalIsBusyException, SameTerminalStateException { 
        _state.switchToOff(); 
        network.setChanged(true);
    } 
    public void turnOn(Network network) throws TerminalIsBusyException, SameTerminalStateException { 
        _state.switchToIdle(); 
        network.setChanged(true);
    }
    public void turnSilent(Network network) throws TerminalIsBusyException, SameTerminalStateException { 
        _state.switchToSilence();
        network.setChanged(true);
    }

    public void addFailedCommunication(Client client) { _failedComms.add(client); }
    public void clearFailedComms() { _failedComms.clear(); }

    public void sendNotifications(String nature) {
        for (Client c: _failedComms) {
            if (c.getNotificationsOn())
                c.addNotification(new Notification(_terminalID, nature));
        }
        clearFailedComms();
    }

    public void startCommunication(Communication comm) {  // coloca o estado a busy e guarda a comunicação
        _state.communicationStarted(); 
        _currentComm = comm;
    }

    public void storeCommunication(Terminal destinationTerminal, Network network) { // guarda a comunicação nas classes necessárias
        _client.addSentCommunication(_currentComm);
        destinationTerminal.getClient().addReceivedCommunication(_currentComm);
        network.addCommunication(_currentComm);
    }

    public void updateDebts(double commCost, Network network) { // atualiza as dívidas do terminal, do cliente e da network
        _debts += commCost;
        _client.informEndComm(commCost);
        network.updateDebts(commCost);
    }

    public void exitCommunication() { // quando acaba uma comunicação interativa, repõe variáveis e atualiza o estado para o anterior 
        _currentComm = null;
        _originatedComm = false;
        _state.communicationEnded();
    }


    public void registerFriend(String friendID, Terminal friend) {
        _friends.put(friendID, friend);
    }

    public void addFriend(String friendID, Network network) throws CannotBefriendSelfException, FriendAlreadyAddedException, 
    TerminalNotFoundException {
        if (friendID.equals(_terminalID)) { throw new CannotBefriendSelfException(); }
        Terminal t = network.findTerminal(friendID);
        if (_friends.get(friendID) != null) { throw new FriendAlreadyAddedException(); }
        _friends.put(friendID, t);
        network.setChanged(true);
    }

    public void removeFriend(String friendID, Network network) throws TerminalNotFoundException, FriendNotFoundException {
        Terminal t = network.findTerminal(friendID);
        if (_friends.remove(friendID, t) == false)  { throw new FriendNotFoundException(); }
        network.setChanged(true);
    }

    public void sendTextCommunication(String destinationTerminalID, String message, Network network) 
    throws DestinationIsOffException, TerminalNotFoundException {
        Terminal destinationTerminal = network.findTerminal(destinationTerminalID);
        destinationTerminal.verifyTextCommAvailability(destinationTerminalID, _client); // se estiver off, lança exceção
        this._isActive = true;
        destinationTerminal.setAsActive();
        Text comm = new Text(network.communicationNumber(), this, destinationTerminal, message);
        double cost = comm.calculateCost();
        this.updateDebts(cost, network);
        _client.addSentCommunication(comm);
        destinationTerminal.getClient().addReceivedCommunication(comm);
        network.addCommunication(comm);
        network.setChanged(true);
    }

    public Communication createVideoComm(Terminal destinationTerminal, String commType, Network network) 
    throws TerminalNotFoundException, DestinationIsOffException, DestinationIsSilentException, DestinationIsBusyException, 
    UnsupportedAtOriginException, UnsupportedAtDestinationException {
        Communication comm;
        if (!this.supportsVideoComm())  { throw new UnsupportedAtOriginException(_terminalID, commType); }
        if (!destinationTerminal.supportsVideoComm()) { throw new UnsupportedAtDestinationException(destinationTerminal.getID(), commType); }
        destinationTerminal.verifyInteractiveCommAvailability(destinationTerminal.getID(), _client);
        if (destinationTerminal.getID().equals(_terminalID)) { throw new DestinationIsBusyException(destinationTerminal.getID()); }
        comm = new Video(network.communicationNumber(), this, destinationTerminal);
        return comm;
    }

    public Communication createVoiceComm(Terminal destinationTerminal, String commType, Network network) 
    throws TerminalNotFoundException, DestinationIsOffException, DestinationIsSilentException, DestinationIsBusyException, 
    UnsupportedAtOriginException, UnsupportedAtDestinationException { 
        Communication comm;
        destinationTerminal.verifyInteractiveCommAvailability(destinationTerminal.getID(), _client);
        if ((destinationTerminal.getID()).equals(_terminalID)) { throw new DestinationIsBusyException(destinationTerminal.getID()); }
        comm = new Voice(network.communicationNumber(), this, destinationTerminal);
        return comm;
    }

    public void startInteractiveCommunication(String destinationTerminalID, String commType, Network network)
    throws DestinationIsOffException, DestinationIsSilentException, DestinationIsBusyException, TerminalNotFoundException,
    UnrecognizedEntryException, UnsupportedAtOriginException, UnsupportedAtDestinationException {
        Terminal destinationTerminal = network.findTerminal(destinationTerminalID);
        Communication comm;
        switch (commType) { 
            case "VIDEO" -> comm = createVideoComm(destinationTerminal, commType, network);
            case "VOICE" -> comm = createVoiceComm(destinationTerminal, commType, network);
            default -> throw new UnrecognizedEntryException(commType);
        };
        this.setAsActive();
        destinationTerminal.setAsActive();
        this._originatedComm = true;
        this.startCommunication(comm);
        destinationTerminal.startCommunication(comm);
        this.storeCommunication(destinationTerminal, network);
        network.setChanged(true);
    }

    public double endInteractiveCommunication(int duration, Network network) {
        Terminal destinationTerminal = _currentComm.getDestinationTerminal();
        _currentComm.commEnd(duration);
        double cost = _currentComm.calculateCost();
        this.updateDebts(cost, network);
        this.exitCommunication();
        destinationTerminal.exitCommunication();
        network.setChanged(true);
        return cost;
    }

    public Communication fetchOngoingCommunication() throws NoOngoingCommunicationException {   
        if (_currentComm == null) { throw new NoOngoingCommunicationException(); }
        return _currentComm;
    }


    public void performPayment(int commKey, Network network) throws InvalidCommunicationException {
        double price = _client.payCommunication(commKey, _terminalID);
        _payments += price;
        _debts -= price;
        network.informPaidComm(price);
        network.setChanged(true);
    }


    /**
     * Checks if this terminal can end the current interactive communication.
     *
     * @return true if this terminal is busy (i.e., it has an active interactive communication) and
     *      it was the originator of this communication.
     **/
    public boolean canEndCurrentCommunication() { return _state.canEndCurrentCommunication(); }

    /**
     * Checks if this terminal can start a new communication.
     *
     * @return true if this terminal is neither off neither busy, false otherwise.
     **/
    public boolean canStartCommunication() { return _state.canStartCommunication(); }


    public boolean verifyInteractiveCommAvailability(String terminalID, Client client) throws DestinationIsBusyException, DestinationIsOffException, 
    DestinationIsSilentException { return _state.verifyInteractiveCommAvailability(terminalID, client); }

    public boolean verifyTextCommAvailability(String terminalID, Client client) throws DestinationIsOffException { 
        return _state.verifyTextCommAvailability(terminalID, client); }

    public String sortFriendList() { 
        if (_friends.size() != 0) {
            List<String> friendList = new ArrayList<>(_friends.keySet());
            Collections.sort(friendList);
            return "|" + String.join(",", friendList);
        }
        return "";
    }

    @Override
    public String toString() {
        return "|" + _terminalID + "|" + _client.getClientId() + "|" + _state.toString() + "|" 
        + Math.round(_payments) + "|" + Math.round(_debts) + sortFriendList(); 
    }
}
