package org.ala.dao;

import java.util.List;

import junit.framework.TestCase;

import org.ala.biocache.dao.SearchDao;
import org.ala.biocache.model.DataProviderCountDTO;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SearchDaoTest extends TestCase {

	public void testSearchDataProviders() throws Exception {
		
		ApplicationContext ac = new ClassPathXmlApplicationContext(new String[]{"classpath:applicationContext-webapp.xml"});
		SearchDao searchDao = (SearchDao) ac.getBean("searchDao");
		List<DataProviderCountDTO> countDTOs = searchDao.getDataProviderCounts();
		
		for(DataProviderCountDTO countDTO: countDTOs){
			System.out.println(countDTO);
		}
	}
	
}
