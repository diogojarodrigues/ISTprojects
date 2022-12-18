package prr.app.terminal;

import prr.Network;
import prr.app.exceptions.UnknownTerminalKeyException;
import prr.exceptions.FriendAlreadyAddedException;
import prr.exceptions.TerminalNotFoundException;
import prr.exceptions.CannotBefriendSelfException;
import prr.terminals.Terminal;
import pt.tecnico.uilib.menus.CommandException;

/**
 * Add a friend.
 */
class DoAddFriend extends TerminalCommand {

	DoAddFriend(Network context, Terminal terminal) {
		super(Label.ADD_FRIEND, context, terminal);
		addStringField("terminalKey", Prompt.terminalKey());
	}

	@Override
	protected final void execute() throws CommandException {
		try {
			_receiver.addFriend(stringField("terminalKey"), _network);
		}
		catch (TerminalNotFoundException e) { throw new UnknownTerminalKeyException(e.getKey()); } 
		catch (CannotBefriendSelfException | FriendAlreadyAddedException e) { /* do nothing */ }
	}
}
