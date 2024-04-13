package pt.ulisboa.tecnico.tuplespaces.server;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;


public class Task {

    public enum Type {
        PUT, TAKE
    }

    private final Type type;
    private final Integer seqNumber;
    private final String pattern;
    Condition condition;

    public Task(Type type, Integer seqNumber, String pattern) {
        this.type = type;
        this.seqNumber = seqNumber;
        this.pattern = pattern;
    }

    public void sleep (Lock lock) throws InterruptedException{
        this.condition = lock.newCondition();
        this.condition.await();
    }

    public void awake() {
        this.condition.signal();
    }

    public Integer getSeqNumber() {
        return this.seqNumber;
    }

    public Type getType() {
        return this.type;
    }

    public String getPattern() {
        return this.pattern;
    }
}
