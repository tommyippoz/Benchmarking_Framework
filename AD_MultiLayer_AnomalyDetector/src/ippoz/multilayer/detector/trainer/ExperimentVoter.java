/**
 * 
 */
package ippoz.multilayer.detector.trainer;

import ippoz.multilayer.commons.layers.LayerType;
import ippoz.multilayer.detector.data.ExperimentData;
import ippoz.multilayer.detector.data.Snapshot;
import ippoz.multilayer.detector.graphics.HistogramChartDrawer;
import ippoz.multilayer.detector.metric.Metric;
import ippoz.multilayer.detector.support.AppLogger;
import ippoz.multilayer.detector.support.AppUtility;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeMap;

// TODO: Auto-generated Javadoc
/**
 * The Class ExperimentVoter.
 *
 * @author Tommy
 */
public class ExperimentVoter extends Thread {
	
	/** The Constant IMG_WIDTH for printing. */
	private static final int IMG_WIDTH = 1000;
	
	/** The Constant IMG_HEIGHT for printing. */
	private static final int IMG_HEIGHT = 1000;
	
	/** The Constant ANOMALY_SCORE_LABEL. */
	public static final String ANOMALY_SCORE_LABEL = "Anomaly Score";
	
	/** The Constant FAILURE_LABEL. */
	public static final String FAILURE_LABEL = "Failure";
	
	/** The experiment data. */
	private ExperimentData expData;
	
	/** The algorithm list. */
	private LinkedList<AlgorithmVoter> algList;
	
	/** The complete results of the voting. */
	private TreeMap<Date, HashMap<AlgorithmVoter, Double>> partialVoting;
	
	/** The contracted results of the voting. */
	private TreeMap<Date, Double> voting;
	
	/**
	 * Instantiates a new experiment voter.
	 *
	 * @param expData the experiment data
	 * @param algList the algorithm list
	 */
	public ExperimentVoter(ExperimentData expData, LinkedList<AlgorithmVoter> algList) {
		super();
		this.expData = expData;
		this.algList = deepClone(algList);
	}
	
