package prr.app.terminal;

import prr.Network;
import prr.terminals.Terminal;
import prr.exceptions.TerminalNotFoundException;
import prr.exceptions.FriendNotFoundException;
import prr.app.exceptions.UnknownTerminalKeyException;
import pt.tecnico.uilib.menus.CommandException;

/**
 * Remove friend.
 */
class DoRemoveFriend extends TerminalCommand {

	DoRemoveFriend(Network context, Terminal terminal) {
		super(Label.REMOVE_FRIEND, context, terminal);
		addStringField("terminalKey", Prompt.terminalKey());
	}

	@Override
	protected final void execute() throws CommandException {
        try {
			_receiver.removeFriend(stringField("terminalKey"), _network);
		}
		catch (TerminalNotFoundException e) { throw new UnknownTerminalKeyException(e.getKey()); }
		catch (FriendNotFoundException e) { /* command produces no effect */ }
	}
}
