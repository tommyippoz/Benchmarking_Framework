/**
 * 
 */
package ippoz.multilayer.detector.algorithm;

import ippoz.multilayer.commons.datacategory.DataCategory;
import ippoz.multilayer.commons.indicator.Indicator;
import ippoz.multilayer.detector.commons.data.Snapshot;
import ippoz.multilayer.detector.configuration.AlgorithmConfiguration;
import ippoz.multilayer.detector.invariants.Invariant;

/**
 * @author Tommy
 *
 */
public class InvariantChecker extends DetectionAlgorithm {
	
	private Invariant invariant;

	public InvariantChecker(AlgorithmConfiguration conf, Invariant invariant) {
		super(conf);
		this.invariant = invariant;
	}

	@Override
	protected double evaluateSnapshot(Snapshot sysSnapshot) {
		return invariant.evaluateInvariant(sysSnapshot) ? 1.0 : 0.0;
	}

	@Override
	protected void printImageResults(String outFolderName, String expTag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void printTextResults(String outFolderName, String expTag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public DataCategory getDataType() {
		return null;
	}

	@Override
	public Indicator getIndicator() {
		return null;
	}

}
