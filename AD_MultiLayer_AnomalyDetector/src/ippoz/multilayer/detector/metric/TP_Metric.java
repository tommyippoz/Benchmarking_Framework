/**
 * 
 */
package ippoz.multilayer.detector.metric;

import ippoz.multilayer.detector.commons.data.ExperimentData;
import ippoz.multilayer.detector.commons.data.Snapshot;

import java.util.Date;
import java.util.HashMap;

/**
 * The Class TP_Metric.
 * Implements a metric based on true positives.
 *
 * @author Tommy
 */
public class TP_Metric extends BetterMaxMetric {
	
	/** The absolute flag. */
	private boolean absolute;

	/**
	 * Instantiates a new tp_ metric.
	 *
	 * @param absolute the absolute flag
	 */
	public TP_Metric(boolean absolute) {
		this.absolute = absolute;
	}

	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.metric.Metric#evaluateAnomalyResults(ippoz.multilayer.detector.data.ExperimentData, java.util.HashMap)
	 */
	@Override
	public double evaluateAnomalyResults(ExperimentData expData, HashMap<Date, Double> anomalyEvaluations) {
		int detectionHits = 0;
		Snapshot snap;
		expData.resetIterator();
		while(expData.hasNextSnapshot()){
			snap = expData.nextSnapshot();
			if(Metric.anomalyTrueFalse(anomalyEvaluations.get(snap.getTimestamp()))){
				if(snap.getInjectedElement() != null && snap.getInjectedElement().getTimestamp().compareTo(snap.getTimestamp()) == 0)
					detectionHits++;
			} 
		}
		if(expData.getSnapshotNumber() > 0){
			if(!absolute)
				return 1.0*detectionHits/expData.getSnapshotNumber();
			else return detectionHits;
		} else return 0.0;
	}
	
	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.metric.Metric#getMetricName()
	 */
	@Override
	public String getMetricName() {
		return "True Positives";
	}

}
