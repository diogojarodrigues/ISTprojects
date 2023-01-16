package prr.communication;

import java.io.Serializable;
import prr.terminals.Terminal;

abstract public class Communication implements Serializable {
	private int _id;
    private Terminal _originTerminal;
    private Terminal _destinationTerminal; 
    private double _cost = 0;
    private String _status = "ONGOING";
    private boolean _isPaid = false;

    public Communication(int id, Terminal originTerminal, Terminal destinationTerminal) {
        _id = id;
        _originTerminal = originTerminal;
        _destinationTerminal = destinationTerminal;
    }

    public int getId() { return _id; }
    public Terminal getOriginTerminal() { return _originTerminal; }
    public Terminal getDestinationTerminal() { return _destinationTerminal; }
    public double getCost() { return _cost; }
    public String getStatus() { return _status; }
    public boolean isPaid() { return _isPaid; }

    public void setCost(double cost) { _cost = cost; }
    public void setStatus(String status) { _status = status; }
    public void setAsPaid() { _isPaid = true; }

    public boolean hasFinished() { return _status.equals("FINISHED"); }

    abstract public double calculateCost();

    public abstract void commEnd(int units);

    @Override
    public String toString() {
        return "|" + _id + "|" + _originTerminal.getID() + "|" + _destinationTerminal.getID() 
        + "|";
    }

}