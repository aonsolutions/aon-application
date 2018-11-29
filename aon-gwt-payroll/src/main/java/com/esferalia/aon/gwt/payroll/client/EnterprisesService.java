package com.esferalia.aon.gwt.payroll.client;

import java.util.List;
import java.util.Map;

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
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("enterprises")
public interface EnterprisesService extends RemoteService {
	
	Integer getDomain(String domain);

	ContextDescriptor getContext(String domain);

	Bonus saveBonusConcept(String domain, Bonus bonus);

	Payment savePaymentConcept(String domain, Payment payment);

	Deduction saveDeductionConcept(String domain, Deduction deduction);
	
	/**
	 * 
	 * @param agreement
	 * @param value: if value > 0 move to Agreements. Else, move to Trash
	 */	
	void updateAgreementId(String domain, Agreement agreement);

	void deleteBonusConcept(String domain, Bonus bonus);

	void deletePaymentConcept(String domain, Payment payment);

	void deleteDeductionConcept(String domain, Deduction deduction);
	
	void deleteAgreement(String domain, Agreement agreement);
	
	void moveAgreement2Parent(String domain, Agreement agreement);
	
	Agreement copyAgreement(String domain, Agreement agreement);

	List<Agreement> getAgreements(String domain, int offset, int limit) ;

	List<Enterprise> getEnterprises(String domain, String user, int offset, int limit) ;

	List<Bonus> getBonusConcepts(String domain, int offset, int limit) ;

	List<Payment> getPaymentConcepts(String domain, int offset, int limit) ;

	List<Deduction> getDeductionConcepts(String domain, int offset, int limit) ;
	
	List<Cost> getEnterprisesCosts(String domain, List<Integer> enterpriseIds); 
	
	List<Extra> getWorkplacesExtras(String domain, List<Integer> workplaceIds) ;

	Integer getParentDomain(String domain );

	WorkplaceInfo getWorkplaceInfo(String domain, Integer workplaceId);

	WorkplaceInfo setWorkplaceInfo(String domain, WorkplaceInfo workplaceInfo);

	List<Workplace> getWorkplaces(Integer workplaceId, String domain);

	ActivitiesCCC getActivitiesCCC(Integer workplaceId, String domain);

	ActivityInfo getActivityInfoDataBase(Integer activityId, String domain);

	ActivityInfo updateActivityInfoDataBase(ActivityInfo activityInfo, String domain);

	ActivityInfo createActivityInfoDataBase(ActivityInfo activityInfo, String domain);

	Map<String, String> getCNAE2009(String domain);

	Map<Integer, String> getEnterpiseAddresses(Integer enterpriseId, String domain);
	
	Map<Integer, String> getEnterpiseCalendars(Integer enterpriseId, String domain);
	
	Map<Integer, String> getEnterpiseActivities(Integer enterpriseId, String domain);

	WorkplaceInfo createWorkplaceInfo(WorkplaceInfo workplaceInfo, Integer enterpriseId, String domain);

	Map<Integer, String> getEnterpiseScopes(Integer enterpriseId, String domain);
	
}
