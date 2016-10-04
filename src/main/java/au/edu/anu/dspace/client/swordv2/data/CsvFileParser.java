package au.edu.anu.dspace.client.swordv2.data;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dspace.client.utils.SplitWithEscapeChar;

public class CsvFileParser extends AbstractParser {

	private static final Logger log = LoggerFactory.getLogger(CsvFileParser.class);

	public CsvFileParser(String[] args) throws ParseException, IOException {
		parseCmdLine(args);
	}
	
	@Override
	public void updateRequestStatus(SwordRequestData data, boolean isSuccess) {
		// No op
	}

	private void parseCmdLine(String[] args) throws ParseException, IOException {
		Options options = new Options();

		options.addOption("csv", true, "CSV file containing metadata and file locations");
		options.addOption("inprogress", true,
				"true if created items should stay in user's workspace, false to have them approved");
		options.addOption("h", "help", false, "Display this help screen");

		GnuParser gnuParser = new GnuParser();
		CommandLine parsedCmdLine = gnuParser.parse(options, args);

		// display help, if required and exit
		if (parsedCmdLine.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("csv", options);
			return;
		}

		String csvFilepath = parsedCmdLine.getOptionValue("csv");
		Path csvFile = Paths.get(csvFilepath);
		
		boolean inProgress = true;
		if (parsedCmdLine.hasOption("inprogress")) {
			if (parsedCmdLine.getOptionValue("inprogress").equals("false")) {
				inProgress = false;
			}
		}

		// csv files don't contain character encoding information, Excel saves them as Windows-1252 - Windows' default
		Reader reader = Files.newBufferedReader(csvFile, Charset.forName("windows-1252"));
		try (CSVParser csvParser = new CSVParser(reader, CSVFormat.EXCEL.withHeader().withCommentMarker('#'))) {
			Map<String, Integer> headerMap = csvParser.getHeaderMap();
			int rowCount = 1;
			
			// iterate over each row
			for (CSVRecord record : csvParser) {
				String collectionName = null;
				Map<String, Set<String>> metadata = new HashMap<>();
				// using a linked hashset so the order of bitstreams is maintained
				Set<BitstreamInfo> bitstreams = new LinkedHashSet<>();

				// iterate over each column of the current row
				for (String header : headerMap.keySet()) {
					String value = record.get(header).trim();

					// if cell contains a value, process it, else skip it
					if (value.length() > 0) {
						if (header.startsWith("file")) {
							processBitstream(csvFile, bitstreams, value);
						} else if (header.equals("collection")) {
							collectionName = value;
						} else {
							processMetadataField(metadata, header, value);
						}
					}
				}
				
				log.info("Row {}", rowCount++);
				log.info("\tCollection: {}", collectionName);
				log.info("\tMetadata: ({} items) {}", metadata.size(), metadata);
				log.info("\tBitstreams: ({} items) {}", bitstreams.size(), bitstreams);

				swordRequests.add(new SwordRequestData(collectionName, metadata, null, bitstreams, inProgress));
			}
		}

	}

	private void processBitstream(Path csvFile, Set<BitstreamInfo> bitstreams, String value)
			throws FileNotFoundException {
		Path bitstreamPath = Paths.get(value);
		// if bitstream path is relative, then resolve it
		// against the directory where CSV file is
		if (!bitstreamPath.isAbsolute()) {
			bitstreamPath = csvFile.getParent().resolve(bitstreamPath);
		}
		bitstreams.add(new BitstreamInfo(bitstreamPath));
	}

	private void processMetadataField(Map<String, Set<String>> metadata, String header, String value) {
		// split values by semicolons that aren't preceded by backslash as escape character
		List<String> values = new SplitWithEscapeChar(value, ";", "\\").getParts();
		
		if (header.contains(" ")) {
			header = header.split(" ", 2)[0];
		}
		
		if (!metadata.containsKey(header)) {
			// using a linked hashset so the order of values for a field is maintained
			metadata.put(header, new LinkedHashSet<>(values));
		} else {
			metadata.get(header).addAll(values);
		}
	}

}
