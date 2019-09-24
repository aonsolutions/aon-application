package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;

import com.esferalia.aon.gwt.payroll.shared.Salary.Type;
import com.google.gwt.view.client.ProvidesKey;

public class SalaryInfo implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer id;
	private Integer domain;
	private Integer contract;
	private Date startDate;
	private Date endDate;
	private Type type;
	private String enterpriseName;
	private String workplaceName;
	private String employeeName;
	private Double totalPayment;
	private Double totalDeduction;
	private Double totalLiquid;
	
	/**
     * The key provider that provides the unique ID of a contact.
     */
    public static final ProvidesKey<SalaryInfo> KEY_PROVIDER = new ProvidesKey<SalaryInfo>() {
      @Override
      public Object getKey(SalaryInfo item) {
        return item == null ? null : item.getId();
      }
    };
	
	public SalaryInfo() {
		super();
	}
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public Integer getDomain() {
		return domain;
	}
	public void setDomain(Integer domain) {
		this.domain = domain;
	}
	public Integer getContract() {
		return contract;
	}
	public void setContract(Integer contract) {
		this.contract = contract;
	}
	public Date getStartDate() {
		return startDate;
	}
	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}
	public Date getEndDate() {
		return endDate;
	}
	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public String getEnterpriseName() {
		return enterpriseName;
	}
	public void setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
	}
	public String getWorkplaceName() {
		return workplaceName;
	}
	public void setWorkplaceName(String workplaceName) {
		this.workplaceName = workplaceName;
	}
	public String getEmployeeName() {
		return employeeName;
	}
	public void setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
	}
	public Double getTotalPayment() {
		return totalPayment;
	}
	public void setTotalPayment(Double totalPayment) {
		this.totalPayment = totalPayment;
	}
	public Double getTotalDecuction() {
		return totalDeduction;
	}
	public void setTotalDeduction(Double totalDeduction) {
		this.totalDeduction = totalDeduction;
	}
	public Double getTotalLiquid() {
		return totalLiquid;
	}
	public void setTotalLiquid(Double totalLiquid) {
		this.totalLiquid = totalLiquid;
	}
	
}
