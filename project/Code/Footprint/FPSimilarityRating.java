package jcolibri.method.retrieve.Footprint;

import java.util.Random;

import jcolibri.cbrcore.CBRCase;
import jcolibri.cbrcore.CaseComponent;

public class FPSimilarityRating {

	public static Double compute(CBRCase case1, CBRCase cmpCase) {
		//CaseComponent desc1 = case1.getDescription();
		
		Random random = new Random();
		return random.nextDouble();
		
	}

}
