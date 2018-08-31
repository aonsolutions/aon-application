package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.common.shared.EvalException;
import com.esferalia.aon.gwt.payroll.shared.Bonus;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.Cost;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfoDataBase;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.Events;
import com.esferalia.aon.gwt.payroll.shared.EventsWorkplace;
import com.esferalia.aon.gwt.payroll.shared.Extra;
import com.esferalia.aon.gwt.payroll.shared.Irpf;
import com.esferalia.aon.gwt.payroll.shared.Period;
import com.esferalia.aon.gwt.payroll.shared.Result;
import com.esferalia.aon.gwt.payroll.shared.SSBonusData;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.SalaryPreview;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceEmployees;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>EmployeesService</code>.
 */
public interface EmployeesServiceAsync extends AgreementServiceAsync, StatisticsServiceAsync,
		CalendarServiceAsync, EmployeeEventsServiceAsync, GPSReportsServiceAsync {
	void getEnterprise(AsyncCallback<Enterprise> callback)
			throws IllegalArgumentException;

	void getEnterprises(AsyncCallback<Enterprise[]> callback)
			throws IllegalArgumentException;


	void getAvailableDeductions(int employeeId,
			AsyncCallback<List<Deduction>> callback)
			throws IllegalArgumentException;

	void getAvailableBonuses(int employeeId,
			AsyncCallback<List<Bonus>> callback)
			throws IllegalArgumentException;

	void getWorkplaceCosts(int workplaceId, AsyncCallback<List<Cost>> callback)
			throws IllegalArgumentException;

	void getEnterpriseCosts(int enterpriseId, AsyncCallback<List<Cost>> callback)
			throws IllegalArgumentException;

	void getSalaries(Employee employee, AsyncCallback<List<Salary>> callback)
			throws IllegalArgumentException;

	void getIrpfs(Employee employee, AsyncCallback<List<Irpf>> callback)
			throws IllegalArgumentException;

	void getExtras(List<Employee> employees, AsyncCallback<List<Extra>> callback)
			throws IllegalArgumentException;

	void getCostReceiptHTML(Cost cost, Salary.Type types[], int zoom,
			AsyncCallback<String> callback) throws IllegalArgumentException;

	void getIrpfReceiptHTML(Irpf irpf, int zoom, AsyncCallback<String> callback)
			throws IllegalArgumentException;

	void getSalaryReceiptHTML(Cost cost, Salary.Type types[], int zoom,
			AsyncCallback<String> callback) throws IllegalArgumentException;

	void getSalaryReceiptHTML(Salary salary, int zoom,
			AsyncCallback<String> callback) throws IllegalArgumentException;

	void saveSalaryDraft(SalaryDraft salaryDraft, AsyncCallback<Void> callback)
			throws IllegalArgumentException;

	void saveSalary(SalaryDraft salaryDraft, AsyncCallback<SalaryDraft> callback)
			throws IllegalArgumentException;

	void saveSalary(SalaryDraft salaryDraft, Date sections[], AsyncCallback<SalaryDraft> callback)
			throws IllegalArgumentException;

	void calculateIrpf(SalaryDraft salaryDraft, AsyncCallback<Double> callback)
			throws IllegalArgumentException;

	void calculateSalaryDraft(SalaryDraft salaryDraft,
			AsyncCallback<SalaryDraft> callback)
			throws IllegalArgumentException;

	void calculateSalaryDraft(SalaryDraft salaryDraft, 
			Date sections [],
			AsyncCallback<SalaryDraft> callback)
			throws IllegalArgumentException;

	void calculateSalaryDraft4Dummies(SalaryDraft salaryDraft,
			AsyncCallback<SalaryDraft> callback)
			throws IllegalArgumentException;

	void eval(String expression, SalaryDraft salaryDraft,
			AsyncCallback<List<Result>> callback)
			throws IllegalArgumentException, EvalException;

	void getContext(SalaryDraft salaryDraft,
			AsyncCallback<ContextDescriptor> callback)
			throws IllegalArgumentException;

	void getSalaryDraftReceipt(SalaryDraft salaryDraft, String mime,
			AsyncCallback<String> callback) throws IllegalArgumentException;

	void getSalaryDraftReceiptHTML(SalaryDraft salaryPreview, int zoom,
			AsyncCallback<String> callback) throws IllegalArgumentException;

	void getIrpfDraftReceipt(SalaryDraft salaryDraft, String mime,
			AsyncCallback<String> callback) throws IllegalArgumentException;

	void getIrpfDraftReceiptHTML(SalaryDraft salaryPreview, int zoom,
			AsyncCallback<String> callback) throws IllegalArgumentException;

	void getSalaryPreviewReceiptHTML(SalaryPreview salaryPreview, int zoom,
			AsyncCallback<String> callback) throws IllegalArgumentException;

	void getEmployee(int employeeId, AsyncCallback<Employee> callback) 
			throws IllegalArgumentException;

	void getEmployees(int workplaceId, Date endDate, String pattern,
			int offset, int limit, AsyncCallback<List<Employee>> callback)
			throws IllegalArgumentException;

	void getTrashEmployees(int workplaceId,
			AsyncCallback<List<Employee>> callback)
			throws IllegalArgumentException;

	void saveEvents(Events events, Date startDate, Date endDate,
			AsyncCallback<Void> callback) throws IllegalArgumentException;

	void getEvents(Integer workplaceId, Date startDate, Date endDate,
			int offset, int limit, String names[],
			AsyncCallback<Events> callback) throws IllegalArgumentException;

	void getAvailPeriod(Integer workplaceId, String name,
			AsyncCallback<Period> callback) throws IllegalArgumentException;

	void getEventsVariables(Integer workplaceId, Integer agreementId,
			Date startDate, Date endDate,
			AsyncCallback<Map<String, String>> callback)
			throws IllegalArgumentException;
	
	void getEmployeeEventsVariables(Integer employeeId, Date startDate, Date endDate,
			AsyncCallback<ContextDescriptor> callback);

	void getVariables(SalaryDraft salaryDraft, Date startDate, Date endDate,
			String names[], AsyncCallback<List<Variable>> callback)
			throws IllegalArgumentException;

	void delete(Salary salaries[], AsyncCallback<Void> callback)
			throws IllegalArgumentException;

	void pasteContract(int workplaceId, int contractId, String document,
			Date startDate, Date endDate, boolean check,
			AsyncCallback<Employee> callback) throws IllegalArgumentException;

	void moveContractId(Employee employee, AsyncCallback<Void> callback)
			throws IllegalArgumentException;

	void deleteContract(Employee employee, AsyncCallback<Void> callback)
			throws IllegalArgumentException;

	void insertPerson(Employee employee, AsyncCallback<Void> callback)
			throws IllegalArgumentException;

	void getAvaiableEmployees(AsyncCallback<Map<String, String>> callback)
			throws IllegalArgumentException;

	void getWorkplaceEmployees(Integer workplaceId, AsyncCallback<WorkplaceEmployees> asyncCallback);

	void setEventsWorkplace(EventsWorkplace updateEventsWorkplace, AsyncCallback<EventsWorkplace> asyncCallback);

	void getEmployeeInfoDataBase(Integer employeeContract, AsyncCallback<EmployeeInfoDataBase> asyncCallback);

	void setEmployeeInfoDataBase(EmployeeInfoDataBase newEmployeeInfo,
			AsyncCallback<EmployeeInfoDataBase> asyncCallback);

	void getEmployeeSSBonuses(Integer contractId, AsyncCallback<List<SSBonusData>> asyncCallback);

	void setEmployeeSSBonuses(Integer contractId, List<SSBonusData> ssBonuses,
			AsyncCallback<List<SSBonusData>> asyncCallback);

	void createEmployeeContract(EmployeeInfoDataBase newEmployeeInfo,
			AsyncCallback<EmployeeInfoDataBase> asyncCallback);

}
