package com.esferalia.aon.gwt.employee.client;

import java.util.List;

import com.esferalia.aon.gwt.employee.shared.Employee;
import com.esferalia.aon.gwt.employee.shared.Enterprise;
import com.esferalia.aon.gwt.employee.shared.Salary;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>EmployeesService</code>.
 */
public interface EmployeesServiceAsync {
	void getEnterprise(AsyncCallback<Enterprise> callback)
			throws IllegalArgumentException;
	void getSalaries(Employee employee, AsyncCallback<List<Salary>> callback)
			throws IllegalArgumentException;
	void getSalaryReceiptHTML(Salary salary, AsyncCallback<String> callback)
			throws IllegalArgumentException;
}
