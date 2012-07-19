package com.code.aon.ui.loader;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.LineNumberReader;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CSVParser {

	private final Pattern csvPattern = Pattern.compile("\"([^\"]*)\"|(?<=,|^)([^,]*)(?:,|$)");
	private ArrayList<String> allMatches = null;
	private Matcher matcher = null;
	private int size;

	public CSVParser() {
		allMatches = new ArrayList<String>();
		matcher = null;
	}

	public String[] parse(String csvLine) {
		matcher = csvPattern.matcher(csvLine);
		allMatches.clear();
		String match;
		while (matcher.find()) {
			match = matcher.group(1);
			if (match != null) {
				allMatches.add(match);
			} else {
				allMatches.add(matcher.group(2));
			}
		}
		size = allMatches.size();
		if (size > 0) {
			return allMatches.toArray(new String[size]);
		} else {
			return new String[0];
		}
	}

	public static void main(String[] args) throws IOException {
		File file = new File("/home/ecastellano/MAC/Diario candina 2012.CSV");
		LineNumberReader reader = new LineNumberReader(new FileReader(file));
		while (reader.ready()) {
			String lineInput = reader.readLine();
			CSVParser myCSV = new CSVParser();
			System.out.println("Testing CSVParser with: \n " + lineInput);
			for (String s : myCSV.parse(lineInput)) {
				System.out.println(s);
			}
		}
	}

}
