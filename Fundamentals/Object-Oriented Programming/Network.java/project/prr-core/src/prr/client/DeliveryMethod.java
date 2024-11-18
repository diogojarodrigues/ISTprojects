package prr.client;

import prr.notification.Notification;
import java.io.Serializable;


public abstract class DeliveryMethod implements Serializable {
	public abstract void delivery(Notification notification);
}
