/**
 * 
 */
package au.edu.anu.dspace.client.swordv2.digitisation.crosswalk;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasKey;
import static org.junit.Assert.assertThat;

import java.nio.charset.StandardCharsets;

import javax.xml.bind.Marshaller;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dspace.client.swordv2.data.SwordMetadata;

/**
 * @author Rahul Khanna
 *
 */
public class DigitisedThesesCrosswalkTest extends AbstractCrosswalkTest {

	private static final Logger log = LoggerFactory.getLogger(DigitisedThesesCrosswalkTest.class);
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		super.setUp();
		cw = new DigitisedThesesCrosswalk();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAccessRights() throws Exception {
		SwordMetadata metadata = generateMetadata("b3732678", true);
		logParsedMetadata(metadata);
		
		assertThat(metadata, hasKey(SwordMetadata.DCTERMS_ACCESSRIGHTS));
		assertThat(metadata.get(SwordMetadata.DCTERMS_ACCESSRIGHTS), contains("ANU Thesis access restricted until 2025.09.01; Pro Vice-Chancellor (Research and Research Training)"));
		
	}
	
	@Test
	public void testAccrualMethods() throws Exception {
		SwordMetadata metadata = generateMetadata("b3579067", true);
		logParsedMetadata(metadata);
		
		assertThat(metadata, hasKey(SwordMetadata.DCTERMS_ACCRUALMETHOD));
		assertThat(metadata.get(SwordMetadata.DCTERMS_ACCRUALMETHOD), contains("ANU Deposit Copy; Received: 20140909"));
	}
	
	@Test
	public void testAlternativeTitles() throws Exception {
		SwordMetadata metadata = generateMetadata("b1014212", true);
		logParsedMetadata(metadata);
		
		assertThat(metadata, hasKey(SwordMetadata.DC_TITLE_ALTERNATIVE));
		assertThat(metadata.get(SwordMetadata.DC_TITLE_ALTERNATIVE), contains("Social structure and social organization in Upper Mandailing, Sumatra"));
	}
	
	@Test
	public void testAuthors() throws Exception {
		SwordMetadata metadata = generateMetadata("b1292681", true);
		logParsedMetadata(metadata);
		
		assertThat(metadata, hasKey(SwordMetadata.DC_CONTRIBUTOR_AUTHOR));
		assertThat(metadata.get(SwordMetadata.DC_CONTRIBUTOR_AUTHOR), contains("Sima, Guang", "De Crespigny, Rafe"));
	}
	
	@Test
	public void testBasicFields() throws Exception {
		SwordMetadata metadata = generateMetadata("b1649967", true);
		logParsedMetadata(metadata);
		
		assertThat(metadata, hasKey(SwordMetadata.DC_CONTRIBUTOR_AUTHOR));
		assertThat(metadata, hasKey(SwordMetadata.DC_TITLE));
		assertThat(metadata, hasKey(SwordMetadata.DC_DATE_ISSUED));
		assertThat(metadata, hasKey(SwordMetadata.DC_IDENTIFIER_OTHER));
		
		assertThat(metadata.get(SwordMetadata.DC_CONTRIBUTOR_AUTHOR), contains("Jack-Hinton, Colin"));
		assertThat(metadata.get(SwordMetadata.DC_TITLE), contains("The European discovery, rediscovery and exploration of the Solomon Islands, 1568-1838"));
		assertThat(metadata.get(SwordMetadata.DC_DATE_ISSUED), contains("1962"));
		assertThat(metadata.get(SwordMetadata.DC_TYPE), contains("Thesis (Ph.D.)--Australian National University, 1962."));
		assertThat(metadata.get(SwordMetadata.DC_IDENTIFIER_OTHER), contains("b1649967"));
	}
	
	@Test
	public void testDatesCopyrighted() throws Exception {
		SwordMetadata metadata = generateMetadata("b1014212", true);
		logParsedMetadata(metadata);
		
		assertThat(metadata, hasKey(SwordMetadata.DC_DATE_COPYRIGHT));
		assertThat(metadata.get(SwordMetadata.DC_DATE_COPYRIGHT), contains("1960"));
	}
	
	@Test
	public void testFormatExtents() throws Exception {
		SwordMetadata metadata = generateMetadata("b1225916", true);
		logParsedMetadata(metadata);
		
		assertThat(metadata, hasKey(SwordMetadata.DC_FORMAT_EXTENT));
		assertThat(metadata.get(SwordMetadata.DC_FORMAT_EXTENT), contains("xii, 336 leaves"));
	}
	
	@Ignore
	public void testFormatMediums() throws Exception {
		// TODO: No records in Sierra currently use this field. 
	}
	
	@Test
	public void testLanguages() throws Exception {
		SwordMetadata metadata = generateMetadata("b1288031", true);
		logParsedMetadata(metadata);
		
		assertThat(metadata.get(SwordMetadata.DC_LANGUAGE), contains("Text in Chinese with English title page. Summary in English in back pocket"));
	}

