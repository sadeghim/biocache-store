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

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.ala.biocache.dto.Sighting;
import org.ala.biocache.service.ContributeService;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import atg.taglib.json.util.JSONArray;
import atg.taglib.json.util.JSONObject;

import com.maxmind.geoip.Location;
import com.maxmind.geoip.LookupService;

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
    /** file ssytem location for GeoIP database */
    private String geoIpDatabase = "/data/geoip/GeoLiteCity.dat";
    @Inject
    protected ContributeService contributeService;
    
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
            HttpServletRequest request,
            Model model) throws Exception  {
        try {
            MiniTaxonConceptDTO dto = getTaxonConceptProperties(guid);
            model.addAttribute("taxonConcept", dto);
        } catch (Exception ex) {
            String msg = "Error: could not find species for guid: "+guid+" ("+ex.getLocalizedMessage()+")";
            logger.error(msg, ex);
            model.addAttribute("error", msg);
        }

        // Determine lat/long for client's IP address
        LookupService lookup = new LookupService(geoIpDatabase, LookupService.GEOIP_MEMORY_CACHE );
        String clientIP = request.getLocalAddr();
        logger.info("client IP address = "+request.getRemoteAddr());
        Location loc = lookup.getLocation(clientIP);
        Float latitude = null;
        Float longitude = null;

        if (loc != null) {
            logger.info(clientIP + " has location: " + loc.postalCode + ", " + loc.city + ", " + loc.region + ". Coords: " + loc.latitude + ", " + loc.longitude);
            latitude = loc.latitude;
            longitude = loc.longitude;
        }

        model.addAttribute("latitude", latitude);
        model.addAttribute("longitude", longitude);

        return SIGHTING;
    }

    /**
     * POST version of sighting page, gets called after submit button has been clicked.
     *
     * @param guid
     * @param taxonConceptID
     * @param eventTime
     * @param sightingTime
     * @param individualCount
     * @param verbatimLocality
     * @param latitude
     * @param longitude
     * @param coordinateUncertainty
     * @param fieldNotes
     * @param sightingAction
     * @param model
     * @return
     */
    @RequestMapping(value = "/contribute/sighting/{guid}", method = RequestMethod.POST)
	public String contributeSighting(
            @PathVariable("guid") String guid,
            @RequestParam(value="guid", required=false) String taxonConceptGuid,
            @RequestParam(value="scientificName", required=false) String scientificName,
            @RequestParam(value="vernacularName", required=false) String vernacularName,
            @RequestParam(value="date", required=false) java.util.Date eventDate,
            @RequestParam(value="time", required=false) String eventTime,
            @RequestParam(value="number", required=false) Integer individualCount,
            @RequestParam(value="verbatimLocality", required=false) String locality,
            @RequestParam(value="latitude", required=false) Float latitude,
            @RequestParam(value="longitude", required=false) Float longitude,
            @RequestParam(value="coordinateUncertainty", required=false) Float coordinateUncertaintyInMeters,
            @RequestParam(value="recordedBy", required=false) String collectorName,
            @RequestParam(value="recordedById", required=false) String userId,
            @RequestParam(value="notes", required=false) String occurrenceRemarks,
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
            
            Sighting s = new Sighting();
            s.setTaxonConceptGuid(taxonConceptGuid);
            
            if(coordinateUncertaintyInMeters!=null){
            	s.setCoordinateUncertaintyInMeters(coordinateUncertaintyInMeters.intValue());
            }
            
            s.setScientificName(scientificName);
            s.setCollectorName(collectorName);
            s.setEventDate(eventDate);
            s.setUserId(userId);
            s.setCollectorName(collectorName);
            s.setLocality(locality);
            s.setLatitude(latitude);
            s.setLongitude(longitude);
            s.setOccurrenceRemarks(occurrenceRemarks);
            
            //send it off to the service
            contributeService.recordSighting(s);
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

	/**
	 * @param contributeService the contributeService to set
	 */
	public void setContributeService(ContributeService contributeService) {
		this.contributeService = contributeService;
	}
}
