package com.esferalia.aon.dsi.nominas.model;

public class Descendiente {
	
	private String an;
	private String ad;
	private String en;
	private String mi;
	private String mr;
	
	public String getAn() {
		return an;
	}
	public Descendiente setAn(String an) {
		this.an = an;
		return this;
	}
	public String getAd() {
		return ad;
	}
	public Descendiente setAd(String ad) {
		this.ad = ad;
		return this;
	}
	public String getEn() {
		return en;
	}
	public Descendiente setEn(String en) {
		this.en = en;
		return this;
	}
	public String getMi() {
		return mi;
	}
	public Descendiente setMi(String mi) {
		this.mi = mi;
		return this;
	}
	public String getMr() {
		return mr;
	}
	public Descendiente setMr(String mr) {
		this.mr = mr;
		return this;
	}
	
	@Override
	public String toString() {
		return "Descendiente [an=" + an + ", ad=" + ad + ", en=" + en + ", mi=" + mi + ", mr=" + mr + "]";
	}

}
