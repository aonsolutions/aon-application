package com.esferalia.aon.gwt.payroll.client;

import java.util.List;

import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.Bonus;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.Cost;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.Extra;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceInfo;
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
	void deleteAgreement(Agreement agreement, AsyncCallback<Void> callback);
	void updateAgreementId(Agreement agreement, AsyncCallback<Void> callback);
	void copyAgreement(Agreement agreement, AsyncCallback<Agreement> callback);
	void getWorkplacesExtras(List<Integer> workplaceIds, AsyncCallback<List<Extra>> callback);
	void getAgreements(int offset , int limit, AsyncCallback<List<Agreement>> callback);
	void getEnterprises(int offset , int limit, AsyncCallback<List<Enterprise>> callback);
	void getEnterprisesCosts(List<Integer> enterpriseIds, AsyncCallback<List<Cost>> callback);
	void getBonusConcepts(int offset , int limit, AsyncCallback<List<Bonus>> callback);
	void getPaymentConcepts(int offset , int limit, AsyncCallback<List<Payment>> callback);
	void getDeductionConcepts(int offset , int limit, AsyncCallback<List<Deduction>> callback);
	void moveAgreement2Parent(Agreement agreement, AsyncCallback<Void> callback);
	void getParentDomain(AsyncCallback<Integer> callback);
	void getWorkplaceInfo(Integer workplaceId, AsyncCallback<WorkplaceInfo> asyncCallback);

}
