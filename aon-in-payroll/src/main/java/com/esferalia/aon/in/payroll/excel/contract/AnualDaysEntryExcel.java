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
