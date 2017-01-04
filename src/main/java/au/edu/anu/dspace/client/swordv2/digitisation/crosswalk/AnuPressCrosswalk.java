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
public class AnuPressCrosswalk extends AbstractCrosswalk {
	
	private static final Logger log = LoggerFactory.getLogger(AnuPressCrosswalk.class);

	/* (non-Javadoc)
	 * @see au.edu.anu.dspace.client.swordv2.digitisation.crosswalk.Crosswalk#generateMetadata(au.edu.anu.dspace.client.swordv2.digitisation.iiirecord.IIIRECORD)
	 */
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
				
		// accrual methods
		List<String> accrualMethods = extractAccrualMethods(iiiRecord);
		swordMetadata.add(SwordMetadata.DCTERMS_ACCRUALMETHOD, accrualMethods);
		
		// dates copyrighted
		List<String> datesCopyrighted = extractDatesCopyrighted(iiiRecord);
		swordMetadata.add(SwordMetadata.DC_DATE_COPYRIGHT, datesCopyrighted);

		// publishers
		List<String> publishers = extractPublishers(iiiRecord);
		swordMetadata.add(SwordMetadata.DC_PUBLISHER, publishers);

		// dates published
		List<String> datesPublished = extractDatesPublished(iiiRecord);
		swordMetadata.add(SwordMetadata.DC_DATE_ISSUED, datesPublished);

		// summaries
		List<String> summaries = extractSummaries(iiiRecord);
		swordMetadata.add(SwordMetadata.DC_DESCRIPTION, summaries);

		// table of contents
		List<String> tableOfContents = extractTableOfContents(iiiRecord);
		swordMetadata.add(SwordMetadata.DC_DESCRIPTION_TABLEOFCONTENTS, tableOfContents);

		// format extents
		List<String> formatExtents = extractFormatExtents(iiiRecord);
		swordMetadata.add(SwordMetadata.DC_FORMAT_EXTENT, formatExtents);
		
		// format mediums
		List<String> formatMediums = extractFormatMediums(iiiRecord);
		swordMetadata.add(SwordMetadata.DC_FORMAT_MEDIUM, formatMediums);

		// languages
		List<String> languages = extractLanguages(iiiRecord);
		swordMetadata.add(SwordMetadata.DC_LANGUAGE, languages);

		// access rights
		List<String> accessRights = extractAccessRights(iiiRecord);
		swordMetadata.add(SwordMetadata.DCTERMS_ACCESSRIGHTS, accessRights);

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

		
		return swordMetadata;
	}

}
