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
import java.util.Calendar;
import java.util.List;
import java.util.logging.Level;

import javax.activation.MimetypesFileTypeMap;
import javax.inject.Inject;

import org.ala.biocache.dao.DataProviderDAO;
import org.ala.biocache.dao.DataResourceDAO;
import org.ala.biocache.dao.ImageRecordDAO;
import org.ala.biocache.dao.OccurrenceRecordDAO;
import org.ala.biocache.dao.RawOccurrenceRecordDAO;
import org.ala.biocache.dao.SearchDAO;
import org.ala.biocache.dao.TaxonConceptDAO;
import org.ala.biocache.dto.CitizenScience;
import org.ala.biocache.dto.OccurrenceDTO;
import org.ala.biocache.dto.Sighting;
import org.ala.biocache.model.DataProvider;
import org.ala.biocache.model.DataResource;
import org.ala.biocache.model.ImageRecord;
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
	public final String BASIS_OF_RECORD_OBSERVATION_TEXT = "observation";

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
	@Inject
	protected ImageRecordDAO imageRecordDAO;
	
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
			ror.setGuid(s.getGuid());
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
			ror.setEventTime(s.getEventTime());			
			long occurrenceId = rawOccurrenceRecordDAO.create(ror);

			if(occurrenceId >0){
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
                }
                else
                    return false;
			
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
			return false;
		}
	}
	
	@Override
	public boolean deleteSighting(String sightingId) {
        
        try {
            // delete record from search index
            searchDAO.deleteOccurrence(sightingId);
            // delete from occurence record table
            occurrenceRecordDAO.deleteById(Long.parseLong(sightingId));
            // delete from rawOccurence record table
            rawOccurrenceRecordDAO.deleteById(Long.parseLong(sightingId));
        } catch (Exception ex) {
            logger.error("Failed to delete occurrence (id: "+sightingId+"): "+ex.getMessage(), ex);
            return false;
        }
        
		return true;
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
	
	/* ============= <implement update sighting function Date: 8 Dec 2010> ===================*/
	/**
	 * see ala-portal-modifications.sql file.
	 * 
	 * add index into MYSQL raw_occurrence_record table. 
	 * MYSQL statement :
	 * alter table raw_occurrence_record add index ix_ror_guid(guid);
	 */
	
	private OccurrenceDTO populateOccurrenceDTO(Sighting s, OccurrenceRecord oc, TaxonConcept tc,
			DataProvider dp, DataResource dr){
		return populateOccurrenceDTO(-1, s, oc, tc, dp, dr);
	}
	
	/* copied from recordSighting function */
	private OccurrenceDTO populateOccurrenceDTO(long occurrenceId, Sighting s, OccurrenceRecord oc, TaxonConcept tc,
			DataProvider dp, DataResource dr){
		OccurrenceDTO ocdto = null;
		
		try{
			if(occurrenceId > 0){
				ocdto = searchDAO.getById("" + occurrenceId);
			}
			if(ocdto == null){
				ocdto = new OccurrenceDTO();
			}
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
			if(s.getEventDate()!=null){
				Calendar cal=Calendar.getInstance();
				cal.setTime(s.getEventDate());
				ocdto.setMonth("" + (cal.get(Calendar.MONTH) + 1));
				ocdto.setYear("" + cal.get(Calendar.YEAR));
			}		
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
		}
		catch(Exception e){
			logger.error(e);
			ocdto = null;
		}

		return ocdto;
	}
	
	private RawOccurrenceRecord populateRawOccurrenceRecord(Sighting s, DataProvider dp, DataResource dr){
		return populateRawOccurrenceRecord(-1, s, dp, dr);
	}
	
	/* copied from recordSighting function */
	private RawOccurrenceRecord populateRawOccurrenceRecord(long occurrenceId, Sighting s, DataProvider dp, DataResource dr){
		RawOccurrenceRecord ror = null;
		
		try{
			if(occurrenceId > 0){
				ror = rawOccurrenceRecordDAO.getById(occurrenceId);
			}			
			if(ror == null){
				ror = new RawOccurrenceRecord();
			}
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
			ror.setGuid(s.getGuid());
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
			ror.setEventTime(s.getEventTime());	
		}
		catch(Exception e){
			logger.error(e);
			ror = null;
		}
		return ror;
	}

	private OccurrenceRecord populateOccurrenceRecord(Sighting s, TaxonConcept tc, 
			DataProvider dp, DataResource dr){
		return populateOccurrenceRecord(-1, s, tc, dp, dr);
	}
	
	/* copied from recordSighting function */
	private OccurrenceRecord populateOccurrenceRecord(long occurrenceId, Sighting s, TaxonConcept tc, 
			DataProvider dp, DataResource dr){
		OccurrenceRecord oc = null;
		try{
			if(occurrenceId > 0){
				oc = occurrenceRecordDAO.getById(occurrenceId);
			}
			if(oc == null){
				oc = new OccurrenceRecord();
			}
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
				Calendar cal=Calendar.getInstance();
				cal.setTime(s.getEventDate());
				oc.setMonth(cal.get(Calendar.MONTH) + 1);
				oc.setYear(cal.get(Calendar.YEAR));
			}
			oc.setTaxonConceptId(tc.getId());
			oc.setTaxonNameId(tc.getTaxonNameId());
			oc.setLeft(tc.getLeft());
		}
		catch(Exception e){
			logger.error(e);
			oc = null;
		}
		return oc;
	}
	
	@Override
	public long updateSighting(Sighting sighting) {
		long occurrenceId = -1;
		long success = -1;
		
		//update record in raw occurrence table
		logger.debug("Recording a sighting for taxon guid: "+sighting.getTaxonConceptGuid());
		
		//find the matching taxon concept
		if(sighting.getTaxonConceptGuid() == null || "".equals(sighting.getTaxonConceptGuid())){
			return success;
		}		
		TaxonConcept tc = taxonConceptDAO.getByGuid(sighting.getTaxonConceptGuid());
		
		DataProvider dp = dataProviderDAO.getByUID(citizenScienceDPUID);
		DataResource dr = dataResourceDAO.getByUID(citizenScienceDRUID);

		logger.debug("Using taxon concept: "+tc);
		logger.debug("Using data provider: "+dp);
		logger.debug("Using data resource: "+dr);
		
		//update record to raw occurrence table
		if(sighting.getGuid() != null){
			long occId = rawOccurrenceRecordDAO.getOccurrenceIdByGuid(sighting.getGuid());
			if(occId > 0){
				RawOccurrenceRecord ror = populateRawOccurrenceRecord(occId, sighting, dp, dr);
				if(ror != null){
					ror.setId(occId);
					occurrenceId = rawOccurrenceRecordDAO.updateOrCreate(ror);		
					logger.debug("updated a rawOccurrenceRecord with ID: "+occurrenceId);
				}
				else{
					logger.info("populateRawOccurrenceRecord is failed! " + sighting);
				}
			}
			else{
				logger.info("getOccurrenceIdByGuid is failed! sighting.getGuid(): " + sighting.getGuid());
			}
		}
				
		if(occurrenceId > 0){
			//update record to occurrence table
			OccurrenceRecord oc = populateOccurrenceRecord(occurrenceId, sighting, tc, dp, dr);
			if(oc != null){
				occurrenceRecordDAO.update(oc);
				logger.debug("updated  occurrenceRecord: " + oc);
			
				//update record to SOLR
				OccurrenceDTO ocdto = populateOccurrenceDTO(occurrenceId, sighting, oc, tc, dp, dr);
				logger.debug("Added to index: "+ocdto);
				try {
					if(ocdto != null){
						searchDAO.updateOccurrence(ocdto);
						success = occurrenceId;
						logger.debug("Added record with ID: "+occurrenceId+" to search indexes.");
					}
				} 
				catch (Exception e) {
					logger.error(e);
					success = -1;
				}
			}
			else{
				logger.info("populateOccurrenceRecord is failed! " + sighting);
			}
		}
		else{
			logger.info("updateSighting failed! occurrenceId: "+occurrenceId + " sighting.getGuid(): " + sighting.getGuid());
		}
		return success;
	}

	@Override
	public long recordCitizenScience(CitizenScience sighting) {		
		long result = -1;
		
		try {
			logger.debug("Recording a sighting for taxon guid: "+sighting.getTaxonConceptGuid());
			if(sighting.getTaxonConceptGuid() == null || "".equals(sighting.getTaxonConceptGuid())){
				return -1;
			}		
			
			//find the matching taxon concept
			TaxonConcept tc = taxonConceptDAO.getByGuid(sighting.getTaxonConceptGuid());
			
			DataProvider dp = dataProviderDAO.getByUID(citizenScienceDPUID);
			DataResource dr = dataResourceDAO.getByUID(citizenScienceDRUID);

			logger.debug("Using taxon concept: "+tc);
			logger.debug("Using data provider: "+dp);
			logger.debug("Using data resource: "+dr);
			
			//add record to raw occurrence table
			RawOccurrenceRecord ror = populateRawOccurrenceRecord(sighting, dp, dr);
			long occurrenceId = rawOccurrenceRecordDAO.create(ror);

			if(occurrenceId >0){
				logger.debug("Created a raw occurrence with ID: "+occurrenceId);
				
				//update occurrence record
				OccurrenceRecord oc = populateOccurrenceRecord(occurrenceId, sighting, tc, dp, dr);
				occurrenceRecordDAO.create(oc);
				
				logger.debug("Created a occurrence with ID: "+occurrenceId);
		
				//update search indexes			
				OccurrenceDTO ocdto = populateOccurrenceDTO(sighting, oc, tc, dp, dr);
				searchDAO.addOccurrence(ocdto);
				logger.debug("Added record with ID: "+occurrenceId+" to search indexes.");
				
				// add image record
				ImageRecord imageRecord = new ImageRecord();
				imageRecord.setOccurrenceId(occurrenceId);
				imageRecord.setDataResourceId(dr.getId());
				for(String url: sighting.getAssociatedMedia()){
					imageRecord.setUrl(url);
					imageRecord.setRawImageType(new MimetypesFileTypeMap().getContentType(url));
					imageRecordDAO.create(imageRecord);
				}

				result = occurrenceId;
			}
		} 
		catch (Exception e) {
			logger.error(e.getMessage(),e);
			result = -1;
		}		
		return result;
	}

	@Override
	public long updateCitizenScience(CitizenScience sighting) {
		long occId = this.updateSighting(sighting);
		
		// delete & create for update image record.
		if(occId > 0){
			List<ImageRecord> l = imageRecordDAO.findByOccurrenceId(occId);
			for(ImageRecord imageRecord : l){
				imageRecordDAO.delete(imageRecord);
			}
			
			// add image record
			DataResource dr = dataResourceDAO.getByUID(citizenScienceDRUID);
			ImageRecord imageRecord = new ImageRecord();
			imageRecord.setOccurrenceId(occId);
			imageRecord.setDataResourceId(dr.getId());
			for(String url: sighting.getAssociatedMedia()){
				imageRecord.setUrl(url);
				imageRecord.setRawImageType(new MimetypesFileTypeMap().getContentType(url));
				imageRecordDAO.create(imageRecord);
			}
		}
		return occId;
	}

	@Override
	public boolean deleteCitizenScience(String sightingId) {
		boolean result =  this.deleteSighting(sightingId);
		if(result){
			List<ImageRecord> l = imageRecordDAO.findByOccurrenceId(Long.valueOf(sightingId));
			for(ImageRecord imageRecord : l){
				imageRecordDAO.delete(imageRecord);
			}
		}
		return result;
	}	
}
