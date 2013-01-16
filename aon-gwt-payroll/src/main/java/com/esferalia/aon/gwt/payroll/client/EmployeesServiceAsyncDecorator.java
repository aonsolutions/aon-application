/**
 * 
 */
package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.payroll.shared.Cost;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author rtrepiana
 * 
 */
public class EmployeesServiceAsyncDecorator implements EmployeesServiceAsync {

	private static class AsyncCallbackWrapper<T> implements AsyncCallback<T> {

		private AsyncCallback<T> asyncCallback;

		private AsyncCallbackWrapper(AsyncCallback<T> asyncCallback) {
			this.asyncCallback = asyncCallback;
		}

		@Override
		public void onSuccess(T result) {
			asyncCallback.onSuccess(result);
			AON.stop();
		}

		@Override
		public void onFailure(Throwable caught) {
			asyncCallback.onFailure(caught);
			AON.fail();
		}
	}

	private EmployeesServiceAsync employeesServiceAsync;

	public EmployeesServiceAsyncDecorator(
			EmployeesServiceAsync employeesServiceAsync) {
		this.employeesServiceAsync = employeesServiceAsync;
	}

	public void getEnterprise(AsyncCallback<Enterprise> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync
				.getEnterprise(new AsyncCallbackWrapper<Enterprise>(callback));
	}

	public void getWorkplaceCosts(int workplaceId,
			AsyncCallback<List<Cost>> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getWorkplaceCosts(workplaceId,
				new AsyncCallbackWrapper<List<Cost>>(callback));
	}

	public void getEnterpriseCosts(int enterpriseId,
			AsyncCallback<List<Cost>> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getEnterpriseCosts(enterpriseId,
				new AsyncCallbackWrapper<List<Cost>>(callback));
	}

	public void getSalaries(Employee employee,
			AsyncCallback<List<Salary>> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getSalaries(employee,
				new AsyncCallbackWrapper<List<Salary>>(callback));
	}

	public void getCostReceiptHTML(Cost cost, int zoom,
			AsyncCallback<String> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getCostReceiptHTML(cost, zoom,
				new AsyncCallbackWrapper<String>(callback));
	}

	public void getSalaryReceiptHTML(Cost cost, int zoom,
			AsyncCallback<String> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getSalaryReceiptHTML(cost, zoom,
				new AsyncCallbackWrapper<String>(callback));
	}

	public void getSalaryReceiptHTML(Salary salary, int zoom,
			AsyncCallback<String> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getSalaryReceiptHTML(salary, zoom,
				new AsyncCallbackWrapper<String>(callback));
	}

	public void getSalaryDraftReceiptHTML(SalaryDraft salaryDraft, int zoom,
			AsyncCallback<String> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getSalaryDraftReceiptHTML(salaryDraft, zoom,
				new AsyncCallbackWrapper<String>(callback));
	}

	public void getEmployees(int workplaceId, Date endDate, String pattern,
			int offset, int limit, AsyncCallback<List<Employee>> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getEmployees(workplaceId, endDate, pattern,
				offset, limit, new AsyncCallbackWrapper<List<Employee>>(
						callback));
	}


}
