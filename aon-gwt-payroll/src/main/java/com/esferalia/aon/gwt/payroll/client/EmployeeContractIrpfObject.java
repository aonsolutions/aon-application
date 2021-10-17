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
	
	// ----------------------------------------------- Constructor 
	
	public EmployeeContractIrpfObject(Integer contractId) {
		this.contractId = contractId;
		this.employeeIrpfList = new ArrayList<>();
	}

	// ----------------------------------------------- DataBase.Methods
	
	public void getEmployeeIrpf(Date date, Consumer<List<EmployeeIrpf>> success, Consumer<Throwable> failure) {
		employeesService.getEmployeeIrpf(contractId, date, new AsyncCallback<List<EmployeeIrpf>>() {
			
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
		employeesService.setEmployeeIrpf(contractId, employeeIrpfList, new AsyncCallback<Void>() {
			
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

	public EmployeeIrpf getEmployeeIrpf(Date date) {
		DateUtils.resetTime(date);
		
		for(EmployeeIrpf employeeIrpf : employeeIrpfList)
			if(DateUtils.equals(date, employeeIrpf.getDate()) && !employeeIrpf.isDelete())
				return employeeIrpf;
		
		return null;
	}
	
	public void createUpdateEmployeeIrpf(Date date, Double moneyBase, Double moneyQuote, Double inkindBase,
			Double inkindQuote, Double irpfPercent, Double baseCgc, Double baseCgp, Double employeeSSQuote, Double totalIrpf) {
		
		EmployeeIrpf employeeIrpf = getEmployeeIrpf(date);
		
		if(null == employeeIrpf) {
			employeeIrpf = new EmployeeIrpf();
			employeeIrpf.setNew(true);
			employeeIrpfList.add(employeeIrpf);
		}
		
		employeeIrpf.setDate(date)
					.setMoneyBase(moneyBase)
					.setMoneyQuote(moneyQuote)
					.setInkindBase(inkindBase)
					.setInkindQuote(inkindQuote)
					.setIrpfPercent(irpfPercent)
					.setBaseCgc(baseCgc)
					.setBaseCgp(baseCgp)
					.setEmployeeSSQuote(employeeSSQuote)
					.setTotalIrpf(totalIrpf);
	}

	public void deleteEmployeeIrpf(EmployeeIrpf employeeIrpf) {
		employeeIrpf.setDelete(true);
	}
	
}

