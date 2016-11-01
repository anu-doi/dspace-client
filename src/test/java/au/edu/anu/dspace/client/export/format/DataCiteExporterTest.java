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

	private static JAXBContext dataCiteContext;
	private static Marshaller dataCiteMarshaller;
	
	private DataCiteExporter dataCite;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		dataCiteContext = JAXBContext.newInstance(Resource.class);
		dataCiteMarshaller = dataCiteContext.createMarshaller();
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
		
		StringWriter xml = new StringWriter();
		dataCiteMarshaller.marshal(resource, xml);
		log.trace("{}", xml.toString());
		
		assertThat(resource.getCreators().getCreator().get(0).getCreatorName(), is("Kimball, Michael"));
		assertThat(resource.getTitles().getTitle().get(0).getValue(), is("Our heritage is already broken: meditations on a regenerative conservation for cultural and natural heritage"));
	}
	
	@Test
	public void testValidateHer() throws Exception {
		List<MetadataEntry> metadata = readMetadata("item1metadata.json");
		DataCiteExporter dataCite = new DataCiteExporter(metadata, "her");
		dataCite.validate();
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