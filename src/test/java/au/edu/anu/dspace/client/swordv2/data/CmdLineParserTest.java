/**
 * 
 */
package au.edu.anu.dspace.client.swordv2.data;

import static org.junit.Assert.assertThat;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.cli.ParseException;
import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Rahul Khanna
 *
 */
public class CmdLineParserTest {
	
	private static final Logger log = LoggerFactory.getLogger(CmdLineParserTest.class);
	
	@Rule
	public TemporaryFolder tmpFolder = new TemporaryFolder();
	
	private CmdLineParser cmdLineParser;
	
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

	/**
	 * Test method for {@link au.edu.anu.dspace.client.swordv2.data.CmdLineParser#getCollectionName()}.
	 * @throws Exception 
	 */
	@Test
	public void testGetCollectionName() throws Exception {
		cmdLineParser = new CmdLineParser(new String[] {"-c", "My Test Collection" } );
		assertThat(cmdLineParser.getSwordRequests().get(0).getCollectionName(), Matchers.is("My Test Collection"));
	}

	/**
	 * Test method for
	 * {@link au.edu.anu.dspace.client.swordv2.data.CmdLineParser#getMetadata()}
	 * .
	 * 
	 * @throws Exception
	 * @throws ParseException
	 */
	@Test
	public void testGetMetadata() throws Exception {
		SwordMetadata metadata;

		cmdLineParser = new CmdLineParser(new String[] { "-m", "title=abc" });
		metadata = cmdLineParser.getSwordRequests().get(0).getMetadata();
		assertThat(metadata, Matchers.notNullValue());
		assertThat(metadata, Matchers.aMapWithSize(1));
		assertThat(metadata.get("title"), Matchers.hasItem("abc"));

		cmdLineParser = new CmdLineParser(new String[] { "-m", "title=first;title=second" });
		metadata = cmdLineParser.getSwordRequests().get(0).getMetadata();
		assertThat(metadata, Matchers.notNullValue());
		assertThat(metadata, Matchers.aMapWithSize(1));
		assertThat(metadata.get("title"), Matchers.hasItems("first", "second"));

		cmdLineParser = new CmdLineParser(new String[] { "-m", "title=my title;description=my description" });
		metadata = cmdLineParser.getSwordRequests().get(0).getMetadata();
		assertThat(metadata, Matchers.notNullValue());
		assertThat(metadata, Matchers.aMapWithSize(2));
		assertThat(metadata.get("title"), Matchers.hasItem("my title"));
		assertThat(metadata.get("description"), Matchers.hasItem("my description"));

		Path tempFile = Files.createTempFile("CmdLineParserTest", null);
		Files.write(tempFile, Arrays.asList("line 1", "line 2"), StandardCharsets.UTF_8,
				StandardOpenOption.TRUNCATE_EXISTING);
		cmdLineParser = new CmdLineParser(new String[] { "-m", "title=@" + tempFile.toAbsolutePath().toString() });
		metadata = cmdLineParser.getSwordRequests().get(0).getMetadata();
		assertThat(metadata, Matchers.notNullValue());
		assertThat(metadata, Matchers.aMapWithSize(1));
		assertThat(metadata.get("title"), Matchers.<String> iterableWithSize(1));

	}

	/**
	 * Test method for
	 * {@link au.edu.anu.dspace.client.swordv2.data.CmdLineParser#getBitstreams()}
	 * .
	 */
	@Test
	public void testGetBitstreams() throws Exception {
		File file1 = tmpFolder.newFile();
		File file2 = tmpFolder.newFile();
		cmdLineParser = new CmdLineParser(new String[] { "-c", "Test Collection", file1.getAbsolutePath(),
				file2.getAbsolutePath() });
		List<SwordRequestData> swordRequests = cmdLineParser.getSwordRequests();
		SwordRequestData swordRequest = swordRequests.get(0);
		assertThat(swordRequest.getBitstreams(), Matchers.<BitstreamInfo> iterableWithSize(2));
	}

}
