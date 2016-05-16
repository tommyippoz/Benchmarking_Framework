/**
 * 
 */
package ippoz.multilayer.detector.algorithm;

import ippoz.multilayer.commons.datacategory.DataCategory;
import ippoz.multilayer.commons.indicator.Indicator;
import ippoz.multilayer.detector.commons.data.Snapshot;
import ippoz.multilayer.detector.configuration.AlgorithmConfiguration;

/**
 * @author Tommy
 *
 */
public class InvariantChecker extends DetectionAlgorithm {
	
	private Invariant invariant;

	public InvariantChecker(AlgorithmConfiguration conf, Invariant invariant) {
		super(conf);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected double evaluateSnapshot(Snapshot sysSnapshot) {
		// TODO Auto-generated method stub
		return 0;
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
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Indicator getIndicator() {
		// TODO Auto-generated method stub
		return null;
	}

}
