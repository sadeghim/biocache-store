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
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import org.ala.biocache.dao.SearchDAO;
import org.ala.biocache.dto.MiniTaxonConceptDTO;
import org.ala.biocache.dto.OccurrenceDTO;
import org.ala.biocache.dto.Sighting;
import org.ala.biocache.dto.TaxaCountDTO;
import org.ala.biocache.service.ContributeService;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
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
import org.jasig.cas.client.authentication.AttributePrincipal;

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
    private String YOUR_SIGHTINGS = "contribute/yourSightings";
    private String CONFIRM_DELETE = "contribute/deleteSightingConfirmation";
    /** Base URL for BIE webapp */
    private String bieBaseUrl = "http://bie.ala.org.au";
    /** Google API key - injected by Spring */
    private String googleKey;
    /** file system location for GeoIP database - get from http://www.maxmind.com/app/geolitecity */
    private String geoIpDatabase = "/data/geoip/GeoLiteCity.dat";
    /** ContributeService service class injected by Spring */
    @Inject
    protected ContributeService contributeService;
    /** Fulltext search DAO */
    @Inject
    protected SearchDAO searchDAO;
    /**
     * Initial sighting page as requested via GET
     *
     * @param guid
     * @param model
     * @return
     */
    @RequestMapping(value = "/share/sighting/{guid}", method = RequestMethod.GET)
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
        // default values are for the Lambert gravitational centre of Australia
        Float latitude = -25f;
        Float longitude = 134f;

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
    @RequestMapping(value = "/share/sighting/{guid}", method = RequestMethod.POST)
	public String contributeSighting(
            @PathVariable("guid") String guid,
            @RequestParam(value="guid", required=false) String taxonConceptGuid,
            @RequestParam(value="scientificName", required=false) String scientificName,
            @RequestParam(value="commonName", required=false) String vernacularName,
            @RequestParam(value="kingdom", required=false) String kingdom,
            @RequestParam(value="family", required=false) String family,
            @RequestParam(value="rank", required=false) String rank,
            @RequestParam(value="date", required=false) String eventDateAsString,
            @RequestParam(value="time", required=false) String eventTime,
            @RequestParam(value="number", required=false) Integer individualCount,
            @RequestParam(value="verbatimLocality", required=false) String locality,
            @RequestParam(value="latitude", required=false) Float latitude,
            @RequestParam(value="longitude", required=false) Float longitude,
            @RequestParam(value="coordinateUncertainty", required=false) Float coordinateUncertaintyInMeters,
            @RequestParam(value="recordedBy", required=false) String collectorName,
            @RequestParam(value="recordedById", required=false) String userId,
            @RequestParam(value="notes", required=false) String occurrenceRemarks,
            @RequestParam(value="stateProvince", required=false) String stateProvince,
            @RequestParam(value="country", required=false) String country,
            @RequestParam(value="countryCode", required=false) String countryCode,
            @RequestParam(value="action", required=false) String sightingAction,
            Model model) throws Exception {
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
        } else if ("Submit".equalsIgnoreCase(sightingAction)) {
            pageType = SIGHTING_THANKYOU;
            // populate bean and pass to DAO...
            
            Sighting s = new Sighting();
            s.setTaxonConceptGuid(taxonConceptGuid);
            
            if(coordinateUncertaintyInMeters!=null){
            	s.setCoordinateUncertaintyInMeters(coordinateUncertaintyInMeters.intValue());
            }
            
            s.setScientificName(scientificName);
            s.setVernacularName(vernacularName);
            s.setFamily(family);
            s.setKingdom(kingdom);
            s.setRank(rank);
            
            if(StringUtils.isNotEmpty(eventDateAsString)){
            	if(eventTime!=null){
            		eventDateAsString = eventDateAsString + " " + eventTime; 
            	}
            	Date eventDate = DateUtils.parseDate(eventDateAsString, new String[]{"dd/MM/yyyy HH:mm", "yyyy-MM-dd HH:mm"});
            	s.setEventDate(eventDate);
            }
            s.setEventTime(eventTime);
            s.setUserId(userId);
            s.setCollectorName(collectorName);
            s.setLocality(locality);
            s.setLatitude(latitude);
            s.setLongitude(longitude);
            s.setOccurrenceRemarks(occurrenceRemarks);
            s.setStateProvince(stateProvince);
            s.setCountry(country);
            s.setCountryCode(countryCode);
            s.setIndividualCount(individualCount);
            
            //send it off to the service
            if (!contributeService.recordSighting(s)) {
                model.addAttribute("error", "Error: your sighting was not saved. Please try again later.");
            } else {
            	return "redirect:/share/your-sightings"; // user sees their list of sightings
            }
        }
        
        return pageType;
    }

    /**
     * Delete a sighting - gets called by occurrence record page if user is logged-in and the
     * sighting was created by that user. Returns a confirmation page on completion.
     *
     * @param sightingId
     * @param userId
     * @param request
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/share/sighting/delete", method = RequestMethod.POST)
	public String deleteSighting(
            @RequestParam(value="sightingId", required=true) String sightingId,
            @RequestParam(value="userId", required=true) String userId,
            HttpServletRequest request,
            Model model) throws Exception {

        String remoteuser = request.getRemoteUser();

        if (!userId.equalsIgnoreCase(remoteuser)) {
            // casUserId must match userId -  if not someone is trying something dodgey...
            String msg = "Logged in userId does not match requested userId. ";
            logger.error(msg + "sightingId = "+sightingId+"; userId = "+userId+"; remoteuser = "+remoteuser);
            model.addAttribute("error", msg);
        } else {
            // requested userId = logged-in userId - go ahead and delete sighting
            if (!contributeService.deleteSighting(sightingId)) {
                model.addAttribute("error", "Your sighting was not deleted. Please try again later.");
            } else {
                model.addAttribute("sightingDeleted", true);
            }
        }
        
        return CONFIRM_DELETE;
    }

    /**
     * Your Sightings page - requires authenticated user_id
     *
     * @param request
     * @param model
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/share/your-sightings*", method = RequestMethod.GET)
	public String yourSighting(
            HttpServletRequest request,
            Model model) throws Exception {

        String remoteuser = request.getRemoteUser();

        if (remoteuser != null) {
            // user is authenticated
            List<TaxaCountDTO> speciesWithCounts = searchDAO.findTaxaByUserId(remoteuser);
            model.addAttribute("speciesWithCounts", speciesWithCounts);
            logger.info("Finding all taxa contributed by "+remoteuser+": "+speciesWithCounts.size());
            List<MiniTaxonConceptDTO> taxonConceptMap = new ArrayList< MiniTaxonConceptDTO>();
            
            for (TaxaCountDTO tc : speciesWithCounts) {
                logger.debug("tc: "+tc);
                String guid = tc.getGuid();
                MiniTaxonConceptDTO mtc = getTaxonConceptProperties(guid);
                mtc.setCount(tc.getCount()); // add counts 
                taxonConceptMap.add(mtc);
                logger.debug("mtc = "+mtc);
            }

            model.addAttribute("taxonConceptMap", taxonConceptMap);

            // get points
            List<OccurrenceDTO> occurrences = searchDAO.findPointsForUserId(remoteuser);
            logger.info("Finding all occurrences contributed by "+remoteuser+": "+occurrences.size());
            model.addAttribute("occurrences", occurrences);
        } 

        return YOUR_SIGHTINGS;
    }

    /**
     * Call BIE JSON service and populate a MiniTaxonConceptDTO
     *
     * @param guid
     * @return
     */
    protected MiniTaxonConceptDTO getTaxonConceptProperties(String guid) throws Exception {
        MiniTaxonConceptDTO dto = new MiniTaxonConceptDTO();
        logger.info("JSON URI: "+bieBaseUrl + "/species/" + guid + ".json");
        String JsonString = getUrlContentAsString(bieBaseUrl + "/species/" + guid + ".json");
        logger.debug("json string for "+guid+": "+JsonString);
        JSONObject jsonObj = new JSONObject(JsonString);
        JSONObject etc = jsonObj.getJSONObject("extendedTaxonConceptDTO");
        dto.setGuid(guid);
        JSONObject tc = etc.getJSONObject("taxonConcept");
        
        if (tc != null) {
            dto.setScientificName(tc.getString("nameString"));
            dto.setRankId(tc.getString("rankID"));
            dto.setRank(tc.getString("rankString"));
        }

        JSONObject taxonName = etc.getJSONObject("taxonName");
        if (taxonName != null) {
            dto.setTaxonName(taxonName.getString("nameComplete"));
        }
        
        JSONObject classification = etc.getJSONObject("classification");
        if(classification!=null){
            dto.setKingdom(classification.getString("kingdom"));
            dto.setFamily(classification.getString("family"));
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
