/**
 * 
 */
package ippoz.multilayer.detector.service;

import ippoz.multilayer.detector.support.AppUtility;

import java.util.Date;

/**
 * @author Tommy
 *
 */
public class ServiceCall {
	
	private String serviceName;
	private Date startTime;
	private Date endTime;
	private String responseCode;
	
	public ServiceCall(String serviceName, Date startTime, Date endTime, String responseCode) {
		this.serviceName = serviceName;
		this.startTime = startTime;
		this.endTime = endTime;
		this.responseCode = responseCode;
	}

	public ServiceCall(String serviceName, String startTime, String endTime, String responseCode) {
		this(serviceName, AppUtility.convertStringToDate(startTime), AppUtility.convertStringToDate(endTime), responseCode);
	}
	
	public String getServiceName(){
		return serviceName;
	}

	public Date getStartTime() {
		return startTime;
	}

	public Date getEndTime() {
		return endTime;
	}
	
	public String getResponseCode(){
		return responseCode;
	}
	
	public boolean isAliveAt(Date timestamp){
		return timestamp.getTime() >= startTime.getTime() && timestamp.getTime() <= endTime.getTime();
	}
	
}
