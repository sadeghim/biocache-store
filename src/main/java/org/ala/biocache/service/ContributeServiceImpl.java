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
package org.ala.biocache.service;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.ala.biocache.dao.DataProviderDAO;
import org.ala.biocache.dao.DataResourceDAO;
import org.ala.biocache.dao.OccurrenceRecordDAO;
import org.ala.biocache.dao.RawOccurrenceRecordDAO;
import org.ala.biocache.dao.SearchDAO;
import org.ala.biocache.dao.TaxonConceptDAO;
import org.ala.biocache.dto.OccurrenceDTO;
import org.ala.biocache.dto.Sighting;
import org.ala.biocache.model.DataProvider;
import org.ala.biocache.model.DataResource;
import org.ala.biocache.model.OccurrenceRecord;
import org.ala.biocache.model.RawOccurrenceRecord;
import org.ala.biocache.model.TaxonConcept;
import org.ala.biocache.util.SearchUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("contributeService")
public class ContributeServiceImpl implements ContributeService {

	/** Logger initialisation */
	private final static Logger logger = Logger.getLogger(ContributeServiceImpl.class);
	
	protected String citizenScienceDPUID = "dp31";
	protected String citizenScienceDRUID = "dr364";
	
	public final int BASIS_OF_RECORD_OBSERVATION = 1;
	public final String BASIS_OF_RECORD_OBSERVATION_TEXT = "Observation";

