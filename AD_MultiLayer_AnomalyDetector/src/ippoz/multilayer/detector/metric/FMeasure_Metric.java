/**
 * 
 */
package ippoz.multilayer.detector.metric;

import ippoz.multilayer.detector.commons.data.ExperimentData;

import java.util.Date;
import java.util.HashMap;

/**
 * The Class FMeasure_Metric.
 * Implements the F-Measure (= F-Score(1))
 *
 * @author Tommy
 */
public class FMeasure_Metric extends BetterMaxMetric {

	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.metric.Metric#evaluateAnomalyResults(ippoz.multilayer.detector.data.ExperimentData, java.util.HashMap)
	 */
	@Override
	public double evaluateAnomalyResults(ExperimentData expData, HashMap<Date, Double> anomalyEvaluations) {
		double p = new Precision_Metric().evaluateAnomalyResults(expData, anomalyEvaluations);
		double r = new Recall_Metric().evaluateAnomalyResults(expData, anomalyEvaluations);
		if(p + r > 0)
			return 2.0*p*r/(p+r);
		else return 0.0;
	}
	
	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.metric.Metric#getMetricName()
	 */
	@Override
	public String getMetricName() {
		return "F-Measure";
	}

}
