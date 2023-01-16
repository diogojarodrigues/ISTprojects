package prr.app.clients;

import java.util.List;
import java.util.Collection;

import prr.Network;
import prr.app.exceptions.UnknownClientKeyException;
import prr.client.Client;
import prr.exceptions.ClientNotFoundException;
import prr.notification.Notification;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

/**
 * Show specific client: also show previous notifications.
 */
class DoShowClient extends Command<Network> {

	DoShowClient(Network receiver) {
		super(Label.SHOW_CLIENT, receiver);
		addStringField("Key", Prompt.key());
	}

	@Override
	protected final void execute() throws CommandException {
		try {
			Client c = _receiver.findClient(stringField("Key"));
			Collection<Notification> notifications = _receiver.getClientNotifications(stringField("Key"));
			_display.addLine(c);
			_display.popup(notifications);
			//_display.display();
		}
		catch (ClientNotFoundException e) { throw new UnknownClientKeyException(e.getKey()); }
	}
}
