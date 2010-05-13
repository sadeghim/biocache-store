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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.ala.biocache.model.FacetResultDTO;
import org.ala.biocache.model.FieldResultDTO;
import org.ala.biocache.model.OccurrenceDTO;
import org.ala.biocache.model.SearchResultDTO;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.FacetParams;
import org.apache.solr.core.CoreContainer;
import org.springframework.stereotype.Component;

/**
 * SOLR implementation of SearchDao. Uses embedded SOLR server (can be a memory hog)
 *
 * @see org.ala.biocache.dao.SearchDao
 * @author "Nick dos Remedios <Nick.dosRemedios@csiro.au>"
 */
@Component
public class SearchDaoImpl implements SearchDao {
    /** log4 j logger */
    private static final Logger logger = Logger.getLogger(SearchDaoImpl.class);
    /** SOLR home directory */
    private static final String SOLR_HOME = "/data/solr/biocache";
    /** SOLR server instance */
    private EmbeddedSolrServer server;
    private boolean $debug = true;

    /**
     * Initialise the SOLR server instance
     */
    protected void initSolrServer() {
        if (this.server == null & SOLR_HOME != null) {
            try {
                System.setProperty("solr.solr.home", SOLR_HOME);
                CoreContainer.Initializer initializer = new CoreContainer.Initializer();
                CoreContainer coreContainer = initializer.initialize();
                server = new EmbeddedSolrServer(coreContainer, "");
            } catch (Exception ex) {
                logger.error("Error initialising embedded SOLR server: "+ex.getMessage(), ex);
            }
        }
    }

    /**
     * @see org.ala.biocache.dao.SearchDao#findByFulltextQuery(java.lang.String, java.lang.String,
     *         java.lang.Integer, java.lang.Integer, java.lang.String, java.lang.String)
     */
    @Override
    public SearchResultDTO findByFulltextQuery(String query, String[] filterQuery, Integer startIndex,
            Integer pageSize, String sortField, String sortDirection) throws Exception {
        SearchResultDTO searchResults = new SearchResultDTO();

        try {
            // set the query
            StringBuilder queryString = new StringBuilder();
            if (query.contains(":") && !query.startsWith("urn")) {
                // search with a field name specified (other than an LSID guid)
                String[] bits = StringUtils.split(query, ":");
                queryString.append(ClientUtils.escapeQueryChars(bits[0]));
                queryString.append(":");
                queryString.append(ClientUtils.escapeQueryChars(bits[1]));
            } else {
                // regular search
                queryString.append(ClientUtils.escapeQueryChars(query));
            }
            
            SolrQuery solrQuery = initSolrQuery(); // general search settings
            solrQuery.setQuery(queryString.toString());

            QueryResponse qr = runSolrQuery(solrQuery, filterQuery, pageSize, startIndex, sortField, sortDirection);
            searchResults = processSolrResponse(qr, solrQuery);


            logger.info("search query: "+queryString.toString());
        } catch (SolrServerException ex) {
            logger.error("Problem communicating with SOLR server. " + ex.getMessage(), ex);
            searchResults.setStatus("ERROR"); // TODO also set a message field on this bean with the error message(?)
        }

        return searchResults;
    }

    /**
     * @see org.ala.biocache.dao.SearchDao#getById(java.lang.String) 
     */
    @Override
    public OccurrenceDTO getById(String id) throws Exception {
        OccurrenceDTO oc = null;
        String query = "id:"+ClientUtils.escapeQueryChars(id);
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQueryType("standard");
        solrQuery.setQuery(query);
        QueryResponse qr = runSolrQuery(solrQuery, null, 1, 0, "score", "asc");
        SearchResultDTO searchResults = processSolrResponse(qr, solrQuery);
        List<OccurrenceDTO> ocs = searchResults.getOccurrences();
        
        if (!ocs.isEmpty() && ocs.size() == 1) {
            oc = ocs.get(0);
        } else if (!ocs.isEmpty()) {
            logger.warn("Get by id returned more than ONE result: "+ocs.size()+" for id: "+id);
        }

        return oc;
    }

    /**
     *
     *
     * @param solrQuery
     * @param filterQuery
     * @param pageSize
     * @param startIndex
     * @param sortField
     * @param sortDirection
     * @return
     * @throws SolrServerException
     */
    private QueryResponse runSolrQuery(SolrQuery solrQuery, String filterQuery[], Integer pageSize,
            Integer startIndex, String sortField, String sortDirection) throws SolrServerException {

        if (filterQuery != null) {
            for (String fq : filterQuery) {
                // pull apart fq. E.g. Rank:species and then sanitize the string parts
                // so that special characters are escaped apporpriately
                if (fq.isEmpty()) continue;
                String[] parts = fq.split(":", 2); // separate query field from query text
                logger.debug("fq split into: "+parts.length+" parts: "+parts[0]+" & "+parts[1]);
                String prefix = null;
                String suffix = null;
                // don't escape range queries
                if (parts[1].contains(" TO ")) {
                    prefix = parts[0];
                    suffix = parts[1];
                } else {
                    prefix = ClientUtils.escapeQueryChars(parts[0]);
                    suffix = ClientUtils.escapeQueryChars(parts[1]);
                }

                solrQuery.addFilterQuery(prefix + ":" + suffix); // solrQuery.addFacetQuery(facetQuery)
                logger.debug("adding filter query: " + prefix + ":" + suffix);
            }
        }

        solrQuery.setRows(pageSize);
        solrQuery.setStart(startIndex);
        solrQuery.setSortField(sortField, ORDER.valueOf(sortDirection));
        // do the Solr search
        if (server == null) {
            this.initSolrServer();
        }

        return server.query(solrQuery); // can throw exception
    }

