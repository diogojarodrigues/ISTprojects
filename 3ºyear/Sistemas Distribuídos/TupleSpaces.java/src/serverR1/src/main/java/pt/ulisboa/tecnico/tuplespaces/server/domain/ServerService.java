package pt.ulisboa.tecnico.tuplespaces.server.domain;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;
import pt.ulisboa.tecnico.tuplespaces.nameserver.contract.NameServerGrpc;
import pt.ulisboa.tecnico.tuplespaces.nameserver.contract.NameServerOuterClass;

public class ServerService {
    private static final String DNS_TARGET = "localhost:5001";

    public static void registerServer(String serviceName, String target, String qualifier, boolean debug) {
        if (debug) {
            System.err.println("Registering server to DNS:");
        }
        try {
            ManagedChannel nameServerchannel = ManagedChannelBuilder.forTarget(DNS_TARGET).usePlaintext().build();
            NameServerGrpc.NameServerBlockingStub nameServerStub = NameServerGrpc.newBlockingStub(nameServerchannel);
            // Register server to DNS
            NameServerOuterClass.RegisterRequest request = NameServerOuterClass.RegisterRequest
                .newBuilder()
                .setServiceName(serviceName)
                .setQualifier(qualifier)
                .setTarget(target)
                .build();
            // make the call using the stub
            nameServerStub.register(request);
            if (debug) {
                System.err.println("Success");
            }
            // Shutdown the channel
            nameServerchannel.shutdown();

        } catch (StatusRuntimeException e) {
            if (debug) { System.err.println("Failed to register server to DNS: " + e.getStatus().getDescription()); }
            throw e;
        }
    }

    public static void unregisterServer(String serviceName, String target, String qualifier) {
        System.out.println("Unregistering server from DNS");
        try {
            ManagedChannel nameServerchannel = ManagedChannelBuilder.forTarget(DNS_TARGET).usePlaintext().build();
            NameServerGrpc.NameServerBlockingStub nameServerStub = NameServerGrpc.newBlockingStub(nameServerchannel);
            // Unregister server from DNS
            NameServerOuterClass.RemoveRequest request = NameServerOuterClass.RemoveRequest.newBuilder().setServiceName(serviceName).setTarget(target).build();
            NameServerOuterClass.RemoveResponse response = nameServerStub.remove(request);
            System.out.println(response);
            // Shutdown the channel
            nameServerchannel.shutdown();
        
        } catch (StatusRuntimeException e) {
            System.err.println("Failed to unregister server from DNS: " + e.getStatus().getDescription());
            throw e;
        }
    }
}
