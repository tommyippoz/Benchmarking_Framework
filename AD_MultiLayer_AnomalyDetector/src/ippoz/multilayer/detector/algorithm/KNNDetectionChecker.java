package ippoz.multilayer.detector.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import ippoz.multilayer.detector.commons.algorithm.Point;
import ippoz.multilayer.detector.commons.configuration.AlgorithmConfiguration;
import ippoz.multilayer.detector.commons.data.DataSeriesSnapshot;
import ippoz.multilayer.detector.commons.data.Snapshot;
import ippoz.multilayer.detector.commons.dataseries.DataSeries;

/**
 * Implementation of the KNN Anomaly Detection algorithm described
 * in Abid, A., & Mahfoudhi, A. (2016). Anomaly detection through 
 * outlier and neighborhood data in Wireless Sensor Networks, 26–30.
 * 
 * All distances between two points were calculated using the
 * Euclidean Distance.
 * 
 * @author filipefalcao
 */
public class KNNDetectionChecker extends DataSeriesDetectionAlgorithm implements AutomaticTrainingAlgorithm {
	
	private List<Point> points;

	public KNNDetectionChecker(DataSeries dataSeries, AlgorithmConfiguration conf) {
		super(dataSeries, conf);
		this.points = new ArrayList<Point>();
	}

	/**
	 * Create the links between the given points. A link will be
	 * created for the smallest distance between the current point
	 * and its predecessor and successor. If the distance to the
	 * predecessor is the same to the successor, two links will be
	 * created.
	 * 
	 * @author filipefalcao
	 */
	public void createLinks() {
		double distanceToThePrevious = 0, distanceToTheNext = 0;
		Point currentPoint, previousPoint = null, nextPoint = null;
		List<Point> links = new ArrayList<Point>();
		
		for (int i = 0; i < this.points.size(); i++) {
			currentPoint = this.points.get(i);
			
			/* 
			 * If the current point is the first point, calculate 
			 * the distance only to the next point. If the current 
			 * point is the last point, calculate the distance only 
			 * to the previous point. Else, calculate both distances.
			 */
			if (i == 0) {
				nextPoint = this.points.get(i + 1);
				distanceToTheNext = Point.distance(currentPoint, nextPoint);
			} else if (i == (this.points.size() - 1)) {
				previousPoint = this.points.get(i - 1);
				distanceToThePrevious = Point.distance(currentPoint, previousPoint);
			} else {
				nextPoint = this.points.get(i + 1);
				previousPoint = this.points.get(i - 1);
				distanceToThePrevious = Point.distance(currentPoint, previousPoint);
				distanceToTheNext = Point.distance(currentPoint, nextPoint);
			}
			
			/*
			 * If the distance to the previous point is the smallest, 
			 * add a link to the previous point. If the distance to 
			 * the next point is the smallest, add a link to the next 
			 * point. If both distances are equal, add links to both 
			 * points. 
			 */
			if (distanceToThePrevious < distanceToTheNext) {
				links.add(previousPoint);
			} else if (distanceToTheNext < distanceToThePrevious) {
				links.add(nextPoint);
			} else {
				links.add(previousPoint);
				links.add(nextPoint);
			}
			
			points.get(i).setLinks(links);
		}
	}
	
	/**
	 * Remove the links between the given points. A link will be
	 * removed if the distance to its successor is bigger than the
	 * distance of the successor to the last point.
	 * 
	 * @author filipefalcao
	 */
	public void removeLinks() {
		int previousIndex = -1, nextIndex;
		double distanceToTheNext, distanceToTheLast;
		Point currentPoint, previousPoint = null, nextPoint = null, lastPoint;
		List<Point> previousLinks, nextLinks;
		
		previousLinks = new ArrayList<Point>();
		nextLinks = new ArrayList<Point>();
		lastPoint = this.points.get(this.points.size() - 1);
		
		for (int i = 0; i < this.points.size(); i++) {
			currentPoint = this.points.get(i);
			
			/*
			 * If the current point is not the last point, calculate the
			 * distance to the next point and the distance to the last 
			 * point.
			 */
			if (i != (this.points.size() - 1)) {
				if (i != 0) {
					previousPoint = this.points.get(i - 1);
					previousIndex = previousPoint.getLinks().indexOf(currentPoint);
				}
				
				nextPoint = this.points.get(i + 1);
				nextIndex = nextPoint.getLinks().indexOf(currentPoint);
				distanceToTheNext = Point.distance(currentPoint, nextPoint);
				distanceToTheLast = Point.distance(nextPoint, lastPoint);
				
				/*
				 * If the distance to the next point is bigger than
				 * the distance from the next point to the last point,
				 * remove all the links to the current point.
				 */
				if (distanceToTheNext > distanceToTheLast) {
					if (previousIndex != -1) {
						previousLinks = previousPoint.getLinks();
						previousLinks.remove(previousIndex);
						previousPoint.setLinks(previousLinks);
					} else if (nextIndex != -1) {
						nextLinks = nextPoint.getLinks();
						nextLinks.remove(nextIndex);
						nextPoint.setLinks(nextLinks);
					}
				}
			}
		}
	}
	
	/**
	 * Finds all the outliers present in the given points.
	 * 
	 * @return A List with all the found ouliers
	 */
	public List<Point> findOutliers() {
		Point currentPoint;
		List<Point> outliers = new ArrayList<Point>();
		
		for (int i = 0; i < this.points.size(); i++) {
			currentPoint = this.points.get(i);
			
			if (currentPoint.getLinks().size() == 0) {
				outliers.add(currentPoint);
			}
		}
		
		return outliers;
	}
	
	@Override
	public AlgorithmConfiguration automaticTraining(HashMap<String, LinkedList<Snapshot>> algExpSnapshots) {
		return null;
	}

	@Override
	protected double evaluateDataSeriesSnapshot(DataSeriesSnapshot sysSnapshot) {
		return 0;
	}

	@Override
	protected void printImageResults(String outFolderName, String expTag) {
		
	}

	@Override
	protected void printTextResults(String outFolderName, String expTag) {
		
	}
	
}
