/**
 * 
 */
package ippoz.multilayer.detector.configuration;

/**
 * @author Tommy
 *
 */
public class RemoteCallConfiguration extends AlgorithmConfiguration {
	
	public static final String RCC_WEIGHT = "rcc_weight";
	
	@Override
	public String toString() {
		return "RCCConf:{weight=" + getItem(WEIGHT) + "}";
	}

	@Override
	public String getFileHeader() {
		return "weigth,score,alpha";
	}

	@Override
	public String toFileRow(boolean complete) {
		if(complete)
			return getItem(WEIGHT) + ", " + getItem(SCORE) + ", " + getItem(RCC_WEIGHT);
		else return getItem(RCC_WEIGHT);
	}

	public double getAlpha() {
		return Double.parseDouble(getItem(RCC_WEIGHT));
	}

}