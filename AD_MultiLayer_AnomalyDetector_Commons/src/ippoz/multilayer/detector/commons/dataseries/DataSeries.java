/**
 * 
 */
package ippoz.multilayer.detector.commons.dataseries;

import ippoz.multilayer.commons.datacategory.DataCategory;
import ippoz.multilayer.commons.indicator.Indicator;
import ippoz.multilayer.commons.layers.LayerType;
import ippoz.multilayer.detector.commons.data.Observation;
import ippoz.multilayer.detector.commons.service.IndicatorStat;
import ippoz.multilayer.detector.commons.service.ServiceCall;
import ippoz.multilayer.detector.commons.service.ServiceStat;
import ippoz.multilayer.detector.commons.service.StatPair;

import java.util.Date;
import java.util.LinkedList;

/**
 * @author Tommy
 *
 */
public abstract class DataSeries implements Comparable<DataSeries> {

	private String seriesName;
	private DataCategory dataCategory;
	
	protected DataSeries(String seriesName, DataCategory dataCategory) {
		this.seriesName = seriesName;
		this.dataCategory = dataCategory;
	}

	public String getName() {
		return seriesName;
	}

	public DataCategory getDataCategory() {
		return dataCategory;
	}

	@Override
	public int compareTo(DataSeries other) {
		return seriesName.equals(other.getName()) && dataCategory.equals(other.getDataCategory()) ? 0 : 1;
	}
	
	public Double getSeriesValue(Observation obs){
		switch(dataCategory){
		case PLAIN:
			return getPlainSeriesValue(obs);
		case DIFFERENCE:
			return getDiffSeriesValue(obs);
		default:
			return null;
		}
	}

	public abstract LayerType getLayerType();
	
	protected abstract Double getPlainSeriesValue(Observation obs);
	
	protected abstract Double getDiffSeriesValue(Observation obs);
	
	// Sincronizza anche se è all'inizio, nel corpo o alla fine.
	public abstract StatPair getSeriesServiceStat(Date timestamp, ServiceCall sCall, ServiceStat sStat);

	protected static StatPair getPairByTime(Date timestamp, ServiceCall sCall, IndicatorStat iStat){
		if(sCall.isAliveAt(timestamp)){
			if(sCall.getStartTime().equals(timestamp))
				return iStat.getFirstObs();
			else if(sCall.getStartTime().before(timestamp) && sCall.getEndTime().after(timestamp))
				return iStat.getAllObs();
			else if(sCall.getEndTime().equals(timestamp))
				return iStat.getLastObs();
		}
		return null;
	}
	
	public static DataSeries fromString(String stringValue) {
		// TODO Auto-generated method stub
		return null;
	}
	
	public static LinkedList<DataSeries> allCombinations(Indicator[] indicators, DataCategory[] dataTypes) {
		// TODO Auto-generated method stub
		return null;
	}
		
}
