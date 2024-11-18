package pt.ulisboa.tecnico.tuplespaces.server.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import pt.ulisboa.tecnico.tuplespaces.server.Task;
import pt.ulisboa.tecnico.tuplespaces.server.Task.Type;

public class ServerState {

	// auxiliary constants
	private final static String TUPLE_BGN = "<";
    private final static String TUPLE_END = ">";
    private final static String TUPLE_SPACE = " ";

	// Debug flag
	private boolean debug;

	// Locks
	ReadWriteLock lock = new ReentrantReadWriteLock();
	private final Lock readlock = lock.readLock();
	private final Lock writelock = lock.writeLock();
	private final Condition readsWaiting = writelock.newCondition();

	// Tuple space
	private ArrayList<String> tuples;
	private TreeMap<Integer, Task> tasks = new TreeMap<>();
	private int taskCounter = 1;
	private TreeMap<Integer, Task> waitingTakes = new TreeMap<>();


	public ServerState(boolean debug) {
		this.tuples = new ArrayList<String>();
		this.debug = debug;
	}


	// AUXILIARY FUNCTIONS--------------------------------------------------------

	/**
     * Check if the tuple is correctly formated to be in the tuple space
     * @param tuple
     * @return true if the tuple is correctly formated, false otherwise
     */
    private boolean isTupleCorrectlyFormated(String tuple) {

		if (tuple == null || tuple.isEmpty()) {     // Check if the tuple is empty
        	return false;
        }
    
        if (!tuple.startsWith(TUPLE_BGN) || !tuple.endsWith(TUPLE_END)) { // Check if the tuple starts and ends with < and >
          return false;
        }
    
        if (tuple.contains(TUPLE_SPACE)) {
          return false;
        }
    
        return true;
    }
	/**
	 * Returns all tuples that match for read operations
	 * @param pattern
	 * @return a list of all tuples that match the pattern
	 */
	private List<String> getAllMatchingTuples(String pattern) {
		List<String> matchingTuples = new ArrayList<>();
		for (String tuple : this.tuples) {
			if (tuple.matches(pattern))
				matchingTuples.add(tuple);
			}
		return matchingTuples;
	}
	/**
	 * Returns the first tuple that matches the pattern
	 * @param pattern
	 * @return
	 */
	private String getMatchingTuple(String pattern) {
		for (String tuple : this.tuples) {
			if (tuple.matches(pattern))
				return tuple;
			}
		return null;
	}
	/**
	 * Returns the first waiting take task that matches the pattern
	 * @param pattern
	 * @return
	 */
	private Task getFirstMatchingTuple(String pattern) {
		for (Map.Entry<Integer, Task> entry : this.waitingTakes.entrySet()) {
			if (entry.getValue().getPattern().matches(pattern))
				return entry.getValue();
			}
		return null;
	}

	// API FUNCTIONS--------------------------------------------------------------

