package prr.terminals;

import java.io.Serializable;
import prr.client.*;
import prr.exceptions.*;
import prr.notification.*;


public class IdleState extends TerminalState implements Serializable {
    public IdleState(Terminal t) { 
        super(t);
    }

    public void switchToIdle() throws SameTerminalStateException {
        throw new SameTerminalStateException(); 
    }

    public void switchToSilence() { _terminal.setState(new SilenceState(_terminal)); }

    public void switchToOff() { _terminal.setState(new OffState(_terminal)); }

    public void switchToBusy() { 
        _terminal.setState(new BusyState(_terminal, this)); 
    }

    public void communicationEnded() { /* does nothing here */ }

    public void emitAfterComm() { _terminal.sendNotifications("B2I"); }

    @Override
    public String toString() { return "IDLE"; }
}