/**
 * 
 */
package ippoz.multilayer.detector.configuration;

import ippoz.multilayer.detector.algorithm.AlgorithmType;

/**
 * The Class SPSConfiguration.
 * Configuration for the SPS algorithm.
 *
 * @author Tommy
 */
public class SPSConfiguration extends AlgorithmConfiguration {

	/** The Constant PDV. */
	public static final String PDV = "pdv";
	
	/** The Constant POV. */
	public static final String POV = "pov";
	
	/** The Constant PDS. */
	public static final String PDS = "pds";
	
	/** The Constant POS. */
	public static final String POS = "pos";
	
	/** The Constant M. */
	public static final String M = "m";
	
	/** The Constant N. */
	public static final String N = "n";
	
	/** The Constant DYN_WEIGHT. */
	public static final String DYN_WEIGHT = "dweight";
	
	public SPSConfiguration() {
		super(AlgorithmType.SPS);
	}
	
	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.configuration.AlgorithmConfiguration#toString()
	 */
	@Override
	public String toString() {
		return "SPSConf:{p=" + getItem(PDV) + ",m=" + getItem(M) + ",dw=" + getItem(DYN_WEIGHT) + "}";
	}

	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.configuration.AlgorithmConfiguration#getFileHeader()
	 */
	@Override
	public String getFileHeader() {
		return "weight,score,pdv,pov,pds,pos,m,n,dweight";
	}

	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.configuration.AlgorithmConfiguration#toFileRow(boolean)
	 */
	@Override
	public String toFileRow(boolean complete) {
		if(complete)
			return getItem(WEIGHT) + ", " + getItem(SCORE) + ", " + getItem(PDV) + "," + getItem(POV) + "," + getItem(PDS) + "," + getItem(POS) + "," + getItem(M) + "," + getItem(N) + "," + getItem(DYN_WEIGHT) + ",";
		else return getItem(PDV) + "," + getItem(POV) + "," + getItem(PDS) + "," + getItem(POS) + "," + getItem(M) + "," + getItem(N) + "," + getItem(DYN_WEIGHT) + ",";
		
	}

}
