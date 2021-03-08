package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.gwt.common.shared.DateUtils;

public class SalaryInfoFilter implements Serializable {

	private static final long serialVersionUID = 1L;
	
	// Dates filter
	private Date dateTillT;
	private Date dateTTo;
	
	// Salary type filter
	private Integer salaryType; // -1 || null == all // 0 == salary // 2 == delay // 3 == settlement
	
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
		
		this.salaryType = null;
		
		this.employeeId = null;
		
		this.workplaceId = null;
		
		this.enterpriseId = null;
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
