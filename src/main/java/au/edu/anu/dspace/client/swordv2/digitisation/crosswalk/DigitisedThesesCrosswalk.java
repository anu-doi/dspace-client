/**
 * 
 */
package au.edu.anu.dspace.client.swordv2.digitisation.crosswalk;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dspace.client.swordv2.digitisation.iiirecord.IIIRECORD;
import au.edu.anu.dspace.client.swordv2.digitisation.iiirecord.MARCSUBFLD;
import au.edu.anu.dspace.client.swordv2.digitisation.iiirecord.VARFLD;

/**
 * @author Rahul Khanna
 *
 */
public class DigitisedThesesCrosswalk extends AbstractCrosswalk {

	private static final Logger log = LoggerFactory.getLogger(DigitisedThesesCrosswalk.class);
	
	@Override
	public Map<String, Set<String>> generateMetadata(IIIRECORD iiiRecord) {
		Map<String, Set<String>> metadata = new HashMap<>();

		String bNumber = iiiRecord.getRecordInfo().getRECORDKEY().trim();
		List<String> titles = new TitleFieldParser().getValues(iiiRecord);
		List<String> datesPublished = new DatePublishedFieldParser().getValues(iiiRecord);
		List<String> publishers = new PublisherFieldParser().getValues(iiiRecord);
		List<String> authors = new AuthorFieldParser().getValues(iiiRecord);
		List<String> formats = new FormatFieldParser().getValues(iiiRecord);
		List<String> subjects = new SubjectFieldParser().getValues(iiiRecord);
		List<String> dissertationNotes = new DissertationNoteParser().getValues(iiiRecord);
		List<String> supervisors = new SupervisorParser().getValues(iiiRecord);
		List<String> colleges = new CollegesParser().getValues(iiiRecord);
		List<String> summaries = new SummaryParser().getValues(iiiRecord);
		
		
		if (titles != null && titles.size() > 0) {
			metadata.put(DC_TITLE, new LinkedHashSet<>(titles));
		}
		if (datesPublished != null && datesPublished.size() > 0) {
			metadata.put(DC_DATE_ISSUED, new LinkedHashSet<>(datesPublished));
		}
		if (publishers != null && publishers.size() > 0) {
			metadata.put(DC_PUBLISHER, new LinkedHashSet<>(publishers));
		}
		if (authors != null && authors.size() > 0) {
			metadata.put(DC_CONTRIBUTOR_AUTHOR, new LinkedHashSet<>(authors));
		}
		if (subjects != null && subjects.size() > 0) {
			metadata.put(DC_SUBJECT, new LinkedHashSet<>(subjects));
		}
		if (formats != null && formats.size() > 0) {
			metadata.put(DC_FORMAT, new LinkedHashSet<>(formats));
		}
		if (bNumber != null) {
			LinkedHashSet<String> identifiers = new LinkedHashSet<>(1);
			identifiers.add(bNumber);
			metadata.put(DC_IDENTIFIER_OTHER, identifiers);
		}
		if (supervisors != null && supervisors.size() > 0) {
			metadata.put(LOCAL_CONTRIBUTOR_SUPERVISOR, new LinkedHashSet<>(supervisors));
		}
		if (colleges != null && colleges.size() > 0) {
			metadata.put(LOCAL_CONTRIBUTOR_AFFILIATION, new LinkedHashSet<>(colleges));
		}
		if (dissertationNotes != null && dissertationNotes.size() > 0) {
			metadata.put(DC_TYPE, new LinkedHashSet<>(dissertationNotes));
		}
		if (summaries != null && summaries.size() > 0) {
			metadata.put(DC_DESCRIPTION_ABSTRACT, new LinkedHashSet<>(summaries));
		}

		return metadata;
	}
	
	
	

	private static interface FieldParser {
		List<String> getValues(IIIRECORD iiiRecord);
	}

	private static abstract class AbstractFieldParser implements FieldParser {
		protected int parseInt(String str) {
			return Integer.parseInt(str, 10);
		}

		protected void sortTagFieldsBySequence(List<VARFLD> marcTagVarFields) {
			marcTagVarFields.sort(new Comparator<VARFLD>() {

				@Override
				public int compare(VARFLD o1, VARFLD o2) {
					int o1SeqNum = parseInt(o1.getHEADER().getSEQUENCENUM());
					int o2SeqNum = parseInt(o2.getHEADER().getSEQUENCENUM());
					if (o1SeqNum < o2SeqNum) {
						return -1;
					} else if (o1SeqNum > o2SeqNum) {
						return 1;
					} else {
						return 0;
					}
				}

			});
		}

