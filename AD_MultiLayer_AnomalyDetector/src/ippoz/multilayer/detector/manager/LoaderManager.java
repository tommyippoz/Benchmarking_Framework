/**
 * 
 */
package ippoz.multilayer.detector.manager;

import ippoz.multilayer.detector.data.ExperimentData;
import ippoz.multilayer.detector.datafetcher.DataFetcher;
import ippoz.multilayer.detector.datafetcher.DatabaseFetcher;
import ippoz.multilayer.detector.support.AppLogger;
import ippoz.multilayer.detector.support.ThreadScheduler;

import java.util.LinkedList;

/**
 * @author Tommy
 *
 */
public class LoaderManager extends ThreadScheduler {
	
	private String tag;
	private String dbUsername;
	private String dbPassword;
	private TimingsManager pManager;
	private LinkedList<String> expIDs;
	private LinkedList<ExperimentData> readData;
	
	public LoaderManager(LinkedList<String> expIDs, String tag, TimingsManager pManager, String dbUsername, String dbPassword) {
		super();
		this.tag = tag;
		this.dbUsername = dbUsername;
		this.dbPassword = dbPassword;
		this.pManager = pManager;
		this.expIDs = expIDs;
		readData = new LinkedList<ExperimentData>();
	}
	
	public LinkedList<ExperimentData> fetch(){
		long start = System.currentTimeMillis();
		try {
			start();
			join();
			if(tag.equals("train")){
				pManager.addTiming(TimingsManager.LOAD_TRAIN_TIME, (double)(System.currentTimeMillis() - start));
				pManager.addTiming(TimingsManager.AVG_LOAD_TRAIN_TIME, (double)((System.currentTimeMillis() - start)/threadNumber()));
			} else {
				pManager.addTiming(TimingsManager.LOAD_VALIDATION_TIME, (double)(System.currentTimeMillis() - start));
				pManager.addTiming(TimingsManager.AVG_LOAD_VALIDATION_TIME, (double)((System.currentTimeMillis() - start)/threadNumber()));	
			}
			AppLogger.logInfo(getClass(), "'" + tag + "' data loaded in " + (System.currentTimeMillis() - start) + " ms");
			AppLogger.logInfo(getClass(), "Average per run: " + ((System.currentTimeMillis() - start)/threadNumber()) + " ms");
		} catch (InterruptedException ex) {
			AppLogger.logException(getClass(), ex, "Unable to complete training phase");
		}
		return readData;
	}

	@Override
	protected void initRun() {
		LinkedList<DataFetcher> fetchList = new LinkedList<DataFetcher>();
		for(String runId : expIDs){
			fetchList.add(new DatabaseFetcher(runId, dbUsername, dbPassword));
		}
		setThreadList(fetchList);
	}

	@Override
	protected void threadStart(Thread t, int tIndex) {
		// TODO Auto-generated method stub
	}

	@Override
	protected void threadComplete(Thread t, int tIndex) {
		ExperimentData data = ((DataFetcher)t).getFetchedData();
		if(data.obsNumber() > 5)
			readData.add(data);
	}

}
