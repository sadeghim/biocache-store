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

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * 
 * @author mok011
 *
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/activemq-producer_context.xml"})
public class JmsMessageProducer {

    private static final Logger logger = Logger.getLogger(JmsMessageProducer.class);

    @Autowired
    private JmsTemplate template = null;
    private static long myGuid = System.currentTimeMillis();
    
    private String getJson(String guid){
    	StringBuffer json;

// json message example:    	
/*
	{
       "guid" : "urn: lsid: ala: sighitng: 123",
       "scientificName" : "Aus bus",
       "vernacularName" : "Aussie blue",
       "locality" : "25 Smith Street",
       "decimalLatitude" : "-37",
       "decimalLongitude" : "125",
       "associatedMedia" : [
           "http: //citscience.ala.org.au/1232.jpg",
           "http: //citscience.ala.org.au/1233.jpg"
       ]
   	}
*/
    	json = new StringBuffer();
    	json.append("{");
    	json.append("\"guid\" : \"" + guid + "\",");
    	json.append("\"scientificName\" : \"Macropus rufus\",");
    	json.append("\"vernacularName\" : \"Red Kangaroo\",");
    	json.append("\"locality\" : \"25 Smith Street\",");
    	json.append("\"decimalLatitude\" : \"-37\",");
    	json.append("\"decimalLongitude\" : \"125\",");
    	json.append("\"associatedMedia\" : [");
    	json.append("\"http: //citscience.ala.org.au/1232.jpg\",");
    	json.append("\"http: //citscience.ala.org.au/1233.jpg\"");
    	json.append("]");
    	json.append("}");
    	
    	return json.toString();
    }    	       
    	   
    /**
     * Generates JMS messages
     */
    @Test
    public void generateCreateMessage() throws JMSException {
        template.send(new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
            	String json = getJson("" + myGuid);
                TextMessage message = session.createTextMessage(json);
            	
                message.setStringProperty(JmsMessageListener.MESSAGE_METHOD, JmsMessageListener.Method.CREATE.toString());                
                logger.debug("B Sending message: " + message.getStringProperty(JmsMessageListener.MESSAGE_METHOD) + " == " + json);
                
                return message;
            }
        });
    }
    
    @Test
    public void generateUpdateMessage() throws JMSException {
        template.send(new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
            	String json = getJson("" + myGuid);
                TextMessage message = session.createTextMessage(json);
            	
                message.setStringProperty(JmsMessageListener.MESSAGE_METHOD, JmsMessageListener.Method.UPDATE.toString());
                
                logger.debug("B Sending message: " + message.getStringProperty(JmsMessageListener.MESSAGE_METHOD) + " == " + json);
                
                return message;
            }
        });
    }
    
    @Test
    public void generateDeleteMessage() throws JMSException {
        template.send(new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
            	String json = getJson("" + myGuid);
                TextMessage message = session.createTextMessage(json);
            	
                message.setStringProperty(JmsMessageListener.MESSAGE_METHOD, JmsMessageListener.Method.DELETE.toString());
                
                logger.debug("B Sending message: " + message.getStringProperty(JmsMessageListener.MESSAGE_METHOD) + " == " + json);
                
                return message;
            }
        });
    }
    
    @Test
    public void generateInvalidMethod() throws JMSException {
    	template.send(new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
            	String json = getJson("" + myGuid);
                TextMessage message = session.createTextMessage(json);
            	
                message.setStringProperty(JmsMessageListener.MESSAGE_METHOD, "");
                
                logger.debug("B Sending message: " + message.getStringProperty(JmsMessageListener.MESSAGE_METHOD) + " == " + json);
                
                return message;
            }
        });
    }
    
    @Test
    public void generateInvalidMessage() throws JMSException {
    	template.send(new MessageCreator() {
            public Message createMessage(Session session) throws JMSException {
            	String json = "";
                TextMessage message = session.createTextMessage(json);
            	
                message.setStringProperty(JmsMessageListener.MESSAGE_METHOD, JmsMessageListener.Method.CREATE.toString());
                
                logger.debug("B Sending message: " + message.getStringProperty(JmsMessageListener.MESSAGE_METHOD) + " == " + json);
                
                return message;
            }
        });
    }    
}