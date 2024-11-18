package pt.ulisboa.tecnico.tuplespaces.client;

import pt.ulisboa.tecnico.tuplespaces.client.grpc.ClientService;
import pt.ulisboa.tecnico.tuplespaces.client.grpc.SequencerService;

public class ClientMain {
    public static void main(String[] args) {

        final boolean debug = false;

        // Check arguments
        if (args.length != 1) {
            System.err.println("Argument(s) incorrect!");
            System.err.println("Usage: mvn exec:java -Dexec.args= <clientId>");
            return;
        }
        
        final String clientServiceName = "TupleSpaces";
        final int clientId = Integer.parseInt(args[0]);

        final ClientService clientService = new ClientService(clientServiceName);
        final SequencerService sequencerService = new SequencerService();

        // Add shutdown hook to unregister server
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                if (debug) System.out.println("Shutting down client...");
                clientService.shutdown();
                sequencerService.shutdown();
                if (debug) System.out.println("Client shut down.");
            } catch (Exception e) {
                System.err.println("Error during client shutdown:");
            }
        }));

        // Wait for input from user
        CommandProcessor parser = new CommandProcessor(clientService, sequencerService, clientId);
        parser.parseInput();
    }
}
