/**
 * 
 */
package au.edu.anu.dspace.client.swordv2.digitisation.crosswalk;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dspace.client.swordv2.digitisation.iiirecord.IIIRECORD;

/**
 * @author Rahul Khanna
 *
 */
public class DigitisedThesesCrosswalk extends AbstractCrosswalk {

	private static final Logger log = LoggerFactory.getLogger(DigitisedThesesCrosswalk.class);
	
	@Override
	public Map<String, Set<String>> generateMetadata(IIIRECORD iiiRecord) {
		Map<String, Set<String>> metadata = new HashMap<>();

		// bNumber
		String bNumber = extractBNumber(iiiRecord);
		if (bNumber != null) {
			LinkedHashSet<String> identifiers = new LinkedHashSet<>(1);
			identifiers.add(bNumber);
			metadata.put(DC_IDENTIFIER_OTHER, identifiers);
		}
		
		// titles
		List<String> titles = extractTitles(iiiRecord);
		if (titles != null && titles.size() > 0) {
			metadata.put(DC_TITLE, new LinkedHashSet<>(titles));
		}
		
		// alternative titles
		List<String> altTitles = extractAlternativeTitles(iiiRecord);
		if (altTitles != null && altTitles.size() > 0) {
			metadata.put(DC_TITLE_ALTERNATIVE, new LinkedHashSet<>(altTitles));
		}
		
		// authors
		List<String> authors = extractAuthors(iiiRecord);
		if (authors != null && authors.size() > 0) {
			metadata.put(DC_CONTRIBUTOR_AUTHOR, new LinkedHashSet<>(authors));
		}
		
		// dates published
		List<String> datesPublished = extractDatesPublished(iiiRecord);
		if (datesPublished != null && datesPublished.size() > 0) {
			metadata.put(DC_DATE_ISSUED, new LinkedHashSet<>(datesPublished));
		}
		
		// dates copyrighted
		List<String> datesCopyrighted = extractDatesCopyrighted(iiiRecord);
		if (datesCopyrighted != null && datesCopyrighted.size() > 0) {
			metadata.put(DC_DATE_COPYRIGHT, new LinkedHashSet<>(datesCopyrighted));
		}
		
		// publishers
		List<String> publishers = extractPublishers(iiiRecord);
		if (publishers != null && publishers.size() > 0) {
			metadata.put(DC_PUBLISHER, new LinkedHashSet<>(publishers));
		}
		
		// format extents
		List<String> formatExtents = extractFormatExtents(iiiRecord);
		if (formatExtents != null && formatExtents.size() > 0) {
			metadata.put(DC_FORMAT_EXTENT, new LinkedHashSet<>(formatExtents));
		}
		
		// format mediums
		List<String> formatMediums = extractFormatMediums(iiiRecord);
		if (formatMediums != null && formatMediums.size() > 0) {
			metadata.put(DC_FORMAT_MEDIUM, new LinkedHashSet<>(formatMediums));
		}
		

		// supervisors
		List<String> supervisors = extractSupervisors(iiiRecord);
		if (supervisors != null && supervisors.size() > 0) {
			metadata.put(LOCAL_CONTRIBUTOR_SUPERVISOR, new LinkedHashSet<>(supervisors));
		}
		
		// colleges
		List<String> colleges = extractColleges(iiiRecord);
		if (colleges != null && colleges.size() > 0) {
			metadata.put(LOCAL_CONTRIBUTOR_AFFILIATION, new LinkedHashSet<>(colleges));
		}
		
		// dissertation notes
		List<String> dissertationNotes = extractDissertationNotes(iiiRecord);
		if (dissertationNotes != null && dissertationNotes.size() > 0) {
			metadata.put(DC_TYPE, new LinkedHashSet<>(dissertationNotes));
		}
		
		// summaries
		List<String> summaries = extractSummaries(iiiRecord);
		if (summaries != null && summaries.size() > 0) {
			metadata.put(DC_DESCRIPTION_ABSTRACT, new LinkedHashSet<>(summaries));
		}

		// accrual methods
		List<String> accrualMethods = extractAccrualMethods(iiiRecord);
		if (accrualMethods != null && accrualMethods.size() > 0) {
			metadata.put(DCTERMS_ACCRUALMETHOD, new LinkedHashSet<>(accrualMethods));
		}
		
		// subjects DDC
		List<String> subjectsDdc = extractSubjectsDdc(iiiRecord);
		if (subjectsDdc != null && subjectsDdc.size() > 0) {
			metadata.put(DC_SUBJECT_DDC, new LinkedHashSet<>(subjectsDdc));
		}
		
		// subject classifications
		List<String> subjectClassifications = extractSubjectClassifications(iiiRecord);
		if (subjectClassifications != null && subjectClassifications.size() > 0) {
			metadata.put(DC_SUBJECT_CLASSIFICATION, new LinkedHashSet<>(subjectClassifications));
		}
		
		// subjects LCC
		List<String> subjectsLcc = extractSubjectsLcc(iiiRecord);
		if (subjectsLcc != null && subjectsLcc.size() > 0) {
			metadata.put(DC_SUBJECT_LCC, new LinkedHashSet<>(subjectsLcc));
		}
		
		// subjects LCSH
		List<String> subjectsLcsh = extractSubjectsLcsh(iiiRecord);
		if (subjectsLcsh != null && subjectsLcsh.size() > 0) {
			metadata.put(DC_SUBJECT_LCSH, new LinkedHashSet<>(subjectsLcsh));
		}
		
		// access rights
		List<String> accessRights = extractAccessRights(iiiRecord);
		if (accessRights != null && accessRights.size() > 0) {
			metadata.put(DCTERMS_ACCESSRIGHTS, new LinkedHashSet<>(accessRights));
		}
		
		// languages
		List<String> languages = extractLanguages(iiiRecord);
		if (languages != null && languages.size() > 0) {
			metadata.put(DC_LANGUAGE, new LinkedHashSet<>(languages));
		}
		
		
		// table of contents
		List<String> tableOfContents = extractTableOfContents(iiiRecord);
		if (tableOfContents != null && tableOfContents.size() > 0) {
			metadata.put(DC_DESCRIPTION_TABLEOFCONTENTS, new LinkedHashSet<>(tableOfContents));
		}
		
		return metadata;
	}
	
	
		
}
