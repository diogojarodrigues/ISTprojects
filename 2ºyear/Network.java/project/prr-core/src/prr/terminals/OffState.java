package prr.terminals;

import java.io.Serializable;

import javax.management.Notification;
import prr.client.*;
import prr.exceptions.*;
import prr.notification.*;


public class OffState extends TerminalState implements Serializable {
    public OffState(Terminal t) { 
        super(t);
    }

    public void switchToIdle() { 
        _terminal.sendNotifications("O2I");
        _terminal.setState(new IdleState(_terminal)); 
    }

    public void switchToSilence() { 
        _terminal.sendNotifications("O2S");
        _terminal.setState(new SilenceState(_terminal)); 
    }

    public void switchToBusy() { /* should not happen */ }

    public void switchToOff() throws SameTerminalStateException { 
        throw new SameTerminalStateException(); 
    }
    
    @Override
    public boolean verifyTextCommAvailability(String terminalID, Client client) throws DestinationIsOffException {
        _terminal.addFailedCommunication(client);
        throw new DestinationIsOffException(terminalID); 
    }

    @Override
    public boolean verifyInteractiveCommAvailability(String terminalID, Client client) throws DestinationIsOffException{ 
        _terminal.addFailedCommunication(client);
        throw new DestinationIsOffException(terminalID); 
    }
    
    public void communicationEnded() { /* does nothing here */ }
    
    @Override
    public boolean canStartCommunication() { return false; }
    
    @Override
    public String toString() { return "OFF"; }
}