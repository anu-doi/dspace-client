/**
 * 
 */
package au.edu.anu.dspace.client.swordv2.digitisation;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Rahul Khanna
 *
 */
public class DirectoryScanner {
	
	private static final Logger log = LoggerFactory.getLogger(DirectoryScanner.class);

	private List<Path> allFiles = new ArrayList<>();
	
	public DirectoryScanner(Collection<Path> dirsToScan) throws IOException {
		for (Path dir : dirsToScan) {
			List<Path> filesInDir = listFilesRecursively(dir);
			allFiles.addAll(filesInDir);
			log.trace("Directory {} contains {} files recursively", dir.toString(), filesInDir.size());
		}
	}
	
	public List<Path> getAllFiles(Collection<Path> dirsToScan) throws IOException {
		return allFiles;
	}
	
	public List<Path> getFilteredFiles(Collection<String> includeRegEx, Collection<String> excludeSubStrings)
			throws IOException {
		List<Path> filteredFiles = new ArrayList<>(allFiles.size());
		
		Set<Pattern> regExPatterns = new LinkedHashSet<>(includeRegEx.size());
		
		if (includeRegEx != null) {
			for (String regEx : includeRegEx) {
				regExPatterns.add(Pattern.compile(regEx));
			}
		}
		
		for (Path file : allFiles) {
			// check if the file is in the include pattern list
			if (!regExPatterns.isEmpty()) {
				boolean include = false;
				for (Pattern p : regExPatterns) {
					if (p.matcher(file.getFileName().toString()).find()) {
						include = true;
						break;
					}
				}
				
				if (!include) {
					continue;
				}
			}
			
			// check if the file contains any of the exclude substrings
			boolean exclude = false;
			if (excludeSubStrings != null && !excludeSubStrings.isEmpty()) {
				for (String excludeSubString : excludeSubStrings) {
					if (file.getFileName().toString().contains(excludeSubString)) {
						exclude = true;
						break;
					}
				}
			}
			
			if (!exclude) {
				filteredFiles.add(file);
			}
		}

		return filteredFiles;
	}

	private List<Path> listFilesRecursively(Path dir) throws IOException {
		List<Path> files = new ArrayList<>();
		
		try (DirectoryStream<Path> dirStream = Files.newDirectoryStream(dir)) {
			for (Path dirEntry : dirStream) {
				if (Files.isDirectory(dirEntry)) {
					files.addAll(listFilesRecursively(dirEntry));
				} else if (Files.isRegularFile(dirEntry)) {
					files.add(dirEntry);
				}
			}
		}
		
		return files;
	}
}
