package au.edu.anu.dspace.client.swordv2;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.swordapp.client.ClientConfiguration;
import org.swordapp.client.SWORDClient;

import au.edu.anu.dspace.client.JobHandler;
import au.edu.anu.dspace.client.config.Config;
import au.edu.anu.dspace.client.swordv2.data.CmdLineParser;
import au.edu.anu.dspace.client.swordv2.data.CsvFileParser;
import au.edu.anu.dspace.client.swordv2.data.SwordRequestDataProvider;
import au.edu.anu.dspace.client.swordv2.digitisation.DigitisedThesisParser;

public class SwordHandler implements JobHandler {
	private static final Logger log = LoggerFactory.getLogger(SwordHandler.class);
	
	// heavy resource; creating just once
	private static final SWORDClient swordClient;
	
	static {
		ClientConfiguration config = new ClientConfiguration();
		swordClient = new SWORDClient(config);
	}

	@Override
	public int handle(Config config, String[] args) {
		String serviceDocUrl = config.getProperty(Config.PROPERTY_SERVICEDOCURL);
		String username = config.getProperty(Config.PROPERTY_USERNAME);
		String password = config.getProperty(Config.PROPERTY_PASSWORD);
		String onBehalfOf = config.getProperty(Config.PROPERTY_ONBEHALFOF);
		SwordServerInfo serverInfo = new SwordServerInfo(serviceDocUrl, username, password, onBehalfOf);
		password = null;

		SwordRequestDataProvider dataProvider;
		try {
			if (args.length > 0 && args[0].equals("csv")) {
				dataProvider = new CsvFileParser(args);
			} else if (args.length > 0 && args[0].equals("digitised")) {
				dataProvider = new DigitisedThesisParser(args);
			} else {
				dataProvider = new CmdLineParser(args);
			}
			SwordProcessor processor = new SwordProcessor(swordClient, serverInfo, dataProvider);
			processor.process();
			return 0;
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return 1;
		}
	}

}
