/**
 * 
 */
package ippoz.multilayer.detector.manager;

import ippoz.multilayer.commons.datacategory.DataCategory;
import ippoz.multilayer.commons.indicator.Indicator;
import ippoz.multilayer.detector.commons.data.ExperimentData;
import ippoz.multilayer.detector.commons.support.AppLogger;
import ippoz.multilayer.detector.commons.support.PreferencesManager;
import ippoz.multilayer.detector.commons.support.ThreadScheduler;
import ippoz.multilayer.detector.configuration.AlgorithmConfiguration;
import ippoz.multilayer.detector.metric.Metric;
import ippoz.multilayer.detector.reputation.Reputation;
import ippoz.multilayer.detector.trainer.AlgorithmTrainer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * The Class TrainerManager.
 * The manager responsible of the training process of the anomaly detector.
 *
 * @author Tommy
 */
public class TrainerManager extends ThreadScheduler {
	
	/** The preference manager. */
	private PreferencesManager prefManager;
	
	/** The timing manager. */
	private TimingsManager pManager;
	
	/** The experiments list. */
	private LinkedList<ExperimentData> expList;
	
	/** The possible configurations. */
	private HashMap<String, LinkedList<AlgorithmConfiguration>> confList;
	
	/** The chosen metric. */
	private Metric metric;
	
	/** The chosen reputation metric. */
	private Reputation reputation;
	
	/** The list of indicators. */
	private LinkedList<Indicator> indList;
	
	/** The data types. */
	private DataCategory[] dataTypes;
	
	/** The algorithm types. */
	private String[] algTypes;
	
	/**
	 * Instantiates a new trainer manager.
	 *
	 * @param prefManager the preference manager
	 * @param pManager the timing manager
	 * @param expList the experiment list
	 * @param confList the configuration list
	 * @param metric the chosen metric
	 * @param reputation the chosen reputation metric
	 * @param dataTypes the data types
	 * @param algTypes the algorithm types
	 */
	public TrainerManager(PreferencesManager prefManager, TimingsManager pManager, LinkedList<ExperimentData> expList, HashMap<String, LinkedList<AlgorithmConfiguration>> confList, Metric metric, Reputation reputation, DataCategory[] dataTypes, String[] algTypes) {
		super();
		this.prefManager = prefManager;
		this.pManager = pManager;
		this.expList = expList;
		this.confList = confList;
		this.metric = metric;
		this.reputation = reputation;
		this.indList = expList.getFirst().getNumericIndicators();
		this.dataTypes = dataTypes;
		this.algTypes = algTypes;
	}

	/**
	 * Starts the train process. 
	 * The scores are saved in a file specified in the preferences.
	 */
	public void train(){
		long start = System.currentTimeMillis();
		try {
			start();
			join();
			saveScores(getThreadList());
			pManager.addTiming(TimingsManager.TRAIN_RUNS, Double.valueOf(expList.size()));
			pManager.addTiming(TimingsManager.TRAIN_TIME, (double)(System.currentTimeMillis() - start));
			pManager.addTiming(TimingsManager.AVG_TRAIN_TIME, ((System.currentTimeMillis() - start)/threadNumber()*1.0));
			AppLogger.logInfo(getClass(), "Training executed in " + (System.currentTimeMillis() - start) + "ms");
		} catch (InterruptedException ex) {
			AppLogger.logException(getClass(), ex, "Unable to complete training phase");
		}
	}

	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.support.ThreadScheduler#initRun()
	 */
	@Override
	protected void initRun(){
		AppLogger.logInfo(getClass(), "Train Started");
		LinkedList<AlgorithmTrainer> trainerList = new LinkedList<AlgorithmTrainer>();
		for(String algType : algTypes){
			if(algType.equals("RCC")){
				trainerList.add(new AlgorithmTrainer(algType, null, null, metric, reputation, expList, confList));
			} else {
				for(Indicator indicator : indList){
					for(DataCategory dataType : dataTypes){
						trainerList.add(new AlgorithmTrainer(algType, indicator, dataType, metric, reputation, expList, confList));
					}
				}
			}
		}
		setThreadList(trainerList);
		pManager.addTiming(TimingsManager.ANOMALY_CHECKERS, Double.valueOf(trainerList.size()));
	}

	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.support.ThreadScheduler#threadStart(java.lang.Thread, int)
	 */
	@Override
	protected void threadStart(Thread t, int tIndex) {
		// TODO
	}

	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.support.ThreadScheduler#threadComplete(java.lang.Thread, int)
	 */
	@Override
	protected void threadComplete(Thread t, int tIndex) {
		AppLogger.logInfo(getClass(), "[" + tIndex + "/" + threadNumber() + "] Found: " + ((AlgorithmTrainer)t).getBestConfiguration().toString());						
	}
	
	/**
	 * Saves scores related to the executed AlgorithmTrainers.
	 *
	 * @param list the list of algorithm trainers
	 */
	@SuppressWarnings("unchecked")
	private void saveScores(LinkedList<? extends Thread> list) {
		BufferedWriter writer;
		AlgorithmTrainer trainer;
		Collections.sort((LinkedList<AlgorithmTrainer>)list);
		try {
			writer = new BufferedWriter(new FileWriter(new File(prefManager.getPreference(DetectionManager.SCORES_FILE_FOLDER) + "scores.csv")));
			writer.write("indicator_name,data_series_type,algorithm_type,reputation_score,metric_score(" + metric.getMetricName() + "),configuration\n");
			for(Thread tThread : list){
				trainer = (AlgorithmTrainer)tThread;
				writer.write(trainer.getIndicatorName() + "," + 
						trainer.getDataType() + "," + 
						trainer.getAlgType() + "," +
						trainer.getReputationScore() + "," + 
						trainer.getMetricScore() + "," +  
						trainer.getBestConfiguration().toFileRow(false) + "\n");
			}
			writer.close();
		} catch(IOException ex){
			AppLogger.logException(getClass(), ex, "Unable to write scores");
		}
	}
	
}
