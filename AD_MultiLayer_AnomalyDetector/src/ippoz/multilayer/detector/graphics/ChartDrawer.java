/**
 * 
 */
package ippoz.multilayer.detector.graphics;

import ippoz.multilayer.detector.support.AppLogger;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.TreeMap;

import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.Dataset;

/**
 * @author Tommy
 *
 */
public abstract class ChartDrawer {
	
	protected JFreeChart chart;
	
	public ChartDrawer(String chartTitle, String xLabel, String yLabel, HashMap<String, TreeMap<Double, Double>> data){
		chart = createChart(chartTitle, xLabel, yLabel, createDataset(data), true, true);
		setupChart(data);
	}

	protected abstract void setupChart(HashMap<String, TreeMap<Double, Double>> data);

	protected abstract JFreeChart createChart(String chartTitle, String xLabel, String yLabel, Dataset dataset, boolean showLegend, boolean createTooltip);
	
	protected abstract Dataset createDataset(HashMap<String, TreeMap<Double, Double>> data);
	
	public void saveToFile(String filename, int width, int height){
		File file = new File(filename);
		File parentFolder = new File(file.getParent());
		try {
			if(!parentFolder.exists())
				parentFolder.mkdirs();
			ChartUtilities.saveChartAsPNG(new File(filename), chart, width, height);
		} catch (IOException ex) {
			AppLogger.logException(getClass(), ex, "Unable to save chart to file");
		}
	}
}