	@Test
	public void testLocalAffiliation() throws Exception {
		SwordMetadata metadata = generateMetadata("b1225916", true);
		logParsedMetadata(metadata);
		
		assertThat(metadata, hasKey(SwordMetadata.LOCAL_CONTRIBUTOR_AFFILIATION));
		assertThat(metadata.get(SwordMetadata.LOCAL_CONTRIBUTOR_AFFILIATION), contains("ANU Digital Theses", "Australian National University. Dept. of Pacific and Southeast Asian History"));
		
	}
	
	@Test
	public void testSubjectClassifications() throws Exception {
		SwordMetadata metadata = generateMetadata("b1815748", true);
		logParsedMetadata(metadata);
		
		assertThat(metadata, hasKey(SwordMetadata.DC_SUBJECT_CLASSIFICATION));
		assertThat(metadata.get(SwordMetadata.DC_SUBJECT_CLASSIFICATION), contains("KB181.O43 1992 Moys"));
	}
	
	@Test
	public void testSubjectDdc() throws Exception {
		SwordMetadata metadata = generateMetadata("b1148601", true);
		logParsedMetadata(metadata);
		
		assertThat(metadata, hasKey(SwordMetadata.DC_SUBJECT_DDC));
		assertThat(metadata.get(SwordMetadata.DC_SUBJECT_DDC), contains("333.109935"));
	}
	
	@Test
	public void testSubjectsLcc() throws Exception {
		SwordMetadata metadata = generateMetadata("b1742094", true);
		logParsedMetadata(metadata);
		
		assertThat(metadata, hasKey(SwordMetadata.DC_SUBJECT_LCC));
		assertThat(metadata.get(SwordMetadata.DC_SUBJECT_LCC), contains("B3376.W564 A85 1989"));
	}
	
	@Test
	public void testSubjectsLcsh() throws Exception {
		SwordMetadata metadata = generateMetadata("b1742094", true);
		logParsedMetadata(metadata);
		
		assertThat(metadata, hasKey(SwordMetadata.DC_SUBJECT_LCSH));
		assertThat(metadata.get(SwordMetadata.DC_SUBJECT_LCSH), contains("Wittgenstein, Ludwig, 1889-1951", "Meaning (Philosophy) History 20th century"));
	}
	
	@Test
	public void testSupervisors() throws Exception {
		SwordMetadata metadata = generateMetadata("b1225916", true);
		logParsedMetadata(metadata);
		
		assertThat(metadata, hasKey(SwordMetadata.LOCAL_CONTRIBUTOR_SUPERVISOR));
		assertThat(metadata.get(SwordMetadata.LOCAL_CONTRIBUTOR_SUPERVISOR), contains("Gunson, Niel"));
	}
	
	@Test
	public void testTableOfContents() throws Exception {
		SwordMetadata metadata = generateMetadata("b1225916", true);
		logParsedMetadata(metadata);

		assertThat(metadata, hasKey(SwordMetadata.DC_DESCRIPTION_TABLEOFCONTENTS));
		assertThat(metadata.get(SwordMetadata.DC_DESCRIPTION_TABLEOFCONTENTS), contains(
				"Prologue: The White Man's God, 1835-7 -- PART I : LEADERSHIP. Chapter 1. "
				+ "A Circuit in the Islands: the Missionaries; 2. Missionaries and the Fijian Ministry; "
				+ "3. Chiefly Authority in the Church -- PART II: THE CHURCH AND ITS PEOPLE. "
				+ "4. Ritual, Rules and Relevance in Fijian Methodism; 5. 'When the Old Gods Rise Again' : "
				+ "Methodist responses to Priestcraft and Syncretism -- PART III: FROM CONFLICT TO "
				+ "CO-OPERATION. 6. Challenges to Methodist Hegemony; 7 Affairs of Church, Government "
				+ "and Land -- PART IV: THE DEVOLUTION OF RESPONSIBILITY. 8. Education - for Church and "
				+ "Community; 9. Silver Coins and Self-Support: The Seeds of Church Independence -- "
				+ "Epilogue: The Fijian Church and Independence -- Appendix I. Fijian Ministers : 1851-1945 -- "
				+ "II. Circuits and Sections: 1835-1945 -- III. Chronology of Events in the History of Fijian "
				+ "Methodism."));
	}
	
	
	@Test
	public void testLogMultipleRecords() throws Exception {
		String[] bNumbers = { "b1017441", "b1649651", "b3732753", "b1649967", "b1649626", "b3732711", "b1649608",
				"b1017377", "b1649605", "b1649602", "b1649967", "b1649838", "b1649625", "b1649615", "b1649631",
				"b1649454", "b1649612", "b1649611", "b3755688", "b1649610", "b1649452", "b1206682", "b1649967",
				"b1649606", "b1649596", "b1017450", "b1017344", "b3761997", "b1649693", "b1649609", "b1649624",
				"b1799981" };
		for (String bNumber : bNumbers) {
			SwordMetadata metadata = generateMetadata(bNumber, false);
			logParsedMetadata(metadata);
		}
	}

}
