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
public abstract class AlgorithmTrainer extends Thread implements Comparable<AlgorithmTrainer> {
	
	/** The algorithm tag. */
	private AlgorithmType algTag;	
	
	/** The involved data series. */
	private DataSeries dataSeries;
	
	/** The used metric. */
	private Metric metric;
	
	/** The used reputation metric. */
	private Reputation reputation;
	
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
	 */
	public AlgorithmTrainer(AlgorithmType algTag, DataSeries dataSeries, Metric metric, Reputation reputation, LinkedList<ExperimentData> trainData) {
		this.algTag = algTag;
		this.dataSeries = dataSeries;
		this.metric = metric;
		this.reputation = reputation;
		expList = deepClone(trainData);
	}
	
	private HashMap<String, LinkedList<Snapshot>> loadAlgExpSnapshots() {
		AlgorithmConfiguration refConf = null;
		if(bestConf != null)
			refConf = bestConf;
		else {
			// TODO
			// refConf = configurations.getFirst();
		}
		HashMap<String, LinkedList<Snapshot>> expAlgMap = new HashMap<String, LinkedList<Snapshot>>();
		for(ExperimentData expData : expList){
			//System.out.println(expData.getName());
			expAlgMap.put(expData.getName(), expData.buildSnapshotsFor(algTag, dataSeries, refConf));
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
		HashMap<String, LinkedList<Snapshot>> algExpSnapshots = loadAlgExpSnapshots();
		bestConf = lookForBestConfiguration(algExpSnapshots);
		metricScore = evaluateMetricScore(getExpList(), algExpSnapshots);
		reputationScore = evaluateReputationScore(getExpList(), algExpSnapshots);
		bestConf.addItem(AlgorithmConfiguration.WEIGHT, String.valueOf(getReputationScore()));
		bestConf.addItem(AlgorithmConfiguration.SCORE, String.valueOf(getMetricScore()));
	}
	
	protected abstract AlgorithmConfiguration lookForBestConfiguration(HashMap<String, LinkedList<Snapshot>> algExpSnapshots);

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
		DetectionAlgorithm algorithm = DetectionAlgorithm.buildAlgorithm(getAlgType(), dataSeries, bestConf);
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
		DetectionAlgorithm algorithm = DetectionAlgorithm.buildAlgorithm(getAlgType(), dataSeries, bestConf);
		for(ExperimentData expData : trainData){
			reputationResults.add(reputation.evaluateReputation(algorithm, algExpSnapshots.get(expData.getName())));
		}
		return AppUtility.calcAvg(reputationResults.toArray(new Double[reputationResults.size()]));
	}

	public DataSeries getDataSeries() {
		return dataSeries;
	}

	public Metric getMetric() {
		return metric;
	}

	public Reputation getReputation() {
		return reputation;
	}

	public LinkedList<ExperimentData> getExpList() {
		return expList;
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
