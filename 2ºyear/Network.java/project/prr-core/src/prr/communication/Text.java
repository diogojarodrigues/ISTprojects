package prr.communication;

import prr.terminals.Terminal;

import prr.terminals.Terminal;

public class Text extends Communication {
    private String _message;
    private int _msgLength;
    
    public Text(int id, Terminal originTerminal, Terminal destinationTerminal, String message) {
        super(id, originTerminal, destinationTerminal);
        _message = message;
        _msgLength = message.length();
        setStatus("FINISHED");
    }

    public int getMsgLength() { return _msgLength; }

    public void commEnd(int units) { /* does nothing here */ }

    @Override
    public double calculateCost() { 
        double cost = getOriginTerminal().getClient().calculateTextCost(this); 
        setCost(cost);
        return cost;
    }


    @Override
    public String toString() {
        return "TEXT" + super.toString() + _msgLength + "|" + Math.round(getCost()) + "|" + getStatus();
    }

}
