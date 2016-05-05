/**
 * 
 */
package ippoz.multilayer.detector.algorithm;

import ippoz.multilayer.detector.configuration.AlgorithmConfiguration;
import ippoz.multilayer.detector.configuration.ConfidenceConfiguration;
import ippoz.multilayer.detector.configuration.HistoricalConfiguration;
import ippoz.multilayer.detector.configuration.RemoteCallConfiguration;
import ippoz.multilayer.detector.configuration.SPSConfiguration;
import ippoz.multilayer.detector.data.ExperimentData;
import ippoz.multilayer.detector.data.Indicator;
import ippoz.multilayer.detector.data.LayerType;
import ippoz.multilayer.detector.data.Snapshot;
import ippoz.multilayer.detector.service.StatPair;
import ippoz.multilayer.detector.support.AppLogger;

/**
 * @author Tommy
 *
 */
public abstract class DetectionAlgorithm {
	
	protected AlgorithmConfiguration conf;
	
	public DetectionAlgorithm(AlgorithmConfiguration conf){
		this.conf = conf;
	}
	
	public double snapshotAnomalyRate(Snapshot sysSnapshot){
		return anomalyTrueFalse(evaluateSnapshot(sysSnapshot))*getWeight();
	}
	
	protected abstract double evaluateSnapshot(Snapshot sysSnapshot);
	
	public double[] evaluateExperiment(ExperimentData expData){
		double[] res = new double[expData.obsNumber()];
		for(int i=0;i<expData.obsNumber();i++){
			if(expData.hasNextSnapshot())
				res[i] = evaluateSnapshot(expData.nextSnapshot());
		}
		return res;
	}
	
	public void printResults(String typeTag, String outFolderName, String expTag){
		if(typeTag.toUpperCase().equals("TEXT"))
			printTextResults(outFolderName, expTag);
		else if(typeTag.toUpperCase().equals("IMAGE"))
			printImageResults(outFolderName, expTag);
		else if(!typeTag.toUpperCase().equals("NULL")){
			AppLogger.logError(getClass(), "OutputTypeError", "Unable to recognize chosen output type");
		}
	}
	
	protected Double getWeight(){
		if(conf != null && conf.getItem(AlgorithmConfiguration.WEIGHT) != null)
			return Double.valueOf(conf.getItem(AlgorithmConfiguration.WEIGHT));
		else return 1.0;
	}

	protected abstract void printImageResults(String outFolderName, String expTag);

	protected abstract void printTextResults(String outFolderName, String expTag);
	
	protected double evaluateValue(Double value, StatPair stats, double varTimes){
		if(value >= (stats.getAvg() - varTimes*stats.getStd()) && value <= (stats.getAvg() + varTimes*stats.getStd()))
			return 0.0;
		else return 1.0;
	}
	
	protected double evaluateAbsDiff(Double value, StatPair stats, double varTimes){
		double outVal = Math.abs(value - stats.getAvg());
		outVal = outVal - varTimes*stats.getStd();
		if(outVal < 0)
			return 0.0;
		else return outVal;
	}
	
	protected double evaluateAbsDiffRate(Double value, StatPair stats, double varTimes){
		double outVal = Math.abs(value - stats.getAvg());
		outVal = outVal - varTimes*stats.getStd();
		if(outVal <= 0 || stats.getAvg() == 0.0)
			return 0.0;
		else return outVal/stats.getAvg();
	}
	
	protected double evaluateOverDiff(Double value, StatPair stats){
		double outVal = value - (stats.getAvg() + stats.getStd());
		if(outVal < 0)
			return 0.0;
		else return outVal;
	}
	
	protected double anomalyTrueFalse(double anomalyValue){
		if(anomalyValue > 0.0)
			return 1.0;
		else return 0.0;	
	}
	
	public static DetectionAlgorithm buildAlgorithm(String algTag, String dataType, Indicator indicator, AlgorithmConfiguration conf) {
		switch(algTag.toUpperCase()){
			case "SPS":
				return new SPSDetector(indicator, dataType, conf);
			case "HIST":
				return new HistoricalIndicatorChecker(indicator, dataType, conf);
			case "CONF":
				return new ConfidenceIntervalChecker(indicator, dataType, conf);
			case "RCC":
				return new RemoteCallChecker(conf);
			default:
				return null;
		}
	}

	public LayerType getLayerType() {
		if(this instanceof IndicatorDetectionAlgorithm){
			return ((IndicatorDetectionAlgorithm)this).getIndicatorLayer();
		} else return LayerType.NO_LAYER;
	}

	public String getAlgorithmType() {
		if (conf instanceof SPSConfiguration)
			return "SPS";
		else if (conf instanceof HistoricalConfiguration)
			return "HIST";
		else if (conf instanceof ConfidenceConfiguration)
			return "CONF";
		else if (conf instanceof RemoteCallConfiguration)
			return "RCC";
		else return "";
	}

	public AlgorithmConfiguration getConfiguration() {
		return conf;
	}

	public abstract String getDataType();

	public abstract Indicator getIndicator();

}
