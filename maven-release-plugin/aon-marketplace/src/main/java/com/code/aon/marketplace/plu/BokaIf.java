package com.code.aon.marketplace.plu;

public class BokaIf extends If {

	Field BONU = new Field(0,0);
	Field STYP = new Field(0,0);
	Field SNR1 = new Field(0,0);
	Field BT10 = new Field(0,0);
	Field BT20 = new Field(0,0);
	Field POS1 = new Field(0,0);
	Field GEW1 = new Field(0,0);
	Field ZEIS = new Field(0,0);
	
	String dateformat = "dd.MM.yyyy";

	boolean valid_bonu = false;
	boolean valid_styp = false;
	boolean valid_snr1 = false;
	boolean valid_bt10 = false;
	boolean valid_bt20 = false;
	boolean valid_pos1 = false;
	boolean valid_gew1 = false;
	boolean valid_zeis = false;

	public Field getBONU() {
		return BONU;
	}
	public void setBONU(Field field) {
		BONU = field;
		this.mask = this.mask + "$bonu";
		valid_bonu = true;
	}

	public Field getSTYP() {
		return STYP;
	}
	public void setSTYP(Field field) {
		STYP = field;
		this.mask = this.mask + "$styp";
		valid_styp = true;
	}

	public Field getSNR1() {
		return SNR1;
	}
	public void setSNR1(Field field) {
		SNR1 = field;
		this.mask = this.mask + "$snr1";
		valid_snr1 = true;
	}

	public Field getBT10() {
		return BT10;
	}
	public void setBT10(Field field) {
		BT10 = field;
		this.mask = this.mask + "$bt10";
		valid_bt10 = true;
	}

	public Field getBT20() {
		return BT20;
	}
	public void setBT20(Field field) {
		BT20 = field;
		this.mask = this.mask + "$bt20";
		valid_bt20 = true;
	}

	public Field getPOS1() {
		return POS1;
	}
	public void setPOS1(Field field) {
		POS1 = field;
		this.mask = this.mask + "$pos1";
		valid_pos1 = true;
	}

	public Field getGEW1() {
		return GEW1;
	}
	public void setGEW1(Field field) {
		GEW1 = field;
		this.mask = this.mask + "$gew1";
		valid_gew1 = true;
	}

	public Field getZEIS() {
		return ZEIS;
	}
	public void setZEIS(Field field) {
		ZEIS = field;
		this.mask = this.mask + "$zeis";
		valid_zeis = true;
	}

	public boolean isValid() {
		return valid_bonu && valid_styp && valid_snr1 && valid_bt10 && valid_bt20 && valid_pos1 && valid_gew1 && valid_zeis;
	}
	
	public String getDateformat() {
		return dateformat;
	}
	
	public void setDateformat(String dateformat) {
		this.dateformat = dateformat;
	}
}
