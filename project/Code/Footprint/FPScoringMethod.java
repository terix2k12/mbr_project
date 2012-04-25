package jcolibri.method.retrieve.Footprint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import sun.security.action.GetLongAction;

import jcolibri.cbrcore.Attribute;
import jcolibri.cbrcore.CBRCase;
import jcolibri.cbrcore.CBRQuery;
import jcolibri.cbrcore.CaseComponent;
import jcolibri.method.retrieve.RetrievalResult;
import jcolibri.method.retrieve.NNretrieval.NNConfig;
import jcolibri.method.retrieve.NNretrieval.NNScoringMethod;
import jcolibri.method.retrieve.NNretrieval.similarity.GlobalSimilarityFunction;
import jcolibri.method.retrieve.NNretrieval.similarity.global.Average;
import jcolibri.method.retrieve.NNretrieval.similarity.local.Equal;
import jcolibri.method.retrieve.NNretrieval.similarity.local.Interval;
import jcolibri.method.retrieve.selection.SelectCases;
import jcolibri.test.test1.TravelDescription;
import jcolibri.util.ProgressController;

public class FPScoringMethod {

	private static CaseList myFootprintSet = null;

	private static HashMap<CBRCase, CaseList> RetrievalSpace;
	private static HashMap<CBRCase, CaseList> AdaptationSpace;
	private static HashMap<CBRCase, CaseList> SolveSpaces;
	private static HashMap<CBRCase, CaseList> CoverageSet;
	private static HashMap<CBRCase, CaseList> ReachabilitySet;
	private static HashMap<CBRCase, CaseList> RelatedSet;

	private static List<CaseList> allCompetenceGroups = null;
	private static HashMap<Integer, CaseList> allCompetenceGroupFootprints = new HashMap<Integer, CaseList>();

	/**
	 * This method is our central retrieve algorithm.
	 * 
	 * @param cases
	 *            all the cases which are used as casebase
	 * @param query
	 *            for which query we retrieve
	 * @param simConfig
	 *            TODO
	 * @return a list of {@link RetrievalResult}
	 */
	public static Collection<CBRCase> retrieveCases(CBRQuery query,
			NNConfig simConfig) {

		// Preparations: build footprint set:
		// TODO should only be done once for a !!specific!! CBRCase casebase!
		if (myFootprintSet == null) {
			System.out.println("DONT FORGET TO BUILD THE FPS FIRST");
		}

		// Debug the Footprintset:
		System.out.println("Number of CompetenceGroups: "
				+ allCompetenceGroups.size());
		System.out.println("Footprintsize: " + myFootprintSet.size());

		// Create an empty list:
		List<RetrievalResult> retrievedCases = new ArrayList<RetrievalResult>();

		// step1: search in whole footprint set for appropiate competencegroup
		// step1a: compute similarity of all cases in the footprint set
		for (CBRCase _case : myFootprintSet) {
			GlobalSimilarityFunction gsf = simConfig
					.getDescriptionSimFunction();
			Double rating = gsf.compute(_case.getDescription(), query
					.getDescription(), _case, query, simConfig);
			retrievedCases.add(new RetrievalResult(_case, rating));
		}
		// step1b: select toprated and fetch corresponding competencegroup:
		java.util.Collections.sort(retrievedCases);
		CBRCase topcase = (CBRCase) SelectCases.selectTopK(retrievedCases, 1)
				.toArray()[0]; // first only
		CaseList competenceGroup = null;
		for (CaseList cGroup : allCompetenceGroups) {
			if (cGroup.contains(topcase)) {
				competenceGroup = cGroup;
				break; // for 1 topcase there is only one competencegroup
			}
		}

		// step2: search in competencegroups
		retrievedCases = new ArrayList<RetrievalResult>();
		for (CBRCase _case : competenceGroup) {
			GlobalSimilarityFunction gsf = simConfig
					.getDescriptionSimFunction();
			Double rating = gsf.compute(_case.getDescription(), query
					.getDescription(), _case, query, simConfig);
			retrievedCases.add(new RetrievalResult(_case, rating));
		}

		// Sort and return retrievalresults:
		java.util.Collections.sort(retrievedCases);
		return SelectCases.selectTopK(retrievedCases, 5);
	}

