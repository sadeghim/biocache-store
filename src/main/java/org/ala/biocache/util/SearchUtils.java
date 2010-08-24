package org.ala.biocache.util;

import java.util.ArrayList;
import java.util.List;

import org.ala.biocache.dto.OccurrenceDTO;
import org.ala.biocache.dto.SearchQuery;
import org.ala.biocache.web.OccurrenceController;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.math.util.MathUtils;
import org.apache.log4j.Logger;

import atg.taglib.json.util.JSONArray;
import atg.taglib.json.util.JSONObject;
/**
 * A class to provide utility methods used to populate search details.
 * @author Natasha
 */
public class SearchUtils {
    /** Logger initialisation */
	private final static Logger logger = Logger.getLogger(SearchUtils.class);
    
    protected String collectoryBaseUrl = "http://collections.ala.org.au";
    protected String bieBaseUrl = "http://bie.ala.org.au";
    
    /**
     * Returns an array that contains the search string to use for a collection
     * search and display name for the results.
     * @param query
     * @return
     */
    public void updateCollectionSearchString(SearchQuery searchQuery) {
        try{
            String query = searchQuery.getQuery();
        StringBuilder solrQuery = new StringBuilder();
        //query the collectory for the institute and collection codes needed to perform the search
        String jsonObject = OccurrenceController.getUrlContentAsString(collectoryBaseUrl + "/collection/summary/" + query);
        JSONObject j = new JSONObject(jsonObject);
        String instituteName = j.getString("name");
        JSONArray institutionCode = j.getJSONArray("derivedInstCodes");
        JSONArray collectionCode = j.getJSONArray("derivedCollCodes");
        StringBuilder displayString = new StringBuilder("Collection: ");
        displayString.append(instituteName);
        // Build Lucene query for institutions
        if (institutionCode != null && institutionCode.size() > 0) {

            List<String> institutions = new ArrayList<String>();
            for (int i = 0; i < institutionCode.size(); i++) {
                institutions.add("institution_code:" + institutionCode.getString(i));
            }
            solrQuery.append("(");
            solrQuery.append(StringUtils.join(institutions, " OR "));
            solrQuery.append(")");

        }

        // Build Lucene query for collections
        if (collectionCode != null && collectionCode.size() > 0) {
            if (solrQuery.length() > 0) {
                solrQuery.append(" AND ");

            }
            //StringBuilder displayString = new StringBuilder("Institution: ");
            List<String> collections = new ArrayList<String>();
            for (int i = 0; i < collectionCode.size(); i++) {
                //quote the collection code to solve issue with invalid characters (eg Invertebrates - Marine & Other)
                collections.add("collection_code:\"" + collectionCode.getString(i) +"\"");
                //	displayString.append(coll).append(" ");
            }
            solrQuery.append("(");
            solrQuery.append(StringUtils.join(collections, " OR "));
            solrQuery.append(")");

        }
        searchQuery.setQuery(solrQuery.toString());
        searchQuery.setDisplayString(displayString.toString());
        
        }
        catch(Exception e){
        	logger.error("Problem contacting the collectory: "+e.getMessage(), e);
            //TODO work out what we want to do to the search if an exception occurs while
            //contacting the collectory etc
        }
        
    }
    /**
     * Returns the filter query string required to perform a taxon concept serach
     * @param query
     * @return
     */
    public void updateTaxonConceptSearchString(SearchQuery searchQuery){
        try {
            //lets retrieve the details of a taxon
		//http://alaslvweb2-cbr.vm.csiro.au:8080/bie-webapp/species/urn:lsid:biodiversity.org.au:apni.taxon:295882

            String query = searchQuery.getQuery();
            String jsonObject = OccurrenceController.getUrlContentAsString(bieBaseUrl + "/species/" + query + ".json");
            
            JSONObject j = new JSONObject(jsonObject);
            JSONObject extendedDTO = j.getJSONObject("extendedTaxonConceptDTO");
            JSONObject taxonConcept = extendedDTO.getJSONObject("taxonConcept");

            //retrieve the left and right values
            String left = taxonConcept.getString("left");
            String right = taxonConcept.getString("right");

            logger.debug("Querying with left and right: " + left + ", " + right);
            logger.debug("Found concept: " + taxonConcept.getString("nameString"));

            //get rank string
            String rankString = taxonConcept.getString("rankString");
            String commonName = null;

            //get a common name
            JSONArray commonNames = extendedDTO.getJSONArray("commonNames");
            if (!commonNames.isEmpty()) {
                commonName = commonNames.getJSONObject(0).getString("nameString");
            }

            //contruct a name for search purposes
            String scientificName = taxonConcept.getString("nameString");
            StringBuffer entityQuerySb = new StringBuffer(rankString + ": " + scientificName);
            if (commonName != null) {
                entityQuerySb.append(" (");
                entityQuerySb.append(commonName);
                entityQuerySb.append(") ");
            }
            searchQuery.addToFilterQuery("lft:[" + left + " TO " + right + "]");
            
            if (logger.isDebugEnabled()) {
                for (String filter : searchQuery.getFilterQuery()) {
                    logger.debug("Filter: " + filter);
                }
            }
            searchQuery.setQuery("*:*");
            searchQuery.setEntityQuery(entityQuerySb.toString());
        } catch (Exception e) {
            //TODO work out what we want to do to the search if an exception occurs while
            //contacting the bie etc
        }
        
    }

