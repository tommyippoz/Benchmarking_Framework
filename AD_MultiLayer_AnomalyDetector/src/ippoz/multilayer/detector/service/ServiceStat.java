/**
 * 
 */
package ippoz.multilayer.detector.service;

import java.util.HashMap;

/**
 * @author Tommy
 *
 */
public class ServiceStat {
	
	private String serviceName;
	private StatPair obsStat;
	private StatPair timeStat;
	private HashMap<String, IndicatorStat> indStat;
	
	public ServiceStat(String serviceName, StatPair obsStat, StatPair timeStat) {
		this.serviceName = serviceName;
		this.obsStat = obsStat;
		this.timeStat = timeStat;
		indStat = new HashMap<String, IndicatorStat>();
	}
	
	public void addIndicatorStat(IndicatorStat newStat){
		indStat.put(newStat.getName(), newStat);
	}

	public String getServiceName() {
		return serviceName;
	}

	public StatPair getObsStat() {
		return obsStat;
	}

	public StatPair getTimeStat() {
		return timeStat;
	}

	public IndicatorStat getIndStat(String indName) {
		return indStat.get(indName);
	}	

}
