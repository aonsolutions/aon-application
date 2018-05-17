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
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.Cost;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarUpdate;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsUpdate;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfoDataBase;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.Events;
import com.esferalia.aon.gwt.payroll.shared.EventsWorkplace;
import com.esferalia.aon.gwt.payroll.shared.Extra;
import com.esferalia.aon.gwt.payroll.shared.ITData;
import com.esferalia.aon.gwt.payroll.shared.ITDataPerson;
import com.esferalia.aon.gwt.payroll.shared.Irpf;
import com.esferalia.aon.gwt.payroll.shared.Period;
import com.esferalia.aon.gwt.payroll.shared.ReportData;
import com.esferalia.aon.gwt.payroll.shared.Result;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.SalaryPreview;
import com.esferalia.aon.gwt.payroll.shared.Statistics;
import com.esferalia.aon.gwt.payroll.shared.Variable;
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

//	@Override
//	public void getAvailablePayments(int employeeId,
//			AsyncCallback<List<Payment>> callback)
//			throws IllegalArgumentException {
//		AON.start();
//		employeesServiceAsync.getAvailablePayments(employeeId,
//				new AsyncCallbackWrapper<List<Payment>>(callback));
//	}

	@Override
	public void getAvailableDeductions(int employeeId,
			AsyncCallback<List<Deduction>> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getAvailableDeductions(employeeId,
				new AsyncCallbackWrapper<List<Deduction>>(callback));
	}
	
	@Override
	public void getAvailableBonuses(int employeeId,
			AsyncCallback<List<Bonus>> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getAvailableBonuses(employeeId,
				new AsyncCallbackWrapper<List<Bonus>>(callback));
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
		employeesServiceAsync.getWorkplaceStats(workplaceId,
				new AsyncCallbackWrapper<Statistics>(callback));
	}

	@Override
	public void getEnterpriseStats(int enterpriseId,
			AsyncCallback<Statistics> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getEnterpriseStats(enterpriseId,
				new AsyncCallbackWrapper<Statistics>(callback));
	}

	@Override
	public void getWorkplaceITData(int workplaceId,
			AsyncCallback<ITData> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getWorkplaceITData(workplaceId,
				new AsyncCallbackWrapper<ITData>(callback));
	}

	@Override
	public void getEnterpriseITData(int enterpriseId,
			AsyncCallback<ITData> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getEnterpriseITData(enterpriseId,
				new AsyncCallbackWrapper<ITData>(callback));
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
	public void getExtras(List<Employee> employees, AsyncCallback<List<Extra>> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getExtras(employees,
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

	@Override
	public void insertPerson(Employee employee, AsyncCallback<Void> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.insertPerson(employee,
				new AsyncCallbackWrapper<Void>(callback));
	}
	
	@Override
	public void getEmployee(int employeeId, AsyncCallback<Employee> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getEmployee(employeeId, new AsyncCallbackWrapper<Employee>(
						callback));
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
	public void getTrashEmployees(int workplaceId,
			AsyncCallback<List<Employee>> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getTrashEmployees(workplaceId,
				new AsyncCallbackWrapper<List<Employee>>(callback));
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

	public void saveSalary(SalaryDraft salaryDraft,
			Date sections [],
			AsyncCallback<SalaryDraft> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.saveSalary(salaryDraft, sections,
				new AsyncCallbackWrapper<SalaryDraft>(callback));
	}

	@Override
	public void eval(String expression, SalaryDraft salaryDraft,
			AsyncCallback<List<Result>> callback)
			throws IllegalArgumentException, EvalException {
		AON.start();
		employeesServiceAsync.eval(expression, salaryDraft,
				new AsyncCallbackWrapper<List<Result>>(callback));

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
	public void calculateSalaryDraft(SalaryDraft salaryDraft,
			Date sections [],
			AsyncCallback<SalaryDraft> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.calculateSalaryDraft(salaryDraft,
				sections,
				new AsyncCallbackWrapper<SalaryDraft>(callback));
	}

	@Override
	public void calculateSalaryDraft4Dummies(SalaryDraft salaryDraft,
			AsyncCallback<SalaryDraft> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.calculateSalaryDraft4Dummies(salaryDraft,
				new AsyncCallbackWrapper<SalaryDraft>(callback));
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
	public void getVariables(SalaryDraft salaryDraft, Date startDate, Date endDate,
			String[] names, AsyncCallback<List<Variable>> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getVariables(salaryDraft, startDate, endDate,
				names, new AsyncCallbackWrapper<List<Variable>>(callback));

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
	public void getEmployeeEventsVariables(Integer employeeId, Date startDate, Date endDate,
			AsyncCallback<ContextDescriptor> callback) {
		AON.start();
		employeesServiceAsync.getEmployeeEventsVariables(employeeId, startDate, endDate,
				new AsyncCallbackWrapper<ContextDescriptor>(callback));
		
	}

	@Override
	public void delete(Salary[] salaries, AsyncCallback<Void> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.delete(salaries, new AsyncCallbackWrapper<Void>(
				callback));
	}


	@Override
	public void saveITDataPerson(
			Map<Integer, LinkedHashMap<Integer, ITDataPerson>> inserts,
			Map<Integer, LinkedHashMap<Integer, ITDataPerson>> deletes,
			Map<Integer, LinkedHashMap<Integer, ITDataPerson>> updates,
			AsyncCallback<ITData> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.saveITDataPerson(inserts, deletes, updates,
				new AsyncCallbackWrapper<ITData>(callback));
	}

	@Override
	public void pasteContract(int workplaceId, int contractId, String document,
			Date startDate, Date endDate, boolean check,
			AsyncCallback<Employee> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.pasteContract(workplaceId, contractId, document,
				startDate, endDate, check, new AsyncCallbackWrapper<Employee>(
						callback));
	}

	@Override
	public void moveContractId(Employee employee, AsyncCallback<Void> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.moveContractId(employee,
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void deleteContract(Employee employee, AsyncCallback<Void> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.deleteContract(employee,
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void getAvaiableEmployees(AsyncCallback<Map<String, String>> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync
				.getAvaiableEmployees(new AsyncCallbackWrapper<Map<String, String>>(
						callback));
	}

	// ------------------------------------------------- GPSReportsServiceAsync
	@Override
	public void getA3Report(Date month, int[] workplaces,
			AsyncCallback<ReportData> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getA3Report(month, workplaces,
				new AsyncCallbackWrapper<ReportData>(callback));
	}

	@Override
	public void getFTEReport(Date start, Date end, int[] workplaces,
			AsyncCallback<ReportData> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getFTEReport(start, end, workplaces,
				new AsyncCallbackWrapper<ReportData>(callback));
	}
	
	// --------------------------------------------------- CalendarServiceAsync
	
	@Override
	public void getEmployeeCalendar(int contract, AsyncCallback<EmployeeCalendarData> callback) {
		AON.start();
		employeesServiceAsync.getEmployeeCalendar(contract,
				new AsyncCallbackWrapper<EmployeeCalendarData>(callback));
	}
	
	@Override
	public void setEmployeeCalendar(int contract, EmployeeCalendarUpdate updateInfo,
			AsyncCallback<EmployeeCalendarUpdate> callback) {
		AON.start();
		employeesServiceAsync.setEmployeeCalendar(contract, updateInfo,
				new AsyncCallbackWrapper<EmployeeCalendarUpdate>(callback));
		
	}
	
	@Override
	public void getHolidayReport(Date start, Date end, int[] workplaces,
			AsyncCallback<ReportData> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getHolidayReport(start, end, workplaces,
				new AsyncCallbackWrapper<ReportData>(callback));
	}

	@Override
	public void getCalendar(int workplaceId, Integer pattern, Integer year,
			AsyncCallback<CalendarDraft> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.getCalendar(workplaceId, pattern, year,
				new AsyncCallbackWrapper<CalendarDraft>(callback));
	}

	@Override
	public void getHolidayDescription(
			AsyncCallback<Map<Integer, String>> callback)
			throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync
				.getHolidayDescription(new AsyncCallbackWrapper<Map<Integer, String>>(
						callback));
	}

	@Override
	public void saveHolidaysAndDays(int workplaceId, String holidayDescription,
			Integer holidayListBox, Map<Date, String> map, CalendarDraft.DayType dayTypes[],
			AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.saveHolidaysAndDays(workplaceId, holidayDescription,
				holidayListBox, map, dayTypes,new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void deletePropertyHoliday(Integer id, Date date,
			AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		employeesServiceAsync.deletePropertyHoliday(id, date,
				new AsyncCallbackWrapper<Void>(callback));

	}

	// --------------------------------------------------- EmployeeEventsServiceAsync

	@Override
	public void setEmployeeEvents(int contract, EmployeeEventsUpdate updateInfo,
			AsyncCallback<EmployeeEventsUpdate> callback) {
		AON.start();
		employeesServiceAsync.setEmployeeEvents(contract, updateInfo,
				new AsyncCallbackWrapper<EmployeeEventsUpdate>(callback));
		
	}

	@Override
	public void getEmployeeEvents(int contract, ArrayList<String> employeeContractVariables,
			AsyncCallback<EmployeeEventsData> callback) {
		AON.start();
		employeesServiceAsync.getEmployeeEvents(contract, employeeContractVariables,
				new AsyncCallbackWrapper<EmployeeEventsData>(callback));
		
	}

	@Override
	public void getWorkplaceEmployees(Integer workplaceId, AsyncCallback<WorkplaceEmployees> callback) {
		AON.start();
		employeesServiceAsync.getWorkplaceEmployees(workplaceId, 
				new AsyncCallbackWrapper<WorkplaceEmployees>(callback));
		
	}

	@Override
	public void setEventsWorkplace(EventsWorkplace updateEventsWorkplace,
			AsyncCallback<EventsWorkplace> callback) {
		AON.start();
		employeesServiceAsync.setEventsWorkplace(updateEventsWorkplace, 
				new AsyncCallbackWrapper<EventsWorkplace>(callback));
		
	}

	@Override
	public void getEmployeeInfoDataBase(Integer employeeContract, AsyncCallback<EmployeeInfoDataBase> callback) {
		AON.start();
		employeesServiceAsync.getEmployeeInfoDataBase(employeeContract, 
				new AsyncCallbackWrapper<EmployeeInfoDataBase>(callback));

	}

	@Override
	public void setEmployeeInfoDataBase(EmployeeInfoDataBase newEmployeeInfo,
			AsyncCallback<EmployeeInfoDataBase> callback) {
		AON.start();
		employeesServiceAsync.setEmployeeInfoDataBase(newEmployeeInfo, 
				new AsyncCallbackWrapper<EmployeeInfoDataBase>(callback));
	
	}
	
}
