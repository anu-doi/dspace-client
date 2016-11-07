/**
 * 
 */
package au.edu.anu.dspace.client.export.format;

import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import au.edu.anu.dspace.client.export.formats.datacite.DateType;
import au.edu.anu.dspace.client.export.formats.datacite.DescriptionType;
import au.edu.anu.dspace.client.export.formats.datacite.Resource;
import au.edu.anu.dspace.client.export.formats.datacite.Resource.Creators;
import au.edu.anu.dspace.client.export.formats.datacite.Resource.Creators.Creator;
import au.edu.anu.dspace.client.export.formats.datacite.Resource.Dates;
import au.edu.anu.dspace.client.export.formats.datacite.Resource.Dates.Date;
import au.edu.anu.dspace.client.export.formats.datacite.Resource.Descriptions;
import au.edu.anu.dspace.client.export.formats.datacite.Resource.Descriptions.Description;
import au.edu.anu.dspace.client.export.formats.datacite.Resource.Formats;
import au.edu.anu.dspace.client.export.formats.datacite.Resource.ResourceType;
import au.edu.anu.dspace.client.export.formats.datacite.Resource.RightsList;
import au.edu.anu.dspace.client.export.formats.datacite.Resource.RightsList.Rights;
import au.edu.anu.dspace.client.export.formats.datacite.Resource.Subjects;
import au.edu.anu.dspace.client.export.formats.datacite.Resource.Subjects.Subject;
import au.edu.anu.dspace.client.export.formats.datacite.Resource.Titles;
import au.edu.anu.dspace.client.export.formats.datacite.Resource.Titles.Title;
import au.edu.anu.dspace.client.rest.model.MetadataEntry;

/**
 * @author Rahul Khanna
 *
 */
public class DataCiteExporter extends AbstractExporter<Resource> {

	private static final String DATACITE_SCHEMA_LOCATION = "http://schema.datacite.org/meta/kernel-4/metadata.xsd";
	private static final String DATACITE_SCHEMA_NS = "http://datacite.org/schema/kernel-4";
	
	
	protected static JAXBContext jaxbContext;
	
	static {
		try {
			jaxbContext = JAXBContext.newInstance(Resource.class);
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}

	
	private List<MetadataEntry> metadata;
	private String crosswalk;
	private Marshaller marshaller;
	
	public DataCiteExporter(List<MetadataEntry> metadata, String crosswalk) throws ExportException {
		this.metadata = metadata;
		this.crosswalk = crosswalk;
		createMarshaller();
	}
	
	@Override
	public void validate() throws ExportException {
		super.validate(jaxbContext, DATACITE_SCHEMA_LOCATION);
		
	}

	@Override
	protected synchronized void generateRootObject() throws ExportException {
		if (this.rootObject == null) {
			Resource dataCiteResource = null;
			try {
				switch (this.crosswalk) {
				case "her":
					dataCiteResource = generateResourceUsingHer(this.metadata);
					break;
	
				default:
					throw new IllegalArgumentException(String.format("Unknown crosswalk: %s", crosswalk));
				}
	
			} catch (Exception e) {
				throw new ExportException(e);
			}
			this.rootObject = dataCiteResource;
		}
	}
	
	@Override
	protected Marshaller getMarshaller() throws ExportException {
		return this.marshaller;
	}
	
	private void createMarshaller() throws ExportException {
		try {
			marshaller = jaxbContext.createMarshaller();
			marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
			marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, String.format("%s %s", DATACITE_SCHEMA_NS, DATACITE_SCHEMA_LOCATION));
		} catch (JAXBException e) {
			throw new ExportException(e);
		}
	}
	
	private Resource generateResourceUsingHer(List<MetadataEntry> metadata) {
		Resource r = new Resource();
		
		// required fields
		
		// dc.contributor.author -> creators
		addAuthors(r, metadata);
		
		// dc.title -> titles
		addTitles(r, metadata);
		
		// dc.publisher -> publishers
		addPublisher(r, metadata);
		
		// dc.date.issued -> publicationYear
		addPublicationYear(r, metadata);
		
		// dc.type -> resourceType
		addResourceType(r, metadata);
		
		
		// optional fields
		
		// dc.subject -> subjects
		addSubjects(r, metadata);
		
		// dc.date.available -> dates
		addDates(r, metadata);
		
		// "application/pdf" -> formats
		addFormats(r, metadata);
		
		// dc.type.status -> version
		addVersion(r, metadata);
		
		// dcterms.accessRights -> rights
		addRights(r, metadata);
		
		// dc.description.abstract -> descriptions
		addDescriptions(r, metadata);
		
		return r;
	}

