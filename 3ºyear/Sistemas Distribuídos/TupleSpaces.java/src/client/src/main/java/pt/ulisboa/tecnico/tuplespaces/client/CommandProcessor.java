package pt.ulisboa.tecnico.tuplespaces.client;

import pt.ulisboa.tecnico.tuplespaces.client.grpc.*;
import pt.ulisboa.tecnico.tuplespaces.client.util.OrderedDelayer;

import pt.ulisboa.tecnico.tuplespaces.replicaTotalOrder.contract.TupleSpacesReplicaTotalOrder.*;
import pt.ulisboa.tecnico.tuplespaces.client.util.ResponseCollector;
import pt.ulisboa.tecnico.tuplespaces.replicaTotalOrder.contract.TupleSpacesReplicaGrpc.TupleSpacesReplicaStub;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;


public class CommandProcessor {

    // Constants
    private static final String SPACE = " ";
    private static final String BGN_TUPLE = "<";
    private static final String END_TUPLE = ">";
    private static final String PUT = "put";
    private static final String READ = "read";
    private static final String TAKE = "take";
    private static final String SLEEP = "sleep";
    private static final String SET_DELAY = "setdelay";
    private static final String EXIT = "exit";
    private static final String GET_TUPLE_SPACES_STATE = "getTupleSpacesState"; 
    private static final int QUALIFIER_A = 0;
    private static final int QUALIFIER_B = 1;
    private static final int QUALIFIER_C = 2;


    private final ClientService clientService;
    private final SequencerService sequencerService;
    private int clientId;
    private int num_servers;

    private final boolean debug = false;

    public CommandProcessor(ClientService clientService, SequencerService sequencerService, int clientId) {
        this.clientService = clientService;
        this.sequencerService = sequencerService;
        this.clientId = clientId;
        this.num_servers = 0;
    }

    void parseInput() {

        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        //Use name server to get all
        List<String> servers = NameServerService.lookup(this.clientService.serviceName);

        // Check if there are servers available
        if (servers == null || servers.isEmpty()) {
            System.out.println("No servers available");
            scanner.close();
            System.exit(0);
        }
        else {
            this.num_servers = (int) servers.stream().filter(element -> element != null && !element.equals("")).count();
            if (debug) System.out.println("Servers:\n " + num_servers + "\n");
        }

        this.clientService.connectTargets(servers);
        this.clientService.delayer = new OrderedDelayer(3);

        if (debug) System.out.println("Servers available: " + this.num_servers + "\n");
        for (int i = 0; i < this.num_servers; i++) {
            if (debug) System.out.println(i + " - " + servers.get(i));
        }

        // Process user input
        while (!exit) {
            if (debug) System.out.print("\n" + clientId + " > ");
            else System.out.print("> ");
            String line = scanner.nextLine().trim();
            String[] split = line.split(SPACE);
             switch (split[0]) {
                case PUT:
                    this.put(split);
                    break;

                case READ:
                    this.read(split);
                    break;

                case TAKE:
                    this.take(split);
                    break;

                case GET_TUPLE_SPACES_STATE:
                    this.getTupleSpacesState(split);
                    break;

                case SLEEP:
                    this.sleep(split);
                    break;

                case SET_DELAY:
                    this.setdelay(split);
                    break;

                case EXIT:
                    exit = true;
                    this.clientService.shutdown();
                    System.exit(0);
                    break;

                default:
                    this.printUsage();
                    break;
             }
        }
        scanner.close();
    }

    private <T> String makeRequest(Object request, int num_answers) throws StatusRuntimeException {
        ResponseCollector col = new ResponseCollector();
        List<TupleSpacesReplicaStub> stubs = this.clientService.getAllAsyncStubs();

        ExecutorService executor = Executors.newFixedThreadPool(1);

            executor.submit(() -> {
            for (int i: clientService.delayer) {

                if (stubs.get(i) == null) {
                    continue;
                }

                switch (request.getClass().getSimpleName()) {
                    case "PutRequest":
                        stubs.get(i).put((PutRequest) request, new ClientObserver<PutResponse>(col));
                        break;
                    case "ReadRequest":
                        stubs.get(i).read((ReadRequest) request, new ClientObserver<ReadResponse>(col));
                        break;
                    case "TakeRequest":
                        stubs.get(i).take((TakeRequest) request, new ClientObserver<TakeResponse>(col));
                        break;
                    default:
                        System.out.println("Unknown request type");
                        break;
                }

            }
        });

        col.waitUntilAllReceived(num_answers);
        executor.shutdown();

        if (col.getErrors().size() > 0) {
            throw new StatusRuntimeException(Status.INVALID_ARGUMENT.withDescription(col.getErrors().get(0)));
        }

        if (col.getResponses().isEmpty()) {
            return "No response";
        }

        return col.getResponses().get(0);
    }


