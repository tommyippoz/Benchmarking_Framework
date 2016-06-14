/**
 * 
 */
package ippoz.multilayer.detector.commons.configuration;

import ippoz.multilayer.commons.support.AppLogger;
import ippoz.multilayer.detector.commons.algorithm.AlgorithmType;

import java.util.HashMap;

/**
 * The Class AlgorithmConfiguration.
 * Basic Configuration for the involved Algorithms.
 *
 * @author Tommy
 */
public abstract class AlgorithmConfiguration implements Cloneable {
	
	/** The Constant WEIGHT. */
	public static final String WEIGHT = "weight";
	
	/** The Constant SCORE. */
	public static final String SCORE = "metric_score";

	/** The configuration map. */
	private HashMap<String, String> confMap;
	
	/** The algorithm type */
	private AlgorithmType algType;
	
	/**
	 * Instantiates a new algorithm configuration.
	 */
	public AlgorithmConfiguration(AlgorithmType algType){
		confMap = new HashMap<String, String>();
		this.algType = algType;
	}
	
	private void setMap(HashMap<String, String> newMap){
		confMap = newMap;
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		HashMap<String, String> newMap = new HashMap<String, String>();
		AlgorithmConfiguration newConf = null;
		try {
			newConf = getConfiguration(algType);
			for(String mapKey : confMap.keySet()){
				newMap.put(mapKey, confMap.get(mapKey));
			}
			newConf.setMap(newMap);
		} catch (Exception ex) {
			AppLogger.logException(getClass(), ex, "Unable to clone configuration");
		}
		return newConf;
	}
	
	public static AlgorithmConfiguration getConfiguration(AlgorithmType algType) {
		switch(algType){
			case SPS:
				return new SPSConfiguration();
			case CONF:
				return new ConfidenceConfiguration();
			case HIST:
				return new HistoricalConfiguration();
			case INV:
				break;
			case PEA:
				return new PearsonIndexConfiguration();
			case RCC:
				return new RemoteCallConfiguration();
			case WER:
				return new WesternElectricRulesConfiguration();
		}
		return null;
	}

	/**
	 * Adds an item.
	 *
	 * @param item the item tag
	 * @param value the itam value
	 */
	public void addItem(String item, String value){
		confMap.put(item, value);
	}
	
	/**
	 * Gets the item.
	 *
	 * @param tag the item tag
	 * @return the item
	 */
	public String getItem(String tag){
		return confMap.get(tag);
	}
	
	/**
	 * Gets the algorithm type.
	 *
	 * @return the algType
	 */
	public AlgorithmType getAlgorithmType(){
		return algType;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return confMap.size() + " configuration parameters found";
	}

	/**
	 * Gets the file header.
	 *
	 * @return the file header
	 */
	public abstract String getFileHeader();

	/**
	 * Converts to a file row.
	 *
	 * @param complete the complete flag, defines if the description is extended or not.
	 * @return the file row string
	 */
	public abstract String toFileRow(boolean complete);	
	
}
