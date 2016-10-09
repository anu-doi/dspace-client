package au.edu.anu.dspace.client.swordv2.data;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Set;

import org.apache.commons.cli.ParseException;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CsvFileParserTest {
	
	private static final Logger log = LoggerFactory.getLogger(CsvFileParserTest.class);
	
	private CsvFileParser csvFileParser;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testCsvParser() throws Exception {
		csvFileParser = createCsvFileParser("Test1.csv");
		List<SwordRequestData> swordRequests = csvFileParser.getSwordRequests();
		logSwordRequests(swordRequests);
		
		assertThat(swordRequests, hasSize(1));
		SwordRequestData row = swordRequests.get(0);
		assertThat(row.getCollectionName(), is("Test Archives Collection"));
		assertThat(row.getMetadata().keySet(), hasItem("dc.type"));
		assertThat(row.getMetadata().get("dc.type"), contains("image", "Test;String"));
	}
	
	@Test
	public void testMultipleValues() throws Exception {
		csvFileParser = createCsvFileParser("TestMultipleValues.csv");
		List<SwordRequestData> swordRequests = csvFileParser.getSwordRequests();
		logSwordRequests(swordRequests);
		
		assertThat(swordRequests, hasSize(1));
		SwordRequestData row = swordRequests.get(0);
		assertThat(row.getCollectionName(), is("Test Archives Collection"));
		assertThat(row.getMetadata().keySet(), containsInAnyOrder("dc-title", "dc-type"));
		assertThat(row.getMetadata().get("dc-type"), contains("first", "second;second", "third"));
	}
	
	@Test
	public void testMultipleHeaders() throws Exception {
		csvFileParser = createCsvFileParser("TestMultipleHeaders.csv");
		List<SwordRequestData> swordRequests = csvFileParser.getSwordRequests();
		logSwordRequests(swordRequests);
		
		assertThat(swordRequests, hasSize(1));
		SwordRequestData row = swordRequests.get(0);
		assertThat(row.getCollectionName(), is("Test Archives Collection"));
		assertThat(row.getMetadata().keySet().size(), is(2));
		assertThat(row.getMetadata().keySet(), containsInAnyOrder("dc-title", "dc-format-extent"));
		List<String> formatExtent = row.getMetadata().get("dc-format-extent");
		assertThat(formatExtent, hasSize(2));
		assertThat(formatExtent, contains("First extent", "Second extent"));
	}
	
	@Test
	public void testEdgeCase() throws Exception {
		csvFileParser = createCsvFileParser("ANU Press backlist sample records 3.csv");
	}
	
	@Test
	public void testCollectionHandle() throws Exception {
		csvFileParser = createCsvFileParser("CollectionHandle.csv");
		List<SwordRequestData> swordRequests = csvFileParser.getSwordRequests();
		assertThat(swordRequests, hasSize(1));
		logSwordRequests(swordRequests);
		SwordRequestData row = swordRequests.get(0);
		assertThat(row.getCollectionName(), is("1885/102456"));
	}
	
	@Test
	public void testMalformedInputException() throws Exception {
		csvFileParser = createCsvFileParser("Songlines.csv");
		List<SwordRequestData> swordRequests = csvFileParser.getSwordRequests();
		logSwordRequests(swordRequests);
		SwordRequestData row = swordRequests.get(0);
		assertThat(row.getCollectionName(), is("123456789/98033"));
		List<String> values = row.getMetadata().get("dc-description-abstract");
		for (String s : values) {
			System.out.println(s);
		}
	}

	private CsvFileParser createCsvFileParser(String filename) throws URISyntaxException, ParseException, IOException {
		URL csvFileUrl = this.getClass().getResource(filename);
		Path csvFile = Paths.get(csvFileUrl.toURI());
		return new CsvFileParser(new String[] { "--csv", csvFile.toString() });
		
	}

	private void logSwordRequests(List<SwordRequestData> swordRequests) {
		for (SwordRequestData sr : swordRequests) {
			log.trace(sr.getMetadata().toString());
		}
	}
}
