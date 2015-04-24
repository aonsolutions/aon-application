/**
 * 
 */
package com.esferalia.aon.gwt.payroll.client;

import static com.esferalia.aon.gwt.common.shared.DateUtils.getFirstDayOfYear;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import junit.framework.Assert;

import org.junit.Before;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.shared.AgreementDraft.SalaryTable;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.Extra;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.TextBox;

/**
 * 
 * @author rtrepiana
 *
 */
public class GWTAgreementDraftTestCase extends GWTTestCase {

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

	public void testEmptyAgreement() {
		AgreementDraft agreementDraftWidget = new AgreementDraft();

		final com.esferalia.aon.gwt.payroll.shared.AgreementDraft agreementDraft = new com.esferalia.aon.gwt.payroll.shared.AgreementDraft();

		// only id, domain and draft dates,n no more
		agreementDraft.setId(666);
		agreementDraft.setDomain(999);
		agreementDraft.setStartDate(DateUtils.getFirstDayOfMonth());
		agreementDraft.setEndDate(DateUtils.getLastDayOfMonth());

		final AgreementDraftObject agreementDraftObject = new AgreementDraftObject(
				6969, agreementDraft,
				new AbstractEmployeesServiceAsync() {
					
					@Override
					public void getContext(
							com.esferalia.aon.gwt.payroll.shared.AgreementDraft agreementDraft,
							int levelId,
							AsyncCallback<ContextDescriptor> callback)
							throws IllegalArgumentException {
						ContextDescriptor contextDescriptor = new ContextDescriptor();
						callback.onSuccess(contextDescriptor);
					}
					@Override
					public void getAvailablePayments(int employeeId,
							AsyncCallback<List<Payment>> callback)
							throws IllegalArgumentException {
						List<Payment> concepts = new ArrayList<Payment>();
						concepts.add(newConcept(13,999,null));
						callback.onSuccess(concepts);
					}
					
					@Override
					public void calculateAgreementDraft(
							com.esferalia.aon.gwt.payroll.shared.AgreementDraft agreementDraft,
							AsyncCallback<com.esferalia.aon.gwt.payroll.shared.AgreementDraft> callback)
							throws IllegalArgumentException {
						callback.onSuccess(agreementDraft);
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

	/**
	 * 
	 */
	public void testOverridePaymentsII() {
		AgreementDraft agreementDraftWidget = new AgreementDraft();

		final com.esferalia.aon.gwt.payroll.shared.AgreementDraft agreementDraft = new com.esferalia.aon.gwt.payroll.shared.AgreementDraft();

		agreementDraft.setId(666);
		agreementDraft.setDomain(999);
		agreementDraft.setDescription("666");
		agreementDraft.setStartDate(DateUtils.getFirstDayOfMonth());
		agreementDraft.setEndDate(DateUtils.getLastDayOfMonth());

		final AgreementDraftObject agreementDraftObject = new AgreementDraftObject(
				6969, agreementDraft,
				new AbstractEmployeesServiceAsync() {
					
					@Override
					public void getContext(
							com.esferalia.aon.gwt.payroll.shared.AgreementDraft agreementDraft,
							int levelId,
							AsyncCallback<ContextDescriptor> callback)
							throws IllegalArgumentException {
						ContextDescriptor contextDescriptor = new ContextDescriptor();
						callback.onSuccess(contextDescriptor);
					}
					@Override
					public void getAvailablePayments(int employeeId,
							AsyncCallback<List<Payment>> callback)
							throws IllegalArgumentException {
						List<Payment> concepts = new ArrayList<Payment>();
						concepts.add(newConcept(13,999,null));
						callback.onSuccess(concepts);
					}
					
					@Override
					public void calculateAgreementDraft(
							com.esferalia.aon.gwt.payroll.shared.AgreementDraft agreementDraft,
							AsyncCallback<com.esferalia.aon.gwt.payroll.shared.AgreementDraft> callback)
							throws IllegalArgumentException {
						Set<Payment> payments = new HashSet<Payment>();
						payments.add(newPayment(1313, 999, 13, "1313 * DIAS_TRABAJADOS / DIAS_MES"));
						payments.add(newPayment(3131, 999, 13, "3131 * DIAS_TRABAJADOS / DIAS_MES"));
						agreementDraft.setPayments(payments);
						callback.onSuccess(agreementDraft);
					}
				});
		
		
		agreementDraftWidget.setAgreementDraftObject(agreementDraftObject);
		
		assertEquals(4, agreementDraftWidget.paymentsTable.getRowCount());
		
		TextBox descriptionBox  = (TextBox) agreementDraftWidget.paymentsTable.getWidget(2, 2);
		descriptionBox.setValue("OVERRIDE", true);
		
		Set<Payment> draftPayments = agreementDraftWidget.agreementDraftObject.getAgreementDraft().getDraftPayments();
		//assertEquals(2, draftPayments.size());
		
	}

	// ------------------------------------------------------------------------
	
	
	private static int newId() {
		return (int)(Math.round(Math.random() * Integer.MAX_VALUE));
	}
	
	private static Payment newConcept(int id, int domain, String expression) {
		Payment payment = new Payment();
		payment.setId(id);
		payment.setDomain(domain);
		payment.setName(Integer.toString(id));
		payment.setIrpfExpression("_P");
		payment.setQuoteExpression("_P");
		payment.setType(Payment.Type.CRA_0001);
		payment.setExpression(expression);
		return payment;
	}
	private static Payment newPayment(int id, int domain, Integer concept, String expression) {
		Payment payment = newConcept(id, domain, expression);
		payment.setConceptId(concept);
		payment.setSalaryType(Salary.Type.SALARY);
		payment.setStartDate(getFirstDayOfYear(new Date()));
		return payment;
	}

}
