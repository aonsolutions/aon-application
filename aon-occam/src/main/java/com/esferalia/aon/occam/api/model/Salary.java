package com.esferalia.aon.occam.api.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collector;

public class Salary implements Serializable{
	
	
	public static class ContextData {
		String expression;
		Date startDate;
		Date endDate;
		
		public Date getEndDate() {
			return endDate;
		}
		
		public Date getStartDate() {
			return startDate;
		}
		
		public String getExpression() {
			return expression;
		}
	}

	private Integer id;
	
	private Date startDate;
	private Date endDate;
	private int salaryDays;
	
	// Enterprise related data
	private String enterpriseCCC;
	private String enterpriseName;
	private String enterpriseAddress;
	private String enterpriseDocument;	
	
	// Employee related data
	private String employeeName;
	private String employeeSSNumber;
	private String employeeDocument;
	private Date employeeSeniorityDate;
	private String employeeQuoteGroup;
	private String employeeCategory;

	// Totals
	private Double totalPayment;
	private Double totalDeduction;
	private Double totalLiquid;
	private Double totalEnterprise;
	private Double totalIrpf;
	private Double totalSSContributions;
	
	// Tax (I.R.P.F) bases 
	private Double irpfBase;
	private double moneyIrpfBase;
	private double inkindIrpfBase;

	// Quote ( SS ) bases
	private Double commonContingenciesBase;
	private Double professionalContingenciesBase;
	private Double estructuralOvertimeBase;
	private Double nonEstructuralOvertimeBase;

	
	
	private Map<String, List<ContextData>> contextdata ;
	
	
	public Salary() {
		contextdata = new HashMap<String, List<ContextData>>();
	}
	
	public Integer getId() {
		return id;
	}
	
	
	public Salary setId(Integer id) {
		this.id = id;
		return this;
	}
	
	public String getEnterpriseCCC() {
		return enterpriseCCC;
	}
	
	
	public Salary setEnterpriseCCC(String enterpriseCCC) {
		this.enterpriseCCC = enterpriseCCC;
		return this;
	}
	
	
	public String getEnterpriseName() {
		return enterpriseName;
	}
	
