/**
 * 
 */
package au.edu.anu.dspace.client.swordv2.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

/**
 * @author Rahul Khanna
 *
 */
public class SwordMetadata extends TreeMap<String, List<String>> {

	private static final long serialVersionUID = 1L;
	private static final String[] TITLE_FIELDS = {"dc-title", "title", "dc.title" };

	public static final String DC_CONTRIBUTOR_ADVISOR = "dc-contributor-advisor";
	public static final String DC_CONTRIBUTOR_AUTHOR = "dc-contributor-author";
	public static final String DC_CONTRIBUTOR_EDITOR = "dc-contributor-editor";
	public static final String DC_CONTRIBUTOR_ILLUSTRATOR = "dc-contributor-illustrator";
	public static final String DC_CONTRIBUTOR_OTHER = "dc-contributor-other";
	public static final String DC_CONTRIBUTOR = "dc-contributor";
	public static final String DC_COVERAGE_SPATIAL = "dc-coverage-spatial";
	public static final String DC_COVERAGE_TEMPORAL = "dc-coverage-temporal";
	public static final String DC_CREATOR = "dc-creator";
	public static final String DC_DATE_ACCESSIONED = "dc-date-accessioned";
	public static final String DC_DATE_AVAILABLE = "dc-date-available";
	public static final String DC_DATE_COPYRIGHT = "dc-date-copyright";
	public static final String DC_DATE_CREATED = "dc-date-created";
	public static final String DC_DATE_ISSUED = "dc-date-issued";
	public static final String DC_DATE_SUBMITTED = "dc-date-submitted";
	public static final String DC_DATE_UPDATED = "dc-date-updated";
	public static final String DC_DATE = "dc-date";
	public static final String DC_DC_IDENTIFIERS = "dc-dc-identifiers";
	public static final String DC_DESCRIPTION_ABSTRACT = "dc-description-abstract";
	public static final String DC_DESCRIPTION_PROVENANCE = "dc-description-provenance";
	public static final String DC_DESCRIPTION_SPONSORSHIP = "dc-description-sponsorship";
	public static final String DC_DESCRIPTION_STATEMENTOFRESPONSIBILITY = "dc-description-statementofresponsibility";
	public static final String DC_DESCRIPTION_TABLEOFCONTENTS = "dc-description-tableofcontents";
	public static final String DC_DESCRIPTION_URI = "dc-description-uri";
	public static final String DC_DESCRIPTION_VERSION = "dc-description-version";
	public static final String DC_DESCRIPTION = "dc-description";
	public static final String DC_FOR = "dc-FoR";
	public static final String DC_FORMAT_EXTENT = "dc-format-extent";
	public static final String DC_FORMAT_MEDIUM = "dc-format-medium";
	public static final String DC_FORMAT_MIMETYPE = "dc-format-mimetype";
	public static final String DC_FORMAT = "dc-format";
	public static final String DC_IDENTIFIER_CITATION = "dc-identifier-citation";
	public static final String DC_IDENTIFIER_GOVDOC = "dc-identifier-govdoc";
	public static final String DC_IDENTIFIER_ISBN = "dc-identifier-isbn";
	public static final String DC_IDENTIFIER_ISMN = "dc-identifier-ismn";
	public static final String DC_IDENTIFIER_ISSN = "dc-identifier-issn";
	public static final String DC_IDENTIFIER_OTHER = "dc-identifier-other";
	public static final String DC_IDENTIFIER_SICI = "dc-identifier-sici";
	public static final String DC_IDENTIFIER_SLUG = "dc-identifier-slug";
	public static final String DC_IDENTIFIER_URI = "dc-identifier-uri";
	public static final String DC_IDENTIFIER = "dc-identifier";
	public static final String DC_LANGUAGE_ISO = "dc-language-iso";
	public static final String DC_LANGUAGE_RFC3066 = "dc-language-rfc3066";
	public static final String DC_LANGUAGE = "dc-language";
	public static final String DC_PROVENANCE = "dc-provenance";
	public static final String DC_PUBLISHER = "dc-publisher";
	public static final String DC_RELATION_HASPART = "dc-relation-haspart";
	public static final String DC_RELATION_HASVERSION = "dc-relation-hasversion";
	public static final String DC_RELATION_ISBASEDON = "dc-relation-isbasedon";
	public static final String DC_RELATION_ISFORMATOF = "dc-relation-isformatof";
	public static final String DC_RELATION_ISPARTOF = "dc-relation-ispartof";
	public static final String DC_RELATION_ISPARTOFSERIES = "dc-relation-ispartofseries";
	public static final String DC_RELATION_ISREFERENCEDBY = "dc-relation-isreferencedby";
	public static final String DC_RELATION_ISREPLACEDBY = "dc-relation-isreplacedby";
	public static final String DC_RELATION_ISVERSIONOF = "dc-relation-isversionof";
	public static final String DC_RELATION_REPLACES = "dc-relation-replaces";
	public static final String DC_RELATION_REQUIRES = "dc-relation-requires";
	public static final String DC_RELATION_URI = "dc-relation-uri";
	public static final String DC_RELATION = "dc-relation";
	public static final String DC_RIGHTS_HOLDER = "dc-rights-holder";
	public static final String DC_RIGHTS_LICENSE = "dc-rights-license";
	public static final String DC_RIGHTS_URI = "dc-rights-uri";
	public static final String DC_RIGHTS = "dc-rights";
	public static final String DC_SOURCE_URI = "dc-source-uri";
	public static final String DC_SOURCE = "dc-source";
	public static final String DC_SUBJECT_CLASSIFICATION = "dc-subject-classification";
	public static final String DC_SUBJECT_DDC = "dc-subject-ddc";
	public static final String DC_SUBJECT_LCC = "dc-subject-lcc";
	public static final String DC_SUBJECT_LCSH = "dc-subject-lcsh";
	public static final String DC_SUBJECT_MESH = "dc-subject-mesh";
	public static final String DC_SUBJECT_OTHER = "dc-subject-other";
	public static final String DC_SUBJECT = "dc-subject";
	public static final String DC_TITLE_ALTERNATIVE = "dc-title-alternative";
	public static final String DC_TITLE = "dc-title";
	public static final String DC_TYPE = "dc-type";
	
