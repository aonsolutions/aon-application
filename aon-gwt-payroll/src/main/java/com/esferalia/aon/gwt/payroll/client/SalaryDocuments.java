package com.esferalia.aon.gwt.payroll.client;

import java.util.Arrays;
import java.util.List;

import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.URL;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

class SalaryDocuments extends AbstractSpinnable<IDocument> implements IDocument {

	private List<Salary> salaries;

	private EmployeesServiceAsync employeesService;

	public SalaryDocuments(Salary salary, EmployeesServiceAsync employeesService) {
		this(Arrays.asList(salary), employeesService);
	}

	public SalaryDocuments(List<Salary> salaries,
			EmployeesServiceAsync employeesService) {
		this.salaries = salaries;
		this.employeesService = employeesService;
		last();
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
		Salary salary = salaries.get(getCurrentIndex());
		String printURL = URL.encode(GWT.getModuleBaseURL() + "salary/"
				+ salary.getId() + "." + format);
		Window.open(printURL, "_blank", null);
	}

	@Override
	public void getAsHTML(int zoom, AsyncCallback<String> callback) {
		Salary salary = salaries.get(getCurrentIndex());
		employeesService.getSalaryReceiptHTML(salary, zoom, callback);
	}

	@Override
	public String[] getSupportedFormats() {
		// TODO Auto-generated method stub
		return new String[] {};
	}

	public void delete(final AsyncCallback<Void> callback) {
		final int index = getCurrentIndex();
		Salary salary = salaries.get(index);
		employeesService.delete(new Salary[] { salary },
				new AsyncCallback<Void>() {
					@Override
					public void onSuccess(Void v) {
						salaries.remove(index);
						if ( salaries.size() > 0 )
							setCurrentIndex(Math.min(salaries.size() - 1, index));
						callback.onSuccess(v);
					}

					@Override
					public void onFailure(Throwable caught) {
						callback.onFailure(caught);
					}
				});
	}

	public List<Salary> getSalaries() {
		return salaries;
	}

	public void setCurrent(Salary salary) {
		setCurrentIndex(salaries.indexOf(salary));
	}
}