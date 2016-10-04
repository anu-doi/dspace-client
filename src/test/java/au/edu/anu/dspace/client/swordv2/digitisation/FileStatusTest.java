/**
 * 
 */
package au.edu.anu.dspace.client.swordv2.digitisation;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

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
public class FileStatusTest {
	
	private static final Logger log = LoggerFactory.getLogger(FileStatusTest.class);
	
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
	public void testFileStatusToStringAndBack() throws Exception {
		FileStatus fs1 = new FileStatus(Status.UPLOADED, Calendar.getInstance().getTime(), 1024L, "Test String");
		log.trace("FileStatus: {}", fs1.toString());
		FileStatus fs2 = new FileStatus(fs1.toString());
		log.trace("FileStatus: {}", fs2.toString());
		assertThat(fs2.getStatus(), Matchers.is(fs1.getStatus()));
		assertThat(fs2.getDate().toString(), Matchers.is(fs1.getDate().toString()));
		assertThat(fs2.getSize(), Matchers.is(fs1.getSize()));
		assertThat(fs2.getNote(), Matchers.is(fs1.getNote()));
	}

	@Test
	public void testFileStatusNullNote() throws Exception {
		FileStatus fs = new FileStatus("UPLOADED;2015-06-26T11:45:59+1000;10;");
		log.trace("FileStatus: {}", fs.toString());
		assertThat(fs.getNote(), Matchers.is(Matchers.emptyString()));
	}
}
