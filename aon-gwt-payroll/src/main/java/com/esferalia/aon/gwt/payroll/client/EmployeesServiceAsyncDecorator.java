/**
 * 
 */
package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.payroll.shared.AgreementDraft;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.Cost;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.EvalException;
import com.esferalia.aon.gwt.payroll.shared.Events;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Period;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.SalaryPreview;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author rtrepiana
 * 
 */
public class EmployeesServiceAsyncDecorator implements EmployeesServiceAsync {


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

	@Override
	public void getEnterprises(AsyncCallback<Enterprise[]> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync
				.getEnterprises(new AsyncCallbackWrapper<Enterprise[]>(callback));
	}

	@Override
	public void getAvailablePayments(int employeeId,
			AsyncCallback<List<Payment>> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getAvailablePayments(employeeId,
				new AsyncCallbackWrapper<List<Payment>>(callback));
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

	public void getSalaryPreviewReceiptHTML(SalaryPreview salaryPreview,
			int zoom, AsyncCallback<String> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getSalaryPreviewReceiptHTML(salaryPreview, zoom,
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

	@Override
	public void saveSalaryDraft(SalaryDraft salaryDraft,
			AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.saveSalaryDraft(salaryDraft,
				new AsyncCallbackWrapper<Void>(callback));

	}

	@Override
	public void saveSalary(SalaryDraft salaryDraft,
			AsyncCallback<SalaryDraft> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.saveSalary(salaryDraft,
				new AsyncCallbackWrapper<SalaryDraft>(callback));
	}

	@Override
	public void eval(String expression, SalaryDraft salaryDraft,
			AsyncCallback<Double> callback) throws IllegalArgumentException,
			EvalException {
		AON.start();
		employeesServiceAsync.eval(expression, salaryDraft,
				new AsyncCallbackWrapper<Double>(callback));

	}

	@Override
	public void getContext(SalaryDraft salaryDraft,
			AsyncCallback<ContextDescriptor> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getContext(salaryDraft,
				new AsyncCallbackWrapper<ContextDescriptor>(callback));
	}

	@Override
	public void calculateSalaryDraft(SalaryDraft salaryDraft,
			AsyncCallback<SalaryDraft> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.calculateSalaryDraft(salaryDraft,
				new AsyncCallbackWrapper<SalaryDraft>(callback));
	}

	@Override
	public void calculateAgreementDraft(AgreementDraft agreementDraft,
			AsyncCallback<AgreementDraft> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.calculateAgreementDraft(agreementDraft,
				new AsyncCallbackWrapper<AgreementDraft>(callback));
	}

	@Override
	public void getSalaryDraftReceiptHTML(SalaryDraft salaryPreview, int zoom,
			AsyncCallback<String> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getSalaryDraftReceiptHTML(salaryPreview, zoom,
				new AsyncCallbackWrapper<String>(callback));
	}

	@Override
	public void getSalaryDraftReceipt(SalaryDraft salaryDraft, String mime,
			AsyncCallback<String> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getSalaryDraftReceipt(salaryDraft, mime,
				new AsyncCallbackWrapper<String>(callback));
	}
	
	@Override
	public void getIrpfDraftReceipt(SalaryDraft salaryDraft, String mime,
			AsyncCallback<String> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getIrpfDraftReceipt(salaryDraft, mime,
				new AsyncCallbackWrapper<String>(callback));
		
	}
	
	@Override
	public void getIrpfDraftReceiptHTML(SalaryDraft salaryPreview, int zoom,
			AsyncCallback<String> callback) throws IllegalArgumentException {
		
		AON.start();
		employeesServiceAsync.getIrpfDraftReceiptHTML(salaryPreview, zoom,
				new AsyncCallbackWrapper<String>(callback));
	}

	@Override
	public void saveEvents(Events events, Date startDate, Date endDate,
			AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.saveEvents(events, startDate, endDate,
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void getEvents(Integer workplaceId, Date startDate, Date endDate,
			int offset, int limit, String names[],
			AsyncCallback<Events> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getEvents(workplaceId, startDate, endDate,
				offset, limit, names,
				new AsyncCallbackWrapper<Events>(callback));
	}

	@Override
	public void getAvailPeriod(Integer workplaceId, String name,
			AsyncCallback<Period> callback) throws IllegalArgumentException
			{
		AON.start();
		employeesServiceAsync.getAvailPeriod(workplaceId, name,
				new AsyncCallbackWrapper<Period>(callback));
	}
}
