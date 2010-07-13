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
import org.ala.biocache.model.TaxaCountDTO;

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
     * Writes the species count in the specified circle to the output stream.
     * @param latitude
     * @param longitude
     * @param radius
     * @param rank
     * @param higherTaxa
     * @param out
     * @return
     * @throws Exception
     */
    int writeSpeciesCountByCircleToStream(Float latitude, Float longitude,
            Integer radius, String rank, List<String> higherTaxa, ServletOutputStream out) throws Exception;


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
    List<OccurrencePoint> findRecordsForLocation(List<String> taxa, String rank,Float latitude, Float longitude, Integer radius, PointType pointType) throws Exception;

    /**
     * Location-based search using circular area around a point and for a given radius (in km)
     *
     * @param latitude
     * @param longitude
     * @param radius
     * @param rank
     * @param higherTaxon
     * @param filterQuery
     * @param startIndex
     * @param pageSize
     * @param sortField
     * @param sortDirection
     * @return
     * @throws Exception
     */
    List<TaxaCountDTO> findAllSpeciesByCircleAreaAndHigherTaxon(Float latitude, Float longitude, Integer radius, String rank, String higherTaxon,
    		String filterQuery, Integer startIndex, Integer pageSize, String sortField, String sortDirection) throws Exception;

    /**
     * Find all species (and counts) for a given location search (lat/long and radius) and a higher taxon (with rank)
     *
     * @param latitude
     * @param longitude
     * @param radius
     * @param rank
     * @param higherTaxa
     * @param filterQuery
     * @param startIndex
     * @param pageSize
     * @param sortField
     * @param sortDirection
     * @return
     * @throws Exception
     */
    List<TaxaCountDTO> findAllSpeciesByCircleAreaAndHigherTaxa(Float latitude, Float longitude, Integer radius, String rank, List<String> higherTaxa,
    		String filterQuery, Integer startIndex, Integer pageSize, String sortField, String sortDirection) throws Exception;

    /**
     * Find all species (and counts) for a given location search (lat/long and radius) and higher taxa (with rank)
     *
     * @param latitude
     * @param longitude
     * @param radius
     * @param filterQuery
     * @param startIndex
     * @param pageSize
     * @param sortField
     * @param sortDirection
     * @return
     * @throws Exception
     */
    List<TaxaCountDTO> findAllKingdomsByCircleArea(Float latitude, Float longitude, Integer radius, String filterQuery,
             Integer startIndex, Integer pageSize, String sortField, String sortDirection) throws Exception;
}
