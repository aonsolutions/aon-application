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
import com.esferalia.aon.gwt.payroll.shared.Certifica2Info;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.ContractConceptCalc;
import com.esferalia.aon.gwt.payroll.shared.ContractExtension;
import com.esferalia.aon.gwt.payroll.shared.ContractInfo;
import com.esferalia.aon.gwt.payroll.shared.ContractPaymentData;
import com.esferalia.aon.gwt.payroll.shared.ContractTransform;
import com.esferalia.aon.gwt.payroll.shared.ContractVariable;
import com.esferalia.aon.gwt.payroll.shared.Cost;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarUpdate;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsUpdate;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeIrpf;
import com.esferalia.aon.gwt.payroll.shared.EmployeeStatus;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.EventEmployee;
import com.esferalia.aon.gwt.payroll.shared.Events;
import com.esferalia.aon.gwt.payroll.shared.EventsWorkplace;
import com.esferalia.aon.gwt.payroll.shared.Extra;
import com.esferalia.aon.gwt.payroll.shared.ITData;
import com.esferalia.aon.gwt.payroll.shared.ITDataPerson;
import com.esferalia.aon.gwt.payroll.shared.ITEmployee;
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
import com.esferalia.aon.gwt.payroll.shared.Workplace;
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

	public void getAgreementDraftReceipt(AgreementDraft agreementDraft, List<Variable> context, int levelId, Type type,
			String mime, AsyncCallback<String> callback) throws IllegalArgumentException {
		employeesServiceAsync.getAgreementDraftReceipt(getCurrentDomainName(), agreementDraft, context, levelId, type, mime, callback);
	}

	public void getAgreementDraftReceiptHTML(AgreementDraft agreementDraft, int levelId, Type type,
			int zoom, AsyncCallback<String> callback) throws IllegalArgumentException {
		employeesServiceAsync.getAgreementDraftReceiptHTML(getCurrentDomainName(), agreementDraft, levelId, type, zoom, callback);
	}

	public void saveAgreementDraft(AgreementDraft agreementDraft, AsyncCallback<AgreementDraft> callback)
			throws IllegalArgumentException {
		employeesServiceAsync.saveAgreementDraft(getCurrentDomainName(), getCurrentUser(), agreementDraft, callback);
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

	public void getCostReceiptPDF(Cost cost, Type[] types, AsyncCallback<String> callback)
			throws IllegalArgumentException {
		employeesServiceAsync.getCostReceiptPDF(getCurrentDomainName(), cost, types, callback);
	}

	public void getCostReceiptHTML(Cost cost, Type[] types, int zoom, AsyncCallback<String> callback)
			throws IllegalArgumentException {
		employeesServiceAsync.getCostReceiptHTML(getCurrentDomainName(), cost, types, zoom, callback);
	}

	public void getSLDCalcReceiptHTML(Cost cost, Type[] types, int zoom, AsyncCallback<String> callback)
			throws IllegalArgumentException {
		employeesServiceAsync.getSLDCalcReceiptHTML(getCurrentDomainName(), getCurrentUser(), cost, types, zoom, callback);
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
		employeesServiceAsync.saveSalary(getCurrentDomainName(),  getCurrentUser(), salaryDraft, callback);
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

	public void syncSalaryDraft(SalaryDraft salaryDraft, AsyncCallback<SalaryDraft> callback)
			throws IllegalArgumentException {
		employeesServiceAsync.syncSalaryDraft(getCurrentDomainName(),  getCurrentUser(), salaryDraft, callback);
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


	public void getSettleDraftReceipt(SalaryDraft salaryDraft, String mime, AsyncCallback<String> callback)
			throws IllegalArgumentException {
		employeesServiceAsync.getSettleDraftReceipt(getCurrentDomainName(),  salaryDraft, mime, callback);
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

	public void getWorkplaceEmployees(Workplace workplace, AsyncCallback<WorkplaceEmployees> asyncCallback) {
		employeesServiceAsync.getWorkplaceEmployees(getCurrentDomainName(), workplace, asyncCallback);
	}

	public void setEventsWorkplace(EventsWorkplace updateEventsWorkplace,
			AsyncCallback<EventsWorkplace> asyncCallback) {
		employeesServiceAsync.setEventsWorkplace(getCurrentDomainName(),  updateEventsWorkplace, asyncCallback);
	}

	public void getEmployeeInfoDataBase(Integer contractId, Workplace workplace, AsyncCallback<EmployeeContractInfo> asyncCallback) throws IllegalArgumentException  {
		employeesServiceAsync.getEmployeeInfoDataBase(getCurrentDomainName(), getCurrentUser(), contractId, workplace, asyncCallback);
	}

	public void setEmployeeInfoDataBase(EmployeeContractInfo employeeContractData, AsyncCallback<EmployeeContractInfo> callback) {
		employeesServiceAsync.setEmployeeInfoDataBase(getCurrentDomainName(),  employeeContractData, callback);
	}

	public void createEmployeeContract(EmployeeContractInfo employeeContractData,
			AsyncCallback<EmployeeContractInfo> asyncCallback) {
		employeesServiceAsync.createEmployeeContract(getCurrentDomainName(),  employeeContractData, asyncCallback);
	}
	
	public void getEmployeeEvents(int contract, ArrayList<String> employeeContractVariables,
			AsyncCallback<EmployeeEventsData> callback) {
		employeesServiceAsync.getEmployeeEvents(getCurrentDomainName(), contract, employeeContractVariables, callback);
	}
	
	public void getWorkplaceEmployeesEvents(Integer workplaceId, AsyncCallback<WorkplaceEmployees> callback) throws IllegalArgumentException {
		employeesServiceAsync.getWorkplaceEmployeesEvents(getCurrentDomainName(), workplaceId, callback);
	}
	
	public void getEmployeeEventsByContract(Integer contractId, ArrayList<String> employeeContractVariablesDB,
			AsyncCallback<EmployeeEventsData> callback) {
		employeesServiceAsync.getEmployeeEventsByContract(getCurrentDomainName(), contractId, employeeContractVariablesDB, callback);
	}
	
	public void setEmployeeEventsByContract(Integer contractId, EmployeeEventsData employeeEventsData, AsyncCallback<EmployeeEventsData> callback) {
		employeesServiceAsync.setEmployeeEventsByContract(getCurrentDomainName(), contractId, employeeEventsData, callback);
	}
	
	public void setEmployeeAFIChanges(Integer contractId, Date newDate, boolean isChangeContract, String tc2, boolean isQuoteContract, Integer quoteGroup,
			boolean isOcupationContract, String ocupation, AsyncCallback<String> callback) {
		
		employeesServiceAsync.setEmployeeAFIChanges(getCurrentDomainName(), contractId,
				newDate, isChangeContract, tc2, isQuoteContract, quoteGroup, isOcupationContract, ocupation, callback);
		
	}
	
	// ----- Payroll Salaries
	
	public void getSalariesDates(SalaryInfoFilter filter, AsyncCallback<Period> callback) {
		employeesServiceAsync.getSalariesDates(getCurrentDomainName(), filter, callback);
	}
	
	public void getSalaries(SalaryInfoFilter filter, AsyncCallback<List<SalaryInfo>> callback) {
		employeesServiceAsync.getSalaries(getCurrentDomainName(), filter, callback);
	}
	
	public void deleteSalaries(ArrayList<Integer> ids, AsyncCallback<Void> callback) {
		employeesServiceAsync.deleteSalaries(getCurrentDomainName(), ids, callback);
	}
	
	public void getWorkplaceActiveEmployees(Integer workplaceId, AsyncCallback<WorkplaceEmployees> callback) {
		employeesServiceAsync.getWorkplaceActiveEmployees(getCurrentDomainName(), workplaceId, callback);
	}
	
	public void getEnterpriseActiveEmployees(Integer enterpriseId, AsyncCallback<List<EmployeeInfo>> callback) {
		employeesServiceAsync.getEnterpriseActiveEmployees(getCurrentDomainName(), enterpriseId, callback);
	}
	
	// ----- New employee calendar
	
	public void getEmployeeCalendarInfo(Integer contractId, AsyncCallback<EmployeeCalendarInfo> callback) throws IllegalArgumentException {
		employeesServiceAsync.getEmployeeCalendarInfo(getCurrentDomainName(), contractId, callback);
	}
	
	public void setEmployeeCalendarInfo(Integer contractId, EmployeeCalendarInfo employeeCalendarInfo, AsyncCallback<Void> callback) throws IllegalArgumentException {
		employeesServiceAsync.setEmployeeCalendarInfo(getCurrentDomainName(), contractId, employeeCalendarInfo, callback);
	}
	
	public void resetEmployeeCalendarInfo(Integer contractId, AsyncCallback<Void> callback) throws IllegalArgumentException {
		employeesServiceAsync.resetEmployeeCalendarInfo(getCurrentDomainName(), contractId, callback);
	}
	
	public void setEmployeeEvents(Integer idEmployee, EmployeeEventsData employeeEventsData, AsyncCallback<EmployeeEventsData> callback) {
		employeesServiceAsync.setEmployeeEvents(getCurrentDomainName(), idEmployee, employeeEventsData, callback);
	}

	public void setEventsDraft(ArrayList<EventEmployee> eventEmployees, AsyncCallback<ArrayList<EventEmployee>> callback) {
		employeesServiceAsync.setEventsDraft(getCurrentDomainName(), eventEmployees, callback);
	}

	public void generateCertifaca2(Integer contractId, AsyncCallback<Void> callback) throws IllegalArgumentException {
		employeesServiceAsync.generateCertifaca2(getCurrentDomainName(), getCurrentUser(), contractId, callback);
	}
	
	public void generateCertifaca2(Integer contractId, Certifica2Info certifica2Info, AsyncCallback<Void> callback) throws IllegalArgumentException {
		employeesServiceAsync.generateCertifaca2(getCurrentDomainName(), getCurrentUser(), contractId, certifica2Info, callback);
	}
	
	// ------------------------------------------------- TGSS Files
	
	public void getEmployeeTa(Integer contractId, String situation, String regimen, String ctaCti, String nss, Date fecha, AsyncCallback<String> callback) throws IllegalArgumentException  {
		employeesServiceAsync.getEmployeeTa(getCurrentDomainName(), getCurrentUser(), contractId, situation, regimen, ctaCti, nss, fecha, callback);
	}
	
	public void getEmployeeIdc(Integer contractId, Date date, AsyncCallback<String> callback) throws IllegalArgumentException  {
		employeesServiceAsync.getEmployeeIdc(getCurrentDomainName(), getCurrentUser(), contractId, date, callback);
	}
	
	public void checkEmployeeIdc(Integer contractId, Date date, String idc, AsyncCallback<String> callback) throws IllegalArgumentException  {
		employeesServiceAsync.checkEmployeeIdc(getCurrentDomainName(), getCurrentUser(), contractId, date, idc, callback);
	}

	public void getEmployeeIdcPlNss(Integer contractId, Date date, AsyncCallback<String> callback) throws IllegalArgumentException  {
		employeesServiceAsync.getEmployeeIdcPlNss(getCurrentDomainName(), getCurrentUser(), contractId, date, callback);
	}
	
	public void getEmployeeIdcDates(Integer contractId, Date date, AsyncCallback<List<Date>> callback) throws IllegalArgumentException  {
		employeesServiceAsync.getEmployeeIdcDates(getCurrentDomainName(), getCurrentUser(), contractId, date, callback);
	}

	public void getEmployeeStatus(Integer contractId, AsyncCallback<EmployeeStatus> callback) {
		employeesServiceAsync.getEmployeeStatus(getCurrentDomainName(), getCurrentUser(), contractId, callback);
	}
	
	public void fillContract(Integer contractId, Integer contractType, String formativeLvl, boolean isTransform, AsyncCallback<Void> callback) throws IllegalArgumentException {
		employeesServiceAsync.fillContract(getCurrentDomainName(), contractId, contractType, formativeLvl, isTransform, callback);
	}
	
	public void fillBasicCopy(Integer contractId, Integer contractType, String formativeLvl, AsyncCallback<Void> callback) throws IllegalArgumentException {
		employeesServiceAsync.fillBasicCopy(getCurrentDomainName(), contractId, contractType, formativeLvl, callback);
	}
	
	public void fillContractExtension(EmployeeInfo employeeData, ContractInfo contractData, AsyncCallback<Void> callback) throws IllegalArgumentException {
		employeesServiceAsync.fillContractExtension(getCurrentDomainName(), employeeData, contractData, callback);
	}
	
	public void fillContractRelocation(Integer contractId, Map<String, String> contractRelocationInfo, AsyncCallback<Void> callback) throws IllegalArgumentException {
		employeesServiceAsync.fillContractRelocation(getCurrentDomainName(), contractId, contractRelocationInfo, callback);
	}
	
	void setData(Integer contractId, ArrayList<Variable> data, AsyncCallback<Void> callback) {
		employeesServiceAsync.setData(getCurrentDomainName(), getCurrentUser(), contractId, data, callback);
	}
	
	// ------------------------------------------------- SEPE Files
	
	public void getEmployeeCbc(String document, Integer contractId, Date startDate, Date endDate, String sepeIde, AsyncCallback<String> callback) throws IllegalArgumentException  {
		employeesServiceAsync.getEmployeeCbc(getCurrentDomainName(), getCurrentUser(), document, contractId, startDate, endDate, sepeIde, callback);
	}

	public void getEmployeeCbcTransform(String cif, String document, Integer contractId, Date startDate, String sepeIde, AsyncCallback<String> callback) throws IllegalArgumentException  {
		employeesServiceAsync.getEmployeeCbcTransform(getCurrentDomainName(), getCurrentUser(), cif, document, contractId, startDate, sepeIde, callback);
	}

	public void getEmployeeCto(String document, Integer contractId, Date startDate, Date endDate, String sepeIde, AsyncCallback<String> callback) throws IllegalArgumentException  {
		employeesServiceAsync.getEmployeeCto(getCurrentDomainName(), getCurrentUser(), document, contractId, startDate, endDate, sepeIde, callback);
	}
	
	public void getEmployeeCtoTransform(String cif, String document, Integer contractId, Date startDate, String sepeIde, AsyncCallback<String> callback) throws IllegalArgumentException  {
		employeesServiceAsync.getEmployeeCtoTransform(getCurrentDomainName(), getCurrentUser(), cif, document, contractId, startDate, sepeIde, callback);
	}
	
	public void getEmployeeCtoExtension(String enterpriseCIF, String document, Integer contractId, Date extensionDate, Integer extensionNum, String sepeExtensionId, AsyncCallback<String> callback) throws IllegalArgumentException {
		employeesServiceAsync.getEmployeeCtoExtension(getCurrentDomainName(), getCurrentUser(), enterpriseCIF, document, contractId, extensionDate, extensionNum, sepeExtensionId, callback);
	}
	
	public void getCertifica2PDF(Integer contractId, String nif, Date endDate, AsyncCallback<String> callback) throws IllegalArgumentException {
		employeesServiceAsync.getCertifica2PDF(getCurrentDomainName(), getCurrentUser(), contractId, nif, endDate, callback);
	}
	
	// ------------------------------------------------- TGSS Comunications
	
	public void sendEmployeeAlta(EmployeeContractInfo employeeContractInfo, AsyncCallback<Void> callback) throws IllegalArgumentException {
		employeesServiceAsync.sendEmployeeAlta(getCurrentDomainName(), getCurrentUser(), employeeContractInfo, callback);
	}
	
	public void sendEmployeeBaja(EmployeeContractInfo employeeContractInfo, AsyncCallback<Void> callback) throws IllegalArgumentException {
		employeesServiceAsync.sendEmployeeBaja(getCurrentDomainName(), getCurrentUser(), employeeContractInfo, callback);
	}

	public void movPrevDelete(String situation, String regimen, String ctaCti, String nss, Date fecha, AsyncCallback<Void> callback) throws IllegalArgumentException {
		employeesServiceAsync.movPrevDelete(getCurrentDomainName(), getCurrentUser(), situation, regimen, ctaCti, nss, fecha, callback);
	}
	
	public void altaConsolidadaDelete(String situation, String regimen, String ctaCti, String nss, Date fecha, AsyncCallback<Void> callback) throws IllegalArgumentException {
		employeesServiceAsync.altaConsolidadaDelete(getCurrentDomainName(), getCurrentUser(), situation, regimen, ctaCti, nss, fecha, callback);
	}
	
	public void cambioCoef(EmployeeContractInfo employeeContractInfo, String coef, Date fecha, AsyncCallback<Void> callback) throws IllegalArgumentException {
		employeesServiceAsync.cambioCoef(getCurrentDomainName(), getCurrentUser(), employeeContractInfo, coef, fecha, callback);
	}

	public void cambioContrato(EmployeeContractInfo employeeContractInfo, String tc2, Date fecha, AsyncCallback<Void> callback) throws IllegalArgumentException {
		employeesServiceAsync.cambioContrato(getCurrentDomainName(), getCurrentUser(), employeeContractInfo, tc2, fecha, callback);
	}
	
	public void cambioGrupCtz(EmployeeContractInfo employeeContractInfo, String grupCtz, Date fecha, AsyncCallback<Void> callback) throws IllegalArgumentException {
		employeesServiceAsync.cambioGrupCtz(getCurrentDomainName(), getCurrentUser(), employeeContractInfo, grupCtz, fecha, callback);
	}
	
	public void cambioOcupacion(EmployeeContractInfo employeeContractInfo, String ocup, Date fecha, AsyncCallback<Void> callback) throws IllegalArgumentException {
		employeesServiceAsync.cambioOcupacion(getCurrentDomainName(), getCurrentUser(), employeeContractInfo, ocup, fecha, callback);
	}
	
	public void cambioCno(EmployeeContractInfo employeeContractInfo, String cno, Date fecha, AsyncCallback<Void> callback) throws IllegalArgumentException {
		employeesServiceAsync.cambioCno(getCurrentDomainName(), getCurrentUser(), employeeContractInfo, cno, fecha, callback);
	}
	
	public void cambioCatProf(EmployeeContractInfo employeeContractInfo, String cat, Date fecha, AsyncCallback<Void> callback) throws IllegalArgumentException {
		employeesServiceAsync.cambioCatProf(getCurrentDomainName(), getCurrentUser(), employeeContractInfo, cat, fecha, callback);
	}
	
	// ------------------------------------------------- SEPE Comunications
	
	public void sendLlamamientoSEPE(EmployeeContractInfo employeeContractInfo, AsyncCallback<Void> callback) throws IllegalArgumentException {
		employeesServiceAsync.sendContractoSEPE(getCurrentDomainName(), getCurrentUser(), employeeContractInfo, callback);
	}
	
	public void sendContractoSEPE(EmployeeContractInfo employeeContractInfo, AsyncCallback<Void> callback) throws IllegalArgumentException {
		employeesServiceAsync.sendContractoSEPE(getCurrentDomainName(), getCurrentUser(), employeeContractInfo, callback);
	}

	public void sendContractoCBSEPE(EmployeeContractInfo employeeContractInfo, AsyncCallback<Void> callback) throws IllegalArgumentException {
		employeesServiceAsync.sendContractoCBSEPE(getCurrentDomainName(), getCurrentUser(), employeeContractInfo, callback);
	}
	
	public void sendContractoCBTransformSEPE(EmployeeContractInfo employeeContractInfo, AsyncCallback<Void> callback) throws IllegalArgumentException {
		employeesServiceAsync.sendContractoCBTransformSEPE(getCurrentDomainName(), getCurrentUser(), employeeContractInfo, callback);
	}
	
	public void removeContractoSEPE(EmployeeContractInfo employeeContractData, AsyncCallback<Void> callback) throws IllegalArgumentException {
		employeesServiceAsync.removeContractoSEPE(getCurrentDomainName(), getCurrentUser(), employeeContractData, callback);
	}
	
	public void sendCertifica2(Integer contractId, Certifica2Info certifica2Info, AsyncCallback<Void> callback) throws IllegalArgumentException {
		employeesServiceAsync.sendCertifica2(getCurrentDomainName(), getCurrentUser(), contractId, certifica2Info, callback);
	}
	
	public void sendContractTransform(EmployeeContractInfo employeeContractData, AsyncCallback<Void> callback) throws IllegalArgumentException {
		employeesServiceAsync.sendContractTransform(getCurrentDomainName(), getCurrentUser(), employeeContractData, callback);
	}
	
	public void sendContractExtension(EmployeeContractInfo employeeContractData, AsyncCallback<Void> callback) throws IllegalArgumentException {
		employeesServiceAsync.sendContractExtension(getCurrentDomainName(), getCurrentUser(), employeeContractData, callback);
	}

	public void removeContractTransform(String ide, Integer contractId, AsyncCallback<Void> callback) throws IllegalArgumentException {
		employeesServiceAsync.removeContractTransform(getCurrentDomainName(), getCurrentUser(), ide, contractId, callback);
	}

	public void getSepeComunicationData(String document, Date fini, Integer contractId, AsyncCallback<Map<String, String>> callback) throws IllegalArgumentException {
		employeesServiceAsync.getSepeComunicationData(getCurrentDomainName(), getCurrentUser(), document, fini, contractId, callback);
	}
	
	public void getSepeTransformComunicationData(String document, String enterpriseCif, Date originalStartDate, String sepeId, Integer contractId, AsyncCallback<Map<String, String>> callback) throws IllegalArgumentException {
		employeesServiceAsync.getSepeTransformComunicationData(getCurrentDomainName(), getCurrentUser(), document, enterpriseCif, originalStartDate, sepeId, contractId, callback);
	}
	
	public void getSepeExtensionComunicationData(String document, String enterpriseCif, Date originalStartDate, String sepeId, Integer contractId, AsyncCallback<Map<String, String>> callback) throws IllegalArgumentException {
		employeesServiceAsync.getSepeExtensionComunicationData(getCurrentDomainName(), getCurrentUser(), document, enterpriseCif, originalStartDate, sepeId, contractId, callback);
	}
	
	// ------------------------------------------------- SEPE Methods
	
	public void getCertifica2Info(Integer contractId, AsyncCallback<Certifica2Info> callback) throws IllegalArgumentException {
		employeesServiceAsync.getCertifica2Info(getCurrentDomainName(), getCurrentUser(), contractId, callback);
	}
	
	// ------------------------------------------------- EmployeeContractPayments

	public void getContractPayements(Integer contractId, AsyncCallback<ContractPaymentData> callback) {
		employeesServiceAsync.getContractPayements(getCurrentDomainName(), contractId, callback);
	}

	public void updateContractPayments(Integer contractId, ContractPaymentData contractPaymentData, AsyncCallback<Void> callback) {
		employeesServiceAsync.updateContractPayments(getCurrentDomainName(), contractId, contractPaymentData, callback);
	}
	
	public void createContractPayment(Integer contractId, ContractConceptCalc contractConceptCalc, AsyncCallback<Void> callback) {
		employeesServiceAsync.createContractPayment(getCurrentDomainName(), contractId, contractConceptCalc, callback);
	}
	
	public void createContractPayment(Integer contractId, List<ContractConceptCalc> contractConceptCalcList, AsyncCallback<Void> callback) {
		employeesServiceAsync.createContractPayment(getCurrentDomainName(), contractId, contractConceptCalcList, callback);
	}
	
	// ------------------------------------------------- ContractExtension
	
	public void contractExtension(ContractExtension contractExtension, AsyncCallback<Void> callback) throws IllegalArgumentException {
		employeesServiceAsync.contractExtension(getCurrentDomainName(), contractExtension, callback);
	}
	
	public void deleteContractExtension(Integer contractId, AsyncCallback<Void> callback) throws IllegalArgumentException {
		employeesServiceAsync.deleteContractExtension(getCurrentDomainName(), contractId, callback);
	}
	
	// ------------------------------------------------- ContractTransform
	
	public void contractTransform(ContractTransform contractTransform, AsyncCallback<Void> callback) throws IllegalArgumentException {
		employeesServiceAsync.contractTransform(getCurrentDomainName(), contractTransform, callback);
	}
	
	public void deleteContractTransform(Integer contractId, AsyncCallback<Void> callback) throws IllegalArgumentException {
		employeesServiceAsync.deleteContractTransform(getCurrentDomainName(), contractId, callback);
	}
	
	
	public void removeContractTransform(Integer contractId, AsyncCallback<Void> callback) throws IllegalArgumentException {
		employeesServiceAsync.removeContractTransform(getCurrentDomainName(), contractId, callback);
	}
	
	// ------------------------------------------------- EmployeeIrpf
	
	public void getEmployeeIrpf(String ssNumber, String document, Date startDate, AsyncCallback<List<EmployeeIrpf>> callback) throws IllegalArgumentException {
		employeesServiceAsync.getEmployeeIrpf(getCurrentDomainName(), ssNumber, document, startDate, callback);
	}
	
	public void setEmployeeIrpf(Integer contractId, String fullName, String document, String ssNumber, List<EmployeeIrpf> employeeIrpfs, AsyncCallback<Void> callback) throws IllegalArgumentException {
		employeesServiceAsync.setEmployeeIrpf(getCurrentDomainName(), contractId, fullName, document, ssNumber, employeeIrpfs, callback);
	}
	
	// ------------------------------------------------- ContractVariables
	
	public void getContractVariables(Integer contractId, AsyncCallback<List<ContractVariable>> callback) throws IllegalArgumentException {
		employeesServiceAsync.getContractVariables(getCurrentDomainName(), contractId, callback);
	}

	public void updateContractVariables(List<ContractVariable> contractVariables, AsyncCallback<Void> callback) throws IllegalArgumentException {
		employeesServiceAsync.updateContractVariables(getCurrentDomainName(), contractVariables, callback);
	}
	
	public void createContractVariable(Integer contractId, ContractVariable contractVariable, AsyncCallback<Void> callback) throws IllegalArgumentException {
		employeesServiceAsync.createContractVariable(getCurrentDomainName(), contractId, contractVariable, callback);
	}

	public void getSalariesOccam(ITEmployee itEmployee, Date startDate, Date endDate, AsyncCallback<List<Certifica2Info>> callback) throws IllegalArgumentException {
		employeesServiceAsync.getSalariesOccam(getCurrentDomainName(), getCurrentUser(), itEmployee, startDate, endDate, callback);
	}
	
	// ------------------------------------------------- Agreement ContextProvider
	
	public void getAgreementContext(int fxLevel, Date startDate, Date endDate, AsyncCallback<ContextDescriptor> callback) throws IllegalArgumentException {
		employeesServiceAsync.getAgreementContext(getCurrentDomainName(), fxLevel, startDate, endDate, callback);
	}

	public void evalAgreement(String expression, Date startDate, int fxLevel, AsyncCallback<List<Result>> callback) throws IllegalArgumentException, EvalException {
		employeesServiceAsync.evalAgreement(getCurrentDomainName(), expression, startDate, fxLevel, callback);
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
