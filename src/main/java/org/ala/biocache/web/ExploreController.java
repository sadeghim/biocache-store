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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.ala.biocache.dao.SearchDao;
import org.ala.biocache.model.TaxaCountDTO;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Controller for the "explore your area" page
 *
 * @author "Nick dos Remedios <Nick.dosRemedios@csiro.au>"
 */
@Controller("exploreController")
public class ExploreController {
    /** Logger initialisation */
	private final static Logger logger = Logger.getLogger(ExploreController.class);

    /** Fulltext search DAO */
    @Inject
    protected SearchDao searchDao;
    /** Name of view for site home page */
	private String YOUR_AREA = "explore/yourArea";
    private String speciesPageUrl = "http://bie.ala.org.au/species/";
    private String googleKey; // set in properties override
    private HashMap<String, List<Float>> addressCache = new HashMap<String, List<Float>>();
    /** Mapping of radius in km to OpenLayers zoom level */
    public final static HashMap<Integer, Integer> radiusToZoomLevelMap = new HashMap<Integer, Integer>();
	static {
		radiusToZoomLevelMap.put(5, 12);
		radiusToZoomLevelMap.put(10, 11);
		radiusToZoomLevelMap.put(50, 9);
	}

    @RequestMapping(value = "/explore/your-area*", method = RequestMethod.GET)
	public String yourAreaView(
            @RequestParam(value="radius", required=false, defaultValue="10") Integer radius,
            @RequestParam(value="latitude", required=false, defaultValue="-35.27412f") Float latitude,
            @RequestParam(value="longitude", required=false, defaultValue="149.11288f") Float longitude,
            @RequestParam(value="address", required=false, defaultValue="-35.27412,149.11288") String address,
            @RequestParam(value="location", required=false, defaultValue="") String location,
            Model model) throws Exception {
        
        model.addAttribute("googleKey", googleKey);
        model.addAttribute("latitude", latitude);
        model.addAttribute("longitude", longitude);
        model.addAttribute("location", location);
        model.addAttribute("address", address);
        model.addAttribute("radius", radius);
        model.addAttribute("zoom", radiusToZoomLevelMap.get(radius));

        //List<TaxaCountDTO> kingdoms = searchDao.findAllKingdomsByCircleArea(latitude, longitude, radius,  null, 0, -1, "kingdom", "asc");
        List<TaxaCountDTO> allLife = searchDao.findAllSpeciesByCircleAreaAndHigherTaxon(latitude, longitude, radius, "*", "*", null, 0, -1, "species", "asc");
        model.addAttribute("allLife", allLife);
        model.addAttribute("allLifeCounts", calculateRecordCount(allLife));
        // Kingdom groups
        List<TaxaCountDTO> animals = searchDao.findAllSpeciesByCircleAreaAndHigherTaxon(latitude, longitude, radius, "kingdom", "Animalia", null, 0, -1, "species", "asc");
        model.addAttribute("animals", animals);
        model.addAttribute("animalsCount", calculateRecordCount(animals));
        List<TaxaCountDTO> plants = searchDao.findAllSpeciesByCircleAreaAndHigherTaxon(latitude, longitude, radius, "kingdom", "Plantae", null, 0, -1, "species", "asc");
        model.addAttribute("plants", plants);
        model.addAttribute("plantsCount", calculateRecordCount(plants));
        List<TaxaCountDTO> fungi = searchDao.findAllSpeciesByCircleAreaAndHigherTaxon(latitude, longitude, radius, "kingdom", "Fungi", null, 0, -1, "species", "asc");
        model.addAttribute("fungi", fungi);
        model.addAttribute("fungiCount", calculateRecordCount(fungi));
        List<TaxaCountDTO> chromista = searchDao.findAllSpeciesByCircleAreaAndHigherTaxon(latitude, longitude, radius, "kingdom", "Chromista", null, 0, -1, "species", "asc");
        model.addAttribute("chromista", chromista);
        model.addAttribute("chromistaCount", calculateRecordCount(chromista));
        List<TaxaCountDTO> protozoa = searchDao.findAllSpeciesByCircleAreaAndHigherTaxon(latitude, longitude, radius, "kingdom", "Protozoa", null, 0, -1, "species", "asc");
        model.addAttribute("protozoa", protozoa);
        model.addAttribute("protozoaCount", calculateRecordCount(protozoa));
        List<TaxaCountDTO> bacteria = searchDao.findAllSpeciesByCircleAreaAndHigherTaxon(latitude, longitude, radius, "kingdom", "Bacteria", null, 0, -1, "species", "asc");
        model.addAttribute("bacteria", bacteria);
        model.addAttribute("bacteriaCount", calculateRecordCount(bacteria));
        // Animal groups
        List<TaxaCountDTO> insects = searchDao.findAllSpeciesByCircleAreaAndHigherTaxon(latitude, longitude, radius, "class", "Insecta", null, 0, -1, "species", "asc");
        model.addAttribute("insects", insects);
        model.addAttribute("insectsCount", calculateRecordCount(insects));
        List<TaxaCountDTO> mammals = searchDao.findAllSpeciesByCircleAreaAndHigherTaxon(latitude, longitude, radius, "class", "Mammalia", null, 0, -1, "species", "asc");
        model.addAttribute("mammals", mammals);
        model.addAttribute("mammalsCount", calculateRecordCount(mammals));
        List<TaxaCountDTO> birds = searchDao.findAllSpeciesByCircleAreaAndHigherTaxon(latitude, longitude, radius, "class", "Aves", null, 0, -1, "species", "asc");
        model.addAttribute("birds", birds);
        model.addAttribute("birdsCount", calculateRecordCount(birds));
        List<TaxaCountDTO> reptiles = searchDao.findAllSpeciesByCircleAreaAndHigherTaxon(latitude, longitude, radius, "class", "Reptilia", null, 0, -1, "species", "asc");
        model.addAttribute("reptiles", reptiles);
        model.addAttribute("reptilesCount", calculateRecordCount(reptiles));
        List<TaxaCountDTO> frogs = searchDao.findAllSpeciesByCircleAreaAndHigherTaxon(latitude, longitude, radius, "class", "Amphibia", null, 0, -1, "species", "asc");
		model.addAttribute("frogs", frogs);
        model.addAttribute("frogsCount", calculateRecordCount(frogs));
        List<String> fishTaxa = new ArrayList<String>();
        // Agnatha|Chondrichthyes|
		fishTaxa.add("Agnatha");
		fishTaxa.add("Chondrichthyes");
		fishTaxa.add("Osteichthyes");
		List<TaxaCountDTO> fish = searchDao.findAllSpeciesByCircleAreaAndHigherTaxa(latitude, longitude, radius, "class", fishTaxa, null, 0, -1, "species", "asc");
		model.addAttribute("fish", fish);
        model.addAttribute("fishCount", calculateRecordCount(fish));
        // TODO: get from properties file or load via Spring
        model.addAttribute("speciesPageUrl", speciesPageUrl);

		return YOUR_AREA;
	}
        /**
	 * Occurrence search page uses SOLR JSON to display results
	 *
     * @param query
     * @param model
     * @return
     * @throws Exception
     */
	@RequestMapping(value = "/explore/download*", method = RequestMethod.GET)
	public void yourAreaDownload(
            @RequestParam(value="radius", required=false, defaultValue="10") Integer radius,
            @RequestParam(value="latitude", required=false, defaultValue="0f") Float latitude,
            @RequestParam(value="longitude", required=false, defaultValue="0f") Float longitude,
            @RequestParam(value="taxa", required=false, defaultValue="") String taxa, // comma separated list
            @RequestParam(value="rank", required=false, defaultValue="") String rank,
            HttpServletResponse response)
            throws Exception {


            logger.debug("Downloading the species in your area... ");
        response.setHeader("Cache-Control", "must-revalidate");
        response.setHeader("Pragma", "must-revalidate");
        response.setHeader("Content-Disposition", "attachment;filename=data");
        response.setContentType("application/vnd.ms-excel");
        String[] taxaArray = StringUtils.split(taxa, ",");
        ArrayList<String> taxaList = null;
        if (taxaArray != null) {
            taxaList = new ArrayList<String>(Arrays.asList(taxaArray));
        } else {
            taxaList = new ArrayList<String>();
        }
        ServletOutputStream out = response.getOutputStream();
        int count =searchDao.writeSpeciesCountByCircleToStream(latitude, longitude, radius, rank, taxaList, out);
        logger.debug("Exported " + count + " species records in the requested area");
        
	}

