/**
 * 
 */
package ippoz.multilayer.detector.algorithm;

import ippoz.multilayer.detector.configuration.AlgorithmConfiguration;
import ippoz.multilayer.detector.configuration.SPSConfiguration;
import ippoz.multilayer.detector.data.Indicator;
import ippoz.multilayer.detector.data.Snapshot;
import ippoz.multilayer.detector.graphics.ChartDrawer;
import ippoz.multilayer.detector.graphics.XYChartDrawer;
import ippoz.multilayer.detector.support.AppUtility;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeMap;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;
import org.apache.commons.math3.special.Erf;

/**
 * @author Tommy
 *
 */
public class SPSDetector extends IndicatorDetectionAlgorithm {
	
	public static final String SPS_UPPER_BOUND = "UpperBound";
	public static final String SPS_LOWER_BOUND = "LowerBound";
	public static final String SPS_OBSERVATION = "Observation";
	public static final String SPS_ANOMALY = "Anomaly";
	public static final String SPS_FAILURE = "Failure";
	
	private static final int IMG_WIDTH = 1000;
	private static final int IMG_HEIGHT = 1000;
	
	private SPSCalculator calculator;
	private TreeMap<Date, Double> anomalies;
	private TreeMap<Date, Double> failures;
	private TreeMap<Date, Double> observations;
	private TreeMap<Date, Double> upperTreshold;
	private TreeMap<Date, Double> lowerTreshold;
	private double[] newTresholds;

	public SPSDetector(Indicator indicator, String categoryTag, AlgorithmConfiguration conf) {
		super(indicator, categoryTag, conf);
		calculator = new SPSCalculator();
		anomalies = new TreeMap<Date, Double>();
		failures = new TreeMap<Date, Double>();
		observations = new TreeMap<Date, Double>();
		upperTreshold = new TreeMap<Date, Double>();
		lowerTreshold = new TreeMap<Date, Double>();
		newTresholds = null;
	}

	@Override
	public double evaluateSnapshot(Snapshot sysSnapshot) {
		double anomalyScore;
		observations.put(sysSnapshot.getTimestamp(), Double.valueOf(sysSnapshot.getObservation().getValue(indicator.getName(), categoryTag)));
		if(newTresholds != null) {
			lowerTreshold.put(sysSnapshot.getTimestamp(), newTresholds[0]);
			upperTreshold.put(sysSnapshot.getTimestamp(), newTresholds[1]);
		} else {
			upperTreshold.put(sysSnapshot.getTimestamp(), 2*observations.get(observations.lastKey()));
			lowerTreshold.put(sysSnapshot.getTimestamp(), 0.0);
		}
		anomalyScore = calculateAnomalyScore(sysSnapshot);
		if(anomalyScore >= 1.0)
			anomalies.put(sysSnapshot.getTimestamp(), Double.valueOf(sysSnapshot.getObservation().getValue(indicator.getName(), categoryTag)));
		if(sysSnapshot.getInjectedElement() != null && sysSnapshot.getInjectedElement().getTimestamp().compareTo(sysSnapshot.getTimestamp()) == 0)
			failures.put(sysSnapshot.getTimestamp(), Double.valueOf(sysSnapshot.getObservation().getValue(indicator.getName(), categoryTag)));
		newTresholds = calculator.calculateTreshold(sysSnapshot);
		return anomalyScore;
	}
	
	private double calculateAnomalyScore(Snapshot sysSnapshot){
		double value = Double.valueOf(sysSnapshot.getObservation().getValue(indicator.getName(), categoryTag));
		if(lowerTreshold.size() > 0 && upperTreshold.size() > 0) {
			if(value <= upperTreshold.get(upperTreshold.lastKey()) && value >= lowerTreshold.get(lowerTreshold.lastKey()))
				return 0;
			else return 1;
		} else return 0;
	}
	
	@Override
	protected void printImageResults(String outFolderName, String expTag) {
		ChartDrawer chart;
		File outFolder = new File(outFolderName + "/graphics/" + expTag);
		if(!outFolder.exists())
			outFolder.mkdirs();
		chart = new XYChartDrawer(indicator.getName() + "#" + categoryTag, "Seconds", "Values", getDataset());
		chart.saveToFile(outFolder.getPath() + "/" + indicator.getName() + "#" + categoryTag + ".png", IMG_WIDTH, IMG_HEIGHT);
	}

	private HashMap<String, TreeMap<Double, Double>> getDataset() {
		Date refDate = observations.firstKey();
		HashMap<String, TreeMap<Double, Double>> dataset = new HashMap<String, TreeMap<Double, Double>>();
		dataset.put(SPS_OBSERVATION, AppUtility.convertMapTimestamps(refDate, observations));
		dataset.put(SPS_UPPER_BOUND, AppUtility.convertMapTimestamps(refDate, upperTreshold));
		dataset.put(SPS_LOWER_BOUND, AppUtility.convertMapTimestamps(refDate, lowerTreshold));
		dataset.put(SPS_ANOMALY, AppUtility.convertMapTimestamps(refDate, anomalies));
		dataset.put(SPS_FAILURE, AppUtility.convertMapTimestamps(refDate, failures));
		return dataset;
	}

	@Override
	protected void printTextResults(String outFolderName, String expTag) {
		// TODO Auto-generated method stub	
	}
	
	@Override
	public String getIndicatorFullName() {
		return indicator.getName() + "#" + categoryTag + "_SPS";
	}
	
	private class SPSCalculator {
		
