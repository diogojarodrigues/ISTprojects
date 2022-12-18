package prr.client;

import prr.communication.Text;
import prr.communication.Video;
import prr.communication.Voice;

public class PlatinumBasePlan extends PaymentPlan {

    public double calculateVideoCommCost(Video comm) { 
        if (comm.getOriginTerminal().isFriend(comm.getDestinationTerminal().getID())) 
            return 5*comm.getDuration();
        return 10*comm.getDuration();
    }

    public double calculateVoiceCommCost(Voice comm) { 
        if (comm.getOriginTerminal().isFriend(comm.getDestinationTerminal().getID())) 
            return 5*comm.getDuration();
        return 10*comm.getDuration();
    }

    public double calculateTextCommCost(Text comm) {
        if (comm.getMsgLength() < 50) return 0;
        return 4;
    }
    
}