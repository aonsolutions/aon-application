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
	
	private List<Enterprise> enterprises;
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
	
	public EnterpriseSalaryObject(List<Enterprise> enterprises) {
		this.enterprises = enterprises;
		this.filter = new SalaryInfoFilter();
	}
	
	// --------------------------------------------- Database Methods
	
	public void getSalariesDates(Consumer<Period> success, Consumer<Throwable> failure){
		
		if((enterprises == null || enterprises.isEmpty()) && filter.getEmployeeId() == null && filter.getWorkplaceId() == null)
			filter.setEnterpriseId(enterprise.getId());
		
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
		
		if((enterprises == null || enterprises.isEmpty()) && filter.getEmployeeId() == null && filter.getWorkplaceId() == null)
			filter.setEnterpriseId(enterprise.getId());
		
		employeesService.getSalaries(filter, new AsyncCallback<List<SalaryInfo>>(){

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(List<SalaryInfo> result) {
				enterpriseSalaries = new ArrayList<>();
				enterpriseSalaries.addAll(result);
				success.accept(enterpriseSalaries);
			}
			
		});
	}
	
	public void getEnterpriseEmployeesDB(Consumer<List<EmployeeInfo>> success, Consumer<Throwable> failure){
		if(null != enterprise && null != enterprise.getId())
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
		else {
			enterpriseEmployees = new ArrayList<>();
			success.accept(null);
		}
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
		if(null == enterprise || null == enterprise.getId()) 
			this.enterpriseSalaries.sort((o1, o2) -> compareString(o1, o2, o1.getEnterpriseName(), o2.getEnterpriseName()));
		else 
			this.enterpriseSalaries.sort((o1, o2) -> compareString(o1, o2, o1.getEmployeeName(), o2.getEmployeeName()));
		
		return this.enterpriseSalaries;
	}
	
	public SalaryInfoFilter getFilter() {
		return this.filter;
	}
	
	public List<Enterprise> getEnterprises() {
		return this.enterprises;
	}
	
	public void setEnterprise(Enterprise enterprise, Consumer<Void> success, Consumer<Throwable> failure) {
		this.enterprise = enterprise;
		getEnterpriseEmployeesDB(
				s -> success.accept(null), 
				f -> {}
		);
	}
	
	public void setEnterprise(Enterprise enterprise) {
		this.enterprise = enterprise;
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
	
	public ArrayList<String> getEnterprisesName(){
		ArrayList<String> names = new ArrayList<>();
		enterprises.forEach(enterprise -> {
			if(AonStringUtils.isNotBlank(enterprise.getName())) names.add(enterprise.getName());
		});
		
		return names;
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
	
	public Enterprise getEnterpriseByName(String enterpriseName){
		if(AonStringUtils.isBlank(enterpriseName)) return null;
		
		for(Enterprise enterprise : enterprises)
			if(AonStringUtils.equalsIgnoreCase(enterpriseName, enterprise.getName())) 
				return enterprise;
		
		return null;
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
