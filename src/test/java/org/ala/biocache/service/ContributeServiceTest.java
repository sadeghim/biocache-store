package org.ala.biocache.service;

import java.util.Date;

import junit.framework.TestCase;

import org.ala.biocache.dto.Sighting;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ContributeServiceTest extends TestCase {
	Sighting s;
	ApplicationContext ctx;
	ContributeService cs;
	String guid = "" + System.currentTimeMillis();
	 
	protected void setUp(){
		s = new Sighting();
		ctx = new ClassPathXmlApplicationContext(new String[]{"classpath*:applicationContext-webapp.xml"});
		cs = ctx.getBean(ContributeService.class);
		
		s.setLocality("Clunes Ross");
		s.setStateProvince("Australian Capital Territory");
		s.setCollectorName("David Martin");
		s.setScientificName("Macropus rufus");
		s.setVernacularName("Red kangaroo");
		s.setFamily("Macropodidae");
		s.setLatitude(-37.12f);
		s.setLongitude(123.12f);
		s.setCoordinateUncertaintyInMeters(100);
		s.setRank("species");
		s.setTaxonConceptGuid("urn:lsid:biodiversity.org.au:afd.taxon:aa745ff0-c776-4d0e-851d-369ba0e6f537");
		s.setUserId("David.Martin@csiro.au");
		s.setOccurrenceRemarks("It was a cold dark night....");
		s.setEventDate(new Date());				
	}
	
	public void testRecordSighting(){
		s.setGuid(guid);
		assertTrue(cs.recordSighting(s));
	}
	
	public void testupdateSighting(){
		s.setGuid(guid);
		s.setUserId("waiman.mokn@csiro.au");
		s.setLatitude(-100.00f);
		s.setLongitude(100.00f);
		cs.updateSighting(s);
	}
}
