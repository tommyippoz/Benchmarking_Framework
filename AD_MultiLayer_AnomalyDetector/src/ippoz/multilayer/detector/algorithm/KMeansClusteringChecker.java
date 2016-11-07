package ippoz.multilayer.detector.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import ippoz.multilayer.detector.commons.algorithm.AlgorithmType;
import ippoz.multilayer.detector.commons.algorithm.KMeans;
import ippoz.multilayer.detector.commons.algorithm.KMeansData;
import ippoz.multilayer.detector.commons.algorithm.Point;
import ippoz.multilayer.detector.commons.configuration.AlgorithmConfiguration;
import ippoz.multilayer.detector.commons.data.DataSeriesSnapshot;
import ippoz.multilayer.detector.commons.data.Snapshot;
import ippoz.multilayer.detector.commons.dataseries.DataSeries;
import ippoz.multilayer.detector.commons.service.ServiceCall;

public class KMeansClusteringChecker extends DataSeriesDetectionAlgorithm implements AutomaticTrainingAlgorithm {

	public KMeansClusteringChecker(DataSeries dataSeries, AlgorithmConfiguration conf) {
		super(dataSeries, conf);
	}

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
			System.out.println("1");
			return 1;
		}

		//System.out.println("0");
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
	public AlgorithmConfiguration automaticTraining(HashMap<String, LinkedList<Snapshot>> algExpSnapshots) {

		AlgorithmConfiguration ac = new AlgorithmConfiguration(AlgorithmType.KMEANS);
		ArrayList<KMeansData> kmeansData = new ArrayList<KMeansData>();
		String indName = null;

		for (LinkedList<Snapshot> expSnapList : algExpSnapshots.values()) {
			for (Snapshot snap : expSnapList) {
				DataSeriesSnapshot dss = (DataSeriesSnapshot) snap;
				indName = dss.getDataSeries().getName();
				for (ServiceCall sc : snap.getServiceCalls()) {
					KMeansData kmd = new KMeansData(sc.getServiceName(), dss.getDataSeries().getName(),
							snap.getTimestamp().toString(), sc.getServiceName(), dss.getSnapValue().toString(),
							dss.getDataSeries().getLayerType().toString());
					kmeansData.add(kmd);
				}
			}
		}

		KMeans kmeans = new KMeans(true);
		kmeans.setData(kmeansData);
		kmeans.init();
		kmeans.calculate();
		kmeans.setThreshould();

		String td = kmeans.saveData();

		ac.addItem(indName, td);

		kmeans = null;

		return ac;
	}
}
