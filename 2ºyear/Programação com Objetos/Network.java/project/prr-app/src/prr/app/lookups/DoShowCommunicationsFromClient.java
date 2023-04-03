package prr.app.lookups;

import prr.app.exceptions.UnknownClientKeyException;
import prr.Network;
import prr.exceptions.ClientNotFoundException;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import java.util.Collection;
import java.util.Comparator;
import prr.client.Client;

/**
 * Show communications from a client.
 */
class DoShowCommunicationsFromClient extends Command<Network> {

	DoShowCommunicationsFromClient(Network receiver) {
		super(Label.SHOW_COMMUNICATIONS_FROM_CLIENT, receiver);
		addStringField("clientKey", Prompt.clientKey());
	}

	@Override
	protected final void execute() throws CommandException {
        try {
			Collection c = _receiver.fetchCommunicationsFromClient(stringField("clientKey"));
			_display.popup(c);
		}
		catch (ClientNotFoundException e) { throw new UnknownClientKeyException(e.getKey()); }
	}
}
