package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfo;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfoFilter;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class EnterpriseSalaryObject {
	
	//Starting Service
	final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	
	private DomainEmployeesServiceAsync employeesService;
	private Integer enterpriseId;
	private List<SalaryInfo> enterpriseSalaries;
	private SalaryInfoFilter filter;
	private List<Workplace> workplaces;
	private List<EmployeeInfo> enterpriseEmployees;
	private String emailStatus;
	private String checkEmailEmployeesStatus;
	
	public EnterpriseSalaryObject() {
		super();
	}

	public EnterpriseSalaryObject(Enterprise enterprise, DomainEmployeesServiceAsync employeesService) {
		this.enterpriseId = enterprise.getId();
		this.employeesService = employeesService;
		this.workplaces = enterprise.getWorkplaces();
		this.filter = new SalaryInfoFilter();
	}

	public void getEnterpriseSalariesDB(Consumer<List<SalaryInfo>> success, Consumer<Throwable> failure){
		
		employeesService.getEnterpriseSalaries(enterpriseId, new AsyncCallback<List<SalaryInfo>>(){

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(List<SalaryInfo> result) {
				enterpriseSalaries = result;
				getEnterpriseEmployeesDB(
						s -> {
							success.accept(result);
						}, f -> {}
				);
			}
			
		});
		
	}
	
	public void getEnterpriseEmployeesDB(Consumer<List<EmployeeInfo>> success, Consumer<Throwable> failure){
		employeesService.getEnterpriseActiveEmployees(enterpriseId, new AsyncCallback<List<EmployeeInfo>>() {

			@Override
			public void onFailure(Throwable caught) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onSuccess(List<EmployeeInfo> result) {
				enterpriseEmployees = result;
				success.accept(result);
			}
		});
	}
	
	public void getFilterSalariesDB(Consumer<List<SalaryInfo>> success, Consumer<Throwable> failure){
		if(null == filter.getWorkplaceId() && null == filter.getEmployeeId() && null == filter.getEnterpriseId())
			filter.setEnterpriseId(enterpriseId);
		
		employeesService.getFilterSalaries(filter, new AsyncCallback<List<SalaryInfo>>(){

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(List<SalaryInfo> result) {
				enterpriseSalaries = result;
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
	
	public List<SalaryInfo> getEnterpriseSalaries() {
		return this.enterpriseSalaries;
	}
	
	public SalaryInfoFilter getFilter() {
		return this.filter;
	}
	
	public List<String> getWorkplacesNames(){
		List<String> workplaceNames = new ArrayList<String>();
		
		for(Workplace workplace: workplaces) {
			if(workplace.getId() >= 0) {
				workplaceNames.add(workplace.getDescription());
			}
		}
		
		return workplaceNames;
	}
	
	public ArrayList<String> getEnterpriseEmployeesName(){
		ArrayList<String> names = new ArrayList<>();
		for(EmployeeInfo employeeInfo : enterpriseEmployees)
			if("" != employeeInfo.getName())
				names.add(employeeInfo.getName() + ", " + employeeInfo.getSurName());
		return names;
	}
	
	public EmployeeInfo getEmployeeDataByNameSurname(String name, String surname){
		EmployeeInfo employeeInfo = null;
		for(EmployeeInfo employee : enterpriseEmployees)
			if(name == employee.getName() && surname == employee.getSurName()) {
				employeeInfo = employee;
			}
		return employeeInfo;
	}

	public Workplace getWorkplaceByDescription(String workplaceDescription) {
		Workplace workplaceInfo = null;
		
		for(Workplace workplace : workplaces) {
			if(workplaceDescription == workplace.getDescription())
				workplaceInfo = workplace;
		}
		
		return workplaceInfo;
	}
	
	public Integer getEnterpriseId() {
		return this.enterpriseId;
	}
	
	public String getEmailStatus(){
		return this.emailStatus;
	}
	
	public String getCheckEmailEmployeesStatus(){
		return this.checkEmailEmployeesStatus;
	}

		
}
