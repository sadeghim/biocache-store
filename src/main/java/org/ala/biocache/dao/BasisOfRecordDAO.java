/***************************************************************************
 * Copyright (C) 2005 Global Biodiversity Information Facility Secretariat.
 * All Rights Reserved.
 *
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 ***************************************************************************/
package org.ala.biocache.dao;

import java.util.List;

import org.ala.biocache.model.BasisOfRecord;

/**
 * The Data Provider DAO
 * @author Tommy Wang (Tommy.wang@csiro.au)
 */
public interface BasisOfRecordDAO {
	
	/**
	 * Gets the basisOfRecord
	 * @param id That identifies the basis of record
	 * @return The basis of record identified by the id
	 */
	public BasisOfRecord getById(long id);	
	
	/**
	 * Gets all the basisOfRecord
	 * @param null
	 * @return a list of all the basisOfRecord
	 */
	public List<BasisOfRecord> getAll();
	
}
