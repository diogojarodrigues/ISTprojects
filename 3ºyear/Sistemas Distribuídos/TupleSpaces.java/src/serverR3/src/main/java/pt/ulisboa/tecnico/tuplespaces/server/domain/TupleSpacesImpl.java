package pt.ulisboa.tecnico.tuplespaces.server.domain;

import io.grpc.stub.StreamObserver;
import static io.grpc.Status.INVALID_ARGUMENT;

import pt.ulisboa.tecnico.tuplespaces.replicaTotalOrder.contract.TupleSpacesReplicaGrpc;
import pt.ulisboa.tecnico.tuplespaces.replicaTotalOrder.contract.TupleSpacesReplicaTotalOrder.*;

public class TupleSpacesImpl extends TupleSpacesReplicaGrpc.TupleSpacesReplicaImplBase {
    private ServerState Server; 

    public TupleSpacesImpl(boolean debug, String qualifier) {
        super();
        this.Server = new ServerState(debug);
    }

    @Override
    public void put(PutRequest request, StreamObserver<PutResponse> responseObserver) {

        int status = Server.put(request.getNewTuple(), request.getSeqNumber());
        
        // check if the tuple is correctly formated
        if (status == -1) {
            responseObserver.onError(INVALID_ARGUMENT.withDescription("Tuple is not corretly formated").asRuntimeException());
            return;
        }

        // respond to the client
        PutResponse response = PutResponse.newBuilder().build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void read(ReadRequest request, StreamObserver<ReadResponse> responseObserver) {

        String output = Server.read(request.getSearchPattern());

        if (output == null) {
            responseObserver.onError(INVALID_ARGUMENT.withDescription("Tuple is not corretly formated").asRuntimeException());
            return;
        }

        ReadResponse response = ReadResponse.newBuilder().setResult(output).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void take(TakeRequest request, StreamObserver<TakeResponse> responseObserver) {

        String output = Server.take(request.getSearchPattern(), request.getSeqNumber());

        if (output == null) {
            responseObserver.onError(INVALID_ARGUMENT.withDescription("Tuple is not corretly formated").asRuntimeException());
            return;
        }

        TakeResponse response = TakeResponse.newBuilder().setResult(output).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getTupleSpacesState(GetTupleSpacesStateRequest request, StreamObserver<GetTupleSpacesStateResponse> responseObserver) {
        String output = Server.getTupleSpacesState();
        GetTupleSpacesStateResponse response = GetTupleSpacesStateResponse.newBuilder().setTuple(output).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
