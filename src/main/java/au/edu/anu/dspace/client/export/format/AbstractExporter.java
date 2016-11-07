package au.edu.anu.dspace.client.export.format;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import au.edu.anu.dspace.client.export.formats.datacite.Resource;
import au.edu.anu.dspace.client.rest.model.MetadataEntry;

public abstract class AbstractExporter<T> implements Exporter<T> {


	
	protected T rootObject;
	
	public AbstractExporter() {
		super();
	}

	public T exportObject() throws ExportException {
		if (this.rootObject == null) {
			generateRootObject();
		}
		return this.rootObject;
	}
	
	
	@Override
	public InputStream exportToStream() throws ExportException {
		if (this.rootObject == null) {
			generateRootObject();
		}
		ByteArrayInputStream is;
		try {
			Marshaller m = getMarshaller();
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			m.marshal(this.rootObject, os);
			is = new ByteArrayInputStream(os.toByteArray());
		} catch (JAXBException e) {
			throw new ExportException(e);
		}
		return is;
	}

	@Override
	public String exportToString() throws ExportException {
		if (this.rootObject == null) {
			generateRootObject();
		}
		StringWriter sw = new StringWriter();
		try {
			Marshaller m = getMarshaller();
			m.marshal(this.rootObject, sw);
		} catch (JAXBException e) {
			throw new ExportException(e);
		}
		return sw.toString();
	}

	@Override
	public Document exportToDocument() throws ExportException {
		if (this.rootObject == null) {
			generateRootObject();
		}
		Document doc;
		try {
			Marshaller m = getMarshaller();
			doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
			m.marshal(this.rootObject, doc);
		} catch (JAXBException | ParserConfigurationException | FactoryConfigurationError e) {
			throw new ExportException(e);
		}
		return doc;
	}

	protected List<MetadataEntry> filterEntriesOfField(List<MetadataEntry> metadata, String field) {
		List<MetadataEntry> filtered = new ArrayList<>();
		for (MetadataEntry entry : metadata) {
			if (entry.getKey().equals(field)) {
				filtered.add(entry);
			}
		}
		return filtered;
	}

	protected MetadataEntry filterFirstEntryOfField(List<MetadataEntry> metadata, String field) {
		MetadataEntry entry = null;
		for (MetadataEntry metadataEntry : metadata) {
			if (metadataEntry.getKey().equals(field)) {
				entry = metadataEntry;
				break;
			}
		}
		return entry;
	}

	protected void validate(JAXBContext context, String schemaUrl)
			throws ExportException {
		if (this.rootObject == null) {
			generateRootObject();
		}

		SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
		try {
			Schema schema = sf.newSchema(new URL(schemaUrl));
			Validator validator = schema.newValidator();
			validator.setErrorHandler(new CustomErrorHandler());
			validator.validate(new JAXBSource(context, this.rootObject));
		} catch (SAXException | IOException | JAXBException e) {
			throw new ExportException(e);
		}

	}

	protected abstract void generateRootObject() throws ExportException;
	
	protected abstract Marshaller getMarshaller() throws ExportException;
	
	
	private class CustomErrorHandler implements ErrorHandler {

		@Override
		public void warning(SAXParseException exception) throws SAXException {
			System.out.println("Warning: " + exception.getMessage());
		}

		@Override
		public void error(SAXParseException exception) throws SAXException {
			System.out.println("Error: " + exception.getMessage());
		}

		@Override
		public void fatalError(SAXParseException exception) throws SAXException {
			System.out.println("Fatal: " + exception.getMessage());
		}
		
	}
}