/**
 * 
 */
package com.esferalia.aon.gwt.payroll.client;

import static com.esferalia.aon.gwt.common.shared.DateUtils.getFirstDayOfYear;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsUpdate;
import com.esferalia.aon.gwt.payroll.shared.Extra;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceEmployees;
import com.google.gwt.core.client.GWT;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.rpc.AsyncCallback;

import junit.framework.Assert;

/**
 * 
 * @author rtrepiana
 *
 */
public class GWTAgreementDraftTestCase extends GWTTestCase {

	private static final class EmployeesServiceImpl extends
			AbstractEmployeesServiceAsync {
		@Override
		public void getContext(String domain,
				com.esferalia.aon.gwt.payroll.shared.AgreementDraft agreementDraft,
				int levelId,
				AsyncCallback<ContextDescriptor> callback)
				throws IllegalArgumentException {
			ContextDescriptor contextDescriptor = new ContextDescriptor();
			callback.onSuccess(contextDescriptor);
		}

		@Override
		public void getAvailablePayments(String domain, int employeeId,
				AsyncCallback<List<Payment>> callback)
				throws IllegalArgumentException {
			List<Payment> concepts = new ArrayList<Payment>();
			callback.onSuccess(concepts);
		}

		@Override
		public void calculateAgreementDraft(String domain,
				com.esferalia.aon.gwt.payroll.shared.AgreementDraft draft,
				AsyncCallback<com.esferalia.aon.gwt.payroll.shared.AgreementDraft> callback)
				throws IllegalArgumentException {
			try {
				Set<Payment> dbPayments = getDbPayments(1);
				Set<Extra> dbExtras = getDbExtras(dbPayments);
				
				Set<Extra> allExtras = new HashSet<Extra>(dbExtras);
				Set<Payment> allPayments = new HashSet<Payment>(dbPayments);
				

				draft.setPayments(allPayments);
				draft.setExtras(allExtras);
				
				callback.onSuccess(draft);
			} catch (Throwable t) {
				Assert.fail(t.getMessage());
			}
		}

		

		@Override
		public void setEmployeeEvents(int contract, EmployeeEventsUpdate updateInfo,
				AsyncCallback<EmployeeEventsUpdate> callback) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void getEmployeeEventsVariables(Integer employeeId, Date startDate, Date endDate,
				AsyncCallback<ContextDescriptor> callback) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void getEmployeeEvents(int contract, ArrayList<String> employeeContractVariables,
				AsyncCallback<EmployeeEventsData> callback) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void getWorkplaceEmployees(Integer workplaceId, AsyncCallback<WorkplaceEmployees> asyncCallback) {
			// TODO Auto-generated method stub
			
		}

		
	}

	@Before
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.junit.client.GWTTestCase#getModuleName()
	 */
	@Override
	public String getModuleName() {
		return "com.esferalia.aon.gwt.payroll.TestingPayroll";
	}

	public void testEmptyAgreement() {
		AgreementDraft agreementDraftWidget = new AgreementDraft();

		final com.esferalia.aon.gwt.payroll.shared.AgreementDraft agreementDraft = new com.esferalia.aon.gwt.payroll.shared.AgreementDraft();

		// only id, domain and draft dates,n no more
		agreementDraft.setId(666);
		agreementDraft.setDomain(999);
		agreementDraft.setStartDate(DateUtils.getFirstDayOfMonth());
		agreementDraft.setEndDate(DateUtils.getLastDayOfMonth());

		final AgreementDraftObject agreementDraftObject = new AgreementDraftObject(
				6969, "TODO: Domain Name", agreementDraft, new AbstractEmployeesServiceAsync() {

					@Override
					public void getContext(
							String domain,
							com.esferalia.aon.gwt.payroll.shared.AgreementDraft agreementDraft,
							int levelId,
							AsyncCallback<ContextDescriptor> callback)
							throws IllegalArgumentException {
						ContextDescriptor contextDescriptor = new ContextDescriptor();
						callback.onSuccess(contextDescriptor);
					}

					@Override
					public void getAvailablePayments(String domain,int employeeId,
							AsyncCallback<List<Payment>> callback)
							throws IllegalArgumentException {
						List<Payment> concepts = new ArrayList<Payment>();
						concepts.add(newConcept(13, 999, null));
						callback.onSuccess(concepts);
					}

					@Override
					public void calculateAgreementDraft(String domain,
							com.esferalia.aon.gwt.payroll.shared.AgreementDraft agreementDraft,
							AsyncCallback<com.esferalia.aon.gwt.payroll.shared.AgreementDraft> callback)
							throws IllegalArgumentException {

						callback.onSuccess(agreementDraft);
					}

					

					@Override
					public void setEmployeeEvents(int contract, EmployeeEventsUpdate updateInfo,
							AsyncCallback<EmployeeEventsUpdate> callback) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void getEmployeeEventsVariables(Integer employeeId, Date startDate, Date endDate,
							AsyncCallback<ContextDescriptor> callback) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void getEmployeeEvents(int contract, ArrayList<String> employeeContractVariables,
							AsyncCallback<EmployeeEventsData> callback) {
						// TODO Auto-generated method stub
						
					}

					@Override
					public void getWorkplaceEmployees(Integer workplaceId,
							AsyncCallback<WorkplaceEmployees> asyncCallback) {
						// TODO Auto-generated method stub
						
					}

					
				});

		agreementDraftWidget.setAgreementDraftObject(agreementDraftObject);

		// only two rows , one for headers and another one for add a new payment
		Assert.assertEquals(2, agreementDraftWidget.paymentsTable.getRowCount());

		// only two rows , one for headers and another one for add a new extra
		Assert.assertEquals(2, agreementDraftWidget.extrasTable.getRowCount());

		// only three rows , one for headers and another for add a new level
		Assert.assertEquals(2, agreementDraftWidget.salaryTable.getRowCount());

	}

