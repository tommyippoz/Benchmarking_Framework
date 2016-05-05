/**
 * 
 */
package ippoz.multilayer.detector.algorithm;

import ippoz.multilayer.detector.configuration.AlgorithmConfiguration;
import ippoz.multilayer.detector.configuration.HistoricalConfiguration;
import ippoz.multilayer.detector.data.Indicator;
import ippoz.multilayer.detector.data.Snapshot;
import ippoz.multilayer.detector.service.IndicatorStat;
import ippoz.multilayer.detector.service.ServiceCall;
import ippoz.multilayer.detector.service.ServiceStat;
import ippoz.multilayer.detector.support.AppLogger;

import java.util.HashMap;

/**
 * @author Tommy
 *
 */
public class HistoricalIndicatorChecker extends IndicatorDetectionAlgorithm {
	
	public HistoricalIndicatorChecker(Indicator indicator, String categoryTag, AlgorithmConfiguration conf) {
		super(indicator, categoryTag, conf);
	}

	@Override
	public double evaluateSnapshot(Snapshot sysSnapshot) {
		double anomalyRate = 0.0;
		if(sysSnapshot.getServiceCalls().size() > 0){
			for(ServiceCall sCall : sysSnapshot.getServiceCalls()){
				if(sysSnapshot.getTimestamp().compareTo(sCall.getStartTime()) == 0)
					anomalyRate = anomalyRate + analyzeCallStart(sysSnapshot.getObservation().getValue(indicator.getName(), categoryTag), sCall, sysSnapshot.getServiceStatList());
				else if(sysSnapshot.getTimestamp().compareTo(sCall.getEndTime()) == 0)
					anomalyRate = anomalyRate + analyzeCallEnd(sysSnapshot.getObservation().getValue(indicator.getName(), categoryTag), sCall, sysSnapshot.getServiceStatList());
				else if(sysSnapshot.getTimestamp().after(sCall.getStartTime()) && sysSnapshot.getTimestamp().before(sCall.getEndTime()))
					anomalyRate = anomalyRate + analyzeCallRun(sysSnapshot.getObservation().getValue(indicator.getName(), categoryTag), sCall, sysSnapshot.getServiceStatList());
			}
			return anomalyRate / sysSnapshot.getServiceCalls().size();
		} else return 0;
	}

	private double analyzeCallRun(String strValue, ServiceCall sCall, HashMap<String, ServiceStat> ssList) {
		IndicatorStat obs = ssList.get(sCall.getServiceName()).getIndStat(indicator.getName());
		if(obs != null)
			return evaluateAbsDiffRate(Double.valueOf(strValue), ssList.get(sCall.getServiceName()).getIndStat(indicator.getName()).getAllObs(), Double.valueOf(conf.getItem(HistoricalConfiguration.INTERVAL_WIDTH)));
		else AppLogger.logError(getClass(), "StatError", "Unable to find Stat for " + sCall.getServiceName() + ":" + indicator.getName());
		return 0.0;
	}

	private double analyzeCallEnd(String strValue, ServiceCall sCall, HashMap<String, ServiceStat> ssList) {
		IndicatorStat obs = ssList.get(sCall.getServiceName()).getIndStat(indicator.getName());
		if(obs != null)
			return evaluateAbsDiffRate(Double.valueOf(strValue), ssList.get(sCall.getServiceName()).getIndStat(indicator.getName()).getLastObs(), Double.valueOf(conf.getItem(HistoricalConfiguration.INTERVAL_WIDTH)));
		else AppLogger.logError(getClass(), "StatError", "Unable to find Stat for " + sCall.getServiceName() + ":" + indicator.getName());
		return 0.0;
	}

	private double analyzeCallStart(String strValue, ServiceCall sCall, HashMap<String, ServiceStat> ssList) {
		IndicatorStat obs = ssList.get(sCall.getServiceName()).getIndStat(indicator.getName());
		if(obs != null)
			return evaluateAbsDiffRate(Double.valueOf(strValue), ssList.get(sCall.getServiceName()).getIndStat(indicator.getName()).getFirstObs(), Double.valueOf(conf.getItem(HistoricalConfiguration.INTERVAL_WIDTH)));
		else AppLogger.logError(getClass(), "StatError", "Unable to find Stat for " + sCall.getServiceName() + ":" + indicator.getName());
			return 0.0;
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
	public String getIndicatorFullName() {
		return indicator.getName() + "#" + categoryTag + "_HIST";
	}

}
