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

/**
 *
 * @author "Tommy Wang <tommy.wang@csiro.au>"
 */

package org.ala.biocache.web;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.ala.biocache.dao.BasisOfRecordDAO;
import org.ala.biocache.dao.DataProviderDAO;
import org.ala.biocache.dao.DataResourceDAO;
import org.ala.biocache.model.BasisOfRecord;
import org.ala.biocache.model.DataProvider;
import org.ala.biocache.model.DataResource;
import org.ala.biocache.model.OccurrencePoint;
import org.ala.biocache.model.PointType;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DataProviderController {

	final String DATA_PROVIDER_LIST = "data/dataProviders";
	final String DATA_RESOURCE_LIST = "data/dataResources";

	/** Data Provider DAO */
	//    @Inject
	//    protected DataProviderDAO dataProviderDAO;

	/** Data Resource DAO */
	@Inject
	protected DataResourceDAO dataResourceDAO;
	
	/** Data Provider DAO */
	@Inject
	protected DataProviderDAO dataProviderDAO;
	
	/** Basis of Record DAO */
	@Inject
	protected BasisOfRecordDAO basisOfRecordDAO;

	/** Logger initialisation */
	private final static Logger logger = Logger.getLogger(GeoJsonController.class);

	@RequestMapping(value = "/data_provider/{dataProviderId}", method = RequestMethod.GET)
	public String getDataProvider(@PathVariable("dataProviderId") String dataProviderId, Model model)
	throws Exception {
		model.addAttribute("dataProviderId", dataProviderId);

		int dataPId = Integer.parseInt(dataProviderId);

		List<DataResource> dataResources = dataResourceDAO.getByDataProviderId(dataPId);
		DataProvider dataProvider = dataProviderDAO.getById(dataPId);
		List<BasisOfRecord> basisOfRecords = basisOfRecordDAO.getAll();

		if (dataResources != null && dataProvider != null) {

			model.addAttribute("dataResources", dataResources);
			model.addAttribute("dataProvider", dataProvider);
			model.addAttribute("basisOfRecords", basisOfRecords);
		} 

		return DATA_PROVIDER_LIST;
	}
	
	@RequestMapping(value = "/data_resource/{dataResourceId}", method = RequestMethod.GET)
	public String getDataResource(@PathVariable("dataResourceId") String dataResourceId, Model model)
	throws Exception {
		model.addAttribute("dataResourceId", dataResourceId);

		int dataRId = Integer.parseInt(dataResourceId);

		DataResource dataResource = dataResourceDAO.getById(dataRId);
		DataProvider dataProvider = dataProviderDAO.getById(dataResource.getDataProviderId());
		BasisOfRecord basisOfRecord = basisOfRecordDAO.getById(dataResource.getBasisOfRecord());

		if (dataResource != null) {
			model.addAttribute("dataProvider", dataProvider);
			model.addAttribute("dataResource", dataResource);
			model.addAttribute("basisOfRecord", basisOfRecord);
		} 

		return DATA_RESOURCE_LIST;
	}

}
