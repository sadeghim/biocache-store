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

import org.ala.biocache.model.DataResource;


/**
 * The Data Resource DAO
 * @author Tommy Wang (Tommy.wang@csiro.au)
 */
public interface DataResourceDAO {
	/**
	 * Creates the resource
	 * @param dataResource To create
	 * @return The id of the created resource
	 */
	public long create(DataResource dataResource);
	
	/**
	 * Gets the dataresource
	 * @param id That identifies the data resource
	 * @return The dataresource identified by the id
	 */
	public DataResource getById(long id);
	
	
	/**
	 * Gets the dataresources
	 * @param id That identifies the data provider
	 * @return The dataresource list identified by the dataprovider id
	 */
	public List<DataResource> getByDataProviderId(long id);
	
	/**
	 * Gets the resource for the name and data provider
	 * @param name Resource name to retreieve
	 * @param dataProviderId That the resource must be attached to
	 * @return The DataResource or null
	 */
	public DataResource getByNameForProvider(String name, long dataProviderId);
	
	/**
	 * Gets the resource for the name, url and data provider
	 * The URL is used since there may be a provider collating 
	 * many Resources and name clashes are likely 
	 * @param name Resource name to retreieve
	 * @param url That the resource should be at
	 * @param dataProviderId That the resource must be attached to
	 * @return The DataResource or null
	 */
	public DataResource getByNameAndUrlForProvider(String name, String url, long dataProviderId);
	
	/**
	 * Gets the resource for the given remote id at url, the url and the data provider
	 * @param remoteId For the RAP
	 * @param url The url 
	 * @param dataProviderId The provider owning the resource
	 * @return The DataResource or null
	 */
	public DataResource getByRemoteIdAtUrlAndUrlForProvider(String remoteId, String url, long dataProviderId);
	
	/**
	 * Updates the data resource
	 * @param dataResource To update.  Should the ID be null, then create is called 
	 * @return The id of the created record or updated
	 */
	public long updateOrCreate(DataResource dataResource);
}
