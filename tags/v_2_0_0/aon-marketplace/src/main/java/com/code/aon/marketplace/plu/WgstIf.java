package com.code.aon.marketplace.plu;

public class WgstIf extends If {

	Field WGNU = new Field(0,0);
	Field HWGN = new Field(0,0);
	Field WGTE = new Field(0,0);
	boolean valid_wgnu = false;
	boolean valid_hwgn = false;
	boolean valid_wgte = false;

	public Field getHWGN() {
		return HWGN;
	}
	public void setHWGN(Field hwgn) {
		HWGN = hwgn;
		this.mask = this.mask + "$hwgn";
		valid_hwgn = true;
	}

	public Field getWGTE() {
		return WGTE;
	}
	public void setWGTE(Field wgte) {
		WGTE = wgte;
		this.mask = this.mask + "$wgte";
		valid_wgte = true;
	}
	
	public Field getWGNU() {
		return WGNU;
	}
	public void setWGNU(Field wgnu) {
		WGNU = wgnu;
		this.mask = this.mask + "$wgnu";
		valid_wgnu = true;
	}

	public boolean isValid() {
		return valid_wgte && valid_hwgn && valid_wgnu;
	}

}
