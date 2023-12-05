package com.esferalia.aon.occam.api.model;

import java.util.Date;

import com.esferalia.aon.occam.api.model.payroll.Contract;

public class ContractExtendedData extends Contract {
	private static final long serialVersionUID = 1L;
	Double grossSalaryLastMonth;
	Double totalMarksLastMonth;
	String personName;
	String contractType;
	String workplaceName;
	
	public ContractExtendedData(){
		super();
	}
	
	public String getWorkplaceName() {
		return workplaceName;
	}
	
	public ContractExtendedData setWorkplaceName(String value) {
		workplaceName = value;
		return this;
	}
	
	public String getContractType() {
		return contractType;
	}
	
	public ContractExtendedData setContractType(String value) {
		contractType = value;
		return this;
	}
	
	public Double getGrossSalaryLastMonth() {
		return grossSalaryLastMonth;
	}
	
	public ContractExtendedData setGrossSalaryLastMonth(Double value) {
		grossSalaryLastMonth = value;
		return this;
	}
	
	public String getPersonName() {
		return personName;
	}
	
	public ContractExtendedData setPersonName(String value) {
		personName = value;
		return this;
	}
	
	public Double getTotalMarksLastMonth() {
		return totalMarksLastMonth;
	}
	
	public ContractExtendedData setTotalMarksLastMonth(Double value) {
		totalMarksLastMonth = value;
		return this;
	}
	
	@Override
	public ContractExtendedData setId(Integer value) {
		super.setId(value);
		return this;
	}
	
	@Override
	public ContractExtendedData setDomain(Integer value) {
		super.setDomain(value);
		return this;
	}

	@Override
	public ContractExtendedData setPerson(Integer value) {
		super.setPerson(value);
		return this;
	}
	
	@Override
	public ContractExtendedData setWorkplace(Integer value) {
		super.setWorkplace(value);
		return this;
	}
	
	@Override
	public ContractExtendedData setStartDate(Date value) {
		super.setStartDate(value);
		return this;
	}
	
	@Override
	public ContractExtendedData setEndDate(Date value) {
		super.setEndDate(value);
		return this;
	}
}
