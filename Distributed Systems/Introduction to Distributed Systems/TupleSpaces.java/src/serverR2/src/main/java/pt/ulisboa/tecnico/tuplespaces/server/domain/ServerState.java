package pt.ulisboa.tecnico.tuplespaces.server.domain;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import pt.ulisboa.tecnico.tuplespaces.server.Tuple;

import java.util.stream.Collectors;

public class ServerState {
	ReadWriteLock lock = new ReentrantReadWriteLock();
	private final Lock readlock = lock.readLock();
	private final Lock writelock = lock.writeLock();
	private Map<String, Condition> waitingConditions = new HashMap<String,Condition>();
	private boolean debug;
	private List<Tuple> tuples;

	public ServerState(boolean debug) {
		this.tuples = new ArrayList<Tuple>();
		this.debug = debug;
	}

	/**
	 * Locks all tuples that match
	 * @param pattern
	 * @param id the id from the client that is doing the request
	 * @return a list of all tuples that were locked, null if the lock was not possible
	 */
	private List<String> lockMatchingTuples(String pattern, Integer id) {
		boolean failedLock = false;
		List<String> blockedTuples = new ArrayList<>();
		for (Tuple tuple : this.tuples) {
			if (tuple.getTuple().matches(pattern)) {
				if (tuple.lock(id))
					blockedTuples.add(tuple.getTuple());
				else
					failedLock = true;
			}
		}
		if (blockedTuples.isEmpty() && failedLock)
			return null;
		return blockedTuples;
	}
	/**
	 * Unlocks all tuples that were locked previously by a client
	 * @param id the clients id
	 */
	private void unlockMatchingTuples(Integer id) {
		for (Tuple tuple : this.tuples) {
			if (tuple.getId() == id)
				tuple.unlock(id);
		}
	}
	/**
	 * Returns all tuples that match for read operations
	 * @param pattern
	 * @return a list of all tuples that match the pattern
	 */
	private List<String> getAllMatchingTuples(String pattern) {
		List<String> matchingTuples = new ArrayList<>();
		for (Tuple tuple : this.tuples) {
			if (tuple.getTuple().matches(pattern))
				matchingTuples.add(tuple.getTuple());
			}
		return matchingTuples;
	}
	/**
	 * Waits for a tuple to be available
	 * @param tuple
	 * @throws InterruptedException
	 */
	private void waitForTuple(String tuple) throws InterruptedException{
		if (waitingConditions.containsKey(tuple)) {
			waitingConditions.get(tuple).await();
			return;
		}
		Condition newC = this.writelock.newCondition();
		waitingConditions.put(tuple, newC);
		newC.await();
		return; 
	}

	// API FUNCTIONS--------------------------------------------------------------

	/**
	 * Add a tuple to the tuple space
	 * @param tuple
	 * @return
	 */
	public int put(String tuple) {
		if (debug) System.err.println("ServerState.put: " + tuple);

		// Check if the tuple is correctly formated
		if (!Tuple.isTupleCorrectlyFormated(tuple)) {
			return -1;
		}

		this.writelock.lock();
		try {
			// Add the tuple to the tuple space
			tuples.add(new Tuple(tuple));

			// Notify the threads that wait for this pattern
			for (String pattern : waitingConditions.keySet()) {
				if (tuple.matches(pattern)) {
					if (debug) System.err.println("ServerState.put: Notifying threads that are waiting for: "+ pattern);
					Condition c = waitingConditions.get(pattern);
					c.signalAll();
					waitingConditions.remove(pattern);
				}
			}
		}
		catch (ConcurrentModificationException e) {
			if (debug) System.err.println("ServerState.put: ConcurrentModificationException for: "+ tuple);
		}
		finally {
			this.writelock.unlock();
		}

		if (debug) System.err.println("ServerState.put: Tupple added to tupple space" + tuple);
		return 0;
	}

