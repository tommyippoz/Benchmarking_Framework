/**
 * 
 */
package ippoz.multilayer.detector.trainer;

import ippoz.multilayer.commons.datacategory.DataCategory;
import ippoz.multilayer.commons.layers.LayerType;
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
 * The Class AlgorithmTrainer.
 * Base class for each algorithm scorer. Extends Thread.
 *
 * @author Tommy
 */
public class AlgorithmTrainer extends Thread implements Comparable<AlgorithmTrainer> {
	
	/** The algorithm tag. */
	private AlgorithmType algTag;	
	
	/** The involved data series. */
	private DataSeries dataSeries;
	
	/** The used metric. */
	private Metric metric;
	
	/** The used reputation metric. */
	private Reputation reputation;
	
	/** The possible configurations. */
	private HashMap<AlgorithmType, LinkedList<AlgorithmConfiguration>> configurations;
	
	/** The experiments' list. */
	private LinkedList<ExperimentData> expList;
	
	/** The best configuration. */
	private AlgorithmConfiguration bestConf;
	
	/** The metric score. */
	private double metricScore;
	
	/** The reputation score. */
	private double reputationScore;
	
	/** Flag that indicates if the trained algorithm retrieves different values (e.g., not always true / false). */
	private boolean sameResultFlag;
	
	/**
	 * Instantiates a new algorithm trainer.
	 *
	 * @param algTag the algorithm tag
	 * @param indicator the involved indicator
	 * @param categoryTag the data category tag
	 * @param metric the used metric
	 * @param reputation the used reputation metric
	 * @param trainData the considered train data
	 * @param configurations the possible configurations
	 */
	public AlgorithmTrainer(AlgorithmType algTag, DataSeries dataSeries, Metric metric, Reputation reputation, LinkedList<ExperimentData> trainData, HashMap<AlgorithmType, LinkedList<AlgorithmConfiguration>> configurations) {
		this.algTag = algTag;
		this.dataSeries = dataSeries;
		this.metric = metric;
		this.reputation = reputation;
		this.configurations = configurations;
		expList = deepClone(trainData);
	}
	
		/**
	 * Instantiates a new algorithm trainer.
	 *
	 * @param algTag the algorithm tag
	 * @param indicator the involved indicator
	 * @param categoryTag the data category tag
	 * @param metric the used metric
	 * @param reputation the used reputation metric
	 * @param trainData the considered train data
	 * @param configurations the possible configurations
	 */
	public AlgorithmTrainer(AlgorithmType algTag, DataSeries dataSeries, Metric metric, Reputation reputation, LinkedList<ExperimentData> trainData, AlgorithmConfiguration configuration) {
		this.algTag = algTag;
		this.dataSeries = dataSeries;
		this.metric = metric;
		this.reputation = reputation;
		expList = deepClone(trainData);
		configurations = new HashMap<AlgorithmType, LinkedList<AlgorithmConfiguration>>();
		configurations.put(algTag, new LinkedList<AlgorithmConfiguration>());
		configurations.get(algTag).add(configuration);
		bestConf = configuration;
	}
	
	private HashMap<String, LinkedList<Snapshot>> loadAlgExpSnapshots() {
		HashMap<String, LinkedList<Snapshot>> expAlgMap = new HashMap<String, LinkedList<Snapshot>>();
		for(ExperimentData expData : expList){
			expAlgMap.put(expData.getName(), expData.buildSnapshotsFor(algTag, dataSeries, bestConf));
		}
		return expAlgMap;
	}
	
	/**
	 * Deep clone of the experiment list.
	 *
	 * @param trainData the train data
	 * @return the cloned experiment list
	 */
	private LinkedList<ExperimentData> deepClone(LinkedList<ExperimentData> trainData) {
		LinkedList<ExperimentData> list = new LinkedList<ExperimentData>();
		try {
			for(ExperimentData eData : trainData){
				list.add(eData.clone());
			}
		} catch (CloneNotSupportedException ex) {
			AppLogger.logException(getClass(), ex, "Unable to clone Experiment");
		}
		return list;
	}
	
