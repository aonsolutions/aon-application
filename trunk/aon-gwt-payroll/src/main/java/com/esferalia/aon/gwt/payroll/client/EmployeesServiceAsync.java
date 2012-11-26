package com.esferalia.aon.gwt.payroll.client;

import java.util.List;

import com.esferalia.aon.gwt.payroll.shared.Cost;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>EmployeesService</code>.
 */
public interface EmployeesServiceAsync {
	void getEnterprise(AsyncCallback<Enterprise> callback)
			throws IllegalArgumentException;
	void getSalaries(Employee employee, AsyncCallback<List<Salary>> callback)
			throws IllegalArgumentException;
	void getCostReceiptHTML(Cost cost, int zoom, AsyncCallback<String> callback)
			throws IllegalArgumentException;
	void getSalaryReceiptHTML(Cost cost, int zoom, AsyncCallback<String> callback)
			throws IllegalArgumentException;
	void getSalaryReceiptHTML(Salary salary, int zoom, AsyncCallback<String> callback)
			throws IllegalArgumentException;
	void getSalaryDraftReceiptHTML(SalaryDraft salaryDraft, int zoom, AsyncCallback<String> callback)
			throws IllegalArgumentException;
}
