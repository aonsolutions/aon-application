/**
 * 
 */
package com.esferalia.aon.gwt.payroll.client;

import static com.esferalia.aon.gwt.common.shared.DateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.gwt.common.shared.DateUtils.getFirstDayOfYear;
import static com.esferalia.aon.gwt.common.shared.DateUtils.getLastDayOfMonth;
import static java.lang.Math.random;

import org.junit.Before;

import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsUpdate;
import com.esferalia.aon.gwt.payroll.shared.Salary.Type;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.rpc.AsyncCallback;

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

		com.esferalia.aon.gwt.payroll.shared.SalaryDraft salaryDraft = newSalaryDraft(
				newEmployee(), random() * 1000);

		EmployeesServiceAsync employeesServiceAsync = new AbstractEmployeesServiceAsync() {
			@Override
			public void calculateSalaryDraft(
					com.esferalia.aon.gwt.payroll.shared.SalaryDraft salaryDraft,
					AsyncCallback<com.esferalia.aon.gwt.payroll.shared.SalaryDraft> callback)
					throws IllegalArgumentException {

				for (int i = 0; i < 15; i++)
					salaryDraft.addVariable("_" + i, i, getFirstDayOfMonth(),
							getLastDayOfMonth(), Scope.SALARY, "" + i, null);

				
				
				callback.onSuccess(salaryDraft);
			}

			@Override
			public void getEmployeeEvents(int contract, AsyncCallback<EmployeeEventsData> callback) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setEmployeeEvents(int contract, EmployeeEventsUpdate updateInfo,
					AsyncCallback<EmployeeEventsUpdate> callback) {
				// TODO Auto-generated method stub
				
			}
		};

		ITDataObject itDataObject = new ITDataObject(workplaceId,
				employeesServiceAsync);

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
		return new Employee().setId(0).setPerson(0).setDocument("DOCUMENT")
				.setName("NAME").setFirstSurname("FIRST_SURNAME")
				.setSecondSurName("SECOND_SURNAME")
				.setStartDate(getFirstDayOfYear())
				.setSocialSecurity("SOCIAL_SECURITY");
	}

	private static com.esferalia.aon.gwt.payroll.shared.SalaryDraft newSalaryDraft(
			Employee employee, Double paymment) {
		return new com.esferalia.aon.gwt.payroll.shared.SalaryDraft()
				.setCgcBase(paymment).setCgpBase(paymment)
				.setIrpfBase(paymment).setTotalPayment(paymment)
				.setEmployee(employee).setEndDate(getLastDayOfMonth())
				.setStartDate(getFirstDayOfMonth())
				.setChargeDate(getLastDayOfMonth())
				.setIssueDate(getLastDayOfMonth()).setId(0)
				.setType(Type.SALARY);
	}

}
