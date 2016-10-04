/**
 * 
 */
package au.edu.anu.dspace.client.swordv2.digitisation;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dspace.client.swordv2.digitisation.FileStatus.Status;

/**
 * @author Rahul Khanna
 *
 */
public class ThesisTrackerTest {
	
	private static final Logger log = LoggerFactory.getLogger(ThesisTrackerTest.class);
	
	private ThesisTracker thesisTracker;

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
	public void testNonExistingTrackerFile() throws Exception {
		Path trackerFile = Files.createTempFile("ThesisTrackerTest", null);
		log.trace("Temp file: {}", trackerFile.toString());
		// delete temp file
		Files.delete(trackerFile);
		thesisTracker = new ThesisTracker(trackerFile);
		Map<String, FileStatus> allTheses = thesisTracker.getAllTheses();
		assertThat(allTheses, Matchers.anEmptyMap());

		thesisTracker.setThesisStatus("b1234567", new FileStatus(Status.UPLOADED, Calendar.getInstance().getTime(),
				10L, "Note"));
		assertThat(Files.isRegularFile(trackerFile), Matchers.is(true));
		assertThat(Files.size(trackerFile), Matchers.greaterThan(0L));
	}

	@Test
	public void testExistingTrackerFile() throws Exception {
		Path trackerFile = Files.createTempFile("ThesisTrackerTest", null);
		log.trace("Temp file: {}", trackerFile.toString());
		try (BufferedWriter writer = Files.newBufferedWriter(trackerFile, StandardCharsets.UTF_8,
				StandardOpenOption.WRITE)) {
			writer.write("b1234567="
					+ new FileStatus(Status.UPLOADED, Calendar.getInstance().getTime(), 10L, "Note 1").toString()
					+ System.lineSeparator());
			writer.write("b1234568="
					+ new FileStatus(Status.UPLOADED, Calendar.getInstance().getTime(), 12L, "Note 2").toString()
					+ System.lineSeparator());
		}

		thesisTracker = new ThesisTracker(trackerFile);
		Map<String, FileStatus> allTheses = thesisTracker.getAllTheses();
		assertThat(allTheses, Matchers.is(Matchers.aMapWithSize(2)));
	}
	
	@Test
	public void testProgressiveWritesToTrackerFile() throws Exception {
		Path trackerFile = Files.createTempFile("ThesisTrackerTest", null);
		log.trace("Temp file: {}", trackerFile.toString());

		thesisTracker = new ThesisTracker(trackerFile);
		final int nEntries = 100;
		for (int i = 0; i < nEntries; i++) { 
			String bNumber = String.format("b%07d", 1234567 + i);
			
			FileStatus fileStatus = new FileStatus(Status.UPLOADED, new Date(), i, String.format("Item %d", i));
			thesisTracker.setThesisStatus(bNumber, fileStatus);
			

			ThesisTracker rereadThesisTracker = new ThesisTracker(trackerFile);
			Map<String, FileStatus> allTheses = rereadThesisTracker.getAllTheses();
			assertThat(allTheses, Matchers.is(Matchers.aMapWithSize(i + 1)));
		}
		
		thesisTracker = new ThesisTracker(trackerFile);
		Map<String, FileStatus> allTheses = thesisTracker.getAllTheses();
		assertThat(allTheses, Matchers.is(Matchers.aMapWithSize(nEntries)));
	}
}
