/**
 * 
 */
package au.edu.anu.dspace.client.swordv2.digitisation.crosswalk;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dspace.client.swordv2.digitisation.iiirecord.IIIRECORD;

/**
 * @author Rahul Khanna
 *
 */
public class DigitisedThesesCrosswalkTest {

	private static final Logger log = LoggerFactory.getLogger(DigitisedThesesCrosswalkTest.class);
	
	private static JAXBContext jaxbContext;
	private Marshaller m;
	private Unmarshaller um;
	
	private DigitisedThesesCrosswalk cw;
	
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		jaxbContext = JAXBContext.newInstance(IIIRECORD.class);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		m = jaxbContext.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.setProperty(Marshaller.JAXB_ENCODING, StandardCharsets.UTF_8.name());
		um = jaxbContext.createUnmarshaller();
		
		cw = new DigitisedThesesCrosswalk();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() throws Exception {
		final String bNumber = "b1649967";
		IIIRECORD iiiRec = getIIIRecord(bNumber);
		PrintWriter pw = new PrintWriter(System.out, true);
		m.marshal(iiiRec, pw);
		
		Map<String, Set<String>> metadata = cw.generateMetadata(iiiRec);
		
		logParsedMetadata(metadata);
	}

	private IIIRECORD getIIIRecord(String bNumber) throws IOException, JAXBException {
		URL xmlUrl = new URL("http://library.anu.edu.au/xrecord=" + bNumber);
		IIIRECORD iiiRec = null;
		iiiRec = (IIIRECORD) um.unmarshal(xmlUrl);
		return iiiRec;
	}

	private void logParsedMetadata(Map<String, Set<String>> metadata) {
		for (Entry<String, Set<String>> entry : metadata.entrySet()) {
			log.trace("{} x{}: {}", entry.getKey(), entry.getValue().size(), entry.getValue());
		}
		log.trace("{} fields", metadata.size());
	}

}
