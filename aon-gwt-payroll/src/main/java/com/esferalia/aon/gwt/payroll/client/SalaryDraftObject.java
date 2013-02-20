package com.esferalia.aon.gwt.payroll.client;

import java.util.List;

import org.apache.tools.ant.taskdefs.Sleep;

import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SalaryDraftObject {

	interface CalculateCallback {
		void onCalculateSucces(SalaryDraftObject object);

		void onCalculateFailure(Throwable throwable);
	}
	
	private SalaryDraft salaryDraft;
	private EmployeesServiceAsync employeesServiceAsync;

	public SalaryDraftObject(SalaryDraft salaryDraft,
			EmployeesServiceAsync employeesServiceAsync) {
		this.salaryDraft = salaryDraft;
		this.employeesServiceAsync = employeesServiceAsync;
	}

	public void calculate(final CalculateCallback callback) {
		employeesServiceAsync.calculateSalaryDraft(salaryDraft,
				new AsyncCallback<SalaryDraft>() {

					@Override
					public void onSuccess(SalaryDraft result) {
						SalaryDraftObject.this.salaryDraft = result;
						callback.onCalculateSucces(SalaryDraftObject.this);
					}

					@Override
					public void onFailure(Throwable caught) {
						callback.onCalculateFailure(caught);
					}
				});
	}

	public SalaryDraft getSalaryDraft() {
		return salaryDraft;
	}

	public void getAsHTML(int zoom, AsyncCallback<String> callback) {
		employeesServiceAsync.getSalaryDraftReceiptHTML(salaryDraft, zoom,
				callback);
	}



	public void download(String mime, AsyncCallback<String> callback) {
		employeesServiceAsync
				.getSalaryDraftReceipt(salaryDraft, mime, callback);
	}
	
	public void getPaymentConcepts(AsyncCallback<List<Payment>> callback) {
		employeesServiceAsync
		.getPaymentConcepts(callback);
	}

}