	/**
	 * Read a tuple from the tuple space. Waits if the tuple is not available
	 * @param tuple
	 * @return
	 */
	public String read(String tuple) {
		if (debug) System.err.println("ServerState.read: " + tuple);

		// Check if the tuple is correctly formated
		if (!Tuple.isTupleCorrectlyFormated(tuple)) {
			return null;
		}

		this.readlock.lock();
		// Wait for the tuple to be available
		List<String> matchingTuples;
		while ((matchingTuples = getAllMatchingTuples(tuple)).isEmpty()) {
			if (debug) System.err.println("ServerState.read: Trying to find requested tuple to be read: "+ tuple);

			try {
				this.readlock.unlock();
				this.writelock.lock();
				this.waitForTuple(tuple);
			} 
			catch( InterruptedException e) {
				if (debug) System.err.println("ServerState.read: Interrupted Exception for: "+ tuple);
			}
			finally {
				this.writelock.unlock();
				this.readlock.lock();
			}
		}

		this.readlock.unlock();
		if (debug) System.err.println("ServerState.read: Found requested tuple to read: "+ tuple);
		return String.join("\n", matchingTuples);
	}

	/**
	 * Lock tuples from the tuple space. Waits if no tuple is available
	 * @param tuple
	 * @param id
	 * @return
	 */
	public String takePhase1(String tuple, Integer id) {
		if (debug) System.err.println("ServerState.takePhase1: " + tuple + " " + id);

		// Check if the tuple is correctly formated
		if (!Tuple.isTupleCorrectlyFormated(tuple)) {
			return null;
		}

		this.writelock.lock();

		// Wait for the tuple to be available
		List<String> matchingTuples;
		while ((matchingTuples = lockMatchingTuples(tuple, id)) != null && matchingTuples.isEmpty()) {
			try {
				if (debug) System.err.println("ServerState.takePhase1: waiting for a tuple. sleeping... "+ tuple);
				this.waitForTuple(tuple);
				if (debug) System.err.println("ServerState.takePhase1: awake "+ tuple);
			} 
			catch( InterruptedException e) {
				if (debug) System.err.println("ServerState.takePhase1: Interrupted Exception for: "+ tuple);
			}
		}

		this.writelock.unlock();
		
		if (matchingTuples == null) {
			if (debug) System.err.println("ServerState.takePhase1: returning null "+ tuple);
			return "NACK";
		}

		if (debug) System.err.println("ServerState.takePhase1: returning tuples "+ tuple);
		return String.join("\n", matchingTuples);
	}

	/**
	 * Release the lock on previously locked tuples by the client
	 * @param id
	 */
	public void takePhase1Release(Integer id) {
		this.writelock.lock();
		unlockMatchingTuples(id);
		this.writelock.unlock();
	}

	/**
	 * Removes the tuple from the tuple space
	 * @param chosenTuple
	 * @param id
	 * @return
	 */
	public String takePhase2(String chosenTuple, Integer id) {
		if (debug) System.err.println("ServerState.takePhase2: " + chosenTuple + " " + id);

		this.writelock.lock();
		for (Tuple tuple : this.tuples) {
			if (tuple.getTuple().matches(chosenTuple) && tuple.getId() == id) {
				try {
					if (debug) System.err.println("ServerState.takePhase2: removing tuple " + chosenTuple);
					return tuple.getTuple();
				}
				catch (ConcurrentModificationException e) {
					if (debug) System.err.println("ServerState.takePhase2: ConcurrentModificationException for: "+ chosenTuple);
				}
				finally {
					this.tuples.remove(tuple);
					unlockMatchingTuples(id);
					this.writelock.unlock();
				}
			}
		}
		this.writelock.unlock();

		if (debug) System.err.println("ServerState.takePhase2: returning null " + chosenTuple);
		return null;
	}

	public String getTupleSpacesState() {
		if (debug) System.err.println("ServerState.getTupleSpacesState: Returning all tuples");
		this.readlock.lock();
		String output = this.tuples.stream().map(tuple -> tuple.toString()).collect(Collectors.joining(", "));
		this.readlock.unlock();
		return output;
	}
}	