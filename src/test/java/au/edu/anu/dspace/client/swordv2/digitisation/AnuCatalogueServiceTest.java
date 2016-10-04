/**
 * 
 */
package au.edu.anu.dspace.client.swordv2.digitisation;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

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
public class AnuCatalogueServiceTest {
	
	private static final Logger log = LoggerFactory.getLogger(AnuCatalogueServiceTest.class);
	
	private AnuCatalogueService acs;
	
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
		acs = new AnuCatalogueService();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testRetrieveCatalogueItem() throws Exception {
		final String bNumber = "b3838868";
		IIIRECORD iiiRecord = acs.retrieveCatalogueItem(bNumber);
		assertThat(iiiRecord, is(notNullValue()));
		String bNumberFromRecord = extractBNumber(iiiRecord);
		assertThat(bNumberFromRecord, is(equalTo(bNumber)));
	}

	private String extractBNumber(IIIRECORD iiiRecord) {
		return iiiRecord.getRecordInfo().getRECORDKEY().trim();
	}

}
