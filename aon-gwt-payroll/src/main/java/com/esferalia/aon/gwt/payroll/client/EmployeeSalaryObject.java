package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.esferalia.aon.gwt.payroll.client.PayrollEmailDialog.Type;
import com.esferalia.aon.gwt.payroll.shared.Period;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfo;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfoFilter;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class EmployeeSalaryObject {
	
	// --------------------------------------------- Variables
	
	final DomainEnterprisesServiceAsync impl = DomainEnterprisesServiceAsync.newInstance();
	final DomainEmployeesServiceAsync employeesService = DomainEmployeesServiceAsync.newInstance();
	
	private Integer employeeId;
	private String fullname;
	
	private List<SalaryInfo> employeeSalaries;
	
	private SalaryInfoFilter filter;
	
	private String emailStatus;
	
	private Date minDate;
	private Date maxDate;
	
	// --------------------------------------------- Constructor
	
	public EmployeeSalaryObject() {
		super();
	}

	public EmployeeSalaryObject(Integer employeeId, String fullname) {
		this.employeeId = employeeId;
		this.fullname = fullname;
		this.filter = new SalaryInfoFilter();
		this.emailStatus = "";
	}
	
	// --------------------------------------------- Database Methods
	
	public void getSalariesDates(Consumer<Period> success, Consumer<Throwable> failure){
		
		filter.setEmployeeId(this.employeeId);
		
		employeesService.getSalariesDates(filter, new AsyncCallback<Period>(){

			@Override
			public void onFailure(Throwable caught) {
				failure.accept(caught);
			}

			@Override
			public void onSuccess(Period result) {
				minDate = result.getStart();
				maxDate = result.getEnd();
				success.accept(result);
			}
			
		});
	}
	
	public void getSalaries(Consumer<List<SalaryInfo>> success, Consumer<Throwable> failure){
		
		filter.setEmployeeId(this.employeeId);
		
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
	
	public void sendPayrollEmail(Type type, HashMap<String, String> params, String from, String to, String cc, String cco, String bodyHTML, Consumer<String> success, Consumer<Throwable> failure) {
		impl.sendPayrollEmail(type, params, from, to, cc, cco, bodyHTML, new AsyncCallback<String>() {

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
	
	public void getPDFSettle(Integer settleId, Consumer<String> success, Consumer<Throwable> failure) {
		impl.getSettlePDF(settleId, new AsyncCallback<String>() {

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
	
	// --------------------------------------------- Getter Methods
	
	public String getEmployeeName(){
		return this.fullname;
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
	
	public Date getMinDate() {
		return minDate;
	}

	public Date getMaxDate() {
		return maxDate;
	}
		
}
