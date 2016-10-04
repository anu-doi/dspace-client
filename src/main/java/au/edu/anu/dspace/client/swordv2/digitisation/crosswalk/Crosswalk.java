/**
 * 
 */
package au.edu.anu.dspace.client.swordv2.digitisation.crosswalk;

import java.util.Map;
import java.util.Set;

import au.edu.anu.dspace.client.swordv2.digitisation.iiirecord.IIIRECORD;

/**
 * @author Rahul Khanna
 *
 */
public interface Crosswalk {

	public Map<String, Set<String>> generateMetadata(IIIRECORD iiiRecord);
}
