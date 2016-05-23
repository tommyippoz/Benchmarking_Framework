/**
 * 
 */
package ippoz.multilayer.detector.metric;

import ippoz.multilayer.detector.commons.data.Snapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * The Class FN_Metric.
 * Implements a metric based on false negatives.
 *
 * @author Tommy
 */
public class FN_Metric extends BetterMinMetric {

	/** The absolute. */
	private boolean absolute;

	/**
	 * Instantiates a new fn_ metric.
	 *
	 * @param absolute the absolute flag
	 */
	public FN_Metric(boolean absolute) {
		this.absolute = absolute;
	}
	
	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.metric.Metric#evaluateAnomalyResults(ippoz.multilayer.detector.data.ExperimentData, java.util.HashMap)
	 */
	@Override
	public double evaluateAnomalyResults(LinkedList<Snapshot> snapList, HashMap<Date, Double> anomalyEvaluations) {
		int detectionHits = 0;
		int undetectable = 0;
		Snapshot snap;
		for(int i=0;i<snapList.size();i++){
			snap = snapList.get(i);
			if(snap.getInjectedElement() != null && snap.getInjectedElement().happensAt(snap.getTimestamp())){	
				if(!Metric.anomalyTrueFalse(anomalyEvaluations.get(snap.getTimestamp())))
					detectionHits++;
				i++;
				while(i<snapList.size()){
					if(snap.getInjectedElement().compliesWith(snapList.get(i).getTimestamp())){
						i++;
						undetectable++;
					}
					else break;
				}
				i--;
			}
		}
		if(!absolute)
			return 1.0*detectionHits/(snapList.size()-undetectable);
		else return detectionHits;
	}

	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.metric.Metric#getMetricName()
	 */
	@Override
	public String getMetricName() {
		return "False Negatives";
	}

}
