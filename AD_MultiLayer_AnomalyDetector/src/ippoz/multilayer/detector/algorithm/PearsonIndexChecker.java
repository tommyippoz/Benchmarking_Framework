/**
 * 
 */
package ippoz.multilayer.detector.algorithm;

import java.util.LinkedList;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

import ippoz.multilayer.detector.commons.configuration.AlgorithmConfiguration;
import ippoz.multilayer.detector.commons.configuration.PearsonIndexConfiguration;
import ippoz.multilayer.detector.commons.data.MultipleSnapshot;
import ippoz.multilayer.detector.commons.data.Snapshot;
import ippoz.multilayer.detector.commons.dataseries.DataSeries;

/**
 * @author Tommy
 *
 */
public class PearsonIndexChecker extends DetectionAlgorithm {
	
	private DataSeries ds1;
	private DataSeries ds2;
	private double pearsonAvg;
	private double pearsonStd;
	private double times;
	private int windowSize;
	
	private LinkedList<Double> firstList;
	private LinkedList<Double> secondList;

	public PearsonIndexChecker(AlgorithmConfiguration conf) {
		super(conf);
		parseVars(conf);
		firstList = new LinkedList<Double>();
		secondList = new LinkedList<Double>();
	}

	private void parseVars(AlgorithmConfiguration conf) {
		String[] splittedDetail = conf.getItem(PearsonIndexConfiguration.PI_DETAIL).split(";");
		times = Double.parseDouble(conf.getItem(PearsonIndexConfiguration.PI_TOLERANCE));
		windowSize = Integer.parseInt(conf.getItem(PearsonIndexConfiguration.PI_WINDOW));
		ds1 = DataSeries.fromString(splittedDetail[0]);
		ds2 = DataSeries.fromString(splittedDetail[1]);
		pearsonAvg = Double.valueOf(splittedDetail[2]);
		pearsonStd = Double.valueOf(splittedDetail[3]);
	}

	public DataSeries getDs1() {
		return ds1;
	}

	public DataSeries getDs2() {
		return ds2;
	}

	@Override
	protected double evaluateSnapshot(Snapshot sysSnapshot) {
		double pValue;
		if(firstList.size() >= windowSize){
			firstList.removeFirst();
			secondList.removeFirst();
		}
		if(sysSnapshot instanceof MultipleSnapshot) {
			firstList.add(((MultipleSnapshot)sysSnapshot).getSnapshot(ds1).getSnapValue());
			secondList.add(((MultipleSnapshot)sysSnapshot).getSnapshot(ds2).getSnapValue());
		} else {
			firstList.add(Double.MIN_VALUE);
			secondList.add(Double.MIN_VALUE);
		}
		if(firstList.size() > 1){
			pValue = new PearsonsCorrelation().correlation(ArrayUtils.toPrimitive(firstList.toArray(new Double[firstList.size()])), ArrayUtils.toPrimitive(secondList.toArray(new Double[secondList.size()])));
			return check(pValue);	
		} else return 0.0;
	}
	
	public double check(double pValue) {
		if(pValue < pearsonAvg - times*pearsonStd || pValue > pearsonAvg + times*pearsonStd)
			return 1.0;
		else return 0.0;
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
	public DataSeries getDataSeries() {
		return null;
	}
	
}
