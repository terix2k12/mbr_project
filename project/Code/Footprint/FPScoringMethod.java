package jcolibri.method.retrieve.Footprint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import jcolibri.cbrcore.CBRCase;
import jcolibri.cbrcore.CBRQuery;
import jcolibri.method.retrieve.RetrievalResult;
import jcolibri.method.retrieve.NNretrieval.NNConfig;
import jcolibri.method.retrieve.NNretrieval.similarity.GlobalSimilarityFunction;
import jcolibri.method.retrieve.selection.SelectCases;

public class FPScoringMethod {

	private static List<CBRCase> myFootprintSet = null;

	public static Collection<RetrievalResult> evaluateSimilarity(
			Collection<CBRCase> cases, CBRQuery query, NNConfig simConfig) {

		// inore the config for now

		// preparations build footprint set:

		// List<CBRCase> footprintSet = createFootprintSet();

		List<RetrievalResult> retrievedCases = new ArrayList<RetrievalResult>();

		// step1: search in all cases
		for (CBRCase cur_case : cases) {

			GlobalSimilarityFunction gsf = simConfig
					.getDescriptionSimFunction();
			Double rating = gsf.compute(cur_case.getDescription(), query
					.getDescription(), cur_case, query, simConfig);

			retrievedCases.add(new RetrievalResult(cur_case, rating));

		}

		java.util.Collections.sort(retrievedCases);
		return retrievedCases;
	}

	private static HashMap<CBRCase, List<CBRCase>> RetrievalSpace;
	private static HashMap<CBRCase, List<CBRCase>> AdaptationSpace;
	private static HashMap<CBRCase, List<CBRCase>> SolveSpaces;

	private static HashMap<CBRCase, List<CBRCase>> CoverageSet;
	private static HashMap<CBRCase, List<CBRCase>> ReachabilitySet;

	private static HashMap<CBRCase, List<CBRCase>> RelatedSet;

	public static List<CBRCase> createFootprintSet(
			Collection<CBRCase> allCases, NNConfig simConfig) {
		if (myFootprintSet == null) {
			myFootprintSet = new ArrayList<CBRCase>();

			// For all cases:
			for (CBRCase cur_case : allCases) {

				// Create all RetrievalSpaces
				Collection<RetrievalResult> retrievalResults = new ArrayList<RetrievalResult>();
				for (CBRCase cmp_case : allCases) {

					Double rating = 1.0;
					retrievalResults.add(new RetrievalResult(cur_case, rating));
				}
				java.util.Collections
						.sort((List<RetrievalResult>) retrievalResults);
				retrievalResults = SelectCases
						.selectTopKRR(retrievalResults, 5);

				List<CBRCase> retrievedCases = new ArrayList<CBRCase>();
				for (RetrievalResult res : retrievalResults) {
					retrievedCases.add(res.get_case());
				}
				RetrievalSpace.put(cur_case, retrievedCases);

				// Create AdaptationSpaces:
				List<CBRCase> adaptableCases = new ArrayList<CBRCase>();
				AdaptationSpace.put(cur_case, retrievedCases); // FIXME
																// calculate

				// Create SolveSpaces:
				SolveSpaces.put(cur_case, retrievedCases); // FIXME intersection

				// Create CoverageSet:
				List<CBRCase> coveredCases = new ArrayList<CBRCase>();
				for (CBRCase cmp_case : allCases) {
					if (SolveSpaces.get(cur_case).contains(cmp_case)) {
						coveredCases.add(cmp_case);
					}
				}
				CoverageSet.put(cur_case, coveredCases);

				// Create ReachabilitySet:
				List<CBRCase> reachableCases = new ArrayList<CBRCase>();
				for (CBRCase cmp_case : allCases) {
					if (SolveSpaces.get(cmp_case).contains(cur_case)) {
						reachableCases.add(cmp_case);
					}
				}
				ReachabilitySet.put(cur_case, reachableCases);

				// Create RelatedSet
				List<CBRCase> union = union(ReachabilitySet.get(cur_case),
						CoverageSet.get(cur_case));
				RelatedSet.put(cur_case, union);
			}

			
			// create CompetenceGroups:
			List<List<CBRCase>> allCompetenceGroups = createCompetenceGroups(allCases);
			

		}
		return myFootprintSet;
	}
	
	private static Double relativeCoverage(CBRCase case1){
		return 0.0; // FIXME
	}
	
	private static List<List<CBRCase>> createCompetenceGroups(Collection<CBRCase> allCases){
		List<List<CBRCase>> allCompetenceGroups = new ArrayList<List<CBRCase>>();
		List<CBRCase> initialCompetenceGroup = (List<CBRCase>) allCases; // TODO
																			// maybe
																			// use
																			// a
																			// copy
																			// here
		allCompetenceGroups.add(initialCompetenceGroup);

		boolean completelySearched = false;
		while (!completelySearched) {
			for (CBRCase cur_case : initialCompetenceGroup) {
				for (CBRCase cur_case2 : allCases) {
					// if this case has sharedcoverage with others in the
					// casebase
					if (!haveSharedCoverage(cur_case, cur_case2)) {
						// remove case from initialCompetenceGroup
						initialCompetenceGroup.remove(cur_case);
						boolean noFriends = true;
						// sort it into an existing CompetenceGroup:
						for (List<CBRCase> cur_list : allCompetenceGroups) {
							for (CBRCase case3 : cur_list) {
								if (haveSharedCoverage(case3, cur_case)) {
									cur_list.add(cur_case);
									noFriends = false;
								}
							}
						}
						if (noFriends) {
							// otherwise create new
							List<CBRCase> newCompetenceGroup = new ArrayList<CBRCase>();
							newCompetenceGroup.add(cur_case);
							allCompetenceGroups.add(newCompetenceGroup);
						}

					}
				}
			}
		}
		
		// FIXME 
		
		return allCompetenceGroups;
	}

	private static boolean haveSharedCoverage(CBRCase case1, CBRCase case2) {
		List<CBRCase> temp = intersect(RelatedSet.get(case1), RelatedSet
				.get(case2));
		return (temp.size() > 0);
	}

	private static List<CBRCase> union(List<CBRCase> list1, List<CBRCase> list2) {
		// FIXME TODO
		return null;
	}

	private static List<CBRCase> intersect(List<CBRCase> list1,
			List<CBRCase> list2) {
		// FIXME TODO
		return null;
	}

}
