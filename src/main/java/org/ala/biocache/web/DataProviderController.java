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

import java.util.List;

import javax.inject.Inject;

import org.ala.biocache.dao.BasisOfRecordDAO;
import org.ala.biocache.dao.DataProviderDAO;
import org.ala.biocache.dao.DataResourceDAO;
import org.ala.biocache.dao.SearchDao;
import org.ala.biocache.model.BasisOfRecord;
import org.ala.biocache.model.DataProvider;
import org.ala.biocache.model.DataProviderCountDTO;
import org.ala.biocache.model.DataResource;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Displays views of data providers and resources.
 *
 * @author Tommy Wang
 * @author Dave Martin (David.Martin@csiro.au)
 */
@Controller
public class DataProviderController {
	
	/** Logger initialisation */
	private final static Logger logger = Logger.getLogger(GeoJsonController.class);

	/** Data Resource DAO */
	@Inject
	protected DataResourceDAO dataResourceDAO;
	
	/** Data Provider DAO */
	@Inject
	protected DataProviderDAO dataProviderDAO;
	
	/** Basis of Record DAO */
	@Inject
	protected BasisOfRecordDAO basisOfRecordDAO;
	
	/** Basis of Record DAO */
	@Inject
	protected SearchDao searchDao;

	final String DATA_PROVIDER_LIST = "data/dataProviders";
	final String DATA_RESOURCE_LIST = "data/dataResources";
	final String DATA_PROVIDER_COUNT = "data/dataProviderCounts";
	
	/**
	 * Display a view of a data provider.
	 * 
	 * @param dataProviderId
	 * @param model
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/data_providers/counts.json}", method = RequestMethod.GET)
	public String getDataProviderCounts(Model model)
	throws Exception {
		List<DataProviderCountDTO> counts = searchDao.getDataProviderCounts();
		model.addAttribute("dataProviders", counts);
		return DATA_PROVIDER_LIST;
	}
	
	/**
	 * Display a view of a data provider.
	 * 
	 * @param dataProviderId
	 * @param model
	 * @return
	 * @throws Exception
	 */
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
	
	/**
	 * Display a view of a data resource.
	 * 
	 * @param dataResourceId
	 * @param model
	 * @return
	 * @throws Exception
	 */
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

	/**
	 * @param searchDao the searchDao to set
	 */
	public void setSearchDao(SearchDao searchDao) {
		this.searchDao = searchDao;
	}

}
