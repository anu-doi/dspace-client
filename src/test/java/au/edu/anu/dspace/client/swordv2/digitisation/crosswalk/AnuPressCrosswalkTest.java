/**
 * 
 */
package au.edu.anu.dspace.client.swordv2.digitisation.crosswalk;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasKey;
import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dspace.client.swordv2.data.SwordMetadata;

/**
 * @author Rahul Khanna
 *
 */
public class AnuPressCrosswalkTest extends AbstractCrosswalkTest {
	
	private static final Logger log = LoggerFactory.getLogger(AnuPressCrosswalkTest.class);
	

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		super.setUp();
		cw = new AnuPressCrosswalk();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAllFields() throws Exception {
		SwordMetadata metadata = generateMetadata("b1122035", true);
		logParsedMetadata(metadata);
		
		assertThat(metadata, hasKey(SwordMetadata.DC_CONTRIBUTOR_AUTHOR));
		assertThat(metadata.get(SwordMetadata.DC_CONTRIBUTOR_AUTHOR), contains("Sawer, Marian"));

		assertThat(metadata, hasKey(SwordMetadata.DC_DATE_COPYRIGHT));
		assertThat(metadata.get(SwordMetadata.DC_DATE_COPYRIGHT), contains("1984"));
		
		assertThat(metadata, hasKey(SwordMetadata.DC_DATE_ISSUED));
		assertThat(metadata.get(SwordMetadata.DC_DATE_ISSUED), contains("1984"));
		
		assertThat(metadata, hasKey(SwordMetadata.DC_DESCRIPTION_TABLEOFCONTENTS));
		assertThat(metadata.get(SwordMetadata.DC_DESCRIPTION_TABLEOFCONTENTS), contains("Includes Employment of women at the Australian National University / report of the Working Party of the Association of Women Employees at the Australian National University to the Vice-Chancellor"));
		
		assertThat(metadata, hasKey(SwordMetadata.DC_FORMAT_EXTENT));
		assertThat(metadata.get(SwordMetadata.DC_FORMAT_EXTENT), contains("xxxiii, 142, xvii, 100 p."));
		
		assertThat(metadata, hasKey(SwordMetadata.DC_IDENTIFIER_OTHER));
		assertThat(metadata.get(SwordMetadata.DC_IDENTIFIER_OTHER), contains("b1122035"));

		assertThat(metadata, hasKey(SwordMetadata.DC_PUBLISHER));
		assertThat(metadata.get(SwordMetadata.DC_PUBLISHER), contains("Canberra : Australian National University"));
		
		assertThat(metadata, hasKey(SwordMetadata.DC_SUBJECT_LCSH));
		assertThat(metadata.get(SwordMetadata.DC_SUBJECT_LCSH), contains("Australian National University Employees Case studies", "Women Employment Case studiesAustralia Canberra (A.C.T.)", "Discrimination in employment Case studies", "Affirmative action programs Case studies"));
		
		assertThat(metadata, hasKey(SwordMetadata.DC_TITLE));
		assertThat(metadata.get(SwordMetadata.DC_TITLE), contains("Towards equal opportunity : women and employment at the Australian National University : a report submitted for the consideration of the Vice-Chancellor of the Australian National University, March 1984"));
		
		assertThat(metadata, hasKey(SwordMetadata.DC_TITLE_ALTERNATIVE));
		assertThat(metadata.get(SwordMetadata.DC_TITLE_ALTERNATIVE), contains("Employment of women at the Australian National University"));
		
	}
	
	
	@Test
	public void testLogRecord() throws Exception {
		SwordMetadata metadata = generateMetadata("b1544005", true);
		logParsedMetadata(metadata);
		
	}
}
