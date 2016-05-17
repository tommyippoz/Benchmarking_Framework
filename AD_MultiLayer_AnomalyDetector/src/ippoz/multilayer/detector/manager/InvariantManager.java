/**
 * 
 */
package ippoz.multilayer.detector.manager;

import ippoz.multilayer.commons.datacategory.DataCategory;
import ippoz.multilayer.commons.indicator.Indicator;
import ippoz.multilayer.detector.commons.data.ExperimentData;
import ippoz.multilayer.detector.configuration.InvariantConfiguration;
import ippoz.multilayer.detector.invariants.IndicatorMember;
import ippoz.multilayer.detector.invariants.Invariant;
import ippoz.multilayer.detector.metric.Metric;
import ippoz.multilayer.detector.reputation.Reputation;
import ippoz.multilayer.detector.trainer.AlgorithmTrainer;

import java.util.LinkedList;

/**
 * @author Tommy
 *
 */
public class InvariantManager {
	
	private final String[] invariantOperandList = {">"};
	
	private LinkedList<Indicator> indList;
	private DataCategory[] dataTypes;
	private LinkedList<ExperimentData> expList;
	private Metric metric;
	private Reputation reputation;
	
	public InvariantManager(LinkedList<Indicator> indList, DataCategory[] dataTypes, LinkedList<ExperimentData> expList, Metric metric, Reputation reputation) {
		this.indList = indList;
		this.dataTypes = dataTypes;
		this.expList = expList;
		this.metric = metric;
		this.reputation = reputation;
	}

	private LinkedList<AlgorithmTrainer> filterInvSyntax(LinkedList<AlgorithmTrainer> allInv) {
		Invariant invariant;
		LinkedList<AlgorithmTrainer> filtered = new LinkedList<AlgorithmTrainer>();
		for(AlgorithmTrainer invTrainer : allInv){
			invariant = ((InvariantConfiguration)(invTrainer.getBestConfiguration())).getInvariant();
			if(!invariant.getFirstMember().getMemberName().equals(invariant.getSecondMember().getMemberName())){	
				filtered.add(invTrainer);
			}
		}
		return filtered;
	}
	
	public LinkedList<AlgorithmTrainer> filterInvType(LinkedList<AlgorithmTrainer> allInv) {
		Invariant invariant;
		LinkedList<AlgorithmTrainer> toRemove = new LinkedList<AlgorithmTrainer>();
		LinkedList<String> foundMembers = new LinkedList<String>();
		for(AlgorithmTrainer invTrainer : allInv){
			invariant = ((InvariantConfiguration)invTrainer.getBestConfiguration()).getInvariant();
			if(invariant.getFirstMember() instanceof IndicatorMember){
				if(!foundMembers.contains(invariant.getFirstMember().toString()))
					foundMembers.add(invariant.getFirstMember().toString());
				else toRemove.add(invTrainer);
			}
		}
		return toRemove;
	}
	
	private LinkedList<AlgorithmTrainer> generateInvariants() {
		LinkedList<AlgorithmTrainer> allInv = new LinkedList<AlgorithmTrainer>();
		for(Indicator firstMember : indList){
			for(DataCategory firstCategory : dataTypes){
				for(Indicator secondMember : indList){
					for(DataCategory secondCategory : dataTypes){
						for(String operand : invariantOperandList){
							allInv.add(new AlgorithmTrainer("INV", null, null, metric, reputation, expList, new InvariantConfiguration(new Invariant(new IndicatorMember(firstMember, firstCategory), new IndicatorMember(secondMember, secondCategory), operand))));
						}
					}
				}
			}
		}
		return allInv;
	}
	
	public LinkedList<AlgorithmTrainer> getAllInvariants(){
		LinkedList<AlgorithmTrainer> allInv = generateInvariants();
		return filterInvSyntax(allInv);
	}
	
}
