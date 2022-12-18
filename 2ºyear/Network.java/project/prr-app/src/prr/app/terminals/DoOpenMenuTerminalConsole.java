package prr.app.terminals;

import pt.tecnico.uilib.menus.Command;
import pt.tecnico.uilib.menus.CommandException;

import prr.Network;
import prr.app.terminal.*;
import prr.terminals.Terminal;
import prr.exceptions.ClientNotFoundException;
import prr.exceptions.TerminalNotFoundException;
import prr.app.exceptions.UnknownClientKeyException;
import prr.app.exceptions.UnknownTerminalKeyException;
import prr.app.terminal.Menu;

/**
 * Open a specific terminal's menu.
 */
class DoOpenMenuTerminalConsole extends Command<Network> {

	DoOpenMenuTerminalConsole(Network receiver) {
		super(Label.OPEN_MENU_TERMINAL, receiver);
		addStringField("terminalKey", Prompt.terminalKey());
	}

	@Override
	protected final void execute() throws CommandException {
		Terminal t;
		try {
			t = _receiver.findTerminal(stringField("terminalKey"));
		}
		catch (TerminalNotFoundException e) { throw new UnknownTerminalKeyException(e.getKey()); }
		
		(new prr.app.terminal.Menu(_receiver, t)).open();
	}
}
