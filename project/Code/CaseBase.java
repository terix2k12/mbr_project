import java.util.ArrayList;
import java.util.List;

import lessons.Lesson;

/**
 * 
 * 
 * 
 * @author Philipp Fonteyn (MS11F010) and Saurabh Baghel (CS12D003)
 * @version 0.2 - 19. March 2012
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
	
	public CaseSet getRetrievalSpace(Lesson c){
		CaseSet retrievalSpace = new CaseSet();
		for(String key : allCases.getIDs()){
			Case x = allCases.getCase(key);
			if( x.canBeRetrievedFor(c) ){
				retrievalSpace.addCase(x);
			}
		}
		return retrievalSpace;
	}
	
	public CaseSet getAdaptionSpace(Lesson c){
		CaseSet adaptionSpace = new CaseSet();
		for(String key : allCases.getIDs()){
			Case x = allCases.getCase(key);
			if( x.canBeAdaptedFor(c) ){
				adaptionSpace.addCase(x);
			}
		}
		return adaptionSpace; 
	}
	
	public boolean solves(Case c, Lesson t){
		return CaseSet.intersect(getAdaptionSpace(t), getRetrievalSpace(t)).hasCase(c);
	}
	
	public CaseSet getCoverageSet(Case c){
		CaseSet coverageSet = new CaseSet();
		for(String key : allCases.getIDs()){
			Case x = allCases.getCase(key);
			if ( solves(c, x.getLesson() ) ){
				coverageSet.addCase(x);
			}	
		}
		return coverageSet;
	}

	public boolean sharedCoverage(Case c1, Case c2){
		return CaseSet.intersect(getRelatedSet(c1), getRelatedSet(c1)).hasSize(1);
	}
	
	public CaseSet getCompetenceGroup(Case c){
		CaseSet competenceGroup = new CaseSet();
		
		// Each case that shares coverage with c:
		for(String key : allCases.getIDs()){
			Case x = allCases.getCase(key);
			if ( sharedCoverage(c, x) ){
				competenceGroup.addCase(x);
			}
		}
		
		// Remove all cases that share coverage with smbd else:
		CaseSet cut = CaseSet.difference(allCases, competenceGroup);
		for(String key : cut.getIDs() ){
			Case x = cut.getCase(key);
			if( sharedCoverage(c, x) ){
				competenceGroup.remove(x);
			}			
		}	
		
		return competenceGroup;
	}
	
	public CaseSet getReachabilitySet(Case c){
		CaseSet reachabilitySet = new CaseSet();
		for(String key : allCases.getIDs()){
			Case x = allCases.getCase(key);
			if ( solves(x, c.getLesson() ) ){
				reachabilitySet.addCase(x);
			}	
		}
		return reachabilitySet;
	}
	
	public CaseSet getRelatedSet(Case c){
		return CaseSet.union(getCoverageSet(c), getReachabilitySet(c));
	}

}