	public void testOverridePaymentsII() {

		com.esferalia.aon.gwt.payroll.shared.AgreementDraft agreementDraft = new com.esferalia.aon.gwt.payroll.shared.AgreementDraft();
		agreementDraft.setId(1);
		agreementDraft.setDomain(2);
		agreementDraft.setStartDate(DateUtils.getFirstDayOfMonth());
		agreementDraft.setEndDate(DateUtils.getLastDayOfMonth());
		agreementDraft.setDescription(String.valueOf(agreementDraft.getId()));
		
		EmployeesServiceAsync employeesService= GWT
				.create(EmployeesService.class);
		final AgreementDraftObject agreementDraftObject = new AgreementDraftObject(
				6969, "TODO: Domain Name", agreementDraft, employeesService);

		AgreementDraft agreementDraftWidget = new AgreementDraft();
		// agreementDraftWidget.setAgreementDraftObject(agreementDraftObject);

		
		/* since RPC calls are asynchronous, we will need to wait 
		 for a response after this test method returns. This line 
		 tells the test runner to wait up to 10 seconds 
		 before timing out. */
		// delayTestFinish(10000);

		// assertEquals(1+5+1, agreementDraftWidget.paymentsTable.getRowCount());

		// TextBox descriptionBox = (TextBox)
		// agreementDraftWidget.paymentsTable.getWidget(2, 2);
		// descriptionBox.setValue("OVERRIDE", true);

		// Set<Payment> draftPayments =
		// agreementDraftWidget.agreementDraftObject.getAgreementDraft().getDraftPayments();
		// assertEquals(2, draftPayments.size());

	}

	// ------------------------------------------------------------------------

	private static int newId() {
		return (int) (Math.round(Math.random() * Integer.MAX_VALUE));
	}

	private static Payment newConcept(int id, int domain, String expression) {
		return newConcept(id, domain, Payment.Type.CRA_0001, expression, "_P",
				"_P");
	}

	private static Payment newPayment(int id, int domain, Payment concept) {
		Payment payment = new Payment();

		payment.setConceptId(concept.getId());
		payment.setType(concept.getType());
		payment.setName(concept.getName());
		payment.setDescription(concept.getDescription());
		payment.setExpression(concept.getExpression());
		payment.setIrpfExpression(concept.getIrpfExpression());
		payment.setQuoteExpression(concept.getQuoteExpression());

		payment.setSalaryType(Salary.Type.SALARY);
		payment.setStartDate(getFirstDayOfYear(new Date()));

		return payment;
	}


	private static Payment newConcept(int id, int domain, Payment.Type type,
			String expression, String irpfExpression, String quoteExpression) {
		Payment payment = new Payment();
		payment.setId(id);
		payment.setType(type);
		payment.setDomain(domain);
		payment.setName("P0"+Integer.toString(id));
		payment.setExpression(expression);
		payment.setIrpfExpression(irpfExpression);
		payment.setQuoteExpression(quoteExpression);
		payment.setDescription(type.getDescription());
		return payment;
	}
	
	private static Set<Extra> getDbExtras(Set<Payment> payments) {
		Set<Extra> dbExtras = new HashSet<Extra>();

		for (Payment payment : payments) {
			if ( payment.getSalaryType() != Salary.Type.EXTRA )
				continue;
			Extra extra = new Extra();
			extra.setPaymentId(payment.getId());
			extra.setDomain(payment.getDomain());
			// d/M 
			extra.setStartDate("01/01");
			extra.setEndDate("31/12");
			extra.setIssueDate("15/" + payment.getMonth()+1);
		}
		
		return dbExtras;
		
	}

	private static Set<Payment> getDbPayments(int domain) {
		Set<Payment> dbPayments = new HashSet<Payment>();
		
		Payment salaryConcept = newConcept(1, 0, "/*user*/SALARIO_MENSUAL/**/* DIAS_TRABAJADOS / DIAS_MES");
		Payment plusConcept = newConcept(2, 0, "/*user*//**/* DIAS_TRABAJADOS / DIAS_MES");
		Payment extraConcept = newConcept(3, 0, Payment.Type.CRA_0004, null, "_P", "_P/12");
		
		Payment salary = newPayment(1, domain, salaryConcept);
		dbPayments.add(salary);
		
		Payment plusOne = newPayment(2, domain, plusConcept);
		plusOne.setExpression("/*user*/PLUS_ONE/**/* DIAS_TRABAJADOS / DIAS_MES");
		dbPayments.add(plusOne);

		Payment plusTwo = newPayment(3, domain, plusConcept);
		plusTwo.setExpression("/*user*/PLUS_TWO/**/* DIAS_TRABAJADOS / DIAS_MES");
		dbPayments.add(plusTwo);

		Payment extraDecember = newPayment(4, domain, extraConcept);
		extraDecember.setExpression("P01 + P02");
		extraDecember.setMonth((short)11);
		extraDecember.setSalaryType(Salary.Type.EXTRA);
		dbPayments.add(extraDecember);

		Payment extraJuly = newPayment(5, domain, extraConcept);
		extraJuly.setExpression("P01 + P02");
		extraJuly.setMonth((short)6);
		extraJuly.setSalaryType(Salary.Type.EXTRA);
		dbPayments.add(extraJuly);
		
		return dbPayments;
	}

}
