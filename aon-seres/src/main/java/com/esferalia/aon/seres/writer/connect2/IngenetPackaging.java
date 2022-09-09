package com.esferalia.aon.seres.writer.connect2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IngenetPackaging {
	
	Integer env;
	Integer cont;
	Integer lin;
	String sscc;
	
	public IngenetPackaging() {

	}
	
	public Integer getEnv() {
		return env;
	}
	
	public IngenetPackaging setEnv(Integer env) {
		this.env = env;
		return this;
	}

	public Integer getCont() {
		return cont;
	}

	public IngenetPackaging setCont(Integer cont) {
		this.cont = cont;
		return this;
	}

	public boolean hasCont() {
		return getCont() != null;
	}
	
	public Integer getLin() {
		return lin;
	}

	public IngenetPackaging setLin(Integer lin) {
		this.lin = lin;
		return this;
	}
	
	public boolean hasLin() {
		return getLin() != null;
	}

	public String getSscc() {
		return sscc;
	}

	public IngenetPackaging setSscc(String sscc) {
		this.sscc = sscc;
		return this;
	}
	
	public boolean hasSscc() {
		return getSscc() != null;
	}
	
	public static IngenetPackaging parse(String data) {
		return new IngenetPackaging()
			.setEnv(parseEnv(data))
			.setCont(parseCont(data))
			.setLin(parseLin(data))
			.setSscc(parseSscc(data));
	}
	
	private static Integer parseEnv(String data) {
		Matcher matcher = Pattern.compile("ENV=[0-9]*;").matcher(data);
		if(matcher.find()) return Integer.parseInt(matcher.group(0).replace("ENV=", "").replace(";", ""));
		return null;
	}
	
	private static Integer parseCont(String data) {
		Matcher matcher = Pattern.compile("CONT=[0-9]*").matcher(data);
		if(matcher.find()) return Integer.parseInt(matcher.group(0).replace("CONT=", ""));
		return null;
	}
	
	private static Integer parseLin(String data) {
		Matcher matcher = Pattern.compile("LIN=[0-9]*;").matcher(data);
		if(matcher.find()) return Integer.parseInt(matcher.group(0).replace("LIN=", "").replace(";", ""));
		return null;
	}
	
	private static String parseSscc(String data) {
		Matcher matcher = Pattern.compile("SSCC=[0-9]*").matcher(data);
		if(matcher.find()) return matcher.group(0).replace("SSCC=", "");
		return null;
	}

	
	public static void main(String[] args) {
		String a = "[ENV=38;CONT=12][ENV=39;CONT=13][ENV=40;CONT=14][ENV=41;CONT=15]";
		
		String[] b = a.split("\\]\\[");
		Integer cont = 0;
		while(cont < b.length) {
			System.out.println(b[cont]);
			cont++;
		}
	}
}
