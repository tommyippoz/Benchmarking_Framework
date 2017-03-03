package ippoz.multilayer.detector.commons.datafetcher.database;

import ippoz.multilayer.detector.commons.data.IndicatorData;
import ippoz.multilayer.detector.commons.data.Observation;
import ippoz.multilayer.detector.commons.datacategory.DataCategory;
import ippoz.multilayer.detector.commons.failure.InjectedElement;
import ippoz.multilayer.detector.commons.indicator.Indicator;
import ippoz.multilayer.detector.commons.layer.LayerType;
import ippoz.multilayer.detector.commons.service.IndicatorStat;
import ippoz.multilayer.detector.commons.service.ServiceCall;
import ippoz.multilayer.detector.commons.service.ServiceStat;
import ippoz.multilayer.detector.commons.service.StatPair;
import ippoz.multilayer.detector.commons.support.AppLogger;
import ippoz.multilayer.detector.commons.support.AppUtility;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * The Class DatabaseManager.
 * Instantiates a MYSQL Database manager
 *
 * @author Tommy
 */
public class DatabaseManager {

    /**
     * The database connector.
     */
    private DatabaseConnector connector;

    /**
     * The runID.
     */
    private String runId;

    /**
     * The map of the layers.
     */
    private HashMap<String, LayerType> layers;

    /**
     * Instantiates a new database manager.
     *
     * @param dbName   the database name
     * @param username the database username
     * @param password the database password
     * @param runId    the runID
     */
    public DatabaseManager(String dbName, String username, String password, String runId) {
        try {
            this.runId = runId;
            connector = new DatabaseConnector(dbName, username, password, false);
            loadSystemLayers();
        } catch (Exception ex) {
            AppLogger.logInfo(getClass(), "Need to start MySQL Server...");
        }
    }

    /**
     * Load system layers.
     */
    private void loadSystemLayers() {
        layers = new HashMap<>();
        for (HashMap<String, String> ptMap : connector.executeCustomQuery(null,
                "select * from probe_type")) {
            layers.put(ptMap.get("probe_type_id"), LayerType.valueOf(ptMap.get("pt_description")));
        }
    }

    /**
     * Flushes database manager.
     *
     * @throws SQLException the SQL exception
     */
    public void flush() throws SQLException {
        connector.closeConnection();
        connector = null;
    }

    /**
     * Gets the observations for the specific runID.
     *
     * @return the run observations
     */
    public LinkedList<Observation> getRunObservations() {
        Observation obs;
        LinkedList<Observation> obsList = new LinkedList<>();
        HashMap<DataCategory, String> indData;
        for (HashMap<String, String> obsMap : connector.executeCustomQuery(null,
                "select observation_id, ob_time from observation where run_id = " + runId)) {
            obs = new Observation(obsMap.get("ob_time"));
            for (HashMap<String, String> indObs : connector.executeCustomQuery(null,
                    "select indicator_observation_id, probe_type_id, in_tag " +
                            "from indicator natural join indicator_observation where observation_id = "
                            + obsMap.get("observation_id"))) {
                indData = new HashMap<>();
                for (HashMap<String, String> indValues : connector.executeCustomQuery(null,
                        "select vc_description, ioc_value from indicator_observation_category natural join "
                                + "value_category where indicator_observation_id = "
                                + indObs.get("indicator_observation_id"))) {
                    indData.put(DataCategory.valueOf(indValues.get("vc_description").toUpperCase()),
                            indValues.get("ioc_value"));
                }
                obs.addIndicator(new Indicator(indObs.get("in_tag"), layers.get(indObs.get("probe_type_id")),
                        String.class), new IndicatorData(indData));
            }
            obsList.add(obs);
        }
        return obsList;
    }

