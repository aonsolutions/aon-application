package com.esferalia.aon.payroll.sepe.certifica;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class Certifica2Payroll implements Serializable {

	private String representativeDocument;
	private String representativeName;
	private String representativeSurname;
	private String regime;
	private String mdCtz;
	private String ccc;
	private String completeCCC;
	private String document;
	private String ssNumber;
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
	
	public Certifica2Payroll() {
		super();
	}

	public void setRepresentativeDocument(String representativeDocument) {
		this.representativeDocument = representativeDocument;
	}

	public void setRepresentativeName(String representativeName) {
		this.representativeName = representativeName;
	}

	public void setRepresentativeSurname(String representativeSurname) {
		this.representativeSurname = representativeSurname;
	}

	public void setRegime(String regime) {
		this.regime = regime;
	}

	public void setMdCtz(String mdCtz) {
		this.mdCtz = mdCtz;
	}

	public void setCcc(String ccc) {
		this.ccc = ccc;
	}

	public void setCompleteCCC(String completeCCC) {
		this.completeCCC = completeCCC;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public void setSsNumber(String ssNumber) {
		this.ssNumber = ssNumber;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	public void setSecondSurname(String secondSurname) {
		this.secondSurname = secondSurname;
	}

	public void setEnterpriseDocument(String enterpriseDocument) {
		this.enterpriseDocument = enterpriseDocument;
	}

	public void setContractType(String contractType) {
		this.contractType = contractType;
	}

	public void setQuoteGroup(String quoteGroup) {
		this.quoteGroup = quoteGroup;
	}

	public void setContractDuration(Integer contractDuration) {
		this.contractDuration = contractDuration;
	}

	public void setProfesionalCategory(String profesionalCategory) {
		this.profesionalCategory = profesionalCategory;
	}

	public void setSuspensionCode(String suspensionCode) {
		this.suspensionCode = suspensionCode;
	}

	public void setSuspension(String suspension) {
		this.suspension = suspension;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public void setSettleQuoteDays(Integer settleQuoteDays) {
		this.settleQuoteDays = settleQuoteDays;
	}

	public void setBaseCgc(Double baseCgc) {
		this.baseCgc = baseCgc;
	}

	public void setBaseUnemployment(Double baseUnemployment) {
		this.baseUnemployment = baseUnemployment;
	}

	public void setQuoteDataList(List<Map<String, String>> quoteDataList) {
		this.quoteDataList = quoteDataList;
	}

	public String getRepresentativeDocument() {
		return representativeDocument;
	}

	public String getRepresentativeName() {
		return representativeName;
	}

	public String getRepresentativeSurname() {
		return representativeSurname;
	}

	public String getRegime() {
		return regime;
	}

	public String getMdCtz() {
		return mdCtz;
	}

	public String getCcc() {
		return ccc;
	}

	public String getCompleteCCC() {
		return completeCCC;
	}

	public String getDocument() {
		return document;
	}

	public String getSsNumber() {
		return ssNumber;
	}

	public String getName() {
		return name;
	}

	public String getSurname() {
		return surname;
	}

	public String getSecondSurname() {
		return secondSurname;
	}

	public String getEnterpriseDocument() {
		return enterpriseDocument;
	}

	public String getContractType() {
		return contractType;
	}

	public String getQuoteGroup() {
		return quoteGroup;
	}

	public Integer getContractDuration() {
		return contractDuration;
	}

	public String getProfesionalCategory() {
		return profesionalCategory;
	}

	public String getSuspensionCode() {
		return suspensionCode;
	}

	public String getSuspension() {
		return suspension;
	}

	public Date getStartDate() {
		return startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public Integer getSettleQuoteDays() {
		return settleQuoteDays;
	}

	public Double getBaseCgc() {
		return baseCgc;
	}

	public Double getBaseUnemployment() {
		return baseUnemployment;
	}

	public List<Map<String, String>> getQuoteDataList() {
		return quoteDataList;
	}
	
}
