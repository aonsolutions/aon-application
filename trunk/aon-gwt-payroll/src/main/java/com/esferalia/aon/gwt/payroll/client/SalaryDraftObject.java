package com.esferalia.aon.gwt.payroll.client;

import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class SalaryDraftObject {
	
	interface CalculateCallback {
		void onCalculateSucces( SalaryDraftObject object);
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


}
