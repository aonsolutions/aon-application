package com.esferalia.aon.gwt.payroll.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public class SalaryPreviewDocument implements IDocument{

	private DomainEmployeesServiceAsync employeesService;
	private com.esferalia.aon.gwt.payroll.shared.SalaryPreview salaryPreview;
	

	public SalaryPreviewDocument(
			com.esferalia.aon.gwt.payroll.shared.SalaryPreview salaryPreview,  DomainEmployeesServiceAsync employeesService) {
		this.salaryPreview = salaryPreview;
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
		employeesService.getSalaryPreviewReceiptHTML(salaryPreview, zoom,
				callback);
	}
	
	
	public com.esferalia.aon.gwt.payroll.shared.SalaryPreview getSalaryPreview() {
		return salaryPreview;
	}
	
}
