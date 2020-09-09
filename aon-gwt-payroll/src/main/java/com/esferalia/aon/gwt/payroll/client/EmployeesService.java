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
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("employees")
public interface EmployeesService  extends RemoteService, CalendarService, EmployeeEventsService,
		StatisticsService, AgreementService {
	
	Enterprise getEnterprise(String domain, String user) throws IllegalArgumentException;

	Enterprise[] getEnterprises(String domain, String user) throws IllegalArgumentException;

	List<Deduction> getAvailableDeductions(String domain, int employeeId)
			throws IllegalArgumentException;

	List<Bonus> getAvailableBonuses(String domain, int employeeId)
			throws IllegalArgumentException;

	List<Cost> getWorkplaceCosts(String domain, int workplaceId)
			throws IllegalArgumentException;

	List<Cost> getEnterpriseCosts(String domain, int enterpriseId)
			throws IllegalArgumentException;

	List<Salary> getSalaries(String domain, Employee employee) throws IllegalArgumentException;

	List<Irpf> getIrpfs(String domain, Employee employee) throws IllegalArgumentException;

	List<Extra> getExtras(String domain, List<Employee> employees) throws IllegalArgumentException;

	String getCostReceiptHTML(String domain, Cost cost, Salary.Type types[], int zoom)
			throws IllegalArgumentException;

	String getIrpfReceiptHTML(String domain, Irpf irpf, int zoom)
			throws IllegalArgumentException;

	String getSalaryReceiptHTML(String domain, Cost cost, Salary.Type types[], int zoom)
			throws IllegalArgumentException;

	String getSalaryReceiptHTML(String domain, Salary salary, int zoom)
			throws IllegalArgumentException;

	void saveSalaryDraft(String domain, SalaryDraft salaryDraft)
			throws IllegalArgumentException;

	SalaryDraft saveSalary(String domain, SalaryDraft salaryDraft)
			throws IllegalArgumentException;
	
	SalaryDraft saveSalary(String domain, SalaryDraft salaryDraft, Date sections [])
			throws IllegalArgumentException;

	ContextDescriptor getContext(String domain, SalaryDraft salaryDraft)
			throws IllegalArgumentException;

	List<Result> eval(String domain, String expression, SalaryDraft salaryDraft)
			throws IllegalArgumentException, EvalException;

	Double calculateIrpf(String domain, SalaryDraft salaryDraft)
			throws IllegalArgumentException;

	SalaryDraft calculateSalaryDraft(String domain, SalaryDraft salaryDraft)
			throws IllegalArgumentException;

	SalaryDraft calculateSalaryDraft(String domain, SalaryDraft salaryDraft, Date sections [])
			throws IllegalArgumentException;

	SalaryDraft calculateSalaryDraft4Dummies(String domain, SalaryDraft salaryDraft)
			throws IllegalArgumentException;

	String getSalaryDraftReceipt(String domain, SalaryDraft salaryDraft, String mime)
			throws IllegalArgumentException;

	String getSalaryDraftReceiptHTML(String domain, SalaryDraft salaryDraft, int zoom)
			throws IllegalArgumentException;

	String getIrpfDraftReceipt(String domain, SalaryDraft salaryDraft, String mime)
			throws IllegalArgumentException;

	String getIrpfDraftReceiptHTML(String domain, SalaryDraft salaryDraft, int zoom)
			throws IllegalArgumentException;

	String getSalaryPreviewReceiptHTML(String domain, SalaryPreview salaryPreview, int zoom)
			throws IllegalArgumentException;

	void insertPerson(String domain, Employee employee) throws IllegalArgumentException;

	Employee getEmployee(String domain, int employeeId) throws IllegalArgumentException;

	List<Employee> getEmployees(String domain, int workplaceId, Date endDate, String pattern,
			int offset, int limit) throws IllegalArgumentException;

	List<Employee> getTrashEmployees(String domain, int workplaceId)
			throws IllegalArgumentException;

	void saveEvents(String domain, Events events, Date startDate, Date endDate)
			throws IllegalArgumentException;

	Events getEvents(String domain, Integer workplaceId, Date startDate, Date endDate,
			int offset, int limit, String names[])
			throws IllegalArgumentException;

	List<Variable> getVariables(String domain, SalaryDraft salaryDraft, Date startDate, Date endDate, String names[])
			throws IllegalArgumentException;

	Period getAvailPeriod(String domain, Integer workplaceId, String name)
			throws IllegalArgumentException;

	Map<String, String> getEventsVariables(String domain, Integer workplaceId,
			Integer agreementId, Date startDate, Date endDate)
			throws IllegalArgumentException;
	
	Map<String, String> getWorkplaceEventsVariables(String currentDomainName, Integer workplaceId, Integer agreementId,
			Date startDate, Date endDate) throws IllegalArgumentException;
	
	ContextDescriptor getEmployeeEventsVariables(String domain, Integer employeeId,
			Date startDate, Date endDate)
			throws IllegalArgumentException;

	Employee pasteContract(String domain, int workplaceId, int contractId, String document,
			Date startDate, Date endDate, boolean check)
			throws IllegalArgumentException;

	void moveContractId(String domain, Employee employee) throws IllegalArgumentException;

	void deleteContract(String domain, Employee employee) throws IllegalArgumentException;

	void delete(String domain, Salary salaries[]) throws IllegalArgumentException;

	Map<String, String> getAvaiableEmployees(String domain) throws IllegalArgumentException;

	WorkplaceEmployees getWorkplaceEmployees(String domain, Integer workplaceId);

	EventsWorkplace setEventsWorkplace(String domain, 
			com.esferalia.aon.gwt.payroll.shared.EventsWorkplace updateEventsWorkplace);

	EmployeeContractInfo getEmployeeInfoDataBase(String domain, Integer employeeContract);

	EmployeeContractInfo setEmployeeInfoDataBase(String domain, EmployeeContractInfo new_employeeContractData);

	EmployeeContractInfo createEmployeeContract(String domain, EmployeeContractInfo employeeContractData);

	WorkplaceEmployees getWorkplaceEmployeesEvents(String currentDomainName, Integer workplaceId);

	EmployeeEventsData getEmployeeEventsByContract(String currentDomainName, Integer contractId,
			ArrayList<String> employeeContractVariablesDB);

	String setEmployeeAFIChanges(String currentDomainName, Integer contractId, Date newDate, boolean isChangeContract,
			String tc2, boolean isQuoteContract, Integer quoteGroup, boolean isOcupationContract, String ocupation);

	String resetCalendar(String currentDomainName, Integer employeeId);

	List<SalaryInfo> getEmployeeSalaries(String currentDomainName, Integer employeeId);

	String deleteSalariesDB(String currentDomainName, ArrayList<Integer> ids);

	List<SalaryInfo> getWorkplaceSalaries(String currentDomainName, Integer workplaceId);

	WorkplaceEmployees getWorkplaceActiveEmployees(String currentDomainName, Integer workplaceId);

	List<SalaryInfo> getEnterpriseSalaries(String currentDomainName, Integer enterpriseId);

	List<EmployeeInfo> getEnterpriseActiveEmployees(String currentDomainName, Integer enterpriseId);

	List<SalaryInfo> getFilterSalaries(String currentDomainName, SalaryInfoFilter filter);

	EmployeeCalendarInfo getEmployeeCalendarInfo(String currentDomainName, Integer contractId);

	String setEmployeeCalendarInfo(String currentDomainName, Integer contractId,
			EmployeeCalendarInfo employeeCalendarInfo);

	String resetEmployeeCalendarInfo(String currentDomainName, Integer contractId);

	EmployeeEventsData setEmployeeEvents(String currentDomainName, Integer idEmployee,
			EmployeeEventsData employeeEventsData);

	ArrayList<EventEmployee> setEventsDraft(String currentDomainName, ArrayList<EventEmployee> eventEmployees);

	String generateCertifaca2(String currentDomainName, SalaryDraft salaryDraft);
	
	String getIdc(String domain, Integer contractId, Date date);


}
