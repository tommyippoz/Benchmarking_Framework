/**
 * 
 */
package ippoz.multilayer.detector.algorithm;

import ippoz.multilayer.commons.datacategory.DataCategory;
import ippoz.multilayer.commons.indicator.Indicator;
import ippoz.multilayer.detector.commons.data.Snapshot;
import ippoz.multilayer.detector.commons.service.IndicatorStat;
import ippoz.multilayer.detector.commons.service.ServiceCall;
import ippoz.multilayer.detector.commons.service.ServiceStat;
import ippoz.multilayer.detector.commons.support.AppLogger;
import ippoz.multilayer.detector.configuration.AlgorithmConfiguration;
import ippoz.multilayer.detector.configuration.HistoricalConfiguration;

import java.util.HashMap;

/**
 * The Class HistoricalIndicatorChecker.
 * Defines a Checker that is able to evaluate if the observation is in the range of (avg,std)
 *
 * @author Tommy
 */
public class HistoricalIndicatorChecker extends IndicatorDetectionAlgorithm {
	
	/**
	 * Instantiates a new historical indicator checker.
	 *
	 * @param indicator the indicator
	 * @param categoryTag the data category tag
	 * @param conf the configuration
	 */
	public HistoricalIndicatorChecker(Indicator indicator, DataCategory categoryTag, AlgorithmConfiguration conf) {
		super(indicator, categoryTag, conf);
	}

	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.algorithm.DetectionAlgorithm#evaluateSnapshot(ippoz.multilayer.detector.data.Snapshot)
	 */
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

	/**
	 * Analyse call during running.
	 *
	 * @param strValue the string value
	 * @param sCall the service call
	 * @param ssList the service stat list
	 * @return the result of the evaluation
	 */
	private double analyzeCallRun(String strValue, ServiceCall sCall, HashMap<String, ServiceStat> ssList) {
		IndicatorStat obs = ssList.get(sCall.getServiceName()).getIndStat(indicator.getName());
		if(obs != null)
			return evaluateAbsDiffRate(Double.valueOf(strValue), ssList.get(sCall.getServiceName()).getIndStat(indicator.getName()).getAllObs(), Double.valueOf(conf.getItem(HistoricalConfiguration.INTERVAL_WIDTH)));
		else AppLogger.logError(getClass(), "StatError", "Unable to find Stat for " + sCall.getServiceName() + ":" + indicator.getName());
		return 0.0;
	}

	/**
	 * Analyse the last observation of the service call.
	 *
	 * @param strValue the string value
	 * @param sCall the service call
	 * @param ssList the service stat list
	 * @return the result of the evaluation
	 */
	private double analyzeCallEnd(String strValue, ServiceCall sCall, HashMap<String, ServiceStat> ssList) {
		IndicatorStat obs = ssList.get(sCall.getServiceName()).getIndStat(indicator.getName());
		if(obs != null)
			return evaluateAbsDiffRate(Double.valueOf(strValue), ssList.get(sCall.getServiceName()).getIndStat(indicator.getName()).getLastObs(), Double.valueOf(conf.getItem(HistoricalConfiguration.INTERVAL_WIDTH)));
		else AppLogger.logError(getClass(), "StatError", "Unable to find Stat for " + sCall.getServiceName() + ":" + indicator.getName());
		return 0.0;
	}

	/**
	 * Analyse the first call observation.
	 *
	 * @param strValue the string value
	 * @param sCall the service call
	 * @param ssList the service stat list
	 * @return the result of the evaluation
	 */
	private double analyzeCallStart(String strValue, ServiceCall sCall, HashMap<String, ServiceStat> ssList) {
		IndicatorStat obs = ssList.get(sCall.getServiceName()).getIndStat(indicator.getName());
		if(obs != null)
			return evaluateAbsDiffRate(Double.valueOf(strValue), ssList.get(sCall.getServiceName()).getIndStat(indicator.getName()).getFirstObs(), Double.valueOf(conf.getItem(HistoricalConfiguration.INTERVAL_WIDTH)));
		else AppLogger.logError(getClass(), "StatError", "Unable to find Stat for " + sCall.getServiceName() + ":" + indicator.getName());
			return 0.0;
	}

	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.algorithm.DetectionAlgorithm#printImageResults(java.lang.String, java.lang.String)
	 */
	@Override
	protected void printImageResults(String outFolderName, String expTag) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.algorithm.DetectionAlgorithm#printTextResults(java.lang.String, java.lang.String)
	 */
	@Override
	protected void printTextResults(String outFolderName, String expTag) {
		// TODO Auto-generated method stub
		
	}

}