		protected String concatenateSubfields(List<MARCSUBFLD> marcSubFields, String subfieldIndicators,
				String subfieldSeparator) {
			StringBuilder conc = new StringBuilder();
			char[] siChars = subfieldIndicators.toCharArray();
			for (int iSubFldIndicator = 0; iSubFldIndicator < siChars.length; iSubFldIndicator++) {
				for (int iMarcSubField = 0; iMarcSubField < marcSubFields.size(); iMarcSubField++) {
					// for (MARCSUBFLD subfield : marcSubFields) {
					if (marcSubFields.get(iMarcSubField).getSUBFIELDINDICATOR()
							.equals(String.valueOf(siChars[iSubFldIndicator]))) {
						conc.append(marcSubFields.get(iMarcSubField).getSUBFIELDDATA());
						if (iMarcSubField < marcSubFields.size() - 1) {
							conc.append(subfieldSeparator);
						}
						// not breaking as there may be multiple subfields with
						// same subfieldindicator
					}
				}
			}
			return conc.toString().trim();
		}

		protected List<VARFLD> getVarFields(IIIRECORD iiiRecord, String[] marcTags) {
			List<VARFLD> marcTagVarFields = new ArrayList<>();

			for (VARFLD varFld : iiiRecord.getVarFields()) {
				String curMarcTag = varFld.getMarcInfo().getMARCTAG();

				for (String iMarcTag : marcTags) {
					if (curMarcTag.equals(iMarcTag)) {
						marcTagVarFields.add(varFld);
					}
				}
			}
			return marcTagVarFields;
		}
		
		protected String stripLeadingTrailingChars(String original, String leadingChars, String trailingChars) {
			StringBuilder sb = new StringBuilder(original);
			
			// strip leading chars
			boolean repeat = false;
			do {
				char firstChar = sb.charAt(0);
				repeat = false;
				for (char c : leadingChars.toCharArray()) {
					if (firstChar == c) {
						sb.deleteCharAt(0);
						repeat = true;
						break;
					}
				}
			} while (repeat);

			// strip trailing chars
			repeat = false;
			do {
				char lastChar = sb.charAt(sb.length() - 1);
				repeat = false;
				for (char c : trailingChars.toCharArray()) {
					if (lastChar == c) {
						sb.deleteCharAt(sb.length() - 1);
						repeat = true;
						break;
					}
				}
			} while (repeat);
			
			return sb.toString();
		}
	}

	private static class TitleFieldParser extends AbstractFieldParser {

		private String[] marcTags = { "245" };

		@Override
		public List<String> getValues(IIIRECORD iiiRecord) {
			List<String> values = new ArrayList<>();
			List<VARFLD> marcTagVarFields = getVarFields(iiiRecord, marcTags);
			sortTagFieldsBySequence(marcTagVarFields);
			for (VARFLD marcTagVarField : marcTagVarFields) {
				String value = concatenateSubfields(marcTagVarField.getMarcSubfld(), "ab", " ");
				if (value != null && value.length() > 0) {
					values.add(stripLeadingTrailingChars(value, " ", " /"));
				}
			}
			return values;
		}

	}

	private static class AuthorFieldParser extends AbstractFieldParser {

		private String[] marcTags = { "100" };

		@Override
		public List<String> getValues(IIIRECORD iiiRecord) {
			List<String> values = new ArrayList<>();
			List<VARFLD> marcTagVarFields = getVarFields(iiiRecord, marcTags);
			sortTagFieldsBySequence(marcTagVarFields);
			for (VARFLD marcTagVarField : marcTagVarFields) {
				String value = concatenateSubfields(marcTagVarField.getMarcSubfld(), "a", "");
				if (value != null && value.length() > 0) {
					values.add(stripLeadingTrailingChars(value, " ", " ,"));
				}
			}
			return values;
		}

	}

	private static class DatePublishedFieldParser extends AbstractFieldParser {
		private String[] marcTags = { "260", "264" };

		@Override
		public List<String> getValues(IIIRECORD iiiRecord) {
			List<String> values = new ArrayList<>();
			List<VARFLD> marcTagVarFields = getVarFields(iiiRecord, marcTags);
			sortTagFieldsBySequence(marcTagVarFields);
			for (VARFLD marcTagVarField : marcTagVarFields) {
				String value = concatenateSubfields(marcTagVarField.getMarcSubfld(), "c", "");
				if (value != null && value.length() > 0) {
					values.add(stripLeadingTrailingChars(value, " [", " .]"));
				}
			}
			return values;
		}

	}

	private static class PublisherFieldParser extends AbstractFieldParser {
		private String[] marcTags = { "260", "264" };

		@Override
		public List<String> getValues(IIIRECORD iiiRecord) {
			List<String> values = new ArrayList<>();
			List<VARFLD> marcTagVarFields = getVarFields(iiiRecord, marcTags);
			sortTagFieldsBySequence(marcTagVarFields);
			for (VARFLD marcTagVarField : marcTagVarFields) {
				String value = concatenateSubfields(marcTagVarField.getMarcSubfld(), "ab", " ");
				if (value != null && value.length() > 0) {
					values.add(stripLeadingTrailingChars(value, " ", " ,"));
				}
			}
			return values;
		}
	}

