/**
 * 
 */
package ippoz.multilayer.detector.metric;

import ippoz.multilayer.detector.commons.data.ExperimentData;

import java.util.Date;
import java.util.HashMap;

// TODO: Auto-generated Javadoc
/**
 * The Class FalseNegativeRate_Metric.
 * Implements a metric dependent on the false negative rate FN/(TP+FN)
 *
 * @author Tommy
 */
public class FalseNegativeRate_Metric extends BetterMinMetric {

	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.metric.Metric#evaluateAnomalyResults(ippoz.multilayer.detector.data.ExperimentData, java.util.HashMap)
	 */
	@Override
	public double evaluateAnomalyResults(ExperimentData expData, HashMap<Date, Double> anomalyEvaluations) {
		double tp = new TN_Metric(true).evaluateAnomalyResults(expData, anomalyEvaluations);
		double fn = new FP_Metric(true).evaluateAnomalyResults(expData, anomalyEvaluations);
		if(tp + fn > 0)
			return 1.0*fn/(tp+fn);
		else return 0.0;
	}

	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.metric.Metric#getMetricName()
	 */
	@Override
	public String getMetricName() {
		return "False Negative Rate";
	}

}
