/**
 * 
 */
package ippoz.multilayer.detector.service;

/**
 * @author Tommy
 *
 */
public class StatPair {
	
	private double avg;
	private double std;
	
	public StatPair(String avg, String std) {
		if(avg != null)
			this.avg = Double.parseDouble(avg);
		else this.avg = 0;
		if(std != null)
			this.std = Double.parseDouble(std);
		else this.std = this.avg / 2;
	}
	
	public StatPair(double avg, double std) {
		this.avg = avg;
		this.std = std;
	}
	
	public double getAvg() {
		return avg;
	}
	
	public double getStd() {
		return std;
	}

}
