package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.EmployeeIrpf;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class EmployeeContractIrpfObject {
	
	// ----------------------------------------------- Variables 
	
	private DomainEmployeesServiceAsync employeesService = DomainEmployeesServiceAsync.newInstance();
	
	private List<EmployeeIrpf> employeeIrpfList;
	private Integer contractId;
	private String ssNumber;
	private String fullName;
	private String document;
	private Date contractStartDate;
	
	// ----------------------------------------------- Constructor 
	
	public EmployeeContractIrpfObject(Integer contractId, String fullName, String document, String ssNumber, Date contractStartDate) {
		this.contractId = contractId;
		this.ssNumber = ssNumber;
		this.fullName = fullName;
		this.document = document;
		this.contractStartDate = contractStartDate;
		this.employeeIrpfList = new ArrayList<>();
	}

	// ----------------------------------------------- DataBase.Methods
	
	public void getEmployeeIrpf(Date date, Consumer<List<EmployeeIrpf>> success, Consumer<Throwable> failure) {
		employeesService.getEmployeeIrpf(ssNumber, document, date, new AsyncCallback<List<EmployeeIrpf>>() {
			
			@Override
			public void onSuccess(List<EmployeeIrpf> employeeIrpfListDB) {
				initEmployeeIrpfList(employeeIrpfListDB);
				success.accept(employeeIrpfListDB);
			}
			
			private void initEmployeeIrpfList(List<EmployeeIrpf> employeeIrpfListDB) {
				employeeIrpfList.clear();
				employeeIrpfList.addAll(employeeIrpfListDB);
				employeeIrpfList.sort((o1, o2) -> o1.getDate().compareTo(o2.getDate()));
			}

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	public void setEmployeeIrpf(Consumer<Void> success, Consumer<Throwable> failure) {
		employeesService.setEmployeeIrpf(contractId, fullName, document, ssNumber, employeeIrpfList, new AsyncCallback<Void>() {
			
			@Override
			public void onSuccess(Void result) {
				success.accept(result);
			}
			
			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}
		});
	}
	
	// ----------------------------------------------- Methods
	
	public Integer getContractStartYear() {
		return DateUtils.getYear(this.contractStartDate);
	}
	
	public String getSSNumber() {
		return this.ssNumber;
	}
	
	public String getFullName() {
		return this.fullName;
	}

	public List<EmployeeIrpf> getEmployeeIrpf(Date date) {
		DateUtils.resetTime(date);
		List<EmployeeIrpf> result = new ArrayList<>();
		
		for(EmployeeIrpf employeeIrpf : employeeIrpfList)
			if(DateUtils.equals(date, employeeIrpf.getDate()) && !employeeIrpf.isDelete())
				result.add(employeeIrpf);
		
		return result;
	}
	
	public void createUpdateEmployeeIrpf(Date date, Double moneyBase, Double moneyQuote, Double inkindBase,
			Double inkindQuote, Double irpfPercent, Double employeeSSQuote, Double totalIrpf, Integer salaryId) {
		
		EmployeeIrpf employeeIrpf = getEmployeeIrpf(date, salaryId);
		
		if(null == employeeIrpf) {
			employeeIrpf = new EmployeeIrpf();
			employeeIrpf.setNew(true);
			employeeIrpf.setSalaryType("Manual");
			employeeIrpfList.add(employeeIrpf);
		}
		
		employeeIrpf.setDate(date)
					.setMoneyBase(moneyBase)
					.setMoneyQuote(moneyQuote)
					.setInkindBase(inkindBase)
					.setInkindQuote(inkindQuote)
					.setIrpfPercent(irpfPercent)
					.setEmployeeSSQuote(employeeSSQuote)
					.setTotalIrpf(totalIrpf);
	}

	private EmployeeIrpf getEmployeeIrpf(Date date, Integer salaryId) {
		if(salaryId == null) {
			List<EmployeeIrpf> employeeIrpfListAux = getEmployeeIrpf(date);
			return employeeIrpfListAux.isEmpty() ? null : employeeIrpfListAux.get(0);
		}
		
		for(EmployeeIrpf employeeIrpf : employeeIrpfList)
			if(salaryId == employeeIrpf.getSalaryId() && !employeeIrpf.isDelete())
				return employeeIrpf;
		
		return null;
	}

	public void deleteEmployeeIrpf(EmployeeIrpf employeeIrpf) {
		employeeIrpf.setDelete(true);
	}

	public Double getAccumulateMoneyBase() {
		Double accumulate = 0.00;
		
		for(EmployeeIrpf employeeIrpf : employeeIrpfList)
			if(employeeIrpf.getMoneyBase() != null)
				accumulate += employeeIrpf.getMoneyBase();
		
		return accumulate;
	}

	public Double getAccumulateMoneyQuote() {
		Double accumulate = 0.00;
		
		for(EmployeeIrpf employeeIrpf : employeeIrpfList)
			if(employeeIrpf.getMoneyQuote() != null)
				accumulate += employeeIrpf.getMoneyQuote();
		
		return accumulate;
	}

	public Double getAccumulateInkindBase() {
		Double accumulate = 0.00;
		
		for(EmployeeIrpf employeeIrpf : employeeIrpfList)
			if(employeeIrpf.getInkindBase() != null)
				accumulate += employeeIrpf.getInkindBase();
		
		return accumulate;
	}

	public Double getAccumulateInkindQuote() {
		Double accumulate = 0.00;
		
		for(EmployeeIrpf employeeIrpf : employeeIrpfList)
			if(employeeIrpf.getInkindQuote() != null)
				accumulate += employeeIrpf.getInkindQuote();
		
		return accumulate;
	}

	public Double getAccumulateTotalIrpf() {
		Double accumulate = 0.00;
		
		for(EmployeeIrpf employeeIrpf : employeeIrpfList)
			if(employeeIrpf.getTotalIrpf() != null)
				accumulate += employeeIrpf.getTotalIrpf();
		
		return accumulate;
	}

	public Double getAccumulateEmployeeSSQuote() {
		Double accumulate = 0.00;
		
		for(EmployeeIrpf employeeIrpf : employeeIrpfList)
			if(employeeIrpf.getEmployeeSSQuote() != null)
				accumulate += employeeIrpf.getEmployeeSSQuote();
		
		return accumulate;
	}
	
}