	@Inject
	TaxonConceptDAO taxonConceptDAO;
	@Inject
	RawOccurrenceRecordDAO rawOccurrenceRecordDAO;
	@Inject
	OccurrenceRecordDAO occurrenceRecordDAO;
	@Inject
	SearchDAO searchDAO;
	@Inject
	DataProviderDAO dataProviderDAO;
	@Inject
	DataResourceDAO dataResourceDAO;

	
	@Override
	public boolean recordSighting(Sighting s){
		try {
			logger.debug("Recording a sighting for taxon guid: "+s.getTaxonConceptGuid());
			
			//find the matching taxon concept
			TaxonConcept tc = taxonConceptDAO.getByGuid(s.getTaxonConceptGuid());
			
			DataProvider dp = dataProviderDAO.getByUID(citizenScienceDPUID);
			DataResource dr = dataResourceDAO.getByUID(citizenScienceDRUID);

			logger.debug("Using taxon concept: "+tc);
			logger.debug("Using data provider: "+dp);
			logger.debug("Using data resource: "+dr);
			
			//add record to raw occurrence table
			RawOccurrenceRecord ror = new RawOccurrenceRecord();
			ror.setFamily(s.getFamily());
			ror.setScientificName(s.getScientificName());
			ror.setVernacularName(s.getVernacularName());
			ror.setRank(s.getRank());
			if(s.getLatitude()!=null) ror.setLatitude(s.getLatitude().toString());
			if(s.getLongitude()!=null) ror.setLongitude(s.getLongitude().toString());
			//hardcoded properties
			ror.setBasisOfRecord(BASIS_OF_RECORD_OBSERVATION_TEXT);
			ror.setDataProviderId(dp.getId());
			ror.setDataResourceId(dr.getId());
			ror.setCollectorName(s.getCollectorName());
			ror.setUserId(s.getUserId());
			ror.setOccurrenceRemarks(s.getOccurrenceRemarks());
			ror.setIndividualCount(s.getIndividualCount());
			ror.setLocality(s.getLocality());
			ror.setStateOrProvince(s.getStateProvince());
			ror.setCountry(s.getCountry());
			ror.setRank(s.getRank());
			ror.setTaxonConceptGuid(s.getTaxonConceptGuid());
			
			if(s.getCoordinateUncertaintyInMeters()!=null) ror.setLatLongPrecision(s.getCoordinateUncertaintyInMeters().toString());
			
			String day = null;
			String month = null;
			String year = null;
			
			if(s.getEventDate()!=null){
				java.text.SimpleDateFormat df = new java.text.SimpleDateFormat("dd");
				java.text.SimpleDateFormat mf = new java.text.SimpleDateFormat("MM");
				java.text.SimpleDateFormat yf = new java.text.SimpleDateFormat("yyyy");

				day = df.format(s.getEventDate());
				month = mf.format(s.getEventDate());
				year = yf.format(s.getEventDate());
				
				ror.setDay(day);
				ror.setMonth(month);
				ror.setYear(year);
			}
			
			long occurrenceId = rawOccurrenceRecordDAO.create(ror);
			
			logger.debug("Created a raw occurrence with ID: "+occurrenceId);
			
			//update occurrence record
			OccurrenceRecord oc = new OccurrenceRecord();
			oc.setId(occurrenceId);
			oc.setBasisOfRecord(BASIS_OF_RECORD_OBSERVATION);
			oc.setDataProviderId(dp.getId());
			oc.setDataResourceId(dr.getId());
			oc.setLatitude(s.getLatitude());
			oc.setLongitude(s.getLongitude());
			oc.setNubConceptId(tc.getId());
			oc.setIsoCountryCode(s.getCountryCode());
			oc.setOccurrenceDate(s.getEventDate());
			if(s.getEventDate()!=null){
				oc.setMonth(new Integer(month));
				oc.setYear(new Integer(year));
			}
			oc.setTaxonConceptId(tc.getId());
			oc.setTaxonNameId(tc.getTaxonNameId());
			oc.setLeft(tc.getLeft());
			occurrenceRecordDAO.create(oc);
			
			logger.debug("Created a occurrence with ID: "+occurrenceId);

			//update search indexes			
			OccurrenceDTO ocdto = new OccurrenceDTO();
			ocdto.setId(Long.toString(oc.getId()));
			ocdto.setFamily(s.getFamily());
			ocdto.setCommonName(s.getVernacularName());
			ocdto.setRawTaxonName(tc.getScientificName());
			ocdto.setTaxonConceptLsid(s.getTaxonConceptGuid());
			ocdto.setBasisOfRecordId(BASIS_OF_RECORD_OBSERVATION);
			ocdto.setBasisOfRecord(BASIS_OF_RECORD_OBSERVATION_TEXT);
			ocdto.setDataProviderUid(citizenScienceDPUID);
			ocdto.setDataResourceUid(citizenScienceDRUID);
			
			ocdto.setDataProvider(dp.getName());
			ocdto.setDataResource(dr.getName());
			
            if (s.getLatitude()!=null && s.getLongitude()!=null) {
                ocdto.setLatitude(s.getLatitude().doubleValue());
                ocdto.setLongitude(s.getLongitude().doubleValue());
                ocdto.setLatLong(s.getLatitude().toString()+","+s.getLongitude().toString());
                logger.info("latLong = "+ocdto.getLatLong());
            }
            
			if(s.getCoordinateUncertaintyInMeters()!=null) ocdto.setCoordinatePrecision(s.getCoordinateUncertaintyInMeters().toString());
			
			ocdto.setLeft(tc.getLeft());
			ocdto.setRight(tc.getRight());
			ocdto.setUserId(s.getUserId());
			ocdto.setCollector(s.getCollectorName());
			ocdto.setMonth(month);
			ocdto.setYear(year);
			ocdto.setRank(s.getRank());
			ocdto.setRankId(tc.getRank());
			ocdto.setOccurrenceDate(s.getEventDate());
			
			ocdto.setKingdom(tc.getKingdom());
			ocdto.setKingdomLsid(tc.getKingdomGuid());
			
			ocdto.setPhylum(tc.getPhylum());
			ocdto.setPhylumLsid(tc.getPhylumGuid());
			
			ocdto.setClazz(tc.getTheClass());
			ocdto.setClassLsid(tc.getTheClassGuid());
			
			ocdto.setOrder(tc.getOrder());
			ocdto.setOrderLsid(tc.getOrderGuid());
			
			ocdto.setFamily(tc.getFamily());
			ocdto.setFamilyLsid(tc.getFamilyGuid());
			
			ocdto.setGenus(tc.getGenus());
			ocdto.setGenusLsid(tc.getGenusGuid());
			
			//names_and_lsid
			ocdto.setNamesLsid(s.getScientificName()+"|"+s.getTaxonConceptGuid()+"|"+s.getVernacularName()+"|"+s.getKingdom()+"|"+s.getFamily());
			
			ocdto.setConfidence(dr.getConfidence());
			
			SearchUtils.initialPointValues(ocdto);
			
			List<String> states = new ArrayList<String>();
			//add states and provinces
			if(StringUtils.isNotEmpty(s.getStateProvince())){
				states.add(s.getStateProvince());
				ocdto.setStates(states);
			} else {
				states.add("Unknown");
				ocdto.setStates(states);
			}
			
			logger.debug("Added to index: "+ocdto);
			searchDAO.addOccurrence(ocdto);
			logger.debug("Added record with ID: "+occurrenceId+" to search indexes.");
			
			return true;
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return false;
		}
	}

	@Override
	public boolean updateSighting(Sighting sighting) {

		//update record in raw occurrence table
		
		
		
		return false;
	}
	
	@Override
	public boolean deleteSighting(String sightingId) {

		//update record in raw occurrence table
		
		
		
		return false;
	}

	/**
	 * @param searchDAO the searchDAO to set
	 */
	public void setSearchDAO(SearchDAO searchDAO) {
		this.searchDAO = searchDAO;
	}

	/**
	 * @return the taxonConceptDAO
	 */
	public TaxonConceptDAO getTaxonConceptDAO() {
		return taxonConceptDAO;
	}

	/**
	 * @param taxonConceptDAO the taxonConceptDAO to set
	 */
	public void setTaxonConceptDAO(TaxonConceptDAO taxonConceptDAO) {
		this.taxonConceptDAO = taxonConceptDAO;
	}

	/**
	 * @param dataProviderDAO the dataProviderDAO to set
	 */
	public void setDataProviderDAO(DataProviderDAO dataProviderDAO) {
		this.dataProviderDAO = dataProviderDAO;
	}

	/**
	 * @param dataResourceDAO the dataResourceDAO to set
	 */
	public void setDataResourceDAO(DataResourceDAO dataResourceDAO) {
		this.dataResourceDAO = dataResourceDAO;
	}
}
