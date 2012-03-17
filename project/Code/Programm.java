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
public class Programm {

	public static void main(String[] args) {
	
		// Build our CaseBase:
		CaseBase casebase = new FootPrintBased_CaseBase();

		// Add some Cases;
		Description desc1 = new Description();
		Lesson less1 = new Lesson();
		Case case1 = new Case("1", desc1, less1);
		
		casebase.add(case1);
		
		// Initialize the CaseBase:
		casebase.performMaintenance();

		// Visualize the CaseBase:
		//casebase.print();
		
		// Create a Problem:
		Problem problem = new Problem();
		
		// See what our CaseBase gives us:
		casebase.retrieve(problem).print();

	}

}
