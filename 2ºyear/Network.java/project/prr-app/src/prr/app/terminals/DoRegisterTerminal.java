package prr.app.terminals;

import prr.Network;
import prr.app.exceptions.DuplicateTerminalKeyException;
import prr.app.exceptions.InvalidTerminalKeyException;
import prr.app.exceptions.UnknownClientKeyException;
import prr.app.exceptions.UnknownClientKeyException;
import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;
import pt.tecnico.uilib.forms.Form;
import prr.exceptions.DuplicateTerminalIdException;
import prr.exceptions.InvalidTerminalIdException;
import prr.exceptions.ClientNotFoundException;

import prr.exceptions.UnrecognizedEntryException;

/**
 * Register terminal.
 */
class DoRegisterTerminal extends Command<Network> {

	DoRegisterTerminal(Network receiver) {
		super(Label.REGISTER_TERMINAL, receiver);

		addStringField("terminalKey", Prompt.terminalKey());
		addOptionField("terminalType", Prompt.terminalType(), "FANCY", "BASIC");
		addStringField("clientKey", Prompt.clientKey());
	}

	@Override
	protected final void execute() throws CommandException {
    	try {
			_receiver.registerTerminal(optionField("terminalType"), stringField("terminalKey"), stringField("clientKey"), "IDLE");
		}
		catch (ClientNotFoundException e) { throw new UnknownClientKeyException(e.getKey()); }
		catch (InvalidTerminalIdException e ) { throw new InvalidTerminalKeyException(e.getKey()); }
		catch (DuplicateTerminalIdException e) { throw new DuplicateTerminalKeyException(e.getKey()); }
		catch (UnrecognizedEntryException e) { e.printStackTrace(); }
	}
}
