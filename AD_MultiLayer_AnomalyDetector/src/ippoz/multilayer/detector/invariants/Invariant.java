/**
 * 
 */
package ippoz.multilayer.detector.invariants;

import ippoz.multilayer.commons.datacategory.DataCategory;
import ippoz.multilayer.detector.commons.data.Snapshot;
import ippoz.multilayer.detector.commons.support.AppUtility;

/**
 * @author Tommy
 *
 */
public class Invariant {
	
	private final static String[] opList = {">", "<", ">=", "<=", "=", "!="};
	
	private InvariantMember firstMember;
	private InvariantMember secondMember;
	private String operand;
	
	public Invariant(InvariantMember firstMember, InvariantMember secondMember, String operand) {
		this.firstMember = firstMember;
		this.secondMember = secondMember;
		this.operand = operand;
	}

	public Invariant(String readString) {
		int opIndex = -1;
		for(String op : opList){
			if(readString.contains(op)){
				operand = op;
				opIndex = readString.indexOf(op);
				break;
			}
		}
		firstMember = getMemberFromString(readString.substring(0, opIndex).trim());
		secondMember = getMemberFromString(readString.substring(opIndex+1).trim());
	}
	
	private InvariantMember getMemberFromString(String mString){
		if(AppUtility.isNumber(mString))
			return new ConstantMember(Double.class, mString);
		else {
			return new IndicatorMember(mString.substring(0, mString.indexOf("(")).trim(), DataCategory.valueOf(mString.substring(mString.indexOf("(")+1, mString.indexOf(")"))));
		}
	}

	public InvariantMember getFirstMember() {
		return firstMember;
	}

	public InvariantMember getSecondMember() {
		return secondMember;
	}
	
	public boolean evaluateInvariant(Snapshot snapshot){
		return evaluateOperand(firstMember.getDoubleValue(snapshot), secondMember.getDoubleValue(snapshot));
	}
	
	private boolean evaluateOperand(double val1, double val2){
		switch(operand){
			case ">":
				return val1 > val2;
			case "<":
				return val1 < val2;
			case "=":
			case "==":
				return val1 == val2;
			case ">=":
				return val1 >= val2;
			case "<=":
				return val1 <= val2;
			case "!=":
				return val1 != val2;
		}
		return false;
	}

	@Override
	public String toString() {
		return firstMember.toString() + " " + operand + " " + secondMember.toString();
	}	
	
	

}
