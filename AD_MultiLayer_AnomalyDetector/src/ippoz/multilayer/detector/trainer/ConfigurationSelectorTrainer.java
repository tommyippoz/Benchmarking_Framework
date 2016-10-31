/**
 * 
 */
package ippoz.multilayer.detector.trainer;

import ippoz.multilayer.detector.algorithm.DetectionAlgorithm;
import ippoz.multilayer.detector.commons.algorithm.AlgorithmType;
import ippoz.multilayer.detector.commons.configuration.AlgorithmConfiguration;
import ippoz.multilayer.detector.commons.data.ExperimentData;
import ippoz.multilayer.detector.commons.data.Snapshot;
import ippoz.multilayer.detector.commons.dataseries.DataSeries;
import ippoz.multilayer.detector.commons.support.AppLogger;
import ippoz.multilayer.detector.commons.support.AppUtility;
import ippoz.multilayer.detector.metric.Metric;
import ippoz.multilayer.detector.reputation.Reputation;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author Tommy
 *
 */
public class ConfigurationSelectorTrainer extends AlgorithmTrainer {

	/** The possible configurations. */
	private LinkedList<AlgorithmConfiguration> configurations;
	
	/**
	 * Instantiates a new algorithm trainer.
	 *
	 * @param algTag the algorithm tag
	 * @param indicator the involved indicator
	 * @param categoryTag the data category tag
	 * @param metric the used metric
	 * @param reputation the used reputation metric
	 * @param trainData the considered train data
	 */
	public ConfigurationSelectorTrainer(AlgorithmType algTag, DataSeries dataSeries, Metric metric, Reputation reputation, LinkedList<ExperimentData> trainData, LinkedList<AlgorithmConfiguration> basicConfigurations) {
		super(algTag, dataSeries, metric, reputation, trainData);
		configurations = confClone(basicConfigurations);
	}
	
	private LinkedList<AlgorithmConfiguration> confClone(LinkedList<AlgorithmConfiguration> inConf) {
		LinkedList<AlgorithmConfiguration> list = new LinkedList<AlgorithmConfiguration>();
		try {
			for(AlgorithmConfiguration conf : inConf){
				list.add((AlgorithmConfiguration) conf.clone());
			}
		} catch (CloneNotSupportedException ex) {
			AppLogger.logException(getClass(), ex, "Unable to clone Configurations");
		}
		return list;
	}

	@Override
	protected AlgorithmConfiguration lookForBestConfiguration(HashMap<String, LinkedList<Snapshot>> algExpSnapshots) {
		Double bestMetricValue = Double.NaN;
		Double currentMetricValue;
		LinkedList<Double> metricResults;
		DetectionAlgorithm algorithm;
		AlgorithmConfiguration bestConf = null;
		try {
			for(AlgorithmConfiguration conf : configurations){
				metricResults = new LinkedList<Double>();
				algorithm = DetectionAlgorithm.buildAlgorithm(getAlgType(), getDataSeries(), conf);
				for(ExperimentData expData : getExpList()){
					metricResults.add(getMetric().evaluateMetric(algorithm, algExpSnapshots.get(expData.getName()))[0]);
				}
				currentMetricValue = AppUtility.calcAvg(metricResults.toArray(new Double[metricResults.size()]));
				if(bestMetricValue.isNaN() || getMetric().compareResults(currentMetricValue, bestMetricValue) == 1){
					bestMetricValue = currentMetricValue;
					bestConf = (AlgorithmConfiguration) conf.clone();
				}
			}
			
		} catch (CloneNotSupportedException ex) {
			AppLogger.logException(getClass(), ex, "Unable to clone configuration");
		}
		return bestConf;
	}
}
