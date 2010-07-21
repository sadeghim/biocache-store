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

package org.ala.biocache.dao;

import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.ServletOutputStream;

import org.ala.biocache.model.DataProviderCountDTO;
import org.ala.biocache.model.FacetResultDTO;
import org.ala.biocache.model.FieldResultDTO;
import org.ala.biocache.model.OccurrenceDTO;
import org.ala.biocache.model.OccurrencePoint;
import org.ala.biocache.model.PointType;
import org.ala.biocache.model.SearchResultDTO;
import org.ala.biocache.model.TaxaCountDTO;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.core.CoreContainer;
import org.springframework.stereotype.Component;

import au.com.bytecode.opencsv.CSVWriter;

import com.ibm.icu.text.SimpleDateFormat;

/**
 * SOLR implementation of SearchDao. Uses embedded SOLR server (can be a memory hog)
 *
 * @see org.ala.biocache.dao.SearchDao
 * @author "Nick dos Remedios <Nick.dosRemedios@csiro.au>"
 */
@Component("searchDao")
public class SearchDaoImpl implements SearchDao {

    /** log4 j logger */
    private static final Logger logger = Logger.getLogger(SearchDaoImpl.class);
    /** SOLR home directory - injected by Spring from properties file */
    protected String solrHome = "/data/solr/biocache";
    /** SOLR server instance */
    protected EmbeddedSolrServer server;
    /** Limit search results - for performance reasons */
    protected static final Integer MAX_DOWNLOAD_SIZE = 100000;
    protected static final String POINT = "point-0.1";
    protected static final String KINGDOM = "kingdom";
    protected static final String KINGDOM_LSID = "kingdom_lsid";
    protected static final String SPECIES = "species";
    protected static final String SPECIES_LSID = "species_lsid";
    protected static final String NAMES_AND_LSID = "names_and_lsid";
    /**
     * Initialise the SOLR server instance
     */
    protected void initSolrServer() {
        if (this.server == null & solrHome != null) {
            try {
                System.setProperty("solr.solr.home", solrHome);
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
            String queryString = formatSearchQuery(query);
            
            SolrQuery solrQuery = initSolrQuery(); // general search settings
            solrQuery.setQuery(queryString);

            QueryResponse qr = runSolrQuery(solrQuery, filterQuery, pageSize, startIndex, sortField, sortDirection);
            searchResults = processSolrResponse(qr, solrQuery);

            logger.info("search query: "+queryString);
        } catch (SolrServerException ex) {
            logger.error("Problem communicating with SOLR server. " + ex.getMessage(), ex);
            searchResults.setStatus("ERROR"); // TODO also set a message field on this bean with the error message(?)
        }

        return searchResults;
    }
    
    /**
     * @see org.ala.biocache.dao.SearchDao#writeSpeciesCountByCircleToStream(java.lang.Float, java.lang.Float, java.lang.Integer, java.lang.String, java.util.List, javax.servlet.ServletOutputStream)
     */
    public int writeSpeciesCountByCircleToStream(Float latitude, Float longitude,
            Integer radius, String rank, List<String> higherTaxa, ServletOutputStream out) throws Exception
    {

        //get the species counts:
        logger.debug("Writing CSV file for species count by circle");
        List<TaxaCountDTO> species = findAllSpeciesByCircleAreaAndHigherTaxa(latitude, longitude, radius, rank, higherTaxa, null, 0, -1, "count", "asc");
        logger.debug("There are " + species.size() + "records being downloaded");
        CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(out), '\t', '"');
        csvWriter.writeNext(new String[]{
            		"GUID",
                        "Kingdom",
                        "Familiy",
            		"Scientific Name",
            		"CommonName",
                        "Count",
        });
        int count = 0;
        for (TaxaCountDTO item : species) {

            String[] record = new String[]{
                item.getGuid(),
                item.getKingdom(),
                item.getFamily(),
                item.getName(),
                item.getCommonName(),
                item.getCount().toString(),
            };


            csvWriter.writeNext(record);
            csvWriter.flush();
            count++;
        }
        return count;
    }

