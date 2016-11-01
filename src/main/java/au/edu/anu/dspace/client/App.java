package au.edu.anu.dspace.client;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dspace.client.config.Config;
import au.edu.anu.dspace.client.export.ExportHandler;
import au.edu.anu.dspace.client.rest.RestHandler;
import au.edu.anu.dspace.client.swordv2.SwordHandler;

public class App {
	private static final Logger log = LoggerFactory.getLogger(App.class);
	
	public static void main(String[] args) {
		JobHandler handler = null;
		String[] contextArgs = null;
		Config config = createConfig();
		
		if (args.length > 0) {
			contextArgs = extractContextArgs(args);
			
			switch (args[0]) {
			case "sword":
				handler = new SwordHandler();
				break;
				
			case "rest":
				handler = new RestHandler();
				break;
				
			case "export":
				handler = new ExportHandler();
				break;
				
			default:
				System.out.println("USAGE: dspace (sword | rest | export) [parameters]...");
			}
		} else {
			// no args provided. Display usage instructions.
		}
		
		
		if (handler != null) {
			handler.handle(config, contextArgs);
		}
	}

	/**
	 * Removes the first array element and creates a new array comprising of the
	 * remaining elements.
	 * 
	 * @param args
	 *            Command line arguments
	 * @return String array without the first command line parameter
	 */
	private static String[] extractContextArgs(String[] args) {
		String[] contextArgs;
		contextArgs = new String[args.length - 1];
		for (int i = 0; i < args.length - 1; i++) {
			contextArgs[i] = args[(i + 1)];
		}
		return contextArgs;
	}
	
	private static Config createConfig() {
		Config config = new Config();
		String configFile = System.getProperty("conf");
		InputStream configFileStream;
		try {
			if (configFile != null) {
				configFileStream = new FileInputStream(configFile);
			} else {
				configFile = "dspace-client.cfg";
				configFileStream = App.class.getClassLoader().getResourceAsStream(configFile);
			}
			
			if (configFileStream != null) {
				Reader configReader = new InputStreamReader(configFileStream, StandardCharsets.UTF_8);
				config.load(configReader);
				log.debug("Using Properties: {}", config);
			} else {
				log.warn("Unable to locate {}", configFile);
			}
		} catch (IOException e) {
			log.warn("Unable to read properties from {}", configFile);
		}
		return config;
	}
}
