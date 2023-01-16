package prr.client;

import prr.communication.Video;
import prr.communication.Voice;
import prr.communication.Text;


import java.io.Serializable;

public abstract class PaymentPlan implements Serializable {

    abstract public double calculateVideoCommCost(Video comm);
    abstract public double calculateVoiceCommCost(Voice comm);
    abstract public double calculateTextCommCost(Text comm);
    
}