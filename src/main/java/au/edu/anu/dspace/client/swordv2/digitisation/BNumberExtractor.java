/**
 * 
 */
package au.edu.anu.dspace.client.swordv2.digitisation;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Rahul Khanna
 *
 */
public class BNumberExtractor {
	private static final Logger log = LoggerFactory.getLogger(BNumberExtractor.class);
	
	public static final String BNUMBER_REGEX = "b\\d{7}";
	
	private String fullString;
	private String bNumber;
	
	public BNumberExtractor(String fullString) throws IllegalArgumentException {
		super();
		this.fullString = fullString;
		extractBNumber();
	}
	
	private void extractBNumber() throws IllegalArgumentException {
		Pattern p = Pattern.compile(BNUMBER_REGEX);
		Matcher matcher = p.matcher(this.fullString);

		// check if there's at least one instance of a b number
		if (!matcher.find()) {
			throw new IllegalArgumentException("No B Number found in '" + this.fullString + "'");
		}
		this.bNumber = matcher.group().toLowerCase().trim();
		
		// warn if there are more b numbers
		if (matcher.find()) {
			log.warn("Found multiple B Numbers in '{}'. Using: ", this.fullString, this.bNumber);
		}
	}
	
	public String getBNumber() {
		return this.bNumber;
	}
}
