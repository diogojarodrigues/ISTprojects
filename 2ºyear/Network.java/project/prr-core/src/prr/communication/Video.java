package prr.communication;

import prr.terminals.Terminal;

public class Video extends Communication {
    private int _duration = 0;

    public Video(int id, Terminal originTerminal, Terminal destinationTerminal) {
        super(id, originTerminal, destinationTerminal);
    }

    public int getDuration() { return _duration; }

    public void commEnd(int duration) { 
        _duration = duration;
        setStatus("FINISHED");
    }

    @Override
    public double calculateCost() { 
        double cost = getOriginTerminal().getClient().calculateVideoCost(this); 
        setCost(cost);
        return cost;
    }

    @Override
    public String toString() {
        return "VIDEO" + super.toString() + _duration + "|" + Math.round(getCost()) + "|" + getStatus();
    }
}
