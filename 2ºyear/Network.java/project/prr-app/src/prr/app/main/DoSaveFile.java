package prr.app.main;

import java.io.FileNotFoundException;
import java.io.IOException;

import prr.exceptions.*;

import prr.NetworkManager;
import pt.tecnico.uilib.forms.Form;
import pt.tecnico.uilib.menus.Command;

/**
 * Command to save a file.
 */
class DoSaveFile extends Command<NetworkManager> {

	DoSaveFile(NetworkManager receiver) {
		super(Label.SAVE_FILE, receiver);
	}

	@Override
	protected final void execute() {
		try {
			_receiver.save();
		}
		catch (MissingFileAssociationException e) { 
			String fileName = Form.requestString(Prompt.newSaveAs());
			try { _receiver.saveAs(fileName); }
			catch (MissingFileAssociationException e2) { e2.printStackTrace(); }
			catch (FileNotFoundException e1) { e1.printStackTrace(); }
			catch (IOException e1) { e1.printStackTrace(); }  
		}
		catch (FileNotFoundException e ) {e.printStackTrace(); }
		
		catch (IOException e) { e.printStackTrace(); } 
		
	}
}
