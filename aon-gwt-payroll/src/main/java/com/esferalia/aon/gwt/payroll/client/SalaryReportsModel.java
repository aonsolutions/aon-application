package com.esferalia.aon.gwt.payroll.client;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

class SalaryReportsModel extends AbstractReportsModel<IDocument>
		implements IDocument {

	private List<Salary> salaries;
	
	private EmployeesServiceAsync employeesService;

	public SalaryReportsModel(Salary salary, EmployeesServiceAsync employeesService) {
		this(Arrays.asList(salary),employeesService);
	}

	public SalaryReportsModel(List<Salary> salaries, EmployeesServiceAsync employeesService) {
		this.salaries = salaries;
		this.employeesService = employeesService;
		first();
	}

	@Override
	public int size() {
		return salaries.size();
	}

	@Override
	public IDocument current() {
		return this;
	}
	
	@Override
	public void print() {
		download();
	}
	
	@Override
	public void download() {
		download("pdf");
	}
	
	@Override
	public void download(String format) {
		Salary salary = salaries.get(currentIndex());
		String printURL = URL.encode(GWT.getModuleBaseURL() + "salary/" + salary.getId() + "." + format );
		Window.open(printURL, "_blank", null);
	}
	
	@Override
	public void getAsHTML(int zoom, AsyncCallback<String> callback) {
		Salary salary = salaries.get(currentIndex());
		employeesService.getSalaryReceiptHTML(salary, zoom, callback);
	}
	
	@Override
	public String[] getSupportedFormats() {
		// TODO Auto-generated method stub
		return new String [] {};
	}
	
}