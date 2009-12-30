package com.code.aon.marketplace.plu;

public class If  {

	String mask = "";
	boolean comaSeparator = false; 

	public boolean isComaSeparator() {
		return comaSeparator;
	}
	public void setComaSeparator(boolean comaSeparator) {
		this.comaSeparator = comaSeparator;
		this.mask = this.mask + ",";
	}

	public String getMask() {
		return mask;
	}
	public void addMask(String type, int length, int code) {
		if (length > 0) {
			String text = "";
			if (type.equals("N")) text = FieldUtils.ZeroFill(0, length);
			else if (type.equals("H") || type.equals("S")) text = "" + (char)code;
			else text = FieldUtils.SpaceFill("", length);
			this.mask = this.mask + text;
		}
	}
	public void setCR() {
		this.mask = this.mask + (char)13;
	}
	public void setEOL() {
		this.mask = this.mask + (char)10;
	}

}
