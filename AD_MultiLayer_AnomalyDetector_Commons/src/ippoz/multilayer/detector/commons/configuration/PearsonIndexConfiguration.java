/**
 * 
 */
package ippoz.multilayer.detector.commons.configuration;

import ippoz.multilayer.detector.commons.algorithm.AlgorithmType;

/**
 * @author Tommy
 *
 */
public class PearsonIndexConfiguration extends AlgorithmConfiguration {

	public static final String PI_DETAIL = "pi_detail";
	public static final String PI_WINDOW = "pi_window";
	public static final String PI_TOLERANCE = "pi_tolerance";
	
	public PearsonIndexConfiguration(){
		super(AlgorithmType.PEA);
	}

	@Override
	public String getFileHeader() {
		return "pi_window, pi_tolerance,pi_details";
	}

	@Override
	public String toFileRow(boolean complete) {
		if(complete)
			return getItem(WEIGHT) + "," + getItem(SCORE) + "," + getItem(PI_WINDOW) + "," + getItem(PI_TOLERANCE) + "," + getItem(PI_DETAIL);
		else return getItem(PI_WINDOW) + "," + getItem(PI_TOLERANCE) + "," + getItem(PI_DETAIL);
	}

}
