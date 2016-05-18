/**
 * 
 */
package ippoz.multilayer.detector.commons.data;

import ippoz.multilayer.detector.commons.dataseries.DataSeries;
import ippoz.multilayer.detector.commons.failure.InjectedElement;
import ippoz.multilayer.detector.commons.service.ServiceCall;
import ippoz.multilayer.detector.commons.service.ServiceStat;
import ippoz.multilayer.detector.commons.service.StatPair;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author Tommy
 *
 */
public class DataSeriesSnapshot extends Snapshot {

	private DataSeries dataSeries;
	private Double snapValue;
	private HashMap<String, StatPair> snapStat;
	
	public DataSeriesSnapshot(Observation obs, LinkedList<ServiceCall> currentCalls, InjectedElement injEl, HashMap<String, ServiceStat> ssList, DataSeries dataSeries) {
		super(obs.getTimestamp(), currentCalls, injEl, ssList);
		this.dataSeries = dataSeries;
		snapValue = dataSeries.getSeriesValue(obs);
		snapStat = dataSeries.getSeriesServiceStats(ssList);
	}

	public DataSeries getDataSeries() {
		return dataSeries;
	}

	public Double getSnapValue() {
		return snapValue;
	}

	public HashMap<String, StatPair> getSnapStat() {
		return snapStat;
	}

}
