/**
 * 
 */
package ippoz.multilayer.detector.configuration;

import java.util.HashMap;

/**
 * @author Tommy
 *
 */
public abstract class AlgorithmConfiguration {
	
	public static final String WEIGHT = "weight";
	public static final String SCORE = "metric_score";

	private HashMap<String, String> confMap;
	
	public AlgorithmConfiguration(){
		confMap = new HashMap<String, String>();
	}
	
	public void addItem(String item, String value){
		confMap.put(item, value);
	}
	
	public String getItem(String tag){
		return confMap.get(tag);
	}

	@Override
	public String toString() {
		return confMap.size() + " configuration parameters found";
	}

	public abstract String getFileHeader();

	public abstract String toFileRow(boolean complete);
	
	
	
}
