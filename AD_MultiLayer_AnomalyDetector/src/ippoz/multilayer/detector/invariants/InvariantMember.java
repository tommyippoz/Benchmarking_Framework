/**
 * 
 */
package ippoz.multilayer.detector.invariants;

import ippoz.multilayer.detector.commons.data.Snapshot;

/**
 * @author Tommy
 *
 */
public abstract class InvariantMember {
	
	private Class<?> memberType;
	private String memberName;
		
	public InvariantMember(Class<?> memberType, String memberName) {
		this.memberType = memberType;
		this.memberName = memberName;
	}
	
	public String getMemberName(){
		return memberName;
	}

	public Object getValue(Snapshot snapshot){
		return memberType.cast(getStringValue(snapshot));
	}
	
	public Object getValueFromRaw(String rawData){
		return memberType.cast(rawData);
	}
	
	public abstract String getStringValue(Snapshot snapshot);
	
	public Double getDoubleValue(Snapshot snapshot){
		return Double.parseDouble(getStringValue(snapshot));
	}
	
	public Long getLongValue(Snapshot snapshot){
		return Long.parseLong(getStringValue(snapshot));
	}
	
	public abstract String toString();

}
