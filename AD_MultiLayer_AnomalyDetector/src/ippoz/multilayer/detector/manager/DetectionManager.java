/**
 * 
 */
package ippoz.multilayer.detector.manager;

import ippoz.multilayer.detector.configuration.AlgorithmConfiguration;
import ippoz.multilayer.detector.configuration.ConfidenceConfiguration;
import ippoz.multilayer.detector.configuration.HistoricalConfiguration;
import ippoz.multilayer.detector.configuration.RemoteCallConfiguration;
import ippoz.multilayer.detector.configuration.SPSConfiguration;
import ippoz.multilayer.detector.metric.Custom_Metric;
import ippoz.multilayer.detector.metric.FMeasure_Metric;
import ippoz.multilayer.detector.metric.FN_Metric;
import ippoz.multilayer.detector.metric.FP_Metric;
import ippoz.multilayer.detector.metric.FScore_Metric;
import ippoz.multilayer.detector.metric.Metric;
import ippoz.multilayer.detector.metric.Precision_Metric;
import ippoz.multilayer.detector.metric.Recall_Metric;
import ippoz.multilayer.detector.metric.TN_Metric;
import ippoz.multilayer.detector.metric.TP_Metric;
import ippoz.multilayer.detector.reputation.BetaReputation;
import ippoz.multilayer.detector.reputation.ConstantReputation;
import ippoz.multilayer.detector.reputation.MetricReputation;
import ippoz.multilayer.detector.reputation.Reputation;
import ippoz.multilayer.detector.support.AppLogger;
import ippoz.multilayer.detector.support.AppUtility;
import ippoz.multilayer.detector.support.PreferencesManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * The Class DetectionManager.
 * This is the manager for the detection. Coordinates all the other managers, especially the ones responsible for training and validation phases.
 *
 * @author Tommy
 */
public class DetectionManager {
	
	/** The Constant DB_USERNAME. */
	private static final String DB_USERNAME = "DB_USERNAME";
	
	/** The Constant DB_PASSWORD. */
	private static final String DB_PASSWORD = "DB_PASSWORD";
	
	/** The Constant TRAIN_RUN_PREFERENCE. */
	private static final String TRAIN_RUN_PREFERENCE = "TRAIN_RUN_IDS";
	
	/** The Constant VALIDATION_RUN_PREFERENCE. */
	private static final String VALIDATION_RUN_PREFERENCE = "VALIDATION_RUN_IDS";
	
	/** The Constant OUTPUT_FORMAT. */
	public static final String OUTPUT_FORMAT = "OUTPUT_TYPE";
	
	/** The Constant OUTPUT_FOLDER. */
	public static final String OUTPUT_FOLDER = "OUTPUT_FOLDER";
	
	/** The Constant METRIC_TYPE. */
	private static final String METRIC_TYPE = "METRIC"; 
	
	/** The Constant REPUTATION_TYPE. */
	private static final String REPUTATION_TYPE = "REPUTATION";
	
	/** The Constant CONF_FILE_FOLDER. */
	public static final String CONF_FILE_FOLDER = "CONF_FILE_FOLDER";
	
	/** The Constant SCORES_FILE_FOLDER. */
	public static final String SCORES_FILE_FOLDER = "SCORES_FILE_FOLDER";
	
	/** The Constant SETUP_FILE_FOLDER. */
	public static final String SETUP_FILE_FOLDER = "SETUP_FILE_FOLDER";
	
	/** The Constant TRAIN_NEEDED_FLAG. */
	private static final String TRAIN_NEEDED_FLAG = "TRAIN_FLAG";
	
	/** The Constant DETECTION_PREFERENCES_FILE. */
	private static final String DETECTION_PREFERENCES_FILE = "DETECTION_PREFERENCES_FILE";
	
	/** The Constant DM_ANOMALY_TRESHOLD. */
	private static final String DM_ANOMALY_TRESHOLD = "ANOMALY_TRESHOLD";
	
	/** The Constant DM_SCORE_TRESHOLD. */
	private static final String DM_SCORE_TRESHOLD = "INDICATOR_SCORE_TRESHOLD";
	
	/** The Constant DM_CONVERGENCE_TIME. */
	private static final String DM_CONVERGENCE_TIME = "CONVERGENCE_TIME";
	
	/** The main preference manager. */
	private PreferencesManager prefManager;
	
	/** The detection preference manager. */
	private PreferencesManager detectionManager;
	
	/** The timings manager. */
	private TimingsManager pManager;
	
	/** The trainer manager. */
	private TrainerManager tManager;
	
	/** The evaluation manager. */
	private EvaluatorManager eManager;
	
	/** The chosen metric. */
	private Metric metric;
	
	/** The chosen reputation metric. */
	private Reputation reputation;
	
	/** The used data types (plain, diff...). */
	private String[] dataTypes;
	
	/** The algorithm types (SPS, Historical...). */
	private String[] algTypes;
	
