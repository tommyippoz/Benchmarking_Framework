/**
 * 
 */
package ippoz.multilayer.detector.service;

/**
 * @author Tommy
 *
 */
public class IndicatorStat {
	
	private String indicatorName;
	private StatPair firstObs;
	private StatPair lastObs;
	private StatPair allObs;
	
	public IndicatorStat(String indicatorName, StatPair firstObs, StatPair lastObs, StatPair allObs) {
		this.indicatorName = indicatorName;
		this.firstObs = firstObs;
		this.lastObs = lastObs;
		this.allObs = allObs;
	}
	
	public String getName(){
		return indicatorName;
	}
	
	public StatPair getFirstObs() {
		return firstObs;
	}
	public StatPair getLastObs() {
		return lastObs;
	}
	public StatPair getAllObs() {
		return allObs;
	}
	
}
