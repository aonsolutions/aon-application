package com.esferalia.aon.gwt.payroll.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class SalaryDraftDocument implements IDocument{

	private EmployeesServiceAsync employeesService;
	private com.esferalia.aon.gwt.payroll.shared.SalaryDraft salaryDraft;
	

	public SalaryDraftDocument(
			com.esferalia.aon.gwt.payroll.shared.SalaryDraft salaryDraft,  EmployeesServiceAsync employeesService) {
		this.salaryDraft = salaryDraft;
		this.employeesService = employeesService;
	}

	@Override
	public void print() {
		// TODO Auto-generated method stub

	}

	@Override
	public void download() {
		// TODO Auto-generated method stub

	}

	@Override
	public void download(String format) {
		// TODO Auto-generated method stub

	}

	@Override
	public String[] getSupportedFormats() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void getAsHTML(int zoom, AsyncCallback<String> callback) {
		employeesService.getSalaryDraftReceiptHTML(salaryDraft, zoom,
				callback);
	}
	
	
	public com.esferalia.aon.gwt.payroll.shared.SalaryDraft getSalaryDraft() {
		return salaryDraft;
	}
	
}
