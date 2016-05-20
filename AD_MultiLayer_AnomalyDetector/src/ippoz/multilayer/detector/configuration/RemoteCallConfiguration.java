/**
 * 
 */
package ippoz.multilayer.detector.configuration;

import ippoz.multilayer.detector.algorithm.AlgorithmType;

/**
 * The Class RemoteCallConfiguration.
 * Configuration for the RemoteCallChecker Algorithm.
 *
 * @author Tommy
 */
public class RemoteCallConfiguration extends AlgorithmConfiguration {

	/** The Constant RCC_WEIGHT. */
	public static final String RCC_WEIGHT = "rcc_weight";
	
	public RemoteCallConfiguration() {
		super(AlgorithmType.RCC);
	}
	
	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.configuration.AlgorithmConfiguration#toString()
	 */
	@Override
	public String toString() {
		return "RCCConf:{weight=" + getItem(WEIGHT) + "}";
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
			return getItem(WEIGHT) + ", " + getItem(SCORE) + ", " + getItem(RCC_WEIGHT);
		else return getItem(RCC_WEIGHT);
	}

	/**
	 * Gets the weight of the remote call checker.
	 *
	 * @return the weight
	 */
	public double getRemoteCallWeight() {
		return Double.parseDouble(getItem(RCC_WEIGHT));
	}

}