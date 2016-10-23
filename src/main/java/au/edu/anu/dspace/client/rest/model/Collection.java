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
public class Collection extends DSpaceObject {
	private String logo;
	private String parentCommunity;
	private List<String> parentCommunityList;
	private List<String> items;
	private String license;
	private String copyrightText;
	private String introductoryText;
	private String shortDescription;
	private String sidebarText;
	private int numberItems;

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

	public List<String> getParentCommunityList() {
		return parentCommunityList;
	}

	public void setParentCommunityList(List<String> parentCommunityList) {
		this.parentCommunityList = parentCommunityList;
	}

	public List<String> getItems() {
		return items;
	}

	public void setItems(List<String> items) {
		this.items = items;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
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

	public int getNumberItems() {
		return numberItems;
	}

	public void setNumberItems(int numberItems) {
		this.numberItems = numberItems;
	}

	@Override
	public String toString() {
		return String.format(
				"Collection [id=%s, name=%s, handle=%s, type=%s, link=%s, expand=%s, logo=%s, parentCommunity=%s, parentCommunityList=%s, items=%s, license=%s, copyrightText=%s, introductoryText=%s, shortDescription=%s, sidebarText=%s, numberItems=%s]",
				id, name, handle, type, link, expand, logo, parentCommunity, parentCommunityList, items, license,
				copyrightText, introductoryText, shortDescription, sidebarText, numberItems);
	}

	
}
