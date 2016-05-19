/**
 * 
 */
package ippoz.multilayer.detector.commons.data;

import ippoz.multilayer.commons.indicator.Indicator;
import ippoz.multilayer.commons.layers.LayerType;
import ippoz.multilayer.detector.commons.dataseries.DataSeries;
import ippoz.multilayer.detector.commons.failure.InjectedElement;
import ippoz.multilayer.detector.commons.service.ServiceCall;
import ippoz.multilayer.detector.commons.service.ServiceStat;
import ippoz.multilayer.detector.commons.support.AppLogger;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * The Class ExperimentData.
 * Stores information about the data of each experiment (service calls, snapshots, timings, injections, stats)
 *
 * @author Tommy
 */
public class ExperimentData implements Cloneable {
	
	/** The experiment name. */
	private String expName;
	
	/** The service call list. */
	private LinkedList<ServiceCall> callList;
	
	/** The injections list. */
	private LinkedList<InjectedElement> injList;
	
	/** The service statistics list. */
	private HashMap<String, ServiceStat> ssList;
	
	/** The timings. */
	private HashMap<String, HashMap<LayerType, LinkedList<Integer>>> timings;
	
	/** The observation list. */
	private LinkedList<Observation> obsList;
	
	/** The snapshot list. */
	private ArrayList<Snapshot> snapList;
	
	/** The snapshot iterator. */
	private Iterator<Snapshot> snapIterator;
	
	/**
	 * Instantiates a new experiment data.
	 *
	 * @param expID the experiment id
	 * @param obsList the observation list
	 * @param callList the service call list
	 * @param injList the injections list
	 * @param ssList the service stats list
	 * @param timings the timings
	 */
	public ExperimentData(String expID, LinkedList<Observation> obsList, LinkedList<ServiceCall> callList, LinkedList<InjectedElement> injList, HashMap<String, ServiceStat> ssList, HashMap<String, HashMap<LayerType, LinkedList<Integer>>> timings){
		this(expID, obsList, new ArrayList<Snapshot>(), callList, injList, ssList, timings);
		snapList = buildSnapshots(obsList);
		snapIterator = snapList.iterator();	
	}
	
	/**
	 * Instantiates a new experiment data.
	 *
	 * @param expID the experiment id
	 * @param snapList the snapshot list
	 * @param callList the service call list
	 * @param injList the injections list
	 * @param ssList the service stats list
	 * @param timings the timings
	 */
	public ExperimentData(String expID, LinkedList<Observation> obsList, ArrayList<Snapshot> snapList, LinkedList<ServiceCall> callList, LinkedList<InjectedElement> injList, HashMap<String, ServiceStat> ssList, HashMap<String, HashMap<LayerType, LinkedList<Integer>>> timings){
		expName = "exp" + expID;
		this.obsList = obsList;
		this.snapList = snapList;
		this.callList = callList;
		this.injList = injList;
		this.ssList = ssList;
		this.timings = timings;
		snapIterator = snapList.iterator();	
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public ExperimentData clone() throws CloneNotSupportedException {
		ExperimentData eData = new ExperimentData(expName, obsList, snapList, callList, injList, ssList, timings);
		return eData;
	}

	/**
	 * Gets the experiment name.
	 *
	 * @return the name
	 */
	public String getName() {
		return expName;
	}

	/**
	 * Builds the snapshots of the experiment depending on the observations.
	 */
	private ArrayList<Snapshot> buildSnapshots(LinkedList<Observation> obsList) {
		int injIndex = 0;
		LinkedList<ServiceCall> currentCalls;
		InjectedElement currentInj;
		ArrayList<Snapshot> builtSnap = new ArrayList<Snapshot>();
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
			builtSnap.add(new Snapshot(obs.getTimestamp(), currentCalls, currentInj, ssList));
		}
		return builtSnap;
	}
	
	/**
	 * Checks for next snapshot.
	 *
	 * @return true, if next snapshot exists
	 */
	public boolean hasNextSnapshot(){
		return snapIterator != null && snapIterator.hasNext();
	}
	
	/**
	 * Returns the next snapshot.
	 *
	 * @return the next snapshot
	 */
	public Snapshot nextSnapshot(){
		if(hasNextSnapshot())
			return snapIterator.next();
		else AppLogger.logError(getClass(), "NoSuchSnapshot", "Empty Snapshot list"); 
		return null;
	}

	/**
	 * Returns the snapshot number.
	 *
	 * @return the number of snapshots
	 */
	public int getSnapshotNumber() {
		return snapList.size();
	}	
	
	/**
	 * Gets the service stats.
	 *
	 * @return the service stats
	 */
	public HashMap<String, ServiceStat> getServiceStats(){
		return ssList;
	}

	/**
	 * Resets snapshot iterator.
	 */
	public void resetIterator() {
		snapIterator = snapList.iterator();
	}

	/**
	 * Gets the injections for this experiment.
	 *
	 * @return the injections
	 */
	public LinkedList<InjectedElement> getInjections() {
		return injList;
	}
	
	/**
	 * Gets the first timestamp.
	 *
	 * @return the first timestamp
	 */
	public Date getFirstTimestamp(){
		return snapList.get(0).getTimestamp();
	}

	/**
	 * Gets the monitor performance indexes.
	 *
	 * @return the monitor performance indexes
	 */
	public HashMap<String, HashMap<LayerType, LinkedList<Integer>>> getMonitorPerformanceIndexes() {
		return timings;
	}

	public HashMap<LayerType, Integer> getLayerIndicators(){
		HashMap<LayerType, Integer> layerInd = new HashMap<LayerType, Integer>();
		if(snapList.size() > 0){
			for(Indicator ind : obsList.get(0).getIndicators()){
				if(layerInd.get(ind.getLayer()) == null)
					layerInd.put(ind.getLayer(), 0);
				layerInd.replace(ind.getLayer(), layerInd.get(ind.getLayer())+1);
			}
		}
		return layerInd;
	}
	
	/**
	 * Gets the indicators.
	 *
	 * @return the indicators
	 */
	public Indicator[] getIndicators() {
		return obsList.get(0).getIndicators();
	}

	public DataSeriesSnapshot getDataSeriesSnapshot(DataSeries dataSeries, int index) {
		return new DataSeriesSnapshot(obsList.get(index), callList, snapList.get(index).getInjectedElement(), ssList, dataSeries); 
	}

	public Snapshot getSnapshot(int index) {
		return snapList.get(index);
	}
	
}