	private static class FormatFieldParser extends AbstractFieldParser {
		private String[] marcTags = { "300" };

		@Override
		public List<String> getValues(IIIRECORD iiiRecord) {
			List<String> values = new ArrayList<>();
			List<VARFLD> marcTagVarFields = getVarFields(iiiRecord, marcTags);
			sortTagFieldsBySequence(marcTagVarFields);
			for (VARFLD marcTagVarField : marcTagVarFields) {
				String value = concatenateSubfields(marcTagVarField.getMarcSubfld(), "a", " ");
				if (value != null && value.length() > 0) {
					values.add(stripLeadingTrailingChars(value, " ", " +"));
				}
			}
			return values;
		}
	}

	private static class SubjectFieldParser extends AbstractFieldParser {
		private String[] marcTags = { "600", "610", "611", "630", "648", "650", "651", "653", "654", "655", "656", "657", "658", "662" };

		@Override
		public List<String> getValues(IIIRECORD iiiRecord) {
			List<String> values = new ArrayList<>();
			List<VARFLD> marcTagVarFields = getVarFields(iiiRecord, marcTags);
			sortTagFieldsBySequence(marcTagVarFields);
			for (VARFLD marcTagVarField : marcTagVarFields) {
				String value = concatenateSubfields(marcTagVarField.getMarcSubfld(), "axy", " ");
				if (value != null && value.length() > 0) {
					values.add(stripLeadingTrailingChars(value, " ", " "));
				}
			}
			return values;
		}
	}
	
	private static class DissertationNoteParser extends AbstractFieldParser {
		private String[] marcTags = { "502" };

		@Override
		public List<String> getValues(IIIRECORD iiiRecord) {
			List<String> values = new ArrayList<>();
			List<VARFLD> marcTagVarFields = getVarFields(iiiRecord, marcTags);
			sortTagFieldsBySequence(marcTagVarFields);
			for (VARFLD marcTagVarField : marcTagVarFields) {
				String value = concatenateSubfields(marcTagVarField.getMarcSubfld(), "a", " ");
				if (value != null && value.length() > 0) {
					values.add(stripLeadingTrailingChars(value, " ", " "));
				}
			}
			return values;
		}
		
	}
	
	private static class SupervisorParser extends AbstractFieldParser {
		private String[] marcTags = { "700" };
		
		@Override
		public List<String> getValues(IIIRECORD iiiRecord) {
			List<String> values = new ArrayList<>();
			List<VARFLD> marcTagVarFields = getVarFields(iiiRecord, marcTags);
			sortTagFieldsBySequence(marcTagVarFields);
			for (VARFLD marcTagVarField : marcTagVarFields) {
				String value = concatenateSubfields(marcTagVarField.getMarcSubfld(), "a", " ");
				if (value != null && value.length() > 0) {
					values.add(stripLeadingTrailingChars(value, " ", " ,"));
				}
			}
			return values;
	
		}
		
	}
	
	private static class CollegesParser extends AbstractFieldParser {
		private String[] marcTags = { "710" };
		
		@Override
		public List<String> getValues(IIIRECORD iiiRecord) {
			List<String> values = new ArrayList<>();
			List<VARFLD> marcTagVarFields = getVarFields(iiiRecord, marcTags);
			sortTagFieldsBySequence(marcTagVarFields);
			for (VARFLD marcTagVarField : marcTagVarFields) {
				String value = concatenateSubfields(marcTagVarField.getMarcSubfld(), "ab", " ");
				if (value != null && value.length() > 0) {
					values.add(stripLeadingTrailingChars(value, " ", " "));
				}
			}
			return values;
	
		}
		
	}
	

	private static class SummaryParser  extends AbstractFieldParser {
		private static final String PARAGRAPH_BREAK = System.lineSeparator() + System.lineSeparator();
		
		private String[] marcTags = { "520" };

		@Override
		public List<String> getValues(IIIRECORD iiiRecord) {
			List<String> values = new ArrayList<>();
			List<VARFLD> marcTagVarFields = getVarFields(iiiRecord, marcTags);
			sortTagFieldsBySequence(marcTagVarFields);
			for (VARFLD marcTagVarField : marcTagVarFields) {
				String value = concatenateSubfields(marcTagVarField.getMarcSubfld(), "ab", " ");
				if (value != null && value.length() > 0) {
					values.add(stripLeadingTrailingChars(value, " ", " "));
				}
			}
			
			if (values.size() > 0) {
				// concatenate all summaries into one value separated by a line separator
				StringBuilder concatenatedSummary = new StringBuilder();
				for (int i = 0; i < values.size(); i++) {
					concatenatedSummary.append(values.get(i));
					if (i < values.size() - 1) {
						concatenatedSummary.append(PARAGRAPH_BREAK);
					}
				}
				values = new ArrayList<>(1);
				values.add(concatenatedSummary.toString());
			}
			return values;
		}
		
	}
	
}
