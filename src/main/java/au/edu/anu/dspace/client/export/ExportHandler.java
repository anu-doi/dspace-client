/**
 * 
 */
package au.edu.anu.dspace.client.export;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;

import au.edu.anu.dspace.client.JobHandler;
import au.edu.anu.dspace.client.config.Config;
import au.edu.anu.dspace.client.export.format.DataCiteExporter;
import au.edu.anu.dspace.client.export.format.ExportException;
import au.edu.anu.dspace.client.export.format.Exporter;
import au.edu.anu.dspace.client.rest.DSpaceRestClient;
import au.edu.anu.dspace.client.rest.RestClientException;
import au.edu.anu.dspace.client.rest.model.DSpaceObject;
import au.edu.anu.dspace.client.rest.model.MetadataEntry;

/**
 * @author Rahul Khanna
 *
 */
public class ExportHandler implements JobHandler {

	private DSpaceRestClient restClient;

	@Override
	public int handle(Config config, String[] args) {
		String restBaseUri = config.getProperty("rest.baseUri");
		String username = config.getProperty("rest.username");
		String password = config.getProperty("rest.password");

		if (restClient == null) {
			Client client = ClientBuilder.newClient();
			restClient = new DSpaceRestClient(client, restBaseUri);
		}

		CommandLine cmd;
		Options options = createOptions();
		try {
			cmd = parseCommandLine(options, args);
		} catch (ParseException e1) {
			print("Unable to parse command line: %s", String.join(" ", args));
			return 1;
		}

		if (cmd.hasOption("help")) {
			displayHelp(options);
			return 0;
		}

		if (!cmd.hasOption("handle") || !cmd.hasOption("format") || !cmd.hasOption("crosswalk")) {
			displayHelp(options);
			return 1;
		}

		String authToken = null;
		try {
			String handleOrId = cmd.getOptionValue("handle");
			String format = cmd.getOptionValue("format");
			String crosswalk = cmd.getOptionValue("crosswalk");

			Exporter<?> exporter = null;

			authToken = restClient.login(username, password);
			print("User %s logged in with token: %s", username, authToken);
			int id = resolveHandleOrId(authToken, handleOrId);
			List<MetadataEntry> metadata = restClient.getItemMetadata(authToken, id);

			switch (format) {
			case "datacite":
				exporter = new DataCiteExporter(metadata, crosswalk);
				if (cmd.hasOption("validate")) {
					List<String> validationMessages = exporter.validate();
					if (validationMessages.isEmpty()) {
						print("Validation successful");
					} else {
						for (String msg : validationMessages) {
							print("Validation Error: %s", msg);
						}
					}
				}
				break;

			default:
				print("Unknown Format Exporter: %s", format);
				break;
			}

			if (exporter != null) {
				if (!cmd.hasOption("output")) {
					print("********");
				}
				streamToOutput(exporter.exportToString(), cmd.getOptionValue("output"));
				if (!cmd.hasOption("output")) {
					print("********");
				} else {
					print("Exported to %s", cmd.getOptionValue("output"));
				}
			}
		} catch (RestClientException | ExportException | IOException e) {
			print(e.getMessage());
			e.printStackTrace();
		} finally {
			if (authToken != null) {
				try {
					restClient.logout(authToken);
					print("User %s logged out. Token %s invalidated", username, authToken);
				} catch (RestClientException e) {
					print("Unable to logoff user %s from REST service at %s. Error: %s", username, restBaseUri,
							e.getMessage());
				}
			}
		}

		return 0;
	}

	private void streamToOutput(String docAsStr, String osName) throws IOException {
		BufferedWriter os;
		boolean closeStreamWhenFinished;
		if (osName != null) {
			// normal file
			Path osPath = Paths.get(osName);
			os = Files.newBufferedWriter(osPath, StandardCharsets.UTF_8, StandardOpenOption.WRITE,
					StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
			closeStreamWhenFinished = true;
		} else {
			// stdout
			os = new BufferedWriter(new OutputStreamWriter(System.out));
			closeStreamWhenFinished = false;
		}

		try {
			os.write(docAsStr);
		} finally {
			if (closeStreamWhenFinished) {
				IOUtils.closeQuietly(os);
			} else {
				try {
					os.flush();
				} catch (IOException e) {
					// no op.
				}
			}
		}

	}

	private int resolveHandleOrId(String authToken, String handleOrId) throws RestClientException {
		int id;
		if (handleOrId.contains("/")) {
			DSpaceObject dSpaceObject = restClient.getHandle(authToken, handleOrId);
			id = dSpaceObject.getId();
			print ("Resolved handle=%s to id=%d", handleOrId, id);
		} else {
			id = Integer.parseInt(handleOrId);
		}
		return id;
	}

	private void displayHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("USAGE: export [options]", options);
	}

	private CommandLine parseCommandLine(Options options, String[] args) throws ParseException {
		CommandLineParser clParser = new DefaultParser();
		CommandLine cmd = clParser.parse(options, args);
		return cmd;
	}

	private Options createOptions() {
		Options options = new Options();
		Option oFormat = new Option("f", "format", true, "(required) format of output. e.g. datacite");
		options.addOption(oFormat);
		Option oCrosswalk = new Option("c", "crosswalk", true, "(required) name of crosswalk to use. e.g. her");
		options.addOption(oCrosswalk);
		Option oIdentifier = new Option("i", "handle", true, "(required) handle or DSpace internal ID");
		options.addOption(oIdentifier);
		Option oValidate = new Option("v", "validate", false, "to validate generated XML using its schema");
		options.addOption(oValidate);
		options.addOption(new Option("o", "output", true, "file to output to. stdout if not provided"));
		options.addOption(new Option("h", "help", false, "display this help"));
		return options;
	}

	private void print(String format, Object... args) {
		System.out.format(format, args);
		System.out.println();
	}

}