	/**
	 * Instantiates a new detection manager.
	 *
	 * @param prefManager the main preference manager
	 */
	public DetectionManager(PreferencesManager prefManager){
		this.prefManager = prefManager;
		pManager = new TimingsManager();
		detectionManager = new PreferencesManager(prefManager.getPreference(DETECTION_PREFERENCES_FILE));
		metric = getMetric();
		reputation = getReputation(metric);
		dataTypes = getDataTypes();
		algTypes = getAlgTypes();
		if(needTest())
			tManager = new TrainerManager(prefManager, pManager, new LoaderManager(readRunIds(TRAIN_RUN_PREFERENCE), "train", pManager, prefManager.getPreference(DB_USERNAME), prefManager.getPreference(DB_PASSWORD)).fetch(), loadConfigurations(), metric, reputation, dataTypes, algTypes);
		eManager = new EvaluatorManager(prefManager, pManager, new LoaderManager(readRunIds(VALIDATION_RUN_PREFERENCE), "validation", pManager, prefManager.getPreference(DB_USERNAME), prefManager.getPreference(DB_PASSWORD)).fetch(), loadValidationMetrics(), detectionManager.getPreference(DM_ANOMALY_TRESHOLD), Double.parseDouble(detectionManager.getPreference(DM_CONVERGENCE_TIME)), Double.parseDouble(detectionManager.getPreference(DM_SCORE_TRESHOLD)));
		pManager.addTiming(TimingsManager.SCORING_METRIC, metric.getMetricName());
		pManager.addTiming(TimingsManager.REPUTATION_METRIC, reputation.getReputationTag());
		pManager.addTiming(TimingsManager.EXECUTION_TIME, new Date().toString());
	}
	
	/**
	 * Loads the validation metrics, used to score the final result.
	 *
	 * @return the list of metrics
	 */
	private Metric[] loadValidationMetrics() {
		File dataTypeFile = new File(prefManager.getPreference(DetectionManager.SETUP_FILE_FOLDER) + "validationMetrics.preferences");
		LinkedList<Metric> metricList = new LinkedList<Metric>();
		BufferedReader reader;
		String readed;
		try {
			if(dataTypeFile.exists()){
				reader = new BufferedReader(new FileReader(dataTypeFile));
				while(reader.ready()){
					readed = reader.readLine();
					if(readed != null){
						readed = readed.trim();
						if(readed.length() > 0 && !readed.trim().startsWith("*")){
							metricList.add(getMetric(readed.trim()));
						}
					}
				}
				reader.close();
			} 
		} catch(Exception ex){
			AppLogger.logException(getClass(), ex, "Unable to read data types");
		}
		return metricList.toArray(new Metric[metricList.size()]);
	}

	/**
	 * Starts the train process.
	 */
	public void train(){
		tManager.train();
	}
	
	/**
	 * Starts the evaluation process.
	 */
	public void evaluate(){
		eManager.detectAnomalies();
	}
	
	/**
	 * Gets the data types.
	 *
	 * @return the data types
	 */
	private String[] getDataTypes() {
		File dataTypeFile = new File(prefManager.getPreference(DetectionManager.SETUP_FILE_FOLDER) + "dataSeries.preferences");
		LinkedList<String> dataList = new LinkedList<String>();
		BufferedReader reader;
		String readed;
		try {
			if(dataTypeFile.exists()){
				reader = new BufferedReader(new FileReader(dataTypeFile));
				while(reader.ready()){
					readed = reader.readLine();
					if(readed != null){
						readed = readed.trim();
						if(readed.length() > 0 && !readed.trim().startsWith("*")){
							dataList.add(readed.trim());
						}
					}
				}
				reader.close();
			} 
		} catch(Exception ex){
			AppLogger.logException(getClass(), ex, "Unable to read data types");
		}
		return dataList.toArray(new String[dataList.size()]);
	}
	
	/**
	 * Gets the algorithm types.
	 *
	 * @return the algorithm types
	 */
	private String[] getAlgTypes() {
		File algTypeFile = new File(prefManager.getPreference(DetectionManager.SETUP_FILE_FOLDER) + "detectionAlgorithms.preferences");
		LinkedList<String> algTypeList = new LinkedList<String>();
		BufferedReader reader;
		String readed;
		try {
			if(algTypeFile.exists()){
				reader = new BufferedReader(new FileReader(algTypeFile));
				while(reader.ready()){
					readed = reader.readLine();
					if(readed != null){
						readed = readed.trim();
						if(readed.length() > 0 && !readed.trim().startsWith("*")){
							algTypeList.add(readed.trim());
						}
					}
				}
				reader.close();
			} 
		} catch(Exception ex){
			AppLogger.logException(getClass(), ex, "Unable to read data types");
		}
		return algTypeList.toArray(new String[algTypeList.size()]);
	}

