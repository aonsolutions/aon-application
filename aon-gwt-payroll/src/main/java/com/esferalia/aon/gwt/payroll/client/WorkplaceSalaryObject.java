package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfo;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfoFilter;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceEmployees;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class WorkplaceSalaryObject {
	
	//Starting Service
	final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	
	private DomainEmployeesServiceAsync employeesService;
	private Integer workplaceId;
	private List<SalaryInfo> workplaceSalaries;
	private SalaryInfoFilter filter;
	private WorkplaceEmployees workplaceEmployees;
	private String emailStatus;
	private String checkEmailEmployeesStatus;
	
	public WorkplaceSalaryObject() {
		super();
	}

	public WorkplaceSalaryObject(Integer workplaceId, DomainEmployeesServiceAsync employeesService) {
		this.workplaceId = workplaceId;
		this.employeesService = employeesService;
		this.filter = new SalaryInfoFilter();
		this.workplaceEmployees = new WorkplaceEmployees();
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
				//success.accept(result);
				getWorkplaceEmployeesDB(
						s -> {
							success.accept(result);
						}, f -> {}
				);
			}
			
		});
	}
	
	public void getWorkplaceEmployeesDB(Consumer<WorkplaceEmployees> success, Consumer<Throwable> failure){
		employeesService.getWorkplaceActiveEmployees(workplaceId, new AsyncCallback<WorkplaceEmployees>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(WorkplaceEmployees result) {
				workplaceEmployees = result;
				success.accept(result);
			}
		});
	}
	
	public void getFilterSalariesDB(Consumer<List<SalaryInfo>> success, Consumer<Throwable> failure){		
		filter.setWorkplaceId(workplaceId);
		employeesService.getFilterSalaries(filter, new AsyncCallback<List<SalaryInfo>>(){

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
	
	public List<SalaryInfo> getWorkplaceSalaries() {
		return this.workplaceSalaries;
	}
	
	public SalaryInfoFilter getFilter() {
		return this.filter;
	}
	
	public WorkplaceEmployees getWorkplaceEmployees(){
		return this.workplaceEmployees;
	}
	
	public EmployeeInfo getEmployeeDataByNameSurname(String name, String surname){
		EmployeeInfo employeeInfo = null;
		for(EmployeeInfo employee : workplaceEmployees.getWorkplaceEmployees())
			if(name == employee.getName() && surname == employee.getSurName()) {
				employeeInfo = employee;
			}
		return employeeInfo;
	}
	
	public String getEmailStatus(){
		return this.emailStatus;
	}
	
	public String getCheckEmailEmployeesStatus(){
		return this.checkEmailEmployeesStatus;
	}


		
}