	public static final String DCTERMS_ABSTRACT = "dcterms-abstract";
	public static final String DCTERMS_ACCESSRIGHTS = "dcterms-accessRights";
	public static final String DCTERMS_ACCRUALMETHOD = "dcterms-accrualMethod";
	public static final String DCTERMS_ACCRUALPERIODICITY = "dcterms-accrualPeriodicity";
	public static final String DCTERMS_ACCRUALPOLICY = "dcterms-accrualPolicy";
	public static final String DCTERMS_ALTERNATIVE = "dcterms-alternative";
	public static final String DCTERMS_AUDIENCE = "dcterms-audience";
	public static final String DCTERMS_AVAILABLE = "dcterms-available";
	public static final String DCTERMS_BIBLIOGRAPHICCITATION = "dcterms-bibliographicCitation";
	public static final String DCTERMS_COMFORMSTO = "dcterms-comformsTo";
	public static final String DCTERMS_CONTRIBUTOR = "dcterms-contributor";
	public static final String DCTERMS_COVERAGE = "dcterms-coverage";
	public static final String DCTERMS_CREATED = "dcterms-created";
	public static final String DCTERMS_CREATOR = "dcterms-creator";
	public static final String DCTERMS_DATE = "dcterms-date";
	public static final String DCTERMS_DATEACCEPTED = "dcterms-dateAccepted";
	public static final String DCTERMS_DATECOPYRIGHTED = "dcterms-dateCopyrighted";
	public static final String DCTERMS_DATESUBMITTED = "dcterms-dateSubmitted";
	public static final String DCTERMS_DESCRIPTION = "dcterms-description";
	public static final String DCTERMS_EDUCATIONLEVEL = "dcterms-educationLevel";
	public static final String DCTERMS_EXTENT = "dcterms-extent";
	public static final String DCTERMS_FORMAT = "dcterms-format";
	public static final String DCTERMS_HASFORMAT = "dcterms-hasFormat";
	public static final String DCTERMS_HASPART = "dcterms-hasPart";
	public static final String DCTERMS_HASVERSION = "dcterms-hasVersion";
	public static final String DCTERMS_IDENTIFIER = "dcterms-identifier";
	public static final String DCTERMS_INSTRUCTIONALMETHOD = "dcterms-instructionalMethod";
	public static final String DCTERMS_ISFORMATOF = "dcterms-isFormatOf";
	public static final String DCTERMS_ISPARTOF = "dcterms-isPartOf";
	public static final String DCTERMS_ISREFERENCEDBY = "dcterms-isReferencedBy";
	public static final String DCTERMS_ISREPLACEDBY = "dcterms-isReplacedBy";
	public static final String DCTERMS_ISREQUIREDBY = "dcterms-isRequiredBy";
	public static final String DCTERMS_ISSUED = "dcterms-issued";
	public static final String DCTERMS_ISVERSIONOF = "dcterms-isVersionOf";
	public static final String DCTERMS_LANGUAGE = "dcterms-language";
	public static final String DCTERMS_LICENSE = "dcterms-license";
	public static final String DCTERMS_MEDIATOR = "dcterms-mediator";
	public static final String DCTERMS_MEDIUM = "dcterms-medium";
	public static final String DCTERMS_MODIFIED = "dcterms-modified";
	public static final String DCTERMS_PROVENANCE = "dcterms-provenance";
	public static final String DCTERMS_PUBLISHER = "dcterms-publisher";
	public static final String DCTERMS_REFERENCES = "dcterms-references";
	public static final String DCTERMS_RELATION = "dcterms-relation";
	public static final String DCTERMS_REPLACES = "dcterms-replaces";
	public static final String DCTERMS_REQUIRES = "dcterms-requires";
	public static final String DCTERMS_RIGHTS = "dcterms-rights";
	public static final String DCTERMS_RIGHTSHOLDER = "dcterms-rightsHolder";
	public static final String DCTERMS_SOURCE = "dcterms-source";
	public static final String DCTERMS_SPATIAL = "dcterms-spatial";
	public static final String DCTERMS_SUBJECT = "dcterms-subject";
	public static final String DCTERMS_TABLEOFCONTENTS = "dcterms-tableOfContents";
	public static final String DCTERMS_TEMPORAL = "dcterms-temporal";
	public static final String DCTERMS_TITLE = "dcterms-title";
	public static final String DCTERMS_TYPE = "dcterms-type";
	public static final String DCTERMS_VALID = "dcterms-valid";
	
