package com.esferalia.aon.dsi.nominas.model;

public class Ascendiente {
	
	private String an;
	private String co;
	private String mi;
	private String mr;
	
	public String getAn() {
		return an;
	}
	public Ascendiente setAn(String an) {
		this.an = an;
		return this;
	}
	public String getCo() {
		return co;
	}
	public Ascendiente setCo(String co) {
		this.co = co;
		return this;
	}
	public String getMi() {
		return mi;
	}
	public Ascendiente setMi(String mi) {
		this.mi = mi;
		return this;
	}
	public String getMr() {
		return mr;
	}
	public Ascendiente setMr(String mr) {
		this.mr = mr;
		return this;
	}
	
	@Override
	public String toString() {
		return "Ascendiente [an=" + an + ", co=" + co + ", mi=" + mi + ", mr=" + mr + "]";
	}
	

}
