package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfo;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfoFilter;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceEmployees;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class WorkplaceSalaryObject {
	
	//Starting Service
	final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	final DomainEmployeesServiceAsync employeesService = DomainEmployeesServiceAsync.newInstance();
	
	private Workplace workplace;
	
	private WorkplaceEmployees workplaceEmployees;
	private List<SalaryInfo> workplaceSalaries;
	
	private SalaryInfoFilter filter;
	
	private String checkEmailEmployeesStatus;
	private String emailStatus;
	
	public WorkplaceSalaryObject() {
		super();
	}
	
	public WorkplaceSalaryObject(Workplace workplace) {
		this.workplace = workplace;
		this.filter = new SalaryInfoFilter();
		this.workplaceEmployees = new WorkplaceEmployees();
	}

	public void getSalaries(Consumer<List<SalaryInfo>> success, Consumer<Throwable> failure){
		
		if(filter.getEmployeeId() == null)
			filter.setWorkplaceId(workplace.getId());
		else
			filter.setWorkplaceId(null);
		
		employeesService.getSalaries(filter, new AsyncCallback<List<SalaryInfo>>(){

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(List<SalaryInfo> result) {
				workplaceSalaries = result;
				getWorkplaceEmployeesDB(
						s -> {
							success.accept(result);
						}, f -> {}
				);
			}
			
		});
	}
	
	public void getWorkplaceEmployeesDB(Consumer<WorkplaceEmployees> success, Consumer<Throwable> failure){
		employeesService.getWorkplaceActiveEmployees(workplace.getId(), new AsyncCallback<WorkplaceEmployees>() {

			@Override
			public void onFailure(Throwable caught) {}

			@Override
			public void onSuccess(WorkplaceEmployees result) {
				workplaceEmployees = result;
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
	
	public String getWorkplaceName() {
		return this.workplace.getDescription();
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
	
	public EmployeeInfo getEmployeeDataByNameSurname(String nameSurname){
		String name = nameSurname.split(", ")[0].trim();
		String surname = nameSurname.split(", ")[1].trim();
		
		for(EmployeeInfo employee : workplaceEmployees.getWorkplaceEmployees()) {
			if( AonStringUtils.equalsIgnoreCase(name,employee.getName().trim()) && 
				AonStringUtils.equalsIgnoreCase(surname,employee.getSurName().trim())) {
				return employee;
			}
		}
		return null;
	}
	
	public String getEmailStatus(){
		return this.emailStatus;
	}
	
	public String getCheckEmailEmployeesStatus(){
		return this.checkEmailEmployeesStatus;
	}


		
}
