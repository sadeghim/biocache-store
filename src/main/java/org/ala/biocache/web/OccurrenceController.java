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

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.ala.biocache.dao.BasisOfRecordDAO;
import org.ala.biocache.dao.DataProviderDAO;
import org.ala.biocache.dao.DataResourceDAO;
import org.ala.biocache.dao.SearchDao;
import org.ala.biocache.model.OccurrenceDTO;
import org.ala.biocache.model.SearchResultDTO;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import atg.taglib.json.util.JSONArray;
import atg.taglib.json.util.JSONObject;

/**
 * Occurrences controller for the BIE biocache site
 *
 * @author "Nick dos Remedios <Nick.dosRemedios@csiro.au>"
 */
@Controller
public class OccurrenceController {

	/** Logger initialisation */
	private final static Logger logger = Logger.getLogger(OccurrenceController.class);

	/** Fulltext search DAO */
	@Inject
	protected SearchDao searchDAO;
	/** Data Resource DAO */
	@Inject
	protected DataResourceDAO dataResourceDAO;
	/** Data Provider DAO */
	@Inject
	protected DataProviderDAO dataProviderDAO;

	/** Name of view for site home page */
	private String HOME = "homePage";
	/** Name of view for list of taxa */
	private final String LIST = "occurrences/list";
	/** Name of view for a single taxon */
	private final String SHOW = "occurrences/show";
	/** Name of view for points GeoJSON service */
	private final String POINTS_GEOJSON = "json/pointsGeoJson";
	/** Name of view for square cells GeoJSON service */
	private final String CELLS_GEOJSON = "json/cellsGeoJson";
	protected String hostUrl = "http://localhost:8888/biocache-webapp";
	protected String bieBaseUrl = "http://alaslvweb2-cbr.vm.csiro.au:8080/bie-webapp";

	/**
	 * Custom handler for the welcome view.
	 * <p>
	 * Note that this handler relies on the RequestToViewNameTranslator to
	 * determine the logical view name based on the request URL: "/welcome.do"
	 * -&gt; "welcome".
	 *
	 * @return viewname to render
	 */
	@RequestMapping("/")
	public String homePageHandler() {
		return HOME;
	}

	/**
	 * Default method for Controller
	 *
	 * @return mav
	 */
	@RequestMapping(value = "/occurrences", method = RequestMethod.GET)
	public ModelAndView listOccurrences() {
		ModelAndView mav = new ModelAndView();
		mav.setViewName(LIST);
		mav.addObject("message", "Results list for search goes here. (TODO)");
		return mav;
	}