    /**
     * @see org.ala.biocache.dao.SearchDao#writeResultsToStream(java.lang.String, java.lang.String[], javax.servlet.ServletOutputStream, int)
     */
    public int writeResultsToStream(String query, String[] filterQuery, ServletOutputStream out, int i) throws Exception {

        int resultsCount = 0;
        try {
            String queryString = formatSearchQuery(query);
            logger.info("search query: "+queryString);
            SolrQuery solrQuery = new SolrQuery();
            solrQuery.setQueryType("standard");
            solrQuery.setRows(MAX_DOWNLOAD_SIZE);
            solrQuery.setQuery(queryString);

            int startIndex = 0;
            int pageSize = 1000;
            
            QueryResponse qr = runSolrQuery(solrQuery, filterQuery, pageSize, startIndex, "score", "asc");
            CSVWriter csvWriter = new CSVWriter(new OutputStreamWriter(out), '\t', '"');
            
            csvWriter.writeNext(new String[]{
            		"Record id",
            		"Concept lsid",
            		"Original taxon name",
            		"Recognised taxon name",
            		"Taxon rank",
                        "Common name",
            		"Family",
            		"latitude",
            		"longitude",
            		"locality",
            		"bio region",
            		"Basis of record",
            		"State/Territory",
            		"Collection code",
            		"Institution code",
            		"Collector",
            		"Catalogue number",
            		"Data provider",
            		"Data resource",
            		"Identifier name",
            });
            
            while(qr.getResults().size()>0 && resultsCount<=MAX_DOWNLOAD_SIZE){
            	logger.debug("Start index: "+startIndex);
	            List<OccurrenceDTO> results = qr.getBeans(OccurrenceDTO.class);
	            for(OccurrenceDTO result : results){
	            	resultsCount++;
	            	
	            	String latitude = null, longitude = null;
	            	
	            	if(result.getLatitude()!=null){
	            		latitude = result.getLatitude().toString();
	            	}
	            	if(result.getLatitude()!=null){
	            		longitude = result.getLongitude().toString();
	            	}
	            	
	            	String[] record = new String[]{
            			result.getId(),
            			result.getTaxonConceptLsid(),
            			result.getRawTaxonName(),
            			result.getTaxonName(),
            			result.getRank(),
            			result.getCommonName(),
            			result.getFamily(),
            			latitude,
            			longitude,
            			result.getPlace(),
            			result.getBiogeographicRegion(),
            			result.getBasisOfRecord(),
            			result.getState(),
            			result.getCollectionCode(),
            			result.getInstitutionCode(),
            			result.getCollector(),
            			result.getCatalogueNumber(),
            			result.getDataProvider(),
            			result.getDataResource(),
            			result.getIdentifierName(),
	            	};
	            	csvWriter.writeNext(record);
	            	csvWriter.flush();
	            }
	            startIndex +=pageSize;
	            qr = runSolrQuery(solrQuery, filterQuery, pageSize, startIndex, "score", "asc");
            }
            
        } catch (SolrServerException ex) {
            logger.error("Problem communicating with SOLR server. " + ex.getMessage(), ex);
            //searchResults.setStatus("ERROR"); // TODO also set a message field on this bean with the error message(?)
        }
        return resultsCount;
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
     * @see org.ala.biocache.dao.SearchDao#getFacetPoints(java.lang.String, java.lang.String[], PointType pointType)
     */
    @Override
    public List<OccurrencePoint> getFacetPoints(String query, String[] filterQuery, PointType pointType) throws Exception {
        List<OccurrencePoint> points = new ArrayList<OccurrencePoint>(); // new OccurrencePoint(PointType.POINT);
        String queryString = formatSearchQuery(query);
        logger.info("search query: "+queryString);
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQueryType("standard");
        solrQuery.setQuery(queryString);
        solrQuery.setRows(0);
        solrQuery.setFacet(true);
        solrQuery.addFacetField(pointType.getLabel());
        solrQuery.setFacetMinCount(1);
        solrQuery.setFacetLimit(MAX_DOWNLOAD_SIZE);  // unlimited = -1

        QueryResponse qr = runSolrQuery(solrQuery, filterQuery, 1, 0, "score", "asc");
        List<FacetField> facets = qr.getFacetFields();

        if (facets != null) {
            for (FacetField facet : facets) {
                List<FacetField.Count> facetEntries = facet.getValues();
                if (facet.getName().contains(pointType.getLabel()) && (facetEntries != null) && (facetEntries.size() > 0)) {

                    for (FacetField.Count fcount : facetEntries) {
                        OccurrencePoint point = new OccurrencePoint(pointType);
                        point.setCount(fcount.getCount());
                        String[] pointsDelimited = StringUtils.split(fcount.getName(),',');
                        List<Float> coords = new ArrayList<Float>();

                        for (String coord : pointsDelimited) {
                            try {
                                Float decimalCoord = Float.parseFloat(coord);
                                coords.add(decimalCoord);
                            } catch (NumberFormatException numberFormatException) {
                                logger.warn("Error parsing Float for Lat/Long: "+numberFormatException.getMessage(), numberFormatException);
                            }
                        }

                        if (!coords.isEmpty()) {
                            Collections.reverse(coords); // must be long, lat order
                            point.setCoordinates(coords);
                            points.add(point);
                        }
                    }
                }
            }
        }

        return points;
    }

    /*
    public List<Term> query(String q, int limit) {
        List<Term> items = null;
        initSolrServer();

        // escape special characters
        SolrQuery query = new SolrQuery();
        query.addTermsField("spell");
        query.setTerms(true);
        query.setTermsLimit(limit);
        query.setTermsLower(q);
        query.setTermsPrefix(q);
        query.setQueryType("/terms");

        try {
            QueryResponse qr = server.query(query);
            TermsResponse resp = qr.getTermsResponse();
            items = resp.getTerms("spell");
        } catch (SolrServerException e) {
            items = null;
        }

        return items;
    }
    */

    /**
     * http://ala-biocache1.vm.csiro.au:8080/solr/select?q=*:*&rows=0&facet=true&facet.field=data_provider_id&facet.field=data_provider&facet.sort=data_provider_id
     * 
	 * @see org.ala.biocache.dao.SearchDao#getDataProviderCounts()
	 */
	@Override
	public List<DataProviderCountDTO> getDataProviderCounts() throws Exception {
        
        List<DataProviderCountDTO> dpDTOs = new ArrayList<DataProviderCountDTO>(); // new OccurrencePoint(PointType.POINT);
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQueryType("standard");
        solrQuery.setQuery("*:*");
        solrQuery.setRows(0);
        solrQuery.setFacet(true);
        solrQuery.addFacetField("data_provider_id");
        solrQuery.addFacetField("data_provider");
        solrQuery.setFacetMinCount(1);
        QueryResponse qr = runSolrQuery(solrQuery, null, 1, 0, "data_provider", "asc");
        List<FacetField> facets = qr.getFacetFields();

        if (facets != null && facets.size()==2) {
        	
        	FacetField dataProviderIdFacet = facets.get(0);
        	FacetField dataProviderNameFacet = facets.get(1);
        	
        	List<FacetField.Count> dpIdEntries = dataProviderIdFacet.getValues();
        	List<FacetField.Count> dpNameEntries = dataProviderNameFacet.getValues();
        	
            for (int i=0; i<dpIdEntries.size(); i++) {
            	
            	FacetField.Count dpIdEntry = dpIdEntries.get(i);
            	FacetField.Count dpNameEntry = dpNameEntries.get(i);
            	
            	String dataProviderId = dpIdEntry.getName();
            	String dataProviderName = dpNameEntry.getName();
            	long count = dpIdEntry.getCount();
            	
            	DataProviderCountDTO dto = new DataProviderCountDTO(dataProviderId, dataProviderName, count);
            	dpDTOs.add(dto);
            }
        }
        logger.info("Find data providers = "+dpDTOs.size());
        return dpDTOs;
	}

	/**
     * @see org.ala.biocache.dao.SearchDao#findRecordsForLocation(Float, Float, Integer)
     */
    @Override
    public List<OccurrencePoint> findRecordsForLocation(List<String> taxa, String rank, Float latitude, Float longitude, Integer radius, PointType pointType) throws Exception {
        List<OccurrencePoint> points = new ArrayList<OccurrencePoint>(); // new OccurrencePoint(PointType.POINT);
        String queryString = buildSpatialQueryString("*:*", latitude, longitude, radius);
        //String queryString = formatSearchQuery(query);
        logger.info("location search query: "+queryString+"; pointType: "+pointType.getLabel());
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQueryType("standard");
        solrQuery.setQuery(queryString);

        ArrayList<String> filterQueries = new ArrayList<String>();
        for (String taxon : taxa) {
            filterQueries.add(rank + ":" + taxon);
        }
        
        solrQuery.setFilterQueries("(" + StringUtils.join(filterQueries, " OR ") + ")");
        logger.info("filterQueries: "+solrQuery.getFilterQueries()[0]);
        
        solrQuery.setRows(0);
        solrQuery.setFacet(true);
        solrQuery.addFacetField(pointType.getLabel());
        solrQuery.setFacetMinCount(1);
        solrQuery.setFacetLimit(MAX_DOWNLOAD_SIZE);  // unlimited = -1

        QueryResponse qr = runSolrQuery(solrQuery, null, 1, 0, "score", "asc");
        List<FacetField> facets = qr.getFacetFields();

        if (facets != null) {
            for (FacetField facet : facets) {
                List<FacetField.Count> facetEntries = facet.getValues();
                if (facet.getName().contains(pointType.getLabel()) && (facetEntries != null) && (facetEntries.size() > 0)) {

                    for (FacetField.Count fcount : facetEntries) {
                        OccurrencePoint point = new OccurrencePoint(pointType);
                        point.setCount(fcount.getCount());
                        String[] pointsDelimited = StringUtils.split(fcount.getName(),',');
                        List<Float> coords = new ArrayList<Float>();

                        for (String coord : pointsDelimited) {
                            try {
                                Float decimalCoord = Float.parseFloat(coord);
                                coords.add(decimalCoord);
                            } catch (NumberFormatException numberFormatException) {
                                logger.warn("Error parsing Float for Lat/Long: "+numberFormatException.getMessage(), numberFormatException);
                            }
                        }

                        if (!coords.isEmpty()) {
                            Collections.reverse(coords); // must be long, lat order
                            point.setCoordinates(coords);
                            points.add(point);
                        }
                    }
                }
            }
        }
        logger.info("findRecordsForLocation: number of points = "+points.size());

        return points;
    }

    /**
     * @see org.ala.biocache.dao.SearchDao#findAllSpeciesByCircleAreaAndHigherTaxa(Float, Float,
     *     Integer, String, String, String, Integer, Integer, String, String)
     */
    @Override
    public List<TaxaCountDTO> findAllSpeciesByCircleAreaAndHigherTaxon(Float latitude, Float longitude,
            Integer radius, String rank, String higherTaxon, String filterQuery, Integer startIndex,
            Integer pageSize, String sortField, String sortDirection) throws Exception {

        String queryString = buildSpatialQueryString("*:*", latitude, longitude, radius);
        List<String> filterQueries = Arrays.asList(rank + ":" + higherTaxon);
        List<String> facetFields = new ArrayList<String>();
        //facetFields.add(SPECIES);
        //facetFields.add(SPECIES_LSID);
        facetFields.add(NAMES_AND_LSID);
        List<TaxaCountDTO> speciesWithCounts = getSpeciesCounts(queryString, filterQueries, facetFields, pageSize, sortField, sortDirection);

        return speciesWithCounts;
    }

    /**
     * @see org.ala.biocache.dao.SearchDao#findAllSpeciesByCircleAreaAndHigherTaxa(Float, Float,
     *     Integer, String, String, String, Integer, Integer, String, String)
     */
    @Override
    public List<TaxaCountDTO> findAllSpeciesByCircleAreaAndHigherTaxa(Float latitude, Float longitude,
            Integer radius, String rank, List<String> higherTaxa, String filterQuery, Integer startIndex,
            Integer pageSize, String sortField, String sortDirection) throws Exception {

        ArrayList<String> filterQueries = new ArrayList<String>();
        
        for (String higherTaxon : higherTaxa) {
            filterQueries.add(rank + ":" + higherTaxon);
        }
        
        String queryString = buildSpatialQueryString("*:*", latitude, longitude, radius);
        List<String> facetFields = new ArrayList<String>();
        facetFields.add(NAMES_AND_LSID);
        //facetFields.add(SPECIES);
        //facetFields.add(SPECIES_LSID);
        //facetFields.add(COMMON_NAME);
        List<TaxaCountDTO> speciesWithCounts = getSpeciesCounts(queryString, filterQueries, facetFields, pageSize, sortField, sortDirection);

        return speciesWithCounts;
    }

    /**
     * @see org.ala.biocache.dao.SearchDao#findAllKingdomsByCircleArea(Float, Float, Integer, String,
     *       Integer, Integer, String, String)
     */
    public List<TaxaCountDTO> findAllKingdomsByCircleArea(Float latitude, Float longitude,
            Integer radius, String filterQuery, Integer startIndex,
            Integer pageSize, String sortField, String sortDirection) throws Exception {

        String queryString =  buildSpatialQueryString("*:*", latitude, longitude, radius);
        List<String> facetFields = new ArrayList<String>();
        facetFields.add(KINGDOM);
        facetFields.add(KINGDOM_LSID);
        List<TaxaCountDTO> speciesWithCounts = getSpeciesCounts(queryString, new ArrayList<String>(), facetFields, pageSize, sortField, sortDirection);

        return speciesWithCounts;
    }
    
    /**
     * @see org.ala.biocache.dao.SearchDao#findRecordByDecadeFor(java.lang.String)
     */
	@Override
	public List<FieldResultDTO> findRecordByDecadeFor(String query) throws Exception {
        List<FieldResultDTO> fDTOs = new ArrayList<FieldResultDTO>(); // new OccurrencePoint(PointType.POINT);
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQueryType("standard");
        solrQuery.add("facet.date","occurrence_date");
        solrQuery.add("facet.date.start", "1850-01-01T12:00:00Z"); // facet date range starts from 1850
        solrQuery.add("facet.date.end", "NOW/DAY"); // facet date range ends for current date (gap period)
        solrQuery.add("facet.date.gap", "+10YEAR"); // gap interval of 10 years
        solrQuery.setQuery(query);
//        solrQuery.setQuery(query);
        solrQuery.setRows(0);
        solrQuery.setFacet(true);
        QueryResponse qr = runSolrQuery(solrQuery, null, 1, 0, "score", "asc");

        //get date fields
        List<FacetField> facetDateFields = qr.getFacetDates();
        FacetField ff = facetDateFields.get(0);
        
        System.out.println(ff.getName());
        boolean addCounts = false;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
        
        for(Count count : ff.getValues()){
        	//only start adding counts when we hit a decade with some results.
        	if(addCounts || count.getCount()>0){
        		addCounts = true;
        		Date date = DateUtils.parseDate(count.getName(), new String[]{"yyyy-MM-dd'T'HH:mm:ss'Z'"});
        		String year = sdf.format(date);
        		FieldResultDTO f = new FieldResultDTO(year, count.getCount());
        		fDTOs.add(f);
        		System.out.println(f.getLabel()+" "+f.getCount() );
        	}
        }
        return fDTOs;
	}

    /**
     * Perform SOLR query - takes a SolrQuery and search params
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
     * Process the {@see org.​apache.​solr.​client.​solrj.​response.QueryResponse} from a SOLR search and return
     * a {@link org.ala.biocache.model.SearchResultDTO}
     *
     * @param qr
     * @param solrQuery
     * @return
     */
    private SearchResultDTO processSolrResponse(QueryResponse qr, SolrQuery solrQuery) {
        SearchResultDTO searchResult = new SearchResultDTO();
        SolrDocumentList sdl = qr.getResults();
        // Iterator it = qr.getResults().iterator() // Use for download 
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
//                        String msg = fcount.getName() + ": " + fcount.getCount();
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
     * Build the query string for a spatial query (using Spatial-Solr plugin syntax)
     *
     * @param fullTextQuery
     * @param latitude
     * @param longitude
     * @param radius
     * @return
     */
    protected String buildSpatialQueryString(String fullTextQuery, Float latitude, Float longitude, Integer radius) {
        String queryString = "{!spatial lat=" + latitude.toString() + " long=" + longitude.toString() +
                " radius=" + radius.toString() + " unit=km calc=plane threadCount=2}" + fullTextQuery;
        return queryString;
    }

    /**
     * Format the search input query for a full-text search
     *
     * @param query
     * @return
     */
    protected String formatSearchQuery(String query) {
        // set the query
        StringBuilder queryString = new StringBuilder();
        if (query.equals("*:*") || query.contains(" AND ") || query.contains(" OR ") || query.startsWith("(")) {
            queryString.append(query);
        } else if (query.contains(":") && !query.startsWith("urn")) {
            // search with a field name specified (other than an LSID guid)
            String[] bits = StringUtils.split(query, ":", 2);
            queryString.append(ClientUtils.escapeQueryChars(bits[0]));
            queryString.append(":");
            queryString.append(ClientUtils.escapeQueryChars(bits[1]));
        } else {
            // regular search
            queryString.append(ClientUtils.escapeQueryChars(query));
        }
        return queryString.toString();
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

    /**
     * Get a distinct list of species and their counts using a facet search
     *
     * @param queryString
     * @param pageSize
     * @param sortField
     * @param sortDirection
     * @return
     * @throws SolrServerException
     */
    protected List<TaxaCountDTO> getSpeciesCounts(String queryString, List<String> filterQueries, List<String> facetFields, Integer pageSize,
            String sortField, String sortDirection) throws SolrServerException {
        //LinkedHashMap<String, Long> speciesWithCounts = new LinkedHashMap<String, Long>();
        List<TaxaCountDTO> speciesCounts = new ArrayList<TaxaCountDTO>();
        SolrQuery solrQuery = new SolrQuery();
        solrQuery.setQueryType("standard");
        solrQuery.setQuery(queryString);
//        for (String fq : filterQueries) {
//            solrQuery.addFilterQuery(fq);
//        }
        solrQuery.addFilterQuery("(" + StringUtils.join(filterQueries, " OR ") + ")");
        solrQuery.setRows(0);
        solrQuery.setFacet(true);
        solrQuery.setFacetSort(sortField);
        for (String facet : facetFields) {
            solrQuery.addFacetField(facet);
        }
        solrQuery.setFacetMinCount(1);
        solrQuery.setFacetLimit(pageSize); // unlimited = -1
        QueryResponse qr = runSolrQuery(solrQuery, null, 1, 0, "score", sortDirection);
        logger.info("SOLR query: " + solrQuery.getQuery() + "; total hits: " + qr.getResults().getNumFound());
        List<FacetField> facets = qr.getFacetFields();
        logger.debug("Facets: " +facets.size()+"; facet #1: "+qr.getFacetFields().get(0).getName());
        java.util.regex.Pattern p = java.util.regex.Pattern.compile("\\|");
        if (facets != null) {            
            for (FacetField facet : facets) {
                List<FacetField.Count> facetEntries = facet.getValues();
                if ((facetEntries != null) && (facetEntries.size() > 0)) {
                    for (int i = 0; i < facetEntries.size(); i++) {
                        FacetField.Count fcount = facetEntries.get(i);
                        //speciesCounts.add(i, new TaxaCountDTO(fcount.getName(), fcount.getCount()));
                        TaxaCountDTO tcDTO = null;
                        if(fcount.getFacetField().getName().equals(NAMES_AND_LSID)){
                            String[] values =p.split(fcount.getName());
                            tcDTO = new TaxaCountDTO(values[0], fcount.getCount());
                            if(values.length>=5){
                                tcDTO.setGuid(StringUtils.trimToNull(values[1]));
                                tcDTO.setCommonName(values[2]);
                                tcDTO.setKingdom(values[3]);
                                tcDTO.setFamily(values[4]);
                            }
                            speciesCounts.add(i, tcDTO);
                        }
                        else{
                            //leave the original code for findAllKingdomsByCircleArea method
                        try {
                            tcDTO = speciesCounts.get(i);
                            tcDTO.setGuid(StringUtils.trimToNull(fcount.getName()));
                            speciesCounts.set(i, tcDTO);
                        } catch (Exception e) {
                            tcDTO = new TaxaCountDTO(fcount.getName(), fcount.getCount());
                            speciesCounts.add(i, tcDTO);
                        }
                        }
                    }
                }
            }
        }

        return speciesCounts;
    }


    public String getSolrHome() {
        return solrHome;
    }

    public void setSolrHome(String solrHome) {
        this.solrHome = solrHome;
    }
}
