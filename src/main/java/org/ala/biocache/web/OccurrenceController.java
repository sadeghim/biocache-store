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
import org.apache.log4j.Logger;
import org.gbif.portal.dto.occurrence.OccurrenceRecordDTO;
import org.gbif.portal.dto.occurrence.RawOccurrenceRecordDTO;
import org.gbif.portal.service.OccurrenceManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Occurrences controller for the BIE biocahce site
 *
 * @author "Nick dos Remedios <Nick.dosRemedios@csiro.au>"
 */
@Controller("occurrenceController")
public class OccurrenceController {

	/** Logger initialisation */
	private final static Logger logger = Logger.getLogger(OccurrenceController.class);
    /** Occurrence Record DAO bean */
    @Inject
    protected OccurrenceManager occurrenceManager;
    //private OccurrenceRecordDAO occurrenceRecordDAO;
    /** Name of view for site home page */
	private String HOME = "homePage";
	/** Name of view for an empty search page */
	private final String SEARCH = "occurrences/search";
	/** Name of view for list of taxa */
	private final String LIST = "occurrences/list";
	/** Name of view for a single taxon */
	private final String SHOW = "occurrences/show";
	
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
     * @param model
     * @return
     * @throws Exception
     */
	@RequestMapping(value = "/occurrences/search", method = RequestMethod.GET)
	public String occurrenceSearch(Model model) throws Exception {
        return LIST;
	}

    /**
	 * Occurrence record page
	 *
     * @param id
	 * @param model
	 * @return view name
	 * @throws Exception
	 */
	@RequestMapping(value = "/occurrences/{id}", method = RequestMethod.GET)
	public String showSpecies(@PathVariable("id") String id, Model model) throws Exception {
		logger.debug("Retrieving occurrence record with guid: "+id+".");
        model.addAttribute("id", id);
		OccurrenceRecordDTO or = occurrenceManager.getOccurrenceRecordFor(id);
		model.addAttribute("occurrenceRecord", or);
        RawOccurrenceRecordDTO ror = occurrenceManager.getRawOccurrenceRecordFor(id);
        model.addAttribute("rawOccurrenceRecord", ror);
       
		return SHOW;
	}
}
