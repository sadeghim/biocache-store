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

package org.ala.biocache.web;

import atg.taglib.json.util.JSONArray;
import atg.taglib.json.util.JSONObject;
import java.util.logging.Level;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for the "contribute data" page
 *
 * @author "Nick dos Remedios <Nick.dosRemedios@csiro.au>"
 */
@Controller("contributeDataController")
public class ContributeDataController {
    /** Logger */
	private final static Logger logger = Logger.getLogger(ContributeDataController.class);
    /** JSP page names */
	private String SIGHTING = "contribute/sighting";
    private String SIGHTING_CONFIRM = "contribute/sightingConfirmation";
    private String SIGHTING_THANKYOU = "contribute/sightingThankyou";
    /** Base URL for BIE webapp */
    private String bieBaseUrl = "http://bie.ala.org.au";
    /** Google API key - injected by Spring */
    private String googleKey;

    /**
     * Initial sighting page as requested via GET
     *
     * @param guid
     * @param model
     * @return
     */
    @RequestMapping(value = "/contribute/sighting/{guid}", method = RequestMethod.GET)
	public String contributeSighting(
            @PathVariable("guid") String guid,
            Model model)  {
        try {
            MiniTaxonConceptDTO dto = getTaxonConceptProperties(guid);
            model.addAttribute("taxonConcept", dto);
        } catch (Exception ex) {
            String msg = "Error: could not find species for guid: "+guid+" ("+ex.getLocalizedMessage()+")";
            logger.error(msg, ex);
            model.addAttribute("error", msg);
        }
        
        return SIGHTING;
    }

    /**
     * POST version of sighting page, gets called after submit button has been clicked.
     *
     * @param guid
     * @param sightingGuid
     * @param sightingDate
     * @param sightingTime
     * @param sightingNumber
     * @param sightingLocation
     * @param sightinglatitude
     * @param sightingLongitude
     * @param sightingCoordinateUncertainty
     * @param sightingNotes
     * @param sightingAddress
     * @param sightingAction
     * @param model
     * @return
     */
    @RequestMapping(value = "/contribute/sighting/{guid}", method = RequestMethod.POST)
	public String contributeSighting(
            @PathVariable("guid") String guid,
            @RequestParam(value="guid", required=false) String sightingGuid,
            @RequestParam(value="date", required=false) String sightingDate,
            @RequestParam(value="time", required=false) String sightingTime,
            @RequestParam(value="number", required=false) String sightingNumber,
            @RequestParam(value="location", required=false) String sightingLocation,
            @RequestParam(value="latitude", required=false) String sightinglatitude,
            @RequestParam(value="longitude", required=false) String sightingLongitude,
            @RequestParam(value="coordinateUncertainty", required=false) String sightingCoordinateUncertainty,
            @RequestParam(value="notes", required=false) String sightingNotes,
            @RequestParam(value="address", required=false) String sightingAddress,
            @RequestParam(value="action", required=false) String sightingAction,
            Model model)  {
        try {
            MiniTaxonConceptDTO dto = getTaxonConceptProperties(guid);
            model.addAttribute("taxonConcept", dto);
        } catch (Exception ex) {
            String msg = "Error: could not find species for guid (POST): "+guid+" ("+ex.getLocalizedMessage()+")";
            logger.error(msg, ex);
            model.addAttribute("error", msg);
        }

        String pageType = SIGHTING;
        // If user submits form show the confirmation page with option to go back and edit values
        if ("Next >".equalsIgnoreCase(sightingAction)) {
            pageType = SIGHTING_CONFIRM;
        } else if ("Finish".equalsIgnoreCase(sightingAction)) {
            pageType = SIGHTING_THANKYOU;
            // populate bean and pass to DAO...
        }
        
        return pageType;
    }

    /**
     * Call BIE JSON service and populate a MiniTaxonConceptDTO
     *
     * @param guid
     * @return
     */
    protected MiniTaxonConceptDTO getTaxonConceptProperties(String guid) throws Exception {
        MiniTaxonConceptDTO dto = new MiniTaxonConceptDTO();
        String JsonString = getUrlContentAsString(bieBaseUrl + "/species/" + guid + ".json");
        logger.debug("json string: "+JsonString);
        JSONObject jsonObj = new JSONObject(JsonString);
        JSONObject etc = jsonObj.getJSONObject("extendedTaxonConceptDTO");
        dto.setGuid(guid);
        JSONObject tc = etc.getJSONObject("taxonConcept");
        
        if (tc != null) {
            dto.setScientificName(tc.getString("nameString"));
            dto.setRankId(tc.getString("rankID"));
        }
        
        JSONArray cns = etc.getJSONArray("commonNames");

        if (cns != null && cns.size() > 0) {
            dto.setCommonName(cns.getJSONObject(0).getString("nameString"));
        }

        JSONArray images = etc.getJSONArray("images");

        if (images != null && images.size() > 0) {
            dto.setImageThumbnailUrl(images.getJSONObject(0).getString("thumbnail"));
        }

        return dto;
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
		String content = gm.getResponseBodyAsString();

		return content;
	}

    /**
     * Inner class/bean for transferring a few taxon concept properties into web page
     */
    public class MiniTaxonConceptDTO {
        private String guid;
        private String scientificName;
        private String rankId;
        private String commonName;
        private String imageThumbnailUrl;

        @Override
        public String toString() {
            return "MiniTaxonConceptDTO{" + "guid=" + guid + "scientificName=" + scientificName + "rankId=" +
                    rankId + "commonName=" + commonName + "imageThumbnailUrl=" + imageThumbnailUrl + '}';
        }
        
        public String getCommonName() {
            return commonName;
        }

        public void setCommonName(String commonName) {
            this.commonName = commonName;
        }

        public String getGuid() {
            return guid;
        }

        public void setGuid(String guid) {
            this.guid = guid;
        }

        public String getImageThumbnailUrl() {
            return imageThumbnailUrl;
        }

        public void setImageThumbnailUrl(String imageThumbnailUrl) {
            this.imageThumbnailUrl = imageThumbnailUrl;
        }

        public String getScientificName() {
            return scientificName;
        }

        public void setScientificName(String scientificName) {
            this.scientificName = scientificName;
        }

        public String getRankId() {
            return rankId;
        }

        public void setRankId(String rankId) {
            this.rankId = rankId;
        }
    }

    public String getGoogleKey() {
        return googleKey;
    }

    public void setGoogleKey(String googleKey) {
        this.googleKey = googleKey;
    }
    
}
