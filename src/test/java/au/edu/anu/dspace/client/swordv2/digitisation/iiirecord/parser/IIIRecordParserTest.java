/**
 * 
 */
package au.edu.anu.dspace.client.swordv2.digitisation.iiirecord.parser;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamReader;

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
public class IIIRecordParserTest {
	private static final Logger log = LoggerFactory.getLogger(IIIRecordParserTest.class);
	
	private static JAXBContext jaxbContext;
	private Marshaller m;
	private Unmarshaller um;

	private IIIRecordParser parser;
	
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
		m.setProperty(Marshaller.JAXB_ENCODING, "Unicode");
		um = jaxbContext.createUnmarshaller();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSingle() throws Exception {
		final String bNumber = "b3732826";
		IIIRECORD iiiRec = getIIIRecord(bNumber);
		PrintWriter pw = new PrintWriter(System.out, true);
		m.marshal(iiiRec, pw);
		
		parser = new IIIRecordParser(iiiRec);
		logParsedRecord(parser);
	}

	@Test
	public void testMultiple() throws Exception {
		List<String> bNumbers = Arrays.asList("b3579067", "b3600203", "b1746074", "b3838868");
		
		for (String bNumber : bNumbers) {
			IIIRECORD iiiRecord = getIIIRecord(bNumber);
			parser = new IIIRecordParser(iiiRecord);
			logParsedRecord(parser);
			log.trace("----------");
		}
	}
	
	private IIIRECORD getIIIRecord(String bNumber) throws IOException, JAXBException {
		URL xmlUrl = new URL("http://library.anu.edu.au/xrecord=" + bNumber);
		IIIRECORD iiiRec = null;
		iiiRec = (IIIRECORD) um.unmarshal(xmlUrl);
		return iiiRec;
	}

	private void logParsedRecord(IIIRecordParser parser) {
		log.trace("BNumber: {}", parser.getBNumber());
		log.trace("Titles {}: {}", parser.getTitles().size(), parser.getTitles());
		log.trace("Dates Published {}: {}", parser.getDatesPublished().size(), parser.getDatesPublished());
		log.trace("Publishers {}: {}", parser.getPublishers().size(), parser.getPublishers());
		log.trace("Authors {}: {}", parser.getAuthors().size(), parser.getAuthors());
		log.trace("Formats {}: {}", parser.getDescriptions().size(), parser.getDescriptions());
		log.trace("Subjects {}: {}", parser.getSubjects().size(), parser.getSubjects());
		log.trace("Dissertation Notes {}: {}", parser.getDissertationNotes().size(), parser.getDissertationNotes());
		log.trace("Supervisors {}: {}", parser.getSupervisors().size(), parser.getSupervisors());
		log.trace("Colleges {}: {}", parser.getColleges().size(), parser.getColleges());
		log.trace("Summaries {}: {}", parser.getSummaries().size(), parser.getSummaries());
	}
}
