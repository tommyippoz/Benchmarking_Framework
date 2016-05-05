/**
 * 
 */
package ippoz.multilayer.detector.data;

import ippoz.multilayer.detector.failure.InjectedElement;
import ippoz.multilayer.detector.service.ServiceCall;
import ippoz.multilayer.detector.service.ServiceStat;
import ippoz.multilayer.detector.support.AppLogger;
import ippoz.multilayer.detector.support.AppUtility;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author Tommy
 *
 */
public class ExperimentData implements Cloneable {
	
	private String expName;
	private LinkedList<Observation> obsList;
	private LinkedList<ServiceCall> callList;
	private LinkedList<InjectedElement> injList;
	private HashMap<String, ServiceStat> ssList;
	private HashMap<String, HashMap<LayerType, LinkedList<Integer>>> timings;
	private LinkedList<Snapshot> snapList;
	private Iterator<Snapshot> snapIterator;
	
	public ExperimentData(String expID, LinkedList<Observation> obsList, LinkedList<ServiceCall> callList, LinkedList<InjectedElement> injList, HashMap<String, ServiceStat> ssList, HashMap<String, HashMap<LayerType, LinkedList<Integer>>> timings){
		expName = "exp" + expID;
		this.obsList = obsList;
		this.callList = callList;
		this.injList = injList;
		this.ssList = ssList;
		this.timings = timings;
		buildSnapshots();
	}
	
	@Override
	public ExperimentData clone() throws CloneNotSupportedException {
		ExperimentData eData = new ExperimentData(expName, obsList, callList, injList, ssList, timings);
		return eData;
	}

	public String getName() {
		return expName;
	}

	private void buildSnapshots() {
		int injIndex = 0;
		LinkedList<ServiceCall> currentCalls;
		InjectedElement currentInj;
		snapList = new LinkedList<Snapshot>();
		for(Observation obs : obsList){
			currentCalls = new LinkedList<ServiceCall>();
			for(ServiceCall call : callList){
				if(call.isAliveAt(obs.getTimestamp()))
					currentCalls.add(call);
			}
			while(injList.size() > injIndex && injList.get(injIndex).getTimestamp().before(obs.getTimestamp())){
				injIndex++;
			}
			if(injList.size() > injIndex && injList.get(injIndex).getTimestamp().compareTo(obs.getTimestamp()) == 0)
				currentInj = injList.get(injIndex);
			else currentInj = null;		
			snapList.add(new Snapshot(obs, currentCalls, currentInj, ssList));
		}
		snapIterator = snapList.iterator();
	}
	
	public boolean hasNextSnapshot(){
		return snapIterator != null && snapIterator.hasNext();
	}
	
	public Snapshot nextSnapshot(){
		if(hasNextSnapshot())
			return snapIterator.next();
		else AppLogger.logError(getClass(), "NoSuchSnapshot", "Empty Snapshot list"); 
		return null;
	}

	public int obsNumber() {
		return obsList.size();
	}	
	
	public HashMap<String, ServiceStat> getServiceStats(){
		return ssList;
	}

	public LinkedList<String> getIndicatorNames() {
		LinkedList<String> indNames = new LinkedList<String>();
		if(obsList != null && obsList.size() > 0){
			for(Indicator ind : obsList.getFirst().getIndicators()){
				indNames.add(ind.getName());
			}
		}
		return indNames;
	}

	public LinkedList<Indicator> getNumericIndicators() {
		LinkedList<Indicator> indList = new LinkedList<Indicator>();
		if(obsList != null && obsList.size() > 0){
			for(Indicator ind : obsList.getFirst().getIndicators()){
				if(AppUtility.isNumber(obsList.getFirst().getValue(ind, IndicatorData.PLAIN_DATA_TAG)))
					indList.add(ind);
			}
		}
		return indList;
	}

	public void resetIterator() {
		snapIterator = snapList.iterator();
	}

	public LinkedList<InjectedElement> getInjections() {
		return injList;
	}
	
	public Date getFirstTimestamp(){
		return obsList.getFirst().getTimestamp();
	}

	public HashMap<String, HashMap<LayerType, LinkedList<Integer>>> getMonitorPerformanceIndexes() {
		return timings;
	}
	
	public HashMap<LayerType, Integer> getLayerIndicators(){
		HashMap<LayerType, Integer> layerInd = new HashMap<LayerType, Integer>();
		if(obsList.size() > 0){
			for(Indicator ind : obsList.getFirst().getIndicators()){
				if(layerInd.get(ind.getLayer()) == null)
					layerInd.put(ind.getLayer(), 0);
				layerInd.replace(ind.getLayer(), layerInd.get(ind.getLayer())+1);
			}
		}
		return layerInd;
	}
	
}
