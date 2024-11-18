package pt.ulisboa.tecnico.tuplespaces.client.grpc;

import io.grpc.stub.StreamObserver;
import pt.ulisboa.tecnico.tuplespaces.replicaTotalOrder.contract.TupleSpacesReplicaTotalOrder.*;

import pt.ulisboa.tecnico.tuplespaces.client.util.ResponseCollector;


public class ClientObserver<R> implements StreamObserver<R> {

    private ResponseCollector col;

    public ClientObserver(ResponseCollector col) {
        this.col = col;
    }
    
    @Override
    public void onNext(R r) {
        if (r instanceof ReadResponse ) {
            ReadResponse a = (ReadResponse) r;
            col.addResponse(a.getResult());
        } else if (r instanceof PutResponse) {
            PutResponse p = (PutResponse) r;
            col.addResponse(p.toString());
        } else if (r instanceof TakeResponse) {
            TakeResponse t = (TakeResponse) r;
            col.addResponse(t.getResult());
        } else if (r instanceof GetTupleSpacesStateResponse) {
            GetTupleSpacesStateResponse t = (GetTupleSpacesStateResponse) r;
            col.addResponse(t.getTuple());
        }
    }

    @Override
    public void onError(Throwable t) {
        col.addError(t.getMessage());
        col.requestFailed();
    }

    @Override
    public void onCompleted() {
        col.requestCompleted();
    }
}