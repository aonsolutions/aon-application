package com.esferalia.aon.in.payroll.pdf.Pdf_API.toolkit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StringToolkit {
	public static List<String> to_words(String text){
		ArrayList<String> words = new ArrayList<String>();
		words.addAll(Arrays.asList(text.split("\\s")));
		words.removeIf(p -> p.length() < 1);
		return words;
	}
	
	//------------HELP INFO------------
		public static String help() {
			String info = "";
			return info;
		}	
}
