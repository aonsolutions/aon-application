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
import com.esferalia.aon.gwt.payroll.shared.CalendarDraft.DayType;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.Cost;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarUpdate;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsUpdate;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.Events;
import com.esferalia.aon.gwt.payroll.shared.EventsWorkplace;
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
import com.esferalia.aon.gwt.payroll.shared.SalaryInfo;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfoFilter;
import com.esferalia.aon.gwt.payroll.shared.SalaryPreview;
import com.esferalia.aon.gwt.payroll.shared.Statistics;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceEmployees;
import com.google.gwt.core.shared.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class DomainEmployeesServiceAsync {
	
	private EmployeesServiceAsync employeesServiceAsync;

	
	public static DomainEmployeesServiceAsync newInstance() {
		EmployeesServiceAsync employeesServiceAsync = 
		GWT.create(EmployeesService.class);
		EmployeesServiceAsyncDecorator employeesServiceAsyncDecorator = 
		new EmployeesServiceAsyncDecorator(employeesServiceAsync);
		return new DomainEmployeesServiceAsync(employeesServiceAsyncDecorator);
	}
	
	public static DomainEmployeesServiceAsync newInstance(EmployeesServiceAsync employeesServiceAsync) {
		return new DomainEmployeesServiceAsync(employeesServiceAsync);
	}

	private DomainEmployeesServiceAsync(EmployeesServiceAsync employeesServiceAsync) {
		this.employeesServiceAsync = employeesServiceAsync;
	}

	public void getWorkplaceStats(int workplaceId, AsyncCallback<Statistics> callback) throws IllegalArgumentException {
		employeesServiceAsync.getWorkplaceStats(getCurrentDomainName(), workplaceId, callback);
	}

	public void getEmployeeCalendar(int contract, AsyncCallback<EmployeeCalendarData> callback) {
		employeesServiceAsync.getEmployeeCalendar(getCurrentDomainName(), contract, callback);
	}

	public void setEmployeeEvents(int contract, EmployeeEventsUpdate updateInfo,
			AsyncCallback<EmployeeEventsUpdate> callback) {
		employeesServiceAsync.setEmployeeEvents(getCurrentDomainName(), contract, updateInfo, callback);
	}
	
	public void resetCalendar(Integer employeeId, AsyncCallback<String> callback) {
		employeesServiceAsync.resetCalendar(getCurrentDomainName(), employeeId, callback);
	}

	public void setEmployeeCalendar(int contract, EmployeeCalendarUpdate updateInfo,
			AsyncCallback<EmployeeCalendarUpdate> callback) {
		employeesServiceAsync.setEmployeeCalendar(getCurrentDomainName(), contract, updateInfo, callback);
	}

	public void getEnterpriseStats(int enterpriseId, AsyncCallback<Statistics> callback)
			throws IllegalArgumentException {
		employeesServiceAsync.getEnterpriseStats(getCurrentDomainName(), enterpriseId, callback);
	}

	public void getCalendar(int workplaceId, Integer pattern, Integer calendar,
			AsyncCallback<CalendarDraft> callback) throws IllegalArgumentException {
		employeesServiceAsync.getCalendar(getCurrentDomainName(), workplaceId, pattern, calendar, callback);
	}

	public void getWorkplaceITData(int workplaceId, AsyncCallback<ITData> callback) throws IllegalArgumentException {
		employeesServiceAsync.getWorkplaceITData(getCurrentDomainName(), workplaceId, callback);
	}

	public void eval(String domain, String expression, AgreementDraft agreementDraft, int levelId,
			AsyncCallback<List<Result>> callback) throws IllegalArgumentException, EvalException {
		employeesServiceAsync.eval(domain, expression, agreementDraft, levelId, callback);
	}

	public void getEnterpriseITData(int enterpriseId, AsyncCallback<ITData> callback) throws IllegalArgumentException {
		employeesServiceAsync.getEnterpriseITData(getCurrentDomainName(), enterpriseId, callback);
	}

	public void getHolidayDescription(AsyncCallback<Map<Integer, String>> callback)
			throws IllegalArgumentException {
		employeesServiceAsync.getHolidayDescription(getCurrentDomainName(), callback);
	}

	public void getAvailablePayments(int employeeId, AsyncCallback<List<Payment>> callback)
			throws IllegalArgumentException {
		employeesServiceAsync.getAvailablePayments(getCurrentDomainName(), employeeId, callback);
	}

	public void saveITDataPerson(Map<Integer, LinkedHashMap<Integer, ITDataPerson>> inserts,
			Map<Integer, LinkedHashMap<Integer, ITDataPerson>> deletes,
			Map<Integer, LinkedHashMap<Integer, ITDataPerson>> updates, AsyncCallback<ITData> callback)
			throws IllegalArgumentException {
		employeesServiceAsync.saveITDataPerson(getCurrentDomainName(), inserts, deletes, updates, callback);
	}

	public void saveHolidaysAndDays(int workplaceId, String holidayDescription, Integer holidayListBox,
			Map<Date, String> map, DayType[] dayTypes, AsyncCallback<Void> callback) throws IllegalArgumentException {
		employeesServiceAsync.saveHolidaysAndDays(getCurrentDomainName(), workplaceId, holidayDescription, holidayListBox, map,
				dayTypes, callback);
	}

	public void getContext(String domain, AgreementDraft agreementDraft, int levelId,
			AsyncCallback<ContextDescriptor> callback) throws IllegalArgumentException {
		employeesServiceAsync.getContext(domain, agreementDraft, levelId, callback);
	}

	public void deletePropertyHoliday(Integer id, Date date, AsyncCallback<Void> callback)
			throws IllegalArgumentException {
		employeesServiceAsync.deletePropertyHoliday(getCurrentDomainName(), id, date, callback);
	}

	public void getAgreementDraftReceiptHTML(AgreementDraft agreementDraft, int levelId, Type type,
			int zoom, AsyncCallback<String> callback) throws IllegalArgumentException {
		employeesServiceAsync.getAgreementDraftReceiptHTML(getCurrentDomainName(), agreementDraft, levelId, type, zoom, callback);
	}

	public void saveAgreementDraft(AgreementDraft agreementDraft, AsyncCallback<AgreementDraft> callback)
			throws IllegalArgumentException {
		employeesServiceAsync.saveAgreementDraft(getCurrentDomainName(), agreementDraft, callback);
	}

	public void calculateAgreementDraft(AgreementDraft agreementDraft,
			AsyncCallback<AgreementDraft> callback) throws IllegalArgumentException {
		employeesServiceAsync.calculateAgreementDraft(getCurrentDomainName(), agreementDraft, callback);
	}

	public void getEnterprise(AsyncCallback<Enterprise> callback) throws IllegalArgumentException {
		employeesServiceAsync.getEnterprise(getCurrentDomainName(), getCurrentUser(), callback);
	}

	public void getEnterprises(AsyncCallback<Enterprise[]> callback) throws IllegalArgumentException {
		employeesServiceAsync.getEnterprises(getCurrentDomainName(), getCurrentUser(), callback);
	}

	public void getChanges(String domain, Agreement agreement, AsyncCallback<SortedSet<Date>> callback)
			throws IllegalArgumentException {
		employeesServiceAsync.getChanges(domain, agreement, callback);
	}

	public void getAvailableDeductions(int employeeId, AsyncCallback<List<Deduction>> callback)
			throws IllegalArgumentException {
		employeesServiceAsync.getAvailableDeductions(getCurrentDomainName(), employeeId, callback);
	}

	public void getAvailableBonuses(int employeeId, AsyncCallback<List<Bonus>> callback)
			throws IllegalArgumentException {
		employeesServiceAsync.getAvailableBonuses(getCurrentDomainName(), employeeId, callback);
	}

	public void getWorkplaceCosts(int workplaceId, AsyncCallback<List<Cost>> callback) throws IllegalArgumentException {
		employeesServiceAsync.getWorkplaceCosts(getCurrentDomainName(), workplaceId, callback);
	}

	public void getEnterpriseCosts(int enterpriseId, AsyncCallback<List<Cost>> callback)
			throws IllegalArgumentException {
		employeesServiceAsync.getEnterpriseCosts(getCurrentDomainName(), enterpriseId, callback);
	}

	public void getSalaries(Employee employee, AsyncCallback<List<Salary>> callback) throws IllegalArgumentException {
		employeesServiceAsync.getSalaries(getCurrentDomainName(), employee, callback);
	}

	public void getIrpfs(Employee employee, AsyncCallback<List<Irpf>> callback) throws IllegalArgumentException {
		employeesServiceAsync.getIrpfs(getCurrentDomainName(), employee, callback);
	}

	public void getExtras(List<Employee> employees, AsyncCallback<List<Extra>> callback)
			throws IllegalArgumentException {
		employeesServiceAsync.getExtras(getCurrentDomainName(), employees, callback);
	}

	public void getCostReceiptHTML(Cost cost, Type[] types, int zoom, AsyncCallback<String> callback)
			throws IllegalArgumentException {
		employeesServiceAsync.getCostReceiptHTML(getCurrentDomainName(), cost, types, zoom, callback);
	}

	public void getIrpfReceiptHTML(Irpf irpf, int zoom, AsyncCallback<String> callback)
			throws IllegalArgumentException {
		employeesServiceAsync.getIrpfReceiptHTML(getCurrentDomainName(),  irpf, zoom, callback);
	}

	public void getSalaryReceiptHTML(Cost cost, Type[] types, int zoom, AsyncCallback<String> callback)
			throws IllegalArgumentException {
		employeesServiceAsync.getSalaryReceiptHTML(getCurrentDomainName(),  cost, types, zoom, callback);
	}

	public void getSalaryReceiptHTML(Salary salary, int zoom, AsyncCallback<String> callback)
			throws IllegalArgumentException {
		employeesServiceAsync.getSalaryReceiptHTML(getCurrentDomainName(),  salary, zoom, callback);
	}

	public void saveSalaryDraft(SalaryDraft salaryDraft, AsyncCallback<Void> callback) throws IllegalArgumentException {
		employeesServiceAsync.saveSalaryDraft(getCurrentDomainName(),  salaryDraft, callback);
	}

	public void saveSalary(SalaryDraft salaryDraft, AsyncCallback<SalaryDraft> callback)
			throws IllegalArgumentException {
		employeesServiceAsync.saveSalary(getCurrentDomainName(),  salaryDraft, callback);
	}

	public void saveSalary(SalaryDraft salaryDraft, Date[] sections, AsyncCallback<SalaryDraft> callback)
			throws IllegalArgumentException {
		employeesServiceAsync.saveSalary(getCurrentDomainName(),  salaryDraft, sections, callback);
	}

	public void calculateIrpf(SalaryDraft salaryDraft, AsyncCallback<Double> callback) throws IllegalArgumentException {
		employeesServiceAsync.calculateIrpf(getCurrentDomainName(),  salaryDraft, callback);
	}

	public void calculateSalaryDraft(SalaryDraft salaryDraft, AsyncCallback<SalaryDraft> callback)
			throws IllegalArgumentException {
		employeesServiceAsync.calculateSalaryDraft(getCurrentDomainName(),  salaryDraft, callback);
	}

	public void calculateSalaryDraft(SalaryDraft salaryDraft, Date[] sections, AsyncCallback<SalaryDraft> callback)
			throws IllegalArgumentException {
		employeesServiceAsync.calculateSalaryDraft(getCurrentDomainName(),  salaryDraft, sections, callback);
	}

	public void calculateSalaryDraft4Dummies(SalaryDraft salaryDraft, AsyncCallback<SalaryDraft> callback)
			throws IllegalArgumentException {
		employeesServiceAsync.calculateSalaryDraft4Dummies(getCurrentDomainName(),  salaryDraft, callback);
	}

	public void eval(String expression, SalaryDraft salaryDraft, AsyncCallback<List<Result>> callback)
			throws IllegalArgumentException, EvalException {
		employeesServiceAsync.eval(getCurrentDomainName(),  expression, salaryDraft, callback);
	}

	public void getContext(SalaryDraft salaryDraft, AsyncCallback<ContextDescriptor> callback)
			throws IllegalArgumentException {
		employeesServiceAsync.getContext(getCurrentDomainName(),  salaryDraft, callback);
	}

	public void getSalaryDraftReceipt(SalaryDraft salaryDraft, String mime, AsyncCallback<String> callback)
			throws IllegalArgumentException {
		employeesServiceAsync.getSalaryDraftReceipt(getCurrentDomainName(),  salaryDraft, mime, callback);
	}

	public void getSalaryDraftReceiptHTML(SalaryDraft salaryPreview, int zoom, AsyncCallback<String> callback)
			throws IllegalArgumentException {
		employeesServiceAsync.getSalaryDraftReceiptHTML(getCurrentDomainName(),  salaryPreview, zoom, callback);
	}

	public void getIrpfDraftReceipt(SalaryDraft salaryDraft, String mime, AsyncCallback<String> callback)
			throws IllegalArgumentException {
		employeesServiceAsync.getIrpfDraftReceipt(getCurrentDomainName(),  salaryDraft, mime, callback);
	}

	public void getIrpfDraftReceiptHTML(SalaryDraft salaryPreview, int zoom, AsyncCallback<String> callback)
			throws IllegalArgumentException {
		employeesServiceAsync.getIrpfDraftReceiptHTML(getCurrentDomainName(),  salaryPreview, zoom, callback);
	}

	public void getSalaryPreviewReceiptHTML(SalaryPreview salaryPreview, int zoom, AsyncCallback<String> callback)
			throws IllegalArgumentException {
		employeesServiceAsync.getSalaryPreviewReceiptHTML(getCurrentDomainName(),  salaryPreview, zoom, callback);
	}

	public void getEmployee(int employeeId, AsyncCallback<Employee> callback) throws IllegalArgumentException {
		employeesServiceAsync.getEmployee(getCurrentDomainName(),  employeeId, callback);
	}

	public void getEmployees(int workplaceId, Date endDate, String pattern, int offset, int limit,
			AsyncCallback<List<Employee>> callback) throws IllegalArgumentException {
		employeesServiceAsync.getEmployees(getCurrentDomainName(),  workplaceId, endDate, pattern, offset, limit, callback);
	}

	public void getTrashEmployees(int workplaceId, AsyncCallback<List<Employee>> callback)
			throws IllegalArgumentException {
		employeesServiceAsync.getTrashEmployees(getCurrentDomainName(),  workplaceId, callback);
	}

	public void saveEvents(Events events, Date startDate, Date endDate, AsyncCallback<Void> callback)
			throws IllegalArgumentException {
		employeesServiceAsync.saveEvents(getCurrentDomainName(),  events, startDate, endDate, callback);
	}

	public void getEvents(Integer workplaceId, Date startDate, Date endDate, int offset, int limit, String[] names,
			AsyncCallback<Events> callback) throws IllegalArgumentException {
		employeesServiceAsync.getEvents(getCurrentDomainName(),  workplaceId, startDate, endDate, offset, limit, names, callback);
	}

	public void getAvailPeriod(Integer workplaceId, String name, AsyncCallback<Period> callback)
			throws IllegalArgumentException {
		employeesServiceAsync.getAvailPeriod(getCurrentDomainName(),  workplaceId, name, callback);
	}

	public void getEventsVariables(Integer workplaceId, Integer agreementId, Date startDate, Date endDate,
			AsyncCallback<Map<String, String>> callback) throws IllegalArgumentException {
		employeesServiceAsync.getEventsVariables(getCurrentDomainName(),  workplaceId, agreementId, startDate, endDate, callback);
	}
	
	public void getWorkplaceEventsVariables(Integer workplaceId, Integer agreementId, Date startDate, Date endDate,
			AsyncCallback<Map<String, String>> callback) {
		employeesServiceAsync.getWorkplaceEventsVariables(getCurrentDomainName(),  workplaceId, agreementId, startDate, endDate, callback);
	}

	public void getEmployeeEventsVariables(Integer employeeId, Date startDate, Date endDate,
			AsyncCallback<ContextDescriptor> callback) {
		employeesServiceAsync.getEmployeeEventsVariables(getCurrentDomainName(),  employeeId, startDate, endDate, callback);
	}

	public void getVariables(SalaryDraft salaryDraft, Date startDate, Date endDate, String[] names,
			AsyncCallback<List<Variable>> callback) throws IllegalArgumentException {
		employeesServiceAsync.getVariables(getCurrentDomainName(),  salaryDraft, startDate, endDate, names, callback);
	}

	public void delete(Salary[] salaries, AsyncCallback<Void> callback) throws IllegalArgumentException {
		employeesServiceAsync.delete(getCurrentDomainName(),  salaries, callback);
	}

	public void pasteContract(int workplaceId, int contractId, String document, Date startDate, Date endDate,
			boolean check, AsyncCallback<Employee> callback) throws IllegalArgumentException {
		employeesServiceAsync.pasteContract(getCurrentDomainName(),  workplaceId, contractId, document, startDate, endDate, check, callback);
	}

	public void moveContractId(Employee employee, AsyncCallback<Void> callback) throws IllegalArgumentException {
		employeesServiceAsync.moveContractId(getCurrentDomainName(),  employee, callback);
	}

	public void deleteContract(Employee employee, AsyncCallback<Void> callback) throws IllegalArgumentException {
		employeesServiceAsync.deleteContract(getCurrentDomainName(),  employee, callback);
	}

	public void insertPerson(Employee employee, AsyncCallback<Void> callback) throws IllegalArgumentException {
		employeesServiceAsync.insertPerson(getCurrentDomainName(),  employee, callback);
	}

	public void getAvaiableEmployees(AsyncCallback<Map<String, String>> callback) throws IllegalArgumentException {
		employeesServiceAsync.getAvaiableEmployees(getCurrentDomainName(),  callback);
	}

	public void getWorkplaceEmployees(Integer workplaceId, AsyncCallback<WorkplaceEmployees> asyncCallback) {
		employeesServiceAsync.getWorkplaceEmployees(getCurrentDomainName(),  workplaceId, asyncCallback);
	}

	public void setEventsWorkplace(EventsWorkplace updateEventsWorkplace,
			AsyncCallback<EventsWorkplace> asyncCallback) {
		employeesServiceAsync.setEventsWorkplace(getCurrentDomainName(),  updateEventsWorkplace, asyncCallback);
	}

	public void getEmployeeInfoDataBase(Integer employeeContract, AsyncCallback<EmployeeContractInfo> asyncCallback) {
		employeesServiceAsync.getEmployeeInfoDataBase(getCurrentDomainName(),  employeeContract, asyncCallback);
	}

	public void setEmployeeInfoDataBase(EmployeeContractInfo new_employeeContractData,
			AsyncCallback<EmployeeContractInfo> asyncCallback) {
		employeesServiceAsync.setEmployeeInfoDataBase(getCurrentDomainName(),  new_employeeContractData, asyncCallback);
	}

	public void createEmployeeContract(EmployeeContractInfo employeeContractData,
			AsyncCallback<EmployeeContractInfo> asyncCallback) {
		employeesServiceAsync.createEmployeeContract(getCurrentDomainName(),  employeeContractData, asyncCallback);
	}
	
	public void getEmployeeEvents(int contract, ArrayList<String> employeeContractVariables,
			AsyncCallback<EmployeeEventsData> callback) {
		employeesServiceAsync.getEmployeeEvents(getCurrentDomainName(), contract, employeeContractVariables, callback);
	}
	
	public void getWorkplaceEmployeesEvents(Integer workplaceId, AsyncCallback<WorkplaceEmployees> callback) {
		employeesServiceAsync.getWorkplaceEmployeesEvents(getCurrentDomainName(), workplaceId, callback);
	}
	
	public void getEmployeeEventsByContract(Integer contractId, ArrayList<String> employeeContractVariablesDB,
			AsyncCallback<EmployeeEventsData> callback) {
		employeesServiceAsync.getEmployeeEventsByContract(getCurrentDomainName(), contractId, employeeContractVariablesDB, callback);
		
	}
	
	public void setEmployeeAFIChanges(Integer contractId, Date newDate, boolean isChangeContract, String tc2, boolean isQuoteContract, Integer quoteGroup,
			boolean isOcupationContract, String ocupation, AsyncCallback<String> callback) {
		
		employeesServiceAsync.setEmployeeAFIChanges(getCurrentDomainName(), contractId,
				newDate, isChangeContract, tc2, isQuoteContract, quoteGroup, isOcupationContract, ocupation, callback);
		
	}
	
	public void getEmployeeSalaries(Integer employeeId, AsyncCallback<List<SalaryInfo>> callback) {
		employeesServiceAsync.getEmployeeSalaries(getCurrentDomainName(), employeeId, callback);
	}
	
	public void deleteSalariesDB(ArrayList<Integer> ids, AsyncCallback<String> callback) {
		employeesServiceAsync.deleteSalariesDB(getCurrentDomainName(), ids, callback);
	}
	
	public void getWorkplaceSalaries(Integer workplaceId, AsyncCallback<List<SalaryInfo>> callback) {
		employeesServiceAsync.getWorkplaceSalaries(getCurrentDomainName(), workplaceId, callback);
	}
	
	public void getFilterEmployeeSalaries(Integer id, SalaryInfoFilter filter, AsyncCallback<List<SalaryInfo>> callback) {
		employeesServiceAsync.getFilterEmployeeSalaries(getCurrentDomainName(), id, filter, callback);
	}

	
	// ------------------------------------------------------------------------
	
	public AgreementServiceAsync asAgreementServiceAsync() {
		return employeesServiceAsync;
	}
	
	
	// ----------------------------------------------------------------- static
	
	private static String getCurrentUser() {
		return Wnd.getCurrentUser();
	}

	private static String getCurrentDomainName() {
		return Wnd.getCurrentDomainNameURL();
	}

	

	

	
	


}
