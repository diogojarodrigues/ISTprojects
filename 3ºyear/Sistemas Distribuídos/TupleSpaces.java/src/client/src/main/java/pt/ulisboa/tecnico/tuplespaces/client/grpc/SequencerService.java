package pt.ulisboa.tecnico.tuplespaces.client.grpc;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.StatusRuntimeException;

import pt.ulisboa.tecnico.sequencer.contract.SequencerGrpc;
import pt.ulisboa.tecnico.sequencer.contract.SequencerOuterClass.*;

public class SequencerService {

	private static final String SEQUENCER_TARGET = "localhost:5002";
    private ManagedChannel sequencerChannel = ManagedChannelBuilder.forTarget(SEQUENCER_TARGET).usePlaintext().build();
    private SequencerGrpc.SequencerBlockingStub sequencerStub = SequencerGrpc.newBlockingStub(sequencerChannel);
    
    public Integer getSeqNumber() {
        try {
            GetSeqNumberRequest request = GetSeqNumberRequest.newBuilder().build();          
            GetSeqNumberResponse response = sequencerStub.getSeqNumber(request);
            return response.getSeqNumber();

        } catch (StatusRuntimeException e) {
            System.out.println("Caught exception with description:\n" + e.getStatus().getDescription() +"\n");
        }
        return -1;
    }

    public void shutdown() {
        sequencerChannel.shutdown();
    }
}
