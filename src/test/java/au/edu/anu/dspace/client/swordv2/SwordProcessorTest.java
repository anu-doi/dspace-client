package au.edu.anu.dspace.client.swordv2;

import static org.junit.Assert.*;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.swordapp.client.SWORDClient;

import au.edu.anu.dspace.client.WorkflowException;
import au.edu.anu.dspace.client.swordv2.data.BitstreamInfo;
import au.edu.anu.dspace.client.swordv2.data.SwordRequestData;
import au.edu.anu.dspace.client.swordv2.data.SwordRequestDataProvider;

public class SwordProcessorTest {

	private static final Logger log = LoggerFactory.getLogger(SwordProcessorTest.class);

	private SwordProcessor swordProcessor;

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
	public void testCreateRecord() throws Exception {
		SWORDClient swordClient = new SWORDClient();
		SwordServerInfo serverInfo = new SwordServerInfo("http://localhost:8080/swordv2/servicedocument",
				"abc@abc.com", "abc123");

		Map<String, Set<String>> metadata;
		Set<BitstreamInfo> bitstreams;

		// init metadata
		metadata = new HashMap<>();
		metadata.put("dc.title", new HashSet<>(Arrays.asList("My Title " + new Date().toString())));
		metadata.put("description", new HashSet<>(Arrays.asList("Custom Description")));
		metadata.put("abstract", new HashSet<>(Arrays.asList("My Abstract")));
		metadata.put("anu.anufield", new HashSet<>(Arrays.asList("Custom ID")));

		// init bitstreams
		bitstreams = new HashSet<BitstreamInfo>();
		Path filepath = Files.createTempFile("TempBitstream", null);
		Files.write(filepath, "Test String".getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
		BitstreamInfo bi = new BitstreamInfo(filepath);
		bitstreams.add(bi);

		final SwordRequestData swordData = new SwordRequestData("Test Collection", metadata, null, bitstreams, true);

		SwordRequestDataProvider provider = new SwordRequestDataProvider() {

			@Override
			public List<SwordRequestData> getSwordRequests() {
				return Arrays.asList(swordData);
			}

			@Override
			public void updateRequestStatus(SwordRequestData data, boolean isSuccess) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public boolean isDryRun() {
				return false;
			}
			
		};

		swordProcessor = new SwordProcessor(swordClient, serverInfo, provider);
		swordProcessor.process();
	}

	@Test(expected = WorkflowException.class)
	public void testCreateRecordInNonExistentCollection() throws Exception {
		SWORDClient swordClient = new SWORDClient();
		SwordServerInfo serverInfo = new SwordServerInfo("http://localhost:8080/swordv2/servicedocument",
				"abc@abc.com", "abc123");

		Map<String, Set<String>> metadata;
		Set<BitstreamInfo> bitstreams;

		// init metadata
		metadata = new HashMap<>();
		metadata.put("dc.title", new HashSet<>(Arrays.asList("My Title " + new Date().toString())));
		metadata.put("description", new HashSet<>(Arrays.asList("Custom Description")));
		metadata.put("abstract", new HashSet<>(Arrays.asList("My Abstract")));
		metadata.put("anu.anufield", new HashSet<>(Arrays.asList("Custom ID")));

		// init bitstreams
		bitstreams = new HashSet<BitstreamInfo>();
		Path filepath = Files.createTempFile("TempBitstream", null);
		Files.write(filepath, "Test String".getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
		BitstreamInfo bi = new BitstreamInfo(filepath);
		bitstreams.add(bi);

		final SwordRequestData swordData = new SwordRequestData("Non existent collection", metadata, null, bitstreams,
				true);

		SwordRequestDataProvider provider = new SwordRequestDataProvider() {

			@Override
			public List<SwordRequestData> getSwordRequests() {
				return Arrays.asList(swordData);
			}

			@Override
			public void updateRequestStatus(SwordRequestData data, boolean isSuccess) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public boolean isDryRun() {
				return false;
			}

		};

		swordProcessor = new SwordProcessor(swordClient, serverInfo, provider);
		swordProcessor.process();
	}
}
