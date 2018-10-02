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
import com.esferalia.aon.gwt.payroll.shared.Extra;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceInfo;
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
	public void deleteAgreement(Agreement agreement,
			AsyncCallback<Void> callback) {
		AON.start();
		enterprisesServiceAsync.deleteAgreement(agreement,  
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
	public void getWorkplacesExtras(List<Integer> workplaceIds, AsyncCallback<List<Extra>> callback) {
		AON.start();
		enterprisesServiceAsync.getWorkplacesExtras(workplaceIds,
				new AsyncCallbackWrapper<List<Extra>>(callback));
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

	@Override
	public void updateAgreementId(Agreement agreement, 
			AsyncCallback<Void> callback) {
		AON.start();
		enterprisesServiceAsync.updateAgreementId(agreement, 
				new AsyncCallbackWrapper<Void>(callback));
		
	}
	
	@Override
	public void copyAgreement(Agreement agreement,
			AsyncCallback<Agreement> callback) {
		AON.start();
		enterprisesServiceAsync.copyAgreement(agreement, 
				new AsyncCallbackWrapper<Agreement>(callback));
	}
	
	@Override
	public void moveAgreement2Parent(Agreement agreement,
			AsyncCallback<Void> callback) {
		AON.start();
		enterprisesServiceAsync.moveAgreement2Parent(agreement, new 
				AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void getParentDomain(AsyncCallback<Integer> callback) {
		AON.start();
		enterprisesServiceAsync.getParentDomain(new 
				AsyncCallbackWrapper<Integer>(callback));
		
	}

	@Override
	public void getWorkplaceInfo(Integer workplaceId, AsyncCallback<WorkplaceInfo> callback) {
		AON.start();
		enterprisesServiceAsync.getWorkplaceInfo(workplaceId,
				new AsyncCallbackWrapper<WorkplaceInfo>(callback));
	}

	@Override
	public void setWorkplaceInfo(WorkplaceInfo workplaceInfo, AsyncCallback<WorkplaceInfo> callback) {
		AON.start();
		enterprisesServiceAsync.setWorkplaceInfo(workplaceInfo,
				new AsyncCallbackWrapper<WorkplaceInfo>(callback));	
	}
}
