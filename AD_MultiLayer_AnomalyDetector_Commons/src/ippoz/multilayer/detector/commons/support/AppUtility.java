/**
 * 
 */
package ippoz.multilayer.detector.commons.support;

import ippoz.multilayer.commons.support.AppLogger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeMap;

/**
 * @author Tommy
 *
 */
public class AppUtility {
	
	public static HashMap<String, String> loadPreferences(File prefFile, String[] tags) throws IOException {
		String readed, tag, value;
		BufferedReader reader;
		HashMap<String, String> map = new HashMap<String, String>();
		if(prefFile.exists()){
			reader = new BufferedReader(new FileReader(prefFile));
			while(reader.ready()){
				readed = reader.readLine();
				if(readed.length() > 0) {
					if(readed.contains("=") && readed.split("=").length == 2){
						tag = readed.split("=")[0];
						value = readed.split("=")[1];
						if(tags != null && tags.length > 0){
							for(String current : tags){
								if(current.toUpperCase().equals(tag.toUpperCase())){
									map.put(tag.trim(), value.trim());
									break;
								}
							}
						} else map.put(tag.trim(), value.trim());
					}
				}
			}
			reader.close();
		} else {
			AppLogger.logInfo(AppUtility.class, "Unexisting preference file: " + prefFile.getAbsolutePath());
		}
		return map;
	}
	
	public static String formatMillis(long dateMillis){
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formatter.format(new Date(dateMillis));
	}
	
	public static Date convertStringToDate(String dateString){
		DateFormat formatter;
		try {
			formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			return formatter.parse(dateString);
		} catch (ParseException ex) {
			AppLogger.logException(AppUtility.class, ex, "Unable to parse date: '" + dateString + "'");
		}
		return null;
	}

	public static boolean isNumber(String value) {
		try {
			Double.parseDouble(value);
			return true;
		} catch(Exception ex){
			return false;
		}
	}
	
	public static double calcAvg(Collection<Double> values) {
		return calcAvg(values.toArray(new Double[values.size()]));
	}
	
	public static Double calcAvg(LinkedList<Integer> values){
		double mean = 0;
		for(Integer d : values){
			mean = mean + d;
		}
		return mean / values.size();
	}
	
	public static Double calcAvg(Double[] values){
		double mean = 0;
		for(Double d : values){
			mean = mean + d;
		}
		return mean / values.length;
	}
	
	public static Double calcStd(Double[] values, Double mean){
		double std = 0;
		for(Double d : values){
			std = std + Math.pow(d-mean, 2);
		}
		return std / values.length;
	}
	
	public static double calcStd(Collection<Double> values, double mean) {
		return calcStd(values.toArray(new Double[values.size()]), mean);
	}
	
	public static Double calcStd(LinkedList<Integer> values, Double mean){
		double std = 0;
		for(Integer d : values){
			std = std + Math.pow(d-mean, 2);
		}
		return std / values.size();
	}
	
	public static double getSecondsBetween(Date current, Date ref){
		if(current.after(ref))
			return (current.getTime() - ref.getTime())/1000;
		else if(current.compareTo(ref) == 0)
			return 0.0;
		else return Double.MAX_VALUE;
	}
	
	public static TreeMap<Double, Double> convertMapTimestamps(Date firstTimestamp, TreeMap<Date, Double> toConvert){
		TreeMap<Double, Double> convertedMap = new TreeMap<Double, Double>();
		if(toConvert.size() > 0) {
			for(Date key : toConvert.keySet()){
				convertedMap.put(AppUtility.getSecondsBetween(key, firstTimestamp), toConvert.get(key));
			}
		}
		return convertedMap;
	}

	public static TreeMap<Double, Double> convertMapSnapshots(TreeMap<Date, Double> resultMap) {
		return convertMapTimestamps(resultMap.firstKey(), resultMap);
	}

	
	
}