    private void put(String[] split){

        // check if input is valid
        if (!this.inputIsValid(split)) {
            this.printUsage();
            return;
        }

        // get the tuple
        String tuple = split[1];

        try {
            int number = sequencerService.getSeqNumber();
            if (number < 0) {
                System.out.println("Error getting sequence number");
                return;
            }

            PutRequest request = PutRequest.newBuilder().setNewTuple(tuple).setSeqNumber(number).build();
            makeRequest(request, this.num_servers);

            System.out.println("OK\n");
        } catch (StatusRuntimeException e) {
            System.out.println("Caught InterruptedException with description:\n" + e.getStatus().getDescription() +"\n");
        }
    }

    private void read(String[] split){
        // check if input is valid
        if (!this.inputIsValid(split)) {
            this.printUsage();
            return;
        }

        // get the tuple
        String tuple = split[1];

        // read the tuple
        try {
            ReadRequest request = ReadRequest.newBuilder().setSearchPattern(tuple).build();
            String response = makeRequest(request, 1);

            System.out.println("OK");
            System.out.println(response + "\n");
        } catch (StatusRuntimeException e) {
            System.out.println("Caught InterruptedException with description:\n" + e.getStatus().getDescription());
        }
    }


    private void take(String[] split){
         // check if input is valid
        if (!this.inputIsValid(split)) {
            this.printUsage();
            return;
        }
        
        // get the tuple & remove it from the tuple space
        String tuple = split[1];
        
        try {
            int number = sequencerService.getSeqNumber();
            if (number < 0) {
                System.out.println("Error getting sequence number");
                return;
            }

            TakeRequest request = TakeRequest.newBuilder().setSearchPattern(tuple).setSeqNumber(number).build();
            String response = makeRequest(request, this.num_servers);

            System.out.println("OK");
            System.out.println(response + "\n");
        } catch (StatusRuntimeException e) {
            System.out.println("Caught InterruptedException with description:\n" + e.getStatus().getDescription() +"\n");
        }
    }

    private void getTupleSpacesState(String[] split){
        // check if input is valid
        if (split.length != 2){
            this.printUsage();
            return;
        }

        String qualifier = split[1]; 
        int index;

        switch (qualifier) {
            case "A":
                index = QUALIFIER_A;
                break;
            case "B":
                index = QUALIFIER_B;
                break;
            case "C":
                index = QUALIFIER_C;
                break;
            default:
                this.printUsage();
                return;
        }

        // get the tuple spaces state of the server with the given qualifier
        try {
            ResponseCollector col = new ResponseCollector();
            GetTupleSpacesStateRequest request = GetTupleSpacesStateRequest.newBuilder().build();
            for (int i: clientService.delayer) {
                if (i == index) { 
                    this.clientService.getAllAsyncStubs().get(index).getTupleSpacesState(request, new ClientObserver<GetTupleSpacesStateResponse>(col));
                    break;
                }
            }
            col.waitUntilAllReceived(1);
            System.out.println("OK");
            System.out.println(col.getResponses().get(0) + "\n");
        } catch (StatusRuntimeException e) {
            System.out.println("Caught exception with description:\n" + e.getStatus().getDescription());
        }
    }

    // CLIENT FUNCTIONS
    private void sleep(String[] split) {
        if (split.length != 2){
            this.printUsage();
            return;
        }
        Integer time;

        // checks if input String can be parsed as an Integer
        try {
            time = Integer.parseInt(split[1]);
        } catch (NumberFormatException e) {
            this.printUsage();
            return;
        }

        try {
            Thread.sleep(time*1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private int indexOfServerQualifier(String qualifier) {
        switch (qualifier) {
            case "A":
                return 0;
            case "B":
                return 1;
            case "C":
                return 2;
            default:
                return -1;
        }
    }

    private void setdelay(String[] split) {
        if (split.length != 3){
          this.printUsage();
          return;
        }
        int qualifier = indexOfServerQualifier(split[1]);
        if (qualifier == -1)
          System.out.println("Invalid server qualifier");
  
        Integer time;
  
        // checks if input String can be parsed as an Integer
        try {
          time = Integer.parseInt(split[2]);
        } catch (NumberFormatException e) {
          this.printUsage();
          return;
        }
        // register delay <time> for when calling server <qualifier>
        this.clientService.setDelay(qualifier, time);
      }

    private void printUsage() {
        System.out.println("Usage:\n" +
                "- put <element[,more_elements]>\n" +
                "- read <element[,more_elements]>\n" +
                "- take <element[,more_elements]>\n" +
                "- getTupleSpacesState <server>\n" +
                "- sleep <integer>\n" +
                "- setdelay <server> <integer>\n" +
                "- exit\n");
    }

    // Check if the input is valid
    private boolean inputIsValid(String[] input){

        if (input.length < 2
                ||
                input.length > 2
                ||
                input[1].isEmpty()
                ||
                !input[1].substring(0,1).equals(BGN_TUPLE)
                ||
                !input[1].endsWith(END_TUPLE)
        ) {
            return false;
        }
        else {
            return true;
        }
    }

}
