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

package org.ala.biocache.dao;

import java.util.List;
import javax.servlet.ServletOutputStream;

import org.ala.biocache.model.OccurrenceDTO;
import org.ala.biocache.model.OccurrencePoint;
import org.ala.biocache.model.PointType;
import org.ala.biocache.model.SearchResultDTO;

/**
 *
 * @author "Nick dos Remedios <Nick.dosRemedios@csiro.au>"
 */
public interface SearchDao {

    /**
     * Find all occurrences for a given (full text) query
     *
     * @param query
     * @param filterQuery
     * @param startIndex
     * @param pageSize
     * @param sortField
     * @param sortDirection
     * @return
     * @throws Exception
     */
    SearchResultDTO findByFulltextQuery(String query, String[] filterQuery, Integer startIndex,
            Integer pageSize, String sortField, String sortDirection) throws Exception;

    /**
     * Retrieve an OccurrenceDTO for a given occurrence id
     *
     * @param id
     * @return
     * @throws Exception
     */
    OccurrenceDTO getById(String id) throws Exception;

    /**
     * Write out the results of this query to the output stream
     * 
     * @param query
     * @param filterQuery
     * @param out
     * @param maxNoOfRecords
     * @return
     * @throws Exception
     */
	int writeResultsToStream(String query, String[] filterQuery, ServletOutputStream out, int maxNoOfRecords) throws Exception;

    /**
     * Retrieve an OccurrencePoint (distinct list of points - lat-long to 4 decimal places) for a given search
     *
     * @param query
     * @param filterQuery
     * @return
     * @throws Exception
     */
    List<OccurrencePoint> getFacetPoints(String query, String[] filterQuery, PointType pointType) throws Exception;

    /**
     * Get a list of occurrence points for a given lat/long and distance (radius)
     *
     * @param latitude
     * @param longitude
     * @param radius
     * @return
     * @throws Exception
     */
    List<OccurrencePoint> findRecordsForLocation(Float latitude, Float longitude, Integer radius, PointType pointType) throws Exception;
}
