package prr.app.terminal;

import prr.Network;
import prr.app.exceptions.UnknownTerminalKeyException;
import prr.exceptions.*;
import prr.terminals.Terminal;
import pt.tecnico.uilib.forms.Form;
import pt.tecnico.uilib.menus.CommandException;

/**
 * Command for starting communication.
 */
class DoStartInteractiveCommunication extends TerminalCommand {

	DoStartInteractiveCommunication(Network context, Terminal terminal) {
		super(Label.START_INTERACTIVE_COMMUNICATION, context, terminal, receiver -> receiver.canStartCommunication());
		addStringField("terminalKey", Prompt.terminalKey());
		addOptionField("commType", Prompt.commType(), "VOICE", "VIDEO");
	}

	@Override
	protected final void execute() throws CommandException {
        try {
			_receiver.startInteractiveCommunication(stringField("terminalKey"), stringField("commType"), _network);
		}	
		catch (TerminalNotFoundException e) { throw new UnknownTerminalKeyException(e.getKey()); }
		catch (UnsupportedAtOriginException e) { _display.popup(Message.unsupportedAtOrigin(e.getKey(), e.getType())); }
		catch (UnsupportedAtDestinationException e) { _display.popup(Message.unsupportedAtDestination(e.getKey(), e.getType())); }
		catch (DestinationIsOffException e) { _display.popup(Message.destinationIsOff(e.getKey())); }
		catch (DestinationIsBusyException e) { _display.popup(Message.destinationIsBusy(e.getKey())); }
		catch (DestinationIsSilentException e) { _display.popup(Message.destinationIsSilent(e.getKey())); }
		catch (UnrecognizedEntryException e) { /* empty on purpose */ }
	}
}
