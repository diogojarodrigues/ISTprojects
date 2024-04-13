package pt.ulisboa.tecnico.tuplespaces.server;

public class Tuple {
    private String tuple;
    private Boolean locked = false;
    private Integer id;

    private final static String TUPLE_BGN = "<";
    private final static String TUPLE_END = ">";
    private final static String TUPLE_SPACE = " ";

    public Tuple(String tuple) {
        this.tuple = tuple;
    }

    @Override
    public String toString() {
        return this.tuple; // Or any meaningful representation of the tuple
    }

    /**
     * Tries to lock a tuple with a given client id
     * @param id
     * @return true if the tuple was locked sucessfully, false otherwise
     */
    public boolean lock(Integer id) {
        if (!this.locked) {
            this.locked = true;
            this.id = id;
            return true;
        }
        if (this.id == id)
            return true;
        return false;
    }

    public void unlock(Integer id) {
        if (this.locked && this.id == id) {
            this.locked = false;
            this.id = null;
        }
    }

    public Integer getId() {
        return this.id;
    }

    public String getTuple() {
        return this.tuple;
    }

    /**
     * Check if the tuple is correctly formated to be in the tuple space
     * @param tuple
     * @return true if the tuple is correctly formated, false otherwise
     */
    public static boolean isTupleCorrectlyFormated(String tuple) {

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
}
