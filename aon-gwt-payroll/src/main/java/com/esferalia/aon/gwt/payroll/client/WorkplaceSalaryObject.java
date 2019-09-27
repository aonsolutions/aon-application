package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfo;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class WorkplaceSalaryObject {
	
	private DomainEmployeesServiceAsync employeesService;
	private Integer workplaceId;
	private List<SalaryInfo> workplaceSalaries;
	
	public WorkplaceSalaryObject() {
		super();
	}

	public WorkplaceSalaryObject(Integer workplaceId, DomainEmployeesServiceAsync employeesService) {
		this.workplaceId = workplaceId;
		this.employeesService = employeesService;
	}

	public void getWorkplaceSalariesDB(Consumer<List<SalaryInfo>> success, Consumer<Throwable> failure){
		
		employeesService.getWorkplaceSalaries(workplaceId, new AsyncCallback<List<SalaryInfo>>(){

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(List<SalaryInfo> result) {
				workplaceSalaries = result;
				success.accept(result);
			}
			
		});
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
	
	public List<SalaryInfo> getWorkplaceSalaries() {
		return this.workplaceSalaries;
	}

		
}