    /**
     *
     *
     * @param qr
     * @param solrQuery
     * @return
     */
    private SearchResultDTO processSolrResponse(QueryResponse qr, SolrQuery solrQuery) {
        SearchResultDTO searchResult = new SearchResultDTO();
        SolrDocumentList sdl = qr.getResults();
        List<FacetField> facets = qr.getFacetFields();
        List<FacetField> facetDates = qr.getFacetDates();
        if (facetDates != null) {
            logger.debug("Facet dates size: "+facetDates.size());
            facets.addAll(facetDates);
        }
        //Map<String, Map<String, List<String>>> highlights = qr.getHighlighting();
        List<OccurrenceDTO> results = qr.getBeans(OccurrenceDTO.class);
        List<FacetResultDTO> facetResults = new ArrayList<FacetResultDTO>();
        searchResult.setTotalRecords(sdl.getNumFound());
        searchResult.setStartIndex(sdl.getStart());
        searchResult.setPageSize(solrQuery.getRows()); //pageSize
        searchResult.setStatus("OK");
        String[] solrSort = StringUtils.split(solrQuery.getSortField(), " "); // e.g. "taxon_name asc"
        logger.debug("sortField post-split: "+StringUtils.join(solrSort, "|"));
        searchResult.setSort(solrSort[0]); // sortField
        searchResult.setDir(solrSort[1]); // sortDirection
        searchResult.setQuery(solrQuery.getQuery());
        searchResult.setOccurrences(results);
        // populate SOLR facet results
        if (facets != null) {
            for (FacetField facet : facets) {
                List<FacetField.Count> facetEntries = facet.getValues();
                if ((facetEntries != null) && (facetEntries.size() > 0)) {
                    ArrayList<FieldResultDTO> r = new ArrayList<FieldResultDTO>();
                    for (FacetField.Count fcount : facetEntries) {
                        String msg = fcount.getName() + ": " + fcount.getCount();
                        //logger.trace(fcount.getName() + ": " + fcount.getCount());
                        r.add(new FieldResultDTO(fcount.getName(), fcount.getCount()));
                    }
                    FacetResultDTO fr = new FacetResultDTO(facet.getName(), r);
                    facetResults.add(fr);
                }
            }
        }
        searchResult.setFacetResults(facetResults);
        // The query result is stored in its original format so that all the information
        // returned is available later on if needed
        searchResult.setQr(qr);
        return searchResult;
    }
   
    /**
     * Helper method to create SolrQuery object and add facet settings
     *
     * @return solrQuery the SolrQuery
     */
    protected SolrQuery initSolrQuery() {
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQueryType("standard");
        // Facets
        solrQuery.setFacet(true);
        solrQuery.addFacetField("basis_of_record");
        solrQuery.addFacetField("type_status");
        solrQuery.addFacetField("data_resource");
        solrQuery.addFacetField("state");
        solrQuery.addFacetField("biogeographic_region");
        solrQuery.addFacetField("rank");
        solrQuery.addFacetField("kingdom");
        solrQuery.addFacetField("family");
        // Date Facet Params
        // facet.date=occurrence_date&facet.date.start=1900-01-01T12:00:00Z&facet.date.end=2010-01-01T12:00:00Z&facet.date.gap=%2B1YEAR
        solrQuery.add("facet.date","occurrence_date");
        solrQuery.add("facet.date.start", "1850-01-01T12:00:00Z"); // facet date range starts from 1850
        solrQuery.add("facet.date.end", "NOW/DAY"); // facet date range ends for current date (gap period)
        solrQuery.add("facet.date.gap", "+10YEAR"); // gap interval of 10 years
        solrQuery.add("facet.date.other", "before"); // include counts before the facet start date ("before" label)
        solrQuery.add("facet.date.include", "lower"); // counts will be included for dates on the starting date but not ending date
        //solrQuery.add("facet.date.other", "after");

        solrQuery.setFacetMinCount(1);
        solrQuery.setFacetLimit(30);
        solrQuery.setRows(10);
        solrQuery.setStart(0);

//        add highlights
//        solrQuery.setHighlight(true);
//        solrQuery.setHighlightFragsize(40);
//        solrQuery.setHighlightSnippets(1);
//        solrQuery.setHighlightSimplePre("<b>");
//        solrQuery.setHighlightSimplePost("</b>");
//        solrQuery.addHighlightField("commonName");

        return solrQuery;
    }
}
