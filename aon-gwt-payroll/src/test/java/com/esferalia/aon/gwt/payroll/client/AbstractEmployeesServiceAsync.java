package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import com.esferalia.aon.gwt.common.shared.EvalException;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.AgreementDraft;
import com.esferalia.aon.gwt.payroll.shared.Bonus;
import com.esferalia.aon.gwt.payroll.shared.CalendarDraft;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.Cost;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.Events;
import com.esferalia.aon.gwt.payroll.shared.Extra;
import com.esferalia.aon.gwt.payroll.shared.HolidayDraft;
import com.esferalia.aon.gwt.payroll.shared.ITData;
import com.esferalia.aon.gwt.payroll.shared.ITDataPerson;
import com.esferalia.aon.gwt.payroll.shared.Irpf;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Period;
import com.esferalia.aon.gwt.payroll.shared.ReportData;
import com.esferalia.aon.gwt.payroll.shared.Result;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.SalaryPreview;
import com.esferalia.aon.gwt.payroll.shared.Statistics;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.esferalia.aon.gwt.payroll.shared.Salary.Type;
import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class AbstractEmployeesServiceAsync implements EmployeesServiceAsync {
	
	
	private <T> void failure(AsyncCallback<T> callback){
		callback.onFailure(new UnsupportedOperationException());
	}

	@Override
	public void getWorkplaceStats(int workplaceId,
			AsyncCallback<Statistics> callback)
			throws IllegalArgumentException {
		failure(callback);
	}

	@Override
	public void getEnterpriseStats(int enterpriseId,
			AsyncCallback<Statistics> callback)
			throws IllegalArgumentException {
		failure(callback);
		
	}

	@Override
	public void getWorkplaceITData(int workplaceId,
			AsyncCallback<ITData> callback) throws IllegalArgumentException {
		failure(callback);
		
	}

	@Override
	public void getEnterpriseITData(int enterpriseId,
			AsyncCallback<ITData> callback) throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void saveITDataPerson(
			Map<Integer, LinkedHashMap<Integer, ITDataPerson>> inserts,
			Map<Integer, LinkedHashMap<Integer, ITDataPerson>> deletes,
			Map<Integer, LinkedHashMap<Integer, ITDataPerson>> updates,
			AsyncCallback<ITData> callback) throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void getCalendar(int workplaceId, Integer pattern,
			Integer calendar, AsyncCallback<CalendarDraft> callback)
			throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void getHolidayDescription(
			AsyncCallback<Map<Integer, String>> callback)
			throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void saveHolidaysAndDays(int workplaceId, String holidayDescription,
			Integer holidayListBox, Map<Date, String> map, CalendarDraft.DayType dayTypes[],
			AsyncCallback<Void> callback) throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void deletePropertyHoliday(Integer id, Date date,
			AsyncCallback<Void> callback) throws IllegalArgumentException {
		failure(callback);
	}

	@Override
	public void getA3Report(Date month, int[] workplaces,
			AsyncCallback<ReportData> callback)
			throws IllegalArgumentException {
		failure(callback);
	}

	@Override
	public void getFTEReport(Date start, Date end, int[] workplaces,
			AsyncCallback<ReportData> callback)
			throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void getHolidayReport(Date start, Date end, int[] workplaces,
			AsyncCallback<ReportData> callback)
			throws IllegalArgumentException {
		failure(callback);
		
	}

	@Override
	public void getEnterprise(AsyncCallback<Enterprise> callback)
			throws IllegalArgumentException {
		failure(callback);
		
	}

	@Override
	public void getEnterprises(AsyncCallback<Enterprise[]> callback)
			throws IllegalArgumentException {
		failure(callback);
		
	}

	@Override
	public void getAvailablePayments(int employeeId,
			AsyncCallback<List<Payment>> callback)
			throws IllegalArgumentException {
		failure(callback);
		
	}

	@Override
	public void getAvailableDeductions(int employeeId,
			AsyncCallback<List<Deduction>> callback)
			throws IllegalArgumentException {
		failure(callback);
		
	}
	
	@Override
	public void getAvailableBonuses(int employeeId,
			AsyncCallback<List<Bonus>> callback)
			throws IllegalArgumentException {
		failure(callback);
	}

	@Override
	public void getWorkplaceCosts(int workplaceId,
			AsyncCallback<List<Cost>> callback)
			throws IllegalArgumentException {
		failure(callback);
		
	}

	@Override
	public void getEnterpriseCosts(int enterpriseId,
			AsyncCallback<List<Cost>> callback)
			throws IllegalArgumentException {
		failure(callback);
		
	}

	@Override
	public void getSalaries(Employee employee,
			AsyncCallback<List<Salary>> callback)
			throws IllegalArgumentException {
		failure(callback);
		
	}

	@Override
	public void getIrpfs(Employee employee,
			AsyncCallback<List<Irpf>> callback)
			throws IllegalArgumentException {
		failure(callback);
		
	}

	@Override
	public void getExtras(List<Employee> employees,
			AsyncCallback<List<Extra>> callback)
			throws IllegalArgumentException {
		failure(callback);
		
	}

	@Override
	public void getCostReceiptHTML(Cost cost, Type[] types, int zoom,
			AsyncCallback<String> callback) throws IllegalArgumentException {
		failure(callback);
		
	}

	@Override
	public void getIrpfReceiptHTML(Irpf irpf, int zoom,
			AsyncCallback<String> callback) throws IllegalArgumentException {
		failure(callback);
		
	}

	@Override
	public void getSalaryReceiptHTML(Cost cost, Type[] types, int zoom,
			AsyncCallback<String> callback) throws IllegalArgumentException {
		failure(callback);
		
	}

	@Override
	public void getSalaryReceiptHTML(Salary salary, int zoom,
			AsyncCallback<String> callback) throws IllegalArgumentException {
		failure(callback);
		
	}

	@Override
	public void saveSalaryDraft(
			com.esferalia.aon.gwt.payroll.shared.SalaryDraft salaryDraft,
			AsyncCallback<Void> callback) throws IllegalArgumentException {
		failure(callback);
		
	}

	@Override
	public void saveAgreementDraft(AgreementDraft agreementDraft,
			AsyncCallback<AgreementDraft> callback)
			throws IllegalArgumentException {
		failure(callback);
		
	}

	@Override
	public void saveSalary(
			com.esferalia.aon.gwt.payroll.shared.SalaryDraft salaryDraft,
			AsyncCallback<com.esferalia.aon.gwt.payroll.shared.SalaryDraft> callback)
			throws IllegalArgumentException {
		failure(callback);
		
	}

	@Override
	public void calculateIrpf(
			com.esferalia.aon.gwt.payroll.shared.SalaryDraft salaryDraft,
			AsyncCallback<Double> callback) throws IllegalArgumentException {
		failure(callback);
		
	}

	@Override
	public void calculateSalaryDraft(
			com.esferalia.aon.gwt.payroll.shared.SalaryDraft salaryDraft,
			AsyncCallback<com.esferalia.aon.gwt.payroll.shared.SalaryDraft> callback)
			throws IllegalArgumentException {
		failure(callback);
		
	}

	@Override
	public void calculateSalaryDraft4Dummies(
			com.esferalia.aon.gwt.payroll.shared.SalaryDraft salaryDraft,
			AsyncCallback<com.esferalia.aon.gwt.payroll.shared.SalaryDraft> callback)
			throws IllegalArgumentException {
		failure(callback);
		
	}

	@Override
	public void calculateAgreementDraft(AgreementDraft agreementDraft,
			AsyncCallback<AgreementDraft> callback)
			throws IllegalArgumentException {
		failure(callback);
		
	}

	@Override
	public void eval(String expression,
			com.esferalia.aon.gwt.payroll.shared.SalaryDraft salaryDraft,
			AsyncCallback<List<Result>> callback)
			throws IllegalArgumentException, EvalException {
		failure(callback);
		
	}

	@Override
	public void eval(String expression, AgreementDraft agreementDraft,
			int levelId, AsyncCallback<List<Result>> callback)
			throws IllegalArgumentException, EvalException {
		failure(callback);
		
	}

	@Override
	public void getContext(
			com.esferalia.aon.gwt.payroll.shared.SalaryDraft salaryDraft,
			AsyncCallback<ContextDescriptor> callback)
			throws IllegalArgumentException {
		failure(callback);
		
	}

	@Override
	public void getContext(AgreementDraft agreementDraft, int levelId,
			AsyncCallback<ContextDescriptor> callback)
			throws IllegalArgumentException {
		failure(callback);
		
	}

	@Override
	public void getSalaryDraftReceipt(
			com.esferalia.aon.gwt.payroll.shared.SalaryDraft salaryDraft,
			String mime, AsyncCallback<String> callback)
			throws IllegalArgumentException {
		failure(callback);
		
	}

	@Override
	public void getSalaryDraftReceiptHTML(
			com.esferalia.aon.gwt.payroll.shared.SalaryDraft salaryPreview,
			int zoom, AsyncCallback<String> callback)
			throws IllegalArgumentException {
		failure(callback);
		
	}

	@Override
	public void getAgreementDraftReceiptHTML(AgreementDraft agreementDraft,
			int levelId, Type type, int zoom, AsyncCallback<String> callback)
			throws IllegalArgumentException {
		failure(callback);
		
	}

	@Override
	public void getIrpfDraftReceipt(
			com.esferalia.aon.gwt.payroll.shared.SalaryDraft salaryDraft,
			String mime, AsyncCallback<String> callback)
			throws IllegalArgumentException {
		failure(callback);
		
	}

	@Override
	public void getIrpfDraftReceiptHTML(
			com.esferalia.aon.gwt.payroll.shared.SalaryDraft salaryPreview,
			int zoom, AsyncCallback<String> callback)
			throws IllegalArgumentException {
		failure(callback);
		
	}

	@Override
	public void getSalaryPreviewReceiptHTML(SalaryPreview salaryPreview,
			int zoom, AsyncCallback<String> callback)
			throws IllegalArgumentException {
		failure(callback);
		
	}
	
	@Override
	public void getEmployee(int employeeId, AsyncCallback<Employee> callback) throws IllegalArgumentException {
		failure(callback);
	}

	@Override
	public void getEmployees(int workplaceId, Date endDate, String pattern,
			int offset, int limit, AsyncCallback<List<Employee>> callback)
			throws IllegalArgumentException {
		failure(callback);
		
	}

	@Override
	public void getTrashEmployees(int workplaceId,
			AsyncCallback<List<Employee>> callback)
			throws IllegalArgumentException {
		failure(callback);
		
	}

	@Override
	public void saveEvents(Events events, Date startDate, Date endDate,
			AsyncCallback<Void> callback) throws IllegalArgumentException {
		failure(callback);
		
	}

	@Override
	public void getEvents(Integer workplaceId, Date startDate,
			Date endDate, int offset, int limit, String[] names,
			AsyncCallback<Events> callback) throws IllegalArgumentException {
		failure(callback);
		
	}

	@Override
	public void getAvailPeriod(Integer workplaceId, String name,
			AsyncCallback<Period> callback) throws IllegalArgumentException {
		failure(callback);
		
	}

	@Override
	public void getEventsVariables(Integer workplaceId,
			Integer agreementId, Date startDate, Date endDate,
			AsyncCallback<Map<String, String>> callback)
			throws IllegalArgumentException {
		failure(callback);
		
	}
	
	@Override
	public void getVariables(SalaryDraft salaryDraft, Date startDate, Date endDate,
			String[] names, AsyncCallback<List<Variable>> callback)
			throws IllegalArgumentException {
		failure(callback);
	}

	@Override
	public void delete(Salary[] salaries, AsyncCallback<Void> callback)
			throws IllegalArgumentException {
		failure(callback);
		
	}

	@Override
	public void getChanges(Agreement agreement,
			AsyncCallback<SortedSet<Date>> callback)
			throws IllegalArgumentException {
		failure(callback);
		
	}

	@Override
	public void pasteContract(int workplaceId, int contractId,
			String document, Date startDate, Date endDate, boolean check,
			AsyncCallback<Employee> callback)
			throws IllegalArgumentException {
		failure(callback);
		
	}

	@Override
	public void moveContractId(Employee employee,
			AsyncCallback<Void> callback) throws IllegalArgumentException {
		failure(callback);
		
	}

	@Override
	public void deleteContract(Employee employee,
			AsyncCallback<Void> callback) throws IllegalArgumentException {
		failure(callback);
		
	}

	@Override
	public void insertPerson(Employee employee, AsyncCallback<Void> callback)
			throws IllegalArgumentException {
		failure(callback);
		
	}

	@Override
	public void getAvaiableEmployees(
			AsyncCallback<Map<String, String>> callback)
			throws IllegalArgumentException {
		failure(callback);
		
	}
	
}