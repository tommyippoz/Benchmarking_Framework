package ippoz.multilayer.detector.commons.support;

import java.io.*;
import java.net.Socket;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author Tommy
 *
 */
public class AppUtility {

	public static boolean isWindows() {
		return System.getProperty("os.name").toUpperCase().contains("WIN");
	}

	public static boolean isUNIX() {
		return System.getProperty("os.name").toUpperCase().contains("UNIX");
	}

    public static Process runScript(String path, String args, boolean setOnFolder, boolean viewOutput) throws
            IOException{
        Process p;
        BufferedReader reader;
        String script = buildScript(path);
        if(setOnFolder) {
            p = Runtime.getRuntime().exec(script + " " + args, null, new File((new File(path))
                    .getAbsolutePath().replaceAll((new File(path)).getName(), "")));
        } else {
            p = Runtime.getRuntime().exec(script + " " + args);
        }
        if(viewOutput){
            reader = new BufferedReader(new InputStreamReader(
                    p.getInputStream()));
            while (reader.ready()) {
                System.out.println(reader.readLine());
            }
            reader.close();
        }
        //AppLogger.logInfo(Probe.class, "Executed \"" + script + "\"");
        return p;
    }

    private static String buildScript(String path){
        String script = path;
        if(path.endsWith(".jar")) {
            script = "java -jar " + path;
        }
        return script;
    }
	
	public static HashMap<String, String> loadPreferences(File prefFile, String[] tags) throws IOException {
		String read, tag, value;
		BufferedReader reader;
		HashMap<String, String> map = new HashMap<>();
		if(prefFile.exists()){
			reader = new BufferedReader(new FileReader(prefFile));
			while(reader.ready()){
				read = reader.readLine();
				if(read.length() > 0) {
					if(read.contains("=") && read.split("=").length == 2){
						tag = read.split("=")[0];
						value = read.split("=")[1];
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
	
	public static boolean isServerUp(int port) {
	    return isServerUp("127.0.0.1", port);
	}
	
	public static boolean isServerUp(String address, int port) {
	    boolean isUp = false;
	    try {
	        Socket socket = new Socket(address, port);
	        isUp = true;
	        socket.close();
	    }
	    catch (IOException e) {}
	    return isUp;
	}
	
	public static String formatMillis(long dateMillis){
		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return formatter.format(new Date(dateMillis));
	}

	public static Date getDateFromString(String dateString) throws ParseException {
	    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    try {
            return formatter.parse(dateString);
        } catch (ParseException e) {
            AppLogger.logException(AppUtility.class, e,
                    "Unable to parse date '" + dateString + "'");
	        throw new ParseException("Unable to parse date", e.getErrorOffset());
        }
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
	
	public static double readMillis(){
		return (double)(1.0*System.nanoTime()/1000000.0);
	}
	
	public static Double calcAvg(Collection<? extends Number> values){
		double mean = 0;
		if(values != null && values.size() > 0) {
			for(Number d : values){
				if(d instanceof Long)
					mean = mean + d.longValue();
				else mean = mean + d.doubleValue();
			}
			return mean / values.size();
		} else return 0.0;
	}
	
	public static Double calcAvg(Double[] values){
		int count = 0;
		double mean = 0;
		for(Double d : values){
			if(d != null){
				mean = mean + d;
				count++;
			}
		}
		return mean / count;
	}

	public static Double calcStd(Double[] values, Double mean){
		int count = 0;
		double std = 0;
		for(Double d : values){
			if(d != null){
				std = std + Math.pow(d-mean, 2);
				count++;
			}
		}
		return std / count;
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
		TreeMap<Double, Double> convertedMap = new TreeMap<>();
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

	public static Double calcMedian(Double[] values) {
        Arrays.sort(values);
        return values[values.length/2];
	}

	public static Double calcMode(Double[] values) {
	    int freq = 0, modeFreq = 0;
	    double mode = 0;
        Arrays.sort(values);
        for(int i=0;i<values.length;i++){
            if(i > 0){
                if(Objects.equals(values[i], values[i - 1]))
                    freq++;
                else {
                    if(freq >= modeFreq){
                        mode = values[i-1];
                        modeFreq = freq;
                        freq = 1;
                    }
                }
            } else {
                freq++;
            }
        }
        return mode;
    }
}