    /**
     * Gets the service calls for the specific runID.
     *
     * @return the service calls
     */
    public LinkedList<ServiceCall> getServiceCalls() {
        LinkedList<ServiceCall> callList = new LinkedList<>();
        for (HashMap<String, String> callMap : connector.executeCustomQuery(null, "select se_name, " +
                "min(start_time) as st_time, max(end_time) as en_time, response " +
                "from service_method_invocation natural join service_method natural join service where run_id = "
                + runId + " group by se_name order by st_time")) {
            callList.add(new ServiceCall(callMap.get("se_name"), callMap.get("st_time"), callMap.get("en_time"),
                    callMap.get("response")));
        }
        return callList;
    }

    /**
     * Gets the service stats for the specific runID.
     *
     * @return the service stats
     */
    public HashMap<String, ServiceStat> getServiceStats() {
        ServiceStat current;
        HashMap<String, ServiceStat> ssList = new HashMap<>();
        for (HashMap<String, String> ssInfo : connector.executeCustomQuery(null,
                "select * from service_stat natural join service")) {
            current = new ServiceStat(ssInfo.get("se_name"), new StatPair(ssInfo.get("serv_dur_avg"),
                    ssInfo.get("serv_dur_std")), new StatPair(ssInfo.get("serv_obs_avg"), ssInfo.get("serv_obs_std")));
            for (HashMap<String, String> isInfo : connector.executeCustomQuery(null,
                    "select * from indicator natural join service_indicator_stat natural join service_stat " +
                            "natural join service where se_name = '" + ssInfo.get("se_name") + "'")) {
                current.addIndicatorStat(new IndicatorStat(isInfo.get("in_tag"),
                        new StatPair(isInfo.get("si_avg_first"), isInfo.get("si_std_first")),
                        new StatPair(isInfo.get("si_avg_last"), isInfo.get("si_std_last")),
                        new StatPair(isInfo.get("si_all_avg"), isInfo.get("si_all_std"))));
            }
            ssList.put(ssInfo.get("se_name"), current);
        }
        return ssList;
    }

    /**
     * Gets the injections for the specific runID.
     *
     * @return the injections
     */
    public LinkedList<InjectedElement> getInjections() {
        LinkedList<InjectedElement> injList = new LinkedList<>();
        for (HashMap<String, String> injInfo : connector.executeCustomQuery(null,
                "select * from failure natural join failure_type where run_id = " + runId
                        + " order by fa_time")) {
            Date fa_time = AppUtility.convertStringToDate(injInfo.get("fa_time"));
            String fa_description = injInfo.get("fa_description");
            String fa_duration1 = injInfo.get("fa_duration");
            // The default time duration for fa is set to 2 seconds if it's not found
            int fa_duration = (fa_duration1 != null) ? Integer.parseInt(fa_duration1) : 2;
            injList.add(new InjectedElement(fa_time, fa_description, fa_duration));
        }
        return injList;
    }

    /**
     * Gets the runID.
     *
     * @return the runID
     */
    public String getRunID() {
        return runId;
    }

    /**
     * Gets the performance timings for the specific runID.
     *
     * @return the performance timings
     */
    public HashMap<String, HashMap<LayerType, LinkedList<Integer>>> getPerformanceTimings() {
        String perfType;
        HashMap<String, HashMap<LayerType, LinkedList<Integer>>> timings = new HashMap<>();
        for (HashMap<String, String> perfIndexes : connector.executeCustomQuery(null,
                "select * from performance_type")) {
            perfType = perfIndexes.get("pet_description");
            timings.put(perfType, new HashMap<>());
            for (HashMap<String, String> timing : connector.executeCustomQuery(null,
                    "select * from performance where run_id = " + runId + " and performance_type_id = "
                            + perfIndexes.get("performance_type_id"))) {
                timings.get(perfType).computeIfAbsent(layers.get(timing.get("probe_type_id")), k -> new LinkedList<>());
                timings.get(perfType).get(layers.get(timing.get("probe_type_id")))
                        .add(Integer.parseInt(timing.get("perf_time")));
            }
        }
        return timings;
    }

}
