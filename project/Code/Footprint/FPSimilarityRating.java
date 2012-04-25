package jcolibri.method.retrieve.Footprint;

import java.util.HashMap;
import java.util.Random;

import jcolibri.cbrcore.Attribute;
import jcolibri.cbrcore.CBRCase;
import jcolibri.cbrcore.CaseComponent;
import jcolibri.exception.AttributeAccessException;

public class FPSimilarityRating {

	private static HashMap<CBRCase, HashMap<CBRCase, Double>> ratingstmp = new HashMap<CBRCase, HashMap<CBRCase, Double>>();

	private static int traID(CBRCase c) {
		String s = (String) c.getID();
		s = s.substring(7);
		return Integer.parseInt(s);

	}

	public static Double compute(CBRCase case1, CBRCase cmpCase) {
		// CaseComponent desc1 = case1.getDescription();

		double result = 0;

		// System.out.println("Comparing:");
		// System.out.println(case1);
		// System.out.println(cmpCase);
		//		
		// System.out.println( case1.getDescription().getClass() );

		// sort
		if (traID(case1) > traID(cmpCase)) {
			CBRCase temp = cmpCase;
			cmpCase = case1;
			case1 = temp;
		}
		// System.out.println( case1.getID() );
		// System.out.println( cmpCase.getID() );

		if (ratingstmp.containsKey(case1)) {
			if (ratingstmp.get(case1).containsKey(cmpCase)) {
				return ratingstmp.get(case1).get(cmpCase);
			}
		}

		Attribute[] attributes = jcolibri.util.AttributeUtils
				.getAttributes(case1.getDescription().getClass());

		for (int i = 0; i < attributes.length; i++) {
			Attribute at1 = attributes[i];
			if (at1.getName().equals("caseId")) {
				continue;
			}
			// System.out.println( at1.getName() );
			// System.out.println( at1.getType() );
			// System.out.println( at1.getClass() );
			try {

				Object a = at1.getValue(case1.getDescription());
				Object b = at1.getValue(cmpCase.getDescription());
				// System.out.println( a );
				// System.out.println( b );

				boolean ok = a.equals(b);
				// System.out.println( ok );
				if (ok) {
					result += 0.2;
				}

			} catch (AttributeAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		// System.out.println(result);
		
		HashMap<CBRCase, Double> newhash;
		if( ratingstmp.containsKey(case1)){
			newhash = ratingstmp.get(case1);	
		}else{
			newhash = new HashMap<CBRCase, Double>();
		}
		newhash.put(cmpCase, result);
		ratingstmp.put(case1, newhash);
		
		return result;

		// Random random = new Random();
		// return random.nextDouble();

	}

}
