/**
 * 
 */
package ippoz.multilayer.detector.reputation;

import ippoz.multilayer.detector.data.ExperimentData;
import ippoz.multilayer.detector.metric.TP_Metric;

import java.util.Date;
import java.util.HashMap;

/**
 * The Class BetaReputation.
 * Constructs a reputation using the Beta calculation.
 *
 * @author Tommy
 */
public class BetaReputation extends Reputation {

	/**
	 * Instantiates a new Beta reputation.
	 *
	 * @param reputationTag the reputation tag
	 */
	public BetaReputation(String reputationTag) {
		super(reputationTag);
	}

	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.reputation.Reputation#evaluateExperimentReputation(ippoz.multilayer.detector.data.ExperimentData, java.util.HashMap)
	 */
	@Override
	public double evaluateExperimentReputation(ExperimentData expData, HashMap<Date, Double> anomalyEvaluations) {
		double tp = new TP_Metric(true).evaluateAnomalyResults(expData, anomalyEvaluations);
		double nInj = expData.getInjections().size();
		double alpha = tp + 1;
		double beta = nInj + 1;
		return alpha*1.0/(alpha + beta);
	}

}
