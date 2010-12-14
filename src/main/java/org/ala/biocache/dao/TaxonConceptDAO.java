package org.ala.biocache.dao;
/**************************************************************************
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
import org.ala.biocache.model.TaxonConcept;
/**
 * DAO for accessing taxon concepts, 
 * 
 * @author Dave Martin
 */
public interface TaxonConceptDAO {
	
	/**
	 * Retrieve the taxon concept by its GUID.
	 * @param guid
	 * @return
	 */
    public TaxonConcept getByGuid(final String guid);
    
    public String findLsidByName(String scientificName);
}
