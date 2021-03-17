package com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p><b>Description:</b> <i>This toolkit contains the basic methods for String manipulation</i></p>
 * @author akrck02
 */
public class StringToolkit {
	
	
	/**
	 * <p><b>Description:</b> <i>Converts a String into separated words.</i></p>
	 * @return List of words (String)
	 */
	public static List<String> to_words(String text) {
		ArrayList<String> words = new ArrayList<String>();
		words.addAll(Arrays.asList(text.split("\\s")));
		words.removeIf(p -> p.length() < 1);
		return words;
	}
	
	
	public static String append(String text,String append,int times) {
		for (int i = 0; i < times; i++) text += append;
		return text;
	}
}
