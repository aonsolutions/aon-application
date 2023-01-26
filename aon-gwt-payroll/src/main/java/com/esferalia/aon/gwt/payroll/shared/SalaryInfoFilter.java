package com.esferalia.aon.gwt.payroll.shared;

import static com.esferalia.aon.gwt.payroll.shared.Shared.format;
import static com.esferalia.aon.gwt.payroll.shared.Shared.parse;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.common.shared.DateUtils;

public class SalaryInfoFilter implements Serializable {

	private static final long serialVersionUID = 1L;
	
	// Dates filter
	private String dateTillT;
	private String dateTTo;
	
	// Salary type filter
	private List<Integer> salaryTypes; // -1 || null == all // 0 == salary //  1 == extra // 2 == settlement // 3 == delay 
	
	// Emplyee filter
	private Integer employeeId; // employeeId == contractId;
	
	// Workplace filter
	private Integer workplaceId;
	
	// Workplace filter
	private Integer enterpriseId;
	
	public SalaryInfoFilter() {
		super();
		this.dateTillT = null;
		this.dateTTo = null;
		
		this.salaryTypes = new ArrayList<>();
		
		this.employeeId = null;
		
		this.workplaceId = null;
		
		this.enterpriseId = null;
	}

	public Date getDateTillT() {
		return parse(dateTillT);
	}
	public void setDateTillT(Date dateTillT) {
		DateUtils.resetTime(dateTillT);
		this.dateTillT = format(dateTillT);
	}
	public Date getDateTTo() {
		return parse(dateTTo);
	}
	public void setDateTTo(Date dateTTo) {
		DateUtils.resetTime(dateTTo);
		this.dateTTo = format(dateTTo);
	}
	public List<Integer> getSalaryTypes() {
		return salaryTypes;
	}
	public void setSalaryTypes(List<Integer> salaryTypes) {
		this.salaryTypes = salaryTypes;
	}
	public Integer getEmployeeId() {
		return employeeId;
	}
	public void setEmployeeId(Integer employeeId) {
		this.employeeId = employeeId;
	}
	public Integer getWorkplaceId() {
		return workplaceId;
	}
	public void setWorkplaceId(Integer workplaceId) {
		this.workplaceId = workplaceId;
	}
	public Integer getEnterpriseId() {
		return enterpriseId;
	}
	public void setEnterpriseId(Integer enterpriseId) {
		this.enterpriseId = enterpriseId;
	}
	
}