	private void addAuthors(Resource r, List<MetadataEntry> metadata) {
		for (MetadataEntry entry : filterEntriesOfField(metadata, "dc.contributor.author")) {
			Creator creator = new Creator();
			creator.setCreatorName(entry.getValue());
			if (r.getCreators() == null) {
				r.setCreators(new Creators());
			}
			r.getCreators().getCreator().add(creator);
		}
	}

	private void addTitles(Resource r, List<MetadataEntry> metadata) {
		for (MetadataEntry entry : filterEntriesOfField(metadata, "dc.title")) {
			Title title = new Title();
			title.setValue(entry.getValue());
//			if (entry.getLanguage() != null && entry.getLanguage().length() > 0) {
//				title.setLang(entry.getLanguage());
//			}
			if (r.getTitles() == null) {
				r.setTitles(new Titles());
			}
			r.getTitles().getTitle().add(title);
		}
	}

	private void addPublisher(Resource r, List<MetadataEntry> metadata) {
		MetadataEntry entry = filterFirstEntryOfField(metadata, "dc.publisher");
		if (entry != null) {
			r.setPublisher(entry.getValue());
		}
	}

	private void addPublicationYear(Resource r, List<MetadataEntry> metadata) {
		MetadataEntry entry = filterFirstEntryOfField(metadata, "dc.date.issued");
		if (entry != null) {
			r.setPublicationYear(entry.getValue());
		}
		
	}

	private void addResourceType(Resource r, List<MetadataEntry> metadata) {
		MetadataEntry entry = filterFirstEntryOfField(metadata, "dc.type");
		ResourceType resourceType = new ResourceType();
		resourceType.setValue(entry.getValue());
		resourceType.setResourceTypeGeneral(au.edu.anu.dspace.client.export.formats.datacite.ResourceType.SOFTWARE);
		r.setResourceType(resourceType);
	}

	private void addSubjects(Resource r, List<MetadataEntry> metadata) {
		for (MetadataEntry entry : filterEntriesOfField(metadata, "dc.subject")) {
			Subject subject = new Subject();
			subject.setValue(entry.getValue());
			if (r.getSubjects() == null) {
				r.setSubjects(new Subjects());
			}
			r.getSubjects().getSubject().add(subject);
		}
	}

	private void addDates(Resource r, List<MetadataEntry> metadata) {
		MetadataEntry entry = filterFirstEntryOfField(metadata, "dc.date.available");
		if (entry != null) {
			Date date = new Date();
			date.setDateType(DateType.AVAILABLE);
			date.setValue(entry.getValue());
			if (r.getDates() == null) {
				r.setDates(new Dates());
			}
			r.getDates().getDate().add(date);
		}
	}

	private void addFormats(Resource r, List<MetadataEntry> metadata) {
		if (r.getFormats() == null) {
			r.setFormats(new Formats());
		}
		r.getFormats().getFormat().add("application/pdf");
	}

	private void addVersion(Resource r, List<MetadataEntry> metadata) {
		MetadataEntry entry = filterFirstEntryOfField(metadata, "dc.type.status");
		if (entry != null) {
			r.setVersion(entry.getValue());
		}
	}

	private void addRights(Resource r, List<MetadataEntry> metadata) {
		for (MetadataEntry entry : filterEntriesOfField(metadata, "dcterms.accessRights")) {
			Rights rights = new Rights();
			rights.setValue(entry.getValue());
			if (r.getRightsList() == null) {
				r.setRightsList(new RightsList());
			}
			r.getRightsList().getRights().add(rights);
		}
	}

	private void addDescriptions(Resource r, List<MetadataEntry> metadata) {
		MetadataEntry entry = filterFirstEntryOfField(metadata, "dc.description.abstract");
		if (entry != null) {
			Description description = new Description();
			description.setDescriptionType(DescriptionType.ABSTRACT);
			description.getContent().add(entry.getValue());
			if (r.getDescriptions() == null) {
				r.setDescriptions(new Descriptions());
			}
			r.getDescriptions().getDescription().add(description);
		}
	}
}
