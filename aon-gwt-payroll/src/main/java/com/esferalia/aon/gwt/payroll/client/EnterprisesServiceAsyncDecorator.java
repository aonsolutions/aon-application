/**
 * 
 */
package com.esferalia.aon.gwt.payroll.client;

import java.util.List;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.Bonus;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.Cost;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author rtrepiana
 * 
 */
public class EnterprisesServiceAsyncDecorator implements
		EnterprisesServiceAsync {

	private EnterprisesServiceAsync enterprisesServiceAsync;

	public EnterprisesServiceAsyncDecorator(
			EnterprisesServiceAsync enterprisesServiceAsync) {
		this.enterprisesServiceAsync = enterprisesServiceAsync;
	}
	
	@Override
	public void getDomain(AsyncCallback<Integer> callback) {
		AON.start();
		enterprisesServiceAsync
				.getDomain(new AsyncCallbackWrapper<Integer>(
						callback));
	}

	@Override
	public void getContext(AsyncCallback<ContextDescriptor> callback) {
		AON.start();
		enterprisesServiceAsync
				.getContext(new AsyncCallbackWrapper<ContextDescriptor>(
						callback));
	}

	@Override
	public void saveBonusConcept(Bonus bonus, AsyncCallback<Bonus> callback) {
		AON.start();
		enterprisesServiceAsync.saveBonusConcept(bonus,
				new AsyncCallbackWrapper<Bonus>(callback));
	}

	@Override
	public void savePaymentConcept(Payment payment,
			AsyncCallback<Payment> callback) {
		AON.start();
		enterprisesServiceAsync.savePaymentConcept(payment,
				new AsyncCallbackWrapper<Payment>(callback));
	}

	@Override
	public void deleteDeductionConcept(Deduction deduction,
			AsyncCallback<Void> callback) {
		AON.start();
		enterprisesServiceAsync.deleteDeductionConcept(deduction,
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void deleteBonusConcept(Bonus bonus, AsyncCallback<Void> callback) {
		AON.start();
		enterprisesServiceAsync.deleteBonusConcept(bonus,
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void deletePaymentConcept(Payment payment,
			AsyncCallback<Void> callback) {
		AON.start();
		enterprisesServiceAsync.deletePaymentConcept(payment,
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void saveDeductionConcept(Deduction deduction,
			AsyncCallback<Deduction> callback) {
		AON.start();
		enterprisesServiceAsync.saveDeductionConcept(deduction,
				new AsyncCallbackWrapper<Deduction>(callback));
	}

	@Override
	public void getEnterprises(int offset, int limit,
			AsyncCallback<List<Enterprise>> callback) {
		AON.start();
		enterprisesServiceAsync.getEnterprises(offset, limit,
				new AsyncCallbackWrapper<List<Enterprise>>(callback));
	}

	@Override
	public void getAgreements(int offset, int limit,
			AsyncCallback<List<Agreement>> callback) {
		AON.start();
		enterprisesServiceAsync.getAgreements(offset, limit,
				new AsyncCallbackWrapper<List<Agreement>>(callback));

	}

	@Override
	public void getBonusConcepts(int offset, int limit,
			AsyncCallback<List<Bonus>> callback) {
		AON.start();
		enterprisesServiceAsync.getBonusConcepts(offset, limit,
				new AsyncCallbackWrapper<List<Bonus>>(callback));
	}

	@Override
	public void getDeductionConcepts(int offset, int limit,
			AsyncCallback<List<Deduction>> callback) {
		AON.start();
		enterprisesServiceAsync.getDeductionConcepts(offset, limit,
				new AsyncCallbackWrapper<List<Deduction>>(callback));
	}

	@Override
	public void getPaymentConcepts(int offset, int limit,
			AsyncCallback<List<Payment>> callback) {
		AON.start();
		enterprisesServiceAsync.getPaymentConcepts(offset, limit,
				new AsyncCallbackWrapper<List<Payment>>(callback));
	}

	@Override
	public void getEnterprisesCosts(List<Integer> enterpriseIds,
			AsyncCallback<List<Cost>> callback) {
		AON.start();
		enterprisesServiceAsync.getEnterprisesCosts(enterpriseIds,
				new AsyncCallbackWrapper<List<Cost>>(callback));
	}

}
