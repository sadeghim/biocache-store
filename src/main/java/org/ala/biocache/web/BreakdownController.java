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
package org.ala.biocache.web;

import java.util.List;

import javax.inject.Inject;

import org.ala.biocache.dao.SearchDao;
import org.ala.biocache.model.FieldResultDTO;
import org.ala.biocache.model.SearchQuery;
import org.ala.biocache.util.SearchUtils;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * A simple controller for providing breakdowns on top of the biocache.
 *
 * @author Dave Martin (David.Martin@csiro.au)
 */
public class BreakdownController {

    @Inject
    protected SearchDao searchDao;
	
    protected SearchUtils searchUtils = new SearchUtils();
    
    protected String SPECIES_JSON = "breakdown/decade/species";
    protected String COLLECTIONS_JSON = "breakdown/decade/collections";
    
    /**
     * 
     * @param guid
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/breakdown/species/decades/{query}.json", method = RequestMethod.GET)
	public String speciesDecadeBreakdown(@PathVariable("query") String query, Model model) throws Exception {
        SearchQuery searchQuery = new SearchQuery(query, "collection");
        searchUtils.updateTaxonConceptSearchString(searchQuery);
    	List<FieldResultDTO> results = searchDao.findRecordByDecadeFor(searchQuery.getQuery());
    	model.addAttribute("decades", results);
    	return SPECIES_JSON;
    }

    /**
     * 
     * @param guid
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/breakdown/collection/decades/{query}.json", method = RequestMethod.GET)
	public String collectionDecadeBreakdown(@PathVariable("query") String query, Model model) throws Exception {
        SearchQuery searchQuery = new SearchQuery(query, "collection");
        searchUtils.updateCollectionSearchString(searchQuery);
    	List<FieldResultDTO> results = searchDao.findRecordByDecadeFor(searchQuery.getQuery());
    	model.addAttribute("decades", results);
    	return COLLECTIONS_JSON;
    }

	/**
	 * @param searchDao the searchDao to set
	 */
	public void setSearchDao(SearchDao searchDao) {
		this.searchDao = searchDao;
	}
}
