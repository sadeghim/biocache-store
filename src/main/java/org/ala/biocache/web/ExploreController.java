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
    /** Mapping of radius in km to OpenLayers zoom level */
    public final static HashMap<Integer, Integer> radiusToZoomLevelMap = new HashMap<Integer, Integer>();
	static {
		radiusToZoomLevelMap.put(5, 12);
		radiusToZoomLevelMap.put(10, 11);
		radiusToZoomLevelMap.put(50, 9);
	}

    @RequestMapping(value = "/explore/your-area", method = RequestMethod.GET)
	public String yourAreaView(
            @RequestParam(value="radius", required=false, defaultValue="10") Integer radius,
            @RequestParam(value="latitude", required=false, defaultValue="-35.27412f") Float latitude,
            @RequestParam(value="longitude", required=false, defaultValue="149.11288f") Float longitude,
            Model model) throws Exception {

        //Float latitude = -33.88549f; // -35.27412f
		model.addAttribute("latitude", latitude);
        //Float longitude = 151.23469f; // 149.11288f
        model.addAttribute("longitude", longitude);
        String location = "Canberra, ACT";
        model.addAttribute("location", location);
        model.addAttribute("radius", radius);
        //Integer zoom = 11;
        model.addAttribute("zoom", radiusToZoomLevelMap.get(radius));

        List<TaxaCountDTO> kingdoms = searchDao.findAllKingdomsByCircleArea(latitude, longitude, radius,  null, 0, -1, "kingdom", "asc");
        model.addAttribute("kingdoms", kingdoms);

        // Get full count of records in area from kindoms facet breakdowns
        Long totalRecords = 0l;
        for (TaxaCountDTO taxa : kingdoms) {
            totalRecords = totalRecords + taxa.getCount();
        }
        model.addAttribute("totalRecords", totalRecords);
        
        List<TaxaCountDTO> mammals = searchDao.findAllSpeciesByCircleAreaAndHigherTaxon(latitude, longitude, radius, "class", "Mammalia", null, 0, -1, "species", "asc");
        model.addAttribute("mammals", mammals);
        List<TaxaCountDTO> birds = searchDao.findAllSpeciesByCircleAreaAndHigherTaxon(latitude, longitude, radius, "class", "Aves", null, 0, -1, "species", "asc");
        model.addAttribute("birds", birds);
        List<TaxaCountDTO> reptiles = searchDao.findAllSpeciesByCircleAreaAndHigherTaxon(latitude, longitude, radius, "class", "Reptilia", null, 0, -1, "species", "asc");
        model.addAttribute("reptiles", reptiles);
        List<TaxaCountDTO> frogs = searchDao.findAllSpeciesByCircleAreaAndHigherTaxon(latitude, longitude, radius, "class", "Amphibia", null, 0, -1, "species", "asc");
		model.addAttribute("frogs", frogs);
        //fish counts
		List<String> fishTaxa = new ArrayList<String>();
		fishTaxa.add("Myxini");
		fishTaxa.add("Petromyzontida");
		fishTaxa.add("Chondrichthyes");
		fishTaxa.add("Sarcopterygii");
		fishTaxa.add("Actinopterygii");
		List<TaxaCountDTO> fish = searchDao.findAllSpeciesByCircleAreaAndHigherTaxa(latitude, longitude, radius, "order", fishTaxa, null, 0, -1, "species", "asc");
		model.addAttribute("fish", fish);
        List<TaxaCountDTO> plants = searchDao.findAllSpeciesByCircleAreaAndHigherTaxon(latitude, longitude, radius, "kingdom", "Plantae", null, 0, -1, "species", "asc");
        model.addAttribute("plants", plants);
		return YOUR_AREA;
	}

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