	private static NNConfig getlocalConfig() {
		// First configure the KNN
		NNConfig simConfig = new NNConfig();
		// Set the average() global similarity function for the description of
		// the case
		simConfig.setDescriptionSimFunction(new Average());
		// The accomodation attribute uses the equal() local similarity function
		simConfig.addMapping(new Attribute("Accomodation",
				TravelDescription.class), new Equal());
		// For the duration attribute we are going to set its local similarity
		// function and the weight
		Attribute duration = new Attribute("Duration", TravelDescription.class);
		simConfig.addMapping(duration, new Interval(31));
		simConfig.setWeight(duration, 0.5);
		// HolidayType --> equal()
		simConfig.addMapping(new Attribute("HolidayType",
				TravelDescription.class), new Equal());
		// NumberOfPersons --> equal()
		simConfig.addMapping(new Attribute("NumberOfPersons",
				TravelDescription.class), new Equal());
		// Price --> InrecaLessIsBetter()
		simConfig.addMapping(new Attribute("Price", TravelDescription.class),
				new Interval(4000));

		return simConfig;
	}



	private Integer toKey(CBRCase case1, CBRCase case2) {
		return case1.hashCode() + case2.hashCode();
	}

	/**
	 * Determines the RetrievalSpace of a "_case" over "allCases".<br>
	 * <i>Hint:</i> Helper method for Footprint creation.
	 * 
	 * @param _case
	 * @param allCases
	 */
	private static void createRetrievalSpace(Collection<CBRCase> allCases,
			NNConfig numSimConfig) {
		System.out.println("creating RetrievalSpace of " + allCases.size()
				+ " cases... ");
		if (RetrievalSpace == null) {
			RetrievalSpace = new HashMap<CBRCase, CaseList>();
		}

		// prepare Config
		NNConfig simConfig = getlocalConfig();



		
		Thread ftbThread = new Thread( new FtbSearcher( allCases, true, RetrievalSpace, simConfig) );
		Thread btfThread = new Thread( new FtbSearcher( allCases, false, RetrievalSpace, simConfig) );
		ftbThread.start();
		btfThread.start();
		
		try {
			ftbThread.join();
			btfThread.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


//		for (CBRCase _case : allCases) {
//			System.out.println("	creating RetrievalSpace for " + _case);
//			List<RetrievalResult> res = new ArrayList<RetrievalResult>();
//			GlobalSimilarityFunction gsf = simConfig
//					.getDescriptionSimFunction();
//
//			// transform _case into query:
//			CBRQuery query = transformToQuery(_case);
//
//			for (CBRCase __case : allCases) {
//				double rating = gsf.compute(__case.getDescription(), query
//						.getDescription(), _case, query, simConfig);
//				RetrievalResult temp_result = new RetrievalResult(__case,
//						rating);
//
//				res.add(temp_result);
//			}
//			java.util.Collections.sort(res);
//			retrievalResults = res;
//
//			retrievalResults = (List<RetrievalResult>) SelectCases
//					.selectTopKRR(retrievalResults, 10);
//
//			// System.out.println("being retrieved");
//			CaseList retrievedCases = new CaseList();
//			for (RetrievalResult ress : retrievalResults) {
//				// System.out.println(res);
//				retrievedCases.add(ress.get_case());
//				retrievedCases.add(_case); // FIXME a case is always retrieved
//											// for itself...
//			}
//
//			System.out.println("RetrievalSpace for case:");
//			System.out.println(_case);
//			System.out.println(retrievedCases);
//			wait2();
//
//			RetrievalSpace.put(_case, retrievedCases);
//		}

		System.out.println("Done.");
	}

	/**
	 * Determines the AdaptionSpace of "_case" over "allCases"
	 * 
	 * @param case1
	 * @param allCases
	 */
	private static void createAdaptionSpace(Collection<CBRCase> allCases) {
		System.out.println("AdaptionSpace: creation - Start.");
		if (AdaptationSpace == null) {
			AdaptationSpace = new HashMap<CBRCase, CaseList>();
		}
		for (CBRCase _case : allCases) {
			CaseList retrievedCases = RetrievalSpace.get(_case);
			// TODO we just take the same set right now!!!!!!!

			CaseList adaptableCases = new CaseList();
			AdaptationSpace.put(_case, retrievedCases);
			// FIXME calculate!!
		}
		System.out.println("AdaptionSpace: creation - Done.");
	}

	/**
	 * Determines the SolveSpace of "_case" over "allCases"
	 * 
	 * @param case1
	 * @param allCases
	 */
	private static void createSolveSpace(Collection<CBRCase> allCases) {
		System.out.println("SolveSpace: Start ... ");
		if (SolveSpaces == null) {
			SolveSpaces = new HashMap<CBRCase, CaseList>();
		}
		for (CBRCase _case : allCases) {
			CaseList retrievedCases = RetrievalSpace.get(_case);
			// TODO we just take the same set right now!!!!!!!

			SolveSpaces.put(_case, retrievedCases); // FIXME intersection
		}
		System.out.println("SolveSpace: Done");
	}

	/**
	 * TODO javadoc
	 * 
	 * @param _case
	 * @param allCases
	 */
	private static void createCoverageSet(Collection<CBRCase> allCases) {
		System.out.println("CoverageSet creation - Start.");
		if (CoverageSet == null) {
			CoverageSet = new HashMap<CBRCase, CaseList>();
		}
		for (CBRCase _case : allCases) {

			// for all _case in casebase:
			System.out.print("  CoverageSet for: " + _case);
			CaseList coveredCases = new CaseList();

			for (CBRCase cmp_case : allCases) {
				// System.out.println("  Shall this case be in the SolveSpace?");
				// System.out.println(cmp_case);
				// System.out.println("  SolveSpace of case: "+SolveSpaces.get(_case)
				// );

				if (SolveSpaces.get(_case).contains(cmp_case)) {
					coveredCases.add(cmp_case);
				}
			}
			// System.out.println("CoverageSet:" + coveredCases );
			CoverageSet.put(_case, coveredCases);

		}
		System.out.println("CoverageSet creation - Done.");
		// wait2();
	}

	private static void wait2() {
		// just wait please...
		System.out.println("hi im waiting for you to read the debugg info....");
		String strPhone = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
		try {
			strPhone = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * TODO javadoc
	 * 
	 * @param case1
	 * @param allCases
	 */
	private static void createReachabilitySet(Collection<CBRCase> allCases) {
		System.out.print("creating ReachabilitySet(c) ... ");
		if (ReachabilitySet == null) {
			ReachabilitySet = new HashMap<CBRCase, CaseList>();
		}
		for (CBRCase _case : allCases) {
			CaseList reachableCases = new CaseList();
			for (CBRCase cmp_case : allCases) {
				if (SolveSpaces.get(cmp_case).contains(_case)) {
					reachableCases.add(cmp_case);
				}
			}
			ReachabilitySet.put(_case, reachableCases);
		}
		System.out.println("Done.");
	}

	/**
	 * Creates the RelatedSet for a "_case" and stores it.
	 * 
	 * @param _case
	 * @param allCases
	 */
	private static void createRelatedSet(Collection<CBRCase> allCases) {
		System.out.println("RelatedSet: creation - Start.");
		if (RelatedSet == null) {
			RelatedSet = new HashMap<CBRCase, CaseList>();
		}
		for (CBRCase _case : allCases) {
			CaseList coverageset = CoverageSet.get(_case);
			CaseList reachabilityset = ReachabilitySet.get(_case);
			CaseList union = CaseList.union(reachabilityset, coverageset);
			// Debuginfo:
			// System.out.println("RelatedSet: for case " + _case);
			// System.out.println("Coverageset:");
			// System.out.println(coverageset);
			// System.out.println("Reachabilityset:");
			// System.out.println(reachabilityset);
			// System.out.println("Union:");
			// System.out.println(union);
			// wait2();
			RelatedSet.put(_case, union);
		}
		System.out.println("Done.");
	}

	/**
	 * Returns the relativeCoverage of "_case"
	 * 
	 * @param _case
	 * @return
	 */
	private static Double relativeCoverage(CBRCase _case) {
		// System.out.println("Computing relativeCoverage:");
		Double relative = 0.0, addition;
		// Sum over cases from CoverageSet(c)
		CaseList coverage = CoverageSet.get(_case);
		// System.out.println("CoverageSet isSize " + coverage.size());
		for (CBRCase _iter : coverage) {
			//
			int rsize = ReachabilitySet.get(_iter).size();
			addition = 1.0 / rsize;
			// System.out.println("plus 1/" + rsize);
			relative += addition;
		}
		return relative;
	}

	private static boolean haveSharedCoverage(CBRCase case1, CBRCase case2) {
		CaseList set1 = RelatedSet.get(case1);
		CaseList set2 = RelatedSet.get(case2);
		// System.out.println("sharedcoverage??");
		// System.out.println("set1:");
		// System.out.println(set1);
		// System.out.println("set2:");
		// System.out.println(set2);

		CaseList temp = CaseList.intersect(set1, set2);

		// System.out.println("intersection:");
		// System.out.println(temp);
		// wait2();
		return (temp.size() > 0);
	}

	/**
	 * Methods sorts "_case" into the set of CompetenceGroups with the following
	 * two conditions:<br>
	 * 1) A case has friends within the group he is sorted into<br>
	 * 2) A case is not friend with any other case in the casebase except for
	 * grouppartners<br>
	 * Note: This is a helper method for construction of CompetenceGroups.
	 * 
	 * @param _case
	 */
	private static void sortIntoCompetenceGroups(CBRCase _case) {
		// System.out.println("Sorting into CGs...");
		// System.out.println(_case);

		CaseList sortIntoGroup = null;
		int likesGroups = 0;
		// Check for all lists
		for (CaseList group : allCompetenceGroups) {
			// System.out.println("Check group " + group.getID());
			// Does he like all members of the group?
			boolean likesEverybody = true;
			for (CBRCase partner : group) {
				if (!haveSharedCoverage(_case, partner)) {
					likesEverybody = false;
					break;
				}
			}
			if (likesEverybody) {
				// System.out.println("likes group "+group.getID());
				sortIntoGroup = group;
				likesGroups++;
			}
			if (likesGroups > 1) {
				break;
			}
		}
		if (likesGroups == 1) {
			// he only likes one group...
			// System.out.println("only likes "+sortIntoGroup.getID());
			sortIntoGroup.add(_case);

		} else { // likes no one or more than one ...
			// ... create a new CompetenceGroup.
			// System.out.println("new group because he likes "+likesGroups);
			CaseList newGroup = new CaseList();
			newGroup.add(_case);
			allCompetenceGroups.add(newGroup);

		}
		// wait2();
	}

	/**
	 * This method builds CompetenceGroups on our current casebase. TODO might
	 * not work if called the SECOND time...
	 * 
	 * @param allCases
	 * @return
	 */
	private static void createCompetenceGroups(Collection<CBRCase> allCases) {
		System.out.println("Creating CompetenceGroups - Start");

		if (allCompetenceGroups == null) {
			allCompetenceGroups = new ArrayList<CaseList>();
		} else {
			// TODO when we later want to REconstruct CG we have to
			// reinitialize!!
		}

		for (CBRCase _case : allCases) {
			// System.out.println("Creating CompetenceGroups - till now " +
			// allCompetenceGroups.size());
			// System.out.println("   next case for sorting in is" + _case);

			sortIntoCompetenceGroups(_case);
		}

		System.out.print("Creating CompetenceGroups - Ende (created "
				+ allCompetenceGroups.size() + ")");
	}

	/**
	 * Here the so called Footprint set is created, which is necessary for the
	 * whole magic of our approach. TODO needs to be called for maintenance
	 * 
	 * @param allCases
	 * @param simConfig
	 * @return
	 */
	public static void createFootprintSet(Collection<CBRCase> allCases,
			NNConfig simConfig) {
		// Preparations: build footprint set:
		// TODO should only be done once for a !!specific!! CBRCase casebase!
		if (myFootprintSet != null) {
			System.out.println("DONT BUILD THE FPS TWICE!!!");// TODO for
																// now....
		}

		System.out.println("Creating Footprintset.....");
		// For all the cases in the casebase:
		CBRCase _case;
		// compute Spaces
		createRetrievalSpace(allCases, simConfig);
		createAdaptionSpace(allCases);
		createSolveSpace(allCases);

		// compute Coverage and Reachability
		createCoverageSet(allCases);
		createReachabilitySet(allCases);

		createRelatedSet(allCases);
		// TODO compute relative coverage...????

		// wait2();

		// Now create the CompetenceGroups:
		createCompetenceGroups(allCases);
		System.out.println("Number of CompetenceGroups: "
				+ allCompetenceGroups.size());

		// And determine a suitable group-footprint for each CompetenceGroup:
		createCompetenceGroupFootprints(allCases);
		// Debug the groupfootprints :)
		System.out.println("Number of CompetenceGroupsFootprints: "
				+ allCompetenceGroupFootprints.size());
		for (Integer key : allCompetenceGroupFootprints.keySet()) {
			CaseList competencegroup = null;
			for (CaseList _group : allCompetenceGroups) {
				if (_group.getID() == key) {
					competencegroup = _group;
				}
			}
			CaseList groupfootprint = allCompetenceGroupFootprints.get(key);

			// System.out.println("Competencegroup:");
			// System.out.println(competencegroup);
			// System.out.println("Corresponding Competencegroup Footprint:");
			// System.out.println(groupfootprint);

		}// end debug

		// Now just union over all CompetenceGroup group-footprints:
		myFootprintSet = new CaseList();
		for (Integer key : allCompetenceGroupFootprints.keySet()) {
			CaseList groupfootprint = allCompetenceGroupFootprints.get(key);
			// System.out.println("current footprint:");
			// System.out.println(myFootprintSet);
			// System.out.println("union with");
			// System.out.println(groupfootprint);
			myFootprintSet = CaseList.union(myFootprintSet, groupfootprint);
		}
		System.out.println("... creation of Footprintset completed!");
	}

	/**
	 * TODO javadoc
	 * 
	 * @param allCases
	 */
	private static void createCompetenceGroupFootprints(
			Collection<CBRCase> allCases) {

		// System.out.println("Creating CompetenceGroup Footprints - Start");

		// init HashMap of all Goups empty and then do for all CompetenceGroups
		allCompetenceGroupFootprints = new HashMap<Integer, CaseList>();

		// for all CompetenceGroups:
		for (CaseList group : allCompetenceGroups) {
			// System.out.println("=======================");
			// System.out.println("		Create a groupfootprint for:");
			// System.out.println(group);

			// initialize GroupFootprint as empty
			CaseList groupfootprint = new CaseList();

			// sort the cases in current CompetenceGroup by relative coverage:
			sortCompetenceGroupByRelativeCoverage(group);
			// System.out.println("		after sorting:");
			// System.out.println(group);

			// as long as there are uncovered cases by the groupfootprint
			int i = 0;
			while (!competenceGroupIsCovered(group, groupfootprint)
					&& i <= group.size()) {
				// select top (not selected) "coveragee"
				CBRCase coveragee = group.get(i);

				// System.out.println("add "+i+"th one to groupfootprint");

				// and add him to group-footprint
				groupfootprint.add(coveragee);

				i++;
			}

			// System.out.println("	Groupfootprint is:");
			// System.out.println(groupfootprint);
			allCompetenceGroupFootprints.put(group.getID(), groupfootprint);
			// System.out.println("	Done for this group.");
			// System.out.println("-------------------------------");
		}

		System.out
				.println("Creating CompetenceGroup Footprints - Ende (total created + "
						+ allCompetenceGroupFootprints.size() + ")");
	}

	/**
	 * Methods checks if "groupfootprint" covers the Competence "group".
	 * 
	 * @param group
	 * @param groupfootprint
	 * @return true if yes
	 */
	private static boolean competenceGroupIsCovered(CaseList group,
			CaseList groupfootprint) {
		// System.out.println("group " + group);
		// System.out.println("footprint " + groupfootprint);

		// the group is non empty, but the footprint is: return false
		if (group.size() > 0 && groupfootprint.size() == 0) {
			// System.out.println("false, weil wegen leer");
			return false;
		}

		boolean groupIsCovered = false;
		for (CBRCase _case : group) {
			// System.out.println("Checking if group member "+_case);
			boolean caseIsCovered = false;
			for (CBRCase _rep : groupfootprint) {
				// System.out.println("is covered by footprint member"+_rep);
				CaseList coverageset = CoverageSet.get(_rep);

				// System.out.println("fps coverageset:" + coverageset);
				if (coverageset.contains(_case)) {
					// System.out.println("Yes, this case is covered by this fpmember");
					caseIsCovered = true;
				} else {
					// System.out.println("No, this case is not covered by this fpmember");
				}
			}
			if (!caseIsCovered) {
				// System.out.println("false");
				return false;
			}
		}
		// System.out.println(groupIsCovered);
		return true;
	}

	/**
	 * Sort this group for relativeCoverage
	 * 
	 * @param group
	 */
	// Philipp: Method is checked and sorts correctly =)
	private static void sortCompetenceGroupByRelativeCoverage(CaseList group) {
		/*
		 * CaseList group2 = new CaseList(); for (int i = 0; i < 10; i++) {
		 * CBRCase _case = group.get(i); group2.add(_case); } group = group2;
		 * 
		 * System.out.println("Running sorting on:"); for (CBRCase _case :
		 * group) { Double rating = relativeCoverage(_case);
		 * System.out.println(_case); System.out.println(rating); }
		 */

		int members = group.size();

		boolean doMore = true;
		while (doMore) {
			doMore = false; // assume this is last pass over array
			for (int i = 0; i < members - 1; i++) {
				CBRCase _case1 = group.get(i);
				CBRCase _case2 = group.get(i + 1);
				if (relativeCoverage(_case1) < relativeCoverage(_case2)) {
					// exchange elements
					group.swap(_case1, _case2);
					doMore = true; // after an exchange, must look again
				}
			}
		}

		/*
		 * System.out.println("After sorting:"); for (CBRCase _case : group) {
		 * Double rating = relativeCoverage(_case); System.out.println(_case);
		 * System.out.println(rating); }
		 */

	}

}
