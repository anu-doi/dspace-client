/**
 * 
 */
package au.edu.anu.dspace.client.swordv2.digitisation;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
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
public class ThesisTracker {
	private static final Logger log = LoggerFactory.getLogger(ThesisTracker.class);
	
	private Path trackerFile;
	private Properties trackerEntries = new Properties();

	public ThesisTracker(Path trackerFile) throws IOException {
		this.trackerFile = trackerFile;
		if (Files.isRegularFile(trackerFile)) {
			if (!Files.isReadable(trackerFile) || !Files.isWritable(trackerFile)) {
				throw new IOException("File " + trackerFile.toString()
						+ " is not readable and writeable. Check permissions");
			}
			readTrackerFile();
		}
	}
	
	public Map<String, FileStatus> getAllTheses() {
		Map<String, FileStatus> theses = new HashMap<String, FileStatus>(trackerEntries.size());
		for (Entry<Object, Object> entry : trackerEntries.entrySet()) {
			String bNumber = (String) entry.getKey();
			try {
				FileStatus fileStatus = new FileStatus((String) entry.getValue());
				theses.put(bNumber, fileStatus);
			} catch (ParseException e) {
				log.warn("Error parsing {}={}. Skipping line", entry.getKey(), entry.getValue());
				log.warn(e.getMessage(), e);
			}
		}
		return theses;
	}
	
	public void setThesisStatus(String bNumber, FileStatus fileStatus) throws IOException {
		trackerEntries.setProperty(bNumber, fileStatus.toString());
		writeTrackerFile();
	}
	
	public Path getTrackerFile() {
		return trackerFile;
	}

	private void readTrackerFile() throws IOException {
		try (BufferedReader reader = Files.newBufferedReader(getTrackerFile(), StandardCharsets.UTF_8)) {
			trackerEntries.load(reader);
		}
	}
	
	private void writeTrackerFile() throws IOException {
		final int maxTries = 10;
		int nAttempts = 1;
		for (boolean successful = false; successful == false; ) {
			try {
				// write entries into a temp file
				try (BufferedWriter tmpFileWriter = Files.newBufferedWriter(getTempTrackerFile(), StandardCharsets.UTF_8,
						StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
					trackerEntries.store(tmpFileWriter, "");
				}
				// verify entries written out match the ones in memory
				verifyPersistedEntries();
				// rename temp file to overwrite tracker file
				Files.move(getTempTrackerFile(), getTrackerFile(), StandardCopyOption.REPLACE_EXISTING);
				successful = true;
			} catch (IOException e) {
				// check if exceeded max tries
				if (nAttempts >= maxTries) {
					// reached max tries threshold, throw caught exception
					throw e;
				} else {
					// increment attempt counter, sleep, and retry
					nAttempts++;
					try {
						Thread.sleep(5000L);
					} catch (InterruptedException e1) {
						// no op
					}
				}
			}
		}
	}
	
	private void verifyPersistedEntries() throws IOException {
		Properties persistedEntries = new Properties();
		try (BufferedReader tmpFileReader = Files.newBufferedReader(getTempTrackerFile(), StandardCharsets.UTF_8)) {
			persistedEntries.load(tmpFileReader);
		}
		// check number of entries persisted match number of entries in memory
		if (trackerEntries.size() != persistedEntries.size()) {
			throw new IOException(String.format("Incorrect of entries in tracker file %s. expected=%d,actual=%d",
					getTempTrackerFile(), trackerEntries.size(), persistedEntries.size()));
		}

		for (Entry<Object, Object> entry : trackerEntries.entrySet()) {
			String key = (String) entry.getKey();
			if (!persistedEntries.containsKey(key)) {
				throw new IOException(
						String.format("Entry key=%s not persisted in %s", key, getTempTrackerFile().toString()));
			}
		}
	}
	
	private Path getTempTrackerFile() {
		String tempFilename = getTrackerFile().getFileName().toString() + "~";
		Path tempFile = getTrackerFile().getParent().resolve(tempFilename);
		return tempFile;
	}
}
