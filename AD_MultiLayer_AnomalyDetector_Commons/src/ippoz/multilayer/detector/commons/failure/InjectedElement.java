/**
 * 
 */
package ippoz.multilayer.detector.commons.failure;

import java.util.Date;

/**
 * The Class InjectedElement.
 * Represents a failure injected a given time.
 *
 * @author Tommy
 */
public class InjectedElement {
	
	/** The failure timestamp. */
	private Date timestamp;
	
	/** The failure description. */
	private String description;

	/**
	 * Instantiates a new injected element.
	 *
	 * @param timestamp the timestamp
	 * @param description the description
	 */
	public InjectedElement(Date timestamp, String description) {
		this.timestamp = timestamp;
		this.description = description;
	}

	/**
	 * Gets the description of the injected element.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Gets the timestamp of the injected element.
	 *
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return timestamp;
	}

}
