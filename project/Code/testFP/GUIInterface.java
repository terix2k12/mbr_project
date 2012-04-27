package jcolibri.test.testFP;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Event;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.util.Collection;

import jcolibri.cbrcore.CBRCase;
import jcolibri.cbrcore.CBRQuery;
import jcolibri.test.test1.TravelDescription;

class GUIInterface extends Frame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7162151001848706466L;
	
	GUIButton endButton;
	GUIButton anotherButton;
	TestFP test;

	private static Choice travelChoice;
	private static Choice accoChoice;
	private static Choice holiTypChoice;
	private static TextArea answer;
	private static Choice seasonChoice;
	private static TextField numPersField;
	private static TextField priceField;

	
	public GUIInterface(TestFP test) {
		this.test = test;
		// create frame
		setTitle("Simple Travel Finder");
		setLayout(new BorderLayout());
		setBackground(Color.lightGray);

		// creating labels
		Label accomodationLabel = new Label("Accomodation:", Label.CENTER);
		Label travelLabel = new Label("Vehicle:", Label.CENTER);
		//Label activityLabel = new Label("Activity:", Label.CENTER);
		Label priceLabel = new Label("Price:", Label.CENTER);
		Label durationLabel = new Label("Season:", Label.CENTER);
		Label holidaytypeLabel = new Label("Holiday type:");
		Label numberofPersLabel = new Label("# of Pers:");

		// creating editables
		seasonChoice = new Choice();
		seasonChoice.add("April");
		seasonChoice.add("Mai");
		seasonChoice.add("Mai");
		seasonChoice.add("June");
		seasonChoice.add("July");
		seasonChoice.add("August");
		seasonChoice.add("September");
		seasonChoice.add("October");
		
		numPersField = new TextField();
		numPersField.setText("2");
		priceField = new TextField();
		priceField.setText("700");
		accoChoice = new Choice();
		//accoChoice.add("n/a");
		accoChoice.add("ThreeStars");
		accoChoice.add("TwoStars");
		accoChoice.add("FourStars");
		accoChoice.add("HolidayFlat");
		
		
		holiTypChoice = new Choice();
		//holiTypChoice.add("n/a");
		holiTypChoice.add("Bathing");
		holiTypChoice.add("Active");
		holiTypChoice.add("Wandering");
		holiTypChoice.add("Recreation");
		
		travelChoice = new Choice();
		travelChoice.add("Plane");
		travelChoice.add("Coach");
		travelChoice.add("Car");
		
		Panel p1 = new Panel();
		p1.setLayout( new GridLayout(2,6) );
		
		//adding labels
		p1.add(accomodationLabel);
		p1.add(travelLabel);
		p1.add(durationLabel);
		p1.add(holidaytypeLabel);
		p1.add(numberofPersLabel);
		p1.add(priceLabel);		
		
		// adding editables
		p1.add(accoChoice);	
		p1.add(travelChoice);
		p1.add(seasonChoice);
		p1.add(holiTypChoice);
		p1.add(numPersField);
		p1.add(priceField);
		
		
		// add panel!
		add("North", p1);
		
		
		// add answer output
		answer = new TextArea();
		answer.setText("Loading Database\n Building FootPrintset\n Please wait!");
		add("Center", answer);

		// add a panel with two buttons...
		endButton = new GUIButton("Quit", false, this);
		anotherButton = new GUIButton("Query", true, this);
		anotherButton.disable();
		Panel p = new Panel();
		p.add(anotherButton);
		p.add(endButton);
        add("South", p);

		
		pack();
		resize(1200, 300);
		show();
	}
	
	public void issueReady(){
		anotherButton.enable();
		answer.setText("Please specify your query and then press start!");
	}

	public void setSearching(){
		String ans = "Solution:\n";
		answer.setText("Searching for a solution... please wait!");
	}
	
	public void setAnswer(Collection<CBRCase> eval){
		String ans = "Solution:\n";
		for (CBRCase nse : eval){
			ans += nse.toString();
			ans += '\n';
		}
		answer.setText(ans);
	}

	@Override
	public boolean handleEvent(Event evt) {
	
		if (evt.id == Event.WINDOW_DESTROY){
			kill();
		}
		
		return super.handleEvent(evt);
	}
	
	public static CBRQuery createAQuery() {
		// Configure the query. Queries only have description.
		TravelDescription queryDesc = new TravelDescription();
		
		String acc = accoChoice.getItem(accoChoice.getSelectedIndex());
		queryDesc.setAccomodation(acc);
		
		String trs = travelChoice.getItem(travelChoice.getSelectedIndex());
		queryDesc.setTransportation(trs);
		
		String drs = seasonChoice.getItem(seasonChoice.getSelectedIndex());
		queryDesc.setSeason(drs);
		
		String htc = holiTypChoice.getItem(holiTypChoice.getSelectedIndex());
		queryDesc.setHolidayType(htc);
		
		String nps = numPersField.getText();
		queryDesc.setNumberOfPersons(Integer.parseInt(nps));
		
		String prc = priceField.getText();
		queryDesc.setPrice(Integer.parseInt(prc));

		CBRQuery query = new CBRQuery();
		query.setDescription(queryDesc);
		return query;
	}
	
	public void kill() {
		disable();
		System.out.println("GUI halt");
	}

	public void idle() {
		System.out.println("GUI will idle now...:");
		while (isEnabled()) {
			//System.out.println("idle");
		}
		dispose();
		System.out.println("GUI disposed");
	}
}
