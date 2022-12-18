package prr.terminals;

import java.io.Serializable;

import prr.client.*;
import prr.exceptions.*;
import prr.notification.*;

public class SilenceState extends TerminalState implements Serializable {
    public SilenceState(Terminal t) { 
        super(t);
    }

    public void switchToIdle() { 
        _terminal.sendNotifications("S2I");
        _terminal.setState(new IdleState(_terminal)); 
    }

    public void switchToSilence() throws SameTerminalStateException { 
        throw new SameTerminalStateException(); }

    public void switchToOff() { _terminal.setState(new OffState(_terminal)); }

    public void switchToBusy() { 
        _terminal.setState(new BusyState(_terminal, this));
    }

    @Override
    public boolean verifyInteractiveCommAvailability(String terminalID, Client client) throws DestinationIsSilentException { 
        _terminal.addFailedCommunication(client);
        throw new DestinationIsSilentException(terminalID); 
    }

    public void communicationEnded() { /* does nothing here */ }


    @Override
    public String toString() { return "SILENCE"; }
}