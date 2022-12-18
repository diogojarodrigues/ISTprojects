package prr.app.lookups;

import java.util.Collection;
import prr.Network;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import java.util.stream.*;
import prr.terminals.Terminal;

/**
 * Show unused terminals (without communications).
 */
class DoShowUnusedTerminals extends Command<Network> {

	DoShowUnusedTerminals(Network receiver) {
		super(Label.SHOW_UNUSED_TERMINALS, receiver);
	}

	@Override
	protected final void execute() throws CommandException {
		Collection<Terminal> terminals = _receiver.selectAllTerminals().stream()
					.filter(terminal -> !terminal.hasActivity())
					.collect(Collectors.toList());
		_display.popup(terminals);
	}  
}