    /**
     * Returns the query to be used when searching for data providers.
     * @param query
     * @return
     */

    public String getDataProviderSearchString(String query) {
        return "data_provider_id:" + query;
    }

    /**
     * Returns the query to be used when searching for data resources.
     * @param query
     * @return
     */
    public String getDataResourceSearchString(String query) {
        return "data_resource_id:" + query;
    }
    /**
     * Returns the query string based on the type of search that needs to be performed.
     * @param query
     * @param type
     * @return [0] is the new query string to apply [1] is additional filter query strings to be applied
     */
    public void updateQueryDetails(SearchQuery searchQuery) {
        logger.debug("Processing " + searchQuery.getQuery() +" using type: " + searchQuery.getType());
        if(searchQuery.getType().equals("collection")){
            updateCollectionSearchString(searchQuery);
        }
        else if(searchQuery.getType().equals("provider")){
            searchQuery.setQuery(getDataProviderSearchString(searchQuery.getQuery()));
        }
        else if(searchQuery.getType().equals("resource")){
            searchQuery.setQuery(getDataResourceSearchString(searchQuery.getQuery()));
        }
        else if(searchQuery.getType().equals("taxon")){
            updateTaxonConceptSearchString(searchQuery);
        }
        //otherwise we can leave the query with its default values ("normal" type)
    }
    /*
CONCAT(CONCAT(ROUND(round(oc.latitude *1)/1,1), ','), ROUND(round(oc.longitude *1)/1,1)),
CONCAT(CONCAT(ROUND(round(oc.latitude *10)/10,1), ','), ROUND(round(oc.longitude *10)/10,1)),
CONCAT(CONCAT(ROUND(round(oc.latitude *100)/100,2), ','), ROUND(round(oc.longitude *100)/100,2)),
CONCAT(CONCAT(ROUND(round(oc.latitude *1000)/1000,3), ','), ROUND(round(oc.longitude *1000)/1000,3)),
CONCAT(CONCAT(ROUND(round(oc.latitude *10000)/10000,4), ','), ROUND(round(oc.longitude *10000)/10000,4)),
     */
    
    public static void initialPointValues(OccurrenceDTO occurrence){
    	Double lat = occurrence.getLatitude();
    	Double lon = occurrence.getLongitude();
    	occurrence.setPoint1(MathUtils.round(lat, 0)+","+MathUtils.round(lon, 0));
    	occurrence.setPoint01(MathUtils.round(lat, 1)+","+MathUtils.round(lon, 1));
    	occurrence.setPoint001(MathUtils.round(lat, 2)+","+MathUtils.round(lon, 2));
    	occurrence.setPoint0001(MathUtils.round(lat, 3)+","+MathUtils.round(lon, 3));
    }
}
