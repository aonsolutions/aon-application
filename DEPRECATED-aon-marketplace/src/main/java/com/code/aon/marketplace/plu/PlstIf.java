package com.code.aon.marketplace.plu;

public class PlstIf extends If {

	Field PNUM = new Field(0,0);
	Field GPR1 = new Field(0,0);
	Field WGNU = new Field(0,0);
	Field ECO1 = new Field(0,0);
	Field PLTE = new Field(0,0);
	boolean valid_pnum = false;
	boolean valid_gpr1 = false;
	boolean valid_wgnu = false;
	boolean valid_eco1 = false;
	boolean valid_plte = false;

	public Field getPNUM() {
		return PNUM;
	}
	public void setPNUM(Field pnum) {
		PNUM = pnum;
		this.mask = this.mask + "$pnum";
		valid_pnum = true;
	}

	public Field getGPR1() {
		return GPR1;
	}
	public void setGPR1(Field gpr1) {
		GPR1 = gpr1;
		this.mask = this.mask + "$gpr1";
		valid_gpr1 = true;
	}

	public Field getWGNU() {
		return WGNU;
	}
	public void setWGNU(Field wgnu) {
		WGNU = wgnu;
		this.mask = this.mask + "$wgnu";
		valid_wgnu = true;
	}

	public Field getECO1() {
		return ECO1;
	}
	public void setECO1(Field eco1) {
		ECO1 = eco1;
		this.mask = this.mask + "$eco1";
		valid_eco1 = true;
	}

	public Field getPLTE() {
		return PLTE;
	}
	public void setPLTE(Field plte) {
		PLTE = plte;
		this.mask = this.mask + "$plte";
		valid_plte = true;
	}

	public boolean isValid() {
		return valid_pnum && valid_gpr1 && valid_wgnu && valid_eco1 && valid_plte;
	}

}
