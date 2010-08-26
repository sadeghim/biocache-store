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

import org.ala.biocache.model.DataProvider;


/**
 * The Data Provider DAO
 * @author trobertson
 */
public interface DataProviderDAO {
	/**
	 * Creates the record
	 * @param dataProvider To create
	 * @return The id of the created record
	 */
	public long create(DataProvider dataProvider);
	
	/**
	 * Gets the unique dataprovider
	 * @param uuid That identifies the data provider
	 * @return The dataprovider identified byt the uuid
	 */
	public DataProvider getByUID(String uid);
	
	/**
	 * Gets the dataprovider
	 * @param id That identifies the data provider
	 * @return The dataprovider identified by the id
	 */
	public DataProvider getById(long id);	
	
	/**
	 * Gets the dataprovider
	 * @param id That identifies the data provider
	 * @return The dataprovider identified by the id
	 */
	public List<DataProvider> getAll();	
	
	/**
	 * Gets the dataprovider
	 * @param name That identifies the data provider
	 * @return The dataprovider identified by the name
	 */
	public DataProvider getByName(String name);	
	
	/**
	 * Marks the record as deleted
	 * @param id To delete
	 */
	public void delete(long id);
	
	/**
	 * Updates the data provider
	 * @param dataProvider To update.  Should the ID be null, then create is called 
	 * @return The id of the created record or updated
	 */
	public long updateOrCreate(DataProvider dataProvider);
}
