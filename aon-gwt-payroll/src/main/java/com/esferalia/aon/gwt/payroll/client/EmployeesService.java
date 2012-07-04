package com.esferalia.aon.gwt.payroll.client;

import java.util.List;

import com.esferalia.aon.gwt.payroll.shared.Cost;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("employees")
public interface EmployeesService extends RemoteService {
	Enterprise getEnterprise() throws IllegalArgumentException;
	List<Salary> getSalaries(Employee employee) throws IllegalArgumentException;
	String getCostReceiptHTML(Cost cost, int zoom) throws IllegalArgumentException;
	String getSalaryReceiptHTML(Cost cost, int zoom) throws IllegalArgumentException;
	String getSalaryReceiptHTML(Salary salary, int zoom) throws IllegalArgumentException;
}
