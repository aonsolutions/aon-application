package com.transtools.jdbc.metadata;

public class Syscolumn {
	private String colname;
	private int coltype;
	private int collength;
	public String getColname() {
		return colname;
	}
	public void setColname(String colname) {
		this.colname = colname;
	}
	public int getColtype() {
		return coltype;
	}
	public void setColtype(int coltype) {
		this.coltype = coltype;
	}
	public int getCollength() {
		return collength;
	}
	public void setCollength(int collength) {
		this.collength = collength;
	} 
}
