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

	private DomainEmployeesServiceAsync employeesService;
	private Employee employee;
	private List<SalaryInfo> employeeSalaries;
	private SalaryInfoFilter filter;
	private String emailStatus;
	private String checkEmailEmployeesStatus;
	
	public EmployeeSalaryObject() {
		super();
	}

	public EmployeeSalaryObject(Employee employee, DomainEmployeesServiceAsync employeesService) {
		this.employee = employee;
		this.employeesService = employeesService;
		this.filter = new SalaryInfoFilter();
		this.emailStatus = "";
	}
	
	public void getEmployeeSalariesDB(Consumer<List<SalaryInfo>> success, Consumer<Throwable> failure){
		
		employeesService.getEmployeeSalaries(employee.getId(), new AsyncCallback<List<SalaryInfo>>(){

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
	
	public void getFilterSalariesDB(Consumer<List<SalaryInfo>> success, Consumer<Throwable> failure){
		filter.setEmployeeId(this.employee.getId());
		employeesService.getFilterSalaries(filter, new AsyncCallback<List<SalaryInfo>>(){

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
