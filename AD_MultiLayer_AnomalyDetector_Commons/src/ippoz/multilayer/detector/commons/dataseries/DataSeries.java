/**
 * 
 */
package ippoz.multilayer.detector.commons.dataseries;

import ippoz.multilayer.commons.datacategory.DataCategory;
import ippoz.multilayer.commons.indicator.Indicator;
import ippoz.multilayer.commons.layers.LayerType;
import ippoz.multilayer.detector.commons.data.Observation;
import ippoz.multilayer.detector.commons.service.ServiceStat;
import ippoz.multilayer.detector.commons.service.StatPair;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeMap;

/**
 * @author Tommy
 *
 */
public class DataSeries implements Comparable<DataSeries> {

	private String seriesName;
	private TreeMap<Date, Double> dataList;
	private DataCategory dataCategory;

	public Double getSeriesValue(Observation obs) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	// Sincronizza anche se è all'inizio, nel corpo o alla fine.
	public HashMap<String, StatPair> getSeriesServiceStats(HashMap<String, ServiceStat> ssList) {
		// TODO Auto-generated method stub
		return null;
	}

	public static DataSeries fromString(String stringValue) {
		// TODO Auto-generated method stub
		return null;
	}

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public DataCategory getDataCategory() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int compareTo(DataSeries arg0) {
		// TODO Auto-generated method stub
		return 0;
	}


	public LayerType getLayerType() {
		// TODO Auto-generated method stub
		return null;
	}


	public static LinkedList<DataSeries> allCombinations(Indicator[] indicators, DataCategory[] dataTypes) {
		// TODO Auto-generated method stub
		return null;
	}
	
	
}
