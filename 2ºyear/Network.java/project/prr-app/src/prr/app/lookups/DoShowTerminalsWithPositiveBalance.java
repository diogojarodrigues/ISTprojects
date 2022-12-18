package prr.app.lookups;

import prr.Network;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import prr.terminals.Terminal;
import java.util.stream.*;
import java.util.Collection;

/**
 * Show terminals with positive balance.
 */
class DoShowTerminalsWithPositiveBalance extends Command<Network> {

	DoShowTerminalsWithPositiveBalance(Network receiver) {
		super(Label.SHOW_TERMINALS_WITH_POSITIVE_BALANCE, receiver);
	}

	@Override
	protected final void execute() throws CommandException {
        Collection<Terminal> terminals = _receiver.selectAllTerminals().stream()
					.filter(terminal -> terminal.getBalance() > 0)
					.collect(Collectors.toList()); 
		_display.popup(terminals);

	}
}
