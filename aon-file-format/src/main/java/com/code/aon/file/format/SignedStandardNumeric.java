package com.code.aon.file.format;

public class SignedStandardNumeric extends Numeric {
		
	@Override
	protected String getSignValue(boolean positive) {
		return isSigned() ? (positive) ? " ": "-" : "";
	}

	
	static public void main(String[] args) {
		  SignedStandardNumeric num = new SignedStandardNumeric();
		  num.applyPattern("S9(14)V99");
		  Object str = new Double("-316.57");
		  System.out.println (num.format(str));
		  num.applyPattern("S9(14).99");
		  str = new Double("-316.57");
		  System.out.println (num.format(str));
		  str = new Double("316.57");
		  System.out.println (num.format(str));
		}
	
}