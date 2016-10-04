/**
 * 
 */
package au.edu.anu.dspace.client.swordv2.digitisation;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @author Rahul Khanna
 *
 */
public class FileStatus {
	// number of fields in a status string. change this value when adding/deleting fields
	private static final int NFIELDCOUNT = 4;
	
	private static final String FIELDSEPARATOR = ";";
	
	private static final SimpleDateFormat isoDateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

	public enum Status {
		UPLOADED
	};

	private Status status;
	private Date date;
	private long size;
	private String note;

	public FileStatus(Status status, Date date, long size, String note) {
		this.status = status;
		this.date = date;
		this.size = size;
		this.note = note;
	}

	public FileStatus(String fileStatusStr) throws ParseException {
		parseFileStatus(fileStatusStr);
	}

	private void parseFileStatus(String fileStatusStr) throws ParseException {
		String[] parts = fileStatusStr.split(FIELDSEPARATOR, NFIELDCOUNT); 
		status = Status.valueOf(parts[0]);
		date = isoDateFormatter.parse(parts[1]);
		size = Long.valueOf(parts[2], 10);
		note = parts[3];
	}

	public Status getStatus() {
		return status;
	}

	public Date getDate() {
		return date;
	}

	public long getSize() {
		return size;
	}

	public String getNote() {
		return note;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(status.toString());
		sb.append(";");
		sb.append(isoDateFormatter.format(date));
		sb.append(";");
		sb.append(size);
		sb.append(";");
		sb.append(note);
		return sb.toString();
	}
}
