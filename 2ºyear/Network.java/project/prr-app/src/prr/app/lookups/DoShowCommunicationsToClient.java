package prr.app.lookups;

import prr.app.exceptions.UnknownClientKeyException;
import prr.Network;
import prr.exceptions.ClientNotFoundException;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import java.util.Collection;

/**
 * Show communications to a client.
 */
class DoShowCommunicationsToClient extends Command<Network> {

	DoShowCommunicationsToClient(Network receiver) {
		super(Label.SHOW_COMMUNICATIONS_TO_CLIENT, receiver);
		addStringField("clientKey", Prompt.clientKey());
	}

	@Override
	protected final void execute() throws CommandException {
        try {
			Collection c = _receiver.fetchCommunicationsToClient(stringField("clientKey"));
			_display.addAll(c);
			_display.display();
		}
		catch (ClientNotFoundException e) { throw new UnknownClientKeyException(e.getKey()); }
	}
}
