/**
 * 
 */
package ippoz.multilayer.detector.configuration;

import ippoz.multilayer.detector.algorithm.AlgorithmType;

/**
 * The Class HistoricalConfiguration.
 * Configuration for the HistoricalChecker Algorithm.
 *
 * @author Tommy
 */
public class HistoricalConfiguration extends AlgorithmConfiguration {

	/** The Constant INTERVAL_WIDTH. */
	public static final String INTERVAL_WIDTH = "interval_width";
	
	public HistoricalConfiguration() {
		super(AlgorithmType.HIST);
	}
	
	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.configuration.AlgorithmConfiguration#toString()
	 */
	@Override
	public String toString() {
		return "HistConf:{interval_width=" + getItem(INTERVAL_WIDTH) + "}";
	}

	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.configuration.AlgorithmConfiguration#getFileHeader()
	 */
	@Override
	public String getFileHeader() {
		return "weigth,score,interval_width";
	}

	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.configuration.AlgorithmConfiguration#toFileRow(boolean)
	 */
	@Override
	public String toFileRow(boolean complete) {
		if(complete)
			return getItem(WEIGHT) + ", " + getItem(SCORE) + ", " + getItem(INTERVAL_WIDTH);
		else return getItem(INTERVAL_WIDTH);
	}

}
