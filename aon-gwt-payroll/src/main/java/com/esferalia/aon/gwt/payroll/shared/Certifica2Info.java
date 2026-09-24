package com.esferalia.aon.gwt.payroll.shared;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.esferalia.aon.watson.util.AonStringUtils;

@SuppressWarnings("serial")
public class Certifica2Info implements Serializable {
	
	public static class Certifica2Period {
		
		private String year;
		private String month;
		private Integer quotedDays;
		private Double base_cgc;
		private Double base_unemployment;
		
		private Date date;
		
		public Certifica2Period(){
			super();
		}

		public Certifica2Period(String year, String month, Integer quotedDays, Double base_cgc,
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

		public Date getDate() {
			return date;
		}

		public void setDate(Date date) {
			this.date = date;
		}
		
	}
	
	// ------------------------------------------ Variables
	
	private String representativeDocument;
	private String representativeName;
	private String representativeSurname;
	private String representativeWork;
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
	private String enterpriseName;
	private String contractType;
	private String quoteGroup;
	private Integer contractDuration;
	private String profesionalCategory;
	private String suspensionCode;
	private String suspension;
	
	private String erteCode;
	private String erteCoef;
	private Date erteEnd;
	
	private Date startDate;
	private Date endDate;
	private Date seniorityDate;
	private Integer settleQuoteDays;
	private Double baseCgc;
	private Double baseUnemployment;
	
	private String address;
	private String city;
	private String zip;
	private String geozone;
	
	private String cnaeCode;
	private String cnae;
	
	private String cnoCode;
	private String cno;
	
	private List<Map<String, String>> quoteDataList;
	
	// ------------------------------------------ Constructor
	
	public Certifica2Info() {
		super();
	}
	
	// ------------------------------------------ Getters / Setters

	public String getRegime() {
		return regime;
	}
	
	public String getMdCtz() {
		return mdCtz;
	}

	public String getRepresentativeDocument() {
		return representativeDocument;
	}

	public void setRepresentativeDocument(String representativeDocument) {
		this.representativeDocument = representativeDocument;
	}

	public String getRepresentativeName() {
		return representativeName;
	}

	public void setRepresentativeName(String representativeName) {
		this.representativeName = representativeName;
	}

	public String getRepresentativeSurname() {
		return representativeSurname;
	}

	public void setRepresentativeSurname(String representativeSurname) {
		this.representativeSurname = representativeSurname;
	}

	public String getRepresentativeWork() {
		return representativeWork;
	}

	public void setRepresentativeWork(String representativeWork) {
		this.representativeWork = representativeWork;
	}

	public void setRegime(String regime) {
		this.regime = regime;
	}
	
	public void setMdCtz(String mdCtz) {
		this.mdCtz = mdCtz;
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
	
	public String getSSNumber() {
		return ssNumber;
	}

	public void setSSNumber(String ssNumber) {
		this.ssNumber = ssNumber;
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

	public String getEnterpriseName() {
		return enterpriseName;
	}

	public void setEnterpriseName(String enterpriseName) {
		this.enterpriseName = enterpriseName;
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

	public String getErteCode() {
		return erteCode;
	}

	public void setErteCode(String erteCode) {
		this.erteCode = erteCode;
	}

	public String getErteCoef() {
		return erteCoef;
	}

	public void setErteCoef(String erteCoef) {
		this.erteCoef = erteCoef;
	}

	public Date getErteEnd() {
		return erteEnd;
	}

	public void setErteEnd(Date erteEnd) {
		this.erteEnd = erteEnd;
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

	public Date getSeniorityDate() {
		return seniorityDate;
	}
	
	public void setSeniorityDate(Date seniorityDate) {
		this.seniorityDate = seniorityDate;
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
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getGeozone() {
		return geozone;
	}

	public void setGeozone(String geozone) {
		this.geozone = geozone;
	}

	public String getCnaeCode() {
		return cnaeCode;
	}

	public void setCnaeCode(String cnaeCode) {
		this.cnaeCode = cnaeCode;
	}

	public String getCnae() {
		return cnae;
	}

	public void setCnae(String cnae) {
		this.cnae = cnae;
	}

	public String getCnoCode() {
		return cnoCode;
	}

	public void setCnoCode(String cnoCode) {
		this.cnoCode = cnoCode;
	}

	public String getCno() {
		return cno;
	}

	public void setCno(String cno) {
		this.cno = cno;
	}

	@Override
	public String toString() {
		String toString = "Certific@2 Info \n { \n";
		
		if(AonStringUtils.isNotBlank(regime)) toString += "\t Regime: " + regime + "\n";
		if(AonStringUtils.isNotBlank(mdCtz)) toString += "\t MdCtz: " + mdCtz + "\n";
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
