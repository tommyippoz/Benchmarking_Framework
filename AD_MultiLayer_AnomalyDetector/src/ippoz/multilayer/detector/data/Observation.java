/**
 * 
 */
package ippoz.multilayer.detector.data;

import ippoz.multilayer.detector.support.AppLogger;
import ippoz.multilayer.detector.support.AppUtility;

import java.util.Date;
import java.util.HashMap;

/**
 * @author Tommy
 *
 */
public class Observation {
	
	private String probeName;
	private Date timestamp;
	private HashMap<Indicator, IndicatorData> observedIndicators;
	
	public Observation(String probeName, String timestamp){
		this.probeName = probeName;
		this.timestamp = AppUtility.convertStringToDate(timestamp);
		observedIndicators = new HashMap<Indicator, IndicatorData>();
	}
	
	public void addIndicator(Indicator newInd, IndicatorData newValue){
		observedIndicators.put(newInd, newValue);
	}

	public String getSourceProbeName() {
		return probeName;
	}

	public Date getTimestamp() {
		return timestamp;
	}
	
	public Indicator[] getIndicators(){
		return observedIndicators.keySet().toArray(new Indicator[observedIndicators.keySet().size()]);
	}

	public String getValue(Indicator indicator, String categoryTag) {
		return observedIndicators.get(indicator).getCategoryValue(categoryTag);
	}
	
	public String getValue(String indicatorName, String categoryTag) {
		for(Indicator ind : getIndicators()){
			if(ind.getName().equals(indicatorName))
				return getValue(ind, categoryTag);
		}
		AppLogger.logError(getClass(), "NoSuchIndicator", "Unable to find Indicator '" + indicatorName + "'");
		return null;
	}
	
	public LayerType getLayerType(){
		return observedIndicators.keySet().iterator().next().getLayer();
	}
	
	public int size(){
		return observedIndicators.size();
	}

	

}