	/**
	 * Add a tuple to the tuple space
	 * @param tuple
	 * @return
	 */
	public int put(String tuple, int seqNumber) {
		if (debug) System.err.println("ServerState.put: " + tuple + " " + seqNumber);

		// Check if the tuple is correctly formated
		if (!isTupleCorrectlyFormated(tuple)) {
			if (debug) System.err.println("ServerState.put: The tuple has incorrect format" + tuple);
			return -1;
		}

		if (seqNumber <= 0) {
			if (debug) System.err.println("ServerState.put: The sequence number is not valid for " + tuple);
			return -1;
		}

		Task task = new Task(Type.PUT, seqNumber, tuple);
		writelock.lock();

		if (this.taskCounter != seqNumber) {
			if (debug) System.err.println("ServerState.put: Not the turn for this task, sleeping..." + tuple);
			if (debug) System.err.println("ServerState.put: expected -> " + this.taskCounter + ", received -> " + seqNumber);
			this.tasks.put(seqNumber, task);
			while (this.taskCounter != seqNumber) { // it is recommended to use a while loop to avoid spurious wakeups
				try {
					task.sleep(writelock);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		if (debug) System.err.println("ServerState.put: executing task " + seqNumber);

		// if this task is in the tasks list, remove it
		if (this.tasks.containsKey(seqNumber)) {
			this.tasks.remove(seqNumber);
		}

		this.tuples.add(tuple);
		if (debug) System.err.println("ServerState.put: Added tuple to Tuplespace" + tuple);

		// let the readers know that there is a new tuple
		this.readsWaiting.signalAll();

		// update the task counter
		this.taskCounter++;

		// if a take is waiting for this tuple, signal it and return
		// only awake that first take and only if the pattern matches the tuple
		Task waitingTake;
		if ((waitingTake = getFirstMatchingTuple(tuple)) != null){
			waitingTake.awake();
			if (debug) System.err.println("ServerState.put: Waking up waiting take: " + waitingTake.getSeqNumber());

			// remove the task from the waitingTakes list
			this.waitingTakes.remove(waitingTake.getSeqNumber());

			// the awoken take task will awake the next task on the counter
			writelock.unlock();
			return 0;
		}

		// if next task is waiting, signal it
		if (this.tasks.containsKey(this.taskCounter)) {
			this.tasks.get(this.taskCounter).awake();
			if (debug) System.err.println("ServerState.put: Waking up next task: " + this.taskCounter);
		}

		writelock.unlock();
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
		if (!isTupleCorrectlyFormated(tuple)) {
			if (debug) System.err.println("ServerState.read: The tuple has incorrect format" + tuple);
			return null;
		}

		this.readlock.lock();

		// if there is no matching tuple, wait for one
		List<String> matchingTuples;
		while ((matchingTuples = getAllMatchingTuples(tuple)).isEmpty()) {
			this.readlock.unlock();
			try {
				this.writelock.lock(); // we have to switch to write lock to wait as readlock does not support conditions
				this.readsWaiting.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			finally {
				this.writelock.unlock(); // switch back to read lock
				this.readlock.lock();
			}
		}

		this.readlock.unlock();
		return String.join("\n", matchingTuples);
	}

	/**
	 * Take a tuple from the tuple space. Waits if the tuple is not available
	 * @param tuple
	 * @return
	 */
	public String take(String tuple, int seqNumber) {
		if (debug) System.err.println("ServerState.take: " + tuple + " " + seqNumber);

		// Check if the tuple is correctly formated
		if (!isTupleCorrectlyFormated(tuple)) {
			return null;
		}

		if (seqNumber <= 0) {
			if (debug) System.err.println("ServerState.put: The sequence number is not valid for " + tuple);
			return null;
		}

		Task task = new Task(Type.TAKE, seqNumber, tuple);
		writelock.lock();

		if (this.taskCounter != seqNumber) {
			if (debug) System.err.println("ServerState.take: Not the turn for this task, sleeping..." + tuple);
			if (debug) System.err.println("ServerState.put: expected -> " + this.taskCounter + ", received -> " + seqNumber);
			this.tasks.put(seqNumber, task);
			while (this.taskCounter != seqNumber) { // it is recommended to use a while loop to avoid spurious wakeups
				try {
					task.sleep(writelock);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		if (debug) System.err.println("ServerState.take: executing task " + seqNumber);

		// if this task is in the tasks list, remove it
		if (this.tasks.containsKey(seqNumber)) {
			this.tasks.remove(seqNumber);
		}

		String matchingTuple;
		// if there is no matching tuple, wait for one and give a chance to next task
		if ((matchingTuple = getMatchingTuple(tuple)) == null) {
			if (debug) System.err.println("ServerState.take: Could not find requested tuple to be taken. Sleeping... "+ tuple);

			// update the task counter
			this.taskCounter++;

			// if next task is waiting, signal it
			if (this.tasks.containsKey(this.taskCounter)) {
				this.tasks.get(this.taskCounter).awake();
				if (debug) System.err.println("ServerState.take: Waking up next task: " + this.taskCounter);
			}

			// go to sleep
			this.waitingTakes.put(seqNumber, task);
			while ((matchingTuple = getMatchingTuple(tuple)) == null) { // it is recommended to use a while loop to avoid spurious wakeups
				try {
					task.sleep(writelock);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			this.waitingTakes.remove(seqNumber);

			// some put task added a tuple while we were sleeping and signaled us
			if (debug) System.err.println("ServerState.take: executing task " + seqNumber);

			this.tuples.remove(matchingTuple);
			if (debug) System.err.println("ServerState.put: Removed tuple from Tuplespace" + matchingTuple);

			// here the taskCounter is already updated by a put operation
		}
		// otherwise, just remove the tuple
		else {
			this.tuples.remove(matchingTuple);
			if (debug) System.err.println("ServerState.put: Removed tuple from Tuplespace" + matchingTuple);

			this.taskCounter++;
		}

		// if next task is waiting, signal it
		if (this.tasks.containsKey(this.taskCounter)) {
			this.tasks.get(this.taskCounter).awake();
			if (debug) System.err.println("ServerState.take: Waking up next task: " + this.taskCounter);
		}

		writelock.unlock();
		return matchingTuple;
	}

	/**
	 * Returns all tuples in tuple space
	 * @param pattern
	 * @return
	 */
	public String getTupleSpacesState() {
		if (debug) System.err.println("ServerState.getTupleSpacesState: Returning all tuples");
		this.readlock.lock();
		String output = this.tuples.toString();
		this.readlock.unlock();
		return output;
	}
}	