package com.code.aon.marketplace.plu;

public class HwstIf extends If {

	Field HWGN = new Field(0,0);
	Field HWGT = new Field(0,0);
	boolean valid_hwgn = false;
	boolean valid_hwgt = false;

	public Field getHWGN() {
		return HWGN;
	}
	public void setHWGN(Field hwgn) {
		HWGN = hwgn;
		this.mask = this.mask + "$hwgn";
		valid_hwgn = true;
	}

	public Field getHWGT() {
		return HWGT;
	}
	public void setHWGT(Field hwgt) {
		HWGT = hwgt;
		this.mask = this.mask + "$hwgt";
		valid_hwgt = true;
	}
	
	public boolean isValid() {
		return valid_hwgt && valid_hwgn;
	}

}
