package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfo;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfoFilter;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class EmployeeSalaryObject {
	
	//Starting Service
	final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	final DomainEmployeesServiceAsync employeesService = DomainEmployeesServiceAsync.newInstance();
	
	private Employee employee;
	
	private List<SalaryInfo> employeeSalaries;
	
	private SalaryInfoFilter filter;
	
	private String checkEmailEmployeesStatus;
	private String emailStatus;
	
	public EmployeeSalaryObject() {
		super();
	}

	public EmployeeSalaryObject(Employee employee) {
		this.employee = employee;
		this.filter = new SalaryInfoFilter();
		this.emailStatus = "";
	}
	
	public void getSalaries(Consumer<List<SalaryInfo>> success, Consumer<Throwable> failure){
		
		filter.setEmployeeId(employee.getId());
		
		employeesService.getSalaries(filter, new AsyncCallback<List<SalaryInfo>>(){

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(List<SalaryInfo> result) {
				employeeSalaries = result;
				success.accept(result);
			}
			
		});
	}
	
	public void deleteSalaries(Set<SalaryInfo> salaries, Consumer<Void> success, Consumer<Throwable> failure) {
		ArrayList<Integer> ids = new ArrayList<Integer>();
		for(SalaryInfo salary : salaries) {
			ids.add(salary.getId());
		}
		
		employeesService.deleteSalaries(ids, new AsyncCallback<Void>(){

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(Void result) {
				success.accept(result);
			}
			
		});
	}
	
	public void sendPayrollEmail(String from, String to, String cc, String cco, String bodyHTML, Consumer<String> success, Consumer<Throwable> failure) {
		impl.sendPayrollEmail(from, to, cc, cco, bodyHTML, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(String result) {
				emailStatus = result;
				success.accept(result);
			}
			
		});
	}
	
	public void checkEmployeesEmails(Set<SalaryInfo> salaries, Consumer<String> success, Consumer<Throwable> failure) {
		ArrayList<Integer> salaryIds = new ArrayList<Integer>();
		for(SalaryInfo salary : salaries) {
			salaryIds.add(salary.getId());
		}
		
		impl.checkEmployeesEmails(salaryIds, new AsyncCallback<String>(){

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(String result) {
				checkEmailEmployeesStatus = result;
				success.accept(result);
			}
			
		});
	}
	
	public void sendPayrollEmailToEmployees(String from, String cc, String cco, String bodyHTML, String completeURL, Consumer<String> success, Consumer<Throwable> failure) {
		impl.sendPayrollEmailToEmployees(from, cc, cco, bodyHTML, completeURL, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(String result) {
				emailStatus = result;
				success.accept(result);
			}
			
		});
	}
	
	public String getEmployeeName(){
		return this.employee.getFullname();
	}
	
	public List<SalaryInfo> getEmployeeSalaries() {
		return this.employeeSalaries;
	}
	
	public SalaryInfoFilter getFilter() {
		return this.filter;
	}
	
	public String getEmailStatus(){
		return this.emailStatus;
	}
	
	public String getCheckEmailEmployeesStatus(){
		return this.checkEmailEmployeesStatus;
	}

		
}
