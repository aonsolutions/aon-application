package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.payroll.shared.AFIChanges;
import com.esferalia.aon.gwt.payroll.shared.ActivitiesCCC;
import com.esferalia.aon.gwt.payroll.shared.ActivityInfo;
import com.esferalia.aon.gwt.payroll.shared.AgrarianJourney;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.Bonus;
import com.esferalia.aon.gwt.payroll.shared.CRA;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.Cost;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseInfo;
import com.esferalia.aon.gwt.payroll.shared.Extra;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Peculiarities;
import com.esferalia.aon.gwt.payroll.shared.SSBonusData;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceInfo;
import com.google.gwt.core.client.GWT;
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

	public void getCCCEmployees(Date month, List<Integer> cccIds, AsyncCallback<List<Employee>> callback) {
		enterprisesServiceAsync.getCCCEmployees(getCurrentDomainName(), month,  cccIds, callback);
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
	
	public void updateActivityInfoDataBase(ActivityInfo activityInfo, AsyncCallback<ActivityInfo> asyncCallback) {
		enterprisesServiceAsync.updateActivityInfoDataBase(activityInfo, getCurrentDomainName(), asyncCallback);
	}
	
	public void createActivityInfoDataBase(ActivityInfo activityInfo, AsyncCallback<ActivityInfo> asyncCallback) {
		enterprisesServiceAsync.createActivityInfoDataBase(activityInfo, getCurrentDomainName(), asyncCallback);
	}
	
	public void getCNAE2009(AsyncCallback<Map<String, String>> asyncCallback) {
		enterprisesServiceAsync.getCNAE2009(getCurrentDomainName(), asyncCallback);
	}
	
	public void getEnterpiseAddresses(Integer enterpriseId, AsyncCallback<Map<Integer, String>> asyncCallback) {
		enterprisesServiceAsync.getEnterpiseAddresses(enterpriseId, getCurrentDomainName(), asyncCallback);
	}
	
	public void getEnterpiseCalendars(Integer enterpriseId, AsyncCallback<Map<Integer, String>> asyncCallback) {
		enterprisesServiceAsync.getEnterpiseCalendars(enterpriseId, getCurrentDomainName(), asyncCallback);
	}
	
	public void getEnterpiseActivities(Integer enterpriseId, AsyncCallback<Map<Integer, String>> asyncCallback) {
		enterprisesServiceAsync.getEnterpiseActivities(enterpriseId, getCurrentDomainName(), asyncCallback);
	}
	
	public void createWorkplaceInfo(WorkplaceInfo workplaceInfo, Integer enterpriseId, AsyncCallback<WorkplaceInfo> asyncCallback) {
		enterprisesServiceAsync.createWorkplaceInfo(workplaceInfo, enterpriseId, getCurrentDomainName(), asyncCallback);
	}
	
	public void getEnterpiseScopes(Integer enterpriseId, AsyncCallback<Map<Integer, String>> asyncCallback) {
		enterprisesServiceAsync.getEnterpiseScopes(enterpriseId, getCurrentDomainName(), asyncCallback);
	}

	public void getEnterpriseInfo(Integer enterpriseId, AsyncCallback<EnterpriseInfo> asyncCallback) {
		enterprisesServiceAsync.getEnterpriseInfo(enterpriseId, getCurrentDomainName(), asyncCallback);
	}
	
	public void updateEnterprise(EnterpriseInfo enterpriseInfo, AsyncCallback<EnterpriseInfo> asyncCallback) {
		enterprisesServiceAsync.updateEnterprise(enterpriseInfo, getCurrentDomainName(), asyncCallback);
	}
	
	public void getEmployeeAgrarianJourney(Date startDate, Date endDate, Integer enterprise_ccc, AsyncCallback<Map<Integer, List<AgrarianJourney>>> asyncCallback) {
		enterprisesServiceAsync.getAgrarianJourney(startDate, endDate, enterprise_ccc, getCurrentDomainName(), asyncCallback);
	}
	
	public void getCRAs(AsyncCallback<List<CRA>> asyncCallback) {
		enterprisesServiceAsync.getCRAs(getCurrentDomainName(), asyncCallback);
	}
	
	public void createNewCRA(Integer enterpriseId, String enterpriseName, long startDate, long endDate, String ccc,
			Integer cccId, String type, AsyncCallback<String> asyncCallback) {
		enterprisesServiceAsync.createNewCRA(getCurrentDomainName(), enterpriseId, enterpriseName, startDate, endDate, ccc, cccId, type, asyncCallback);
	}
	
	public void deleteCRA(Integer code, AsyncCallback<String> asyncCallback) {
		enterprisesServiceAsync.deleteCRA(getCurrentDomainName(), code, asyncCallback);
	}
	
	public void getEmployeePeculiarities(Integer contractId, AsyncCallback<Peculiarities> asyncCallback) {
		enterprisesServiceAsync.getEmployeePeculiarities(getCurrentDomainName(), contractId, asyncCallback);
	}
	
	public void setEmployeePeculiarities(Integer contractId, Peculiarities peculiarities, AsyncCallback<String> asyncCallback) {
		enterprisesServiceAsync.setEmployeePeculiarities(getCurrentDomainName(), contractId, peculiarities, asyncCallback);
	}
	
	// SS_BONUS DIALOG
	public void getEmployeeSSBonuses(Integer contractId, AsyncCallback<List<SSBonusData>> asyncCallback) {
		enterprisesServiceAsync.getEmployeeSSBonuses(getCurrentDomainName(), contractId, asyncCallback);
	}
	
	public void getBonusConcepts(AsyncCallback<List<SSBonusData>> asyncCallback) {
		enterprisesServiceAsync.getBonusConcepts(getCurrentDomainName(), asyncCallback);
	}
	
	public void setEmployeeSSBonuses(Integer contractId, List<SSBonusData> ssBonuses, AsyncCallback<List<SSBonusData>> asyncCallback) {
		enterprisesServiceAsync.setEmployeeSSBonuses(getCurrentDomainName(),  contractId, ssBonuses, asyncCallback);
	}
	
	public void setEmployeeAFIChanges(Integer contractId, AFIChanges afiChangesMap, AsyncCallback<String> asyncCallback) {
		enterprisesServiceAsync.setEmployeeAFIChanges(getCurrentDomainName(), contractId, afiChangesMap, asyncCallback);
	}
	
	public void getEmployeeAFIChanges(Integer contractId, AsyncCallback<AFIChanges> asyncCallback) {
		enterprisesServiceAsync.getEmployeeAFIChanges(getCurrentDomainName(), contractId, asyncCallback);
	}
	
	// ----------------------------------------------------------------- static
	
	private static String getCurrentUser() {
		return Wnd.getCurrentUser();
	}

	private static String getCurrentDomainName() {
		return Wnd.getCurrentDomainNameURL();
	}


}
