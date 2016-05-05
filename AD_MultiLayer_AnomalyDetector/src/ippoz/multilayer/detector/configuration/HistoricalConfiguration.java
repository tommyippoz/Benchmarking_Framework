/**
 * 
 */
package ippoz.multilayer.detector.configuration;

/**
 * @author Tommy
 *
 */
public class HistoricalConfiguration extends AlgorithmConfiguration {
	
	public static final String INTERVAL_WIDTH = "interval_width";
	
	@Override
	public String toString() {
		return "HistConf:{interval_width=" + getItem(INTERVAL_WIDTH) + "}";
	}

	@Override
	public String getFileHeader() {
		return "weigth,score,interval_width";
	}

	@Override
	public String toFileRow(boolean complete) {
		if(complete)
			return getItem(WEIGHT) + ", " + getItem(SCORE) + ", " + getItem(INTERVAL_WIDTH);
		else return getItem(INTERVAL_WIDTH);
	}

}
