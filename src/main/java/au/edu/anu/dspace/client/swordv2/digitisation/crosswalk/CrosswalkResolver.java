/**
 * 
 */
package au.edu.anu.dspace.client.swordv2.digitisation.crosswalk;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Rahul Khanna
 *
 */
public class CrosswalkResolver {
	private static final String CROSSWALKS_FILE = "crosswalks.properties";

	private static final Logger log = LoggerFactory.getLogger(CrosswalkResolver.class);
	
	private Map<String, Crosswalk> crosswalks;
	
	public CrosswalkResolver() throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
		Properties cwProps = loadCrosswalks();
		crosswalks = createCrosswalks(cwProps);
	}

	public Crosswalk getCrosswalk(String id) {
		Crosswalk cw = crosswalks.get(id);
		return cw;
	}


	private Properties loadCrosswalks() throws IOException {
		Properties cwProps = new Properties();
		InputStream cwMapStream = this.getClass().getResourceAsStream(CROSSWALKS_FILE);
		try (BufferedReader cwMapReader = new BufferedReader(
				new InputStreamReader(cwMapStream, StandardCharsets.UTF_8))) {
			cwProps.load(cwMapReader);
		}
		return cwProps;
	}


	private Map<String, Crosswalk> createCrosswalks(Properties cwProps)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		
		Map<String, Crosswalk> cwMap = new HashMap<>(cwProps.entrySet().size());
		
		for (Entry<Object, Object> entry : cwProps.entrySet()) {
			String id = null;
			try {
				id = (String) entry.getKey();
				String cwClassName = (String) entry.getValue();
				Crosswalk cw = (Crosswalk) Class.forName(cwClassName).newInstance();
				cwMap.put(id, cw);
				log.trace("Added crosswalk {}={}", id, cwClassName);
			} catch (ClassCastException | InstantiationException | IllegalAccessException | ClassNotFoundException e) {
				log.warn("Unable to create crosswalk for id {}", id);
				throw e;
			}
		}
		
		return cwMap;
	}
}
