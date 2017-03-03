package ippoz.multilayer.detector.voter;

import ippoz.multilayer.detector.algorithm.DataSeriesDetectionAlgorithm;
import ippoz.multilayer.detector.algorithm.DetectionAlgorithm;
import ippoz.multilayer.detector.commons.algorithm.AlgorithmType;
import ippoz.multilayer.detector.commons.configuration.AlgorithmConfiguration;
import ippoz.multilayer.detector.commons.data.Snapshot;
import ippoz.multilayer.detector.commons.dataseries.DataSeries;
import ippoz.multilayer.detector.commons.layer.LayerType;

/**
 * The Class AlgorithmVoter.
 * Used to score a specific pre-instantiated algorithm.
 *
 * @author Tommy
 */
public class AlgorithmVoter implements Cloneable {

    /**
     * The algorithm.
     */
    private DetectionAlgorithm algorithm;

    /**
     * The metric score.
     */
    private double metricScore;

    /**
     * The reputation score.
     */
    private double reputationScore;

    /**
     * Instantiates a new algorithm voter.
     *
     * @param algorithm             the algorithm
     * @param metricScore     the metric score
     * @param reputationScore the reputation score
     */
    public AlgorithmVoter(DetectionAlgorithm algorithm, double metricScore, double reputationScore) {
        this.algorithm = algorithm;
        this.metricScore = metricScore;
        this.reputationScore = reputationScore;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#clone()
     */
    @Override
    protected AlgorithmVoter clone() throws CloneNotSupportedException {
        if (algorithm instanceof DataSeriesDetectionAlgorithm) {
            return new AlgorithmVoter(DetectionAlgorithm.buildAlgorithm(algorithm.getAlgorithmType(), algorithm.getDataSeries(),
                    algorithm.getConfiguration()), metricScore, reputationScore);
        } else {
            return new AlgorithmVoter(DetectionAlgorithm.buildAlgorithm(algorithm.getAlgorithmType(), null,
                    algorithm.getConfiguration()), metricScore, reputationScore);
        }

    }

    /**
     * Votes the selected snapshot.
     *
     * @param snap the snapshot
     * @return the anomaly voting.
     */
    public double voteSnapshot(Snapshot snap) {
        return algorithm.snapshotAnomalyRate(snap);
    }

    /**
     * Gets the metric score.
     *
     * @return the metric score
     */
    public double getMetricScore() {
        return metricScore;
    }

    /**
     * Gets the reputation score.
     *
     * @return the reputation score
     */
    public double getReputationScore() {
        return reputationScore;
    }

    /**
     * Prints the results.
     *
     * @param outFormat     the output format
     * @param outFolderName the output folder
     * @param expTag        the experiment tag
     */
    public void printResults(String outFormat, String outFolderName, String expTag) {
        algorithm.printResults(outFormat, outFolderName, expTag);
    }

    /**
     * Gets the indicator layer type.
     *
     * @return the layer type
     */
    public LayerType getLayerType() {
        if (algorithm.getDataSeries() != null) {
            return algorithm.getDataSeries().getLayerType();
        } else {
            return LayerType.NO_LAYER;
        }
    }

    /**
     * Gets the algorithm type.
     *
     * @return the algorithm type
     */
    public AlgorithmType getAlgorithmType() {
        return algorithm.getAlgorithmType();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return algorithm.toString();
    }

    public DataSeries getDataSeries() {
        return algorithm.getDataSeries();
    }

    public AlgorithmConfiguration getAlgorithmConfiguration() {
        return algorithm.getConfiguration();
    }

    public boolean usesSeries(DataSeries serie) {
        return algorithm.usesSeries(serie);
    }

}
