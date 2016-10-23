/**
 * 
 */
package au.edu.anu.dspace.client.rest.model;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

/**
 * @author Rahul Khanna
 *
 */
@XmlRootElement
public class Item extends DSpaceObject {

	@JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss.SSS")
	private Date lastModified;
	private String parentCollection;
	private List<String> parentCollectionList;
	private List<String> parentCommunityList;
	private List<String> bitstreams;
	private boolean archived;
	private boolean withdrawn;

	public Date getLastModified() {
		return lastModified;
	}

	public void setLastModified(Date lastModified) {
		this.lastModified = lastModified;
	}

	public String getParentCollection() {
		return parentCollection;
	}

	public void setParentCollection(String parentCollection) {
		this.parentCollection = parentCollection;
	}

	public List<String> getParentCollectionList() {
		return parentCollectionList;
	}

	public void setParentCollectionList(List<String> parentCollectionList) {
		this.parentCollectionList = parentCollectionList;
	}

	public List<String> getParentCommunityList() {
		return parentCommunityList;
	}

	public void setParentCommunityList(List<String> parentCommunityList) {
		this.parentCommunityList = parentCommunityList;
	}

	public List<String> getBitstreams() {
		return bitstreams;
	}

	public void setBitstreams(List<String> bitstreams) {
		this.bitstreams = bitstreams;
	}

	public boolean isArchived() {
		return archived;
	}

	public void setArchived(boolean archived) {
		this.archived = archived;
	}

	public boolean isWithdrawn() {
		return withdrawn;
	}

	public void setWithdrawn(boolean withdrawn) {
		this.withdrawn = withdrawn;
	}

	@Override
	public String toString() {
		return String.format(
				"Item [id=%s, name=%s, handle=%s, type=%s, link=%s, expand=%s, lastModified=%s, parentCollection=%s, parentCollectionList=%s, parentCommunityList=%s, bitstreams=%s, archived=%s, withdrawn=%s]",
				id, name, handle, type, link, expand, lastModified, parentCollection, parentCollectionList,
				parentCommunityList, bitstreams, archived, withdrawn);
	}


	
}
