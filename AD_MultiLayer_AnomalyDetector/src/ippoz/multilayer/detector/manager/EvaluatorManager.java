/**
 * 
 */
package ippoz.multilayer.detector.manager;

import ippoz.multilayer.detector.algorithm.DetectionAlgorithm;
import ippoz.multilayer.detector.configuration.AlgorithmConfiguration;
import ippoz.multilayer.detector.configuration.ConfidenceConfiguration;
import ippoz.multilayer.detector.configuration.HistoricalConfiguration;
import ippoz.multilayer.detector.configuration.RemoteCallConfiguration;
import ippoz.multilayer.detector.configuration.SPSConfiguration;
import ippoz.multilayer.detector.data.ExperimentData;
import ippoz.multilayer.detector.data.Indicator;
import ippoz.multilayer.detector.metric.Metric;
import ippoz.multilayer.detector.support.AppLogger;
import ippoz.multilayer.detector.support.PreferencesManager;
import ippoz.multilayer.detector.support.ThreadScheduler;
import ippoz.multilayer.detector.trainer.AlgorithmVoter;
import ippoz.multilayer.detector.trainer.ExperimentVoter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author Tommy
 *
 */
public class EvaluatorManager extends ThreadScheduler {
	
	private PreferencesManager prefManager;
	private TimingsManager pManager;
	private LinkedList<ExperimentData> expList;
	private Metric[] validationMetrics;
	private double anomalyTreshold;
	private double algConvergence;
	private double detectorScoreTreshold;
	private HashMap<String, Indicator> indicatorList;
	
	public EvaluatorManager(PreferencesManager prefManager, TimingsManager pManager, LinkedList<ExperimentData> expList, Metric[] validationMetrics, String anTresholdString, double algConvergence, double detectorScoreTreshold) {
		this.prefManager = prefManager;
		this.pManager = pManager;
		this.expList = expList;
		this.validationMetrics = validationMetrics;
		this.algConvergence = algConvergence;
		this.detectorScoreTreshold = detectorScoreTreshold;
		buildIndicatorList();
		anomalyTreshold = getAnomalyVoterTreshold(anTresholdString, loadTrainScores().size());
	}

	private void buildIndicatorList(){
		indicatorList = new HashMap<String, Indicator>();
		for(Indicator ind : expList.getFirst().getNumericIndicators()){
			indicatorList.put(ind.getName(), ind);
		}
	}
	
	public void detectAnomalies(){
		long start = System.currentTimeMillis();
		try {
			start();
			join();
			pManager.addTiming(TimingsManager.VALIDATION_RUNS, Double.valueOf(expList.size()));
			pManager.addTiming(TimingsManager.VALIDATION_TIME, (double)(System.currentTimeMillis() - start));
			pManager.addTiming(TimingsManager.AVG_VALIDATION_TIME, (System.currentTimeMillis() - start)/threadNumber()*1.0);
			AppLogger.logInfo(getClass(), "Detection executed in " + (System.currentTimeMillis() - start) + " ms");
		} catch (InterruptedException ex) {
			AppLogger.logException(getClass(), ex, "Unable to complete evaluation phase");
		}
	}
	
	private double getAnomalyVoterTreshold(String anTresholdString, int checkers){
		switch(anTresholdString){
			case "HALF":
				return checkers/2;
			default:
				return Double.parseDouble(anTresholdString);
		}
	}
	
	@Override
	protected void initRun() {
		LinkedList<AlgorithmVoter> algVoters = loadTrainScores();
		LinkedList<ExperimentVoter> voterList = new LinkedList<ExperimentVoter>();
		setupResultsFile();
		for(ExperimentData expData : expList){
			voterList.add(new ExperimentVoter(expData, algVoters));
		}
		setThreadList(voterList);
		pManager.addTiming(TimingsManager.SELECTED_ANOMALY_CHECKERS, Double.valueOf(voterList.size()));
	}

