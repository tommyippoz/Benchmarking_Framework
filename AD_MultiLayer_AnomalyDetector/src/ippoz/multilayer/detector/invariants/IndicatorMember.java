/**
 * 
 */
package ippoz.multilayer.detector.invariants;

import ippoz.multilayer.commons.datacategory.DataCategory;
import ippoz.multilayer.commons.indicator.Indicator;
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

	@Override
	public String getStringValue(Snapshot snapshot) {
		return snapshot.getObservation().getValue(indicator.getName(), categoryTag);
	}

	@Override
	public String toString() {
		return getMemberName();
	}

}
