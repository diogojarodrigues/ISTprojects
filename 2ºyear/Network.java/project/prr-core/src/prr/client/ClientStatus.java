package prr.client;

import java.io.Serializable;
import prr.communication.*;

public abstract class ClientStatus implements Serializable{
	protected Client _client;

	public ClientStatus(Client client) {
		_client = client;
	}

	abstract public double calculateVideoCost(Video comm);
	abstract public double calculateVoiceCost(Voice comm);
	abstract public double calculateTextCost(Text comm);

	public abstract void update();

}