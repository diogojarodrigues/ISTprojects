package prr.terminals;

import java.io.Serializable;
import prr.client.*;
import prr.exceptions.*;
import prr.notification.*;


public class BusyState extends TerminalState implements Serializable {
    private TerminalState _stateBeforeComm;

    public BusyState(Terminal t, TerminalState stateBeforeComm) { 
        super(t);
        _stateBeforeComm = stateBeforeComm;
    }

    public void switchToIdle() throws TerminalIsBusyException { 
        throw new TerminalIsBusyException(); }

    public void switchToSilence() throws TerminalIsBusyException { 
        throw new TerminalIsBusyException(); }

    public void switchToBusy() { /* should not happen */}

    public void switchToOff() throws TerminalIsBusyException { 
        throw new TerminalIsBusyException(); }

    @Override
    public boolean verifyInteractiveCommAvailability(String terminalID, Client client) throws DestinationIsBusyException {
        _terminal.addFailedCommunication(client);
        throw new DestinationIsBusyException(terminalID); }
    
    @Override
    public boolean canStartCommunication() { return false; }

    @Override
    public boolean canEndCurrentCommunication() { return _terminal.hasStartedComm(); } 

    @Override
    public void communicationEnded() { 
        _stateBeforeComm.emitAfterComm();
        _terminal.setState(_stateBeforeComm); 
    }

    @Override
    public String toString() { return "BUSY"; }
}