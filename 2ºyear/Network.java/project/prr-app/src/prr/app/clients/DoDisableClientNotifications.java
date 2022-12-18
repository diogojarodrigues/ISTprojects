package prr.app.clients;

import prr.Network;
import prr.app.exceptions.UnknownClientKeyException;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import prr.exceptions.ClientNotFoundException;
import prr.exceptions.ClientNotificationsAlreadyDisabledException;

/**
 * Disable client notifications.
 */
class DoDisableClientNotifications extends Command<Network> {

	DoDisableClientNotifications(Network receiver) {
		super(Label.DISABLE_CLIENT_NOTIFICATIONS, receiver);
		addStringField("key", Prompt.key());
	}

	@Override
	protected final void execute() throws CommandException {
		try {
			_receiver.disableClientNotifications(stringField("key"));
		}
		catch(ClientNotFoundException e) { throw new UnknownClientKeyException(e.getKey()); }
		catch(ClientNotificationsAlreadyDisabledException e) {
			_display.popup(Message.clientNotificationsAlreadyDisabled());
		}
	}
}
