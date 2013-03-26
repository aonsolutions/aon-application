package com.code.aon.file.format;

public class StandardNumeric extends Numeric {
		
	@Override
	protected String getSignValue(boolean positive) {
		return isSigned() ? (positive) ? "0": "-" : "";
	}

	
	static public void main(String[] args) {
		  StandardNumeric num = new StandardNumeric();
		  num.applyPattern("S9(14)V99");
		  Object str = new Double("-316.57");
		  System.out.println (num.format(str));
		  str = new Double("316.57");
		  System.out.println (num.format(str));
		}
	
}