/**
 * 
 */
package au.edu.anu.dspace.client.swordv2.data;

import java.util.Map;
import java.util.Set;

/**
 * @author Rahul Khanna
 *
 */
public class SwordRequestData {
	
	private final String collectionName;
	private final SwordMetadata metadata;
	private final String editMediaLink;
	private final Set<BitstreamInfo> bitstreams;
	private final boolean inProgress;
	
	public SwordRequestData(String collectionName, SwordMetadata metadata, String editMediaLink,
			Set<BitstreamInfo> bitstreams, boolean inProgress) {
		super();
		this.collectionName = collectionName;
		this.metadata = metadata;
		this.editMediaLink = editMediaLink;
		this.bitstreams = bitstreams;
		this.inProgress = inProgress;
	}

	public String getCollectionName() {
		return collectionName;
	}

	public SwordMetadata getMetadata() {
		return metadata;
	}

	public String getEditMediaLink() {
		return editMediaLink;
	}

	public Set<BitstreamInfo> getBitstreams() {
		return bitstreams;
	}
	
	public boolean isInProgress() {
		return inProgress;
	}
	
}
