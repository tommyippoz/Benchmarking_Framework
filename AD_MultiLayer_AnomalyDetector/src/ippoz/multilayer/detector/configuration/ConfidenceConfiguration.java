/**
 * 
 */
package ippoz.multilayer.detector.configuration;

import ippoz.multilayer.detector.algorithm.AlgorithmType;

/**
 * The Class ConfidenceConfiguration.
 * Configuration for the ConfidenceIntervalChecker Algorithm.
 *
 * @author Tommy
 */
public class ConfidenceConfiguration extends AlgorithmConfiguration {

	/** The Constant ALPHA. */
	public static final String ALPHA = "alpha";
	
	public ConfidenceConfiguration() {
		super(AlgorithmType.CONF);
	}
	
	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.configuration.AlgorithmConfiguration#toString()
	 */
	@Override
	public String toString() {
		return "CIntConf:{alpha=" + getItem(ALPHA) + "}";
	}

	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.configuration.AlgorithmConfiguration#getFileHeader()
	 */
	@Override
	public String getFileHeader() {
		return "weigth,score,alpha";
	}

	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.configuration.AlgorithmConfiguration#toFileRow(boolean)
	 */
	@Override
	public String toFileRow(boolean complete) {
		if(complete)
			return getItem(WEIGHT) + ", " + getItem(SCORE) + ", " + getItem(ALPHA);
		else return getItem(ALPHA);
	}

	/**
	 * Gets the alpha of the confidence interval.
	 *
	 * @return the alpha
	 */
	public double getAlpha() {
		return Double.parseDouble(getItem(ALPHA));
	}


}
