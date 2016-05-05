/**
 * 
 */
package ippoz.multilayer.detector.graphics;

import ippoz.multilayer.detector.algorithm.SPSDetector;

import java.awt.BasicStroke;
import java.awt.Color;
import java.util.HashMap;
import java.util.TreeMap;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.general.Dataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.util.ShapeUtilities;

/**
 * @author Tommy
 *
 */
public class XYChartDrawer extends ChartDrawer {

	public XYChartDrawer(String chartTitle, String xLabel, String yLabel, HashMap<String, TreeMap<Double, Double>> data) {
		super(chartTitle, xLabel, yLabel, data);
	}

	@Override
	protected void setupChart(HashMap<String, TreeMap<Double, Double>> data) {
		XYPlot plot = chart.getXYPlot( );
	     XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
	     int i = 0;
	     for(String seriesName : data.keySet()){
	    	 switch(seriesName){
	    	 	case SPSDetector.SPS_UPPER_BOUND:
	    	 		renderer.setSeriesPaint(i, Color.RED);
	    	 		renderer.setSeriesStroke(i, new BasicStroke(2.0f));
	    	 		renderer.setSeriesShapesVisible(i, false);
	    	 		break;
	    	 	case SPSDetector.SPS_OBSERVATION:
	    	 		renderer.setSeriesPaint(i, Color.BLUE);
	    	 		renderer.setSeriesStroke(i, new BasicStroke(4.0f));
	    	 		renderer.setSeriesShapesVisible(i, false);
	    	 		break;
	    	 	case SPSDetector.SPS_LOWER_BOUND:
	    	 		renderer.setSeriesPaint(i, Color.GREEN);
	    	 		renderer.setSeriesStroke(i, new BasicStroke(2.0f));
	    	 		renderer.setSeriesShapesVisible(i, false);
	    	 		break;
	    	 	case SPSDetector.SPS_ANOMALY:
	    	 		renderer.setSeriesPaint(i, Color.BLACK);
	    	 		renderer.setSeriesShape(i, ShapeUtilities.createDiamond(8));
	    	 		renderer.setSeriesShapesFilled(i, false);
	    	 		renderer.setSeriesShapesVisible(i, true);
	    	 		renderer.setSeriesLinesVisible(i, false);
	    	 		break;
	    	 	case SPSDetector.SPS_FAILURE:
	    	 		renderer.setSeriesPaint(i, Color.YELLOW);
	    	 		renderer.setSeriesShape(i, ShapeUtilities.createDiagonalCross(6, 2));
	    	 		renderer.setSeriesShapesFilled(i, true);
	    	 		renderer.setSeriesShapesVisible(i, true);
	    	 		renderer.setSeriesLinesVisible(i, false);
	    	 		break;
	    	 }
	    	 i++;
	     }
	     plot.setRenderer(renderer);
		
	}

	@Override
	protected JFreeChart createChart(String chartTitle, String xLabel, String yLabel, Dataset dataset, boolean showLegend, boolean createTooltip) {
		return ChartFactory.createXYLineChart(chartTitle, xLabel, yLabel, (XYDataset) dataset, PlotOrientation.VERTICAL, showLegend, createTooltip, false);	
	}

	@Override
	protected Dataset createDataset(HashMap<String, TreeMap<Double, Double>> data) {
		XYSeries current;
		XYSeriesCollection dataset = new XYSeriesCollection();
		for(String seriesName : data.keySet()){
			current = new XYSeries(seriesName);
			for(Double key : data.get(seriesName).keySet()){
				current.add(key, data.get(seriesName).get(key));
			}
			dataset.addSeries(current);
		}
		return dataset;
	}

}