	public boolean isValidTrain(){
		return !sameResultFlag;
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		Double bestMetricValue = Double.NaN;
		Double currentMetricValue;
		LinkedList<Double> metricResults;
		DetectionAlgorithm algorithm;
		HashMap<String, LinkedList<Snapshot>> algExpSnapshots = loadAlgExpSnapshots();
		for(AlgorithmConfiguration conf : configurations.get(algTag)){
			metricResults = new LinkedList<Double>();
			algorithm = DetectionAlgorithm.buildAlgorithm(dataSeries, conf);
			for(ExperimentData expData : expList){
				metricResults.add(metric.evaluateMetric(algorithm, algExpSnapshots.get(expData.getName()))[0]);
			}
			currentMetricValue = AppUtility.calcAvg(metricResults.toArray(new Double[metricResults.size()]));
			if(bestMetricValue.isNaN() || metric.compareResults(currentMetricValue, bestMetricValue) == 1){
				bestMetricValue = currentMetricValue;
				bestConf = conf;
			}
		}
		metricScore = evaluateMetricScore(expList, algExpSnapshots);
		reputationScore = evaluateReputationScore(expList, algExpSnapshots);
		bestConf.addItem(AlgorithmConfiguration.WEIGHT, String.valueOf(reputationScore));
		bestConf.addItem(AlgorithmConfiguration.SCORE, String.valueOf(metricScore));
	}

	/**
	 * Evaluates metric score on a specified set of experiments.
	 *
	 * @param trainData the train data
	 * @param algExpSnapshots 
	 * @return the metric score
	 */
	private double evaluateMetricScore(LinkedList<ExperimentData> trainData, HashMap<String, LinkedList<Snapshot>> algExpSnapshots){
		double[] metricEvaluation = null;
		LinkedList<Double> metricResults = new LinkedList<Double>();
		LinkedList<Double> algResults = new LinkedList<Double>();
		DetectionAlgorithm algorithm = DetectionAlgorithm.buildAlgorithm(dataSeries, bestConf);
		for(ExperimentData expData : trainData){
			metricEvaluation = metric.evaluateMetric(algorithm, algExpSnapshots.get(expData.getName()));
			metricResults.add(metricEvaluation[0]);
			algResults.add(metricEvaluation[1]);
		}
		sameResultFlag = AppUtility.calcStd(algResults, AppUtility.calcAvg(algResults)) == 0.0;
		return AppUtility.calcAvg(metricResults.toArray(new Double[metricResults.size()]));
	}
	
	/**
	 * Evaluate reputation score on a specified set of experiments.
	 *
	 * @param trainData the train data
	 * @return the reputation score
	 */
	private double evaluateReputationScore(LinkedList<ExperimentData> trainData, HashMap<String, LinkedList<Snapshot>> algExpSnapshots){
		LinkedList<Double> reputationResults = new LinkedList<Double>();
		DetectionAlgorithm algorithm = DetectionAlgorithm.buildAlgorithm(dataSeries, bestConf);
		for(ExperimentData expData : trainData){
			reputationResults.add(reputation.evaluateReputation(algorithm, algExpSnapshots.get(expData.getName())));
		}
		return AppUtility.calcAvg(reputationResults.toArray(new Double[reputationResults.size()]));
	}

	/**
	 * Gets the metric score.
	 *
	 * @return the metric score
	 */
	public double getMetricScore() {
		return metricScore;
	}
	
	/**
	 * Gets the reputation score.
	 *
	 * @return the reputation score
	 */
	public double getReputationScore() {
		return reputationScore;
	}
	
	/**
	 * Gets the best configuration.
	 *
	 * @return the best configuration
	 */
	public AlgorithmConfiguration getBestConfiguration(){
		return bestConf;
	}
	
	/**
	 * Gets the series name.
	 *
	 * @return the series name
	 */
	public String getSeriesName(){
		if(dataSeries != null)
			return dataSeries.getName();
		else return null;
	}
	
	/**
	 * Gets the layer.
	 *
	 * @return the layer
	 */
	public LayerType getLayerType(){
		if(dataSeries != null)
			return dataSeries.getLayerType();
		else return null;
	}
	
	/**
	 * Gets the series name.
	 *
	 * @return the series name
	 */
	public DataCategory getDataCategory(){
		if(dataSeries != null)
			return dataSeries.getDataCategory();
		else return null;
	}

	/**
	 * Gets the algorithm type.
	 *
	 * @return the algorithm type
	 */
	public AlgorithmType getAlgType(){
		return algTag;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	@Override
	public int compareTo(AlgorithmTrainer other) {
		return Double.compare(other.getMetricScore(), getMetricScore());
	}

	public String getSeriesDescription() {
		if(dataSeries != null)
			return dataSeries.toString();
		else return "Default";
	}
	
}
