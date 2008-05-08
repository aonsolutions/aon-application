package com.code.aon.marketplace.plu;

import java.util.StringTokenizer;

public class FieldUtils {
	
	public static String ZeroFill(int value, int length) {
		return Filler("" + value, "0", length, false);
	}

	public static String SpaceFill(String value, int length) {
		return Filler(value, " ", length, true);
	}

	private static String Filler(String value, String c, int length, boolean right) {
		int initial = value.length();
		for (int i = initial; i<length; i++) {
			if (right) value = "" + value + "" + c + "";
			else value = "" + c + "" + value + "";
		}
		if (value.length() > length) {
			if (right) value = value.substring(0, length - 1);
			else value = value.substring(value.length() - length , length - 1);
		}
	
		return value;
	}

	public static int getFieldLength(String line) {
		// NOMB,  L, T, 0, 1	; Descripcion
		int length = 0;
		StringTokenizer st = new StringTokenizer(line, ",");
		if (st.countTokens() >= 2) { 
			int count = 0;
			while (st.hasMoreElements() && count < 1) {
				st.nextElement();
				count++;
			}
			String ret = "" + st.nextElement();
			ret = ret.trim();
			length = Integer.parseInt(ret);
		}
		return length;
	}
	
	public static String getFieldType(String line) {
		// NOMB,  L, T, 0, 1	; Descripcion
		String type = "N";
		StringTokenizer st = new StringTokenizer(line, ",");
		if (st.countTokens() >= 2) { 
			int count = 0;
			while (st.hasMoreElements() && count < 2) {
				st.nextElement();
				count++;
			}
			String ret = "" + st.nextElement();
			ret = ret.trim();
			type = ret;
		}
		return type;
	}

	public static int getFieldCode(String line) {
		// NOMB,  L, T, 0, 1, C	; Descripcion
		int code = 0;
		try {
			StringTokenizer st = new StringTokenizer(line, ",");
			if (st.countTokens() >= 5) { 
				int count = 0;
				while (st.hasMoreElements() && count < 5) {
					st.nextElement();
					count++;
				}
				String ret = "" + st.nextElement();
				ret = ret.trim();
				if (ret.length() > 3) ret = ret.substring(0,3);
				code = Integer.parseInt(ret);
			}
		}
		catch (Exception e) {}
		return code;
	}

	public static String clearSpecialTags(String text) {
		text = text.replace("\\n", "");
		text = text.replace("\\r", "");
		text = text.replaceAll("\\^.*?;","");
		text = text.replaceAll("\\^.*?\\+","");
		text = text.replaceAll("\\^.*?-","");
		return text.trim();
	}

	public static String obtainPluCode(Integer code) {
		String plu = "";
		String[] first = {"Z","Y","X","W","V","U","T","S","R","Q"};
		int i = 0;
		if (code > 999) {
			i = code / 1000;
		}
		plu = plu + first[i] + "" + zeroFill(code - (i * 1000), 3); 
		return plu;
	}

	private static String zeroFill(int i, int j) {
		String data = "" + i;
		String ret = data;
		for (int x = data.length(); x < j; x++) {
			ret = "0" + ret;
		}
		return ret;
	}

}
