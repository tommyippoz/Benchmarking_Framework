/**
 * 
 */
package ippoz.multilayer.detector.algorithm;

import ippoz.multilayer.commons.datacategory.DataCategory;
import ippoz.multilayer.commons.indicator.Indicator;
import ippoz.multilayer.commons.layers.LayerType;
import ippoz.multilayer.detector.configuration.AlgorithmConfiguration;

// TODO: Auto-generated Javadoc
/**
 * The Class IndicatorDetectionAlgorithm.
 *
 * @author Tommy
 */
public abstract class IndicatorDetectionAlgorithm extends DetectionAlgorithm {
	
	/** The indicator. */
	protected Indicator indicator; 
	
	/** The data category tag. */
	protected DataCategory categoryTag;

	/**
	 * Instantiates a new indicator detection algorithm.
	 *
	 * @param indicator the indicator
	 * @param categoryTag the data category tag
	 * @param conf the configuration
	 */
	public IndicatorDetectionAlgorithm(Indicator indicator, DataCategory categoryTag, AlgorithmConfiguration conf) {
		super(conf);
		this.categoryTag = categoryTag;
		this.indicator = indicator;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getClass().getSimpleName() + "@" + indicator.getName();
	}

	/**
	 * Gets the indicator layer.
	 *
	 * @return the indicator layer
	 */
	public LayerType getIndicatorLayer() {
		return indicator.getLayer();
	}
	
	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.algorithm.DetectionAlgorithm#getDataType()
	 */
	@Override
	public DataCategory getDataType() {
		return categoryTag;
	}

	/* (non-Javadoc)
	 * @see ippoz.multilayer.detector.algorithm.DetectionAlgorithm#getIndicator()
	 */
	@Override
	public Indicator getIndicator() {
		return indicator;
	}	

}
