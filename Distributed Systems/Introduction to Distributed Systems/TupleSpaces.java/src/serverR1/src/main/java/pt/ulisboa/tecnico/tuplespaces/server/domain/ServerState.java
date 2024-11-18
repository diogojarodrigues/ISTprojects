package pt.ulisboa.tecnico.tuplespaces.server.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ServerState {

  private final static String TUPLE_DIVISOR = ",";
  private final static String TUPLE_BGN = "<";
  private final static String TUPLE_END = ">";
  private final static String TUPLE_SPACE = " ";
  private final static String IS_APLHANUMERIC = "[a-zA-Z0-9]+";
  private boolean debug;
  private List<String> tuples;

  public ServerState(boolean debug) {
    this.tuples = new ArrayList<String>();
    this.debug = debug;
  }

  // AUXILIARY FUNCTIONS
  private static boolean isElementCorrectlyFormated(String element, boolean isRegex) {

    if (isRegex) {
      try {
        Pattern.compile(element);
        return true;
      } catch (Exception e) {
        // Do nothing
      }
    }

    return element.matches(IS_APLHANUMERIC);
  }

  private boolean isTupleCorrectlyFormated(String tuple, boolean isRegex) {

    if (tuple == null || tuple.isEmpty()) {
      return false;
    }

    if (
        !tuple.startsWith(TUPLE_BGN) || !tuple.endsWith(TUPLE_END)) {
      return false;
    }

    // Remove the first and last character <a,b,c> -> a,b,c
    tuple = tuple.substring(1, tuple.length() - 1);

    if (tuple.isEmpty() || tuple.startsWith(TUPLE_DIVISOR) || tuple.endsWith(TUPLE_DIVISOR)) {
      return false;
    }

    if (tuple.contains(TUPLE_DIVISOR + TUPLE_DIVISOR) || tuple.contains(TUPLE_BGN) || tuple.contains(TUPLE_END) || tuple.contains(TUPLE_SPACE)) {
      return false;
    }

    String[] tupleParts = tuple.split(TUPLE_DIVISOR);

    if (debug) System.err.println("ServerState.isTupleCorrectlyFormated: ");
    for (String part : tupleParts) {
      if (debug) System.err.println(part);
      if (!isElementCorrectlyFormated(part, isRegex))
        return false;
    }

    return true;
  }

  private String getMatchingTuple(String pattern) {
    for (String tuple : this.tuples) {
      if (tuple.matches(pattern)) {
        return tuple;
      }
    }
    return null;
  }

  private List<String> getAllMatchingTuples(String pattern) {
    List<String> matchingTuples = new ArrayList<>();
    for (String tuple : this.tuples) {
      if (tuple.matches(pattern)) {
        matchingTuples.add(tuple);
      }
    }
    return matchingTuples;
  }

  // API FUNCTIONS
  public synchronized int put(String tuple) {
    if (debug) System.err.println("ServerState.put: " + tuple);

    // Check if the tuple is correctly formated
    if (!isTupleCorrectlyFormated(tuple, false)) {
      return -1;
    }

    tuples.add(tuple);
    notifyAll();
    if (debug) System.err.println("ServerState.put: Tupple added to tupple space" + tuple);
    return 0;
  }

  public synchronized String read(String pattern) {
    if (!isTupleCorrectlyFormated(pattern, true)) {
      return null;
    }

    List<String> matchingTuples;
    while ((matchingTuples = getAllMatchingTuples(pattern)).isEmpty()) {
      //wait
      try {
        if (debug) System.err.println("ServerState.read: Trying to find requested tuple to be read: "+ pattern);
        wait();
      } catch( InterruptedException e) {
      }
    }
    if (debug) System.err.println("ServerState.read: Found requested tuple to read: "+ pattern);

    return String.join("\n", matchingTuples);
  }

  public synchronized String take(String pattern) {
    if (!isTupleCorrectlyFormated(pattern, true)) {
      return null;
    }

    String matchingTuple;
    while ((matchingTuple = getMatchingTuple(pattern)) == null) {
      //wait
      try {
        if (debug) System.err.println("ServerState.take: Trying to find requested tuple to be taken: "+ pattern);
        wait();
      } 
      catch( InterruptedException e) {
        //do nothing
      }
    }
    this.tuples.remove(matchingTuple);
    if (debug) System.err.println("ServerState.take: Found requested tuple to take: "+ pattern);

    return matchingTuple;
  }

  public synchronized String getTupleSpacesState() {
      if (debug) System.err.println("ServerState.getTupleSpacesState: Returning all tuples");
      String output = this.tuples.toString();
      return output;
  }
}