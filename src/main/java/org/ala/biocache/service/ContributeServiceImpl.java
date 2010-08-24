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

import org.ala.biocache.dao.OccurrenceRecordDAO;
import org.ala.biocache.dao.RawOccurrenceRecordDAO;
import org.ala.biocache.dao.SearchDAO;
import org.ala.biocache.dao.TaxonConceptDAO;
import org.ala.biocache.dto.OccurrenceDTO;
import org.ala.biocache.dto.Sighting;
import org.ala.biocache.model.OccurrenceRecord;
import org.ala.biocache.model.RawOccurrenceRecord;
import org.ala.biocache.model.TaxonConcept;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

@Component("contributeService")
public class ContributeServiceImpl implements ContributeService {

	/** Logger initialisation */
	private final static Logger logger = Logger.getLogger(ContributeServiceImpl.class);
	
	protected Long citizenScienceDPId = 999l;
	protected Long citizenScienceDRId = 9999l;
	
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
	
	@Override
	public boolean recordSighting(Sighting s){
		try {
			logger.debug("Recording a sighting for taxon guid: "+s.getTaxonConceptGuid());
			
			//find the matching taxon concept
			TaxonConcept tc = taxonConceptDAO.getByGuid(s.getTaxonConceptGuid());
			
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
			ror.setDataProviderId(citizenScienceDPId);
			ror.setDataResourceId(citizenScienceDRId);
			ror.setCollectorName(s.getCollectorName());
			ror.setUserId(s.getUserId());
			ror.setCountry(s.getCountry());
			ror.setOccurrenceRemarks(s.getOccurrenceRemarks());
			
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
			oc.setDataProviderId(citizenScienceDPId);
			oc.setDataResourceId(citizenScienceDRId);
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
			ocdto.setRawTaxonName(s.getScientificName());
			ocdto.setTaxonConceptLsid(s.getTaxonConceptGuid());
			ocdto.setBasisOfRecordId(BASIS_OF_RECORD_OBSERVATION);
			ocdto.setBasisOfRecord(BASIS_OF_RECORD_OBSERVATION_TEXT);
			ocdto.setDataProviderId(citizenScienceDPId.intValue());
			ocdto.setDataResourceId(citizenScienceDRId.intValue());
			ocdto.setDataProvider("Contributed sightings");
			ocdto.setDataResource("Contributed sightings");
			
			if(s.getLatitude()!=null)  ocdto.setLatitude(s.getLatitude().doubleValue());
			if(s.getLongitude()!=null) ocdto.setLongitude(s.getLongitude().doubleValue());
			if(s.getCoordinateUncertaintyInMeters()!=null) ocdto.setCoordinatePrecision(s.getCoordinateUncertaintyInMeters().toString());
			
			ocdto.setLeft(tc.getLeft());
			ocdto.setUserId(s.getUserId());
			ocdto.setCollector(s.getCollectorName());
			ocdto.setMonth(month);
			ocdto.setYear(year);
			ocdto.setRank(s.getRank());
			
			if(s.getCoordinateUncertaintyInMeters()!=null) ocdto.setCoordinatePrecision(s.getCoordinateUncertaintyInMeters().toString());
			//add states and provinces
			if(s.getStateProvince()!=null){
				List<String> states = new ArrayList<String>();
				states.add(s.getStateProvince());
				ocdto.setStates(states);
			}
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
}
