package dadkvs.consoleclient;

import dadkvs.DadkvsConsole;
import dadkvs.DadkvsConsoleServiceGrpc;
import dadkvs.DadkvsMain;
import dadkvs.DadkvsMainServiceGrpc;
import dadkvs.util.CollectorStreamObserver;
import dadkvs.util.GenericResponseCollector;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

public class DadkvsConsoleClient {

    public static void main(String[] args) throws Exception {
        int n_servers = 5;
        int replica = 0;
        int mode = 0;
        int configuration = 0;
        int client_id = 0;
        int sequence_number = 0;
        boolean isleader = true;

        System.out.println(DadkvsConsole.class.getSimpleName());

        // receive and print arguments
        System.out.printf("Received %d arguments%n", args.length);
        for (int i = 0; i < args.length; i++) {
            System.out.printf("arg[%d] = %s%n", i, args[i]);
        }

        // check arguments
        if (args.length < 2) {
            System.err.println("Argument(s) missing!");
            System.err.printf("Usage: java %s host port%n", DadkvsConsoleClient.class.getName());
            return;
        }

        // set servers
        String host = args[0];
        int port = Integer.parseInt(args[1]);
        String[] targets = new String[n_servers];
        for (int i = 0; i < n_servers; i++) {
            int target_port = port + i;
            targets[i] = new String();
            targets[i] = host + ":" + target_port;
            System.out.printf("targets[%d] = %s%n", i, targets[i]);
        }

        // Let us use plaintext communication because we do not have certificates
        ManagedChannel[] channels = new ManagedChannel[n_servers];
        for (int i = 0; i < n_servers; i++) {
            channels[i] = ManagedChannelBuilder.forTarget(targets[i]).usePlaintext().build();
        }

        DadkvsMainServiceGrpc.DadkvsMainServiceStub[] main_async_stubs =
                new DadkvsMainServiceGrpc.DadkvsMainServiceStub[n_servers];
        DadkvsConsoleServiceGrpc.DadkvsConsoleServiceStub[] console_async_stubs =
                new DadkvsConsoleServiceGrpc.DadkvsConsoleServiceStub[n_servers];

        for (int i = 0; i < n_servers; i++) {
            main_async_stubs[i] = DadkvsMainServiceGrpc.newStub(channels[i]);
            console_async_stubs[i] = DadkvsConsoleServiceGrpc.newStub(channels[i]);
        }

        Scanner scanner = new Scanner(System.in);
        String command;

        boolean keep_going = true;

        while (keep_going) {
            System.out.print("console> ");
            command = scanner.nextLine();
            String[] commandParts = command.split(" ");
            String mainCommand = commandParts[0].toLowerCase();
            String parameter1 = commandParts.length > 1 ? commandParts[1] : null;
            String parameter2 = commandParts.length > 2 ? commandParts[2] : null;
            String parameter3 = commandParts.length > 3 ? commandParts[3] : null;

            switch (mainCommand) {
                case "help":
                    System.out.println("\thelp");
                    System.out.println("\tleader on/off replica");
                    System.out.println("\tdebug mode replica");
                    System.out.println("\treconfig configuration");
                    System.out.println("\texit");
                    break;
                case "leader":
                    System.out.println("leader " + parameter1 + " " + parameter2);
                    if ((parameter1.equals("on") || parameter1.equals("off"))
                            && (parameter2 != null)) {
                        if (parameter1.equals("on")) isleader = true;
                        else isleader = false;

                        try {
                            replica = Integer.parseInt(parameter2);
                            System.out.println(
                                    "setting leader " + isleader + " replica " + replica);

                            DadkvsConsole.SetLeaderRequest.Builder setleader_request =
                                    DadkvsConsole.SetLeaderRequest.newBuilder();
                            ArrayList<DadkvsConsole.SetLeaderReply> setleader_responses =
                                    new ArrayList<DadkvsConsole.SetLeaderReply>();
                            ;
                            GenericResponseCollector<DadkvsConsole.SetLeaderReply>
                                    setleader_collector =
                                            new GenericResponseCollector<
                                                    DadkvsConsole.SetLeaderReply>(
                                                    setleader_responses, 1);
                            CollectorStreamObserver<DadkvsConsole.SetLeaderReply>
                                    setleader_observer =
                                            new CollectorStreamObserver<
                                                    DadkvsConsole.SetLeaderReply>(
                                                    setleader_collector);
                            setleader_request.setIsleader(isleader);
                            console_async_stubs[replica].setleader(
                                    setleader_request.build(), setleader_observer);
                            setleader_collector.waitForTarget(1);
                            if (setleader_responses.size() >= 1) {
                                Iterator<DadkvsConsole.SetLeaderReply> setleader_iterator =
                                        setleader_responses.iterator();
                                DadkvsConsole.SetLeaderReply setleader_reply =
                                        setleader_iterator.next();
                                System.out.println("reply = " + setleader_reply.getIsleaderack());
                            } else System.out.println("no reply received");
                        } catch (NumberFormatException e) {
                            System.out.println("usage: leader 1/0 replica (1=on, 0=off)");
                        }
                    } else {
                        System.out.println("usage: leader 1/0 replica (1=on, 0=off)");
                    }
                    break;
                case "debug":
                    System.out.println("debug " + parameter1 + " " + parameter2);
                    if ((parameter1 != null) && (parameter2 != null)) {
                        try {
                            mode = Integer.parseInt(parameter1);
                            replica = Integer.parseInt(parameter2);
                            System.out.println(
                                    "setting debug with mode " + mode + " on replica " + replica);

                            DadkvsConsole.SetDebugRequest.Builder setdebug_request =
                                    DadkvsConsole.SetDebugRequest.newBuilder();
                            ArrayList<DadkvsConsole.SetDebugReply> setdebug_responses =
                                    new ArrayList<DadkvsConsole.SetDebugReply>();
                            ;
                            GenericResponseCollector<DadkvsConsole.SetDebugReply>
                                    setdebug_collector =
                                            new GenericResponseCollector<
                                                    DadkvsConsole.SetDebugReply>(
                                                    setdebug_responses, 1);
                            CollectorStreamObserver<DadkvsConsole.SetDebugReply> setdebug_observer =
                                    new CollectorStreamObserver<DadkvsConsole.SetDebugReply>(
                                            setdebug_collector);
                            setdebug_request.setMode(mode);
                            console_async_stubs[replica].setdebug(
                                    setdebug_request.build(), setdebug_observer);
                            setdebug_collector.waitForTarget(1);
                            if (setdebug_responses.size() >= 1) {
                                Iterator<DadkvsConsole.SetDebugReply> setdebug_iterator =
                                        setdebug_responses.iterator();
                                DadkvsConsole.SetDebugReply setdebug_reply =
                                        setdebug_iterator.next();
                                System.out.println("reply = " + setdebug_reply.getAck());
                            } else System.out.println("no reply received");
                        } catch (NumberFormatException e) {
                            System.out.println("usage: debug mode replica");
                        }
                    } else {
                        System.out.println("usage: debug mode replica");
                    }
                    break;
                case "reconfig":
                    System.out.println("reconfig " + parameter1);
                    int responses_needed = 1;
                    if (parameter1 != null) {
                        try {
                            configuration = Integer.parseInt(parameter1);
                            System.out.println("reconfiguring to configuration " + configuration);
                            int old_config = 0;
                            int old_config_ts = 0;

                            DadkvsMain.ReadRequest.Builder read_request =
                                    DadkvsMain.ReadRequest.newBuilder();
                            ;
                            ArrayList<DadkvsMain.ReadReply> read_responses =
                                    new ArrayList<DadkvsMain.ReadReply>();
                            ;
                            GenericResponseCollector<DadkvsMain.ReadReply> read_collector =
                                    new GenericResponseCollector<DadkvsMain.ReadReply>(
                                            read_responses, n_servers);
                            ;

                            read_request.setKey(0);
                            for (int i = 0; i < n_servers; i++) {
                                CollectorStreamObserver<DadkvsMain.ReadReply> read_observer =
                                        new CollectorStreamObserver<DadkvsMain.ReadReply>(
                                                read_collector);
                                main_async_stubs[i].read(read_request.build(), read_observer);
                            }
                            read_collector.waitForTarget(responses_needed);
                            if (read_responses.size() >= responses_needed) {
                                Iterator<DadkvsMain.ReadReply> read_iterator =
                                        read_responses.iterator();
                                DadkvsMain.ReadReply read_reply = read_iterator.next();
                                System.out.println(
                                        "read key "
                                                + read_request.getKey()
                                                + " = <"
                                                + read_reply.getValue()
                                                + ","
                                                + read_reply.getTimestamp()
                                                + ">");
                                old_config = read_reply.getValue();
                                old_config_ts = read_reply.getTimestamp();
                            } else {
                                System.out.println("error reading configuration");
                            }

                            if (configuration != old_config + 1)
                                System.out.println("configuration should be " + (old_config + 1));
                            else {
                                sequence_number = sequence_number + 1;
                                int reqid = sequence_number * 100 + client_id;

                                DadkvsMain.CommitRequest.Builder commit_request =
                                        DadkvsMain.CommitRequest.newBuilder();

                                commit_request
                                        .setReqid(reqid)
                                        .setKey1(0)
                                        .setVersion1(old_config_ts)
                                        .setKey2(0)
                                        .setVersion2(old_config_ts)
                                        .setWritekey(0)
                                        .setWriteval(configuration);

                                System.out.println("Reqid " + reqid);

                                ArrayList<DadkvsMain.CommitReply> commit_responses = new ArrayList<>();
                                GenericResponseCollector<DadkvsMain.CommitReply> commit_collector = new GenericResponseCollector<>(commit_responses, n_servers);

                                for (int i = 0; i < n_servers; i++) {
                                    CollectorStreamObserver<DadkvsMain.CommitReply>
                                            commit_observer =
                                                    new CollectorStreamObserver<
                                                            DadkvsMain.CommitReply>(
                                                            commit_collector);
                                    main_async_stubs[i].committx(
                                            commit_request.build(), commit_observer);
                                }
                                commit_collector.waitForTarget(responses_needed);
                                if (commit_responses.size() >= responses_needed) {
                                    Iterator<DadkvsMain.CommitReply> commit_iterator =
                                            commit_responses.iterator();
                                    DadkvsMain.CommitReply commit_reply = commit_iterator.next();
                                    System.out.println(
                                            "Reqid = "
                                                    + reqid
                                                    + " id in reply = "
                                                    + commit_reply.getReqid());
                                    if (commit_reply.getAck()) {
                                        System.out.println(
                                                "Committed key "
                                                        + commit_request.getWritekey()
                                                        + " with value "
                                                        + commit_request.getWriteval());
                                    } else {
                                        System.out.println("Commit Failed");
                                    }
                                } else System.out.println("Panic...error commiting");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("usage: reconfig configuration");
                        }
                    } else {
                        System.out.println("usage: reconfig configuration");
                    }
                    break;
                case "exit":
                    keep_going = false;
                    break;
                case "":
                    break;
                default:
                    System.out.println("Unknown command: " + mainCommand);
                    break;
            }
        }
        System.out.println("Exiting...");
        for (int i = 0; i < n_servers; i++) {
            channels[i].shutdownNow();
        }
        scanner.close();
    }
}