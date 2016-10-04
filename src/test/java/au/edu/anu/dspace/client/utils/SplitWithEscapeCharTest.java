/**
 * 
 */
package au.edu.anu.dspace.client.utils;

import static org.junit.Assert.*;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

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
public class SplitWithEscapeCharTest {
	private static final Logger log = LoggerFactory.getLogger(SplitWithEscapeCharTest.class);
	
	private SplitWithEscapeChar swec;
	
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
	public void testSemicolonSeparator() {
		Map<String, String[]> data = new LinkedHashMap<>();
		data.put("1;2", new String[] {"1", "2"});
		data.put("1;2\\;2a", new String[] {"1", "2;2a"});
		data.put("1\\;1a", new String[] {"1;1a"});
		data.put("1\\;1a;2;3", new String[] {"1;1a", "2", "3"});
		data.put("1\\;1a;2\\;2a", new String[] {"1;1a", "2;2a"});
		data.put("1\\;1a;2\\;2a;3", new String[] {"1;1a", "2;2a", "3"});
		data.put("1\\;", new String[] {"1;"});
		data.put("1;;3", new String[] {"1", "", "3"});
		data.put("1;;3;", new String[] {"1", "", "3"});
		data.put("1\\2\\3\\4", new String[] {"1\\2\\3\\4"});
		
		for (Entry<String, String[]> sample : data.entrySet()) {
			swec = new SplitWithEscapeChar(sample.getKey(), ";", "\\");
			log.trace("Input: {}\tOutput: {}", sample.getKey(), swec.getParts());
			assertThat(swec.getParts(), Matchers.contains(sample.getValue()));
		}
	}
}
