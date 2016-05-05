/**
 * 
 */
package ippoz.multilayer.detector.manager;

import ippoz.multilayer.detector.configuration.AlgorithmConfiguration;
import ippoz.multilayer.detector.data.ExperimentData;
import ippoz.multilayer.detector.data.Indicator;
import ippoz.multilayer.detector.metric.Metric;
import ippoz.multilayer.detector.reputation.Reputation;
import ippoz.multilayer.detector.support.AppLogger;
import ippoz.multilayer.detector.support.PreferencesManager;
import ippoz.multilayer.detector.support.ThreadScheduler;
import ippoz.multilayer.detector.trainer.AlgorithmTrainer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author Tommy
 *
 */
public class TrainerManager extends ThreadScheduler {
	
	private PreferencesManager prefManager;
	private TimingsManager pManager;
	private LinkedList<ExperimentData> expList;
	private HashMap<String, LinkedList<AlgorithmConfiguration>> confList;
	private Metric metric;
	private Reputation reputation;
	private LinkedList<Indicator> indList;
	private String[] dataTypes;
	private String[] algTypes;
	
	public TrainerManager(PreferencesManager prefManager, TimingsManager pManager, LinkedList<ExperimentData> expList, HashMap<String, LinkedList<AlgorithmConfiguration>> confList, Metric metric, Reputation reputation, String[] dataTypes, String[] algTypes) {
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

	@Override
	protected void initRun(){
		AppLogger.logInfo(getClass(), "Train Started");
		LinkedList<AlgorithmTrainer> trainerList = new LinkedList<AlgorithmTrainer>();
		for(String algType : algTypes){
			if(algType.equals("RCC")){
				trainerList.add(new AlgorithmTrainer(algType, null, null, metric, reputation, expList, confList));
			} else {
				for(Indicator indicator : indList){
					for(String dataType : dataTypes){
						trainerList.add(new AlgorithmTrainer(algType, indicator, dataType, metric, reputation, expList, confList));
					}
				}
			}
		}
		setThreadList(trainerList);
		pManager.addTiming(TimingsManager.ANOMALY_CHECKERS, Double.valueOf(trainerList.size()));
	}

	@Override
	protected void threadStart(Thread t, int tIndex) {
		// TODO
	}

	@Override
	protected void threadComplete(Thread t, int tIndex) {
		AppLogger.logInfo(getClass(), "[" + tIndex + "/" + threadNumber() + "] Found: " + ((AlgorithmTrainer)t).getBestConfiguration().toString());						
	}
	
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
