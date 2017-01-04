/**
 * 
 */
package au.edu.anu.dspace.client.swordv2.digitisation.crosswalk;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import au.edu.anu.dspace.client.swordv2.digitisation.iiirecord.IIIRECORD;
import au.edu.anu.dspace.client.swordv2.digitisation.iiirecord.MARCSUBFLD;
import au.edu.anu.dspace.client.swordv2.digitisation.iiirecord.VARFLD;

/**
 * @author Rahul Khanna
 *
 */
public abstract class AbstractCrosswalk implements Crosswalk {

	private static final String SUBFIELDS_A_TO_Z = "abcdefghijklmnopqrstuvwxyz";
	
	protected static final String PARAGRAPH_BREAK = System.lineSeparator() + System.lineSeparator();

	private String concatenateSubfields(List<MARCSUBFLD> marcSubFields, String subfieldIndicators,
			String subfieldSeparator) {
		StringBuilder conc = new StringBuilder();
		char[] siChars = subfieldIndicators.toCharArray();
		for (int iSubFldIndicator = 0; iSubFldIndicator < siChars.length; iSubFldIndicator++) {
			for (int iMarcSubField = 0; iMarcSubField < marcSubFields.size(); iMarcSubField++) {
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

	
	private String getIndicator1(VARFLD marcTagVarField) {
		return marcTagVarField.getMarcInfo().getINDICATOR1();
	}


	private String getIndicator2(VARFLD marcTagVarField) {
		return marcTagVarField.getMarcInfo().getINDICATOR2();
	}
	
	
	private List<VARFLD> getVarFields(IIIRECORD iiiRecord, String... marcTags) {
		List<VARFLD> marcTagVarFields = new ArrayList<>();

		for (VARFLD varFld : iiiRecord.getVarFields()) {
			String curMarcTag = varFld.getMarcInfo().getMARCTAG();

			for (String iMarcTag : marcTags) {
				if (curMarcTag.equals(iMarcTag)) {
					marcTagVarFields.add(varFld);
				}
			}
		}
		sortTagFieldsBySequence(marcTagVarFields);
		return marcTagVarFields;
	}
	
	private int parseInt(String str) {
		return Integer.parseInt(str);
	}


	private void sortTagFieldsBySequence(List<VARFLD> marcTagVarFields) {
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
	
	private String stripLeadingTrailingChars(String original, String leadingChars, String trailingChars) {
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

	// =================================

	protected List<String> extractAccessRights(IIIRECORD iiiRecord) {
		List<String> values = new ArrayList<>();
		for (VARFLD marcTagVarField : getVarFields(iiiRecord, "506")) {
			if (getIndicator1(marcTagVarField).equals("1")) { 
				String value = concatenateSubfields(marcTagVarField.getMarcSubfld(), SUBFIELDS_A_TO_Z, " ");
				if (value != null && value.length() > 0) {
					values.add(stripLeadingTrailingChars(value, " ", " "));
				}
			}
		}
		return values;
	}
	
	protected List<String> extractAccrualMethods(IIIRECORD iiiRecord) {
		List<String> values = new ArrayList<>();
		for (VARFLD marcTagVarField : getVarFields(iiiRecord, "541")) {
			String value = concatenateSubfields(marcTagVarField.getMarcSubfld(), "cd", " ");
			if (value != null && value.length() > 0) {
				values.add(stripLeadingTrailingChars(value, " ", " "));
			}
		}
		return values;
	}
	
	protected List<String> extractAlternativeTitles(IIIRECORD iiiRecord) {
		List<String> values = new ArrayList<>();
		for (VARFLD marcTagVarField : getVarFields(iiiRecord, "130", "210", "240", "242", "246", "730", "740")) {
			String value = concatenateSubfields(marcTagVarField.getMarcSubfld(), SUBFIELDS_A_TO_Z, " ");
			if (value != null && value.length() > 0) {
				values.add(stripLeadingTrailingChars(value, " ", " /"));
			}
		}
		return values;
	}
	
	protected List<String> extractAuthors(IIIRECORD iiiRecord) {
		List<String> values = new ArrayList<>();
		for (VARFLD marcTagVarField : getVarFields(iiiRecord, "100")) {
			String value = concatenateSubfields(marcTagVarField.getMarcSubfld(), "a", "");
			if (value != null && value.length() > 0) {
				values.add(stripLeadingTrailingChars(value, " ", " ,"));
			}
		}
		
		for (VARFLD marcTagVarField : getVarFields(iiiRecord, "700")) {
			if (concatenateSubfields(marcTagVarField.getMarcSubfld(), "e", "").equalsIgnoreCase("author")) {
				String value = concatenateSubfields(marcTagVarField.getMarcSubfld(), "a", "");
				if (value != null && value.length() > 0) {
					values.add(stripLeadingTrailingChars(value, " ", " ,"));
				}
			}
		}
		
		return values;
	}


	protected String extractBNumber(IIIRECORD iiiRecord) {
		String bNumber = iiiRecord.getRecordInfo().getRECORDKEY().trim();
		return bNumber;
	}
	
	protected List<String> extractColleges(IIIRECORD iiiRecord) {
		List<String> values = new ArrayList<>();
		for (VARFLD marcTagVarField : getVarFields(iiiRecord, "710")) {
			String value = concatenateSubfields(marcTagVarField.getMarcSubfld(), "ab", " ");
			if (value != null && value.length() > 0) {
				values.add(stripLeadingTrailingChars(value, " ", " "));
			}
		}
		return values;
	}
	
	protected List<String> extractDatesCopyrighted(IIIRECORD iiiRecord) {
		List<String> values = new ArrayList<>();
		for (VARFLD marcTagVarField : getVarFields(iiiRecord, "260", "264")) {
			String value = concatenateSubfields(marcTagVarField.getMarcSubfld(), "c", "");
			if (value != null && value.length() > 0) {
				values.add(stripLeadingTrailingChars(value, " [", " .]"));
			}
		}
		return values;
	}
	
	protected List<String> extractDatesPublished(IIIRECORD iiiRecord) {
		List<String> values = new ArrayList<>();
		for (VARFLD marcTagVarField : getVarFields(iiiRecord, "260", "264")) {
			String value = concatenateSubfields(marcTagVarField.getMarcSubfld(), "c", "");
			if (value != null && value.length() > 0) {
				values.add(stripLeadingTrailingChars(value, " [", " .]"));
			}
		}
		return values;
	}
	
	protected List<String> extractDissertationNotes(IIIRECORD iiiRecord) {
		List<String> values = new ArrayList<>();
		for (VARFLD marcTagVarField : getVarFields(iiiRecord, "502")) {
			String value = concatenateSubfields(marcTagVarField.getMarcSubfld(), "a", " ");
			if (value != null && value.length() > 0) {
				values.add(stripLeadingTrailingChars(value, " ", " "));
			}
		}
		return values;
	}
	
	protected List<String> extractFormatExtents(IIIRECORD iiiRecord) {
		List<String> values = new ArrayList<>();
		for (VARFLD marcTagVarField : getVarFields(iiiRecord, "300")) {
			String value = concatenateSubfields(marcTagVarField.getMarcSubfld(), "a", " ");
			if (value != null && value.length() > 0) {
				values.add(stripLeadingTrailingChars(value, " ", " +:"));
			}
		}
		return values;
	}
	
	protected List<String> extractFormatMediums(IIIRECORD iiiRecord) {
		List<String> values = new ArrayList<>();
		for (VARFLD marcTagVarField : getVarFields(iiiRecord, "340")) {
			String value = concatenateSubfields(marcTagVarField.getMarcSubfld(), "a", " ");
			if (value != null && value.length() > 0) {
				values.add(stripLeadingTrailingChars(value, " ", " +"));
			}
		}
		return values;
	}
	
	protected List<String> extractLanguages(IIIRECORD iiiRecord) {
		List<String> values = new ArrayList<>();
		for (VARFLD marcTagVarField : getVarFields(iiiRecord, "546")) {
			String value = concatenateSubfields(marcTagVarField.getMarcSubfld(), SUBFIELDS_A_TO_Z, " ");
			if (value != null && value.length() > 0) {
				values.add(stripLeadingTrailingChars(value, " ", " "));
			}
		}
		return values;
	}
	
	protected List<String> extractPublishers(IIIRECORD iiiRecord) {
		List<String> values = new ArrayList<>();
		for (VARFLD marcTagVarField : getVarFields(iiiRecord, "260", "264")) {
			String value = concatenateSubfields(marcTagVarField.getMarcSubfld(), "ab", " ");
			if (value != null && value.length() > 0) {
				values.add(stripLeadingTrailingChars(value, " ", " ,"));
			}
		}
		return values;

	}
	
	protected List<String> extractSubjectClassifications(IIIRECORD iiiRecord) {
		List<String> values = new ArrayList<>();
		for (VARFLD marcTagVarField : getVarFields(iiiRecord, "084")) {
			String value = concatenateSubfields(marcTagVarField.getMarcSubfld(), SUBFIELDS_A_TO_Z, " ");
			if (value != null && value.length() > 0) {
				values.add(stripLeadingTrailingChars(value, " ", " "));
			}
		}
		return values;
	}
	
	protected List<String> extractSubjectsDdc(IIIRECORD iiiRecord) {
		List<String> values = new ArrayList<>();
		for (VARFLD marcTagVarField : getVarFields(iiiRecord, "082")) {
			String value = concatenateSubfields(marcTagVarField.getMarcSubfld(), SUBFIELDS_A_TO_Z, " ");
			if (value != null && value.length() > 0) {
				values.add(stripLeadingTrailingChars(value, " ", " "));
			}
		}
		return values;
	}


	protected List<String> extractSubjectsLcc(IIIRECORD iiiRecord) {
		List<String> values = new ArrayList<>();
		for (VARFLD marcTagVarField : getVarFields(iiiRecord, "050", "090")) {
			String value = concatenateSubfields(marcTagVarField.getMarcSubfld(), SUBFIELDS_A_TO_Z, " ");
			if (value != null && value.length() > 0) {
				values.add(stripLeadingTrailingChars(value, " ", " "));
			}
		}
		return values;
	}


	protected List<String> extractSubjectsLcsh(IIIRECORD iiiRecord) {
		List<String> values = new ArrayList<>();
		for (VARFLD marcTagVarField : getVarFields(iiiRecord, "600", "610", "611", "630", "650")) {
			String value = concatenateSubfields(marcTagVarField.getMarcSubfld(), SUBFIELDS_A_TO_Z, " ");
			if (value != null && value.length() > 0) {
				values.add(stripLeadingTrailingChars(value, " ", " "));
			}
		}
		for (VARFLD marcTagVarField : getVarFields(iiiRecord, "651")) {
			if (getIndicator2(marcTagVarField).equalsIgnoreCase("0")) {
				String value = concatenateSubfields(marcTagVarField.getMarcSubfld(), SUBFIELDS_A_TO_Z, " ");
				if (value != null && value.length() > 0) {
					values.add(stripLeadingTrailingChars(value, " ", " "));
				}
			}
		}
		return values;
	}
	
	protected List<String> extractAbstracts(IIIRECORD iiiRecord) {
		List<String> values = new ArrayList<>();
		for (VARFLD marcTagVarField : getVarFields(iiiRecord, "520")) {
			if (getIndicator1(marcTagVarField).equals("3")) {
				String value = concatenateSubfields(marcTagVarField.getMarcSubfld(), "ab", " ");
				if (value != null && value.length() > 0) {
					values.add(stripLeadingTrailingChars(value, " ", " "));
				}
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
	
	protected List<String> extractSummaries(IIIRECORD iiiRecord) {
		List<String> values = new ArrayList<>();
		for (VARFLD marcTagVarField : getVarFields(iiiRecord, "520")) {
			String value = concatenateSubfields(marcTagVarField.getMarcSubfld(), SUBFIELDS_A_TO_Z, " ");
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
	
	protected List<String> extractSupervisors(IIIRECORD iiiRecord) {
		List<String> values = new ArrayList<>();
		for (VARFLD marcTagVarField : getVarFields(iiiRecord, "700")) {
			if (concatenateSubfields(marcTagVarField.getMarcSubfld(), "e", "").equalsIgnoreCase("degree supervisor")) {
				String value = concatenateSubfields(marcTagVarField.getMarcSubfld(), "a", " ");
				if (value != null && value.length() > 0) {
					values.add(stripLeadingTrailingChars(value, " ", " ,"));
				}
			}
		}
		return values;
	}
	
	protected List<String> extractTableOfContents(IIIRECORD iiiRecord) {
		List<String> values = new ArrayList<>();
		for (VARFLD marcTagVarField : getVarFields(iiiRecord, "505")) {
			String value = concatenateSubfields(marcTagVarField.getMarcSubfld(), SUBFIELDS_A_TO_Z, " ");
			if (value != null && value.length() > 0) {
				values.add(stripLeadingTrailingChars(value, " ", " "));
			}
		}
		return values;
	}
	
	protected List<String> extractTitles(IIIRECORD iiiRecord) {
		List<String> values = new ArrayList<>();
		for (VARFLD marcTagVarField : getVarFields(iiiRecord, "245")) {
			String value = concatenateSubfields(marcTagVarField.getMarcSubfld(), "ab", " ");
			if (value != null && value.length() > 0) {
				values.add(stripLeadingTrailingChars(value, " ", " /"));
			}
		}
		return values;
	}
}
