package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.payroll.shared.AgreementDraft;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.Cost;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.EvalException;
import com.esferalia.aon.gwt.payroll.shared.Events;
import com.esferalia.aon.gwt.payroll.shared.Irpf;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Period;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.SalaryPreview;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("employees")
public interface EmployeesService extends RemoteService {
	Enterprise getEnterprise() throws IllegalArgumentException;

	Enterprise[] getEnterprises() throws IllegalArgumentException;

	List<Payment> getAvailablePayments(int employeeId)
			throws IllegalArgumentException;

	List<Cost> getWorkplaceCosts(int workplaceId)
			throws IllegalArgumentException;

	List<Cost> getEnterpriseCosts(int enterpriseId)
			throws IllegalArgumentException;

	List<Salary> getSalaries(Employee employee) throws IllegalArgumentException;

	List<Irpf> getIrpfs(Employee employee) throws IllegalArgumentException;

	String getCostReceiptHTML(Cost cost, Salary.Type types [], int zoom)
			throws IllegalArgumentException;

	String getIrpfReceiptHTML(Irpf irpf, int zoom)
			throws IllegalArgumentException;

	String getSalaryReceiptHTML(Cost cost, Salary.Type types [], int zoom)
			throws IllegalArgumentException;

	String getSalaryReceiptHTML(Salary salary, int zoom)
			throws IllegalArgumentException;

	void saveSalaryDraft(SalaryDraft salaryDraft)
			throws IllegalArgumentException;

	SalaryDraft saveSalary(SalaryDraft salaryDraft)
			throws IllegalArgumentException;

	AgreementDraft saveAgreementDraft(AgreementDraft agreementDraft)
			throws IllegalArgumentException;

	ContextDescriptor getContext(SalaryDraft salaryDraft)
			throws IllegalArgumentException;

	Double eval(String expression, SalaryDraft salaryDraft)
			throws IllegalArgumentException, EvalException;

	SalaryDraft calculateSalaryDraft(SalaryDraft salaryDraft)
			throws IllegalArgumentException;

	AgreementDraft calculateAgreementDraft(AgreementDraft agreementDraft)
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

	List<Employee> getEmployees(int workplaceId, Date endDate, String pattern,
			int offset, int limit) throws IllegalArgumentException;

	void saveEvents(Events events, Date startDate, Date endDate)
			throws IllegalArgumentException;

	Events getEvents(Integer workplaceId, Date startDate, Date endDate,
			int offset, int limit, String names[])
			throws IllegalArgumentException;

	Period getAvailPeriod(Integer workplaceId, String name)
			throws IllegalArgumentException;

	Map<String, String> getEventsVariables(Integer workplaceId,
			Integer agreementId, Date startDate, Date endDate)
			throws IllegalArgumentException;

	void delete(Salary salaries [])
			throws IllegalArgumentException;

}
