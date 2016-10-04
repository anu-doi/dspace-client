/**
 * 
 */
package au.edu.anu.dspace.client.swordv2.digitisation;

import static org.hamcrest.Matchers.aMapWithSize;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;

import org.hamcrest.Matchers;
import org.hamcrest.collection.IsCollectionWithSize;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dspace.client.swordv2.digitisation.DigitisedItemInfoExtractor.DigitisedItemInfo;
import au.edu.anu.dspace.client.swordv2.digitisation.crosswalk.DigitisedThesesCrosswalk;

/**
 * @author Rahul Khanna
 *
 */
public class DigitisedItemInfoExtractorTest {

	private static final Logger log = LoggerFactory.getLogger(DigitisedItemInfoExtractorTest.class);
	
	private DigitisedItemInfoExtractor diiExtractor;
	
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
	public void testSingleFile() throws Exception {
		Collection<Path> files = new ArrayList<>();
		files.add(Paths.get("C:\\Some Folder\\Another-Folder\\Open Access-c1\\b1234567-Smith.pdf"));
		
		diiExtractor = new DigitisedItemInfoExtractor(files);
		
		Collection<DigitisedItemInfo> items = diiExtractor.getDigitisedItems();
		assertThat(items, is(notNullValue()));
		assertThat(items, hasSize(1));
		
		DigitisedItemInfo itemInfo = items.iterator().next();
		assertThat(itemInfo.getBNumber(), is("b1234567"));
		assertThat(itemInfo.getCollectionName(), is("Open Access"));
		assertThat(itemInfo.getCrosswalk(), instanceOf(DigitisedThesesCrosswalk.class));
		assertThat(itemInfo.getFileset(), contains(Paths.get("C:\\Some Folder\\Another-Folder\\Open Access-c1\\b1234567-Smith.pdf")));
	}

	@Test
	public void testMultipleFiles() throws Exception {
		Collection<Path> files = new ArrayList<>();
		files.add(Paths.get("C:\\Some Folder\\Another-Folder\\Open-Access-c1\\b12345679-Smith-3.pdf"));
		files.add(Paths.get("C:\\Some Folder\\Another-Folder\\Open-Access-c1\\b12345679-Smith-2.pdf"));
		files.add(Paths.get("C:\\Some Folder\\Another-Folder\\Open-Access-c1\\b12345679-Smith-1.pdf"));

		diiExtractor = new DigitisedItemInfoExtractor(files);

		Collection<DigitisedItemInfo> items = diiExtractor.getDigitisedItems();
		assertThat(items, is(notNullValue()));
		assertThat(items, hasSize(1));

		DigitisedItemInfo itemInfo = items.iterator().next();
		assertThat(itemInfo.getBNumber(), is("b1234567"));
		assertThat(itemInfo.getCollectionName(), is("Open-Access"));
		assertThat(itemInfo.getCrosswalk(), instanceOf(DigitisedThesesCrosswalk.class));
		assertThat(itemInfo.getFileset(),
				contains(Paths.get("C:\\Some Folder\\Another-Folder\\Open-Access-c1\\b12345679-Smith-1.pdf"),
						Paths.get("C:\\Some Folder\\Another-Folder\\Open-Access-c1\\b12345679-Smith-2.pdf"),
						Paths.get("C:\\Some Folder\\Another-Folder\\Open-Access-c1\\b12345679-Smith-3.pdf")));

	}
}
