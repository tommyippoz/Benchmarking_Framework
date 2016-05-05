/**
 * 
 */
package ippoz.multilayer.detector.failure;

import java.util.Date;

/**
 * @author Tommy
 *
 */
public class InjectedElement {
	
	private Date timestamp;
	private String description;

	public InjectedElement(Date timestamp, String description) {
		this.timestamp = timestamp;
		this.description = description;
	}

	public String getDescription() {
		return description;
	}

	public Date getTimestamp() {
		return timestamp;
	}

}
