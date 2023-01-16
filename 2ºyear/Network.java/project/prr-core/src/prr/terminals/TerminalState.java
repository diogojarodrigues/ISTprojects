package prr.terminals;

import java.io.Serializable;

import prr.client.*;
import prr.exceptions.*;

public abstract class TerminalState implements Serializable {
    protected Terminal _terminal;

    public TerminalState(Terminal terminal) { 
        _terminal = terminal;
    }

    public abstract void switchToIdle() throws TerminalIsBusyException, SameTerminalStateException;
    public abstract void switchToSilence() throws TerminalIsBusyException, SameTerminalStateException;
    public abstract void switchToOff() throws TerminalIsBusyException, SameTerminalStateException;
    public abstract void switchToBusy();

    public boolean canStartCommunication() { return true; }
    public boolean canEndCurrentCommunication() { return false; }
    public boolean verifyTextCommAvailability(String terminalID, Client client) throws DestinationIsOffException { return true; }
    public boolean verifyInteractiveCommAvailability(String terminalID, Client client) throws DestinationIsBusyException, 
    DestinationIsOffException, DestinationIsSilentException { return true; }

    public void communicationStarted() { switchToBusy(); }

    public void emitAfterComm() { /* overriden in idle state */ }

    public abstract void communicationEnded();
}