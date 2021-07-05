package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.esferalia.aon.watson.util.AonStringUtils;

@SuppressWarnings("serial")
public class Certifica2Info implements Serializable {
	
	// ------------------------------------------ Variables
	
	private String regime;
	private String ccc;
	private String completeCCC;
	private String document;
	private String name;
	private String surname;
	private String secondSurname;
	private String enterpriseDocument;
	private String contractType;
	private String quoteGroup;
	private Integer contractDuration;
	private String profesionalCategory;
	private String suspensionCode;
	private String suspension;
	private Date startDate;
	private Date endDate;
	private Integer settleQuoteDays;
	private Double baseCgc;
	private Double baseUnemployment;
	
	private List<Map<String, String>> quoteDataList;
	
	// ------------------------------------------ Constructor
	
	public Certifica2Info() {
		super();
	}
	
	// ------------------------------------------ Getters / Setters

	public String getRegime() {
		return regime;
	}

	public void setRegime(String regime) {
		this.regime = regime;
	}

	public String getCcc() {
		return ccc;
	}

	public void setCcc(String ccc) {
		this.ccc = ccc;
	}

	public String getCompleteCCC() {
		return completeCCC;
	}

	public void setCompleteCCC(String completeCCC) {
		this.completeCCC = completeCCC;
	}

	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public String getSecondSurname() {
		return secondSurname;
	}

	public void setSecondSurname(String secondSurname) {
		this.secondSurname = secondSurname;
	}
	
	public String getFullName() {
		return (this.surname + " " + this.secondSurname + ", " + this.name).trim();
	}

	public String getEnterpriseDocument() {
		return enterpriseDocument;
	}

	public void setEnterpriseDocument(String enterpriseDocument) {
		this.enterpriseDocument = enterpriseDocument;
	}

	public String getContractType() {
		return contractType;
	}

	public void setContractType(String contractType) {
		this.contractType = contractType;
	}

	public String getQuoteGroup() {
		return quoteGroup;
	}

	public void setQuoteGroup(String quoteGroup) {
		this.quoteGroup = quoteGroup;
	}

	public Integer getContractDuration() {
		return contractDuration;
	}

	public void setContractDuration(Integer contractDuration) {
		this.contractDuration = contractDuration;
	}

	public String getProfesionalCategory() {
		return profesionalCategory;
	}

	public void setProfesionalCategory(String profesionalCategory) {
		this.profesionalCategory = profesionalCategory;
	}

	public String getSuspensionCode() {
		return suspensionCode;
	}

	public void setSuspensionCode(String suspensionCode) {
		this.suspensionCode = suspensionCode;
	}

	public String getSuspension() {
		return suspension;
	}

	public void setSuspension(String suspension) {
		this.suspension = suspension;
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

	public Integer getSettleQuoteDays() {
		return settleQuoteDays;
	}

	public void setSettleQuoteDays(Integer settleQuoteDays) {
		this.settleQuoteDays = settleQuoteDays;
	}

	public Double getBaseCgc() {
		return baseCgc;
	}

	public void setBaseCgc(Double baseCgc) {
		this.baseCgc = baseCgc;
	}

	public Double getBaseUnemployment() {
		return baseUnemployment;
	}

	public void setBaseUnemployment(Double baseUnemployment) {
		this.baseUnemployment = baseUnemployment;
	}

	public List<Map<String, String>> getQuoteDataList() {
		return quoteDataList;
	}

	public void setQuoteDataList(List<Map<String, String>> quoteDataList) {
		this.quoteDataList = quoteDataList;
	}
	
	@Override
	public String toString() {
		String toString = "Certific@2 Info \n { \n";
		
		if(AonStringUtils.isNotBlank(regime)) toString += "\t Regime: " + regime + "\n";
		if(AonStringUtils.isNotBlank(ccc)) toString += "\t CCC: " + ccc + "\n";
		if(AonStringUtils.isNotBlank(completeCCC)) toString += "\t CompleteCCC: " + completeCCC + "\n";
		if(AonStringUtils.isNotBlank(document)) toString += "\t Document: " + document + "\n";
		if(AonStringUtils.isNotBlank(name)) toString += "\t Name: " + name + "\n";
		if(AonStringUtils.isNotBlank(surname)) toString += "\t Surname: " + surname + "\n";
		if(AonStringUtils.isNotBlank(secondSurname)) toString += "\t SecondSurname: " + secondSurname + "\n";
		if(AonStringUtils.isNotBlank(enterpriseDocument)) toString += "\t EnterpriseDocument: " + enterpriseDocument + "\n";
		if(AonStringUtils.isNotBlank(contractType)) toString += "\t ContractType: " + contractType + "\n";
		if(AonStringUtils.isNotBlank(quoteGroup)) toString += "\t QuoteGroup: " + quoteGroup + "\n";
		if(null != contractDuration) toString += "\t ContractDuration: " + contractDuration + "\n";
		if(AonStringUtils.isNotBlank(profesionalCategory)) toString += "\t ProfesionalCategory: " + profesionalCategory + "\n";
		if(AonStringUtils.isNotBlank(suspensionCode)) toString += "\t SuspensionCode: " + suspensionCode + "\n";
		if(null != startDate) toString += "\t StartDate: " + startDate + "\n";
		if(null != endDate) toString += "\t EndDate: " + endDate + "\n";
		if(null != settleQuoteDays) toString += "\t SettleQuoteDays: " + settleQuoteDays + "\n";
		if(null != baseCgc) toString += "\t BaseCgc: " + baseCgc + "\n";
		if(null != baseUnemployment) toString += "\t BaseUnemployment: " + baseUnemployment + "\n";
		
		for(Map<String, String> quoteData : quoteDataList) {
			String quoteDataRow = "\t{ \n";
			for(Entry<String, String> quoteDataEntry : quoteData.entrySet())
				quoteDataRow += "\t\t" + quoteDataEntry.getKey() + ": " + quoteDataEntry.getValue() + " ";
			quoteDataRow += "\n\t}";
			toString += quoteDataRow + "\n";
		}
		
		toString += "}";
		
		return toString;
	}
	
}
