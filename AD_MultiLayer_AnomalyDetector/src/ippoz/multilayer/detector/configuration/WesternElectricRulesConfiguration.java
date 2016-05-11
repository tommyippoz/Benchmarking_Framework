/**
 * 
 */
package ippoz.multilayer.detector.configuration;

/**
 * @author Tommy
 *
 */
public class WesternElectricRulesConfiguration extends AlgorithmConfiguration {
	
	/** The Constant WER_WEIGHT. */
	public static final String WER_WEIGHT = "wer_weight";
	
	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.configuration.AlgorithmConfiguration#toString()
	 */
	@Override
	public String toString() {
		return "WERConf:{weight=" + getItem(WEIGHT) + "}";
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
			return getItem(WEIGHT) + ", " + getItem(SCORE) + ", " + getItem(WER_WEIGHT);
		else return getItem(WER_WEIGHT);
	}

	/**
	 * Gets the weight of the western electric rules checker.
	 *
	 * @return the weight
	 */
	public double getRemoteCallWeight() {
		return Double.parseDouble(getItem(WER_WEIGHT));
	}

}
