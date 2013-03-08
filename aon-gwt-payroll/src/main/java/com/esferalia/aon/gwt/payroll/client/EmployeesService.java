package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.List;

import com.esferalia.aon.gwt.payroll.shared.Cost;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.Payment;
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
	List<Payment> getPaymentConcepts() throws IllegalArgumentException;
	List<Cost> getWorkplaceCosts(int workplaceId) throws IllegalArgumentException;
	List<Cost> getEnterpriseCosts(int enterpriseId) throws IllegalArgumentException;
	List<Salary> getSalaries(Employee employee) throws IllegalArgumentException;
	String getCostReceiptHTML(Cost cost, int zoom) throws IllegalArgumentException;
	String getSalaryReceiptHTML(Cost cost, int zoom) throws IllegalArgumentException;
	String getSalaryReceiptHTML(Salary salary, int zoom) throws IllegalArgumentException;
	void saveSalaryDraft(SalaryDraft salaryDraft) throws IllegalArgumentException;
	SalaryDraft saveSalary(SalaryDraft salaryDraft ) throws IllegalArgumentException;
	SalaryDraft calculateSalaryDraft(SalaryDraft salaryDraft ) throws IllegalArgumentException;
	String getSalaryDraftReceipt(SalaryDraft salaryDraft, String mime) throws IllegalArgumentException;
	String getSalaryDraftReceiptHTML(SalaryDraft salaryDraft, int zoom) throws IllegalArgumentException;
	String getSalaryPreviewReceiptHTML(SalaryPreview salaryPreview, int zoom) throws IllegalArgumentException;
	List<Employee> getEmployees(int workplaceId, Date endDate, String pattern, int offset, int limit ) throws IllegalArgumentException;
}
