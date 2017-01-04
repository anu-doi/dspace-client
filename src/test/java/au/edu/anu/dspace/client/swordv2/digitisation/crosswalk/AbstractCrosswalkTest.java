package au.edu.anu.dspace.client.swordv2.digitisation.crosswalk;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.junit.Before;
import org.junit.BeforeClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dspace.client.swordv2.data.SwordMetadata;
import au.edu.anu.dspace.client.swordv2.digitisation.iiirecord.IIIRECORD;

public class AbstractCrosswalkTest {

	private static final Logger log = LoggerFactory.getLogger(AbstractCrosswalkTest.class);
	
	protected static JAXBContext jaxbContext;
	
	protected Marshaller m;
	protected Unmarshaller um;
	protected AbstractCrosswalk cw;

	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		jaxbContext = JAXBContext.newInstance(IIIRECORD.class);
	}

	@Before
	public void setUp() throws Exception {
		m = jaxbContext.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		m.setProperty(Marshaller.JAXB_ENCODING, StandardCharsets.UTF_8.name());
		um = jaxbContext.createUnmarshaller();
	}

	public AbstractCrosswalkTest() {
		super();
	}

	protected SwordMetadata generateMetadata(String bNumber, boolean logXml) throws IOException, JAXBException {
		IIIRECORD iiiRec = getIIIRecord(bNumber);
		if (logXml) {
			logIiiRecordXml(iiiRec);
		}
		return cw.generateMetadata(iiiRec);
	}

	private IIIRECORD getIIIRecord(String bNumber) throws IOException, JAXBException {
		URL xmlUrl = new URL("http://library.anu.edu.au/xrecord=" + bNumber);
		IIIRECORD iiiRec = null;
		iiiRec = (IIIRECORD) um.unmarshal(xmlUrl);
		return iiiRec;
	}

	private void logIiiRecordXml(IIIRECORD iiiRec) throws JAXBException {
		StringWriter xmlStrWriter = new StringWriter();
		m.marshal(iiiRec, xmlStrWriter);
		log.trace("XML Record for {}:{}{}", iiiRec.getRecordInfo().getRECORDKEY().trim(), System.lineSeparator(), xmlStrWriter);
	}

	protected void logParsedMetadata(SwordMetadata metadata) {
		for (Entry<String, List<String>> entry : metadata.entrySet()) {
			log.trace("{} ({}): {}", entry.getKey(), entry.getValue().size(), entry.getValue());
		}
		log.trace("{} fields", metadata.size());
	}

}