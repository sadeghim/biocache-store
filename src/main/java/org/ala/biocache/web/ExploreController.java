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

import java.util.List;
import javax.inject.Inject;
import org.ala.biocache.dao.SearchDao;
import org.ala.biocache.model.OccurrencePoint;
import org.ala.biocache.model.PointType;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
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
    protected SearchDao searchDAO;
    /** Name of view for site home page */
	private String YOUR_AREA = "explore/yourArea";

    @RequestMapping(value = "/explore/your-area", method = RequestMethod.GET)
	public ModelAndView yourAreaView(@RequestParam(value="radius", required=false, defaultValue="50") Integer radius) throws Exception {
		ModelAndView mav = new ModelAndView();
		mav.setViewName(YOUR_AREA);
        Float latitude = -35.27412f;
		mav.addObject("latitude", latitude);
        Float longitude = 149.11288f;
        mav.addObject("longitude", longitude);
        String location = "Canberra, ACT";
        mav.addObject("location", location);
        mav.addObject("radius", radius);
        Integer zoom = 11;
        mav.addObject("zoom", zoom);
        //String query = "{!spatial lat="+ latitude.toString() +" long="+ longitude.toString() +
        //        " radius=" + radius.toString() + " unit=km calc=arc threadCount=2}*:*";
        //mav.addObject("query", query);
        //PointType pointType = PointType.POINT_00001;
        //List<OccurrencePoint> points  = searchDAO.findRecordsForLocation(latitude, longitude, radius, pointType);

		return mav;
	}

   

}
