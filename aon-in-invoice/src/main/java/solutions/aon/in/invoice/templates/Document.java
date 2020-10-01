package solutions.aon.in.invoice.templates;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Document {

	public static enum DocumentType {
		LEGAL_PERSON_NIF{
			@Override
			public boolean accept(String data) {
				Matcher matcher = Pattern.compile("^[A-JUV](\\d{7})([0-9])$").matcher(data.toUpperCase());
				if (matcher.matches()) {
					return getCifCtrlDigit(matcher.group(1)) == Integer.parseInt(matcher.group(2));
				}
				matcher = Pattern.compile("^[NPQRSW](\\d{7})([A-J])$").matcher(data.toUpperCase());
				if (matcher.matches()) {
					return "JABCDEFGHI".charAt(getCifCtrlDigit(matcher.group(1))) == matcher.group(2).charAt(0);
				}
				return false;
			}  
			
			private int getCifCtrlDigit(String str) {
				int digits[] = new int[str.length()];
				for (int i = 0; i < str.length(); i++) {
					digits[i] = Integer.parseInt(String.valueOf(str.charAt(i)));
				}
				return getCifCtrlDigit(digits);
			}

			private int getCifCtrlDigit(int digits[]) {
				int a = digits[1] + digits[3] + digits[5];
				int b1 = digits[0] * 2;
				int b3 = digits[2] * 2;
				int b5 = digits[4] * 2;
				int b7 = digits[6] * 2;
				int u1 = b1 % 10;
				int d1 = (b1 - u1) / 10;
				int u3 = b3 % 10;
				int d3 = (b3 - u3) / 10;
				int u5 = b5 % 10;
				int d5 = (b5 - u5) / 10;
				int u7 = b7 % 10;
				int d7 = (b7 - u7) / 10;
				int b = u1 + d1 + u3 + d3 + u5 + d5 + u7 + d7;
				int c = a + b;
				int e = c % 10;
				int d = e != 0 ? 10 - e : 0;
				return d;
			}
			
		},
		DNI{
			private static final String ACCEPTED_PATTERN = "^(\\d{8})([A-HJ-NP-TV-Z])$";
			
			@Override
			public boolean accept(String data) {
				return is(data, ACCEPTED_PATTERN);
			}
		},
		NIE{
			private static final String ACCEPTED_PATTERN = "^([XYZ])(\\d{7})([A-HJ-NP-TV-Z])$";

			@Override
			public boolean accept(String data) {
				Matcher matcher = Pattern.compile(ACCEPTED_PATTERN).matcher(data.toUpperCase());
				if (matcher.matches()) {
					char match1 = matcher.group(1).charAt(0);
					int matchInt1 = Integer.parseInt(XYZ.get(match1) + matcher.group(2));
					char match3 = matcher.group(3).charAt(0);
					return "TRWAGMYFPDXBNJZSQVHLCKE".charAt(matchInt1 % 23) == match3;
				}
				return false;
			}
		}, 
		NATURAL_PERSON_NIF{
			private static final String ACCEPTED_PATTERN = "^[KLM](\\d{7})([A-HJ-NP-TV-Z])$";
			
			@Override
			public boolean accept(String data) {
				return is(data, ACCEPTED_PATTERN);
			}
		},
		;
		
		public abstract boolean accept(String data);
		
		private static  final Map<Character, Character> XYZ = new HashMap<Character, Character>() {
			private static final long serialVersionUID = -6313954948473568524L;
			{
				put('X', '0');
				put('Y', '1');
				put('Z', '2');
			}
		};

		private static boolean is(String str, String regex) {
			Matcher matcher = Pattern.compile(regex).matcher(str.toUpperCase());
			if (matcher.matches()) {
				int match1 = Integer.parseInt(matcher.group(1));
				char match2 = matcher.group(2).charAt(0);
				return "TRWAGMYFPDXBNJZSQVHLCKE".charAt(match1 % 23) == match2;
			}
			return false;
		}
	}

	private String data;
	private DocumentType type;

	public Document(String data, DocumentType type) {
		super();
		this.data = data;
		this.type = type;
	}
	
	public String getData() {
		return data;
	}
	public DocumentType getType() {
		return type;
	}
	
	public static Document parse(String data) {
		for ( DocumentType type : DocumentType.values()) {
			if (type.accept(data)) {
				return new Document(data, type);
			}
		}
		return null;
	}

}