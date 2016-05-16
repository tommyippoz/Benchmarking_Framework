/**
 * 
 */
package ippoz.multilayer.detector.invariants;

import ippoz.multilayer.detector.commons.data.Snapshot;

/**
 * @author Tommy
 *
 */
public class ConstantMember extends InvariantMember {

	private String baseValue;
	
	public ConstantMember(Class<?> memberType, String baseValue) {
		super(memberType, "Constant");
		this.baseValue = baseValue;
	}

	@Override
	public String getStringValue(Snapshot snapshot) {
		return String.valueOf(getValueFromRaw(baseValue));
	}

	@Override
	public String toString() {
		return baseValue;
	}

}
