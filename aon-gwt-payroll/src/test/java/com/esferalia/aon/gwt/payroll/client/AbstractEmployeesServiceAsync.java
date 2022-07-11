package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
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
import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarUpdate;
import com.esferalia.aon.gwt.payroll.shared.EmployeeStatus;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.Events;
import com.esferalia.aon.gwt.payroll.shared.Extra;
import com.esferalia.aon.gwt.payroll.shared.ITData;
import com.esferalia.aon.gwt.payroll.shared.ITDataPerson;
import com.esferalia.aon.gwt.payroll.shared.Irpf;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Period;
import com.esferalia.aon.gwt.payroll.shared.Result;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.Salary.Type;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.SalaryPreview;
import com.esferalia.aon.gwt.payroll.shared.Statistics;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.google.gwt.user.client.rpc.AsyncCallback;

public abstract class AbstractEmployeesServiceAsync implements EmployeesServiceAsync {

	private <T> void failure(AsyncCallback<T> callback) {
		callback.onFailure(new UnsupportedOperationException());
	}

	@Override
	public void getEmployeeCalendar(String domain, int contract, AsyncCallback<EmployeeCalendarData> callback) {
		failure(callback);
	}
	
	@Override
	public void setEmployeeCalendar(String domain, int contract, EmployeeCalendarUpdate updateInfo, AsyncCallback<EmployeeCalendarUpdate> callback) {
		failure(callback);
	}

	@Override
	public void getWorkplaceStats(String domain, int workplaceId, AsyncCallback<Statistics> callback) throws IllegalArgumentException {
		failure(callback);
	}

	@Override
	public void getEnterpriseStats(String domain, int enterpriseId, AsyncCallback<Statistics> callback)
			throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void getWorkplaceITData(String domain, int workplaceId, AsyncCallback<ITData> callback) throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void getEnterpriseITData(String domain, int enterpriseId, AsyncCallback<ITData> callback) throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void saveITDataPerson(String domain, Map<Integer, LinkedHashMap<Integer, ITDataPerson>> inserts,
			Map<Integer, LinkedHashMap<Integer, ITDataPerson>> deletes,
			Map<Integer, LinkedHashMap<Integer, ITDataPerson>> updates, AsyncCallback<ITData> callback)
			throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void getCalendar(String domain, int workplaceId, Integer pattern, Integer calendar, AsyncCallback<CalendarDraft> callback)
			throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void getHolidayDescription(String domain, AsyncCallback<Map<Integer, String>> callback) throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void saveHolidaysAndDays(String domain, int workplaceId, String holidayDescription, Integer holidayListBox,
			Map<Date, String> map, CalendarDraft.DayType dayTypes[], AsyncCallback<Void> callback)
			throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void deletePropertyHoliday(String domain, Integer id, Date date, AsyncCallback<Void> callback)
			throws IllegalArgumentException {
		failure(callback);
	}


	@Override
	public void getEnterprise(String domain, String user, AsyncCallback<Enterprise> callback) throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void getEnterprises(String domain, String user, AsyncCallback<Enterprise[]> callback) throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void getAvailablePayments(String domain, int employeeId, AsyncCallback<List<Payment>> callback)
			throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void getAvailableDeductions(String domain, int employeeId, AsyncCallback<List<Deduction>> callback)
			throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void getAvailableBonuses(String domain, int employeeId, AsyncCallback<List<Bonus>> callback)
			throws IllegalArgumentException {
		failure(callback);
	}

