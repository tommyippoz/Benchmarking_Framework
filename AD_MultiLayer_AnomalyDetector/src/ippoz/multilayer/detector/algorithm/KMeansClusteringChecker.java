package ippoz.multilayer.detector.algorithm;

import java.util.ArrayList;
import java.util.HashMap;

import ippoz.multilayer.detector.commons.algorithm.AlgorithmType;
import ippoz.multilayer.detector.commons.algorithm.KMeans;
import ippoz.multilayer.detector.commons.algorithm.KMeansData;
import ippoz.multilayer.detector.commons.algorithm.Point;
import ippoz.multilayer.detector.commons.configuration.AlgorithmConfiguration;
import ippoz.multilayer.detector.commons.data.DataSeriesSnapshot;
import ippoz.multilayer.detector.commons.datafetcher.database.DatabaseConnector;
import ippoz.multilayer.detector.commons.dataseries.DataSeries;
import ippoz.multilayer.detector.commons.support.PreferencesManager;

public class KMeansClusteringChecker extends DataSeriesDetectionAlgorithm implements AutomaticTrainingAlgorithm {

	public KMeansClusteringChecker(DataSeries dataSeries, AlgorithmConfiguration conf) {
		super(dataSeries, conf);
	}

	int i;

	@Override
	protected double evaluateDataSeriesSnapshot(DataSeriesSnapshot sysSnapshot) {
		// TODO Auto-generated method stub
		String trainingClusters = conf.getItem(sysSnapshot.getDataSeries().getName());

		String[] tClusters = trainingClusters.split(",");

		int count = 0;

		Point p = null;
		double value = sysSnapshot.getSnapValue();

		for (int i = 0; i < tClusters.length; i++) {

			if (tClusters[i].contains("centroid")) {
				i++;
				double axisX = Double.parseDouble(tClusters[i]);
				i++;
				double axisY = Double.parseDouble(tClusters[i]);
				p = new Point(axisX, axisY);
			} else {

				if (tClusters[i] == null || tClusters[i].equals("")) {
					continue;
				}

				double trainingThreshould = Double.parseDouble(tClusters[i]);
				Point p1 = new Point(value, p.getX());
				double distance = Point.distance(p, p1);
				if (distance > trainingThreshould) {
					count++;
				}
			}

		}

		if (count == tClusters.length) {
			return 1;
		}

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
	public AlgorithmConfiguration automaticTraining() {
		
		PreferencesManager prefManager = new PreferencesManager("detector.preferences");
		PreferencesManager detectionManager = new PreferencesManager(prefManager.getPreference("DETECTION_PREFERENCES_FILE"));
		String dbName = detectionManager.getPreference("DB_USERNAME");
		String password = detectionManager.getPreference("DB_PASSWORD"); 

		AlgorithmConfiguration ac = new AlgorithmConfiguration(AlgorithmType.KMEANS);
		ArrayList<String> indicators = new ArrayList<String>();
		DatabaseConnector dc = new DatabaseConnector("experiment", dbName, password, false);

		for (HashMap<String, String> d : dc.executeCustomQuery(null, "select in_tag from indicator")) {
			if (d != null && d.get("in_tag") != null) {
				indicators.add(d.get("in_tag"));
			}
		}

		for (String indicator : indicators) {

			ArrayList<KMeansData> kmeansData = new ArrayList<KMeansData>();
			for (HashMap<String, String> d : dc.executeCustomQuery(null,
					"select in_tag as 'indicator_name', pt_description as 'layer', ioc_value as 'value', ob_time, se_name as 'service_name', me_name as 'method_name' "
							+ "from (select in_tag, pt_description, ioc_value, ob_time, run_id from run "
							+ "natural join observation natural join indicator_observation "
							+ "natural join indicator_observation_category natural join indicator "
							+ "natural join probe_type " + "where in_tag = '" + indicator
							+ "' and value_category_id = 1 " + "and run_id > 1600 " + "and run_id < 1680 "
							+ "order by indicator_id, ob_time) as t1 " + "inner join " + "(select * "
							+ "from service_method_invocation smi " + "natural join service_method "
							+ "natural join method " + "natural join service) as t2 "
							+ "on t1.run_id = t2.run_id and t1.ob_time >= t2.start_time and t1.ob_time <= t2.end_time "
							+ "order by service_name, indicator_name, ob_time")) {

				KMeansData kmd = new KMeansData(d.get("method_name"), d.get("indicator_name"), d.get("ob_time"),
						d.get("service_name"), d.get("value"), d.get("layer"));
				kmeansData.add(kmd);

			}

			KMeans kmeans = new KMeans(true);
			kmeans.setData(kmeansData);
			kmeans.init();
			kmeans.calculate();
			kmeans.setThreshould();

			String td = kmeans.saveData();

			ac.addItem(indicator, td);

			kmeans = null;
		}
		dc = null;
		indicators = null;

		return ac;
	}

	/*
	 * public int calculateDistanceRate() {
	 * 
	 * ArrayList<Double> distances = new ArrayList<Double>(); double
	 * MAX_DISTANCE = 0; double MIN_DISTANCE = 0;
	 * 
	 * for (int i = 0; i < trainClusters.size(); i++) { for (int j = 0; j <
	 * evalClusters.size(); j++) { double distance = distanceCentroids.get("T" +
	 * i + ":E" + j); distances.add(distance); if (distance > MAX_DISTANCE) {
	 * MAX_DISTANCE = distance; } else if (distance < MIN_DISTANCE) {
	 * MIN_DISTANCE = distance; } } }
	 * 
	 * if (MAX_DISTANCE == 0) { MAX_DISTANCE = 1; }
	 * 
	 * for (Double d : distances) { evaluateDistances.put(d, d / MAX_DISTANCE);
	 * }
	 * 
	 * for (int i = 0; i < trainClusters.size(); i++) { for (int j = 0; j <
	 * evalClusters.size(); j++) { double distance = distanceCentroids.get("T" +
	 * i + ":E" + j); double eval = evaluateDistances.get(distance);
	 * 
	 * if (eval > 0.3) { anomalousClusters.add(evalClusters.get(j)); //
	 * System.out.println("Evaluation between: T" + i + ":E" + j // + " = " +
	 * eval); return 1; } } }
	 * 
	 * return 0; }
	 * 
	 * public void getDistanceBetweenClusters() {
	 * 
	 * System.out.println(trainClusters.size());
	 * System.out.println(evalClusters.size());
	 * 
	 * for (Cluster tc1 : trainClusters) { Point tc = tc1.getCentroid(); for
	 * (Cluster ec1 : evalClusters) { Point ec = ec1.getCentroid(); // Centering
	 * the centroids, so we can get the minimum distance // between them
	 * (ortogonal distance) ec.setX(tc.getX());
	 * 
	 * // Getting the distance between them //
	 * System.out.println(ec.distance(tc, ec)); //
	 * System.out.println("Centroid Cluster T" + tc1.getId() + ": " // +
	 * tc1.getCentroid()); // System.out.println("Centroid Cluster E" +
	 * ec1.getId() + ": " // + ec1.getCentroid()); //
	 * System.out.println("Distance Between Clusters T" + // tc1.getId() +
	 * " and E" + ec1.getId() + " is " // + ec.distance(tc, ec));
	 * distanceCentroids.put("T" + tc1.getId() + ":E" + ec1.getId(),
	 * ec.distance(tc, ec)); } } }
	 * 
	 * }
	 */

}
