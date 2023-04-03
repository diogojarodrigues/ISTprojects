package prr.app.terminal;

import prr.Network;
import prr.terminals.Terminal;
import pt.tecnico.uilib.menus.CommandException;
import prr.exceptions.SameTerminalStateException;
import prr.exceptions.TerminalIsBusyException;

/**
 * Turn on the terminal.
 */
class DoTurnOnTerminal extends TerminalCommand {

	DoTurnOnTerminal(Network context, Terminal terminal) {
		super(Label.POWER_ON, context, terminal);
	}

	@Override
	protected final void execute() throws CommandException {
        try { _receiver.turnOn(_network); }
		catch (SameTerminalStateException e) { _display.popup(Message.alreadyOn()); }
		catch (TerminalIsBusyException e) { /* command produces no effect */}
	}
}
