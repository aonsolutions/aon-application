package com.code.aon.file.format;

public class AlphaNumericTrim extends AlphaNumeric {

	
	public String format(String value) {
		return super.format(value).trim();
	}

	static public void main(String[] args) {
		AlphaNumericTrim alpha = new AlphaNumericTrim();
		alpha.applyPattern("X(10)");
		System.out.println ("#"+alpha.format("5180")+"#");
	}

}