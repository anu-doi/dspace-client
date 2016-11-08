/**
 * 
 */
package au.edu.anu.dspace.client.export.format;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.hamcrest.core.StringContains;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import au.edu.anu.dspace.client.export.formats.datacite.Resource;
import au.edu.anu.dspace.client.rest.model.MetadataEntry;

/**
 * @author Rahul Khanna
 *
 */
public class DataCiteExporterTest {

	private static final Logger log = LoggerFactory.getLogger(DataCiteExporterTest.class);

	private DataCiteExporter dataCite;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
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
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testExportHer() throws Exception {
		List<MetadataEntry> metadata = readMetadata("item1metadata.json");
		DataCiteExporter dataCite = new DataCiteExporter(metadata, "her");
		Resource resource = dataCite.exportObject();
		String xmlStr = dataCite.exportToString();
		
		log.trace("{}", xmlStr);
		
		assertThat(resource.getCreators().getCreator().get(0).getCreatorName(), is("Kimball, Michael"));
		assertThat(resource.getTitles().getTitle().get(0).getValue(), is("Our heritage is already broken: meditations on a regenerative conservation for cultural and natural heritage"));
		assertThat(resource.getPublisher(), is("ANU Press"));
		
		assertThat(xmlStr, StringContains.containsString("xsi:schemaLocation=\"http://datacite.org/schema/kernel-4 https://schema.datacite.org/meta/kernel-4.0/metadata.xsd\""));
		assertThat(xmlStr, StringContains.containsString("xmlns=\"http://datacite.org/schema/kernel-4\""));
		assertThat(xmlStr, StringContains.containsString("xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""));
	}
	
	@Test
	public void testValidateHer() throws Exception {
		List<MetadataEntry> metadata = readMetadata("item1metadata.json");
		DataCiteExporter dataCite = new DataCiteExporter(metadata, "her");
		List<String> validationMessages = dataCite.validate();
		for (String msg : validationMessages) {
			log.info("Validation: {}", msg);
		}
	}

	private List<MetadataEntry> readMetadata(String filename)
			throws JsonParseException, JsonMappingException, IOException {
		List<MetadataEntry> metadata;
		try (InputStream metadataStream = this.getClass().getResourceAsStream(filename)) {
			ObjectMapper mapper = new ObjectMapper();
			metadata = mapper.readValue(metadataStream, new TypeReference<List<MetadataEntry>>() {
			});
		}
		return metadata;
	}
	
}