	@Override
	public void getWorkplaceCosts(String domain, int workplaceId, AsyncCallback<List<Cost>> callback) throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void getEnterpriseCosts(String domain, int enterpriseId, AsyncCallback<List<Cost>> callback)
			throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void getSalaries(String domain, Employee employee, AsyncCallback<List<Salary>> callback) throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void getIrpfs(String domain, Employee employee, AsyncCallback<List<Irpf>> callback) throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void getExtras(String domain, List<Employee> employees, AsyncCallback<List<Extra>> callback)
			throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void getCostReceiptPDF(String domain, Cost cost, Type[] types, AsyncCallback<String> callback)
			throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void getCostReceiptHTML(String domain, Cost cost, Type[] types, int zoom, AsyncCallback<String> callback)
			throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void getSLDCalcReceiptHTML(String domain, String user,Cost cost, Type[] types, int zoom, AsyncCallback<String> callback)
			throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void getIrpfReceiptHTML(String domain, Irpf irpf, int zoom, AsyncCallback<String> callback)
			throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void getSalaryReceiptHTML(String domain, Cost cost, Type[] types, int zoom, AsyncCallback<String> callback)
			throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void getSalaryReceiptHTML(String domain, Salary salary, int zoom, AsyncCallback<String> callback)
			throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void saveSalaryDraft(String domain, com.esferalia.aon.gwt.payroll.shared.SalaryDraft salaryDraft,
			AsyncCallback<Void> callback) throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void saveAgreementDraft(String domain, AgreementDraft agreementDraft, AsyncCallback<AgreementDraft> callback)
			throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void saveSalary(String domain, String user, com.esferalia.aon.gwt.payroll.shared.SalaryDraft salaryDraft,
			AsyncCallback<com.esferalia.aon.gwt.payroll.shared.SalaryDraft> callback) throws IllegalArgumentException {
		failure(callback);
	}

	@Override
	public void saveSalary(String domain, com.esferalia.aon.gwt.payroll.shared.SalaryDraft salaryDraft, Date sections [],
			AsyncCallback<com.esferalia.aon.gwt.payroll.shared.SalaryDraft> callback) throws IllegalArgumentException {
		failure(callback);
	}

	@Override
	public void calculateIrpf(String domain, com.esferalia.aon.gwt.payroll.shared.SalaryDraft salaryDraft,
			AsyncCallback<Double> callback) throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void calculateSalaryDraft(String domain, com.esferalia.aon.gwt.payroll.shared.SalaryDraft salaryDraft,
			AsyncCallback<com.esferalia.aon.gwt.payroll.shared.SalaryDraft> callback) throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void calculateSalaryDraft(String domain, com.esferalia.aon.gwt.payroll.shared.SalaryDraft salaryDraft, Date sections [],
			AsyncCallback<com.esferalia.aon.gwt.payroll.shared.SalaryDraft> callback) throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void syncSalaryDraft(String domain, String user,
			com.esferalia.aon.gwt.payroll.shared.SalaryDraft salaryDraft, AsyncCallback<com.esferalia.aon.gwt.payroll.shared.SalaryDraft> callback) throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void calculateAgreementDraft(String domain,AgreementDraft agreementDraft, AsyncCallback<AgreementDraft> callback)
			throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void eval(String domain, String expression, com.esferalia.aon.gwt.payroll.shared.SalaryDraft salaryDraft,
			AsyncCallback<List<Result>> callback) throws IllegalArgumentException, EvalException {
		failure(callback);

	}

	@Override
	public void eval(String domain, String expression, AgreementDraft agreementDraft, int levelId,
			AsyncCallback<List<Result>> callback) throws IllegalArgumentException, EvalException {
		failure(callback);

	}

	@Override
	public void getContext(String domain, com.esferalia.aon.gwt.payroll.shared.SalaryDraft salaryDraft,
			AsyncCallback<ContextDescriptor> callback) throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void getContext(String domain, AgreementDraft agreementDraft, int levelId, AsyncCallback<ContextDescriptor> callback)
			throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void getSalaryDraftReceipt(String domain, com.esferalia.aon.gwt.payroll.shared.SalaryDraft salaryDraft, String mime,
			AsyncCallback<String> callback) throws IllegalArgumentException {
		failure(callback);

	}
	
	@Override
	public void getSettleDraftReceipt(String domain, com.esferalia.aon.gwt.payroll.shared.SalaryDraft salaryDraft, String mime,
			AsyncCallback<String> callback) throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void getAgreementDraftReceipt(String domain, AgreementDraft agreementDraft, List<Variable> context, int levelId, Type type, String mime,
			AsyncCallback<String> callback) throws IllegalArgumentException {
		failure(callback);
	}


