package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfo;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class EnterpriseSalaryObject {
	
	private DomainEmployeesServiceAsync employeesService;
	private Integer enterpriseId;
	private List<SalaryInfo> enterpriseSalaries;
	
	public EnterpriseSalaryObject() {
		super();
	}

	public EnterpriseSalaryObject(Integer enterpriseId, DomainEmployeesServiceAsync employeesService) {
		this.enterpriseId = enterpriseId;
		this.employeesService = employeesService;
	}

	public void getEnterpriseSalariesDB(Consumer<List<SalaryInfo>> success, Consumer<Throwable> failure){
		
//		employeesService.getEnterpriseSalaries(enterpriseId, new AsyncCallback<List<SalaryInfo>>(){
//
//			@Override
//			public void onFailure(Throwable caught) {
//				failure.accept(caught);
//			}
//
//			@Override
//			public void onSuccess(List<SalaryInfo> result) {
//				enterpriseSalaries = result;
//				success.accept(result);
//			}
//			
//		});
	}
	
	public void delete(Set<SalaryInfo> salaries, Consumer<String> success, Consumer<Throwable> failure) {
		ArrayList<Integer> ids = new ArrayList<Integer>();
		for(SalaryInfo salary : salaries) {
			ids.add(salary.getId());
		}
		
		employeesService.deleteSalariesDB(ids, new AsyncCallback<String>(){

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(String result) {
				success.accept(result);
			}
			
		});
	}
	
	public List<SalaryInfo> getEnterpriseSalaries() {
		return this.enterpriseSalaries;
	}

		
}
