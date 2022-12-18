package prr.app.lookups;

import prr.Network;
import prr.exceptions.ClientNotFoundException;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import java.util.Collection;

/**
 * Command for showing all communications.
 */
class DoShowAllCommunications extends Command<Network> {

	DoShowAllCommunications(Network receiver) {
		super(Label.SHOW_ALL_COMMUNICATIONS, receiver);
	}

	@Override
	protected final void execute() throws CommandException {
        Collection c = _receiver.showAllCommunications();
		_display.popup(c);
	}
}
