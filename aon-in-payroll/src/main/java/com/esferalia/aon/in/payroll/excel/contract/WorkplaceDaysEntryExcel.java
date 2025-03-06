package com.esferalia.aon.in.payroll.excel.contract;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class WorkplaceDaysEntryExcel implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String description;
    
	private List<WorkplaceMonthlyDaysEntryExcel> meses = new ArrayList<>();
	private List<AnualDaysEntryExcel> contracts = new ArrayList<>();
	
	public WorkplaceDaysEntryExcel() {
		super();
	}

	public String getDescription() {
		return description;
	}

	public WorkplaceDaysEntryExcel setDescription(String description) {
		this.description = description;
		return this;
	}

	public List<WorkplaceMonthlyDaysEntryExcel> getMeses() {
		return meses;
	}

	public WorkplaceDaysEntryExcel setMeses(List<WorkplaceMonthlyDaysEntryExcel> meses) {
		this.meses = meses;
		return this;
	}
	
	public List<AnualDaysEntryExcel> getContracts() {
		return contracts;
	}

	public WorkplaceDaysEntryExcel setContracts(List<AnualDaysEntryExcel> contracts) {
		this.contracts = contracts;
		return this;
	}
	
}
