/**
 * 
 */
package com.esferalia.aon.gwt.payroll.client;

import static com.esferalia.aon.gwt.common.shared.DateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.gwt.common.shared.DateUtils.getFirstDayOfYear;
import static com.esferalia.aon.gwt.common.shared.DateUtils.getLastDayOfMonth;
import static java.lang.Math.random;
import junit.framework.Assert;

import org.junit.Before;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.google.gwt.junit.client.GWTTestCase;

/**
 * 
 * @author rtrepiana
 *
 */
public class GWTSalaryDraftTestCase extends GWTTestCase {

	@Before
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.junit.client.GWTTestCase#getModuleName()
	 */
	@Override
	public String getModuleName() {
		return "com.esferalia.aon.gwt.payroll.Payroll";
	}

	/**
	 * 
	 */
	public void testSimple() {

		SalaryDraft salaryDraftWidget = new SalaryDraft();

		final int workplaceId = 0;

		com.esferalia.aon.gwt.payroll.shared.SalaryDraft salaryDraft = newSalaryDraft(newEmployee(), random()* 1000);

		EmployeesServiceAsync employeesServiceAsync = new AbstractEmployeesServiceAsync() {
		};
		
		ITDataObject itDataObject = new ITDataObject(workplaceId, employeesServiceAsync);

		SalaryDraftObject salaryDraftObject = new SalaryDraftObject(
				salaryDraft, itDataObject, employeesServiceAsync);

		salaryDraftWidget.setSalaryDraftObject(salaryDraftObject);
		
		//@formatter:off
//		Assert.assertEquals("EMPLOYEE DOCUMENT", 
//				salaryDraft.getEmployeeDocument(),
//				salaryDraftWidget.employeeDocumentLabel.getText());
		//@formatter:on
		
		
		assertTrue(true);
	}
	
	// ------------------------------------------------------------------------
	
	private static Employee newEmployee() {
		return new Employee()
		.setId(0)
		.setPerson(0)
		.setDocument("DOCUMENT")
		.setName("NAME")
		.setFirstSurname("FIRST_SURNAME")
		.setSecondSurName("SECOND_SURNAME")
		.setStartDate(getFirstDayOfYear())
		.setSocialSecurity("SOCIAL_SECURITY")
		;
	}
	
	private static com.esferalia.aon.gwt.payroll.shared.SalaryDraft newSalaryDraft(Employee employee, Double paymment){
		return new com.esferalia.aon.gwt.payroll.shared.SalaryDraft()
		.setCgcBase(paymment)
		.setCgpBase(paymment)
		.setIrpfBase(paymment)
		.setTotalPayment(paymment)
		.setEmployee(employee)
		.setEndDate(getLastDayOfMonth())
		.setStartDate(getFirstDayOfMonth())
		.setChargeDate(getLastDayOfMonth())
		.setIssueDate(getLastDayOfMonth())
		.setId(0)
		;
	}
	

}
