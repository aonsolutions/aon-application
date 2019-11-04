package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.gwt.common.shared.DateUtils;

public class SalaryInfoFilter implements Serializable {

	private static final long serialVersionUID = 1L;
	
	// Dates filter
	private Boolean noDateFilter;
	private Boolean dateMYFilter;
	private Date dateMY;
	private Boolean dateTTFilter;
	private Date dateTillT;
	private Date dateTTo;
	
	// Salary type filter
	private Integer salaryType; // 0 == salary // 2 == delay // 3 == settlement
	
	public SalaryInfoFilter() {
		super();
		this.noDateFilter = false;
		this.dateMYFilter = false;
		this.dateMY = null;
		this.dateTTFilter = false;
		this.dateTillT = null;
		this.dateTTo = null;
		
		this.salaryType = null;
	}

	public Boolean isNoDateFilter() {
		return noDateFilter;
	}
	public void setNoDateFilter(Boolean noDateFilter) {
		this.noDateFilter = noDateFilter;
	}
	public Boolean isDateMYFilter() {
		return dateMYFilter;
	}
	public void setDateMYFilter(Boolean dateMYFilter) {
		this.dateMYFilter = dateMYFilter;
	}
	public Date getDateMY() {
		return dateMY;
	}
	public void setDateMY(Date dateMY) {
		DateUtils.resetTime(dateMY);
		this.dateMY = dateMY;
	}
	public Boolean isDateTTFilter() {
		return dateTTFilter;
	}
	public void setDateTTFilter(Boolean dateTTFilter) {
		this.dateTTFilter = dateTTFilter;
	}
	public Date getDateTillT() {
		return dateTillT;
	}
	public void setDateTillT(Date dateTillT) {
		DateUtils.resetTime(dateTillT);
		this.dateTillT = dateTillT;
	}
	public Date getDateTTo() {
		return dateTTo;
	}
	public void setDateTTo(Date dateTTo) {
		DateUtils.resetTime(dateTTo);
		this.dateTTo = dateTTo;
	}
	public Integer getSalaryType() {
		return salaryType;
	}
	public void setSalaryType(Integer salaryType) {
		this.salaryType = salaryType;
	}
	
	
}
