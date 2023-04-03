package prr.app.lookups;

import prr.Network;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import prr.client.Client;
import java.util.Collection;
import java.util.stream.*;
import java.util.Comparator;

/**
 * Show clients with positive balance.
 */
class DoShowClientsWithoutDebts extends Command<Network> {

	DoShowClientsWithoutDebts(Network receiver) {
		super(Label.SHOW_CLIENTS_WITHOUT_DEBTS, receiver);
	}

	@Override
	protected final void execute() throws CommandException {
        Collection<Client> clients = _receiver.selectAllClients().stream()
					.filter(c -> c.getDebts() == 0)
					.sorted(Comparator.comparing(c -> c.getClientId(), String.CASE_INSENSITIVE_ORDER)) 
					.collect(Collectors.toList());
		_display.popup(clients); 
	}
}
