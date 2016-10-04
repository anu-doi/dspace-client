/**
 * 
 */
package au.edu.anu.dspace.client.swordv2.digitisation;

import static org.junit.Assert.*;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Rahul Khanna
 *
 */
public class BNumberExtractorTest {
	private static final Logger log = LoggerFactory.getLogger(BNumberExtractorTest.class);
	
	private BNumberExtractor extractor;
	
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
	public void testBNumbersOnly() {
		String fullString = "b1234567";
		extractor = new BNumberExtractor(fullString);
		log.trace("BNumber: {}", extractor.getBNumber());
	}
	
	@Test
	public void testStartsWithBNumber() throws Exception {
		String fullString = "b1234567 test String blah bleh bloh 1234567";
		extractor = new BNumberExtractor(fullString);
		log.trace("BNumber: {}", extractor.getBNumber());
		assertThat(extractor.getBNumber(), Matchers.is("b1234567"));
	}
	
	@Test
	public void testMultipleBNumbers() throws Exception {
		String fullString = "b1234567 b2345678 b2345679";
		extractor = new BNumberExtractor(fullString);
		log.trace("BNumber: {}", extractor.getBNumber());
		assertThat(extractor.getBNumber(), Matchers.is("b1234567"));
	}

	
	@Test
	public void testRealFilename() throws Exception {
		String fullString = "b10685741-The Strategy of General Giap since 1964.pdf";
		extractor = new BNumberExtractor(fullString);
		log.trace("BNumber: {}", extractor.getBNumber());
		assertThat(extractor.getBNumber(), Matchers.is("b1068574"));
	}
	
	@Test
	public void testAbsoluteWinPath() throws Exception {
		String path = "C:\\Some folder\\Some subfolder\\b12345678-Some Title";
		extractor = new BNumberExtractor(path);
		log.trace("BNumber: {}", extractor.getBNumber());
		assertThat(extractor.getBNumber(), Matchers.is("b1234567"));
	}
	
	@Test
	public void testAbsoluteUnixPath() throws Exception {
		String path = "/home/user/Some subfolder/b12345678-Some Title";
		extractor = new BNumberExtractor(path);
		log.trace("BNumber: {}", extractor.getBNumber());
		assertThat(extractor.getBNumber(), Matchers.is("b1234567"));
	}
	

	@Test(expected = IllegalArgumentException.class)
	public void testShortBNumber() throws Exception {
		String fullString = "b123456";
		extractor = new BNumberExtractor(fullString);
	}

	@Test
	public void testLongBNumber() throws Exception {
		String fullString = "b12345678";
		extractor = new BNumberExtractor(fullString);
		log.trace("BNumber: {}", extractor.getBNumber());
		assertThat(extractor.getBNumber(), Matchers.is("b1234567"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCapitalB() throws Exception {
		String fullString = "B1234567-some title";
		extractor = new BNumberExtractor(fullString);
		log.trace("BNumber: {}", extractor.getBNumber());
		assertThat(extractor.getBNumber(), Matchers.is("b1234567"));
	}
	
	@Test
	public void testWithX() throws Exception {
		String fullString = "b1014853x-Marchant.pdf";
		extractor = new BNumberExtractor(fullString);
		log.trace("BNumber: {}", extractor.getBNumber());
		assertThat(extractor.getBNumber(), Matchers.is("b1014853"));
	}
}
