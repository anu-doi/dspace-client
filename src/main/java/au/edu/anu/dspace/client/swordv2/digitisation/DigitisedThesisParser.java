/**
 * 
 */
package au.edu.anu.dspace.client.swordv2.digitisation;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.xml.bind.JAXBException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dspace.client.swordv2.data.AbstractParser;
import au.edu.anu.dspace.client.swordv2.data.BitstreamInfo;
import au.edu.anu.dspace.client.swordv2.data.SwordMetadata;
import au.edu.anu.dspace.client.swordv2.data.SwordRequestData;
import au.edu.anu.dspace.client.swordv2.digitisation.DigitisedItemInfoExtractor.DigitisedItemInfo;
import au.edu.anu.dspace.client.swordv2.digitisation.FileStatus.Status;
import au.edu.anu.dspace.client.swordv2.digitisation.crosswalk.Crosswalk;
import au.edu.anu.dspace.client.swordv2.digitisation.iiirecord.IIIRECORD;

/**
 * @author Rahul Khanna
 *
 */
public class DigitisedThesisParser extends AbstractParser {
	
	private static final Logger log = LoggerFactory.getLogger(DigitisedThesisParser.class);

	private CommandLine parsedCmdLine;
	private String collectionName;
	private boolean inProgress = true;
	private ThesisTracker thesisTracker;
	private AnuCatalogueService catalogueSvc;

	public DigitisedThesisParser(String[] args) throws ParseException, JAXBException, IOException,
			InstantiationException, IllegalAccessException, ClassNotFoundException {
		CommandLineParser parser = new GnuParser();
		parsedCmdLine = parser.parse(generateOptions(), args);

		parseCollectionName();
		parseInProgressFlag();
		parseThesisTracker();
		parseDryRun();

		catalogueSvc = new AnuCatalogueService();

		List<Path> filesToUpload;
		if (parsedCmdLine.hasOption("f")) {
			filesToUpload = new ArrayList<Path>(parsedCmdLine.getOptionValues("f").length);
			for (String filepath : parsedCmdLine.getOptionValues("f")) {
				filesToUpload.add(Paths.get(filepath));
			}

			// generate sword requests from the files specified via command line
			generateSwordRequests(filesToUpload);
		} else if (parsedCmdLine.hasOption("d")) {
			Collection<Path> dirsToScan = readDirectoryListFile(parsedCmdLine.getOptionValue("d"));
			DirectoryScanner dirScanner = new DirectoryScanner(dirsToScan);

			if (thesisTracker != null) {
				filesToUpload = dirScanner.getFilteredFiles(Arrays.asList(BNumberExtractor.BNUMBER_REGEX),
						thesisTracker.getAllTheses().keySet());
			} else {
				filesToUpload = dirScanner.getFilteredFiles(Arrays.asList(BNumberExtractor.BNUMBER_REGEX), null);
			}
			log.info("Files to upload: {}", filesToUpload.size());
			if (log.isInfoEnabled()) {
				
				for (Path file : filesToUpload) {
					log.info("\t{}", file.toString());
				}
			}

			// generate sword requests from the new files found in directories
			// specified in directory list file
			generateSwordRequests(filesToUpload);
		}

	}

	@Override
	public void updateRequestStatus(SwordRequestData data, boolean isSuccess) {
		if (isSuccess) {
			String bNumber = data.getMetadata().get("dc-identifier-other").iterator().next();
			long size = 0L;
			for (BitstreamInfo bitstreamInfo : data.getBitstreams()) {
				size += bitstreamInfo.getSize();
			}
			FileStatus fileStatus = new FileStatus(Status.UPLOADED, Calendar.getInstance().getTime(), size, "");
			if (thesisTracker != null) {
				try {
					thesisTracker.setThesisStatus(bNumber, fileStatus);
				} catch (IOException e) {
					log.error("Unable to write to Thesis Tracker: {}", thesisTracker.getTrackerFile().toString());
					log.error(e.getMessage(), e);
				}
			}
		}
	}

