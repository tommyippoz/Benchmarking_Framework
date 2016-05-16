/**
 * 
 */
package ippoz.multilayer.detector.metric;

import ippoz.multilayer.detector.commons.data.ExperimentData;

import java.util.Date;
import java.util.HashMap;

/**
 * The Class FalsePositiveRate_Metric.
 * Implements a metric dependent on the false positive rate FP/(FP+TN)
 *
 * @author Tommy
 */
public class FalsePositiveRate_Metric extends BetterMinMetric {

	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.metric.Metric#evaluateAnomalyResults(ippoz.multilayer.detector.data.ExperimentData, java.util.HashMap)
	 */
	@Override
	public double evaluateAnomalyResults(ExperimentData expData, HashMap<Date, Double> anomalyEvaluations) {
		double tn = new TN_Metric(true).evaluateAnomalyResults(expData, anomalyEvaluations);
		double fp = new FP_Metric(true).evaluateAnomalyResults(expData, anomalyEvaluations);
		if(tn + fp > 0)
			return 1.0*fp/(fp+tn);
		else return 0.0;
	}

	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.metric.Metric#getMetricName()
	 */
	@Override
	public String getMetricName() {
		return "False Positive Rate";
	}

}
