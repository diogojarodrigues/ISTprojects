package prr.client;

import prr.communication.Text;
import prr.communication.Video;
import prr.communication.Voice;

public class NormalBasePlan extends PaymentPlan {

    public double calculateVideoCommCost(Video comm) { 
        if (comm.getOriginTerminal().isFriend(comm.getDestinationTerminal().getID())) 
            return 15*comm.getDuration();
        return 30*comm.getDuration();
    }

    public double calculateVoiceCommCost(Voice comm) { 
        if (comm.getOriginTerminal().isFriend(comm.getDestinationTerminal().getID())) 
            return 10*comm.getDuration();
        return 20*comm.getDuration();
    }

    public double calculateTextCommCost(Text comm) {
        int msgLength = comm.getMsgLength();
        if (msgLength < 50) return 10;
        if (msgLength >= 100) return 2*msgLength;
        return 16;
    }

}