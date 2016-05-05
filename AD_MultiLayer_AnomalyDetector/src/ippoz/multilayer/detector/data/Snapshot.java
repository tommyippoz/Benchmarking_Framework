/**
 * 
 */
package ippoz.multilayer.detector.data;

import ippoz.multilayer.detector.failure.InjectedElement;
import ippoz.multilayer.detector.service.ServiceCall;
import ippoz.multilayer.detector.service.ServiceStat;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author Tommy
 *
 */
public class Snapshot {
	
	private Observation obs;
	private LinkedList<ServiceCall> sCall;
	private InjectedElement injEl;
	private HashMap<String, ServiceStat> ssList;
	
	public Snapshot(Observation obs, LinkedList<ServiceCall> currentCalls, InjectedElement injEl, HashMap<String, ServiceStat> ssList) {
		this.obs = obs;
		this.sCall = currentCalls;
		this.injEl = injEl;
		this.ssList = ssList;
	}
	
	public Date getTimestamp(){
		return obs.getTimestamp();
	}
	
	public HashMap<String, ServiceStat> getServiceStatList(){
		return ssList;
	}
	
	public Observation getObservation() {
		return obs;
	}
	
	public LinkedList<ServiceCall> getServiceCalls() {
		return sCall;
	}
	
	public InjectedElement getInjectedElement() {
		return injEl;
	}
	
	
	
}
