/**
 * 
 */
package au.edu.anu.dspace.client.swordv2.digitisation.iiirecord;

import static org.junit.Assert.*;

import java.io.File;
import java.io.InputStream;
import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Rahul Khanna
 *
 */
public class IIIRECORDTest {

	private static JAXBContext jaxbContext;
	private Marshaller m;
	private Unmarshaller um;

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
		um = jaxbContext.createUnmarshaller();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testStream() throws Exception {
		try (InputStream xmlStream = this.getClass().getResourceAsStream("IIIRecord1.xml")) {
			IIIRECORD iiiRec = (IIIRECORD) um.unmarshal(xmlStream);
			m.marshal(iiiRec, System.out);
			
		}
	}
	
	@Test
	public void testUrl() throws Exception {
		String bNumber = "b1272260";
		URL xmlUrl = new URL("http://library.anu.edu.au/xrecord=" + bNumber);
		try (InputStream urlStream = xmlUrl.openStream()) {
			IIIRECORD iiiRec = (IIIRECORD) um.unmarshal(urlStream);
			m.marshal(iiiRec, System.out);
		}
	}

}