	/**
	 * Deep clone of the voters' list.
	 *
	 * @param algorithms the algorithms
	 * @return the deep-cloned list
	 */
	private LinkedList<AlgorithmVoter> deepClone(LinkedList<AlgorithmVoter> algorithms) {
		LinkedList<AlgorithmVoter> list = new LinkedList<AlgorithmVoter>();
		try {
			for(AlgorithmVoter aVoter : algorithms){
				list.add(aVoter.clone());
			}
		} catch (CloneNotSupportedException ex) {
			AppLogger.logException(getClass(), ex, "Unable to clone Experiment");
		}
		return list;
	}

	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		Snapshot snapshot;
		HashMap<AlgorithmVoter, Double> snapVoting;
		partialVoting = new TreeMap<Date, HashMap<AlgorithmVoter, Double>>();
		voting = new TreeMap<Date, Double>();
		expData.resetIterator();
		while(expData.hasNextSnapshot()){
			snapshot = expData.nextSnapshot();
			snapVoting = new HashMap<AlgorithmVoter, Double>();
			for(AlgorithmVoter aVoter : algList){
				snapVoting.put(aVoter, aVoter.voteSnapshot(snapshot));
			}
			partialVoting.put(snapshot.getTimestamp(), snapVoting);
			voting.put(snapshot.getTimestamp(), voteResults(snapVoting));
		}
	}
	
	/**
	 * Votes results obtaining a contracted indication about anomaly (double score)
	 *
	 * @param algResults the complete algorithm scoring results
	 * @return contracted anomaly score
	 */
	private double voteResults(HashMap<AlgorithmVoter, Double> algResults){
		double snapScore = 0.0;
		for(AlgorithmVoter aVoter : algList){
			snapScore = snapScore + 1.0*aVoter.getReputationScore()*algResults.get(aVoter);
		}
		return snapScore;
	}
	
	/**
	 * Prints the anomaly voting.
	 *
	 * @param outFormat the output format
	 * @param outFolderName the output folder
	 * @param validationMetrics the metrics used for validation and printed in the file
	 * @param anomalyTreshold the anomaly threshold
	 * @param algConvergence the algorithm convergence time (for printing)
	 */
	public void printVoting(String outFormat, String outFolderName, Metric[] validationMetrics, double anomalyTreshold, double algConvergence) {
		printExperimentVoting(outFolderName, validationMetrics, anomalyTreshold, algConvergence);
		for(AlgorithmVoter aVoter : algList){
			aVoter.printResults(outFormat, outFolderName, expData.getName());
		}
	}

	/**
	 * Prints the experiment voting.
	 *
	 * @param outFolderName the output folder
	 * @param validationMetrics the metrics used for validation and printed in the file
	 * @param anomalyTreshold the anomaly threshold
	 * @param algConvergence the algorithm convergence time (for printing)
	 */
	private void printExperimentVoting(String outFolderName, Metric[] validationMetrics, double anomalyTreshold, double algConvergence) {
		printGraphics(outFolderName, anomalyTreshold, algConvergence);
		printText(outFolderName);
		printMetrics(outFolderName, validationMetrics, anomalyTreshold);
	}
	
	/**
	 * Prints the metrics.
	 *
	 * @param outFolderName the output folder
	 * @param validationMetrics the metrics used for validation and printed in the file
	 * @param anomalyTreshold the anomaly threshold
	 */
	private synchronized void printMetrics(String outFolderName, Metric[] validationMetrics, double anomalyTreshold) {
		PrintWriter pw;
		try {
			pw = new PrintWriter(new FileOutputStream(new File(outFolderName + "/voter/results.csv"), true));
			pw.append(expData.getName() + "," + expData.getSnapshotNumber() + ",");
			for(Metric met : validationMetrics){
				pw.append(String.valueOf(met.evaluateAnomalyResults(expData, voting, anomalyTreshold)) + ",");
			}
			pw.append("\n");
			pw.close();
		} catch (FileNotFoundException ex) {
			AppLogger.logException(getClass(), ex, "Unable to find results file");
		} 
	}

	/**
	 * Prints the graphics.
	 *
	 * @param outFolderName the output folder
	 * @param anomalyTreshold the anomaly threshold
	 * @param algConvergence the algorithm convergence time (for printing)
	 */
	private void printGraphics(String outFolderName, double anomalyTreshold, double algConvergence){
		HistogramChartDrawer hist;
		HashMap<String, TreeMap<Double, Double>> voterMap = new HashMap<String, TreeMap<Double, Double>>();
		voterMap.put(ANOMALY_SCORE_LABEL, AppUtility.convertMapSnapshots(voting));
		voterMap.put(FAILURE_LABEL, AppUtility.convertFailures(expData));
		hist = new HistogramChartDrawer("Anomaly Score", "Seconds", "Score", voterMap, anomalyTreshold, algConvergence);
		hist.saveToFile(outFolderName + "/voter/graphic/" + expData.getName() + ".png", IMG_WIDTH, IMG_HEIGHT);
	}
	
	/**
	 * Prints the textual summarization of the voting.
	 *
	 * @param outFolderName the output folder
	 */
	private void printText(String outFolderName){
		BufferedWriter writer = null;
		HashMap<LayerType, HashMap<String, Integer>> countMap;
		String partial;
		int count;
		try {
			countMap = buildMap();
			writer = new BufferedWriter(new FileWriter(new File(outFolderName + "/voter/" + expData.getName() + ".csv")));
			writer.write("timestamp,anomaly_alerts,");
			for(LayerType currentLayer : countMap.keySet()){
				for(String algTag : countMap.get(currentLayer).keySet()){
					writer.write(currentLayer.toString() + "@" + algTag + ",");
				}
			}
			writer.write("details\n");
			for(Date timestamp : partialVoting.keySet()){
				countMap = buildMap();
				partial = "";
				count = 0;
				for(AlgorithmVoter aVoter : algList){
					if(partialVoting.get(timestamp).get(aVoter) > 0.0){
						countMap.get(aVoter.getLayerType()).replace(aVoter.getAlgorithmType(), countMap.get(aVoter.getLayerType()).get(aVoter.getAlgorithmType()) + 1);			
						partial = partial + aVoter.toString() + "|";
						count++;
					}
				}
				writer.write(AppUtility.getSecondsBetween(timestamp, expData.getFirstTimestamp()) + ",");
				writer.write(count + ",");
				for(LayerType currentLayer : countMap.keySet()){
					for(String algTag : countMap.get(currentLayer).keySet()){
						writer.write(countMap.get(currentLayer).get(algTag) + ",");
					}
				}
				writer.write(partial + "\n");
			}
			writer.close();
		} catch(IOException ex){
			AppLogger.logException(getClass(), ex, "Unable to save voting text output");
		} 
	}

	/**
	 * Builds the basic map used in printText function.
	 *
	 * @return the basic map
	 */
	private HashMap<LayerType, HashMap<String, Integer>> buildMap() {
		HashMap<LayerType, HashMap<String, Integer>> map = new HashMap<LayerType, HashMap<String, Integer>>();
		for(AlgorithmVoter aVoter : algList){
			if(!map.keySet().contains(aVoter.getLayerType()))
				map.put(aVoter.getLayerType(), new HashMap<String, Integer>());
			if(!map.get(aVoter.getLayerType()).containsKey(aVoter.getAlgorithmType()))
				map.get(aVoter.getLayerType()).put(aVoter.getAlgorithmType(), 0);
		}
		return map;
	}

}
