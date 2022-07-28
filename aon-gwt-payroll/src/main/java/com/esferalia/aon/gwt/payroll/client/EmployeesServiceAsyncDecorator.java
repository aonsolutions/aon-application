/**
 * 
 */
package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.common.shared.EvalException;
import com.esferalia.aon.gwt.payroll.shared.Bonus;
import com.esferalia.aon.gwt.payroll.shared.CalendarDraft;
import com.esferalia.aon.gwt.payroll.shared.Certifica2Info;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.ContractConceptCalc;
import com.esferalia.aon.gwt.payroll.shared.ContractExtension;
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
import com.esferalia.aon.gwt.payroll.shared.Period;
import com.esferalia.aon.gwt.payroll.shared.Result;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfo;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfoFilter;
import com.esferalia.aon.gwt.payroll.shared.SalaryPreview;
import com.esferalia.aon.gwt.payroll.shared.Statistics;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceEmployees;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author rtrepiana
 * 
 */
public class EmployeesServiceAsyncDecorator extends AgreementServiceAsyncDecorator implements EmployeesServiceAsync,
		CalendarServiceAsync, EmployeeEventsServiceAsync {

	private EmployeesServiceAsync employeesServiceAsync;

	public EmployeesServiceAsyncDecorator(
			EmployeesServiceAsync employeesServiceAsync) {
		super(employeesServiceAsync);
		this.employeesServiceAsync = employeesServiceAsync;
	}

	public void getEnterprise(String domain, String user, AsyncCallback<Enterprise> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync
				.getEnterprise(domain, user, new AsyncCallbackWrapper<Enterprise>(callback));
	}

	@Override
	public void getEnterprises(String domain, String user, AsyncCallback<Enterprise[]> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync
				.getEnterprises(domain, user, new AsyncCallbackWrapper<Enterprise[]>(callback));
	}

//	@Override
//	public void getAvailablePayments(int employeeId,
//			AsyncCallback<List<Payment>> callback)
//			throws IllegalArgumentException {
//		AON.start();
//		employeesServiceAsync.getAvailablePayments(employeeId,
//				new AsyncCallbackWrapper<List<Payment>>(callback));
//	}

	@Override
	public void getAvailableDeductions(String domain, int employeeId,
			AsyncCallback<List<Deduction>> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getAvailableDeductions(domain, employeeId,
				new AsyncCallbackWrapper<List<Deduction>>(callback));
	}
	
	@Override
	public void getAvailableBonuses(String domain, int employeeId,
			AsyncCallback<List<Bonus>> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getAvailableBonuses(domain, employeeId,
				new AsyncCallbackWrapper<List<Bonus>>(callback));
	}

	public void getWorkplaceCosts(String domain, int workplaceId,
			AsyncCallback<List<Cost>> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getWorkplaceCosts(domain, workplaceId,
				new AsyncCallbackWrapper<List<Cost>>(callback));
	}

	public void getEnterpriseCosts(String domain, int enterpriseId,
			AsyncCallback<List<Cost>> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getEnterpriseCosts(domain, enterpriseId,
				new AsyncCallbackWrapper<List<Cost>>(callback));
	}

	@Override
	public void getWorkplaceStats(String domain, int workplaceId,
			AsyncCallback<Statistics> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getWorkplaceStats(domain, workplaceId,
				new AsyncCallbackWrapper<Statistics>(callback));
	}

	@Override
	public void getEnterpriseStats(String domain, int enterpriseId,
			AsyncCallback<Statistics> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getEnterpriseStats(domain, enterpriseId,
				new AsyncCallbackWrapper<Statistics>(callback));
	}

	@Override
	public void getWorkplaceITData(String domain, int workplaceId,
			AsyncCallback<ITData> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getWorkplaceITData(domain, workplaceId,
				new AsyncCallbackWrapper<ITData>(callback));
	}

	@Override
	public void getEnterpriseITData(String domain, int enterpriseId,
			AsyncCallback<ITData> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getEnterpriseITData(domain, enterpriseId,
				new AsyncCallbackWrapper<ITData>(callback));
	}

	public void getSalaries(String domain, Employee employee,
			AsyncCallback<List<Salary>> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getSalaries(domain, employee,
				new AsyncCallbackWrapper<List<Salary>>(callback));
	}

	@Override
	public void getIrpfs(String domain, Employee employee, AsyncCallback<List<Irpf>> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getIrpfs(domain, employee,
				new AsyncCallbackWrapper<List<Irpf>>(callback));
	}

	@Override
	public void getExtras(String domain, List<Employee> employees, AsyncCallback<List<Extra>> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getExtras(domain, employees,
				new AsyncCallbackWrapper<List<Extra>>(callback));
	}

	@Override
	public void getIrpfReceiptHTML(String domain, Irpf irpf, int zoom,
			AsyncCallback<String> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getIrpfReceiptHTML(domain, irpf, zoom,
				new AsyncCallbackWrapper<String>(callback));
	}

	public void getCostReceiptPDF(String domain, Cost cost, Salary.Type types[],
			AsyncCallback<String> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getCostReceiptPDF(domain, cost, types, 
				new AsyncCallbackWrapper<String>(callback));
	}

	public void getCostReceiptHTML(String domain, Cost cost, Salary.Type types[], int zoom,
			AsyncCallback<String> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getCostReceiptHTML(domain, cost, types, zoom,
				new AsyncCallbackWrapper<String>(callback));
	}

	public void getSLDCalcReceiptHTML(String domain, String user, Cost cost, Salary.Type types[], int zoom,
			AsyncCallback<String> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getSLDCalcReceiptHTML(domain, user, cost, types, zoom,
				new AsyncCallbackWrapper<String>(callback));
	}

	public void getSalaryReceiptHTML(String domain, Cost cost, Salary.Type types[], int zoom,
			AsyncCallback<String> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getSalaryReceiptHTML(domain, cost, types, zoom,
				new AsyncCallbackWrapper<String>(callback));
	}

	public void getSalaryReceiptHTML(String domain, Salary salary, int zoom,
			AsyncCallback<String> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getSalaryReceiptHTML(domain, salary, zoom,
				new AsyncCallbackWrapper<String>(callback));
	}

	public void getSalaryPreviewReceiptHTML(String domain, SalaryPreview salaryPreview,
			int zoom, AsyncCallback<String> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getSalaryPreviewReceiptHTML(domain, salaryPreview, zoom,
				new AsyncCallbackWrapper<String>(callback));
	}

	@Override
	public void insertPerson(String domain, Employee employee, AsyncCallback<Void> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.insertPerson(domain, employee,
				new AsyncCallbackWrapper<Void>(callback));
	}
	
	@Override
	public void getEmployee(String domain, int employeeId, AsyncCallback<Employee> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getEmployee(domain, employeeId, new AsyncCallbackWrapper<Employee>( 
						callback));
	}

	public void getEmployees(String domain, int workplaceId, Date endDate, String pattern,
			int offset, int limit, AsyncCallback<List<Employee>> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getEmployees(domain, workplaceId, endDate, pattern,
				offset, limit, new AsyncCallbackWrapper<List<Employee>>(
						callback));
	}

	@Override
	public void getTrashEmployees(String domain, int workplaceId,
			AsyncCallback<List<Employee>> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getTrashEmployees(domain, workplaceId,
				new AsyncCallbackWrapper<List<Employee>>(callback));
	}

	@Override
	public void saveSalaryDraft(String domain, SalaryDraft salaryDraft,
			AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.saveSalaryDraft(domain, salaryDraft,
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void saveSalary(String domain,  String user, SalaryDraft salaryDraft,
			AsyncCallback<SalaryDraft> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.saveSalary(domain, user, salaryDraft,
				new AsyncCallbackWrapper<SalaryDraft>(callback));
	}

	public void saveSalary(String domain, SalaryDraft salaryDraft,
			Date sections [],
			AsyncCallback<SalaryDraft> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.saveSalary(domain, salaryDraft, sections,
				new AsyncCallbackWrapper<SalaryDraft>(callback));
	}

	@Override
	public void eval(String domain, String expression, SalaryDraft salaryDraft,
			AsyncCallback<List<Result>> callback)
			throws IllegalArgumentException, EvalException {
		AON.start();
		employeesServiceAsync.eval(domain, expression, salaryDraft,
				new AsyncCallbackWrapper<List<Result>>(callback));

	}

	@Override
	public void getContext(String domain, SalaryDraft salaryDraft,
			AsyncCallback<ContextDescriptor> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getContext(domain, salaryDraft,
				new AsyncCallbackWrapper<ContextDescriptor>(callback));
	}


	@Override
	public void calculateIrpf(String domain, SalaryDraft salaryDraft,
			AsyncCallback<Double> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.calculateIrpf(domain, salaryDraft,
				new AsyncCallbackWrapper<Double>(callback));
	}

	@Override
	public void calculateSalaryDraft(String domain, SalaryDraft salaryDraft,
			AsyncCallback<SalaryDraft> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.calculateSalaryDraft(domain, salaryDraft,
				new AsyncCallbackWrapper<SalaryDraft>(callback));
	}

	@Override
	public void calculateSalaryDraft(String domain, SalaryDraft salaryDraft,
			Date sections [],
			AsyncCallback<SalaryDraft> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.calculateSalaryDraft(domain, salaryDraft,
				sections,
				new AsyncCallbackWrapper<SalaryDraft>(callback));
	}

	@Override
	public void syncSalaryDraft(String domain, String user,
			SalaryDraft salaryDraft, AsyncCallback<SalaryDraft> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.syncSalaryDraft(domain, user,
				salaryDraft, new AsyncCallbackWrapper<SalaryDraft>(callback));
	}

	@Override
	public void getSalaryDraftReceiptHTML(String domain, SalaryDraft salaryPreview, int zoom,
			AsyncCallback<String> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getSalaryDraftReceiptHTML(domain, salaryPreview, zoom,
				new AsyncCallbackWrapper<String>(callback));
	}

	@Override
	public void getSalaryDraftReceipt(String domain, SalaryDraft salaryDraft, String mime,
			AsyncCallback<String> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getSalaryDraftReceipt(domain, salaryDraft, mime,
				new AsyncCallbackWrapper<String>(callback));
	}
	
	@Override
	public void getSettleDraftReceipt(String domain, SalaryDraft salaryDraft, String mime,
			AsyncCallback<String> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getSettleDraftReceipt(domain, salaryDraft, mime,
				new AsyncCallbackWrapper<String>(callback));
	}

	@Override
	public void getIrpfDraftReceipt(String domain, SalaryDraft salaryDraft, String mime,
			AsyncCallback<String> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getIrpfDraftReceipt(domain, salaryDraft, mime,
				new AsyncCallbackWrapper<String>(callback));

	}

	@Override
	public void getIrpfDraftReceiptHTML(String domain, SalaryDraft salaryPreview, int zoom,
			AsyncCallback<String> callback) throws IllegalArgumentException {

		AON.start();
		employeesServiceAsync.getIrpfDraftReceiptHTML(domain, salaryPreview, zoom,
				new AsyncCallbackWrapper<String>(callback));
	}

	@Override
	public void saveEvents(String domain, Events events, Date startDate, Date endDate,
			AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.saveEvents(domain, events, startDate, endDate,
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void getEvents(String domain, Integer workplaceId, Date startDate, Date endDate,
			int offset, int limit, String names[],
			AsyncCallback<Events> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getEvents(domain, workplaceId, startDate, endDate,
				offset, limit, names,
				new AsyncCallbackWrapper<Events>(callback));
	}

	@Override
	public void getVariables(String domain, SalaryDraft salaryDraft, Date startDate, Date endDate,
			String[] names, AsyncCallback<List<Variable>> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getVariables(domain, salaryDraft, startDate, endDate,
				names, new AsyncCallbackWrapper<List<Variable>>(callback));

	}

	@Override
	public void getAvailPeriod(String domain, Integer workplaceId, String name,
			AsyncCallback<Period> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getAvailPeriod(domain, workplaceId, name,
				new AsyncCallbackWrapper<Period>(callback));
	}

	@Override
	public void getEventsVariables(String domain, Integer workplaceId, Integer agreementId,
			Date startDate, Date endDate,
			AsyncCallback<Map<String, String>> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getEventsVariables(domain, workplaceId, agreementId,
				startDate, endDate,
				new AsyncCallbackWrapper<Map<String, String>>(callback));

	}
	
	@Override
	public void getWorkplaceEventsVariables(String domain, Integer workplaceId, Integer agreementId,
			Date startDate, Date endDate, AsyncCallback<Map<String, String>> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getWorkplaceEventsVariables(domain, workplaceId, agreementId,
				startDate, endDate,
				new AsyncCallbackWrapper<Map<String, String>>(callback));
	}
	
	@Override
	public void getEmployeeEventsVariables(String domain, Integer employeeId, Date startDate, Date endDate,
			AsyncCallback<ContextDescriptor> callback) {
		AON.start();
		employeesServiceAsync.getEmployeeEventsVariables(domain, employeeId, startDate, endDate,
				new AsyncCallbackWrapper<ContextDescriptor>(callback));
		
	}

	@Override
	public void delete(String domain, Salary[] salaries, AsyncCallback<Void> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.delete(domain, salaries, new AsyncCallbackWrapper<Void>( 
				callback));
	}


	@Override
	public void saveITDataPerson(String domain,
			Map<Integer, LinkedHashMap<Integer, ITDataPerson>> inserts,
			Map<Integer, LinkedHashMap<Integer, ITDataPerson>> deletes,
			Map<Integer, LinkedHashMap<Integer, ITDataPerson>> updates,
			AsyncCallback<ITData> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.saveITDataPerson(domain, inserts, deletes, updates,
				new AsyncCallbackWrapper<ITData>(callback));
	}

	@Override
	public void pasteContract(String domain, int workplaceId, int contractId, String document,
			Date startDate, Date endDate, boolean check,
			AsyncCallback<Employee> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.pasteContract(domain, workplaceId, contractId, document,
				startDate, endDate, check, new AsyncCallbackWrapper<Employee>(
						callback));
	}

	@Override
	public void moveContractId(String domain, Employee employee, AsyncCallback<Void> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.moveContractId(domain, employee,
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void deleteContract(String domain, Employee employee, AsyncCallback<Void> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.deleteContract(domain, employee,
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void getAvaiableEmployees(String domain, AsyncCallback<Map<String, String>> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync
				.getAvaiableEmployees(domain, new AsyncCallbackWrapper<Map<String, String>>(
						callback));
	}
	
	// --------------------------------------------------- CalendarServiceAsync
	
	@Override
	public void getEmployeeCalendar(String domain, int contract, AsyncCallback<EmployeeCalendarData> callback) {
		AON.start();
		employeesServiceAsync.getEmployeeCalendar(domain, contract,
				new AsyncCallbackWrapper<EmployeeCalendarData>(callback));
	}
	
	@Override
	public void setEmployeeCalendar(String domain, int contract, EmployeeCalendarUpdate updateInfo,
			AsyncCallback<EmployeeCalendarUpdate> callback) {
		AON.start();
		employeesServiceAsync.setEmployeeCalendar(domain, contract, updateInfo,
				new AsyncCallbackWrapper<EmployeeCalendarUpdate>(callback));
		
	}
	

	@Override
	public void getCalendar(String domain, int workplaceId, Integer pattern, Integer year,
			AsyncCallback<CalendarDraft> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getCalendar(domain, workplaceId, pattern, year,
				new AsyncCallbackWrapper<CalendarDraft>(callback));
	}

	@Override
	public void getHolidayDescription(String domain, 
			AsyncCallback<Map<Integer, String>> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync
				.getHolidayDescription(domain, new AsyncCallbackWrapper<Map<Integer, String>>(
						callback));
	}

	@Override
	public void saveHolidaysAndDays(String domain, int workplaceId, String holidayDescription,
			Integer holidayListBox, Map<Date, String> map, CalendarDraft.DayType dayTypes[],
			AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.saveHolidaysAndDays(domain, workplaceId, holidayDescription,
				holidayListBox, map, dayTypes,new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void deletePropertyHoliday(String domain, Integer id, Date date,
			AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.deletePropertyHoliday(domain, id, date,
				new AsyncCallbackWrapper<Void>(callback));

	}

	// --------------------------------------------------- EmployeeEventsServiceAsync

	@Override
	public void setEmployeeEvents(String domain, int contract, EmployeeEventsUpdate updateInfo,
			AsyncCallback<EmployeeEventsUpdate> callback) {
		AON.start();
		employeesServiceAsync.setEmployeeEvents(domain, contract, updateInfo,
				new AsyncCallbackWrapper<EmployeeEventsUpdate>(callback));
		
	}

	@Override
	public void getEmployeeEvents(String domain, int contract, ArrayList<String> employeeContractVariables,
			AsyncCallback<EmployeeEventsData> callback) {
		AON.start();
		employeesServiceAsync.getEmployeeEvents(domain, contract, employeeContractVariables,
				new AsyncCallbackWrapper<EmployeeEventsData>(callback));
		
	}

	@Override
	public void getWorkplaceEmployees(String domain, Workplace workplace, AsyncCallback<WorkplaceEmployees> callback) {
		AON.start();
		employeesServiceAsync.getWorkplaceEmployees(domain, workplace, 
				new AsyncCallbackWrapper<WorkplaceEmployees>(callback));
		
	}

	@Override
	public void setEventsWorkplace(String domain, EventsWorkplace updateEventsWorkplace,
			AsyncCallback<EventsWorkplace> callback) {
		AON.start();
		employeesServiceAsync.setEventsWorkplace(domain, updateEventsWorkplace, 
				new AsyncCallbackWrapper<EventsWorkplace>(callback));
		
	}

	@Override
	public void getEmployeeInfoDataBase(String domain, String user, Integer contractId, Workplace workplace, AsyncCallback<EmployeeContractInfo> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getEmployeeInfoDataBase(domain, user, contractId, workplace,
				new AsyncCallbackWrapper<EmployeeContractInfo>(callback));

	}

	@Override
	public void setEmployeeInfoDataBase(String domain, EmployeeContractInfo newEmployeeInfo,
			AsyncCallback<EmployeeContractInfo> callback) {
		AON.start();
		employeesServiceAsync.setEmployeeInfoDataBase(domain, newEmployeeInfo, 
				new AsyncCallbackWrapper<EmployeeContractInfo>(callback));
	
	}

	@Override
	public void createEmployeeContract(String domain, EmployeeContractInfo employeeContractData,
			AsyncCallback<EmployeeContractInfo> callback) {
		AON.start();
		employeesServiceAsync.createEmployeeContract(domain, employeeContractData,
				new AsyncCallbackWrapper<EmployeeContractInfo>(callback));
	}

	@Override
	public void getWorkplaceEmployeesEvents(String domain, Integer workplaceId,
			AsyncCallback<WorkplaceEmployees> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getWorkplaceEmployeesEvents(domain, workplaceId,
				new AsyncCallbackWrapper<WorkplaceEmployees>(callback));
	}

	@Override
	public void getEmployeeEventsByContract(String domain, Integer contractId,
			ArrayList<String> employeeContractVariablesDB, AsyncCallback<EmployeeEventsData> callback) {
		AON.start();
		employeesServiceAsync.getEmployeeEventsByContract(domain, contractId, employeeContractVariablesDB,
				new AsyncCallbackWrapper<EmployeeEventsData>(callback));
	}
	

	@Override
	public void setEmployeeEventsByContract(String domain, Integer contractId,
			EmployeeEventsData employeeContractVariablesDB, AsyncCallback<EmployeeEventsData> callback) {
		AON.start();
		employeesServiceAsync.setEmployeeEventsByContract(domain, contractId, employeeContractVariablesDB,
				new AsyncCallbackWrapper<EmployeeEventsData>(callback));
	}


	@Override
	public void setEmployeeAFIChanges(String currentDomainName, Integer contractId, Date newDate, boolean isChangeContract, String tc2,
			boolean isQuoteContract, Integer quoteGroup, boolean isOcupationContract, String ocupation,
			AsyncCallback<String> callback) {
		
		AON.start();
		employeesServiceAsync.setEmployeeAFIChanges(currentDomainName, contractId, 
				newDate, isChangeContract, tc2, isQuoteContract, quoteGroup, isOcupationContract, ocupation, new AsyncCallbackWrapper<String>(callback));
	}

	@Override
	public void resetCalendar(String currentDomainName, Integer employeeId, AsyncCallback<String> callback) {
		AON.start();
		employeesServiceAsync.resetCalendar(currentDomainName, employeeId, new AsyncCallbackWrapper<String>(callback));
	}
	
	// ----- Payroll Salaries
	
	@Override
	public void getSalariesDates(String currentDomainName, SalaryInfoFilter filter, AsyncCallback<Period> callback) {
		AON.start();
		employeesServiceAsync.getSalariesDates(currentDomainName, filter, new AsyncCallbackWrapper<Period>(callback));
	}

	@Override
	public void getSalaries(String currentDomainName, SalaryInfoFilter filter, AsyncCallback<List<SalaryInfo>> callback) {
		AON.start();
		employeesServiceAsync.getSalaries(currentDomainName, filter, new AsyncCallbackWrapper<List<SalaryInfo>>(callback));
	}
	

	@Override
	public void deleteSalaries(String currentDomainName, ArrayList<Integer> ids, AsyncCallback<Void> callback) {
		AON.start();
		employeesServiceAsync.deleteSalaries(currentDomainName, ids, new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void getWorkplaceActiveEmployees(String currentDomainName, Integer workplaceId, AsyncCallback<WorkplaceEmployees> callback) {
		AON.start();
		employeesServiceAsync.getWorkplaceActiveEmployees(currentDomainName, workplaceId, new AsyncCallbackWrapper<WorkplaceEmployees>(callback));
	}

	@Override
	public void getEnterpriseActiveEmployees(String currentDomainName, Integer enterpriseId, AsyncCallback<List<EmployeeInfo>> callback) {
		AON.start();
		employeesServiceAsync.getEnterpriseActiveEmployees(currentDomainName, enterpriseId, new AsyncCallbackWrapper<List<EmployeeInfo>>(callback));
	}

	// ----- New employee calendar

	@Override
	public void getEmployeeCalendarInfo(String domainName, Integer contractId, AsyncCallback<EmployeeCalendarInfo> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getEmployeeCalendarInfo(domainName, contractId, new AsyncCallbackWrapper<EmployeeCalendarInfo>(callback));
	}

	@Override
	public void setEmployeeCalendarInfo(String domainName, Integer contractId, EmployeeCalendarInfo employeeCalendarInfo, AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.setEmployeeCalendarInfo(domainName, contractId, employeeCalendarInfo, new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void resetEmployeeCalendarInfo(String domainName, Integer contractId, AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.resetEmployeeCalendarInfo(domainName, contractId, new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void setEmployeeEvents(String domainName, Integer idEmployee, EmployeeEventsData employeeEventsData,
			AsyncCallback<EmployeeEventsData> callback) {
		AON.start();
		employeesServiceAsync.setEmployeeEvents(domainName, idEmployee, employeeEventsData, new AsyncCallbackWrapper<EmployeeEventsData>(callback));
	}

	@Override
	public void setEventsDraft(String domainName, ArrayList<EventEmployee> eventEmployees,
			AsyncCallback<ArrayList<EventEmployee>> callback) {
		AON.start();
		employeesServiceAsync.setEventsDraft(domainName, eventEmployees, new AsyncCallbackWrapper<ArrayList<EventEmployee>>(callback));
	}

	@Override
	public void generateCertifaca2(String domainName, String user, Integer contractId, AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.generateCertifaca2(domainName, user, contractId, new AsyncCallbackWrapper<Void>(callback));
	} 
	
	@Override
	public void generateCertifaca2(String domainName, String user, Integer contractId, Certifica2Info certifica2Info, AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.generateCertifaca2(domainName, user, contractId, certifica2Info, new AsyncCallbackWrapper<Void>(callback));
	}
	
	// ------------------------------------------------- TGSS Files
	
	@Override
	public void getEmployeeTa(String domain, String user, Integer contractId, String situation, String regimen, String ctaCti, String nss, Date fecha, AsyncCallback<String> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getEmployeeTa(domain, user, contractId, situation, regimen, ctaCti, nss, fecha, new AsyncCallbackWrapper<String>(callback));
	}

	@Override
	public void getEmployeeIdc(String domain, String user, Integer contractId, Date date, AsyncCallback<String> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getEmployeeIdc(domain, user, contractId, date, new AsyncCallbackWrapper<String>(callback));
	}
	
	@Override
	public void checkEmployeeIdc(String domain, String user, Integer contractId, Date date, String idc, AsyncCallback<String> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.checkEmployeeIdc(domain, user, contractId, date, idc, new AsyncCallbackWrapper<String>(callback));
	}
	
	
	@Override
	public void getEmployeeIdcPlNss(String domain, String user, Integer contractId, Date date, AsyncCallback<String> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getEmployeeIdcPlNss(domain, user, contractId, date, new AsyncCallbackWrapper<String>(callback));
	}

	@Override
	public void getEmployeeIdcDates(String domain, String user, Integer contractId, Date date, AsyncCallback<List<Date>> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getEmployeeIdcDates(domain, user, contractId, date, new AsyncCallbackWrapper<List<Date>>(callback));
	}

	@Override
	public void getEmployeeStatus(String domain, String user, Integer contractId, AsyncCallback<EmployeeStatus> callback) {
		AON.start();
		employeesServiceAsync.getEmployeeStatus(domain, user, contractId, new AsyncCallbackWrapper<EmployeeStatus>(callback));
	}

	@Override
	public void fillContract(String currentDomainName, Integer contractId, Integer contractType, String formativeLvl, boolean isTransform, AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.fillContract(currentDomainName, contractId, contractType, formativeLvl, isTransform, new AsyncCallbackWrapper<Void>(callback));
	}
	
	@Override
	public void setData(String currentDomainName, String user, Integer contractId, ArrayList<Variable> data,
			AsyncCallback<Void> callback) {
		AON.start();
		employeesServiceAsync.setData(currentDomainName, user, contractId, data, new AsyncCallbackWrapper<Void>(callback));
	}
	
	// ------------------------------------------------- SEPE Files

	@Override
	public void getEmployeeCbc(String currentDomainName, String user, String document, Integer contractId, Date startDate,
			Date endDate, String sepeIde, AsyncCallback<String> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getEmployeeCbc(currentDomainName, user, document, contractId, startDate, endDate, sepeIde, new AsyncCallbackWrapper<String>(callback));
	}
	
	@Override
	public void getEmployeeCbcTransform(String currentDomainName, String user, String cif, String document, Integer contractId, Date startDate, String sepeIde, AsyncCallback<String> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getEmployeeCbcTransform(currentDomainName, user, cif, document, contractId, startDate, sepeIde, new AsyncCallbackWrapper<String>(callback));
	}

	@Override
	public void getEmployeeCto(String currentDomainName, String user, String document, Integer contractId, Date startDate,
			Date endDate, String sepeIde, AsyncCallback<String> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getEmployeeCto(currentDomainName, user, document, contractId, startDate, endDate, sepeIde, new AsyncCallbackWrapper<String>(callback));
	}
	
	@Override
	public void getEmployeeCtoTransform(String currentDomainName, String user, String cif, String document, Integer contractId, Date startDate, String sepeIde, AsyncCallback<String> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getEmployeeCtoTransform(currentDomainName, user, cif, document, contractId, startDate, sepeIde, new AsyncCallbackWrapper<String>(callback));
	}
	
	@Override
	public void getCertifica2PDF(String currentDomainName, String user, Integer contractId, String nif, Date endDate, 
			AsyncCallback<String> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getCertifica2PDF(currentDomainName, user, contractId, nif, endDate, new AsyncCallbackWrapper<String>(callback));
	}

	// ------------------------------------------------- TGSS Comunications
	
	@Override
	public void sendEmployeeAlta(String currentDomainName, String user, EmployeeContractInfo employeeContractInfo, AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.sendEmployeeAlta(currentDomainName, user, employeeContractInfo, new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void sendEmployeeBaja(String currentDomainName, String user, EmployeeContractInfo employeeContractInfo, AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.sendEmployeeBaja(currentDomainName, user, employeeContractInfo, new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void movPrevDelete(String currentDomainName, String user, String situation, String regimen, String ctaCti, String nss, Date fecha, AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.movPrevDelete(currentDomainName, user, situation, regimen, ctaCti, nss, fecha, new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void altaConsolidadaDelete(String currentDomainName, String user, String situation, String regimen, String ctaCti, String nss, Date fecha, AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.altaConsolidadaDelete(currentDomainName, user, situation, regimen, ctaCti, nss, fecha, new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void cambioCoef(String currentDomainName, String user, EmployeeContractInfo employeeContractInfo, String coef, Date fecha, AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.cambioCoef(currentDomainName, user, employeeContractInfo, coef, fecha, new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void cambioContrato(String currentDomainName, String user, EmployeeContractInfo employeeContractInfo, String tc2, Date fecha, AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.cambioContrato(currentDomainName, user, employeeContractInfo, tc2, fecha, new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void cambioGrupCtz(String currentDomainName, String user, EmployeeContractInfo employeeContractInfo, String grup_ctz, Date fecha, AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.cambioGrupCtz(currentDomainName, user, employeeContractInfo, grup_ctz, fecha, new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void cambioOcupacion(String currentDomainName, String user, EmployeeContractInfo employeeContractInfo, String ocup, Date fecha, AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.cambioOcupacion(currentDomainName, user, employeeContractInfo, ocup, fecha, new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void cambioCatProf(String currentDomainName, String user, EmployeeContractInfo employeeContractInfo, String cat, Date fecha, AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.cambioCatProf(currentDomainName, user, employeeContractInfo, cat, fecha, new AsyncCallbackWrapper<Void>(callback));
	}
	
	// ------------------------------------------------- SEPE Comunications

	@Override
	public void sendContractoSEPE(String currentDomainName, String user, EmployeeContractInfo employeeContractInfo, AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.sendContractoSEPE(currentDomainName, user, employeeContractInfo, new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void sendContractoCBSEPE(String currentDomainName, String user, EmployeeContractInfo employeeContractInfo, AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.sendContractoCBSEPE(currentDomainName, user, employeeContractInfo, new AsyncCallbackWrapper<Void>(callback));
	}
	
	@Override
	public void sendContractoCBTransformSEPE(String currentDomainName, String user, EmployeeContractInfo employeeContractInfo, AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.sendContractoCBTransformSEPE(currentDomainName, user, employeeContractInfo, new AsyncCallbackWrapper<Void>(callback));
	}
	
	@Override
	public void removeContractoSEPE(String currentDomainName, String user, EmployeeContractInfo employeeContractData, AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.removeContractoSEPE(currentDomainName, user, employeeContractData, new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void sendCertifica2(String currentDomainName, String user, Integer contractId, Certifica2Info certifica2Info, AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.sendCertifica2(currentDomainName, user, contractId, certifica2Info, new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void sendContractTransform(String currentDomainName, String currentUser,
			EmployeeContractInfo employeeContractData, AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.sendContractTransform(currentDomainName, currentUser, employeeContractData, new AsyncCallbackWrapper<Void>(callback));
	}
	
	@Override
	public void sendContractExtension(String currentDomainName, String currentUser,
			EmployeeContractInfo employeeContractData, AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.sendContractExtension(currentDomainName, currentUser, employeeContractData, new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void removeContractTransform(String currentDomainName, String currentUser, String ide,
			AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.removeContractTransform(currentDomainName, currentUser, ide, new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void getSepeComunicationData(String currentDomainName, String currentUser, String document, Date date,
			Integer contractId, AsyncCallback<Map<String, String>> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getSepeComunicationData(currentDomainName, currentUser, document, date, contractId, new AsyncCallbackWrapper<Map<String, String>>(callback));
	}

	@Override
	public void getSepeTransformComunicationData(String currentDomainName, String currentUser, String document,
			String enterpriseCif, Date originalStartDate, String sepeId, Integer contractId,
			AsyncCallback<Map<String, String>> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getSepeTransformComunicationData(currentDomainName, currentUser, document, enterpriseCif, originalStartDate, sepeId, contractId, new AsyncCallbackWrapper<Map<String, String>>(callback));
	}

	
	// ------------------------------------------------- SEPE Methods

	@Override
	public void getCertifica2Info(String currentDomainName, String user, Integer contractId, AsyncCallback<Certifica2Info> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getCertifica2Info(currentDomainName, user, contractId, new AsyncCallbackWrapper<Certifica2Info>(callback));
	}
	
	// ------------------------------------------------- EmployeeContractPayments


	@Override
	public void getContractPayements(String currentDomainName, Integer contractId, AsyncCallback<ContractPaymentData> callback) {
		AON.start();
		employeesServiceAsync.getContractPayements(currentDomainName, contractId, new AsyncCallbackWrapper<ContractPaymentData>(callback));
	}

	@Override
	public void updateContractPayments(String currentDomainName, Integer contractId, ContractPaymentData contractPaymentData, AsyncCallback<Void> callback) {
		AON.start();
		employeesServiceAsync.updateContractPayments(currentDomainName, contractId, contractPaymentData, new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void createContractPayment(String currentDomainName, Integer contractId, ContractConceptCalc contractConceptCalc, AsyncCallback<Void> callback) {
		AON.start();
		employeesServiceAsync.createContractPayment(currentDomainName, contractId, contractConceptCalc, new AsyncCallbackWrapper<Void>(callback));
	}
	
	@Override
	public void createContractPayment(String currentDomainName, Integer contractId, List<ContractConceptCalc> contractConceptCalcList, AsyncCallback<Void> callback) {
		AON.start();
		employeesServiceAsync.createContractPayment(currentDomainName, contractId, contractConceptCalcList, new AsyncCallbackWrapper<Void>(callback));
	}
	
	// ------------------------------------------------- Contract Extension

	@Override
	public void contractExtension(String currentDomainName, ContractExtension contractExtension, AsyncCallback<Void> callback) {
		AON.start();
		employeesServiceAsync.contractExtension(currentDomainName, contractExtension, new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void deleteContractExtension(String currentDomainName, Integer contractId, AsyncCallback<Void> callback) {
		AON.start();
		employeesServiceAsync.deleteContractExtension(currentDomainName, contractId, new AsyncCallbackWrapper<Void>(callback));
	}
	
	// ------------------------------------------------- ContractTransform

	@Override
	public void contractTransform(String currentDomainName, ContractTransform contractTransform, AsyncCallback<Integer> callback) {
		AON.start();
		employeesServiceAsync.contractTransform(currentDomainName, contractTransform, new AsyncCallbackWrapper<Integer>(callback));
	}
	
	// ------------------------------------------------- EmployeeIrpf

	@Override
	public void getEmployeeIrpf(String currentDomainName, String ssNumber, Date startDate, AsyncCallback<List<EmployeeIrpf>> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getEmployeeIrpf(currentDomainName, ssNumber, startDate, new AsyncCallbackWrapper<List<EmployeeIrpf>>(callback));
	}

	@Override
	public void setEmployeeIrpf(String currentDomainName, Integer contractId, String fullName, String document, String ssNumber, List<EmployeeIrpf> employeeIrpfs, AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.setEmployeeIrpf(currentDomainName, contractId, fullName, document, ssNumber, employeeIrpfs, new AsyncCallbackWrapper<Void>(callback));
	}
	
	// ------------------------------------------------- ContractVariables

	@Override
	public void getContractVariables(String currentDomainName, Integer contractId, AsyncCallback<List<ContractVariable>> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getContractVariables(currentDomainName, contractId, new AsyncCallbackWrapper<List<ContractVariable>>(callback));
	}

	@Override
	public void updateContractVariables(String currentDomainName, List<ContractVariable> contractVariables, AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.updateContractVariables(currentDomainName, contractVariables, new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void createContractVariable(String currentDomainName, Integer contractId, ContractVariable contractVariable, AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.createContractVariable(currentDomainName, contractId, contractVariable, new AsyncCallbackWrapper<Void>(callback));
	}
	
	@Override
	public void getSalariesOccam(String currentDomainName, String login, ITEmployee itEmployee, Date startDate, Date endDate,
			AsyncCallback<List<Certifica2Info>> callback) {
		AON.start();
		employeesServiceAsync.getSalariesOccam(currentDomainName, login, itEmployee, startDate, endDate, new AsyncCallbackWrapper<>(callback));
	}

}
