/**
 * 
 */
package ippoz.multilayer.detector.metric;

import ippoz.multilayer.detector.commons.data.Snapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * The Class TN_Metric.
 * Implements a metric based on true negatives.
 *
 * @author Tommy
 */
public class TN_Metric extends BetterMaxMetric {

	/** The absolute flag. */
	private boolean absolute;

	/**
	 * Instantiates a new tn_ metric.
	 *
	 * @param absolute the absolute flag
	 */
	public TN_Metric(boolean absolute) {
		this.absolute = absolute;
	}
	
	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.metric.Metric#evaluateAnomalyResults(ippoz.multilayer.detector.data.ExperimentData, java.util.HashMap)
	 */
	@Override
	public double evaluateAnomalyResults(LinkedList<Snapshot> snapList, HashMap<Date, Double> anomalyEvaluations) {
		int detectionHits = 0;
		Snapshot snap;
		for(int i=0;i<snapList.size();i++){
			snap = snapList.get(i);
			if(!Metric.anomalyTrueFalse(anomalyEvaluations.get(snap.getTimestamp()))){
				if(snap.getInjectedElement() == null || snap.getInjectedElement().getTimestamp().compareTo(snap.getTimestamp()) != 0)
					detectionHits++;
			} 
		}
		if(!absolute)
			return 1.0*detectionHits/snapList.size();
		else return detectionHits;
	}
	
	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.metric.Metric#getMetricName()
	 */
	@Override
	public String getMetricName() {
		return "True Negatives";
	}

}
