/**
 * 
 */
package ippoz.multilayer.detector.manager;

import ippoz.multilayer.detector.algorithm.DetectionAlgorithm;
import ippoz.multilayer.detector.commons.data.ExperimentData;
import ippoz.multilayer.detector.commons.dataseries.DataSeries;
import ippoz.multilayer.detector.commons.support.AppLogger;
import ippoz.multilayer.detector.commons.support.PreferencesManager;
import ippoz.multilayer.detector.commons.support.ThreadScheduler;
import ippoz.multilayer.detector.configuration.AlgorithmConfiguration;
import ippoz.multilayer.detector.configuration.ConfidenceConfiguration;
import ippoz.multilayer.detector.configuration.HistoricalConfiguration;
import ippoz.multilayer.detector.configuration.InvariantConfiguration;
import ippoz.multilayer.detector.configuration.RemoteCallConfiguration;
import ippoz.multilayer.detector.configuration.SPSConfiguration;
import ippoz.multilayer.detector.invariants.Invariant;
import ippoz.multilayer.detector.metric.Metric;
import ippoz.multilayer.detector.trainer.AlgorithmVoter;
import ippoz.multilayer.detector.trainer.ExperimentVoter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.LinkedList;

/**
 * The Class EvaluatorManager.
 * The manager of the evaluation-scoring phase. Called after the training or when train scores are already available.
 *
 * @author Tommy
 */
public class EvaluatorManager extends ThreadScheduler {
	
	/** The preference manager. */
	private PreferencesManager prefManager;
	
	/** The timings manager. */
	private TimingsManager pManager;
	
	/** The experiments list. */
	private LinkedList<ExperimentData> expList;
	
	/** The validation metrics. */
	private Metric[] validationMetrics;
	
	/** The anomaly threshold. Votings over that threshold raise alarms. */
	private double anomalyTreshold;
	
	/** The algorithm convergence time. */
	private double algConvergence;
	
	/** The detector score threshold. Used to filter the available anomaly checkers by score. */
	private double detectorScoreTreshold;
	
	/**
	 * Instantiates a new evaluator manager.
	 *
	 * @param prefManager the preference manager
	 * @param pManager the timings manager
	 * @param expList the experiment list
	 * @param validationMetrics the validation metrics
	 * @param anTresholdString the an threshold string
	 * @param algConvergence the algorithm convergence
	 * @param detectorScoreTreshold the detector score threshold
	 */
	public EvaluatorManager(PreferencesManager prefManager, TimingsManager pManager, LinkedList<ExperimentData> expList, Metric[] validationMetrics, String anTresholdString, double algConvergence, double detectorScoreTreshold) {
		this.prefManager = prefManager;
		this.pManager = pManager;
		this.expList = expList;
		this.validationMetrics = validationMetrics;
		this.algConvergence = algConvergence;
		this.detectorScoreTreshold = detectorScoreTreshold;
		anomalyTreshold = getAnomalyVoterTreshold(anTresholdString, loadTrainScores().size());
	}
	
	/**
	 * Detects anomalies.
	 * This is the core of the evaluation, which ends in the anomaly evaluation of each snapshot of each experiment.
	 */
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
	
	/**
	 * Gets the anomaly voter threshold.
	 *
	 * @param anTresholdString the anomaly threshold string read from preferences
	 * @param checkers the number of selected checkers
	 * @return the anomaly voter threshold
	 */
	private double getAnomalyVoterTreshold(String anTresholdString, int checkers){
		switch(anTresholdString){
			case "HALF":
				return checkers/2;
			default:
				return Double.parseDouble(anTresholdString);
		}
	}
	
	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.support.ThreadScheduler#initRun()
	 */
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

	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.support.ThreadScheduler#threadStart(java.lang.Thread, int)
	 */
	@Override
	protected void threadStart(Thread t, int tIndex) {
		AppLogger.logInfo(getClass(), "Evaluating experiment " + tIndex + "/" + threadNumber());
	}

	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.support.ThreadScheduler#threadComplete(java.lang.Thread, int)
	 */
	@Override
	protected void threadComplete(Thread t, int tIndex) {
		((ExperimentVoter)t).printVoting(prefManager.getPreference(DetectionManager.OUTPUT_FORMAT), prefManager.getPreference(DetectionManager.OUTPUT_FOLDER), validationMetrics, anomalyTreshold, algConvergence);
	}
	
	/**
	 * Loads train scores.
	 * This is the outcome of some previous training phases.
	 *
	 * @return the list of AlgorithmVoters resulting from the read scores
	 */
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
									case "INV":
										conf = new InvariantConfiguration(new Invariant(splitted[5]));
										break;
									default:
										conf = null;
								}
								if(conf != null){
									conf.addItem(AlgorithmConfiguration.WEIGHT, splitted[3]);
									conf.addItem(AlgorithmConfiguration.SCORE, splitted[4]);
								}
								voterList.add(new AlgorithmVoter(DetectionAlgorithm.buildAlgorithm(splitted[0], DataSeries.fromString(splitted[1]), conf), Double.parseDouble(splitted[4]), Double.parseDouble(splitted[3])));
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
	
	/**
	 * Setup results file.
	 */
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
