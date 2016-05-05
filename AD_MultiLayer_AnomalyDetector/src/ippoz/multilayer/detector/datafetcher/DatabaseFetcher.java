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
 * The Class DatabaseFetcher.
 * Concrete fetcher that uses a MYSQL database manager.
 *
 * @author Tommy
 */
public class DatabaseFetcher extends DataFetcher {
	
	/** The database manager. */
	private DatabaseManager dbManager;
	
	/**
	 * Instantiates a new database fetcher.
	 *
	 * @param runId the runID
	 * @param username the database username
	 * @param password the database password
	 */
	public DatabaseFetcher(String runId, String username, String password){
		dbManager = new DatabaseManager("experiment", username, password, runId);
	}

	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.datafetcher.DataFetcher#getObservations()
	 */
	@Override
	protected LinkedList<Observation> getObservations() {
		return dbManager.getRunObservations();
	}

	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.datafetcher.DataFetcher#getServiceCalls()
	 */
	@Override
	protected LinkedList<ServiceCall> getServiceCalls() {
		return dbManager.getServiceCalls();
	}

	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.datafetcher.DataFetcher#getServiceStats()
	 */
	@Override
	protected HashMap<String, ServiceStat> getServiceStats() {
		return dbManager.getServiceStats();
	}

	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.datafetcher.DataFetcher#getInjections()
	 */
	@Override
	protected LinkedList<InjectedElement> getInjections() {
		return dbManager.getInjections();
	}

	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.datafetcher.DataFetcher#flush()
	 */
	@Override
	public void flush() {
		try {
			dbManager.flush();
			dbManager = null;
		} catch (SQLException ex) {
			AppLogger.logException(getClass(), ex, "Unable to close SQL Connection");
		}
	}

	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.datafetcher.DataFetcher#getID()
	 */
	@Override
	protected String getID() {
		return dbManager.getRunID();
	}

	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.datafetcher.DataFetcher#getPerformanceTimings()
	 */
	@Override
	protected HashMap<String, HashMap<LayerType, LinkedList<Integer>>> getPerformanceTimings() {
		return dbManager.getPerformanceTimings();
	}

}
