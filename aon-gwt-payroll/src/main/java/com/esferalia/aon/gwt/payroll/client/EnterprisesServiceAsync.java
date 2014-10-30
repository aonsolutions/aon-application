package com.esferalia.aon.gwt.payroll.client;

import java.util.List;

import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.Bonus;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.Cost;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>EnterprisesService</code>.
 */
public interface EnterprisesServiceAsync {
	void getDomain(AsyncCallback<Integer> callback);
	void getContext(AsyncCallback<ContextDescriptor> callback);
	void saveBonusConcept(Bonus bonus, AsyncCallback<Bonus> callback);
	void savePaymentConcept(Payment payment, AsyncCallback<Payment> callback);
	void saveDeductionConcept(Deduction deduction, AsyncCallback<Deduction> callback);
	void deleteBonusConcept(Bonus bonus, AsyncCallback<Void> callback);
	void deletePaymentConcept(Payment payment, AsyncCallback<Void> callback);
	void deleteDeductionConcept(Deduction deduction, AsyncCallback<Void> callback);
	void getAgreements(int offset , int limit, AsyncCallback<List<Agreement>> callback);
	void getEnterprises(int offset , int limit, AsyncCallback<List<Enterprise>> callback);
	void getEnterprisesCosts(List<Integer> enterpriseIds, AsyncCallback<List<Cost>> callback);
	void getBonusConcepts(int offset , int limit, AsyncCallback<List<Bonus>> callback);
	void getPaymentConcepts(int offset , int limit, AsyncCallback<List<Payment>> callback);
	void getDeductionConcepts(int offset , int limit, AsyncCallback<List<Deduction>> callback);

}
