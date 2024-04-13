package pt.ulisboa.tecnico.tuplespaces.server;

import java.util.concurrent.TimeUnit;

import io.grpc.BindableService;
import io.grpc.Server;
import io.grpc.ServerBuilder;
import pt.ulisboa.tecnico.tuplespaces.server.domain.TupleSpacesImpl;
import pt.ulisboa.tecnico.tuplespaces.server.domain.ServerService;

public class ServerMain {
    private static final String service_name = "TupleSpaces";
    private static final String host = "localhost";

    public static void main(String[] args) {
        System.out.println(ServerMain.class.getSimpleName());

        // receive and print arguments
        System.out.printf("Received %d arguments%n", args.length);
        for (int i = 0; i < args.length; i++) {
            System.out.printf("arg[%d] = %s%n", i, args[i]);
        }
        System.out.println();

        // check arguments
        if (args.length != 2 && args.length != 3) {
            System.err.println("Argument(s) invalid!");
            System.err.println("Usage: mvn exec:java -Dexec.args=<port> <qualifier> {-debug}");
            return;
        }
        final boolean debug;
        if (args.length == 3 && args[2].equals("-debug")) {
            System.out.println("Debug mode enabled");
            debug = true;
        } else {
            debug = false;
        }

        final int port = Integer.parseInt(args[0]);
        final String qualifier = args[1];
        final String target = host + ":" + port;  // ip:port  

        final BindableService impl = (BindableService) new TupleSpacesImpl(debug, qualifier);
        
        // Create a new server to listen on port
        Server server = ServerBuilder.forPort(port).addService(impl).build();

        // Add shutdown hook to unregister server
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Shutting down server...");
            try {
                server.shutdown();
                server.awaitTermination(30, TimeUnit.SECONDS);
                // Remove server from DNS
                ServerService.unregisterServer(service_name, target, qualifier);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                System.err.println("Error during server shutdown: " + e);
            }
            System.out.println("Server shut down.");
        }));

        // Start the server
        try {
            server.start();
            // Register server to DNS
            ServerService.registerServer(service_name, target, qualifier, debug);     
        } catch (Exception e) {
            System.err.println("Server failed to start: " + e);
            System.exit(1);
        }
		System.out.println("Server started\n");

        try {
            server.awaitTermination();
        } catch (Exception e) {
            System.err.println("Server failed to terminate: " + e);
            System.exit(1);
        }
    }
}

