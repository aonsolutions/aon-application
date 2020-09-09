package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.common.shared.EvalException;
import com.esferalia.aon.gwt.payroll.shared.Bonus;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.Cost;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.EventEmployee;
import com.esferalia.aon.gwt.payroll.shared.Events;
import com.esferalia.aon.gwt.payroll.shared.EventsWorkplace;
import com.esferalia.aon.gwt.payroll.shared.Extra;
import com.esferalia.aon.gwt.payroll.shared.Irpf;
import com.esferalia.aon.gwt.payroll.shared.Period;
import com.esferalia.aon.gwt.payroll.shared.Result;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfo;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfoFilter;
import com.esferalia.aon.gwt.payroll.shared.SalaryPreview;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceEmployees;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>EmployeesService</code>.
 */
public interface EmployeesServiceAsync extends AgreementServiceAsync, StatisticsServiceAsync,
		CalendarServiceAsync, EmployeeEventsServiceAsync{
	void getEnterprise(String domain, String user, AsyncCallback<Enterprise> callback)
			throws IllegalArgumentException;

	void getEnterprises(String domain, String user, AsyncCallback<Enterprise[]> callback)
			throws IllegalArgumentException;


	void getAvailableDeductions(String domain, int employeeId,
			AsyncCallback<List<Deduction>> callback)
			throws IllegalArgumentException;

	void getAvailableBonuses(String domain, int employeeId,
			AsyncCallback<List<Bonus>> callback)
			throws IllegalArgumentException;

	void getWorkplaceCosts(String domain, int workplaceId, AsyncCallback<List<Cost>> callback)
			throws IllegalArgumentException;

	void getEnterpriseCosts(String domain, int enterpriseId, AsyncCallback<List<Cost>> callback)
			throws IllegalArgumentException;

	void getSalaries(String domain, Employee employee, AsyncCallback<List<Salary>> callback)
			throws IllegalArgumentException;

	void getIrpfs(String domain, Employee employee, AsyncCallback<List<Irpf>> callback)
			throws IllegalArgumentException;

	void getExtras(String domain, List<Employee> employees, AsyncCallback<List<Extra>> callback)
			throws IllegalArgumentException;

	void getCostReceiptHTML(String domain, Cost cost, Salary.Type types[], int zoom,
			AsyncCallback<String> callback) throws IllegalArgumentException;

	void getIrpfReceiptHTML(String domain, Irpf irpf, int zoom, AsyncCallback<String> callback)
			throws IllegalArgumentException;

	void getSalaryReceiptHTML(String domain, Cost cost, Salary.Type types[], int zoom,
			AsyncCallback<String> callback) throws IllegalArgumentException;

	void getSalaryReceiptHTML(String domain, Salary salary, int zoom,
			AsyncCallback<String> callback) throws IllegalArgumentException;

	void saveSalaryDraft(String domain, SalaryDraft salaryDraft, AsyncCallback<Void> callback)
			throws IllegalArgumentException;

	void saveSalary(String domain, SalaryDraft salaryDraft, AsyncCallback<SalaryDraft> callback)
			throws IllegalArgumentException;

	void saveSalary(String domain, SalaryDraft salaryDraft, Date sections[], AsyncCallback<SalaryDraft> callback)
			throws IllegalArgumentException;

	void calculateIrpf(String domain, SalaryDraft salaryDraft, AsyncCallback<Double> callback)
			throws IllegalArgumentException;

	void calculateSalaryDraft(String domain, SalaryDraft salaryDraft,
			AsyncCallback<SalaryDraft> callback)
			throws IllegalArgumentException;

	void calculateSalaryDraft(String domain, SalaryDraft salaryDraft, 
			Date sections [],
			AsyncCallback<SalaryDraft> callback)
			throws IllegalArgumentException;

	void calculateSalaryDraft4Dummies(String domain, SalaryDraft salaryDraft,
			AsyncCallback<SalaryDraft> callback)
			throws IllegalArgumentException;

	void eval(String domain, String expression, SalaryDraft salaryDraft,
			AsyncCallback<List<Result>> callback)
			throws IllegalArgumentException, EvalException;

	void getContext(String domain, SalaryDraft salaryDraft,
			AsyncCallback<ContextDescriptor> callback)
			throws IllegalArgumentException;

	void getSalaryDraftReceipt(String domain, SalaryDraft salaryDraft, String mime,
			AsyncCallback<String> callback) throws IllegalArgumentException;

	void getSalaryDraftReceiptHTML(String domain, SalaryDraft salaryPreview, int zoom,
			AsyncCallback<String> callback) throws IllegalArgumentException;

	void getIrpfDraftReceipt(String domain, SalaryDraft salaryDraft, String mime,
			AsyncCallback<String> callback) throws IllegalArgumentException;

	void getIrpfDraftReceiptHTML(String domain, SalaryDraft salaryPreview, int zoom,
			AsyncCallback<String> callback) throws IllegalArgumentException;

	void getSalaryPreviewReceiptHTML(String domain, SalaryPreview salaryPreview, int zoom,
			AsyncCallback<String> callback) throws IllegalArgumentException;

	void getEmployee(String domain, int employeeId, AsyncCallback<Employee> callback) 
			throws IllegalArgumentException;

	void getEmployees(String domain, int workplaceId, Date endDate, String pattern,
			int offset, int limit, AsyncCallback<List<Employee>> callback)
			throws IllegalArgumentException;

	void getTrashEmployees(String domain, int workplaceId,
			AsyncCallback<List<Employee>> callback)
			throws IllegalArgumentException;

	void saveEvents(String domain, Events events, Date startDate, Date endDate,
			AsyncCallback<Void> callback) throws IllegalArgumentException;

	void getEvents(String domain, Integer workplaceId, Date startDate, Date endDate,
			int offset, int limit, String names[],
			AsyncCallback<Events> callback) throws IllegalArgumentException;

	void getAvailPeriod(String domain, Integer workplaceId, String name,
			AsyncCallback<Period> callback) throws IllegalArgumentException;

	void getEventsVariables(String domain, Integer workplaceId, Integer agreementId,
			Date startDate, Date endDate,
			AsyncCallback<Map<String, String>> callback)
			throws IllegalArgumentException;
	
	void getWorkplaceEventsVariables(String currentDomainName, Integer workplaceId, Integer agreementId, Date startDate,
			Date endDate, AsyncCallback<Map<String, String>> callback) throws IllegalArgumentException;
	
	void getEmployeeEventsVariables(String domain, Integer employeeId, Date startDate, Date endDate,
			AsyncCallback<ContextDescriptor> callback);

	void getVariables(String domain, SalaryDraft salaryDraft, Date startDate, Date endDate,
			String names[], AsyncCallback<List<Variable>> callback)
			throws IllegalArgumentException;

	void delete(String domain, Salary salaries[], AsyncCallback<Void> callback)
			throws IllegalArgumentException;

	void pasteContract(String domain, int workplaceId, int contractId, String document,
			Date startDate, Date endDate, boolean check,
			AsyncCallback<Employee> callback) throws IllegalArgumentException;

	void moveContractId(String domain, Employee employee, AsyncCallback<Void> callback)
			throws IllegalArgumentException;

	void deleteContract(String domain, Employee employee, AsyncCallback<Void> callback)
			throws IllegalArgumentException;

	void insertPerson(String domain, Employee employee, AsyncCallback<Void> callback)
			throws IllegalArgumentException;

	void getAvaiableEmployees(String domain, AsyncCallback<Map<String, String>> callback)
			throws IllegalArgumentException;

	void getWorkplaceEmployees(String domain, Integer workplaceId, AsyncCallback<WorkplaceEmployees> asyncCallback);

	void setEventsWorkplace(String domain, EventsWorkplace updateEventsWorkplace, AsyncCallback<EventsWorkplace> asyncCallback);

	void getEmployeeInfoDataBase(String domain, Integer employeeContract, AsyncCallback<EmployeeContractInfo> asyncCallback);

	void setEmployeeInfoDataBase(String domain, EmployeeContractInfo new_employeeContractData,
			AsyncCallback<EmployeeContractInfo> asyncCallback);

	void createEmployeeContract(String domain, EmployeeContractInfo employeeContractData,
			AsyncCallback<EmployeeContractInfo> asyncCallback);

	void getWorkplaceEmployeesEvents(String currentDomainName, Integer workplaceId,
			AsyncCallback<WorkplaceEmployees> callback);

	void getEmployeeEventsByContract(String currentDomainName, Integer contractId,
			ArrayList<String> employeeContractVariablesDB, AsyncCallback<EmployeeEventsData> callback);

	void setEmployeeAFIChanges(String currentDomainName, Integer contractId, Date newDate, boolean isChangeContract, String tc2,
			boolean isQuoteContract, Integer quoteGroup, boolean isOcupationContract, String ocupation,
			AsyncCallback<String> callback);

	void resetCalendar(String currentDomainName, Integer employeeId, AsyncCallback<String> callback);

	void getEmployeeSalaries(String currentDomainName, Integer employeeId, AsyncCallback<List<SalaryInfo>> callback);

	void deleteSalariesDB(String currentDomainName, ArrayList<Integer> ids, AsyncCallback<String> callback);

	void getWorkplaceSalaries(String currentDomainName, Integer workplaceId, AsyncCallback<List<SalaryInfo>> callback);

	void getWorkplaceActiveEmployees(String currentDomainName, Integer workplaceId,
			AsyncCallback<WorkplaceEmployees> callback);

	void getEnterpriseSalaries(String currentDomainName, Integer enterpriseId, AsyncCallback<List<SalaryInfo>> callback);

	void getEnterpriseActiveEmployees(String currentDomainName, Integer enterpriseId, AsyncCallback<List<EmployeeInfo>> callback);

	void getFilterSalaries(String currentDomainName, SalaryInfoFilter filter, AsyncCallback<List<SalaryInfo>> callback);

	void getEmployeeCalendarInfo(String currentDomainName, Integer contractId, AsyncCallback<EmployeeCalendarInfo> callback);

	void setEmployeeCalendarInfo(String currentDomainName, Integer contractId, EmployeeCalendarInfo employeeCalendarInfo, AsyncCallback<String> callback);

	void resetEmployeeCalendarInfo(String currentDomainName, Integer contractId, AsyncCallback<String> callback);

	void setEmployeeEvents(String currentDomainName, Integer idEmployee, EmployeeEventsData employeeEventsData,
			AsyncCallback<EmployeeEventsData> callback);

	void setEventsDraft(String currentDomainName, ArrayList<EventEmployee> eventEmployees,
			AsyncCallback<ArrayList<EventEmployee>> callback);

	void generateCertifaca2(String currentDomainName, SalaryDraft salaryDraft, AsyncCallback<String> callback);
	
	void getIdc(String domain, Integer contractId, Date date, AsyncCallback<String> callback);
}
