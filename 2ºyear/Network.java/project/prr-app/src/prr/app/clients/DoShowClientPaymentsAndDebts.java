package prr.app.clients;

import prr.Network;
import prr.app.exceptions.UnknownClientKeyException;
import prr.exceptions.ClientNotFoundException;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

/**
 * Show the payments and debts of a client.
 */
class DoShowClientPaymentsAndDebts extends Command<Network> {

	DoShowClientPaymentsAndDebts(Network receiver) {
		super(Label.SHOW_CLIENT_BALANCE, receiver);
		addStringField("key", Prompt.key());
	}

	@Override
	protected final void execute() throws CommandException {
		try {
			long payments = Math.round(_receiver.fetchClientPayments(stringField("key")));
			long debts = Math.round(_receiver.fetchClientDebts(stringField("key")));
			_display.popup(Message.clientPaymentsAndDebts(stringField("key"), payments, debts)); 
		}
		catch (ClientNotFoundException e) { throw new UnknownClientKeyException(e.getKey()); }
	}	
}
