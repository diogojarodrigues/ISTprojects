package prr.communication;

import prr.terminals.Terminal;

public class Voice extends Communication {
    private int _duration = 0;

    public Voice(int id, Terminal originTerminal, Terminal destinationTerminal) {
        super(id, originTerminal, destinationTerminal);
    }

    public int getDuration() { return _duration; }

    public void commEnd(int duration) { 
        _duration = duration;
        setStatus("FINISHED");
    }

    @Override
    public double calculateCost() { 
        double cost = getOriginTerminal().getClient().calculateVoiceCost(this); 
        setCost(cost);
        return cost;
    }

    @Override
    public String toString() {
        return "VOICE" + super.toString() + _duration + "|" + Math.round(getCost()) + "|" + getStatus();
    }
}
