/**
 * 
 */
package ippoz.multilayer.detector.invariants;

import ippoz.multilayer.detector.commons.data.Snapshot;

/**
 * @author Tommy
 *
 */
public class Invariant {
	
	private InvariantMember firstMember;
	private InvariantMember secondMember;
	private String operand;
	
	public Invariant(InvariantMember firstMember, InvariantMember secondMember, String operand) {
		this.firstMember = firstMember;
		this.secondMember = secondMember;
		this.operand = operand;
	}

	public InvariantMember getFirstMember() {
		return firstMember;
	}

	public InvariantMember getSecondMember() {
		return secondMember;
	}
	
	public boolean evaluateInvariant(Snapshot snapshot){
		if(firstMember.getValue(snapshot) instanceof Double || firstMember.getValue(snapshot) instanceof Integer)
			return evaluateOperand(firstMember.getDoubleValue(snapshot), secondMember.getDoubleValue(snapshot));
		else if(firstMember.getValue(snapshot) instanceof Long)
			return evaluateOperand(firstMember.getLongValue(snapshot), secondMember.getLongValue(snapshot));
		else return false;
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
