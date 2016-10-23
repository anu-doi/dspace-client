/**
 * 
 */
package au.edu.anu.dspace.client.rest.model;

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Rahul Khanna
 *
 */
@XmlRootElement
public class Community extends DSpaceObject {
	private String logo;
	private String parentCommunity;
	private String copyrightText;
	private String introductoryText;
	private String shortDescription;
	private String sidebarText;
	private int countItems;
	private List<String> subcommunities;
	private List<String> collections;

	public String getLogo() {
		return logo;
	}

	public void setLogo(String logo) {
		this.logo = logo;
	}

	public String getParentCommunity() {
		return parentCommunity;
	}

	public void setParentCommunity(String parentCommunity) {
		this.parentCommunity = parentCommunity;
	}

	public String getCopyrightText() {
		return copyrightText;
	}

	public void setCopyrightText(String copyrightText) {
		this.copyrightText = copyrightText;
	}

	public String getIntroductoryText() {
		return introductoryText;
	}

	public void setIntroductoryText(String introductoryText) {
		this.introductoryText = introductoryText;
	}

	public String getShortDescription() {
		return shortDescription;
	}

	public void setShortDescription(String shortDescription) {
		this.shortDescription = shortDescription;
	}

	public String getSidebarText() {
		return sidebarText;
	}

	public void setSidebarText(String sidebarText) {
		this.sidebarText = sidebarText;
	}

	public int getCountItems() {
		return countItems;
	}

	public void setCountItems(int countItems) {
		this.countItems = countItems;
	}

	public List<String> getSubcommunities() {
		return subcommunities;
	}

	public void setSubcommunities(List<String> subcommunities) {
		this.subcommunities = subcommunities;
	}

	public List<String> getCollections() {
		return collections;
	}

	public void setCollections(List<String> collections) {
		this.collections = collections;
	}

	@Override
	public String toString() {
		return String.format(
				"Community [id=%s, name=%s, handle=%s, type=%s, link=%s, expand=%s, logo=%s, parentCommunity=%s, copyrightText=%s, introductoryText=%s, shortDescription=%s, sidebarText=%s, countItems=%s, subcommunities=%s, collections=%s]",
				id, name, handle, type, link, expand, logo, parentCommunity, copyrightText, introductoryText,
				shortDescription, sidebarText, countItems, subcommunities, collections);
	}

	
}
