/**
 * 
 */
package au.edu.anu.dspace.client.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Rahul Khanna
 *
 */
public class SplitWithEscapeChar {
	
	private List<String> splitParts;
	
	public SplitWithEscapeChar(String fullString, String regex, String escapeChar) {
		split(fullString, regex, escapeChar);
	}
	
	private void split(String fullString, String regex, String escapeChar) {
		String[] interimParts = fullString.split(regex);
		
		splitParts = new ArrayList<>(interimParts.length);
		for (int i = 0; i < interimParts.length; i++) {
			if (!interimParts[i].endsWith(escapeChar)) {
				splitParts.add(interimParts[i]);
			} else { 
				StringBuilder sb = new StringBuilder(interimParts[i]);
				sb.deleteCharAt(sb.length() - escapeChar.length());
				do {
					sb.append(regex);
					i++;
					if (i < interimParts.length) {
						sb.append(interimParts[i]);
					}
				} while (i < interimParts.length && interimParts[i].endsWith(escapeChar));
				splitParts.add(sb.toString());
			}
		}
	}

	public List<String> getParts() {
		return splitParts;
	}
}
