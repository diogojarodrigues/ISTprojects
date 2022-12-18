package prr.client;

import prr.communication.Text;
import prr.communication.Video;
import prr.communication.Voice;

import prr.communication.Communication;

public class PlatinumClient extends ClientStatus {
	private int _consecutiveTextComms = 0;
	private PaymentPlan _tariff = new PlatinumBasePlan();

	public PlatinumClient(Client c) { super(c); }

	public void downgradeToGold() { _client.setStatus(new GoldClient(_client)); }
	public void downgradeToNormal() { _client.setStatus(new NormalClient(_client)); }
	
	public double calculateVideoCost(Video comm) { 
		_consecutiveTextComms = 0;
		return _tariff.calculateVideoCommCost(comm); 
	}

	public double calculateVoiceCost(Voice comm) {
		_consecutiveTextComms = 0;
		return _tariff.calculateVoiceCommCost(comm);
	}

	public double calculateTextCost(Text comm) {
		_consecutiveTextComms++;
		return _tariff.calculateTextCommCost(comm);
	}

	public void update() {
		double balance = _client.getBalance(); 
		if (balance < 0 ) downgradeToNormal();
		if (balance > 0 && _consecutiveTextComms == 2) downgradeToGold();
	}

	@Override
	public String toString() { return "PLATINUM"; }
}