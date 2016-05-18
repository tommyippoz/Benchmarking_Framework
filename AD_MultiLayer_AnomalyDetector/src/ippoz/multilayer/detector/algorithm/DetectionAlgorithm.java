/**
 * 
 */
package ippoz.multilayer.detector.algorithm;

import ippoz.multilayer.detector.commons.data.Snapshot;
import ippoz.multilayer.detector.commons.dataseries.DataSeries;
import ippoz.multilayer.detector.commons.service.StatPair;
import ippoz.multilayer.detector.commons.support.AppLogger;
import ippoz.multilayer.detector.configuration.AlgorithmConfiguration;
import ippoz.multilayer.detector.configuration.ConfidenceConfiguration;
import ippoz.multilayer.detector.configuration.HistoricalConfiguration;
import ippoz.multilayer.detector.configuration.InvariantConfiguration;
import ippoz.multilayer.detector.configuration.RemoteCallConfiguration;
import ippoz.multilayer.detector.configuration.SPSConfiguration;
import ippoz.multilayer.detector.configuration.WesternElectricRulesConfiguration;

/**
 * The Class DetectionAlgorithm.
 *
 * @author Tommy
 */
public abstract class DetectionAlgorithm {
	
	/** The conf. */
	protected AlgorithmConfiguration conf;
	
	/**
	 * Instantiates a new detection algorithm.
	 *
	 * @param conf the configuration
	 */
	public DetectionAlgorithm(AlgorithmConfiguration conf){
		this.conf = conf;
	}
	
	/**
	 * Converts a double score into a 0-1 one.
	 *
	 * @param anomalyValue the anomaly value
	 * @return the double
	 */
	protected static double anomalyTrueFalse(double anomalyValue){
		if(anomalyValue > 0.0)
			return 1.0;
		else return 0.0;	
	}
	
	/**
	 * Builds a DetectionAlgorithm.
	 *
	 * @param algTag the algorithm tag
	 * @param dataType the data type
	 * @param indicator the indicator
	 * @param conf the configuration
	 * @return the detection algorithm
	 */
	public static DetectionAlgorithm buildAlgorithm(String algTag, DataSeries dataSeries, AlgorithmConfiguration conf) {
		switch(algTag.toUpperCase()){
			case "SPS":
				return new SPSDetector(dataSeries, conf);
			case "HIST":
				return new HistoricalIndicatorChecker(dataSeries, conf);
			case "CONF":
				return new ConfidenceIntervalChecker(dataSeries, conf);
			case "RCC":
				return new RemoteCallChecker(conf);
			case "WER":
				return new WesternElectricRulesChecker(dataSeries, conf);
			case "INV":
				return new InvariantChecker(conf);
			default:
				return null;
		}
	}
	
	/**
	 * Defines the anomaly rate of a given snapshot.
	 *
	 * @param sysSnapshot the given snapshot
	 * @return the anomaly rate of the snapshot
	 */
	public double snapshotAnomalyRate(Snapshot sysSnapshot){
		return anomalyTrueFalse(evaluateSnapshot(sysSnapshot))*getWeight();
	}
	
	/**
	 * Evaluates a snapshot.
	 *
	 * @param sysSnapshot the snapshot
	 * @return the result of the evaluation
	 */
	protected abstract double evaluateSnapshot(Snapshot sysSnapshot);
	
	/**
	 * Prints the results of the detection.
	 *
	 * @param typeTag the output type tag
	 * @param outFolderName the output folder name
	 * @param expTag the experiment tag
	 */
	public void printResults(String typeTag, String outFolderName, String expTag){
		if(typeTag.toUpperCase().equals("TEXT"))
			printTextResults(outFolderName, expTag);
		else if(typeTag.toUpperCase().equals("IMAGE"))
			printImageResults(outFolderName, expTag);
		else if(!typeTag.toUpperCase().equals("NULL")){
			AppLogger.logError(getClass(), "OutputTypeError", "Unable to recognize chosen output type");
		}
	}
	
	/**
	 * Gets the weight of the algorithm.
	 *
	 * @return the weight
	 */
	protected Double getWeight(){
		if(conf != null && conf.getItem(AlgorithmConfiguration.WEIGHT) != null)
			return Double.valueOf(conf.getItem(AlgorithmConfiguration.WEIGHT));
		else return 1.0;
	}

	/**
	 * Prints the image results.
	 *
	 * @param outFolderName the out folder name
	 * @param expTag the exp tag
	 */
	protected abstract void printImageResults(String outFolderName, String expTag);

	/**
	 * Prints the text results.
	 *
	 * @param outFolderName the out folder name
	 * @param expTag the exp tag
	 */
	protected abstract void printTextResults(String outFolderName, String expTag);
	
	/**
	 * Evaluates a value.
	 *
	 * @param value the value
	 * @param stats the stats
	 * @param varTimes the var times
	 * @return the double
	 */
	protected double evaluateValue(Double value, StatPair stats, double varTimes){
		if(value >= (stats.getAvg() - varTimes*stats.getStd()) && value <= (stats.getAvg() + varTimes*stats.getStd()))
			return 0.0;
		else return 1.0;
	}
	
	/**
	 * Evaluates absolute difference.
	 *
	 * @param value the value
	 * @param stats the stats
	 * @param varTimes the tolerance (the range is defined by std*tolerance)
	 * @return the evaluation
	 */
	protected double evaluateAbsDiff(Double value, StatPair stats, double varTimes){
		double outVal = Math.abs(value - stats.getAvg());
		outVal = outVal - varTimes*stats.getStd();
		if(outVal < 0)
			return 0.0;
		else return outVal;
	}
	
	/**
	 * Evaluate absolute difference rate.
	 *
	 * @param value the value
	 * @param stats the stats
	 * @param varTimes the tolerance (the range is defined by std*tolerance)
	 * @return the evaluation
	 */
	protected double evaluateAbsDiffRate(Double value, StatPair stats, double varTimes){
		double outVal = Math.abs(value - stats.getAvg());
		outVal = outVal - varTimes*stats.getStd();
		if(outVal <= 0 || stats.getAvg() == 0.0)
			return 0.0;
		else return outVal/stats.getAvg();
	}
	
	/**
	 * Evaluate over diff.
	 *
	 * @param value the value
	 * @param stats the stats
	 * @return the evaluation
	 */
	protected double evaluateOverDiff(Double value, StatPair stats){
		double outVal = value - (stats.getAvg() + stats.getStd());
		if(outVal < 0)
			return 0.0;
		else return outVal;
	}
	
	/**
	 * Gets the algorithm type.
	 *
	 * @return the algorithm type
	 */
	public String getAlgorithmType() {
		if (conf instanceof SPSConfiguration)
			return "SPS";
		else if (conf instanceof HistoricalConfiguration)
			return "HIST";
		else if (conf instanceof ConfidenceConfiguration)
			return "CONF";
		else if (conf instanceof RemoteCallConfiguration)
			return "RCC";
		else if (conf instanceof WesternElectricRulesConfiguration)
			return "WER";
		else if (conf instanceof InvariantConfiguration)
			return "INV";
		else return "";
	}

	/**
	 * Gets the configuration.
	 *
	 * @return the configuration
	 */
	public AlgorithmConfiguration getConfiguration() {
		return conf;
	}

	/**
	 * Gets the data series.
	 *
	 * @return the data series
	 */
	public abstract DataSeries getDataSeries();

}
