package prr.client;

import prr.communication.Text;
import prr.communication.Video;
import prr.communication.Voice;

public class GoldClient extends ClientStatus {
	private int _consecutiveVideoComms = 0;
	private PaymentPlan _tariff = new GoldBasePlan();
	
	public GoldClient(Client c) { super(c); }

	public void downgradeToNormal() { _client.setStatus(new NormalClient(_client)); }

	public void upgradeToPlatinum() { _client.setStatus(new PlatinumClient(_client)); }

	public double calculateVideoCost(Video comm) {
		_consecutiveVideoComms++;
		return _tariff.calculateVideoCommCost(comm);
	}

	public double calculateVoiceCost(Voice comm) { 
		_consecutiveVideoComms = 0;
		return _tariff.calculateVoiceCommCost(comm); }

	public double calculateTextCost(Text comm) { 
		_consecutiveVideoComms = 0;
		return _tariff.calculateTextCommCost(comm); }

	public void update() {
		double balance = _client.getBalance();
		if (balance < 0) downgradeToNormal();
		if (_consecutiveVideoComms == 5 && balance > 0) {
			_consecutiveVideoComms = 0;
			upgradeToPlatinum();
		}
	}

	@Override
	public String toString() { return "GOLD"; }
}
