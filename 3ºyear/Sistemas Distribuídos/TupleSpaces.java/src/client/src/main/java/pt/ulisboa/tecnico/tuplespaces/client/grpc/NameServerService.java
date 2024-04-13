package pt.ulisboa.tecnico.tuplespaces.client.grpc;

import java.util.ArrayList;
import java.util.List;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import pt.ulisboa.tecnico.tuplespaces.nameserver.contract.NameServerGrpc;
import pt.ulisboa.tecnico.tuplespaces.nameserver.contract.NameServerOuterClass.LookupRequest;
import pt.ulisboa.tecnico.tuplespaces.nameserver.contract.NameServerOuterClass.LookupResponse;

public class NameServerService {

    private static final String DNS_TARGET = "localhost:5001";


    public static List<String> lookup(String serviceName) {
        ManagedChannel nameServerChannel = ManagedChannelBuilder.forTarget(DNS_TARGET).usePlaintext().build();
        NameServerGrpc.NameServerBlockingStub nameServerStub = NameServerGrpc.newBlockingStub(nameServerChannel);

        try {
            LookupRequest request = LookupRequest.newBuilder().setServiceName(serviceName).build();
            LookupResponse response = nameServerStub.lookup(request);
            return response.getTargetsList();
        } catch (StatusRuntimeException e) {
            System.out.println("Caught exception with description:\n" + e.getStatus().getDescription() +"\n");
        } finally {
            nameServerChannel.shutdown();
        }

        return null;
    }
}
