package com.esferalia.aon.payroll.sepe.certifica2;

public class Certifica2Info {
	
	private String year;
	private String month;
	private Integer quotedDays;
	private Double base_cgc;
	private Double base_unemployment;
	
	public Certifica2Info(){
		super();
	}

	public Certifica2Info(String year, String month, Integer quotedDays, Double base_cgc,
			Double base_unemployment) {
		super();
		this.year = year;
		this.month = month;
		this.quotedDays = quotedDays;
		this.base_cgc = base_cgc;
		this.base_unemployment = base_unemployment;
	}

	public String getYear() {
		return year;
	}

	public void setYear(String year) {
		this.year = year;
	}

	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	public Integer getQuotedDays() {
		return quotedDays;
	}

	public void setQuotedDays(Integer quotedDays) {
		this.quotedDays = quotedDays;
	}

	public Double getBase_cgc() {
		return base_cgc;
	}

	public void setBase_cgc(Double base_cgc) {
		this.base_cgc = base_cgc;
	}

	public Double getBase_unemployment() {
		return base_unemployment;
	}

	public void setBase_unemployment(Double base_unemployment) {
		this.base_unemployment = base_unemployment;
	}
}