	/**
	 * Loads the possible configurations for all the algorithms.
	 *
	 * @return the map of the configurations
	 */
	private HashMap<String, LinkedList<AlgorithmConfiguration>> loadConfigurations() {
		File confFile = new File(prefManager.getPreference(DetectionManager.CONF_FILE_FOLDER) + "conf.csv");
		HashMap<String, LinkedList<AlgorithmConfiguration>> confList = new HashMap<String, LinkedList<AlgorithmConfiguration>>();
		AlgorithmConfiguration alConf;
		BufferedReader reader;
		String[] header;
		String readed;
		int i;
		try {
			if(confFile.exists()){
				reader = new BufferedReader(new FileReader(confFile));
				readed = reader.readLine();
				if(readed != null){
					header = readed.split(",");
					while(reader.ready()){
						readed = reader.readLine();
						if(readed != null){
							readed = readed.trim();
							if(readed.length() > 0){
								if(readed.split(",")[0].toUpperCase().equals("TYPE")){
									header = readed.split(",");
								} else {
									i = 0;
									if(readed.split(",")[0].toUpperCase().equals("SPS")){
										alConf = new SPSConfiguration();	
									} else if(readed.split(",")[0].toUpperCase().equals("HIST")){
										alConf = new HistoricalConfiguration();
									} else if(readed.split(",")[0].toUpperCase().equals("CONF")){
										alConf = new ConfidenceConfiguration();
									} else if(readed.split(",")[0].toUpperCase().equals("RCC")){
										alConf = new RemoteCallConfiguration();
									} else alConf = null;
									for(String element : readed.split(",")){
										if(i > 0)
											alConf.addItem(header[i], element);
										i++;
									}
									if(confList.get(readed.split(",")[0].toUpperCase()) == null)
										confList.put(readed.split(",")[0].toUpperCase(), new LinkedList<AlgorithmConfiguration>());
									confList.get(readed.split(",")[0].toUpperCase()).add(alConf);
								}
							}
						}
					}
				}
				reader.close();
			} 
		} catch(Exception ex){
			AppLogger.logException(getClass(), ex, "Unable to read configurations");
		}
		return confList;
	}
	
	/**
	 * Returns run IDs parsing a specific tag.
	 *
	 * @param runTag the run tag
	 * @return the list of IDs
	 */
	private LinkedList<String> readRunIds(String runTag){
		String from, to;
		String idPref = prefManager.getPreference(runTag).trim();
		LinkedList<String> idList = new LinkedList<String>();
		if(idPref != null && idPref.length() > 0){
			if(idPref.contains("-")){
				from = idPref.split("-")[0].trim();
				to = idPref.split("-")[1].trim();
				for(int i=Integer.parseInt(from);i<=Integer.parseInt(to);i++){
					idList.add(String.valueOf(i));
				}
			} else {
				for(String id : idPref.split(",")){
					idList.add(id.trim());
				}
			}
		}
		return idList;
	}
	
	/**
	 * Gets the reputation.
	 *
	 * @param metric the chosen metric
	 * @return the obtained reputation
	 */
	private Reputation getReputation(Metric metric) {
		String reputationType = prefManager.getPreference(REPUTATION_TYPE);
		switch(reputationType.toUpperCase()){
			case "BETA":
				return new BetaReputation(reputationType);
			case "METRIC":
				return new MetricReputation(reputationType, metric);
			default:
				if(AppUtility.isNumber(reputationType))
					return new ConstantReputation(reputationType, Double.parseDouble(reputationType));
				else return null;
		}
	}
	
	/**
	 * Gets the metric.
	 *
	 * @param metricType the metric tag
	 * @return the obtained metric
	 */
	private Metric getMetric(String metricType){
		String param = null;
		if(metricType.contains("(")){
			param = metricType.substring(metricType.indexOf("(")+1, metricType.indexOf(")"));
			metricType = metricType.substring(0, metricType.indexOf("("));
		}
		switch(metricType.toUpperCase()){
			case "TP":
			case "TRUEPOSITIVE":
				return new TP_Metric(false);
			case "TN":
			case "TRUENEGATIVE":
				return new TN_Metric(false);
			case "FN":
			case "FALSENEGATIVE":
				return new FN_Metric(false);
			case "FP":
			case "FALSEPOSITIVE":
				return new FP_Metric(false);
			case "PRECISION":
				return new Precision_Metric();
			case "RECALL":
				return new Recall_Metric();
			case "F-MEASURE":
			case "FMEASURE":
				return new FMeasure_Metric();
			case "F-SCORE":
			case "FSCORE":
				return new FScore_Metric(Double.valueOf(param));
			case "CUSTOM":
				return new Custom_Metric();
			default:
				return null;
		}
	}

	/**
	 * Returns the metric.
	 *
	 * @return the metric
	 */
	private Metric getMetric() {
		return getMetric(prefManager.getPreference(METRIC_TYPE));
	}
	
	/**
	 * Returns a boolean targeting the need of training before evaluation.
	 *
	 * @return true if training is needed
	 */
	public boolean needTest(){
		return !prefManager.getPreference(TRAIN_NEEDED_FLAG).equals("0");
	}

	/**
	 * Prints the timings details.
	 */
	public void printDetails() {
		pManager.printTimings(prefManager.getPreference(DetectionManager.SCORES_FILE_FOLDER) + "performanceScores.csv");
	}
	
}
