import descriptions.Description;
import lessons.Lesson;

/**
 * 
 * 
 * 
 * @author Philipp Fonteyn (MS11F010) and Saurabh Baghel (CS12D003)
 * @version 0.1 - 17. March 2012
 * @created 17. March 2012
 */
public class Case {
	
	private String id;
	private Lesson lesson;
	private Description description;
	
	public Case(String i, Description desc, Lesson less){
		id = i;
		description = desc;
		lesson = less;		
	}

	public String getID() {
		return id;
	}

	public Description getDescription() {
		return description;
	}
	
	

}
