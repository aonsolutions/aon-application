package com.esferalia.aon.in.payroll.excel.contract;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AnualDaysEntryExcel implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String fullName;
    private String document;
    private String naf;
    
    private String startDate;
    private String endDate;
    
    private String gender;
    
    private String agreementLevel;
    private String agreementCategory;
    
    private String contractType;
    private String partiality;
    
    private List<MonthlyDaysEntryExcel> meses = new ArrayList<>();
	
	public AnualDaysEntryExcel() {
		super();
	}

	public String getFullName() {
		return fullName;
	}

	public AnualDaysEntryExcel setFullName(String fullName) {
		this.fullName = fullName;
		return this;
	}

	public String getDocument() {
		return document;
	}

	public AnualDaysEntryExcel setDocument(String document) {
		this.document = document;
		return this;
	}

	public String getNaf() {
		return naf;
	}

	public AnualDaysEntryExcel setNaf(String naf) {
		this.naf = naf;
		return this;
	}

	public String getStartDate() {
		return startDate;
	}

	public AnualDaysEntryExcel setStartDate(String startDate) {
		this.startDate = startDate;
		return this;
	}

	public String getEndDate() {
		return endDate;
	}

	public AnualDaysEntryExcel setEndDate(String endDate) {
		this.endDate = endDate;
		return this;
	}

	public String getGender() {
		return gender;
	}

	public AnualDaysEntryExcel setGender(String gender) {
		this.gender = gender;
		return this;
	}

	public String getAgreementLevel() {
		return agreementLevel;
	}

	public AnualDaysEntryExcel setAgreementLevel(String agreementLevel) {
		this.agreementLevel = agreementLevel;
		return this;
	}

	public String getAgreementCategory() {
		return agreementCategory;
	}

	public AnualDaysEntryExcel setAgreementCategory(String agreementCategory) {
		this.agreementCategory = agreementCategory;
		return this;
	}

	public String getContractType() {
		return contractType;
	}

	public AnualDaysEntryExcel setContractType(String contractType) {
		this.contractType = contractType;
		return this;
	}

	public String getPartiality() {
		return partiality;
	}

	public AnualDaysEntryExcel setPartiality(String partiality) {
		this.partiality = partiality;
		return this;
	}

	public List<MonthlyDaysEntryExcel> getMeses() {
		return meses;
	}

	public AnualDaysEntryExcel setMeses(List<MonthlyDaysEntryExcel> meses) {
		this.meses = meses;
		return this;
	}
	
	@Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Informe Mensual de ").append(fullName).append(" (DNI: ").append(document)
          .append(", NAF: ").append(naf).append(")\n");

        for (MonthlyDaysEntryExcel mes : meses) {
            sb.append(mes).append("\n");
        }

        return sb.toString();
    }
	
}
