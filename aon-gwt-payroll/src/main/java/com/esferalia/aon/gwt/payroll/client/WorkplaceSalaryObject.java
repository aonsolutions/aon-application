package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.payroll.client.PayrollEmailDialog.Type;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.Mail;
import com.esferalia.aon.gwt.payroll.shared.Period;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfo;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfoFilter;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceEmployees;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class WorkplaceSalaryObject {
	
	// --------------------------------------------- Variables
	
	final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	final DomainEmployeesServiceAsync employeesService = DomainEmployeesServiceAsync.newInstance();
	
	private Workplace workplace;
	
	private WorkplaceEmployees workplaceEmployees;
	private List<SalaryInfo> workplaceSalaries;
	
	private SalaryInfoFilter filter;
	
	private String emailStatus;
	
	private Date minDate;
	private Date maxDate;
	
	// --------------------------------------------- Constructor
	
	public WorkplaceSalaryObject() {
		super();
	}
	
	public WorkplaceSalaryObject(Workplace workplace) {
		this.workplace = workplace;
		this.filter = new SalaryInfoFilter();
		this.workplaceEmployees = new WorkplaceEmployees();
	}
	
	// --------------------------------------------- Database Methods
	
	public void getSalariesDates(Consumer<Period> success, Consumer<Throwable> failure){
		
		if(filter.getEmployeeId() == null)
			filter.setWorkplaceId(workplace.getId());
		else
			filter.setWorkplaceId(null);
		
		employeesService.getSalariesDates(filter, new AsyncCallback<Period>(){

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(Period result) {
				minDate = result.getStart();
				maxDate = result.getEnd();
				getWorkplaceEmployeesDB(
						s -> success.accept(result), 
						f -> {}
				);
			}
			
		});
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
				success.accept(result);
			}
			
		});
	}
	
	public void getWorkplaceEmployeesDB(Consumer<WorkplaceEmployees> success, Consumer<Throwable> failure){
		employeesService.getWorkplaceActiveEmployees(workplace.getId(), new AsyncCallback<WorkplaceEmployees>() {

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(WorkplaceEmployees result) {
				workplaceEmployees = result;
				success.accept(result);
			}
		});
	}
	
	public void deleteSalaries(Set<SalaryInfo> salaries, Consumer<Void> success, Consumer<Throwable> failure) {
		ArrayList<Integer> ids = new ArrayList<>();
		salaries.forEach(salary -> ids.add(salary.getId()));
		
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
	
	public void sendPayrollEmail(Type type, HashMap<String, String> params, Mail mail, Consumer<String> success, Consumer<Throwable> failure) {
		impl.sendPayrollEmail(type, params, mail, new AsyncCallback<String>() {

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
		impl.getSalariesPDF(enterpriseId, salaryIds, new AsyncCallback<String>() {

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
	
	// --------------------------------------------- Getters Methods
	
	public String getWorkplaceName() {
		return this.workplace.getDescription();
	}
	
	public WorkplaceEmployees getWorkplaceEmployees(){
		return this.workplaceEmployees;
	}
	
	public SalaryInfoFilter getFilter() {
		return this.filter;
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
	
	public List<SalaryInfo> getWorkplaceSalaries() {
		this.workplaceSalaries.sort((o1, o2) -> compareString(o1, o2, o1.getEmployeeName(), o2.getEmployeeName()));
		return this.workplaceSalaries;
	}
	
	public EmployeeInfo getEmployeeDataByNameSurname(String nameSurname){
		if(AonStringUtils.isBlank(nameSurname)) return null;
		
		String name = nameSurname.split(", ")[0].trim();
		String surname = nameSurname.split(", ")[1].trim();
		
		for(EmployeeInfo employee : workplaceEmployees.getWorkplaceEmployees())
			if( AonStringUtils.equalsIgnoreCase(name,employee.getName().trim()) && 
				AonStringUtils.equalsIgnoreCase(surname,employee.getSurName().trim()))
				return employee;
			
		
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
