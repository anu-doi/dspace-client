/**
 * 
 */
package au.edu.anu.dspace.client.swordv2.digitisation.crosswalk;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dspace.client.swordv2.data.SwordMetadata;
import au.edu.anu.dspace.client.swordv2.digitisation.iiirecord.IIIRECORD;

/**
 * @author Rahul Khanna
 *
 */
public class DigitisedThesesCrosswalk extends AbstractCrosswalk {

	private static final Logger log = LoggerFactory.getLogger(DigitisedThesesCrosswalk.class);
	
	@Override
	public SwordMetadata generateMetadata(IIIRECORD iiiRecord) {
		SwordMetadata swordMetadata = new SwordMetadata();

		// bNumber
		String bNumber = extractBNumber(iiiRecord);
		swordMetadata.add(SwordMetadata.DC_IDENTIFIER_OTHER, bNumber);
		
		// titles
		List<String> titles = extractTitles(iiiRecord);
		swordMetadata.add(SwordMetadata.DC_TITLE, titles);
		
		// alternative titles
		List<String> altTitles = extractAlternativeTitles(iiiRecord);
		swordMetadata.add(SwordMetadata.DC_TITLE_ALTERNATIVE, altTitles);
		
		// authors
		List<String> authors = extractAuthors(iiiRecord);
		swordMetadata.add(SwordMetadata.DC_CONTRIBUTOR_AUTHOR, authors);
		
		// dates published
		List<String> datesPublished = extractDatesPublished(iiiRecord);
		//swordMetadata.add(SwordMetadata.DC_DATE_ISSUED, datesPublished);
		swordMetadata.add(SwordMetadata.DC_DATE_CREATED, datesPublished);
		
		// dates copyrighted
		List<String> datesCopyrighted = extractDatesCopyrighted(iiiRecord);
		swordMetadata.add(SwordMetadata.DC_DATE_COPYRIGHT, datesCopyrighted);
		
		// publishers
		List<String> publishers = extractPublishers(iiiRecord);
		swordMetadata.add(SwordMetadata.DC_PUBLISHER, publishers);
		
		// format extents
		List<String> formatExtents = extractFormatExtents(iiiRecord);
		swordMetadata.add(SwordMetadata.DC_FORMAT_EXTENT, formatExtents);
		
		// format mediums
		List<String> formatMediums = extractFormatMediums(iiiRecord);
		swordMetadata.add(SwordMetadata.DC_FORMAT_MEDIUM, formatMediums);
		

		// supervisors
		List<String> supervisors = extractSupervisors(iiiRecord);
		swordMetadata.add(SwordMetadata.LOCAL_CONTRIBUTOR_SUPERVISOR, supervisors);
		
		// colleges
		List<String> colleges = extractColleges(iiiRecord);
		swordMetadata.add(SwordMetadata.LOCAL_CONTRIBUTOR_AFFILIATION, colleges);
		
		// dissertation notes
		List<String> dissertationNotes = extractDissertationNotes(iiiRecord);
		swordMetadata.add(SwordMetadata.DC_TYPE, dissertationNotes);
		swordMetadata.add(SwordMetadata.LOCAL_DESCRIPTION_NOTES, dissertationNotes);
		
		// abstracts
		List<String> abstracts = extractAbstracts(iiiRecord);
		swordMetadata.add(SwordMetadata.DC_DESCRIPTION_ABSTRACT, abstracts);

		// accrual methods
		List<String> accrualMethods = extractAccrualMethods(iiiRecord);
		swordMetadata.add(SwordMetadata.DCTERMS_ACCRUALMETHOD, accrualMethods);
		
		// subjects DDC
		List<String> subjectsDdc = extractSubjectsDdc(iiiRecord);
		swordMetadata.add(SwordMetadata.DC_SUBJECT_DDC, subjectsDdc);
		
		// subject classifications
		List<String> subjectClassifications = extractSubjectClassifications(iiiRecord);
		swordMetadata.add(SwordMetadata.DC_SUBJECT_CLASSIFICATION, subjectClassifications);
		
		// subjects LCC
		List<String> subjectsLcc = extractSubjectsLcc(iiiRecord);
		swordMetadata.add(SwordMetadata.DC_SUBJECT_LCC, subjectsLcc);
		
		// subjects LCSH
		List<String> subjectsLcsh = extractSubjectsLcsh(iiiRecord);
		swordMetadata.add(SwordMetadata.DC_SUBJECT_LCSH, subjectsLcsh);
		
		// access rights
		List<String> accessRights = extractAccessRights(iiiRecord);
		swordMetadata.add(SwordMetadata.DCTERMS_ACCESSRIGHTS, accessRights);
		
		// languages
		List<String> languages = extractLanguages(iiiRecord);
		swordMetadata.add(SwordMetadata.DC_LANGUAGE, languages);
		
		// table of contents
		List<String> tableOfContents = extractTableOfContents(iiiRecord);
		swordMetadata.add(SwordMetadata.DC_DESCRIPTION_TABLEOFCONTENTS, tableOfContents);
		
		return swordMetadata;
	}
	
	
		
}
