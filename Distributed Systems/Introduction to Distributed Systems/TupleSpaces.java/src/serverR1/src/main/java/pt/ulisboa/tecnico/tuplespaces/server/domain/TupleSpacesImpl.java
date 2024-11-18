package pt.ulisboa.tecnico.tuplespaces.server.domain;

import io.grpc.stub.StreamObserver;
import static io.grpc.Status.INVALID_ARGUMENT;
import pt.ulisboa.tecnico.tuplespaces.centralized.contract.TupleSpacesGrpc;
import pt.ulisboa.tecnico.tuplespaces.centralized.contract.TupleSpacesCentralized.*;
import java.util.List;

public class TupleSpacesImpl extends TupleSpacesGrpc.TupleSpacesImplBase {
    private ServerState Server;

    public TupleSpacesImpl(boolean debug) {
        super();
        Server = new ServerState(debug);
    }

    @Override
    public void put(PutRequest request, StreamObserver<PutResponse> responseObserver) {
        int status = Server.put(request.getNewTuple());

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
        
        String output = Server.take(request.getSearchPattern());

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
