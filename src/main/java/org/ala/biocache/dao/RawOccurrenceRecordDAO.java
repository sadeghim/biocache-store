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

import java.util.Date;
import java.util.List;

import org.ala.biocache.model.RawOccurrenceRecord;

/**
 * The Raw Occurrence Record DAO
 * @author trobertson
 */
public interface RawOccurrenceRecordDAO {
    /**
     * Creates the record
     * @param rawOccurrenceRecord To create
     * @return The id of the created record
     */
    public long create(RawOccurrenceRecord rawOccurrenceRecord);
    
    /**
     * Creates the record if the given record has no ID, otherwise will update
     * @param rawOccurrenceRecord To create or update
     * @return The id of the created record
     */
    public long updateOrCreate(RawOccurrenceRecord rawOccurrenceRecord);
    
    /**
     * A RawOccurrenceRecord is considered unique with the supplied values with a DataResource
     * 
     * Note: This is the method that determines if the harvesting is doing an insert or update
     * 
     * @param dataResourceId That groups the records
     * @param institutionCode For the record
     * @param collectionCode For the record
     * @param catalogueNumber for the record
     * @return The record or null
     */
    public RawOccurrenceRecord getUniqueRecord(long dataResourceId, String institutionCode, String collectionCode, String catalogueNumber, String unitQualifier);
    
    /**
     * Gets the records that have an updated after the given date or an updated of null
     * @param resourceAccessPointId within which to work
     * @param dataResourceId within which to work
     * @param start The date limit to get records touched from
     * @param startAt The lower parameter for the paging
     * @return The records or an empty list
     */
    public List<RawOccurrenceRecord> getCreatedOrModifiedSince(long dataResourceId, Date start, long startAt, int maxResults);
    
    /**
     * Gets the data resource ids for the RAP
     * @param resourceAccessPointId 
     * @return
     */
    public List<Long> getDataResourceIdsFor(long resourceAccessPointId);
    
    /**
     * Gets the unique record for the raw occurrence record id
     * @param id uniquely identifying the record
     * @return The raw occurrence record
     */
    public RawOccurrenceRecord getById(long id);

    /**
     * Delete the record by its id
     *
     * @param id
     */
    public void deleteById(long id);
    
    public long getOccurrenceIdByGuid(String guid);
}