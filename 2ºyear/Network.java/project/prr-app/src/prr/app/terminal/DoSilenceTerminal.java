package prr.app.terminal;

import prr.Network;
import prr.terminals.Terminal;
import pt.tecnico.uilib.menus.CommandException;
import prr.exceptions.SameTerminalStateException;
import prr.exceptions.TerminalIsBusyException;

/**
 * Silence the terminal.
 */
class DoSilenceTerminal extends TerminalCommand {

	DoSilenceTerminal(Network context, Terminal terminal) {
		super(Label.MUTE_TERMINAL, context, terminal);
	}

	@Override
	protected final void execute() throws CommandException {
        try { _receiver.turnSilent(_network); }
		catch (SameTerminalStateException e) { _display.popup(Message.alreadySilent()); }
		catch (TerminalIsBusyException e) { /* command produces no effect */ }
	}
}
