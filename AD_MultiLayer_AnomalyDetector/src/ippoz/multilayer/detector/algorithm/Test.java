package ippoz.multilayer.detector.algorithm;

import ippoz.multilayer.detector.commons.configuration.AlgorithmConfiguration;
import ippoz.multilayer.detector.commons.data.DataSeriesSnapshot;
import ippoz.multilayer.detector.commons.dataseries.DataSeries;

public class Test extends DataSeriesDetectionAlgorithm{
	
	int i;

	public Test(DataSeries dataSeries, AlgorithmConfiguration conf) {
		super(dataSeries, conf);
		i=0;
		// TODO Auto-generated constructor stub
	}

	@Override
	protected double evaluateDataSeriesSnapshot(DataSeriesSnapshot sysSnapshot) {
		i++;
		if(i%2==0)
		return 0;
		else return 1;
	}

	@Override
	protected void printImageResults(String outFolderName, String expTag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void printTextResults(String outFolderName, String expTag) {
		// TODO Auto-generated method stub
		
	}

}
