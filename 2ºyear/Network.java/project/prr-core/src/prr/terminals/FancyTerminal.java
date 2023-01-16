package prr.terminals;

import java.io.Serializable;
import prr.client.Client;

public class FancyTerminal extends Terminal implements Serializable{

	public FancyTerminal(String terminalId, Client client, String state) {
		super(terminalId, client, state);
	}

    public boolean supportsVideoComm() { return true; }

    @Override
    public String toString() {
        return "FANCY" + super.toString();
    }

}
