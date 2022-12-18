package prr.app.lookups;

import prr.Network;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import java.util.Collection;
import prr.client.Client;
import java.util.Comparator;
import java.util.stream.*;

/**
 * Show clients with negative balance.
 */
class DoShowClientsWithDebts extends Command<Network> {

	DoShowClientsWithDebts(Network receiver) {
		super(Label.SHOW_CLIENTS_WITH_DEBTS, receiver);
	}

	@Override
	protected final void execute() throws CommandException {
		Collection<Client> clients = _receiver.selectAllClients().stream()
					.filter(c -> c.getDebts() > 0)
					.sorted(Comparator.comparing(Client::getDebts).reversed().thenComparing(Client::getClientId))
					.collect(Collectors.toList());

		_display.popup(clients); 
	}
}
