package jcolibri.method.retrieve.Footprint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import jcolibri.cbrcore.CBRCase;
import jcolibri.cbrcore.CBRCaseBase;
import jcolibri.cbrcore.CBRQuery;
import jcolibri.cbrcore.CaseComponent;
import jcolibri.method.retrieve.RetrievalResult;
import jcolibri.method.retrieve.NNretrieval.NNConfig;
import jcolibri.method.retrieve.NNretrieval.similarity.GlobalSimilarityFunction;
import jcolibri.method.retrieve.selection.SelectCases;

public class FtbSearcher implements Runnable {

	
	boolean SpeedVsQuality = true;
	
	private HashMap<CBRCase, CaseList> theSpace;
	private boolean theDir;
	private ArrayList<CBRCase> theCases;
	private NNConfig simConfig;

	public FtbSearcher(Collection<CBRCase> allCases, boolean dir,
			HashMap<CBRCase, CaseList> retrievalSpace, NNConfig conf) {

		theSpace = retrievalSpace;
		theDir = dir;
		theCases = (ArrayList<CBRCase>) allCases;
		simConfig = conf;

	}

	@Override
	public void run() {

		if (theDir) // search forward till half
		{
			searchForward();
		} else { // search backward till half
			searchBackward();
		}

	}

	private static CBRQuery transformToQuery(CBRCase _case) {
		/*
		 * TravelDescription queryDesc = new TravelDescription();
		 * queryDesc.setAccomodation("ThreeStars"); queryDesc.setDuration(7);
		 * queryDesc.setHolidayType("Recreation");
		 * queryDesc.setNumberOfPersons(2); queryDesc.setPrice(700);
		 */

		CaseComponent queryDesc = _case.getDescription();

		CBRQuery query = new CBRQuery();
		query.setDescription(queryDesc);

		return query;
	}

	
	private void executeStep(CBRCase _case, List<RetrievalResult> retrievalResults){
		
		System.out.println("	creating RetrievalSpace for " + _case);
		List<RetrievalResult> res = new ArrayList<RetrievalResult>();
		GlobalSimilarityFunction gsf = simConfig
				.getDescriptionSimFunction();

		// transform _case into query:
		CBRQuery query = transformToQuery(_case);

		for (CBRCase __case : theCases) {
			
			
			double rating;
			
			if(SpeedVsQuality){
				rating = gsf.compute(__case.getDescription(), query
						.getDescription(), _case, query, simConfig);
			}else{
				rating = FPSimilarityRating.FAKEcompute(_case, __case);	
			}
			
			RetrievalResult temp_result = new RetrievalResult(__case,
					rating);

			res.add(temp_result);
		}
		java.util.Collections.sort(res);
		retrievalResults = res;

		retrievalResults = (List<RetrievalResult>) SelectCases
				.selectTopKRR(retrievalResults, 10);

		// System.out.println("being retrieved");
		CaseList retrievedCases = new CaseList();
		for (RetrievalResult ress : retrievalResults) {
			// System.out.println(res);
			retrievedCases.add(ress.get_case());
			retrievedCases.add(_case); // FIXME a case is always retrieved
			// for itself...
		}
//
//		System.out.println("RetrievalSpace for case:");
//		System.out.println(_case);
//		System.out.println(retrievedCases);
		// wait2();

		theSpace.put(_case, retrievedCases);		
	}
	
	private void searchForward() {
		List<RetrievalResult> retrievalResults = new ArrayList<RetrievalResult>();
		int s = theCases.size();
		//s = 50;
		for (int i=0; i<s/2; i++) {
			CBRCase _case = theCases.get(i);
			executeStep(_case, retrievalResults);
		}
	}
	
	private void searchBackward() {
		List<RetrievalResult> retrievalResults = new ArrayList<RetrievalResult>();
		int s = theCases.size();
		//s = 50;
		for (int i=s-1; i>=s/2; i--) {
			CBRCase _case = theCases.get(i);
			executeStep(_case, retrievalResults);
		}
	}

}
