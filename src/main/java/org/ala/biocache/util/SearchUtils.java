package org.ala.biocache.util;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import atg.taglib.json.util.JSONArray;
import atg.taglib.json.util.JSONObject;
import org.apache.log4j.Logger;
/**
 * A class to provide utility methods used to populate search details.
 * @author Natasha
 */
public class SearchUtils {
    /** Logger initialisation */
	private final static Logger logger = Logger.getLogger(SearchUtils.class);
    protected String collectoryBaseUrl = "http://collections.ala.org.au";
    /**
     * Returns an array that contains the search string to use for a collection
     * search and display name for the results.
     * @param query
     * @return
     */
    public String[] getCollectionSearchString(String query) {
        try{
        StringBuilder solrQuery = new StringBuilder();
        //query the collectory for the institute and collection codes needed to perform the search
        String jsonObject = org.ala.biocache.web.OccurrenceController.getUrlContentAsString(collectoryBaseUrl + "/collection/summary/" + query);
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
                collections.add("collection_code:" + collectionCode.getString(i));
                //	displayString.append(coll).append(" ");
            }
            solrQuery.append("(");
            solrQuery.append(StringUtils.join(collections, " OR "));
            solrQuery.append(")");

        }
        return new String[]{solrQuery.toString(), displayString.toString()};
        }
        catch(Exception e){}
        return null;
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
     * @return
     */
    public String getQueryString(String query, String type) {
        logger.debug("Processing " + query +" using type: " + type);
        if (type.equals("collection")) {
            String[] values = getCollectionSearchString(query);
            if(values!= null)
                return values[0];
            return null;
        } else if (type.equals("provider")) {
            return getDataProviderSearchString(query);
        } else if (type.equals("resource")) {
            return getDataResourceSearchString(query);
        }
        //otherwise it is a normal search 
        return query;
    }
}
