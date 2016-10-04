/**
 * 
 */
package au.edu.anu.dspace.client.swordv2.digitisation;

import java.io.IOException;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dspace.client.swordv2.digitisation.iiirecord.IIIRECORD;

/**
 * @author Rahul Khanna
 *
 */
public class AnuCatalogueService {

	private static final Logger log = LoggerFactory.getLogger(AnuCatalogueService.class);
	
	private static final String CATALOGUE_BASE_URL = "http://library.anu.edu.au/xrecord=";

	private JAXBContext jaxbContext;
	private Unmarshaller unmarshaller;
	
	public AnuCatalogueService() throws JAXBException {
		jaxbContext = JAXBContext.newInstance(IIIRECORD.class);
		unmarshaller = jaxbContext.createUnmarshaller();
	}
	
	public IIIRECORD retrieveCatalogueItem(String bNumber) throws JAXBException, IOException {
		String iiiRecordUrl = generateRecordUrl(bNumber);
		URL xmlUrl = new URL(iiiRecordUrl);
		IIIRECORD iiiRec = (IIIRECORD) unmarshaller.unmarshal(xmlUrl);
		log.trace("Retrieved IIIRecord from {}", iiiRecordUrl);
		return iiiRec;
	}

	private String generateRecordUrl(String bNumber) {
		return String.format("%s%s", CATALOGUE_BASE_URL, bNumber);
	}
}