	@Override
	protected void threadStart(Thread t, int tIndex) {
		AppLogger.logInfo(getClass(), "Evaluating experiment " + tIndex + "/" + threadNumber());
	}

	@Override
	protected void threadComplete(Thread t, int tIndex) {
		((ExperimentVoter)t).printVoting(prefManager.getPreference(DetectionManager.OUTPUT_FORMAT), prefManager.getPreference(DetectionManager.OUTPUT_FOLDER), validationMetrics, anomalyTreshold, algConvergence);
	}
	
	private LinkedList<AlgorithmVoter> loadTrainScores() {
		File asFile = new File(prefManager.getPreference(DetectionManager.SCORES_FILE_FOLDER) + "scores.csv");
		BufferedReader reader;
		AlgorithmConfiguration conf;
		String[] splitted;
		LinkedList<AlgorithmVoter> voterList = new LinkedList<AlgorithmVoter>();
		String readed;
		try {
			if(asFile.exists()){
				reader = new BufferedReader(new FileReader(asFile));
				reader.readLine();
				while(reader.ready()){
					readed = reader.readLine();
					if(readed != null){
						readed = readed.trim();
						if(readed.length() > 0 && readed.indexOf(",") != -1){
							splitted = readed.split(",");
							if(splitted.length > 4 && Double.parseDouble(splitted[4]) >= detectorScoreTreshold){
								switch(splitted[2]){
									case "HIST":
										conf = new HistoricalConfiguration();
										conf.addItem(HistoricalConfiguration.INTERVAL_WIDTH, splitted[5]);
										break;
									case "SPS":
										conf = new SPSConfiguration();
										conf.addItem(SPSConfiguration.PDV, splitted[5]);
										conf.addItem(SPSConfiguration.PDS, splitted[6]);
										conf.addItem(SPSConfiguration.POV, splitted[7]);
										conf.addItem(SPSConfiguration.POS, splitted[8]);
										conf.addItem(SPSConfiguration.M, splitted[9]);
										conf.addItem(SPSConfiguration.N, splitted[10]);
										conf.addItem(SPSConfiguration.DYN_WEIGHT, splitted[11]);
										break;
									case "CONF":
										conf = new ConfidenceConfiguration();
										conf.addItem(ConfidenceConfiguration.ALPHA, splitted[5]);
										break;
									case "RCC":
										conf = new RemoteCallConfiguration();
										conf.addItem(RemoteCallConfiguration.RCC_WEIGHT, splitted[5]);
										break;
									default:
										conf = null;
								}
								if(conf != null){
									conf.addItem(AlgorithmConfiguration.WEIGHT, splitted[3]);
									conf.addItem(AlgorithmConfiguration.SCORE, splitted[4]);
								}
								voterList.add(new AlgorithmVoter(DetectionAlgorithm.buildAlgorithm(splitted[2], splitted[1], indicatorList.get(splitted[0]), conf), Double.parseDouble(splitted[4]), Double.parseDouble(splitted[3])));
							}
						}
					}
				}
				reader.close();
			} 
		} catch(Exception ex){
			AppLogger.logException(getClass(), ex, "Unable to read scores");
		}
		return voterList;
	}
	
	private void setupResultsFile() {
		File resultsFile;
		PrintWriter pw;
		try {
			resultsFile = new File(prefManager.getPreference(DetectionManager.OUTPUT_FOLDER) + "/voter/results.csv");
			if(resultsFile.exists())
				resultsFile.delete();
			pw = new PrintWriter(new FileOutputStream(resultsFile, true));
			pw.append("exp_name,exp_obs,");
			for(Metric met : validationMetrics){
				pw.append(met.getMetricName() + ",");
			}
			pw.append("\n");
			pw.close();
		} catch (FileNotFoundException ex) {
			AppLogger.logException(getClass(), ex, "Unable to find results file");
		} 		
	}

}
