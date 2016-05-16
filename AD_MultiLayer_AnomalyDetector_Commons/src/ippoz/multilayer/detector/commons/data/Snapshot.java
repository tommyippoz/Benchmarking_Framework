/**
 * 
 */
package ippoz.multilayer.detector.commons.data;

import ippoz.multilayer.detector.commons.failure.InjectedElement;
import ippoz.multilayer.detector.commons.service.ServiceCall;
import ippoz.multilayer.detector.commons.service.ServiceStat;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * The Class Snapshot.
 * Stores data related to a single observation of a target system, enriching it with general information about the system (serviceCalls, injections, serviceStats).
 *
 * @author Tommy
 */
public class Snapshot {
	
	/** The observation. */
	private Observation obs;
	
	/** The list of services called at that time instant. */
	private LinkedList<ServiceCall> sCall;
	
	/** The injection at that time instant. */
	private InjectedElement injEl;
	
	/** The service stat list. */
	private HashMap<String, ServiceStat> ssList;
	
	/**
	 * Instantiates a new snapshot.
	 *
	 * @param obs the observation
	 * @param currentCalls the current calls
	 * @param injEl the injection
	 * @param ssList the service stat list
	 */
	public Snapshot(Observation obs, LinkedList<ServiceCall> currentCalls, InjectedElement injEl, HashMap<String, ServiceStat> ssList) {
		this.obs = obs;
		this.sCall = currentCalls;
		this.injEl = injEl;
		this.ssList = ssList;
	}
	
	/**
	 * Gets the timestamp of that snapshot.
	 *
	 * @return the timestamp
	 */
	public Date getTimestamp(){
		return obs.getTimestamp();
	}
	
	/**
	 * Gets the service stat list.
	 *
	 * @return the service stat list
	 */
	public HashMap<String, ServiceStat> getServiceStatList(){
		return ssList;
	}
	
	/**
	 * Gets the observation.
	 *
	 * @return the observation
	 */
	public Observation getObservation() {
		return obs;
	}
	
	/**
	 * Gets the service calls.
	 *
	 * @return the service calls
	 */
	public LinkedList<ServiceCall> getServiceCalls() {
		return sCall;
	}
	
	/**
	 * Gets the injected element.
	 *
	 * @return the injected element
	 */
	public InjectedElement getInjectedElement() {
		return injEl;
	}
	
}
