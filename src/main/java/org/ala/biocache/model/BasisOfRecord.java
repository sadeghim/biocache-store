/* *************************************************************************
 *  Copyright (C) 2010 Atlas of Living Australia
 *  All Rights Reserved.
 * 
 *  The contents of this file are subject to the Mozilla Public
 *  License Version 1.1 (the "License"); you may not use this file
 *  except in compliance with the License. You may obtain a copy of
 *  the License at http://www.mozilla.org/MPL/
 * 
 *  Software distributed under the License is distributed on an "AS
 *  IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 *  implied. See the License for the specific language governing
 *  rights and limitations under the License.
 ***************************************************************************/

package org.ala.biocache.model;

import java.util.Date;

/**
 * Java bean of Basis of Record
 *
 * @author "Tommy Wang <Tommy.wang@csiro.au>"
 */
public class BasisOfRecord {
    protected String description;
    protected Long id;
    protected Long version;
    
    /**
	 * Convienience
	 */
	public BasisOfRecord(long id, Long version, String description) {
		this.id = id;
		this.version = version;
		this.description = description;
	}
	
	/**
	 * Default
	 */
	public BasisOfRecord() {		
	}
    
	public Long getVersion() {
		return version;
	}
	
	public void setVersion(Long version) {
		this.version = version;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public Long getId() {
		return id;
	}
	
	public void setId(Long id) {
		this.id = id;
	}
}