    protected Long calculateRecordCount(List<TaxaCountDTO> taxa) {
        // Get full count of records in area from facet breakdowns
        Long totalRecords = 0l;
        for (TaxaCountDTO taxon : taxa) {
            totalRecords = totalRecords + taxon.getCount();
        }
        return totalRecords;
    }

    /**
     * JSON web service that returns a list of species and record counts for a given location search
     * and a higher taxa with rank. 
     *
     * @param radius
     * @param latitude
     * @param longitude
     * @param taxa
     * @param rank
     * @param model
     * @throws Exception
     */
    @RequestMapping(value = "/explore/species.json", method = RequestMethod.GET)
	public void listSpeciesForHigherTaxa(
            @RequestParam(value="radius", required=false, defaultValue="10") Integer radius,
            @RequestParam(value="latitude", required=false, defaultValue="0f") Float latitude,
            @RequestParam(value="longitude", required=false, defaultValue="0f") Float longitude,
            @RequestParam(value="taxa", required=false, defaultValue="") String taxa, // comma separated list
            @RequestParam(value="rank", required=false, defaultValue="") String rank,
            Model model) throws Exception {

        String[] taxaArray = StringUtils.split(taxa, ",");
        ArrayList<String> taxaList = null;
        if (taxaArray != null) {
            taxaList = new ArrayList<String>(Arrays.asList(taxaArray));
        } else {
            taxaList = new ArrayList<String>();
        }

        model.addAttribute("taxa", taxa);
        model.addAttribute("rank", rank);
        List<TaxaCountDTO> species = searchDao.findAllSpeciesByCircleAreaAndHigherTaxa(latitude, longitude, radius, rank, taxaList, null, 0, -1, "count", "asc");
        model.addAttribute("species", species);
        model.addAttribute("speciesCount", species.size());
    }

    public String getGoogleKey() {
        return googleKey;
    }

    public void setGoogleKey(String googleKey) {
        this.googleKey = googleKey;
    }
	/**
	 * @param searchDao the searchDao to set
	 */
	public void setSearchDao(SearchDao searchDao) {
		this.searchDao = searchDao;
	}
	/**
	 * @param speciesPageUrl the speciesPageUrl to set
	 */
	public void setSpeciesPageUrl(String speciesPageUrl) {
		this.speciesPageUrl = speciesPageUrl;
	}
	/**
	 * @param addressCache the addressCache to set
	 */
	public void setAddressCache(HashMap<String, List<Float>> addressCache) {
		this.addressCache = addressCache;
	}
}
