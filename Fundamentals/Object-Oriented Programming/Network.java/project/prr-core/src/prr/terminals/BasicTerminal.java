package prr.terminals;

import java.io.Serializable;
import prr.client.Client;

public class BasicTerminal extends Terminal implements Serializable{

	public BasicTerminal(String terminalId, Client client, String state) {
        super(terminalId, client, state);
    }

    public boolean supportsVideoComm() { return false; }    

    @Override
    public String toString() {
        return "BASIC" + super.toString();
    }
}
