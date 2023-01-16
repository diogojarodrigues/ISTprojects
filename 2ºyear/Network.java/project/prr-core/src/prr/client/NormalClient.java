package prr.client;

import prr.communication.Text;
import prr.communication.Video;
import prr.communication.Voice;

public class NormalClient extends ClientStatus {
	private PaymentPlan _tariff = new NormalBasePlan();

	public NormalClient(Client c) { super(c); }

	public void upgradeToGold() {
		_client.setStatus(new GoldClient(_client));
	}

	public double calculateVideoCost(Video comm) {
		return _tariff.calculateVideoCommCost(comm);
	}

	public double calculateVoiceCost(Voice comm) {
		return _tariff.calculateVoiceCommCost(comm);
	}

	public double calculateTextCost(Text comm) {
		return _tariff.calculateTextCommCost(comm);
	}

	public void update() { 
		if (_client.getBalance() > 500) 
			upgradeToGold();
	}

	@Override
	public String toString() { return "NORMAL"; }
}