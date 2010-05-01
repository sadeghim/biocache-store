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

package org.ala.biocache.dao.solr;

import java.util.ArrayList;
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
import org.apache.solr.core.CoreContainer;
import org.springframework.stereotype.Component;

/**
 *
 * @author "Nick dos Remedios <Nick.dosRemedios@csiro.au>"
 */
@Component
public class SearchDAOImpl {
    /** log4 j logger */
    private static final Logger logger = Logger.getLogger(SearchDAOImpl.class);
    /** SOLR home directory */
    private static final String SOLR_HOME = "/data/solr/biocache";
    /** SOLR server instance */
    private EmbeddedSolrServer server;

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
    public SearchResultDTO findByScientificName(String query, String filterQuery, Integer startIndex,
            Integer pageSize, String sortField, String sortDirection) throws Exception {
        SearchResultDTO searchResults = new SearchResultDTO();

        try {
            // set the query
            StringBuffer queryString = new StringBuffer();
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
            searchResults = doSolrSearch(queryString.toString(), filterQuery, pageSize, startIndex, sortField, sortDirection);
            logger.info("search query: "+queryString.toString());
        } catch (SolrServerException ex) {
            logger.error("Problem communicating with SOLR server. " + ex.getMessage(), ex);
            searchResults.setStatus("ERROR"); // TODO also set a message field on this bean with the error message(?)
        }

        return searchResults;
    }

    public OccurrenceDTO getById(String id) throws Exception {
        OccurrenceDTO oc = null;
        String query = "id:"+ClientUtils.escapeQueryChars(id);
        SearchResultDTO searchResults = doSolrSearch(query.toString(), null, 1, 0, "score", "asc");
        List<OccurrenceDTO> ocs = searchResults.getOccurrences();
        
        if (!ocs.isEmpty()) {
            oc = ocs.get(0);
        }

        return oc;
    }

    /**
     * Re-usable method for performing SOLR searches - takes query string input
     *
     * @param queryString
     * @param filterQuery
     * @param pageSize
     * @param startIndex
     * @param sortField
     * @param sortDirection
     * @return
     * @throws SolrServerException
     */
    private SearchResultDTO doSolrSearch(String queryString, String filterQuery, Integer pageSize,
          Integer startIndex, String sortField, String sortDirection) throws SolrServerException {

        SolrQuery solrQuery = initSolrQuery(); // general search settings
        solrQuery.setQuery(queryString);

        return doSolrQuery(solrQuery, filterQuery, pageSize, startIndex, sortField, sortDirection);
    }

    /**
     * Re-usable method for performing SOLR searches - takes SolrQuery input
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
    private SearchResultDTO doSolrQuery(SolrQuery solrQuery, String filterQuery, Integer pageSize,
            Integer startIndex, String sortField, String sortDirection) throws SolrServerException {

        SearchResultDTO searchResult = new SearchResultDTO();

        // set the facet query if set
        if (filterQuery != null && !filterQuery.isEmpty()) {
            // pull apart fq. E.g. Rank:species and then sanitize the string parts
            // so that special characters are escaped apporpriately
            String[] parts = filterQuery.split(":");
            String prefix = ClientUtils.escapeQueryChars(parts[0]);
            String suffix = ClientUtils.escapeQueryChars(parts[1]);
            solrQuery.addFilterQuery(prefix + ":" + suffix); // solrQuery.addFacetQuery(facetQuery)
            logger.debug("adding filter query: " + StringUtils.join(solrQuery.getFacetQuery(), ", "));
        }

        solrQuery.setRows(pageSize);
        solrQuery.setStart(startIndex);
        solrQuery.setSortField(sortField, ORDER.valueOf(sortDirection));
        // do the Solr search
        if (server == null) {
            this.initSolrServer();
        }

        QueryResponse qr = server.query(solrQuery); // can throw exception
        SolrDocumentList sdl = qr.getResults();
        List<FacetField> facets = qr.getFacetFields();
        //Map<String, Map<String, List<String>>> highlights = qr.getHighlighting();
        List<OccurrenceDTO> results = qr.getBeans(OccurrenceDTO.class);
        //List<OccurrenceDTO> results = new ArrayList<OccurrenceDTO>();
        List<FacetResultDTO> facetResults = new ArrayList<FacetResultDTO>();
        searchResult.setTotalRecords(sdl.getNumFound());
        searchResult.setStartIndex(sdl.getStart());
        searchResult.setStatus("OK");
        searchResult.setSort(sortField);
        searchResult.setDir(sortDirection);
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
        solrQuery.setFacet(true);
        solrQuery.addFacetField("data_resource");
        solrQuery.addFacetField("kingdom");
        solrQuery.addFacetField("family");
        solrQuery.addFacetField("state");
        solrQuery.addFacetField("biogeographic_region");
        solrQuery.addFacetField("basis_of_record");
        solrQuery.addFacetField("type_status");

        solrQuery.setFacetMinCount(1);
        solrQuery.setRows(10);
        solrQuery.setStart(0);

        //add highlights
//        solrQuery.setHighlight(true);
//        solrQuery.setHighlightFragsize(40);
//        solrQuery.setHighlightSnippets(1);
//        solrQuery.setHighlightSimplePre("<b>");
//        solrQuery.setHighlightSimplePost("</b>");
//        solrQuery.addHighlightField("commonName");

        return solrQuery;
    }
}
