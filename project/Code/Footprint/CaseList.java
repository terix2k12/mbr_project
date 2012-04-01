package jcolibri.method.retrieve.Footprint;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jcolibri.cbrcore.CBRCase;

public class CaseList implements Iterable<CBRCase>{
	
	private static int globalid = 0;
	private int id;
	
	private CaseList caselist;
	
	public CaseList(){
		id = globalid;
		globalid++;
		
		caselist = new CaseList();
	}
	
	public int getID(){
		return id;
	}

	public void add(CBRCase _case) {
		caselist.add(_case);		
	}
	
	private CaseList get(){
		return caselist;		
	}
	
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
		for (CBRCase case1 : list1.get()) {
			if (!union.contains(case1)) {
				union.add(case1);
			}
		}
		for (CBRCase case2 : list2.get()) {
			if (!union.contains(case2)) {
				union.add(case2);
			}
		}
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
			if (!list2.contains(case1)) {
				intersection.add(case1);
			}
		}
		for (CBRCase case2 : list2.caselist) {
			if (!list1.contains(case2)) {
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

}
