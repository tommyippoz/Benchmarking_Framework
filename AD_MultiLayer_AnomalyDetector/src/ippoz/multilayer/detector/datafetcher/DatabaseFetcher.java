/**
 * 
 */
package ippoz.multilayer.detector.datafetcher;

import ippoz.multilayer.detector.data.LayerType;
import ippoz.multilayer.detector.data.Observation;
import ippoz.multilayer.detector.datafetcher.database.DatabaseManager;
import ippoz.multilayer.detector.failure.InjectedElement;
import ippoz.multilayer.detector.service.ServiceCall;
import ippoz.multilayer.detector.service.ServiceStat;
import ippoz.multilayer.detector.support.AppLogger;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * @author Tommy
 *
 */
public class DatabaseFetcher extends DataFetcher {
	
	private DatabaseManager dbManager;
	
	public DatabaseFetcher(String runId, String username, String password){
		dbManager = new DatabaseManager("experiment", username, password, runId);
	}

	@Override
	protected LinkedList<Observation> getObservations() {
		return dbManager.getRunObservations();
	}

	@Override
	protected LinkedList<ServiceCall> getServiceCalls() {
		return dbManager.getServiceCalls();
	}

	@Override
	protected HashMap<String, ServiceStat> getServiceStats() {
		return dbManager.getServiceStats();
	}

	@Override
	protected LinkedList<InjectedElement> getInjections() {
		return dbManager.getInjections();
	}

	@Override
	public void flush() {
		try {
			dbManager.flush();
			dbManager = null;
		} catch (SQLException ex) {
			AppLogger.logException(getClass(), ex, "Unable to close SQL Connection");
		}
	}

	@Override
	protected String getID() {
		return dbManager.getRunID();
	}

	@Override
	protected HashMap<String, HashMap<LayerType, LinkedList<Integer>>> getPerformanceTimings() {
		return dbManager.getPerformanceTimings();
	}

}
