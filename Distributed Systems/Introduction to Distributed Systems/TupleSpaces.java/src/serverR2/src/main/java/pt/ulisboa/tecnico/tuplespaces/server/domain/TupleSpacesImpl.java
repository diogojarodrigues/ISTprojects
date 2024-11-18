package pt.ulisboa.tecnico.tuplespaces.server.domain;

import io.grpc.stub.StreamObserver;
import static io.grpc.Status.INVALID_ARGUMENT;

import pt.ulisboa.tecnico.tuplespaces.replicaXuLiskov.contract.TupleSpacesReplicaGrpc;
import pt.ulisboa.tecnico.tuplespaces.replicaXuLiskov.contract.TupleSpacesReplicaXuLiskov.*;

public class TupleSpacesImpl extends TupleSpacesReplicaGrpc.TupleSpacesReplicaImplBase {
    private ServerState Server;
    private String qualifier; 

    public TupleSpacesImpl(boolean debug, String qualifier) {
        super();
        this.Server = new ServerState(debug);
        this.qualifier = qualifier;
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
        PutResponse response = PutResponse.newBuilder().setResult("ACK " + qualifier).build();
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
   public void takePhase1(TakePhase1Request request, StreamObserver<TakePhase1Response> responseObserver) {
       String output = Server.takePhase1(request.getSearchPattern(), request.getClientId());

       if (output == null) {
           responseObserver.onError(INVALID_ARGUMENT.withDescription("Tuple is not corretly formated").asRuntimeException());
           return;
       }

       TakePhase1Response response = TakePhase1Response.newBuilder().setReservedTuples(output).build();
       responseObserver.onNext(response);
       responseObserver.onCompleted();
    }

    @Override
    public void takePhase1Release(TakePhase1ReleaseRequest request, StreamObserver<TakePhase1ReleaseResponse> responseObserver) {
        Server.takePhase1Release(request.getClientId());

        TakePhase1ReleaseResponse response = TakePhase1ReleaseResponse.newBuilder().setResult("ACK " + qualifier).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void takePhase2(TakePhase2Request request, StreamObserver<TakePhase2Response> responseObserver) {
        String output = Server.takePhase2(request.getTuple(), request.getClientId());

        if (output == null) {
            responseObserver.onError(INVALID_ARGUMENT.withDescription("Couldnt remove the tuple!").asRuntimeException());
            return;
        }

        TakePhase2Response response = TakePhase2Response.newBuilder().setResult("ACK " + qualifier).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }

    @Override
    public void getTupleSpacesState(GetTupleSpacesStateRequest request, StreamObserver<GetTupleSpacesStateResponse> responseObserver) {
        String output = Server.getTupleSpacesState();
        GetTupleSpacesStateResponse response = GetTupleSpacesStateResponse.newBuilder().setTuples(output).build();
        responseObserver.onNext(response);
        responseObserver.onCompleted();
    }
}