		private LinkedList<SPSBlock> observedValues;
		private double pdv;
		private double pov;
		private double pds;
		private double pos;
		private double m;
		@SuppressWarnings("unused")
		private double n;
		private boolean dynamicWeights;
		
		public SPSCalculator(){
			observedValues = new LinkedList<SPSBlock>();
			pdv = Double.parseDouble(conf.getItem(SPSConfiguration.PDV));
			pov = Double.parseDouble(conf.getItem(SPSConfiguration.POV));
			pds = Double.parseDouble(conf.getItem(SPSConfiguration.PDS));
			pos = Double.parseDouble(conf.getItem(SPSConfiguration.POS));
			m = Double.parseDouble(conf.getItem(SPSConfiguration.M));
			n = Double.parseDouble(conf.getItem(SPSConfiguration.N));
			dynamicWeights = (Double.parseDouble(conf.getItem(SPSConfiguration.DYN_WEIGHT)) == 1.0);		
		}
		
		public double[] calculateTreshold(Snapshot sysSnapshot){
			double calcTreshold = 0;
			addSPSBlock(Double.parseDouble(sysSnapshot.getObservation().getValue(indicator.getName(), categoryTag)), sysSnapshot.getTimestamp());
			if(observedValues.size() > 1)
				calcTreshold = computeThreshold();
			else calcTreshold = observedValues.getLast().getObs();
			return new double[]{observedValues.getLast().getObs() - calcTreshold, observedValues.getLast().getObs() + calcTreshold};
		}
		
		private double computeThreshold() {
			double driftBound = driftUpperBound();
			double offsetBound = offsetUpperBound();
			double pred = Erf.erf(pdv)*Math.sqrt(2.0*driftBound)*(2.0/3)*Math.pow(observedValues.getLast().getTimeDiff(), (3/2));
			double sm = Erf.erf(pov)*Math.sqrt(2.0*offsetBound);
			return pred + sm;
		}
		
		private double driftUpperBound(){
			int dof = observedValues.size() - 1;
			ChiSquaredDistribution chiSq = new ChiSquaredDistribution(dof);
			return weightedDriftVariance()*(dof/chiSq.inverseCumulativeProbability(pds));
		}
		
		private double offsetUpperBound(){
			int dof = observedValues.size() - 1;
			ChiSquaredDistribution chiSq = new ChiSquaredDistribution(dof);
			return weightedOffsetVariance()*(dof/chiSq.inverseCumulativeProbability(pos));
		}
		
		private double weightedDriftVariance(){
			double wdf = 0;
			double weigthSum = getWeightSum();
			double nWeightSum = 0;
			double weigthDMean = 0;
			for(int i=0;i<observedValues.size();i++){
				weigthDMean = weigthDMean + getWeigth(i)*observedValues.get(i).getDrift();
				nWeightSum = nWeightSum + Math.pow(getWeigth(i)/weigthSum, 2);
			}
			weigthDMean = weigthDMean/weigthSum;
			for(int i=0;i<observedValues.size();i++){
				wdf = wdf + (getWeigth(i)/weigthSum)*Math.pow(observedValues.get(i).getDrift() - weigthDMean, 2);
			}
			return wdf/(1-nWeightSum);
		}
		
		private double weightedOffsetVariance(){
			double wof = 0;
			double weigthSum = getWeightSum();
			double nWeightSum = 0;
			double weigthOMean = 0;
			for(int i=0;i<observedValues.size();i++){
				weigthOMean = weigthOMean + getWeigth(i)*observedValues.get(i).getOffset();
				nWeightSum = nWeightSum + Math.pow(getWeigth(i)/weigthSum, 2);
			}
			weigthOMean = weigthOMean/weigthSum;
			for(int i=0;i<observedValues.size();i++){
				wof = wof + (getWeigth(i)/weigthSum)*Math.pow(observedValues.get(i).getOffset() - weigthOMean, 2);
			}
			return wof/(1-nWeightSum);
		}

		private void addSPSBlock(double newValue, Date timestamp){
			observedValues.add(new SPSBlock(newValue, timestamp));
			if(observedValues.size() > m)
				observedValues.removeFirst();
		}
		
		private double getWeigth(int obsIndex){
			if(dynamicWeights){
				return ((obsIndex+1.0)/observedValues.size());
			} else return 1.0;
		}
		
		private double getWeightSum(){
			double tot = 0.0;
			if(dynamicWeights){
				for(int i=0;i<observedValues.size();i++){
					tot = tot + getWeigth(i);
				}
				return tot;
			} else return 1.0*observedValues.size();
		}
		
		private class SPSBlock {
			
			private double obs;
			private Date timestamp;
			private double drift;
			private double offset;
			private int timeDiff;
			
			public SPSBlock(double obs, Date timestamp) {
				this.obs = obs;
				this.timestamp = timestamp;
				if(observedValues.size() > 0){
					drift = (obs - observedValues.getLast().getDrift())/2;
					offset = obs - observedValues.getLast().getObs();
					timeDiff = (int) ((timestamp.getTime() - observedValues.getLast().getTimestamp().getTime())/1000);
				} else {
					drift = obs;
					offset = obs;
					timeDiff = 1;
				}
			}

			public double getObs() {
				return obs;
			}
			
			public double getDrift() {
				return drift;
			}
			
			public double getOffset() {
				return offset;
			}
			
			public int getTimeDiff(){
				return timeDiff;
			}
			
			public Date getTimestamp(){
				return timestamp;
			}
			
		}
		
	}

}
