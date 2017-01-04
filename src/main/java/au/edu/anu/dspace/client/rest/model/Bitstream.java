/**
 * 
 */
package au.edu.anu.dspace.client.rest.model;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Rahul Khanna
 *
 */
@XmlRootElement
public class Bitstream extends DSpaceObject {

	private String bundleName;
	private String description;
	private String format;
	private String mimeType;
	private long sizeBytes;
	private String parentObject;
	private String retrieveLink;
	@XmlElement(name = "checkSum")
	private Checksum checksum;
	private int sequenceId;
	private String policies;

	public String getBundleName() {
		return bundleName;
	}

	public void setBundleName(String bundleName) {
		this.bundleName = bundleName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public long getSizeBytes() {
		return sizeBytes;
	}

	public void setSizeBytes(long sizeBytes) {
		this.sizeBytes = sizeBytes;
	}

	public String getParentObject() {
		return parentObject;
	}

	public void setParentObject(String parentObject) {
		this.parentObject = parentObject;
	}

	public String getRetrieveLink() {
		return retrieveLink;
	}

	public void setRetrieveLink(String retrieveLink) {
		this.retrieveLink = retrieveLink;
	}

	public Checksum getChecksum() {
		return checksum;
	}

	public void setChecksum(Checksum checksum) {
		this.checksum = checksum;
	}

	public int getSequenceId() {
		return sequenceId;
	}

	public void setSequenceId(int sequenceId) {
		this.sequenceId = sequenceId;
	}

	public String getPolicies() {
		return policies;
	}

	public void setPolicies(String policies) {
		this.policies = policies;
	}
	
	@Override
	public String toString() {
		return "Bitstream [bundleName=" + bundleName + ", description=" + description + ", format=" + format
				+ ", mimeType=" + mimeType + ", sizeBytes=" + sizeBytes + ", parentObject=" + parentObject
				+ ", retrieveLink=" + retrieveLink + ", checksum=" + checksum + ", sequenceId=" + sequenceId
				+ ", policies=" + policies + ", id=" + id + ", name=" + name + ", handle=" + handle + ", type=" + type
				+ ", link=" + link + ", expand=" + expand + "]";
	}
	



	public static class Checksum {
		private String value;
		@XmlElement(name = "checkSumAlgorithm")
		private String checksumAlgorithm;

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}

		public String getChecksumAlgorithm() {
			return checksumAlgorithm;
		}

		public void setChecksumAlgorithm(String checksumAlgorithm) {
			this.checksumAlgorithm = checksumAlgorithm;
		}

		@Override
		public String toString() {
			return String.format("Checksum [value=%s, checksumAlgorithm=%s]", value, checksumAlgorithm);
		}
		
	}
}
