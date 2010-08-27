
package org.ala.biocache.util;

import java.io.OutputStream;
import java.util.Set;
import javax.inject.Inject;
import org.ala.biocache.dao.DataProviderDAO;
import org.ala.biocache.dao.DataResourceDAO;
import org.springframework.stereotype.Component;

/**
 * A Utility class for generating citation information
 * @author Natasha
 */

@Component("citationUtils")
public class CitationUtils {
    @Inject
	protected DataProviderDAO dataProviderDAO;
    @Inject
	protected DataResourceDAO dataResourceDAO;
    public void addCitation(Set<String> keys, OutputStream out) throws Exception{
        for(String key : keys){
            //TODO query the citation service for the correct citation
            //The code below will be removed when the citation service is available
            String value = null;
            if(key.startsWith("dp")){
                value = "Data has been sourced from provider: "+dataProviderDAO.getByUID(key).getName()+"\n";


            }
            else if(key.startsWith("dr"))
                value = "Data has been source from resource: "+ dataResourceDAO.getByUID(key).getName() +"\n";
            if(value != null)
                out.write(value.getBytes());
        }

    }
}