	private Options generateOptions() {
		Options options = new Options();
	
		options.addOption("c", "collection", true, "collection name");
		options.addOption("inprogress", true,
				"true if created items should stay in user's workspace, false to have them approved");
		options.addOption("d", "directoryfile", true, "file containing directories to scan for new files");
		options.addOption("f", "file", true, "file to upload");
		options.addOption("x", "exclude", true, "file containing B-Numbers already uploaded");
		options.addOption("n", "dry-run", false, "flag to incidicate it's a dry run i.e. no changes will be made");
		return options;
	}

	private List<Path> readDirectoryListFile(String directoryListFilepath) throws IOException {
		List<Path> directories = new ArrayList<>();

		try (BufferedReader reader = Files.newBufferedReader(Paths.get(directoryListFilepath),
				StandardCharsets.UTF_8)) {
			for (String line = reader.readLine(); line != null; line = reader.readLine()) {
				Path dir = Paths.get(line);
				if (Files.isDirectory(dir)) {
					directories.add(dir);
				} else {
					log.warn("Skipping path [{}]. It either doesn't exist or is not a directory.");
				}
			}
		}

		if (directories.size() == 0) {
			log.warn("No directories specified in the directory list file [{}]", directoryListFilepath);
		}
		return directories;
	}

	private void generateSwordRequests(Collection<Path> filesToUpload)
			throws JAXBException, IOException, InstantiationException, IllegalAccessException, ClassNotFoundException {
		Map<String, FileStatus> completedTheses;

		if (thesisTracker != null) {
			completedTheses = thesisTracker.getAllTheses();
		} else {
			completedTheses = new HashMap<String, FileStatus>(0);
		}

		Collection<DigitisedItemInfo> items = new DigitisedItemInfoExtractor(filesToUpload).getDigitisedItems();

		int rowCount = 1;
		for (DigitisedItemInfo item : items) {
			String bNumber = item.getBNumber();

			// if the B Number hasn't already been created create a Sword
			// Request
			if (!completedTheses.containsKey(bNumber)) {
				IIIRECORD iiiRecord = catalogueSvc.retrieveCatalogueItem(bNumber);
				Crosswalk crosswalk = item.getCrosswalk();
				SwordMetadata metadata = crosswalk.generateMetadata(iiiRecord);
				// use collection name if specified on command line, else use the one from the directory name
				String targetCollectionName = this.collectionName != null ? this.collectionName
						: item.getCollectionName();
				SortedSet<BitstreamInfo> bitstreams = generateBitstreams(item.getFileset());
				SwordRequestData data = new SwordRequestData(targetCollectionName, metadata, null, bitstreams,
						inProgress);

				log.info("Row {}", rowCount++);
				log.info("\tCollection: {}", targetCollectionName);
				log.info("\tMetadata: ({} items) {}", metadata.size(), metadata);
				log.info("\tBitstreams: ({} items) {}", bitstreams.size(), bitstreams);

				swordRequests.add(data);
			} else {
				log.info("Skipping {}. Already uploaded.", bNumber);
			}
		}

	}
	
	private SortedSet<BitstreamInfo> generateBitstreams(Set<Path> fileset) throws FileNotFoundException {
		TreeSet<BitstreamInfo> bitstreams = new TreeSet<>();
		for (Path filepath : fileset) {
			bitstreams.add(new BitstreamInfo(filepath));
		}
		
		return bitstreams;
	}

	private void parseDryRun() {
		if (parsedCmdLine.hasOption("dry-run")) {
			this.isDryRun = true;
		}
	}

	private void parseInProgressFlag() {
		if (parsedCmdLine.hasOption("inprogress")) {
			if (parsedCmdLine.getOptionValue("inprogress").equals("false")) {
				this.inProgress = false;
			}
		}
	}

	private void parseCollectionName() {
		if (parsedCmdLine.hasOption("c") && parsedCmdLine.getOptionValue("c").length() > 0) {
			this.collectionName = parsedCmdLine.getOptionValue("c");
		}
	}

	private void parseThesisTracker() throws IOException {
		if (parsedCmdLine.hasOption("x")) {
			thesisTracker = new ThesisTracker(Paths.get(parsedCmdLine.getOptionValue("x")));
		}
	}
	
}
