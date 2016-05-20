/**
 * 
 */
package ippoz.multilayer.detector.configuration;

import ippoz.multilayer.detector.algorithm.AlgorithmType;
import ippoz.multilayer.detector.invariants.Invariant;

/**
 * @author Tommy
 *
 */
public class InvariantConfiguration extends AlgorithmConfiguration {

	private Invariant invariant;
	
	public InvariantConfiguration(Invariant invariant){
		super(AlgorithmType.INV);
		this.invariant = invariant;
	}
	
	public Invariant getInvariant() {
		return invariant;
	}

	@Override
	public String getFileHeader() {
		return invariant.toString();
	}

	@Override
	public String toFileRow(boolean complete) {
		return invariant.toString();
	}

}
