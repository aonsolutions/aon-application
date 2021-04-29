package com.esferalia.aon.in.payroll.pdf.api.toolkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>
 * <b>Description:</b> <i>This toolkit contains the basic methods for String
 * manipulation</i>
 * </p>
 * 
 * @author akrck02
 */
public class StringToolkit {

	/**
	 * <p>
	 * <b>Description:</b> <i>Converts a String into separated words.</i>
	 * </p>
	 * 
	 * @return List of words (String)
	 */
	public static List<String> toWords(String text) {
		ArrayList<String> words = new ArrayList<String>();
		words.addAll(Arrays.asList(text.split("\\s")));
		words.removeIf(p -> p.length() < 1);
		return words;
	}

	/**
	 * Append somthing x times to a String
	 * 
	 * @param text
	 * @param append
	 * @param times
	 * @return
	 */
	public static String append(String text, String append, int times) {
		for (int i = 0; i < times; i++)
			text += append;
		return text;
	}

	/** 
	 * Trims something or returns an empty String
	 * 
	 * @param str - The String to trim
	 * @return [String] Trimmed String or ""
	 */
	public static String trimToEmpty(String str) {
		String res = (isEmpty(str)) ? "" : str.trim();
		return res;
	}

	/**
	 * returns if a String is empty
	 * 
	 * @param str - The String to evaluate
	 * @return [Boolean] if <code>str</code> is empty
	 */
	public static boolean isEmpty(String str) {
		if (str == null)
			return true;
		if (str.trim().equals(""))
			return true;
		return false;
	}
	
	/**
	 * Generates Lorem ipsum text x times
	 * 
	 * @param times - the number of times to repear lorem
	 * @return [String] Lorem ipsum text.
	 */
	public static String lorem(int times) {
		String text = " Lorem ipsum dolor sit amet consectetur adipiscing elit primis quam gravida turpis,"
				+ " vitae dictum velit sociis molestie ad id lacus vel. Platea maecenas sociis est "
				+ "ante aenean vulputate scelerisque potenti egestas, hac turpis consequat torquent"
				+ " urna augue malesuada enim pharetra, aliquam porta nisi risus blandit fames eu habitant."
				+ " Cum maecenas ad lectus libero quam tortor, pretium arcu mus dis morbi augue,"
				+ " orci dapibus varius vestibulum a. Torquent fringilla proin aenean mauris "
				+ "dignissim varius habitasse odio, sagittis convallis ridiculus litora sed "
				+ "purus quisque rhoncus, quis libero accumsan hendrerit euismod maecenas primis.";
		
		String lorem = "";
		return append(lorem,text, times);
}
	
	/**
	 * Get something inside a text with regex
	 * @param text
	 * @param regex
	 * @return
	 */
	public static String getInside(String text,String regex) {
		return text.replaceAll(".*(" + regex + ").*","$1");
	}

}
