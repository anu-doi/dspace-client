/**
 * 
 */
package au.edu.anu.dspace.client.swordv2.data;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dspace.client.utils.SplitWithEscapeChar;

/**
 * @author Rahul Khanna
 *
 */
public class CmdLineParser extends AbstractParser {

	private static final Logger log = LoggerFactory.getLogger(CmdLineParser.class);

	private CommandLine parsedCmdLine;

	public CmdLineParser(String[] args) throws ParseException, IOException {
		parseCmdLine(args);
	}

	@Override
	public void updateRequestStatus(SwordRequestData data, boolean isSuccess) {
		// No op
	}

	private void parseCmdLine(String[] args) throws ParseException, IOException {
		Options options = new Options();

		options.addOption("c", "collection", true, "Collection Name");
		options.addOption("m", "metadata", true, "Metadata String");
		options.addOption("e", "edit-media-link", true, "Edit Media Link");
		options.addOption("inprogress", true,
				"true if created items should be marked as in progress, false to have them approved");
		options.addOption("h", "help", false, "Display this help screen");
		
		CommandLineParser parser = new GnuParser();
		parsedCmdLine = parser.parse(options, args);
		
		if (parsedCmdLine.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("csv", options);
			return;
		}

		SwordRequestData swordRequest = new SwordRequestData(readCollectionName(), readMetadata(), readEditMediaLink(),
				readBitstreams(), readInProgress());
		swordRequests.addAll(Arrays.asList(swordRequest));
	}

	private String readCollectionName() {
		String collectionName = null;
		if (parsedCmdLine.hasOption("c")) {
			collectionName = parsedCmdLine.getOptionValue("c");
		}
		return collectionName;
	}

	private SwordMetadata readMetadata() throws ParseException, IOException {
		SwordMetadata swordMetadata = null;
		if (parsedCmdLine.hasOption('m')) {
			// split argument against option 'm' by ';'.
			String metadataParam = parsedCmdLine.getOptionValue('m');
			List<String> metadataEntries = new SplitWithEscapeChar(metadataParam, ";", "\\").getParts();
			swordMetadata = new SwordMetadata();

			// each metadata entry contains key and value in 'key=value' format.
			for (String entry : metadataEntries) {
				String[] keyValue = entry.split("=", 2);
				if (keyValue.length != 2) {
					throw new ParseException("Unable to parse Metadata");
				}

				// if value starts with '@' then read the contents of the file.
				String key = keyValue[0].toLowerCase().trim();
				String value = null;
				if (keyValue[1].startsWith("@")) {
					Path metadataFilepath = Paths.get(keyValue[1].substring(1));
					StringWriter metadataWriter;
					try (InputStream metadataContentStream = Files.newInputStream(metadataFilepath)) {
						metadataWriter = new StringWriter((int) Files.size(metadataFilepath));
						IOUtils.copy(metadataContentStream, metadataWriter, StandardCharsets.UTF_8.toString());
						value = metadataWriter.toString().trim();
					}
				} else {
					value = keyValue[1].trim();
				}
				swordMetadata.add(key, value);
			}

			log.debug("Parsed metadata from command line: {}", swordMetadata);
		}

		return swordMetadata;
	}

	private String readEditMediaLink() {
		String editMediaLink = null;
		if (parsedCmdLine.hasOption("e")) {
			editMediaLink = parsedCmdLine.getOptionValue("e");
		}
		return editMediaLink;
	}

	private Set<BitstreamInfo> readBitstreams() {
		Set<BitstreamInfo> bitstreams = null;

		String[] args = parsedCmdLine.getArgs();
		if (args.length > 0) {
			bitstreams = new HashSet<BitstreamInfo>(args.length);
			for (String arg : args) {
				try {
					bitstreams.add(new BitstreamInfo(arg));
				} catch (FileNotFoundException e) {
					log.warn("Skipping non-existent file {}", arg);
				}
			}
		}
		return bitstreams;
	}
	
	private boolean readInProgress() {
		boolean inProgress = true;
		if (parsedCmdLine.hasOption("inprogress")) {
			if (parsedCmdLine.getOptionValue("inprogress").equals("false")) {
				inProgress = false;
			}
		}
		return inProgress;
	}
}
