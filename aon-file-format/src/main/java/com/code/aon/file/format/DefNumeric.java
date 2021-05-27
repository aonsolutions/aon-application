package com.code.aon.file.format;

public class DefNumeric extends Numeric {
		
	@Override
	protected String getSignValue(boolean positive) {
		return isSigned() ? (positive) ? "0": "N" : "";
	}

	
	static public void main(String[] args) {
		  DefNumeric num = new DefNumeric();
		  num.applyPattern("S9(14)V99");
		  Object str = new Double("-316.57");
		  System.out.println (num.format(str));
		  str = new Double("316.57");
		  System.out.println (num.format(str));
		}
	
}