	public static final String LOCAL_BIBLIOGRAPHICCITATION_ISSUE = "local-bibliographicCitation-issue";
	public static final String LOCAL_BIBLIOGRAPHICCITATION_LASTPAGE = "local-bibliographicCitation-lastpage";
	public static final String LOCAL_BIBLIOGRAPHICCITATION_PLACEOFPUBLICATION = "local-bibliographicCitation-placeofpublication";
	public static final String LOCAL_BIBLIOGRAPHICCITATION_STARTPAGE = "local-bibliographicCitation-startpage";
	public static final String LOCAL_CITATION = "local-citation";
	public static final String LOCAL_COLLECTION = "local-collection";
	public static final String LOCAL_CONTRIBUTOR_AFFILIATION = "local-contributor-affiliation";
	public static final String LOCAL_CONTRIBUTOR_AUTHOREMAIL = "local-contributor-authoremail";
	public static final String LOCAL_CONTRIBUTOR_AUTHORUID = "local-contributor-authoruid";
	public static final String LOCAL_CONTRIBUTOR_COPYRIGHTHOLDER = "local-contributor-copyrightholder";
	public static final String LOCAL_CONTRIBUTOR_COPYRIGHTHOLDERCONTACT = "local-contributor-copyrightholdercontact";
	public static final String LOCAL_CONTRIBUTOR_DEPARTMENT = "local-contributor-department";
	public static final String LOCAL_CONTRIBUTOR_INSTITUTION = "local-contributor-institution";
	public static final String LOCAL_CONTRIBUTOR_ROLE = "local-contributor-role";
	public static final String LOCAL_CONTRIBUTOR_SUPERVISOR = "local-contributor-supervisor";
	public static final String LOCAL_CONTRIBUTOR_SUPERVISORCONTACT = "local-contributor-supervisorcontact";
	public static final String LOCAL_DATE_EMBARGOPERIODMONTHS = "local-date-embargoperiodmonths";
	public static final String LOCAL_DESCRIPTION_EMBARGO = "local-description-embargo";
	public static final String LOCAL_DESCRIPTION_NOTES = "local-description-notes";
	public static final String LOCAL_DESCRIPTION_REFEREED = "local-description-refereed";
	public static final String LOCAL_FORMAT_EXTENTPAGES = "local-format-extentpages";
	public static final String LOCAL_IDENTIFIER_ABSFOR = "local-identifier-absfor";
	public static final String LOCAL_IDENTIFIER_ABSSEO = "local-identifier-absseo";
	public static final String LOCAL_IDENTIFIER_ADTID = "local-identifier-adtid";
	public static final String LOCAL_IDENTIFIER_ARIESPUBLICATION = "local-identifier-ariespublication";
	public static final String LOCAL_IDENTIFIER_CITATIONMONTH = "local-identifier-citationmonth";
	public static final String LOCAL_IDENTIFIER_CITATIONNUMBER = "local-identifier-citationnumber";
	public static final String LOCAL_IDENTIFIER_CITATIONPAGES = "local-identifier-citationpages";
	public static final String LOCAL_IDENTIFIER_CITATIONPUBLICATION = "local-identifier-citationpublication";
	public static final String LOCAL_IDENTIFIER_CITATIONVOLUME = "local-identifier-citationvolume";
	public static final String LOCAL_IDENTIFIER_CITATIONYEAR = "local-identifier-citationyear";
	public static final String LOCAL_IDENTIFIER_DOI = "local-identifier-doi";
	public static final String LOCAL_IDENTIFIER_EPRINTID = "local-identifier-eprintid";
	public static final String LOCAL_IDENTIFIER_ESSN = "local-identifier-essn";
	public static final String LOCAL_IDENTIFIER_OBJECTCITATION = "local-identifier-objectCitation";
	public static final String LOCAL_IDENTIFIER_SCOPUSID = "local-identifier-scopusID";
	public static final String LOCAL_IDENTIFIER_SHEETNUMBER = "local-identifier-sheetNumber";
	public static final String LOCAL_IDENTIFIER_THOMSONID = "local-identifier-thomsonID";
	public static final String LOCAL_IDENTIFIER_UIDSUBMITTEDBY = "local-identifier-uidSubmittedBy";
	public static final String LOCAL_PUBLISHER_NAME = "local-publisher-name";
	public static final String LOCAL_PUBLISHER_URL = "local-publisher-url";
	public static final String LOCAL_RELATION_ISCOMMENTARYON = "local-relation-iscommentaryon";
	public static final String LOCAL_REQUEST_EMAIL = "local-request-email";
	public static final String LOCAL_REQUEST_NAME = "local-request-name";
	public static final String LOCAL_RIGHTS_ISPUBLISHED = "local-rights-ispublished";
	public static final String LOCAL_TITLE_CHAPTER = "local-title-chapter";
	public static final String LOCAL_TYPE_DEGREE = "local-type-degree";
	public static final String LOCAL_TYPE_PUBLISHER = "local-type-publisher";
	public static final String LOCAL_TYPE_STATUS = "local-type-status";

	
	public void add(String fieldName, Collection<String> fieldValues) {
		for (String fieldValue : fieldValues) {
			add(fieldName, fieldValue);
		}
	}
	
	public void add(String fieldName, String fieldValue) {
		Objects.requireNonNull(fieldName);
		Objects.requireNonNull(fieldValue);
		
		if (fieldValue.length() > 0) {
			if (this.containsKey(fieldName)) {
				this.get(fieldName).add(fieldValue);
			} else {
				this.put(fieldName, new ArrayList<String>(Arrays.asList(fieldValue)));
			}
		}
	}
	
	public String getTitle() {
		String title = null;
		for (String titleField : TITLE_FIELDS) {
			if (this.containsKey(titleField)) {
				title = this.get(titleField).get(0);
				break;
			}
		}
		return title;
	}
	
}
