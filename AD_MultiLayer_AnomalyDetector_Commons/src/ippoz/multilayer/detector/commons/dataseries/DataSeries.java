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

	@Override
	public String toString() {
		return seriesName + "#" + dataCategory + "#" + getLayerType();
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
		String layer = stringValue.substring(stringValue.lastIndexOf("#")+1);
		String partial = stringValue.substring(0, stringValue.indexOf(layer)-1);
		String dataType = partial.substring(partial.lastIndexOf("#")+1);
		String dataSeries = stringValue.substring(0, partial.lastIndexOf("#"));
		return fromStrings(dataSeries, DataCategory.valueOf(dataType), LayerType.valueOf(layer));
	}
	
	public static DataSeries fromStrings(String seriesName, DataCategory dataType, LayerType layerType) {
		if(layerType.equals(LayerType.COMPOSITION)){
			if(seriesName.contains(")*(")){
				return new ProductDataSeries(DataSeries.fromString(seriesName.substring(1,  seriesName.indexOf(")*(")).trim()), DataSeries.fromString(seriesName.substring(seriesName.indexOf(")*(")+3, seriesName.length()-1).trim()), dataType);
			} else if(seriesName.contains("/")){
				return new FractionDataSeries(DataSeries.fromString(seriesName.substring(1,  seriesName.indexOf(")/(")).trim()), DataSeries.fromString(seriesName.substring(seriesName.indexOf(")/(")+3, seriesName.length()-1).trim()), dataType);
			} else if(seriesName.contains("+")){
				return new SumDataSeries(DataSeries.fromString(seriesName.substring(1,  seriesName.indexOf(")+(")).trim()), DataSeries.fromString(seriesName.substring(seriesName.indexOf(")+(")+3, seriesName.length()-1).trim()), dataType);
			} else if(seriesName.contains("-")){
				return new DiffDataSeries(DataSeries.fromString(seriesName.substring(1,  seriesName.indexOf(")-(")).trim()), DataSeries.fromString(seriesName.substring(seriesName.indexOf(")-(")+3, seriesName.length()-1).trim()), dataType);
			} else return null;
		} else return new IndicatorDataSeries(new Indicator(seriesName, layerType, Double.class), dataType);
	}
	
	public static LinkedList<DataSeries> allCombinations(Indicator[] indicators, DataCategory[] dataTypes) {
		LinkedList<DataSeries> outList = new LinkedList<DataSeries>();
		LinkedList<DataSeries> simpleInd = new LinkedList<DataSeries>();
		LinkedList<DataSeries> complexInd = new LinkedList<DataSeries>();
		for(Indicator ind : indicators){
			for(DataCategory dCat : dataTypes){
				simpleInd.add(new IndicatorDataSeries(ind, dCat));
			}
		}
		for(DataSeries ds1 : simpleInd){
			for(DataSeries ds2 : simpleInd){
				for(DataCategory dCat : dataTypes){
					complexInd.add(new FractionDataSeries(ds1, ds2, dCat));
				}
			}
		}
		outList.addAll(simpleInd);
		outList.addAll(complexInd.subList(0, 1000));
		return outList;
	}
		
}
