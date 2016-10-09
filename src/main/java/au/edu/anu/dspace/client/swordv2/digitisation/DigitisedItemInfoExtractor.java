/**
 * 
 */
package au.edu.anu.dspace.client.swordv2.digitisation;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dspace.client.swordv2.digitisation.crosswalk.Crosswalk;
import au.edu.anu.dspace.client.swordv2.digitisation.crosswalk.CrosswalkResolver;

/**
 * @author Rahul Khanna
 *
 */
public class DigitisedItemInfoExtractor {

	private static final Logger log = LoggerFactory.getLogger(DigitisedItemInfoExtractor.class);
	
	
	private Collection<DigitisedItemInfo> digitisedItemsInfo;


	public DigitisedItemInfoExtractor(Collection<Path> files)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
		digitisedItemsInfo = generateInfo(files);
		
	}
	
	public Collection<DigitisedItemInfo> getDigitisedItems() {
		return digitisedItemsInfo;
	}

	private Collection<DigitisedItemInfo> generateInfo(Collection<Path> files)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
		Map<String, DigitisedItemInfo> dii = new HashMap<>();

		for (Path file : files) {
			DigitisedItemInfo itemInfo = extractItemInfo(file);
			if (!dii.keySet().contains(itemInfo.getBNumber())) {
				dii.put(itemInfo.getBNumber(), itemInfo);
				log.trace("Extracted from file={} -> bNumber={}, collection={}, crosswalk={}", file.toString(),
						itemInfo.getBNumber(), itemInfo.getCollectionName(),
						itemInfo.getCrosswalk().getClass().getSimpleName());
			} else {
				dii.get(itemInfo.bNumber).fileset.add(file);
				log.trace("Previously extracted file={} -> bNumber={} (additional file)", file.toString(),
						itemInfo.getBNumber());
			}
		}

		return dii.values();
	}
	
	private DigitisedItemInfo extractItemInfo(Path file)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
		DigitisedItemInfo dii = null;

		String fileName = file.getFileName().toString();
		String parentFolderName = file.getParent().getFileName().toString();

		String bNumber = extractBNumber(fileName);
		
		String collectionName = extractCollectionName(parentFolderName);
		String cwName = extractCwName(parentFolderName);
		Crosswalk crosswalk = new CrosswalkResolver().getCrosswalk(cwName);

		dii = new DigitisedItemInfo(collectionName, bNumber, crosswalk, file);
		return dii;

	}

	private String extractCwName(String parentFolderName) {
		return parentFolderName.substring(parentFolderName.lastIndexOf("-") + 1);
	}

	private String extractBNumber(String fileName) {
		return new BNumberExtractor(fileName).getBNumber();
	}
	
	private String extractCollectionName(String parentFolderName) {
		return parentFolderName.substring(0, parentFolderName.lastIndexOf("-"));
	}

	public static class DigitisedItemInfo {
		private final String collectionName;
		private final String bNumber;
		private final Crosswalk cw;
		private final SortedSet<Path> fileset;
		
		public DigitisedItemInfo(String collectionName, String bNumber, Crosswalk cw, Path firstFile) {
			super();
			this.collectionName = collectionName;
			this.bNumber = bNumber;
			this.cw = cw;
			this.fileset = new TreeSet<Path>();
			this.fileset.add(firstFile);
		}

		public String getCollectionName() {
			return collectionName;
		}

		public String getBNumber() {
			return bNumber;
		}

		public Crosswalk getCrosswalk() {
			return cw;
		}

		public SortedSet<Path> getFileset() {
			return fileset;
		}
		
		
	}
}
