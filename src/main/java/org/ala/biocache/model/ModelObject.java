/**
 * 
 */
package org.ala.biocache.model;

import java.io.Serializable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The base class for all modelled objects allowing for standard data 
 * management and tracking.
 * 
 * @author tim
 */
public class ModelObject implements Serializable {
	/**
	 * Generated
	 */
	private static final long serialVersionUID = -420233408889452195L;

	/**
	 * Logger
	 */
	Log logger = LogFactory.getLog(ModelObject.class);
	
	/**
	 * Unique identifer, defaults to un saved
	 */
	protected long id = -1;

	/**
	 * @return Returns the id.
	 */
	public long getId() {
		return id;
	}

	/**
	 * @param id The id to set.
	 */
	public void setId(long id) {
		this.id = id;
	}
}
