/**
 * 
 */
package au.edu.anu.dspace.client.swordv2.data;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.commons.httpclient.methods.InputStreamRequestEntity;

/**
 * @author Rahul Khanna
 *
 */
public class BitstreamInfo implements Comparable<BitstreamInfo> {
	
	private Path file;

	public BitstreamInfo(String filepath) throws FileNotFoundException {
		this(Paths.get(filepath));
	}

	public BitstreamInfo(Path file) throws FileNotFoundException {
		if (!Files.isRegularFile(file)) {
			throw new FileNotFoundException(String.format("File [%s] doesn't exist", file.toString()));
		}
		this.file = file;
	}

	public String getFilepath() {
		return file.toAbsolutePath().toString();
	}

	public Path getFile() {
		return file;
	}
	
	public String getFilename() {
		return file.getFileName().toString();
	}
	
	public long getSize() {
		try {
			return Files.size(file);
		} catch (IOException e) {
			return InputStreamRequestEntity.CONTENT_LENGTH_AUTO;
		}
	}
	
	@Override
	public String toString() {
		return getFilepath();
	}

	@Override
	public int compareTo(BitstreamInfo o) {
		return this.file.compareTo(o.getFile());
	}
}
