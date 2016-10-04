/**
 * 
 */
package au.edu.anu.dspace.client.swordv2.digitisation.iiirecord.parser;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import au.edu.anu.dspace.client.swordv2.digitisation.iiirecord.IIIRECORD;
import au.edu.anu.dspace.client.swordv2.digitisation.iiirecord.MARCSUBFLD;
import au.edu.anu.dspace.client.swordv2.digitisation.iiirecord.VARFLD;

/**
 * @author Rahul Khanna
 *
 */
public class IIIRecordParser {

	private static final Logger log = LoggerFactory.getLogger(IIIRecordParser.class);

	private String bNumber;
	private List<String> titles;
	private List<String> datesPublished;
	private List<String> publishers;
	private List<String> authors;
	private List<String> formats;
	private List<String> subjects;
	private List<String> dissertationNotes;
	private List<String> supervisors;
	private List<String> colleges;
	private List<String> summaries;
	
	
	public IIIRecordParser(IIIRECORD iiiRecord) {
		bNumber = iiiRecord.getRecordInfo().getRECORDKEY().trim();
		titles = new TitleFieldParser().getValues(iiiRecord);
		datesPublished = new DatePublishedFieldParser().getValues(iiiRecord);
		publishers = new PublisherFieldParser().getValues(iiiRecord);
		authors = new AuthorFieldParser().getValues(iiiRecord);
		formats = new FormatFieldParser().getValues(iiiRecord);
		subjects = new SubjectFieldParser().getValues(iiiRecord);
		dissertationNotes = new DissertationNoteParser().getValues(iiiRecord);
		supervisors = new SupervisorParser().getValues(iiiRecord);
		colleges = new CollegesParser().getValues(iiiRecord);
		summaries = new SummaryParser().getValues(iiiRecord);
	}

	public String getBNumber() {
		return bNumber;
	}

	public List<String> getTitles() {
		return titles;
	}

	public List<String> getDatesPublished() {
		return datesPublished;
	}

	public List<String> getPublishers() {
		return publishers;
	}

	public List<String> getAuthors() {
		return authors;
	}

	public List<String> getDescriptions() {
		return formats;
	}

	public List<String> getSubjects() {
		return subjects;
	}

	public List<String> getDissertationNotes() {
		return dissertationNotes;
	}

	public List<String> getSupervisors() {
		return supervisors;
	}
	
	public List<String> getColleges() {
		return colleges;
	}

	public List<String> getSummaries() {
		return summaries;
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

		protected List<VARFLD> getVarFields(IIIRECORD iiiRecord, int[] marcTags) {
			List<VARFLD> marcTagVarFields = new ArrayList<>();

			for (VARFLD varFld : iiiRecord.getVarFields()) {
				int curMarcTag = parseInt(varFld.getMarcInfo().getMARCTAG());

				for (int iMarcTag : marcTags) {
					if (curMarcTag == iMarcTag) {
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

		private int[] marcTags = { 245 };

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

		private int[] marcTags = { 100 };

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
		private int[] marcTags = { 260, 264 };

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
		private int[] marcTags = { 260, 264 };

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
		private int[] marcTags = { 300 };

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
		private int[] marcTags = { 600, 610, 611, 630, 648, 650, 651, 653, 654, 655, 656, 657, 658, 662 };

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
		private int[] marcTags = { 502 };

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
		private int[] marcTags = { 700 };
		
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
		private int[] marcTags = { 710 };
		
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
		private int[] marcTags = { 520 };

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
						concatenatedSummary.append(System.lineSeparator() + System.lineSeparator());
					}
				}
				values = new ArrayList<>(1);
				values.add(concatenatedSummary.toString());
			}
			return values;
		}
		
	}
}
