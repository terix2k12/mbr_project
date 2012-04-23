package jcolibri.method.retrieve.Footprint;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jcolibri.cbrcore.CBRCase;

/**
 * Wrapper class for a list of cases...
 * provides ids for identity and enable more readable code...
 * @author philipp
 *
 */
public class CaseList implements Iterable<CBRCase>{
	
	private static int globalid = 0;
	private int id;
	
	private List<CBRCase> caselist;
	
	public CaseList(){
		id = globalid;
		globalid++;
		
		caselist = new ArrayList<CBRCase>();
	}
	
	public int getID(){
		return id;
	}

	public void add(CBRCase _case) {
		if(caselist.contains(_case)){
			//System.err.println("Warning, trying to add a case twice.");
		}else{
			caselist.add(_case);
		}
	}
	
	//private CaseList get(){
	//	return caselist;		
	//}
	
	public boolean contains(CBRCase _case){
		return caselist.contains(_case);
	}
	
	/**
	 * Set operation union
	 * 
	 * @param list1
	 * @param list2
	 * @return A new list containing cases in either list1 or list2
	 */
	public static CaseList union(CaseList list1, CaseList list2) {
		CaseList union = new CaseList();
//		System.out.println("Union for");
//		System.out.println(list1);
//		System.out.println("and:");
//		System.out.println(list2);
//		System.out.println("Union: Union init:");
//		System.out.println(union);
		for (CBRCase case1 : list1.caselist) {
			if (!union.contains(case1)) {
				union.add(case1);
			}
		}
		for (CBRCase case2 : list2.caselist) {
			if (!union.contains(case2)) {
				union.add(case2);
			}
		}
//		System.out.println("Union is:");
//		System.out.println(union);
		return union;
	}

	/**
	 * Set operation intersection
	 * 
	 * @param list1
	 * @param list2
	 * @return A new list containing cases in both list1 and list2
	 */
	public static CaseList intersect(CaseList list1, CaseList list2) {
		CaseList intersection = new CaseList();
		for (CBRCase case1 : list1.caselist) {
			if (list2.contains(case1)) {
				intersection.add(case1);
			}
		}
		for (CBRCase case2 : list2.caselist) {
			if (list1.contains(case2)) {
				intersection.add(case2);
			}
		}
		return intersection;
	}

	@Override
	public Iterator<CBRCase> iterator() {
		return caselist.iterator();
	}

	public int size() {
		return caselist.size();
	}

	public CBRCase get(int i) {
		return caselist.get(i);		
	}

	/**
	 * Swaps the elements in the caselist
	 * @param case1
	 * @param case2
	 */
	public void swap(CBRCase case1, CBRCase case2) {
		int id1 = caselist.indexOf(case1);
		int id2 = caselist.indexOf(case2);
		caselist.set(id2, case1);
		caselist.set(id1, case2);		
	}
	
	public String toString(){
		String string = "";
		string += "CG("+getID()+")="+size()+"\n";
		for(CBRCase _case : caselist){
			string += _case + "\n";
		}
		return string;
	}

}
