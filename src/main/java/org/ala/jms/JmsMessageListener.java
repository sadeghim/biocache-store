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

package org.ala.jms;

import javax.inject.Inject;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.ala.biocache.dao.RawOccurrenceRecordDAO;
import org.ala.biocache.dao.TaxonConceptDAO;
import org.ala.biocache.service.ContributeService;
import org.ala.biocache.dto.CitizenScience;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Component;

/**
 * 
 * @author mok011
 *
 */

@Component
public class JmsMessageListener implements MessageListener { 

    private static final Logger logger = Logger.getLogger(JmsMessageListener.class);    
    public static final String MESSAGE_METHOD = "messageMethod";

    @Inject
    protected ContributeService contributeService;
    @Inject
    protected TaxonConceptDAO taxonConceptDao;
    
    @Inject
	RawOccurrenceRecordDAO rawOccurrenceRecordDAO;
    
    protected ObjectMapper mapper = new ObjectMapper();
    
    public enum Method {CREATE, UPDATE, DELETE}
    
    public JmsMessageListener(){
	    //initialise the object mapper	
		mapper.getDeserializationConfig().set(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    /**
     * Implementation of <code>MessageListener</code>.
     */
    public void onMessage(Message message) {
    	Method messageMethod = null;
    	String json = null;
    	long occId = -1;
    	
        try {
        	if(message.getStringProperty(MESSAGE_METHOD) != null && !"".equals(message.getStringProperty(MESSAGE_METHOD))){
        		messageMethod = Method.valueOf(message.getStringProperty(MESSAGE_METHOD));	
        	       	 
        		CitizenScience sighting = null;
	            if (message instanceof TextMessage) {
	                TextMessage tm = (TextMessage)message;
	                json = tm.getText();
	                
	                if(json != null && !"".equals(json)){
	                	sighting = (CitizenScience)mapper.readValue(json, CitizenScience.class);	                	
	                	String guid = taxonConceptDao.findLsidByName(sighting.getScientificName());
	                	sighting.setTaxonConceptGuid(guid);
	                	if(guid == null || "".equals(guid)){
	                		logger.error("No guid found!!! ScientificName: " + sighting.getScientificName());
	                		return;
	                	}	                	
	                }
	                else{
	                	logger.error("Empty Json Message!  Method: " + message.getStringProperty(MESSAGE_METHOD));
	                	return;
	                }
	                
	                switch(messageMethod){
	                	case CREATE:
	                		occId = contributeService.recordCitizenScience(sighting);	                		
	                		break;
	                		
	                	case UPDATE:
	                		occId = contributeService.updateCitizenScience(sighting);
	                		break;
	                		
	                	case DELETE:
	                		occId = rawOccurrenceRecordDAO.getOccurrenceIdByGuid(sighting.getGuid());
	                    	if(!contributeService.deleteCitizenScience("" + occId)){
	                    		occId = -1;
	                    	}
	                		break;
	                		
	                	default:
	                		logger.error("Invalid method! Method: " + message.getStringProperty(MESSAGE_METHOD) + ".  json= " + json); 
	                		break;
	                }
	                if(occId < 1){
	                	logger.error("Method = " + message.getStringProperty(MESSAGE_METHOD) + " is failed!!! : Processed message " + json);
	                }
	                logger.debug("Method = " + message.getStringProperty(MESSAGE_METHOD) + " : Processed message " + json);  
	            }
        	}
        	else{
        		logger.error("Invalid method! Method: " + message.getStringProperty(MESSAGE_METHOD));
        	}
        } 
        catch (Exception e) {
        	logger.error("Error Message: " + json + " Method :"  + messageMethod);
            logger.error(e.getMessage(), e);
        }
    }
    
}
