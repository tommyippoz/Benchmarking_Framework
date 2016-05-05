/**
 * 
 */
package ippoz.multilayer.detector.data;


/**
 * @author Tommy
 *
 */
public class Indicator {
	
	private String indicatorName;
	private LayerType indicatorLayer;
	private Class<?> indicatorType;
	
	public Indicator(String indicatorName, LayerType indicatorLayer, Class<?> indicatorType) {
		this.indicatorName = indicatorName;
		this.indicatorLayer = indicatorLayer;
		this.indicatorType = indicatorType;
	}

	public Object getValue(String rawValue){
		return indicatorType.cast(rawValue);
	}
	
	public String getName(){
		return indicatorName;
	}
	
	public LayerType getLayer(){
		return indicatorLayer;
	}

}