	public Salary setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
		return this;
	}

	public String getEnterpriseDocument() {
		return enterpriseDocument;
	}
	
	public Salary setEnterpriseDocument(String enterpriseDocument) {
		this.enterpriseDocument = enterpriseDocument;
		return this;
	}
	
	public String getEnterpriseAddress() {
		return enterpriseAddress;
	}
	
	public Salary setEnterpriseAddress(String enterpriseAddress) {
		this.enterpriseAddress = enterpriseAddress;
		return this;
	}


	public String getEmployeeName() {
		return employeeName;
	}


	public Salary setEmployeeName(String employeeName) {
		this.employeeName = employeeName;
		return this;
	}


	public String getEmployeeSSNumber() {
		return employeeSSNumber;
	}


	public Salary setEmployeeSSNumber(String employeeSSNumber) {
		this.employeeSSNumber = employeeSSNumber;
		return this;
	}


	public String getEmployeeDocument() {
		return employeeDocument;
	}


	public Salary setEmployeeDocument(String employeeDocument) {
		this.employeeDocument = employeeDocument;
		return this;
	}


	public Date getEmployeeSeniorityDate() {
		return employeeSeniorityDate;
	}


	public Salary setEmployeeSeniorityDate(Date employeeSeniorityDate) {
		this.employeeSeniorityDate = employeeSeniorityDate;
		return this;
	}


	public String getEmployeeQuoteGroup() {
		return employeeQuoteGroup;
	}


	public Salary setEmployeeQuoteGroup(String employeeQuoteGroup) {
		this.employeeQuoteGroup = employeeQuoteGroup;
		return this;
	}


	public String getEmployeeCategory() {
		return employeeCategory;
	}


	public Salary setEmployeeCategory(String employeeCategory) {
		this.employeeCategory = employeeCategory;
		return this;
	}


	public Double getTotalPayment() {
		return totalPayment;
	}


	public Salary setTotalPayment(Double totalPayment) {
		this.totalPayment = totalPayment;
		return this;
	}


	public Double getTotalDeduction() {
		return totalDeduction;
	}


	public Salary setTotalDeduction(Double totalDeduction) {
		this.totalDeduction = totalDeduction;
		return this;
	}


	public Double getTotalLiquid() {
		return totalLiquid;
	}


	public Salary setTotalLiquid(Double totalLiquid) {
		this.totalLiquid = totalLiquid;
		return this;
	}


	public Double getTotalEnterprise() {
		return totalEnterprise;
	}


	public Salary setTotalEnterprise(Double totalEnterprise) {
		this.totalEnterprise = totalEnterprise;
		return this;
	}


	public Double getTotalIrpf() {
		return totalIrpf;
	}


	public Salary setTotalIrpf(Double totalIrpf) {
		this.totalIrpf = totalIrpf;
		return this;
	}


	public Double getTotalSSContributions() {
		return totalSSContributions;
	}


	public Salary setTotalSSContributions(Double totalSSContributions) {
		this.totalSSContributions = totalSSContributions;
		return this;
	}


	public Double getIrpfBase() {
		return irpfBase;
	}


	public Salary setIrpfBase(Double irpfBase) {
		this.irpfBase = irpfBase;
		return this;
	}


	public double getMoneyIrpfBase() {
		return moneyIrpfBase;
	}


	public Salary setMoneyIrpfBase(double moneyIrpfBase) {
		this.moneyIrpfBase = moneyIrpfBase;
		return this;
	}


	public double getInkindIrpfBase() {
		return inkindIrpfBase;
	}


	public Salary setInkindIrpfBase(double inkindIrpfBase) {
		this.inkindIrpfBase = inkindIrpfBase;
		return this;
	}


	public Double getCommonContingenciesBase() {
		return commonContingenciesBase;
	}


	public Salary setCommonContingenciesBase(Double commonContingenciesBase) {
		this.commonContingenciesBase = commonContingenciesBase;
		return this;
	}


	public Double getProfessionalContingenciesBase() {
		return professionalContingenciesBase;
	}


	public Salary setProfessionalContingenciesBase(
			Double professionalContingenciesBase) {
		this.professionalContingenciesBase = professionalContingenciesBase;
		return this;
	}


	public Double getEstructuralOvertimeBase() {
		return estructuralOvertimeBase;
	}


	public Salary setEstructuralOvertimeBase(Double estructuralOvertimeBase) {
		this.estructuralOvertimeBase = estructuralOvertimeBase;
		return this;
	}


	public Double getNonEstructuralOvertimeBase() {
		return nonEstructuralOvertimeBase;
	}


	public Salary setNonEstructuralOvertimeBase(Double nonEstructuralOvertimeBase) {
		this.nonEstructuralOvertimeBase = nonEstructuralOvertimeBase;
		return this;
	}
	
	
	public int getSalaryDays() {
		return salaryDays;
	}
	
	public Salary setSalaryDays(int salaryDays) {
		this.salaryDays = salaryDays;
		return this;
	}
	
	public Date getStartDate() {
		return startDate;
	}
	
	public Salary setStartDate(Date startDate) {
		this.startDate = startDate;
		return this;
	}
	
	public Date getEndDate() {
		return endDate;
	}
	
	public Salary setEndDate(Date endDate) {
		this.endDate = endDate;
		return this;
	}
	
	public Map<String, List<ContextData>> getContextData() {
		return Collections.unmodifiableMap(contextdata);
	}
	

	public <A,R> R getContextData(String name, Collector<? super String,A,R> collector ) {
		List<ContextData> datas = contextdata.get(name);
		if ( datas == null ) 
			return null;
		return datas.stream()
		.map(d-> d.expression)
		.collect(collector);
	}

	public Salary setContextData(String name, String value, Date startDate, Date endDate) {
		
		List<ContextData> datas = contextdata.get(name);
		if ( datas == null  ) 
			contextdata.put(name, datas = new ArrayList<ContextData>(1));
	
		ContextData contextData = new ContextData();
		contextData.endDate = endDate;
		contextData.startDate = startDate;
		contextData.expression = value;
		datas.add(contextData);
		return this;
	}
}
