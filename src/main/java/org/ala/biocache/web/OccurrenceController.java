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

import org.ala.biocache.model.SearchResultDTO;
import org.ala.biocache.dao.SearchDao;
import org.ala.biocache.model.OccurrenceCell;
import org.ala.biocache.model.OccurrenceDTO;
import org.ala.biocache.model.OccurrencePoint;
import org.ala.biocache.model.PointType;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

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
	@RequestMapping(value = "/occurrences/search*", method = RequestMethod.GET)
	public String occurrenceSearch(
            @RequestParam(value="q", required=false) String query,
            @RequestParam(value="fq", required=false) String[] filterQuery,
            @RequestParam(value="start", required=false, defaultValue="0") Integer startIndex,
			@RequestParam(value="pageSize", required=false, defaultValue ="20") Integer pageSize,
			@RequestParam(value="sort", required=false, defaultValue="score") String sortField,
			@RequestParam(value="dir", required=false, defaultValue ="asc") String sortDirection,
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
        Integer lastPage = (totalRecords.intValue() / pageSize) + 1;
        model.addAttribute("lastPage", lastPage);

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

    @RequestMapping(value = "/occurrences/json/points.geojson", method = RequestMethod.GET)
	public String pointsGeoJson(
            @RequestParam(value="q", required=true) String query,
            @RequestParam(value="fq", required=false) String[] filterQuery,
            @RequestParam(value="callback", required=false) String callback,
            @RequestParam(value="zoom", required=false, defaultValue="0") Integer zoomLevel,
            Model model,
            HttpServletResponse response)
            throws Exception {

        if (callback != null && !callback.isEmpty()) {
            response.setContentType("text/javascript");
        } else {
            response.setContentType("application/json");
        }

        PointType pointType = PointType.POINT_1;

        // Map zoom levels to lat/long accuracy levels
        if (zoomLevel != null) {
            if (zoomLevel >= 0 && zoomLevel <= 6) {
                // 0-6 levels
                pointType = PointType.POINT_1;
            } else if (zoomLevel > 6 && zoomLevel <= 8) {
                // 6-7 levels
                pointType = PointType.POINT_01;
            } else if (zoomLevel > 8 && zoomLevel <= 10) {
                // 8-9 levels
                pointType = PointType.POINT_001;
            } else if (zoomLevel > 10 && zoomLevel <= 12) {
                // 10-12 levels
                pointType = PointType.POINT_0001;
            } else if (zoomLevel > 12 ) {
                // 12-n levels
                pointType = PointType.POINT_00001;
            }
        }

        List<OccurrencePoint> points = searchDAO.getFacetPoints(query, filterQuery, pointType);
        logger.debug("Points search for "+pointType.getLabel()+" - found: "+points.size());
        model.addAttribute("points", points);

        return POINTS_GEOJSON;
    }

    @RequestMapping(value = "/occurrences/json/cells.geojson", method = RequestMethod.GET)
	public String cellsGeoJson(
            @RequestParam(value="q", required=true) String query,
            @RequestParam(value="fq", required=false) String[] filterQuery,
            @RequestParam(value="callback", required=false) String callback,
            @RequestParam(value="zoom", required=false, defaultValue="0") Integer zoomLevel,
            Model model,
            HttpServletResponse response)
            throws Exception {

        if (callback != null && !callback.isEmpty()) {
            response.setContentType("text/javascript");
        } else {
            response.setContentType("application/json");
        }

        PointType pointType = PointType.POINT_1;

        // Map zoom levels to lat/long accuracy levels
        if (zoomLevel != null) {
            if (zoomLevel >= 0 && zoomLevel <= 6) {
                // 0-6 levels
                pointType = PointType.POINT_1;
            } else if (zoomLevel > 6 && zoomLevel <= 8) {
                // 6-7 levels
                pointType = PointType.POINT_01;
            } else if (zoomLevel > 8 && zoomLevel <= 10) {
                // 8-9 levels
                pointType = PointType.POINT_001;
            } else if (zoomLevel > 10 && zoomLevel <= 12) {
                // 10-12 levels
                pointType = PointType.POINT_0001;
            } else if (zoomLevel > 12 ) {
                // 12-n levels
                pointType = PointType.POINT_00001;
            }
        }

        List<OccurrencePoint> points = searchDAO.getFacetPoints(query, filterQuery, pointType);
        logger.debug("Cells search for "+pointType.getLabel()+" - found: "+points.size());
        List<OccurrenceCell> cells = new ArrayList<OccurrenceCell>();

        for (OccurrencePoint point : points) {
            //logger.debug("point => "+point);
            OccurrenceCell cell = new OccurrenceCell(point);
            //logger.debug("cell => "+cell);
            //cell.createCellCoords();
            cells.add(cell);
        }

        model.addAttribute("cells", cells);

        return CELLS_GEOJSON;
    }

	/**
	 * @param hostUrl the hostUrl to set
	 */
	public void setHostUrl(String hostUrl) {
		this.hostUrl = hostUrl;
	}
}
