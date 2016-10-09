/**
 * 
 */
package au.edu.anu.dspace.client.swordv2.digitisation.crosswalk;

import au.edu.anu.dspace.client.swordv2.data.SwordMetadata;
import au.edu.anu.dspace.client.swordv2.digitisation.iiirecord.IIIRECORD;

/**
 * @author Rahul Khanna
 *
 */
public interface Crosswalk {

	public SwordMetadata generateMetadata(IIIRECORD iiiRecord);
}
