package com.code.aon.marketplace.plu;

import com.code.aon.product.enumeration.PluProductType;


public class PluItem{

	private int cod;

	private int fam;
	
	private int subfam;

	private int plu;
	
	private String bar;

	private String typ;

	private int sec;

	private int mos;

	private int cat;

	private String txt;
	
	private double prc;

	public int getCod() {
		return cod;
	}

	public void setCod(int cod) {
		this.cod = cod;
	}

	public int getSec() {
		return sec;
	}

	public void setSec(int sec) {
		this.sec = sec;
	}

	public int getMos() {
		return mos;
	}

	public void setMos(int mos) {
		this.mos = mos;
	}

	public int getPlu() {
		return plu;
	}

	public void setPlu(int plu) {
		this.plu = plu;
	}

	public double getPrc() {
		return prc;
	}

	public void setPrc(double price) {
		this.prc = price;
	}

	public String getTxt(boolean short_desc) {
		String temp = txt;
		if (short_desc) {
			if (temp.length() > 20) {
				temp = temp.substring(0,19);
			}
		}
		else {
			if (temp.length() > 64) {
				temp = temp.substring(0,63);
			}
		}
		return temp;
	}

	public void setTxt(String txt) {
		if (txt == null) txt = "";
		this.txt = txt;
	}

	public int getCat() {
		return cat;
	}

	public void setCat(int cat) {
		this.cat = cat;
	}

	public int getFam() {
		return fam;
	}

	public void setFam(int fam) {
		this.fam = fam;
	}

	public int getSubfam() {
		return subfam;
	}

	public void setSubfam(int subfam) {
		this.subfam = subfam;
	}

	public String getBar() {
		if (bar == null || bar.equals("0000000000000") || bar.trim().equals("")) return null;
		return bar;
	}

	public void setBar(String bar) {
		this.bar = bar;
	}

	public PluProductType getTyp() {
		if (typ != null && typ.equals("U")) return PluProductType.UNITARY;
		return PluProductType.WEITHED;
	}

	public void setTyp(String typ) {
		this.typ = typ;
	}

}
