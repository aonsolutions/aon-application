package com.esferalia.aon.gwt.payroll.client;

import java.util.List;

import com.esferalia.aon.gwt.payroll.shared.ActivitiesCCC;
import com.esferalia.aon.gwt.payroll.shared.ActivityInfo;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.Bonus;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.Cost;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.Extra;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceInfo;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class DomainEnterprisesServiceAsync {
	
	private EnterprisesServiceAsync enterprisesServiceAsync;
	
	
	public static DomainEnterprisesServiceAsync newInstance() {
		EnterprisesServiceAsync enterprisesServiceASync = GWT
				.create(EnterprisesService.class);
		EnterprisesServiceAsync enterprisesServiceAsyncDecorator = 
				new EnterprisesServiceAsyncDecorator(
				enterprisesServiceASync);
		return new DomainEnterprisesServiceAsync(enterprisesServiceAsyncDecorator);
	}

	private DomainEnterprisesServiceAsync(EnterprisesServiceAsync enterprisesServiceAsync) {
		this.enterprisesServiceAsync = enterprisesServiceAsync;
	}

	public void getDomain(AsyncCallback<Integer> callback) {
		enterprisesServiceAsync.getDomain(getCurrentDomainName(), callback);
	}

	public void getContext(AsyncCallback<ContextDescriptor> callback) {
		enterprisesServiceAsync.getContext(getCurrentDomainName(), callback);
	}

	public void saveBonusConcept(Bonus bonus, AsyncCallback<Bonus> callback) {
		enterprisesServiceAsync.saveBonusConcept(getCurrentDomainName(), bonus, callback);
	}

	public void savePaymentConcept(Payment payment, AsyncCallback<Payment> callback) {
		enterprisesServiceAsync.savePaymentConcept(getCurrentDomainName(), payment, callback);
	}

	public void saveDeductionConcept(Deduction deduction, AsyncCallback<Deduction> callback) {
		enterprisesServiceAsync.saveDeductionConcept(getCurrentDomainName(), deduction, callback);
	}

	public void deleteBonusConcept(Bonus bonus, AsyncCallback<Void> callback) {
		enterprisesServiceAsync.deleteBonusConcept(getCurrentDomainName(), bonus, callback);
	}

	public void deletePaymentConcept(Payment payment, AsyncCallback<Void> callback) {
		enterprisesServiceAsync.deletePaymentConcept(getCurrentDomainName(), payment, callback);
	}

	public void deleteDeductionConcept(Deduction deduction, AsyncCallback<Void> callback) {
		enterprisesServiceAsync.deleteDeductionConcept(getCurrentDomainName(), deduction, callback);
	}

	public void deleteAgreement(Agreement agreement, AsyncCallback<Void> callback) {
		enterprisesServiceAsync.deleteAgreement(getCurrentDomainName(), agreement, callback);
	}

	public void updateAgreementId(Agreement agreement, AsyncCallback<Void> callback) {
		enterprisesServiceAsync.updateAgreementId(getCurrentDomainName(), agreement, callback);
	}

	public void copyAgreement(Agreement agreement, AsyncCallback<Agreement> callback) {
		enterprisesServiceAsync.copyAgreement(getCurrentDomainName(), agreement, callback);
	}

	public void getWorkplacesExtras(List<Integer> workplaceIds, AsyncCallback<List<Extra>> callback) {
		enterprisesServiceAsync.getWorkplacesExtras(getCurrentDomainName(), workplaceIds, callback);
	}

	public void getAgreements(int offset, int limit, AsyncCallback<List<Agreement>> callback) {
		enterprisesServiceAsync.getAgreements(getCurrentDomainName(), offset, limit, callback);
	}

	public void getEnterprises(int offset, int limit, AsyncCallback<List<Enterprise>> callback) {
		enterprisesServiceAsync.getEnterprises(getCurrentDomainName(), getCurrentUser(), offset, limit, callback);
	}

	public void getEnterprisesCosts(List<Integer> enterpriseIds, AsyncCallback<List<Cost>> callback) {
		enterprisesServiceAsync.getEnterprisesCosts(getCurrentDomainName(), enterpriseIds, callback);
	}

	public void getBonusConcepts(int offset, int limit, AsyncCallback<List<Bonus>> callback) {
		enterprisesServiceAsync.getBonusConcepts(getCurrentDomainName(), offset, limit, callback);
	}

	public void getPaymentConcepts(int offset, int limit, AsyncCallback<List<Payment>> callback) {
		enterprisesServiceAsync.getPaymentConcepts(getCurrentDomainName(), offset, limit, callback);
	}

	public void getDeductionConcepts(int offset, int limit, AsyncCallback<List<Deduction>> callback) {
		enterprisesServiceAsync.getDeductionConcepts(getCurrentDomainName(), offset, limit, callback);
	}

	public void moveAgreement2Parent(Agreement agreement, AsyncCallback<Void> callback) {
		enterprisesServiceAsync.moveAgreement2Parent(getCurrentDomainName(), agreement, callback);
	}

	public void getParentDomain(AsyncCallback<Integer> callback) {
		enterprisesServiceAsync.getParentDomain(getCurrentDomainName(), callback);
	}

	public void getWorkplaceInfo(Integer workplaceId, AsyncCallback<WorkplaceInfo> asyncCallback) {
		enterprisesServiceAsync.getWorkplaceInfo(getCurrentDomainName(), workplaceId, asyncCallback);
	}

	public void setWorkplaceInfo(WorkplaceInfo workplaceInfo,
			AsyncCallback<WorkplaceInfo> asyncCallback) {
		enterprisesServiceAsync.setWorkplaceInfo(getCurrentDomainName(), workplaceInfo, asyncCallback);
	}
	
	public void getWorkplaces(Integer workplaceId, AsyncCallback<List<Workplace>> asyncCallback) {
		enterprisesServiceAsync.getWorkplaces(workplaceId, getCurrentDomainName(), asyncCallback);
	}
	
	public void getActivitiesCCC(Integer workplaceId, AsyncCallback<ActivitiesCCC> asyncCallback) {
		enterprisesServiceAsync.getActivitiesCCC(workplaceId, getCurrentDomainName(), asyncCallback);
	}
	
	public void getActivityInfoDataBase(Integer activityId, AsyncCallback<ActivityInfo> asyncCallback) {
		enterprisesServiceAsync.getActivityInfoDataBase(activityId, getCurrentDomainName(), asyncCallback);
	}
	
	// ----------------------------------------------------------------- static
	
	private static String getCurrentUser() {
		return Wnd.getCurrentUser();
	}

	private static String getCurrentDomainName() {
		return Wnd.getCurrentDomainNameURL();
	}

}
