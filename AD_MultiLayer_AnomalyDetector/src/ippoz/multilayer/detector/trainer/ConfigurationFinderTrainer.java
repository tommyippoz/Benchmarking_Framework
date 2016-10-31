/**
 * 
 */
package ippoz.multilayer.detector.trainer;

import ippoz.multilayer.commons.support.AppLogger;
import ippoz.multilayer.detector.algorithm.AutomaticTrainingAlgorithm;
import ippoz.multilayer.detector.algorithm.DetectionAlgorithm;
import ippoz.multilayer.detector.commons.algorithm.AlgorithmType;
import ippoz.multilayer.detector.commons.configuration.AlgorithmConfiguration;
import ippoz.multilayer.detector.commons.data.ExperimentData;
import ippoz.multilayer.detector.commons.data.Snapshot;
import ippoz.multilayer.detector.commons.dataseries.DataSeries;
import ippoz.multilayer.detector.metric.Metric;
import ippoz.multilayer.detector.reputation.Reputation;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author Tommy
 *
 */
public class ConfigurationFinderTrainer extends AlgorithmTrainer {

	public ConfigurationFinderTrainer(AlgorithmType algTag, DataSeries dataSeries, Metric metric, Reputation reputation, LinkedList<ExperimentData> trainData) {
		super(algTag, dataSeries, metric, reputation, trainData);
	}

	@Override
	protected AlgorithmConfiguration lookForBestConfiguration(HashMap<String, LinkedList<Snapshot>> algExpSnapshots) {
		DetectionAlgorithm da = DetectionAlgorithm.buildAlgorithm(getAlgType(), getDataSeries(), null);
		if(da instanceof AutomaticTrainingAlgorithm)
			return ((AutomaticTrainingAlgorithm)da).automaticTraining();
		else {
			AppLogger.logError(getClass(), "TrainingError", "Algorithm " + getAlgType() + " is not an automatic training algorithm");
			return null;
		}
	}

}
