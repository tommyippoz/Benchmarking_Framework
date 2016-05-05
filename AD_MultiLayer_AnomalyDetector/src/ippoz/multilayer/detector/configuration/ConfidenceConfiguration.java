/**
 * 
 */
package ippoz.multilayer.detector.configuration;

/**
 * @author Tommy
 *
 */
public class ConfidenceConfiguration extends AlgorithmConfiguration {
	
	public static final String ALPHA = "alpha";
	
	@Override
	public String toString() {
		return "CIntConf:{alpha=" + getItem(ALPHA) + "}";
	}

	@Override
	public String getFileHeader() {
		return "weigth,score,alpha";
	}

	@Override
	public String toFileRow(boolean complete) {
		if(complete)
			return getItem(WEIGHT) + ", " + getItem(SCORE) + ", " + getItem(ALPHA);
		else return getItem(ALPHA);
	}

	public double getAlpha() {
		return Double.parseDouble(getItem(ALPHA));
	}


}
