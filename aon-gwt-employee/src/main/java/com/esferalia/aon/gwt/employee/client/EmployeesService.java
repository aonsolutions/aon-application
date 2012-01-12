package com.esferalia.aon.gwt.employee.client;

import java.sql.SQLException;
import java.util.List;

import com.esferalia.aon.gwt.employee.shared.Employee;
import com.esferalia.aon.gwt.employee.shared.Enterprise;
import com.esferalia.aon.gwt.employee.shared.Salary;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("employees")
public interface EmployeesService extends RemoteService {
	Enterprise getEnterprise() throws IllegalArgumentException;
	List<Salary> getSalaries(Employee employee) throws IllegalArgumentException;
	String getSalaryReceiptHTML(Salary salary, float zoomRatio) throws IllegalArgumentException;
}
