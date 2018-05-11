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
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.SalaryPreview;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceEmployees;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("employees")
public interface EmployeesService extends RemoteService, CalendarService, EmployeeEventsService,
		StatisticsService, GPSReportsService, AgreementService {
	
	Enterprise getEnterprise() throws IllegalArgumentException;

	Enterprise[] getEnterprises() throws IllegalArgumentException;

	List<Deduction> getAvailableDeductions(int employeeId)
			throws IllegalArgumentException;

	List<Bonus> getAvailableBonuses(int employeeId)
			throws IllegalArgumentException;

	List<Cost> getWorkplaceCosts(int workplaceId)
			throws IllegalArgumentException;

	List<Cost> getEnterpriseCosts(int enterpriseId)
			throws IllegalArgumentException;

	List<Salary> getSalaries(Employee employee) throws IllegalArgumentException;

	List<Irpf> getIrpfs(Employee employee) throws IllegalArgumentException;

	List<Extra> getExtras(List<Employee> employees) throws IllegalArgumentException;

	String getCostReceiptHTML(Cost cost, Salary.Type types[], int zoom)
			throws IllegalArgumentException;

	String getIrpfReceiptHTML(Irpf irpf, int zoom)
			throws IllegalArgumentException;

	String getSalaryReceiptHTML(Cost cost, Salary.Type types[], int zoom)
			throws IllegalArgumentException;

	String getSalaryReceiptHTML(Salary salary, int zoom)
			throws IllegalArgumentException;

	void saveSalaryDraft(SalaryDraft salaryDraft)
			throws IllegalArgumentException;

	SalaryDraft saveSalary(SalaryDraft salaryDraft)
			throws IllegalArgumentException;
	
	SalaryDraft saveSalary(SalaryDraft salaryDraft, Date sections [])
			throws IllegalArgumentException;

	ContextDescriptor getContext(SalaryDraft salaryDraft)
			throws IllegalArgumentException;

	List<Result> eval(String expression, SalaryDraft salaryDraft)
			throws IllegalArgumentException, EvalException;

	Double calculateIrpf(SalaryDraft salaryDraft)
			throws IllegalArgumentException;

	SalaryDraft calculateSalaryDraft(SalaryDraft salaryDraft)
			throws IllegalArgumentException;

	SalaryDraft calculateSalaryDraft(SalaryDraft salaryDraft, Date sections [])
			throws IllegalArgumentException;

	SalaryDraft calculateSalaryDraft4Dummies(SalaryDraft salaryDraft)
			throws IllegalArgumentException;

	String getSalaryDraftReceipt(SalaryDraft salaryDraft, String mime)
			throws IllegalArgumentException;

	String getSalaryDraftReceiptHTML(SalaryDraft salaryDraft, int zoom)
			throws IllegalArgumentException;

	String getIrpfDraftReceipt(SalaryDraft salaryDraft, String mime)
			throws IllegalArgumentException;

	String getIrpfDraftReceiptHTML(SalaryDraft salaryDraft, int zoom)
			throws IllegalArgumentException;

	String getSalaryPreviewReceiptHTML(SalaryPreview salaryPreview, int zoom)
			throws IllegalArgumentException;

	void insertPerson(Employee employee) throws IllegalArgumentException;

	Employee getEmployee(int employeeId) throws IllegalArgumentException;

	List<Employee> getEmployees(int workplaceId, Date endDate, String pattern,
			int offset, int limit) throws IllegalArgumentException;

	List<Employee> getTrashEmployees(int workplaceId)
			throws IllegalArgumentException;

	void saveEvents(Events events, Date startDate, Date endDate)
			throws IllegalArgumentException;

	Events getEvents(Integer workplaceId, Date startDate, Date endDate,
			int offset, int limit, String names[])
			throws IllegalArgumentException;

	List<Variable> getVariables(SalaryDraft salaryDraft, Date startDate, Date endDate, String names[])
			throws IllegalArgumentException;

	Period getAvailPeriod(Integer workplaceId, String name)
			throws IllegalArgumentException;

	Map<String, String> getEventsVariables(Integer workplaceId,
			Integer agreementId, Date startDate, Date endDate)
			throws IllegalArgumentException;
	
	ContextDescriptor getEmployeeEventsVariables(Integer employeeId,
			Date startDate, Date endDate)
			throws IllegalArgumentException;

	Employee pasteContract(int workplaceId, int contractId, String document,
			Date startDate, Date endDate, boolean check)
			throws IllegalArgumentException;

	void moveContractId(Employee employee) throws IllegalArgumentException;

	void deleteContract(Employee employee) throws IllegalArgumentException;

	void delete(Salary salaries[]) throws IllegalArgumentException;

	Map<String, String> getAvaiableEmployees() throws IllegalArgumentException;

	WorkplaceEmployees getWorkplaceEmployees(Integer workplaceId);

	EventsWorkplace setEventsWorkplace(
			com.esferalia.aon.gwt.payroll.shared.EventsWorkplace updateEventsWorkplace);

	EmployeeInfoDataBase getEmployeeInfoDataBase(Integer id);

	EmployeeInfoDataBase setEmployeeInfoDataBase(EmployeeInfoDataBase newEmployeeInfo);

}
