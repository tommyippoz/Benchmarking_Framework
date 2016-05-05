/**
 * 
 */
package ippoz.multilayer.detector.datafetcher;

import ippoz.multilayer.detector.data.ExperimentData;
import ippoz.multilayer.detector.data.LayerType;
import ippoz.multilayer.detector.data.Observation;
import ippoz.multilayer.detector.failure.InjectedElement;
import ippoz.multilayer.detector.service.ServiceCall;
import ippoz.multilayer.detector.service.ServiceStat;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author Tommy
 *
 */
public abstract class DataFetcher extends Thread {

	private ExperimentData expData;
	
	public ExperimentData getFetchedData(){
		return expData;
	}
	
	@Override
	public void run() {
		expData = new ExperimentData(getID(), getObservations(), getServiceCalls(), getInjections(), getServiceStats(), getPerformanceTimings());
	}

	protected abstract String getID();
	
	protected abstract LinkedList<Observation> getObservations();

	protected abstract LinkedList<ServiceCall> getServiceCalls();

	protected abstract HashMap<String, ServiceStat> getServiceStats();

	protected abstract LinkedList<InjectedElement> getInjections();

	protected abstract HashMap<String, HashMap<LayerType, LinkedList<Integer>>> getPerformanceTimings();
	
	public abstract void flush();
	
}
