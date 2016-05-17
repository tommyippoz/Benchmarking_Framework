/**
 * 
 */
package ippoz.multilayer.detector.invariants;

import ippoz.multilayer.commons.datacategory.DataCategory;
import ippoz.multilayer.commons.indicator.Indicator;
import ippoz.multilayer.commons.layers.LayerType;
import ippoz.multilayer.detector.commons.data.Snapshot;

/**
 * @author Tommy
 *
 */
public class IndicatorMember extends InvariantMember{

	private Indicator indicator;
	private DataCategory categoryTag;
	
	public IndicatorMember(Indicator indicator, DataCategory categoryTag) {
		super(indicator.getIndicatorType(), indicator.getName());
		this.indicator = indicator;
		this.categoryTag = categoryTag;
	}
	
	public IndicatorMember(String indicatorName, DataCategory categoryTag) {
		super(Double.class, indicatorName);
		indicator = new Indicator(indicatorName, LayerType.NO_LAYER, Double.class);
		this.categoryTag = categoryTag;
	}

	@Override
	public String getStringValue(Snapshot snapshot) {
		return snapshot.getObservation().getValue(indicator.getName(), categoryTag);
	}

	@Override
	public String toString() {
		return getMemberName() + " (" + categoryTag + ")";
	}

	@Override
	public boolean equals(Object other) {
		IndicatorMember oMember = (IndicatorMember)other;
		return oMember.getMemberName().equals(getMemberName()) && oMember.getDataCategory().equals(categoryTag);
	}

	private DataCategory getDataCategory() {
		return categoryTag;
	}
	
	

}