	/**
	 * Occurrence search page uses SOLR JSON to display results
	 * 
	 * @param query
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/occurrences/searchByTaxon*", method = RequestMethod.GET)
	public String occurrenceSearchByTaxon(
			@RequestParam(value="q", required=false) String query,
			@RequestParam(value="fq", required=false) String[] filterQuery,
			@RequestParam(value="start", required=false, defaultValue="0") Integer startIndex,
			@RequestParam(value="pageSize", required=false, defaultValue ="20") Integer pageSize,
			@RequestParam(value="sort", required=false, defaultValue="score") String sortField,
			@RequestParam(value="dir", required=false, defaultValue ="asc") String sortDirection,
			@RequestParam(value="rad", required=false, defaultValue="10") Integer radius,
			@RequestParam(value="lat", required=false, defaultValue="-35.27412f") Float latitude,
			@RequestParam(value="lon", required=false, defaultValue="149.11288f") Float longitude,
			Model model)
	throws Exception {

		if (query == null || query.isEmpty()) {
			return LIST;
		}

		//lets retrieve the details of a taxon
		//http://alaslvweb2-cbr.vm.csiro.au:8080/bie-webapp/species/urn:lsid:biodiversity.org.au:apni.taxon:295882

		String jsonObject = getUrlContentAsString(bieBaseUrl+"/species/"+query+".json");
		JSONObject j = new JSONObject(jsonObject);
		JSONObject extendedDTO = j.getJSONObject("extendedTaxonConceptDTO");
		JSONObject taxonConcept = extendedDTO.getJSONObject("taxonConcept");

		logger.debug("Found concept: "+taxonConcept.getString("nameString"));


		String rankString = taxonConcept.getString("rankString");
		String commonName = null;

		JSONArray commonNames = extendedDTO.getJSONArray("commonNames");
		if(!commonNames.isEmpty()){
			commonName = commonNames.getJSONObject(0).getString("nameString");
		}

		String scientificName = taxonConcept.getString("nameString");
		StringBuffer entityQuerySb = new StringBuffer(rankString+ ": " +scientificName);
		if(commonName!=null){
			entityQuerySb.append(" (");
			entityQuerySb.append(commonName);
			entityQuerySb.append(") ");
		}

		//FIXME - should be able to use left/right for non major ranks
		String solrQuery = "taxon_concept_lsid:"+query; //default to just searching on taxon lsid
		if("species".equalsIgnoreCase(taxonConcept.getString("rankString")) ){
			solrQuery = "species_lsid:"+query;
		}
		if("genus".equalsIgnoreCase(taxonConcept.getString("rankString")) ){
			solrQuery = "genus_lsid:"+query;
		}
		if("family".equalsIgnoreCase(taxonConcept.getString("rankString")) ){
			solrQuery = "family_lsid:"+query;
		}
		if("order".equalsIgnoreCase(taxonConcept.getString("rankString")) ){
			solrQuery = "order_lsid:"+query;
		}
		if("class".equalsIgnoreCase(taxonConcept.getString("rankString")) ){
			solrQuery = "class_lsid:"+query;
		}
		if("phylum".equalsIgnoreCase(taxonConcept.getString("rankString")) ){
			solrQuery = "phylum_lsid:"+query;
		}
		if("kingdom".equalsIgnoreCase(taxonConcept.getString("rankString")) ){
			solrQuery = "kingdom_lsid:"+query;
		}

		// if params are set but empty (e.g. foo=&bar=) then provide sensible defaults
		if (filterQuery != null && filterQuery.length == 0) {
			filterQuery = null;
		}
		if (startIndex == null) {
			startIndex = 0;
		}
		if (pageSize == null) {
			pageSize = 20;
		}
		if (sortField.isEmpty()) {
			sortField = "score";
		}
		if (sortDirection.isEmpty()) {
			sortDirection = "asc";
		}

		SearchResultDTO searchResult = new SearchResultDTO();
		String queryJsEscaped = StringEscapeUtils.escapeJavaScript(query);
		model.addAttribute("entityQuery", entityQuerySb.toString());

		model.addAttribute("query", query);
		model.addAttribute("queryJsEscaped", queryJsEscaped);
		model.addAttribute("facetQuery", filterQuery);

		searchResult = searchDAO.findByFulltextQuery(solrQuery, filterQuery, startIndex, pageSize, sortField, sortDirection);

		model.addAttribute("searchResult", searchResult);
		logger.debug("query = "+query);
		Long totalRecords = searchResult.getTotalRecords();
		model.addAttribute("totalRecords", totalRecords);

		if (pageSize > 0) {
			Integer lastPage = (totalRecords.intValue() / pageSize) + 1;
			model.addAttribute("lastPage", lastPage);
		}

		return LIST;
	}

	/**
	 * Occurrence search page uses SOLR JSON to display results
	 * 
	 * @param query
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/occurrences/searchByDataProviderId*", method = RequestMethod.GET)
	public String occurrenceSearchByDataProviderId(
			@RequestParam(value="q", required=false) String query,
			@RequestParam(value="fq", required=false) String[] filterQuery,
			@RequestParam(value="start", required=false, defaultValue="0") Integer startIndex,
			@RequestParam(value="pageSize", required=false, defaultValue ="20") Integer pageSize,
			@RequestParam(value="sort", required=false, defaultValue="score") String sortField,
			@RequestParam(value="dir", required=false, defaultValue ="asc") String sortDirection,
			@RequestParam(value="rad", required=false, defaultValue="10") Integer radius,
			@RequestParam(value="lat", required=false, defaultValue="-35.27412f") Float latitude,
			@RequestParam(value="lon", required=false, defaultValue="149.11288f") Float longitude,
			Model model)
		throws Exception {

		int dataProviderId;
		String dataProviderName;

		try {
			dataProviderId = Integer.valueOf(query);
		} catch (NumberFormatException nfe) {
			return LIST;
		}

		if (dataProviderId != 0) {
			dataProviderName = dataProviderDAO.getById(dataProviderId).getName();

			String solrQuery = "data_provider_id:" + dataProviderId;
			SearchResultDTO searchResult = new SearchResultDTO();
			String queryJsEscaped = StringEscapeUtils.escapeJavaScript(dataProviderName);
			
			model.addAttribute("query", dataProviderName);
			model.addAttribute("queryJsEscaped", queryJsEscaped);
			
			searchResult = searchDAO.findByFulltextQuery(solrQuery, filterQuery, startIndex, pageSize, sortField, sortDirection);

			model.addAttribute("searchResult", searchResult);
			logger.debug("query = "+query);
			Long totalRecords = searchResult.getTotalRecords();
			model.addAttribute("totalRecords", totalRecords);

			if (pageSize > 0) {
				Integer lastPage = (totalRecords.intValue() / pageSize) + 1;
				model.addAttribute("lastPage", lastPage);
			}
		}		

		return LIST;

	}
	
	/**
	 * Occurrence search page uses SOLR JSON to display results
	 * 
	 * @param query
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/occurrences/searchByDataResourceId*", method = RequestMethod.GET)
	public String occurrenceSearchByDataResourceId(
			@RequestParam(value="q", required=false) String query,
			@RequestParam(value="fq", required=false) String[] filterQuery,
			@RequestParam(value="start", required=false, defaultValue="0") Integer startIndex,
			@RequestParam(value="pageSize", required=false, defaultValue ="20") Integer pageSize,
			@RequestParam(value="sort", required=false, defaultValue="score") String sortField,
			@RequestParam(value="dir", required=false, defaultValue ="asc") String sortDirection,
			@RequestParam(value="rad", required=false, defaultValue="10") Integer radius,
			@RequestParam(value="lat", required=false, defaultValue="-35.27412f") Float latitude,
			@RequestParam(value="lon", required=false, defaultValue="149.11288f") Float longitude,
			Model model)
		throws Exception {

		int dataResourceId;
		String dataResourceName;

		try {
			dataResourceId = Integer.valueOf(query);
		} catch (NumberFormatException nfe) {
			return LIST;
		}

		if (dataResourceId != 0) {
			dataResourceName = dataResourceDAO.getById(dataResourceId).getName();

			String solrQuery = "data_resource_id:" + dataResourceId;
			SearchResultDTO searchResult = new SearchResultDTO();
			String queryJsEscaped = StringEscapeUtils.escapeJavaScript(dataResourceName);
			
			model.addAttribute("query", dataResourceName);
			model.addAttribute("queryJsEscaped", queryJsEscaped);
			
			searchResult = searchDAO.findByFulltextQuery(solrQuery, filterQuery, startIndex, pageSize, sortField, sortDirection);

			model.addAttribute("searchResult", searchResult);
			logger.debug("query = "+query);
			Long totalRecords = searchResult.getTotalRecords();
			model.addAttribute("totalRecords", totalRecords);

			if (pageSize > 0) {
				Integer lastPage = (totalRecords.intValue() / pageSize) + 1;
				model.addAttribute("lastPage", lastPage);
			}
		}		

		return LIST;

	}

	/**
	 * Occurrence search for a given collection. Takes zero or more collectionCode and institutionCode
	 * parameters (but at least one must be set).
	 *
	 * @param collectionCode
	 * @param institutionCode
	 * @param query
	 * @param filterQuery
	 * @param startIndex
	 * @param pageSize
	 * @param sortField
	 * @param sortDirection
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/occurrences/searchForCollection*", method = RequestMethod.GET)
	public String occurrenceSearchForCollection(
			@RequestParam(value="coll", required=false) String[] collectionCode,
			@RequestParam(value="inst", required=false) String[] institutionCode,
			@RequestParam(value="q", required=false) String query,
			@RequestParam(value="fq", required=false) String[] filterQuery,
			@RequestParam(value="start", required=false, defaultValue="0") Integer startIndex,
			@RequestParam(value="pageSize", required=false, defaultValue ="20") Integer pageSize,
			@RequestParam(value="sort", required=false, defaultValue="score") String sortField,
			@RequestParam(value="dir", required=false, defaultValue ="asc") String sortDirection,
			Model model)
	throws Exception {

		// no query so exit method
		if (query == null || query.isEmpty()) {
			//return LIST;
		}

		// one of collectionCode or institutionCode must be set
		if ((query == null || query.isEmpty()) && (collectionCode==null || collectionCode.length==0) && (institutionCode==null || institutionCode.length==0)) {
			return LIST;
		}

		// if params are set but empty (e.g. foo=&bar=) then provide sensible defaults
		if (filterQuery != null && filterQuery.length == 0) {
			filterQuery = null;
		}
		if (startIndex == null) {
			startIndex = 0;
		}
		if (pageSize == null) {
			pageSize = 20;
		}
		if (sortField.isEmpty()) {
			sortField = "score";
		}
		if (sortDirection.isEmpty()) {
			sortDirection = "asc";
		}

		StringBuilder solrQuery = new StringBuilder();
		StringBuilder entityQuerySb = new StringBuilder();

		// Build Lucene query for institutions
		if (institutionCode!=null && institutionCode.length > 0) {
			StringBuilder displayString = new StringBuilder("Collection: ");
			List<String> institutions = new ArrayList<String>();
			for (String inst : institutionCode) {
				institutions.add("institution_code:" + inst);
				displayString.append(inst).append(" ");
			}
			solrQuery.append("(");
			solrQuery.append(StringUtils.join(institutions, " OR "));
			solrQuery.append(")");
			entityQuerySb.append(displayString);
		}

		// Build Lucene query for collections
		if (collectionCode!=null && collectionCode.length > 0) {
			if (solrQuery.length() > 0) {
				solrQuery.append(" AND ");
				entityQuerySb.append(" ");
			}
			StringBuilder displayString = new StringBuilder("Institution: ");
			List<String> collections = new ArrayList<String>();
			for (String coll : collectionCode) {
				collections.add("collection_code:" + coll);
				displayString.append(coll).append(" ");
			}
			solrQuery.append("(");
			solrQuery.append(StringUtils.join(collections, " OR "));
			solrQuery.append(")");
			entityQuerySb.append(displayString);
		}

		// FQ requests will have empty coll and inst params but use formated query instead
		if (solrQuery.length() == 0 && !query.isEmpty()) {
			solrQuery.append(query);
		} else if (query == null || query.isEmpty()) {
			query = solrQuery.toString();
		}

		logger.info("solr query: "+solrQuery.toString());

		SearchResultDTO searchResult = new SearchResultDTO();
		String queryJsEscaped = StringEscapeUtils.escapeJavaScript(query);
		model.addAttribute("entityQuery", entityQuerySb.toString());

		model.addAttribute("query", query);
		model.addAttribute("queryJsEscaped", queryJsEscaped);
		model.addAttribute("facetQuery", filterQuery);

		searchResult = searchDAO.findByFulltextQuery(solrQuery.toString(), filterQuery, startIndex, pageSize, sortField, sortDirection);

		model.addAttribute("searchResult", searchResult);
		logger.debug("query = "+query);
		Long totalRecords = searchResult.getTotalRecords();
		model.addAttribute("totalRecords", totalRecords);

		if (pageSize > 0) {
			Integer lastPage = (totalRecords.intValue() / pageSize) + 1;
			model.addAttribute("lastPage", lastPage);
		}

		return LIST;
	}

	/**
	 * Retrieve content as String.
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static String getUrlContentAsString(String url) throws Exception {
		HttpClient httpClient = new HttpClient();
		GetMethod gm = new GetMethod(url);
		gm.setFollowRedirects(true);
		httpClient.executeMethod(gm);
		// String requestCharset = gm.getRequestCharSet();
		String content = gm.getResponseBodyAsString();
		// content = new String(content.getBytes(requestCharset), "UTF-8");
		return content;
	}

	/**
	 * Occurrence search page uses SOLR JSON to display results
	 * 
	 * @param query
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/occurrences/search*", method = RequestMethod.GET)
	public String occurrenceSearch(
			@RequestParam(value="q", required=false) String query,
			@RequestParam(value="fq", required=false) String[] filterQuery,
			@RequestParam(value="start", required=false, defaultValue="0") Integer startIndex,
			@RequestParam(value="pageSize", required=false, defaultValue ="20") Integer pageSize,
			@RequestParam(value="sort", required=false, defaultValue="score") String sortField,
			@RequestParam(value="dir", required=false, defaultValue ="asc") String sortDirection,
			@RequestParam(value="rad", required=false, defaultValue="10") Integer radius,
			@RequestParam(value="lat", required=false, defaultValue="-35.27412f") Float latitude,
			@RequestParam(value="lon", required=false, defaultValue="149.11288f") Float longitude,
			Model model)
	throws Exception {

		if (query == null || query.isEmpty()) {
			return LIST;
		}
		// if params are set but empty (e.g. foo=&bar=) then provide sensible defaults
		if (filterQuery != null && filterQuery.length == 0) {
			filterQuery = null;
		}
		if (startIndex == null) {
			startIndex = 0;
		}
		if (pageSize == null) {
			pageSize = 20;
		}
		if (sortField.isEmpty()) {
			sortField = "score";
		}
		if (sortDirection.isEmpty()) {
			sortDirection = "asc";
		}

		SearchResultDTO searchResult = new SearchResultDTO();
		String queryJsEscaped = StringEscapeUtils.escapeJavaScript(query);
		model.addAttribute("query", query);
		model.addAttribute("queryJsEscaped", queryJsEscaped);
		model.addAttribute("facetQuery", filterQuery);

		searchResult = searchDAO.findByFulltextQuery(query, filterQuery, startIndex, pageSize, sortField, sortDirection);
		model.addAttribute("searchResult", searchResult);
		logger.debug("query = "+query);
		Long totalRecords = searchResult.getTotalRecords();
		model.addAttribute("totalRecords", totalRecords);

		if (pageSize > 0) {
			Integer lastPage = (totalRecords.intValue() / pageSize) + 1;
			model.addAttribute("lastPage", lastPage);
		}

		return LIST;
	}

	/**
	 * Occurrence search page uses SOLR JSON to display results
	 * 
	 * @param query
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/occurrences/download*", method = RequestMethod.GET)
	public String occurrenceDownload(
			@RequestParam(value="q", required=false) String query,
			@RequestParam(value="fq", required=false) String[] filterQuery,
			HttpServletResponse response)
	throws Exception {

		if (query == null || query.isEmpty()) {
			return LIST;
		}
		// if params are set but empty (e.g. foo=&bar=) then provide sensible defaults
		if (filterQuery != null && filterQuery.length == 0) {
			filterQuery = null;
		}

		response.setHeader("Cache-Control", "must-revalidate");
		response.setHeader("Pragma", "must-revalidate");
		response.setHeader("Content-Disposition", "attachment;filename=data");
		response.setContentType("application/vnd.ms-excel");

		ServletOutputStream out = response.getOutputStream();

		searchDAO.writeResultsToStream(query, filterQuery, out, 100000);

		return null;
	}


	/**
	 * Occurrence record page
	 *
	 * @param id
	 * @param model
	 * @return view name
	 * @throws Exception
	 */
	@RequestMapping(value = {"/occurrences/{id}", "/occurrences/{id}.json"}, method = RequestMethod.GET)
	public String showOccurrence(@PathVariable("id") String id, Model model) throws Exception {
		logger.debug("Retrieving occurrence record with guid: "+id+".");
		model.addAttribute("id", id);
		OccurrenceDTO occurrence = searchDAO.getById(id);
		model.addAttribute("occurrence", occurrence);
		model.addAttribute("hostUrl", hostUrl);
		return SHOW;
	}


	/**
	 * @param hostUrl the hostUrl to set
	 */
	public void setHostUrl(String hostUrl) {
		this.hostUrl = hostUrl;
	}
}
