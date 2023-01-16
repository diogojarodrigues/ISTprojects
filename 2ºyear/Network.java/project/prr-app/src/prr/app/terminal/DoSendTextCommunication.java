package prr.app.terminal;

import prr.Network;
import prr.terminals.Terminal;
import prr.app.exceptions.UnknownTerminalKeyException;
import prr.exceptions.*;
import pt.tecnico.uilib.forms.Form;
import pt.tecnico.uilib.menus.CommandException;

/**
 * Command for sending a text communication.
 */
class DoSendTextCommunication extends TerminalCommand {

	DoSendTextCommunication(Network context, Terminal terminal) {
		super(Label.SEND_TEXT_COMMUNICATION, context, terminal, receiver -> receiver.canStartCommunication());
		addStringField("terminalKey", Prompt.terminalKey());
		addStringField("textMessage", Prompt.textMessage());
	}

	@Override
	protected final void execute() throws CommandException {
		try {
			_receiver.sendTextCommunication(stringField("terminalKey"), stringField("textMessage"), _network);
		}
		catch (TerminalNotFoundException e) { throw new UnknownTerminalKeyException(e.getKey()); }
		catch (DestinationIsOffException e) { _display.popup(Message.destinationIsOff(e.getKey())); }
		
	}
} 
