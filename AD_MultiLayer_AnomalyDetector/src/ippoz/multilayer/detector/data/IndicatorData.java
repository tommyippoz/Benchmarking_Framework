/*
 * 
 */
package ippoz.multilayer.detector.data;

import ippoz.multilayer.detector.support.AppLogger;

import java.util.HashMap;

/**
 * @author Tommy
 *
 */
public class IndicatorData {
	
	public static final String PLAIN_DATA_TAG = "PLAIN";
	public static final String DIFF_DATA_TAG = "DIFFERENCE";
	
	private HashMap<String, String> dataMap;
	
	public IndicatorData(HashMap<String, String> dataMap){
		this.dataMap = dataMap;
	}
	
	public String getCategoryValue(String categoryTag){
		if(dataMap.containsKey(categoryTag))
			return dataMap.get(categoryTag);
		else {
			AppLogger.logError(getClass(), "NoSuchCategoryData", "Category '" + categoryTag + "' not found");
			return null;
		}
	}

}
