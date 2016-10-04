/**
 * 
 */
package au.edu.anu.dspace.client.swordv2.digitisation.crosswalk;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
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
public class CrosswalkResolverTest {

	private static final Logger log = LoggerFactory.getLogger(CrosswalkResolverTest.class);
	
	private CrosswalkResolver cwResolver;
	
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
		cwResolver = new CrosswalkResolver();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		Crosswalk crosswalk = cwResolver.getCrosswalk("c1");
		assertThat(crosswalk, Matchers.is(notNullValue()));
		assertThat(crosswalk, is(instanceOf(DigitisedThesesCrosswalk.class)));
	}

	@Test
	public void testNonExistentCrosswalk() {
		Crosswalk crosswalk = cwResolver.getCrosswalk("dummy");
		assertThat(crosswalk, is(nullValue()));
	}
}
