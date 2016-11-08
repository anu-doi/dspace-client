/**
 * 
 */
package au.edu.anu.dspace.client.export.format;

import java.io.InputStream;
import java.util.List;

import org.w3c.dom.Document;

/**
 * @author Rahul Khanna
 *
 */
public interface Exporter<T> {

	public T exportObject() throws ExportException;

	public InputStream exportToStream() throws ExportException;

	public String exportToString() throws ExportException;

	public Document exportToDocument() throws ExportException;
	
	public List<String> validate() throws ExportException;
}
