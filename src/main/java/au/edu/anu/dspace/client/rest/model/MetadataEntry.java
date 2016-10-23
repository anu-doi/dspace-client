/**
 * 
 */
package au.edu.anu.dspace.client.rest.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Rahul Khanna
 *
 */
@XmlRootElement
public class MetadataEntry {
	private String key;
	private String value;
	private String language;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	@Override
	public String toString() {
		return String.format("MetadataEntry [key=%s, value=%s, language=%s]", key, value, language);
	}

}
