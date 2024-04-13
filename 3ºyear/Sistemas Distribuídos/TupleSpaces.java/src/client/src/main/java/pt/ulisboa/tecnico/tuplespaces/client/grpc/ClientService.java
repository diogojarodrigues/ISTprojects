package pt.ulisboa.tecnico.tuplespaces.client.grpc;

import java.util.ArrayList;
import java.util.List;

import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import pt.ulisboa.tecnico.tuplespaces.client.util.OrderedDelayer;

import pt.ulisboa.tecnico.tuplespaces.replicaTotalOrder.contract.TupleSpacesReplicaGrpc.TupleSpacesReplicaStub;
import pt.ulisboa.tecnico.tuplespaces.replicaTotalOrder.contract.TupleSpacesReplicaGrpc;

public class ClientService {

	private List<ManagedChannel> channels;
	private List<TupleSpacesReplicaStub> asyncStubs;

	public String serviceName;
	public OrderedDelayer delayer;

	public ClientService(String serviceName) {
		this.serviceName = serviceName;
		this.channels = new ArrayList<ManagedChannel>();
		this.asyncStubs = new ArrayList<TupleSpacesReplicaStub>();
	} 

	public void setDelay(int id, int delay) {
		delayer.setDelay(id, delay);
		System.out.println("Delay added\n");
	}
	/***
	 * establish the connection with all the servers provided by DNS
	 * @param targets the index of the servers to connect to
	 */
	public void connectTargets(List<String> targets) {
		// Channel is the abstraction to connect to a service endpoint.
		ManagedChannel channel;
		for (String target : targets) {
			if (target == null || target.isEmpty()){
				this.channels.add(null);
				this.asyncStubs.add(null);
				continue;
			}
			channel = ManagedChannelBuilder.forTarget(target).usePlaintext().build();
			this.channels.add(channel);
			this.asyncStubs.add(TupleSpacesReplicaGrpc.newStub(channel));
		}
	}

	public List<TupleSpacesReplicaStub> getAllAsyncStubs() {
		return this.asyncStubs;
	}

	public void shutdown() {
		for (ManagedChannel channel : this.channels) {
			if (channel != null)
				channel.shutdown();
		}
	}
}
