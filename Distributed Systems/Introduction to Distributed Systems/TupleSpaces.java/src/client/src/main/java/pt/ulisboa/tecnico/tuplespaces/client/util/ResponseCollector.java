package pt.ulisboa.tecnico.tuplespaces.client.util;

import java.util.ArrayList;

public class ResponseCollector {
    ArrayList<String> collectedResponses;
    ArrayList<String> collectedErros;
    Integer requestsCompleted;
    Integer requestsFailed;

    public ResponseCollector() {
        collectedResponses = new ArrayList<String>();
        collectedErros = new ArrayList<String>();
        requestsCompleted = 0;
        requestsFailed = 0;
    }

    synchronized public void addResponse(String s) {
        collectedResponses.add(s);
    }

    synchronized public void addError(String s) {
        collectedErros.add(s);
    }

    synchronized public void requestCompleted() {
        requestsCompleted++;
        notifyAll();
    }

    synchronized public void requestFailed() {
        requestsFailed++;
        notifyAll();
    }

    synchronized public ArrayList<String> getResponses() {
        return this.collectedResponses;
    }

    synchronized public ArrayList<String> getErrors() {
        return this.collectedErros;
    }

    synchronized public void waitUntilAllReceived(int n) {
        while (requestsCompleted + requestsFailed < n) { 
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("Error waiting for responses");
            }
        }

    }
}
