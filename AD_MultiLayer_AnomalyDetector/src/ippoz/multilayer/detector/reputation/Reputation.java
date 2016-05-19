/**
 * 
 */
package ippoz.multilayer.detector.reputation;

import ippoz.multilayer.detector.algorithm.DetectionAlgorithm;
import ippoz.multilayer.detector.commons.data.DataSeriesSnapshot;
import ippoz.multilayer.detector.commons.data.ExperimentData;

import java.util.Date;
import java.util.HashMap;

/**
 * The Class Reputation.
 * Needs to be extended from concrete reputation classes.
 *
 * @author Tommy
 */
public abstract class Reputation {
	
	/** The reputation tag. */
	private String reputationTag;

	/**
	 * Instantiates a new reputation.
	 *
	 * @param reputationTag the reputation tag
	 */
	public Reputation(String reputationTag) {
		this.reputationTag = reputationTag;
	}

	/**
	 * Gets the reputation tag.
	 *
	 * @return the reputation tag
	 */
	public String getReputationTag(){
		return reputationTag;
	}
	
	/**
	 * Evaluates the reputation of a given detection algorithm in a specific experiment.
	 *
	 * @param alg the algorithm
	 * @param expData the experiment data
	 * @return the computed reputation
	 */
	public double evaluateReputation(DetectionAlgorithm alg, ExperimentData expData){
		DataSeriesSnapshot sysSnapshot;
		HashMap<Date, Double> anomalyEvaluations = new HashMap<Date, Double>();
		expData.resetIterator();
		for(int i=0;i<expData.getSnapshotNumber();i++){
			sysSnapshot = expData.getDataSeriesSnapshot(alg.getDataSeries(), i);
			anomalyEvaluations.put(sysSnapshot.getTimestamp(), alg.snapshotAnomalyRate(sysSnapshot, expData.getSnapshot(i)));
		}
		expData.resetIterator();
		return evaluateExperimentReputation(expData, anomalyEvaluations);
	}

	/**
	 * Votes experiment reputation.
	 *
	 * @param expData the experiment data
	 * @param anomalyEvaluations the anomaly evaluations of each snapshot
	 * @return the final reputation
	 */
	protected abstract double evaluateExperimentReputation(ExperimentData expData, HashMap<Date, Double> anomalyEvaluations);
	
}
