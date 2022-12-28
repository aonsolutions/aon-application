package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.payroll.client.PayrollEmailDialog.Type;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.Period;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfo;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfoFilter;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class EnterpriseSalaryObject {
	
	// --------------------------------------------- Variables
	
	final DomainEnterprisesServiceAsync enterpriseService = DomainEnterprisesServiceAsync.newInstance();
	final DomainEmployeesServiceAsync employeesService = DomainEmployeesServiceAsync.newInstance();
	
	private Enterprise enterprise;
	
	private List<EmployeeInfo> enterpriseEmployees;
	private List<SalaryInfo> enterpriseSalaries;
	private List<Workplace> workplaces;
	
	private SalaryInfoFilter filter;
	
	private String emailStatus;
	
	private Date minDate;
	private Date maxDate;
	
	// --------------------------------------------- Constructor
	
	public EnterpriseSalaryObject() {
		super();
	}

	public EnterpriseSalaryObject(Enterprise enterprise) {
		this.enterprise = enterprise;
		this.workplaces = enterprise.getWorkplaces();
		this.filter = new SalaryInfoFilter();
	}
	
	// --------------------------------------------- Database Methods
	
	public void getSalariesDates(Consumer<Period> success, Consumer<Throwable> failure){
		
		if(filter.getEmployeeId() == null && filter.getWorkplaceId() == null)
			filter.setEnterpriseId(enterprise.getId());
		else
			filter.setEnterpriseId(null);
		
		employeesService.getSalariesDates(filter, new AsyncCallback<Period>(){

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(Period result) {
				minDate = result.getStart();
				maxDate = result.getEnd();
				getEnterpriseEmployeesDB(
						s -> success.accept(result), 
						f -> {}
				);
			}
			
		});
	}

	public void getSalaries(Consumer<List<SalaryInfo>> success, Consumer<Throwable> failure){
		
		if(filter.getEmployeeId() == null && filter.getWorkplaceId() == null)
			filter.setEnterpriseId(enterprise.getId());
		else
			filter.setEnterpriseId(null);
		
		employeesService.getSalaries(filter, new AsyncCallback<List<SalaryInfo>>(){

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
	
	public void getEnterpriseEmployeesDB(Consumer<List<EmployeeInfo>> success, Consumer<Throwable> failure){
		employeesService.getEnterpriseActiveEmployees(enterprise.getId(), new AsyncCallback<List<EmployeeInfo>>() {

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(List<EmployeeInfo> result) {
				enterpriseEmployees = result;
				success.accept(result);
			}
		});
	}
	
	public void deleteSalaries(Set<SalaryInfo> salaries, Consumer<Void> success, Consumer<Throwable> failure) {
		ArrayList<Integer> ids = new ArrayList<>();
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
	
	public void sendPayrollEmail(Type type, HashMap<String, String> params, String from, String to, String cc, String cco, String bodyHTML, Consumer<String> success, Consumer<Throwable> failure) {
		enterpriseService.sendPayrollEmail(type, params, from, to, cc, cco, bodyHTML, new AsyncCallback<String>() {

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
	
	public void getPDFSalaries(Integer enterpriseId, List<Integer> salaryIds, Consumer<String> success, Consumer<Throwable> failure) {
		enterpriseService.getSalariesPDF(enterpriseId, salaryIds, new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(String dataURI) {
				success.accept(dataURI);
			}
		});
	}
	
	// --------------------------------------------- Get Info Methods
	
	public List<SalaryInfo> getEnterpriseSalaries() {
		this.enterpriseSalaries.sort((o1, o2) -> compareString(o1, o2, o1.getEmployeeName(), o2.getEmployeeName()));
		return this.enterpriseSalaries;
	}
	
	public SalaryInfoFilter getFilter() {
		return this.filter;
	}
	
	public String getEnterpriseName() {
		return this.enterprise.getName();
	}
	
	public String getEmailStatus(){
		return this.emailStatus;
	}
	
	public Date getMinDate() {
		return minDate;
	}

	public Date getMaxDate() {
		return maxDate;
	}

	public List<String> getWorkplacesNames(){
		List<String> workplaceNames = new ArrayList<>();
		workplaces.forEach(workplace -> {
			if(workplace.getId() >= 0) workplaceNames.add(workplace.getDescription());
		});
		
		return workplaceNames;
	}
	
	public ArrayList<String> getEnterpriseEmployeesName(){
		ArrayList<String> names = new ArrayList<>();
		enterpriseEmployees.forEach(employeeInfo -> {
			if(AonStringUtils.isNotBlank(employeeInfo.getName())) names.add(employeeInfo.getName() + ", " + employeeInfo.getSurName());
		});
		
		return names;
	}
	
	public EmployeeInfo getEmployeeDataByNameSurname(String nameSurname){
		if(AonStringUtils.isBlank(nameSurname)) return null;
		
		String name = nameSurname.split(", ")[0].trim();
		String surname = nameSurname.split(", ")[1].trim();
		
		for(EmployeeInfo employee : enterpriseEmployees)
			if(AonStringUtils.equalsIgnoreCase(name,employee.getName().trim()) && AonStringUtils.equalsIgnoreCase(surname,employee.getSurName().trim())) 
				return employee;
		
		return null;
	}

	public Workplace getWorkplaceByDescription(String workplaceDescription) {
		for(Workplace workplace : workplaces)
			if(AonStringUtils.equalsIgnoreCase(workplaceDescription, workplace.getDescription()))
				return workplace;
		
		return null;
	}
	
	// --------------------------------------------- Auxiliar Methods
	
	private int compareString(Object o1, Object o2, String s1, String s2) {
		if (o1 == o2) return 0;
		else if (o1 == null) return -1;
		else if (o2 == null) return 1;
		else
        	return s1.compareTo(s2);
	}
	
		
}
