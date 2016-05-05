/**
 * 
 */
package ippoz.multilayer.detector.algorithm;

import ippoz.multilayer.detector.configuration.AlgorithmConfiguration;
import ippoz.multilayer.detector.data.Indicator;
import ippoz.multilayer.detector.data.Snapshot;
import ippoz.multilayer.detector.service.ServiceCall;
import ippoz.multilayer.detector.service.ServiceStat;
import ippoz.multilayer.detector.support.AppUtility;

import java.util.Date;

/**
 * @author Tommy
 *
 */
public class RemoteCallChecker extends DetectionAlgorithm {
	
	private static String WEIGHT_TAG = "rcc_weight";

	private double weight;
	
	public RemoteCallChecker(double weight) {
		super(null);
		this.weight = weight;
	}

	public RemoteCallChecker(AlgorithmConfiguration conf) {
		super(conf);
		weight = Double.parseDouble(conf.getItem(WEIGHT_TAG));
	}

	@Override
	public double evaluateSnapshot(Snapshot sysSnapshot) {
		double evalResult = 0.0;
		for(ServiceCall call : sysSnapshot.getServiceCalls()){
			evalResult = evalResult + analyzeServiceCall(sysSnapshot.getTimestamp(), call, sysSnapshot.getServiceStatList().get(call.getServiceName()));
		}
		return evalResult / sysSnapshot.getServiceCalls().size();
	}

	private double analyzeServiceCall(Date snapTime, ServiceCall call, ServiceStat serviceStat) {
		if(call.getEndTime().compareTo(snapTime) == 0){
			if(!call.getResponseCode().equals("200"))
				return weight;
			else return evaluateAbsDiff(AppUtility.getSecondsBetween(call.getEndTime(), call.getStartTime()), serviceStat.getTimeStat(), 1.0);
		} else return evaluateOverDiff(AppUtility.getSecondsBetween(snapTime, call.getStartTime()), serviceStat.getTimeStat());
	}

	@Override
	protected void printImageResults(String outFolderName, String expTag) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void printTextResults(String outFolderName, String expTag) {
		// TODO Auto-generated method stub
	}

	@Override
	public Double getWeight() {
		return weight;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName();
	}

	@Override
	public String getDataType() {
		return null;
	}

	@Override
	public Indicator getIndicator() {
		return null;
	}
	
	

}
