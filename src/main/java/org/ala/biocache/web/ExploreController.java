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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import javax.inject.Inject;
import org.ala.biocache.dao.SearchDao;
import org.ala.biocache.model.TaxaCountDTO;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for the "explore your area" page
 *
 * @author "Nick dos Remedios <Nick.dosRemedios@csiro.au>"
 */
@Controller
public class ExploreController {
    /** Logger initialisation */
	private final static Logger logger = Logger.getLogger(ExploreController.class);

    /** Fulltext search DAO */
    @Inject
    protected SearchDao searchDao;
    /** Name of view for site home page */
	private String YOUR_AREA = "explore/yourArea";
    private String speciesPageUrl = "http://alaslvweb2-cbr.vm.csiro.au:8080/bie-webapp/species/";
    
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
            Model model) throws Exception {

        model.addAttribute("latitude", latitude);
        model.addAttribute("longitude", longitude);
        String location = "Canberra, ACT"; // TODO: get via gazateer service
        model.addAttribute("location", location);
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
		fishTaxa.add("Myxini");
		fishTaxa.add("Petromyzontida");
		fishTaxa.add("Chondrichthyes");
		fishTaxa.add("Sarcopterygii");
		fishTaxa.add("Actinopterygii");
		List<TaxaCountDTO> fish = searchDao.findAllSpeciesByCircleAreaAndHigherTaxa(latitude, longitude, radius, "order", fishTaxa, null, 0, -1, "species", "asc");
		model.addAttribute("fish", fish);
        model.addAttribute("fishCount", calculateRecordCount(fish));
        // TODO: get from properties file or load via Spring
        model.addAttribute("speciesPageUrl", speciesPageUrl);

		return YOUR_AREA;
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
            @RequestParam(value="taxa", required=false, defaultValue="") String taxa,
            @RequestParam(value="rank", required=false, defaultValue="") String rank,
            Model model) throws Exception {

        model.addAttribute("taxa", taxa);
        model.addAttribute("rank", rank);
        List<TaxaCountDTO> species = searchDao.findAllSpeciesByCircleAreaAndHigherTaxon(latitude, longitude, radius, rank, taxa, null, 0, -1, "count", "asc");
        model.addAttribute("species", species);
        model.addAttribute("speciesCount", species.size());
    }
}
