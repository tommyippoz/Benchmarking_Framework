/**
 * 
 */
package ippoz.multilayer.detector.metric;

import ippoz.multilayer.detector.data.ExperimentData;

import java.util.Date;
import java.util.HashMap;

/**
 * The Class Precision_Metric.
 * Implements a metric based on Precision.
 *
 * @author Tommy
 */
public class Precision_Metric extends BetterMaxMetric {

	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.metric.Metric#evaluateAnomalyResults(ippoz.multilayer.detector.data.ExperimentData, java.util.HashMap)
	 */
	@Override
	public double evaluateAnomalyResults(ExperimentData expData, HashMap<Date, Double> anomalyEvaluations) {
		double tp = new TP_Metric(true).evaluateAnomalyResults(expData, anomalyEvaluations);
		double fp = new FP_Metric(true).evaluateAnomalyResults(expData, anomalyEvaluations);
		if(tp + fp > 0)
			return 1.0*tp/(tp+fp);
		else return 0.0;
	}
	
	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.metric.Metric#getMetricName()
	 */
	@Override
	public String getMetricName() {
		return "Precision";
	}

}