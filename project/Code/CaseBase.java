import java.util.ArrayList;
import java.util.List;

/**
 * 
 * 
 * 
 * @author Philipp Fonteyn (MS11F010) and Saurabh Baghel (CS12D003)
 * @version 0.1 - 17. March 2012
 * @created 17. March 2012
 */
public abstract class CaseBase {
	
	CaseSet allCases = new CaseSet();
	List<CaseSet> competenceGroups = new ArrayList<CaseSet>();

	public void add(Case c) {
		allCases.addCase(c);		
	}

	public void delete(Case c) {
		allCases.deleteCase(c);		
	}
	
	public abstract CaseSet retrieve(Problem t);
	
	public abstract void performMaintenance();
	
	public CaseSet getRetrievalSpace(Case c){
		CaseSet retrievalSpace = new CaseSet();
		for(String key : allCases.getIDs()){
			Case x = allCases.getCase(key);
			// FIXME
		}
		return retrievalSpace;
	}
	
	public CaseSet getAdaptionSpace(Case c){
		CaseSet set = null;
		return set; 
	}
	
	public CaseSet getSolveSpace(Case c){
		return CaseSet.intersect(getAdaptionSpace(c), getRetrievalSpace(c));
	}
	
	public CaseSet getCoverageSet(Case c){
		return null;
	}
	
	public CaseSet getCompetenceGroup(Case c){
		return null;
	}
	
	public CaseSet getReachabilitySet(Case c){
		return null;
	}
	
	public CaseSet getRelatedSet(Case c){
		return CaseSet.union(getCoverageSet(c), getReachabilitySet(c));
	}

}
