/**
 * 
 */
package ippoz.multilayer.detector.configuration;

import java.util.HashMap;

/**
 * The Class AlgorithmConfiguration.
 * Basic Configuration for the involved Algorithms.
 *
 * @author Tommy
 */
public abstract class AlgorithmConfiguration {
	
	/** The Constant WEIGHT. */
	public static final String WEIGHT = "weight";
	
	/** The Constant SCORE. */
	public static final String SCORE = "metric_score";

	/** The configuration map. */
	private HashMap<String, String> confMap;
	
	/**
	 * Instantiates a new algorithm configuration.
	 */
	public AlgorithmConfiguration(){
		confMap = new HashMap<String, String>();
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
