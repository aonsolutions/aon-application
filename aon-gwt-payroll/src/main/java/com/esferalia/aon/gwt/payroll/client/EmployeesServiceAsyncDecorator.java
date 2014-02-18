/**
 * 
 */
package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.AgreementDraft;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.Cost;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.EvalException;
import com.esferalia.aon.gwt.payroll.shared.Events;
import com.esferalia.aon.gwt.payroll.shared.Extra;
import com.esferalia.aon.gwt.payroll.shared.Irpf;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Period;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.Statistics;
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
	
	@Override
	public void getWorkplaceStats(int workplaceId,
			AsyncCallback<Statistics> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getWorkplaceStats(workplaceId, new AsyncCallbackWrapper<Statistics>(callback));
	}
	
	@Override
	public void getEnterpriseStats(int enterpriseId,
			AsyncCallback<Statistics> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getEnterpriseStats(enterpriseId, new AsyncCallbackWrapper<Statistics>(callback));
	}
	
	public void getSalaries(Employee employee,
			AsyncCallback<List<Salary>> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getSalaries(employee,
				new AsyncCallbackWrapper<List<Salary>>(callback));
	}

	@Override
	public void getIrpfs(Employee employee, AsyncCallback<List<Irpf>> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getIrpfs(employee,
				new AsyncCallbackWrapper<List<Irpf>>(callback));
	}

	@Override
	public void getExtras(Employee employee, AsyncCallback<List<Extra>> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getExtras(employee,
				new AsyncCallbackWrapper<List<Extra>>(callback));
	}

	@Override
	public void getIrpfReceiptHTML(Irpf irpf, int zoom,
			AsyncCallback<String> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getIrpfReceiptHTML(irpf, zoom,
				new AsyncCallbackWrapper<String>(callback));
	}

	public void getCostReceiptHTML(Cost cost, Salary.Type types[], int zoom,
			AsyncCallback<String> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getCostReceiptHTML(cost, types, zoom,
				new AsyncCallbackWrapper<String>(callback));
	}

	public void getSalaryReceiptHTML(Cost cost, Salary.Type types[], int zoom,
			AsyncCallback<String> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getSalaryReceiptHTML(cost, types, zoom,
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
	public void saveAgreementDraft(AgreementDraft agreementDraft,
			AsyncCallback<AgreementDraft> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.saveAgreementDraft(agreementDraft,
				new AsyncCallbackWrapper<AgreementDraft>(callback));
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
	public void calculateIrpf(SalaryDraft salaryDraft,
			AsyncCallback<Double> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.calculateIrpf(salaryDraft,
				new AsyncCallbackWrapper<Double>(callback));
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
	public void getAgreementDraftReceiptHTML(AgreementDraft agreementDraft,
			int levelId, Salary.Type type, int zoom,
			AsyncCallback<String> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync
				.getAgreementDraftReceiptHTML(agreementDraft, levelId, type,
						zoom, new AsyncCallbackWrapper<String>(callback));
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
			AsyncCallback<Period> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getAvailPeriod(workplaceId, name,
				new AsyncCallbackWrapper<Period>(callback));
	}

	@Override
	public void getEventsVariables(Integer workplaceId, Integer agreementId,
			Date startDate, Date endDate,
			AsyncCallback<Map<String, String>> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getEventsVariables(workplaceId, agreementId,
				startDate, endDate,
				new AsyncCallbackWrapper<Map<String, String>>(callback));

	}

	@Override
	public void delete(Salary[] salaries, AsyncCallback<Void> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.delete(salaries, new AsyncCallbackWrapper<Void>(
				callback));
	}

	@Override
	public void getChanges(Agreement agreement,
			AsyncCallback<SortedSet<Date>> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getChanges(agreement,
				new AsyncCallbackWrapper<SortedSet<Date>>(callback));
	}
}
