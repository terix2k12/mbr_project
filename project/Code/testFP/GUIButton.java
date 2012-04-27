package jcolibri.test.testFP;

import java.awt.Button;
import java.awt.Event;

import jcolibri.cbrcore.CBRQuery;
import jcolibri.exception.ExecutionException;

class GUIButton extends Button {

	/**
	 * 
	 */
	private static final long serialVersionUID = 578018102979147565L;
	
	GUIInterface gui;
	boolean mode;

	public GUIButton(String txt, boolean mode, GUIInterface gui) {
		this.gui = gui;
		this.mode = mode;
		setLabel(txt);
	}

	@Override
	public boolean action(Event evt, Object what) {
		System.out.println("button is clicked...");

		if (mode) {
			System.out.println("gui Button issues a query");
			CBRQuery query = gui.createAQuery();
			// Run a cycle with the query
			try {
				gui.test.cycle(query);
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("gui Button halts ");
			gui.kill();
		}

		return super.action(evt, what);
	}
}
