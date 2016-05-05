/**
 * 
 */
package ippoz.multilayer.detector.configuration;

/**
 * @author Tommy
 *
 */
public class SPSConfiguration extends AlgorithmConfiguration {
	
	public static final String PDV = "pdv";
	public static final String POV = "pov";
	public static final String PDS = "pds";
	public static final String POS = "pos";
	public static final String M = "m";
	public static final String N = "n";
	public static final String DYN_WEIGHT = "dweight";
	
	@Override
	public String toString() {
		return "SPSConf:{p=" + getItem(PDV) + ",m=" + getItem(M) + ",dw=" + getItem(DYN_WEIGHT) + "}";
	}

	@Override
	public String getFileHeader() {
		return "weight,score,pdv,pov,pds,pos,m,n,dweight";
	}

	@Override
	public String toFileRow(boolean complete) {
		if(complete)
			return getItem(WEIGHT) + ", " + getItem(SCORE) + ", " + getItem(PDV) + "," + getItem(POV) + "," + getItem(PDS) + "," + getItem(POS) + "," + getItem(M) + "," + getItem(N) + "," + getItem(DYN_WEIGHT) + ",";
		else return getItem(PDV) + "," + getItem(POV) + "," + getItem(PDS) + "," + getItem(POS) + "," + getItem(M) + "," + getItem(N) + "," + getItem(DYN_WEIGHT) + ",";
		
	}

}
