import java.util.HashMap;
import java.util.Set;

/**
 * 
 * 
 * 
 * @author Philipp Fonteyn (MS11F010) and Saurabh Baghel (CS12D003)
 * @version 0.1 - 17. March 2012
 * @created 17. March 2012
 */
public class CaseSet {

	HashMap<String, Case> cases = new HashMap<String, Case>();

	public CaseSet() {

	}

	// Adding, deleting etc.

	public void addCase(Case c) {
		cases.put(c.getID(), c);
	}

	public void deleteCase(Case c) {
		cases.remove(c).getID();

	}

	public boolean hasCase(Case c) {
		return cases.containsKey(c.getID());
	}

	public boolean hasCase(String s) {
		return cases.containsKey(s);
	}

	public Case getCase(String key) {
		return cases.get(key);
	}
	
	public Set<String> getIDs() {
		return cases.keySet();
	}

	// Arithmetics:

	/**
	 * The common set operation Intersection
	 * @param set1
	 * @param set2
	 * @return intersection of both sets
	 */
	public static CaseSet intersect(CaseSet set1, CaseSet set2) {
		CaseSet intersection = new CaseSet();
		for (String key : set1.cases.keySet()) {
			if (set2.hasCase(key)) {
				intersection.addCase( set1.getCase(key) );
			}

		}
		return intersection;
	}

	/**
	 * The common set operation Union
	 * @param set1
	 * @param set2
	 * @return union of both sets
	 */
	public static CaseSet union(CaseSet set1, CaseSet set2) {
		CaseSet union = new CaseSet();
		for (String key : set1.cases.keySet()) {
			union.addCase( set1.getCase(key) );
		}
		for (String key : set2.cases.keySet()) {
			union.addCase( set2.getCase(key) );
		}		
		return union;
	}

	public void print() {
		System.out.println(" CaseSet{");
		for (String key : cases.keySet()) {
			System.out.print(key + " ");
		}
		System.out.println("}");
	}

}