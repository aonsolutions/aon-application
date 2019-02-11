package com.esferalia.aon.gwt.payroll.client;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.payroll.shared.ActivitiesCCC;
import com.esferalia.aon.gwt.payroll.shared.ActivityInfo;
import com.esferalia.aon.gwt.payroll.shared.AgrarianJourney;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.Bonus;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.Cost;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseInfo;
import com.esferalia.aon.gwt.payroll.shared.Extra;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceInfo;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>EnterprisesService</code>.
 */
public interface EnterprisesServiceAsync {
	void getDomain(String domain, AsyncCallback<Integer> callback);
	void getContext(String domain, AsyncCallback<ContextDescriptor> callback);
	void saveBonusConcept(String domain, Bonus bonus, AsyncCallback<Bonus> callback);
	void savePaymentConcept(String domain, Payment payment, AsyncCallback<Payment> callback);
	void saveDeductionConcept(String domain, Deduction deduction, AsyncCallback<Deduction> callback);
	void deleteBonusConcept(String domain, Bonus bonus, AsyncCallback<Void> callback);
	void deletePaymentConcept(String domain, Payment payment, AsyncCallback<Void> callback);
	void deleteDeductionConcept(String domain, Deduction deduction, AsyncCallback<Void> callback);
	void deleteAgreement(String domain, Agreement agreement, AsyncCallback<Void> callback);
	void updateAgreementId(String domain, Agreement agreement, AsyncCallback<Void> callback);
	void copyAgreement(String domain, Agreement agreement, AsyncCallback<Agreement> callback);
	void getWorkplacesExtras(String domain, List<Integer> workplaceIds, AsyncCallback<List<Extra>> callback);
	void getAgreements(String domain, int offset , int limit, AsyncCallback<List<Agreement>> callback);
	void getEnterprises(String domain, String user,int offset , int limit, AsyncCallback<List<Enterprise>> callback);
	void getEnterprisesCosts(String domain, List<Integer> enterpriseIds, AsyncCallback<List<Cost>> callback);
	void getBonusConcepts(String domain, int offset , int limit, AsyncCallback<List<Bonus>> callback);
	void getCCCEmployees(String domain, Date month, List<Integer> cccIds, AsyncCallback<List<Employee>> callback ); 
	void getPaymentConcepts(String domain, int offset , int limit, AsyncCallback<List<Payment>> callback);
	void getDeductionConcepts(String domain, int offset , int limit, AsyncCallback<List<Deduction>> callback);
	void moveAgreement2Parent(String domain, Agreement agreement, AsyncCallback<Void> callback);
	void getParentDomain(String domain, AsyncCallback<Integer> callback);
	void getWorkplaceInfo(String domain, Integer workplaceId, AsyncCallback<WorkplaceInfo> asyncCallback);
	void setWorkplaceInfo(String domain, WorkplaceInfo workplaceInfo, AsyncCallback<WorkplaceInfo> asyncCallback);
	void getWorkplaces(Integer workplaceId, String domain, AsyncCallback<List<Workplace>> asyncCallback);
	void getActivitiesCCC(Integer workplaceId, String currentDomainName, AsyncCallback<ActivitiesCCC> asyncCallback);
	void getActivityInfoDataBase(Integer activityId, String domain, AsyncCallback<ActivityInfo> asyncCallback);
	void updateActivityInfoDataBase(ActivityInfo activityInfo, String domain, AsyncCallback<ActivityInfo> asyncCallback);
	void createActivityInfoDataBase(ActivityInfo activityInfo, String domain, AsyncCallback<ActivityInfo> asyncCallback);
	void getCNAE2009(String domain, AsyncCallback<Map<String, String>> asyncCallback);
	void getEnterpiseAddresses(Integer enterpriseId, String domain, AsyncCallback<Map<Integer, String>> asyncCallback);
	void getEnterpiseCalendars(Integer enterpriseId, String domain, AsyncCallback<Map<Integer, String>> asyncCallback);
	void getEnterpiseActivities(Integer enterpriseId, String domain, AsyncCallback<Map<Integer, String>> asyncCallback);
	void createWorkplaceInfo(WorkplaceInfo workplaceInfo, Integer enterpriseId, String domain, AsyncCallback<WorkplaceInfo> asyncCallback);
	void getEnterpiseScopes(Integer enterpriseId, String domain, AsyncCallback<Map<Integer, String>> asyncCallback);
	void getEnterpriseInfo(Integer enterpriseId, String domain, AsyncCallback<EnterpriseInfo> asyncCallback);
	void updateEnterprise(EnterpriseInfo enterpriseInfo, String domain, AsyncCallback<EnterpriseInfo> asyncCallback);
	void getAgrarianJourney(Date startDate, Date endDate, Integer enterprise_ccc, String domain, AsyncCallback<Map<Integer, List<AgrarianJourney>>> asyncCallback);

}
