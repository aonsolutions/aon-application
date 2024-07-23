package com.esferalia.aon.occam.api.model;

import java.util.Date;
import java.util.List;

import com.esferalia.aon.occam.api.model.payroll.Contract;

public class ContractExtendedData extends Contract {
	private static final long serialVersionUID = 1L;
	Double grossSalaryLastMonth;
	Double totalMarksLastMonth;
	String personName;
	String personDocument;
	String contractType;
	String workplaceName;
	List<String> idLists;
	String personFirstName;
	String personSecondName;
	String quoteGroup;
	String occupation;
	String cno;
	String rlce;
	String workerCollective;

	public String getWorkerCollective() {
		return workerCollective;
	}

	public ContractExtendedData setWorkerCollective(String workerCollective) {
		this.workerCollective = workerCollective;
		return this;
	}

	public ContractExtendedData(){
		super();
	}
	
	public String getCno() {
		return cno;
	}

	public ContractExtendedData setCno(String value) {
		this.cno = value;
		return this;
	}

	public String getRlce() {
		return rlce;
	}

	public ContractExtendedData setRlce(String value) {
		this.rlce = value;
		return this;
	}

	public String getOccupation() {
		return occupation;
	}

	public ContractExtendedData setOccupation(String value) {
		this.occupation = value;
		return this;
	}

	public String getQuoteGroup() {
		return quoteGroup;
	}

	public ContractExtendedData setQuoteGroup(String value) {
		this.quoteGroup = value;
		return this;
	}

	public String getPersonFirstName() {
		return personFirstName;
	}

	public ContractExtendedData setPersonFirstName(String value) {
		this.personFirstName = value;
		return this;
	}

	public String getPersonSecondName() {
		return personSecondName;
	}

	public ContractExtendedData setPersonSecondName(String value) {
		this.personSecondName = value;
		return this;
	}

	public List<String> getIdLists() {
		return idLists;
	}

	public ContractExtendedData setIdLists(List<String> value) {
		this.idLists = value;
		return this;
	}

	public String getPersonDocument() {
		return personDocument;
	}
	
	public ContractExtendedData setPersonDocument(String value) {
		personDocument = value;
		return this;
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