	@Override
	public void getSalaryDraftReceiptHTML(String domain, com.esferalia.aon.gwt.payroll.shared.SalaryDraft salaryPreview, int zoom,
			AsyncCallback<String> callback) throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void getAgreementDraftReceiptHTML(String domain, AgreementDraft agreementDraft, int levelId, Type type, int zoom,
			AsyncCallback<String> callback) throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void getIrpfDraftReceipt(String domain, com.esferalia.aon.gwt.payroll.shared.SalaryDraft salaryDraft, String mime,
			AsyncCallback<String> callback) throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void getIrpfDraftReceiptHTML(String domain, com.esferalia.aon.gwt.payroll.shared.SalaryDraft salaryPreview, int zoom,
			AsyncCallback<String> callback) throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void getSalaryPreviewReceiptHTML(String domain, SalaryPreview salaryPreview, int zoom, AsyncCallback<String> callback)
			throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void getEmployee(String domain, int employeeId, AsyncCallback<Employee> callback) throws IllegalArgumentException {
		failure(callback);
	}

	@Override
	public void getEmployees(String domain, int workplaceId, Date endDate, String pattern, int offset, int limit,
			AsyncCallback<List<Employee>> callback) throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void getTrashEmployees(String domain, int workplaceId, AsyncCallback<List<Employee>> callback)
			throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void saveEvents(String domain, Events events, Date startDate, Date endDate, AsyncCallback<Void> callback)
			throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void getEvents(String domain, Integer workplaceId, Date startDate, Date endDate, int offset, int limit, String[] names,
			AsyncCallback<Events> callback) throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void getAvailPeriod(String domain, Integer workplaceId, String name, AsyncCallback<Period> callback)
			throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void getEventsVariables(String domain, Integer workplaceId, Integer agreementId, Date startDate, Date endDate,
			AsyncCallback<Map<String, String>> callback) throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void getVariables(String domain, SalaryDraft salaryDraft, Date startDate, Date endDate, String[] names,
			AsyncCallback<List<Variable>> callback) throws IllegalArgumentException {
		failure(callback);
	}

	@Override
	public void delete(String domain, Salary[] salaries, AsyncCallback<Void> callback) throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void getChanges(String domain, Agreement agreement, AsyncCallback<SortedSet<Date>> callback)
			throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void pasteContract(String domain, int workplaceId, int contractId, String document, Date startDate, Date endDate,
			boolean check, AsyncCallback<Employee> callback) throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void moveContractId(String domain, Employee employee, AsyncCallback<Void> callback) throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void deleteContract(String domain, Employee employee, AsyncCallback<Void> callback) throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void insertPerson(String domain, Employee employee, AsyncCallback<Void> callback) throws IllegalArgumentException {
		failure(callback);

	}

	@Override
	public void getAvaiableEmployees(String domain, AsyncCallback<Map<String, String>> callback) throws IllegalArgumentException {
		failure(callback);

	}
	
	@Override
	public void getEmployeeTa(String domain, String user, Integer contractId, String situation, String regimen, String ctaCti, String nss, Date fecha, AsyncCallback<String> callback) {
		failure(callback);		
	}
	
	@Override
	public void getEmployeeIdc(String domain, String user, Integer contractId, Date date,
			AsyncCallback<String> callback) {
		failure(callback);		
	}
	
	@Override
	public void checkEmployeeIdc(String domain, String user, Integer contractId, Date date, String idc,
			AsyncCallback<String> callback) throws IllegalArgumentException {
		failure(callback);		
	}

	@Override
	public void getEmployeeIdcPlNss(String domain, String user, Integer contractId, Date date,
			AsyncCallback<String> callback) {
		failure(callback);		
	}
	
	@Override
	public void getEmployeeIdcDates(String domain, String user, Integer contractId, Date date,
			AsyncCallback<List<Date>> callback) {
		failure(callback);		
	}

	@Override
	public void getEmployeeStatus(String domain, String user, Integer contractId, AsyncCallback<EmployeeStatus> callback) {
		failure(callback);				
	}
	
	@Override
	public void setData(String currentDomainName, String user, Integer contractId, ArrayList<Variable> data,
			AsyncCallback<Void> callback) {
		failure(callback);
	}
}