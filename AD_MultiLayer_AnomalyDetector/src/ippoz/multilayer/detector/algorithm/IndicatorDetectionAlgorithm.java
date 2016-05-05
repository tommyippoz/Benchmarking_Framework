/**
 * 
 */
package ippoz.multilayer.detector.algorithm;

import ippoz.multilayer.detector.configuration.AlgorithmConfiguration;
import ippoz.multilayer.detector.data.Indicator;
import ippoz.multilayer.detector.data.LayerType;

/**
 * @author Tommy
 *
 */
public abstract class IndicatorDetectionAlgorithm extends DetectionAlgorithm {
	
	protected Indicator indicator; 
	protected String categoryTag;

	public IndicatorDetectionAlgorithm(Indicator indicator, String categoryTag, AlgorithmConfiguration conf) {
		super(conf);
		this.categoryTag = categoryTag;
		this.indicator = indicator;
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "@" + indicator.getName();
	}

	public LayerType getIndicatorLayer() {
		return indicator.getLayer();
	}
	
	@Override
	public String getDataType() {
		return categoryTag;
	}

	@Override
	public Indicator getIndicator() {
		return indicator;
	}

	public abstract String getIndicatorFullName();
	
	

}
