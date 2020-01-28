/**
 * 
 */
package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.common.client.AON;
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
import com.esferalia.aon.occam.api.model.MailAccount;
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
	public void getDomain(String domain, AsyncCallback<Integer> callback) {
		AON.start();
		enterprisesServiceAsync
				.getDomain(domain, new AsyncCallbackWrapper<Integer>(
						callback));
	}

	@Override
	public void getContext(String domain, AsyncCallback<ContextDescriptor> callback) {
		AON.start();
		enterprisesServiceAsync
				.getContext(domain, new AsyncCallbackWrapper<ContextDescriptor>(
						callback));
	}

	@Override
	public void saveBonusConcept(String domain, Bonus bonus, AsyncCallback<Bonus> callback) {
		AON.start();
		enterprisesServiceAsync.saveBonusConcept(domain, bonus,
				new AsyncCallbackWrapper<Bonus>(callback));
	}

	@Override
	public void savePaymentConcept(String domain, Payment payment,
			AsyncCallback<Payment> callback) {
		AON.start();
		enterprisesServiceAsync.savePaymentConcept(domain, payment,
				new AsyncCallbackWrapper<Payment>(callback));
	}

	@Override
	public void deleteDeductionConcept(String domain, Deduction deduction,
			AsyncCallback<Void> callback) {
		AON.start();
		enterprisesServiceAsync.deleteDeductionConcept(domain, deduction,
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void deleteBonusConcept(String domain, Bonus bonus, AsyncCallback<Void> callback) {
		AON.start();
		enterprisesServiceAsync.deleteBonusConcept(domain, bonus,
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void deletePaymentConcept(String domain, Payment payment,
			AsyncCallback<Void> callback) {
		AON.start();
		enterprisesServiceAsync.deletePaymentConcept(domain, payment,
				new AsyncCallbackWrapper<Void>(callback));
	}
	
	@Override
	public void deleteAgreement(String domain, Agreement agreement,
			AsyncCallback<Void> callback) {
		AON.start();
		enterprisesServiceAsync.deleteAgreement(domain, agreement,  
				new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void saveDeductionConcept(String domain, Deduction deduction,
			AsyncCallback<Deduction> callback) {
		AON.start();
		enterprisesServiceAsync.saveDeductionConcept(domain, deduction,
				new AsyncCallbackWrapper<Deduction>(callback));
	}

	@Override
	public void getEnterprises(String domain, String user, int offset, int limit,
			AsyncCallback<List<Enterprise>> callback) {
		AON.start();
		enterprisesServiceAsync.getEnterprises(domain, user, offset, limit,
				new AsyncCallbackWrapper<List<Enterprise>>(callback));
	}
	
	@Override
	public void getWorkplacesExtras(String domain, List<Integer> workplaceIds, AsyncCallback<List<Extra>> callback) {
		AON.start();
		enterprisesServiceAsync.getWorkplacesExtras(domain, workplaceIds,
				new AsyncCallbackWrapper<List<Extra>>(callback));
	}
	
	@Override
	public void getAgreement(String domain, Integer agreementId, AsyncCallback<Agreement> callback) {
		AON.start();
		enterprisesServiceAsync.getAgreement(domain, agreementId,
				new AsyncCallbackWrapper<Agreement>(callback));
	}
	

	@Override
	public void getAgreements(String domain, int offset, int limit,
			AsyncCallback<List<Agreement>> callback) {
		AON.start();
		enterprisesServiceAsync.getAgreements(domain, offset, limit,
				new AsyncCallbackWrapper<List<Agreement>>(callback));

	}

	@Override
	public void getBonusConcepts(String domain, int offset, int limit,
			AsyncCallback<List<Bonus>> callback) {
		AON.start();
		enterprisesServiceAsync.getBonusConcepts(domain, offset, limit,
				new AsyncCallbackWrapper<List<Bonus>>(callback));
	}

	@Override
	public void getDeductionConcepts(String domain, int offset, int limit,
			AsyncCallback<List<Deduction>> callback) {
		AON.start();
		enterprisesServiceAsync.getDeductionConcepts(domain, offset, limit,
				new AsyncCallbackWrapper<List<Deduction>>(callback));
	}

	@Override
	public void getPaymentConcepts(String domain, int offset, int limit,
			AsyncCallback<List<Payment>> callback) {
		AON.start();
		enterprisesServiceAsync.getPaymentConcepts(domain, offset, limit,
				new AsyncCallbackWrapper<List<Payment>>(callback));
	}

	@Override
	public void getEnterprisesCosts(String domain, List<Integer> enterpriseIds,
			AsyncCallback<List<Cost>> callback) {
		AON.start();
		enterprisesServiceAsync.getEnterprisesCosts(domain, enterpriseIds,
				new AsyncCallbackWrapper<List<Cost>>(callback));
	}

	@Override
	public void updateAgreementId(String domain, Agreement agreement, 
			AsyncCallback<Void> callback) {
		AON.start();
		enterprisesServiceAsync.updateAgreementId(domain, agreement, 
				new AsyncCallbackWrapper<Void>(callback));
		
	}
	
	@Override
	public void copyAgreement(String domain, Agreement agreement,
			AsyncCallback<Agreement> callback) {
		AON.start();
		enterprisesServiceAsync.copyAgreement(domain, agreement, 
				new AsyncCallbackWrapper<Agreement>(callback));
	}
	
	@Override
	public void moveAgreement2Parent(String domain, Agreement agreement,
			AsyncCallback<Void> callback) {
		AON.start();
		enterprisesServiceAsync.moveAgreement2Parent(domain, agreement, new 
				AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void getParentDomain(String domain, AsyncCallback<Integer> callback) {
		AON.start();
		enterprisesServiceAsync.getParentDomain(domain, new 
				AsyncCallbackWrapper<Integer>(callback));
		
	}

	@Override
	public void getWorkplaceInfo(String domain, Integer workplaceId, AsyncCallback<WorkplaceInfo> callback) {
		AON.start();
		enterprisesServiceAsync.getWorkplaceInfo(domain, workplaceId,
				new AsyncCallbackWrapper<WorkplaceInfo>(callback));
	}

	@Override
	public void setWorkplaceInfo(String domain, WorkplaceInfo workplaceInfo, AsyncCallback<WorkplaceInfo> callback) {
		AON.start();
		enterprisesServiceAsync.setWorkplaceInfo(domain, workplaceInfo,
				new AsyncCallbackWrapper<WorkplaceInfo>(callback));	
	}

	@Override
	public void getWorkplaces(Integer workplaceId, String doamin, AsyncCallback<List<Workplace>> callback) {
		AON.start();
		enterprisesServiceAsync.getWorkplaces(workplaceId, doamin,
				new AsyncCallbackWrapper<List<Workplace>>(callback));	
		
	}

	@Override
	public void getActivitiesCCC(Integer workplaceId, String domain,
			AsyncCallback<ActivitiesCCC> callback) {
		AON.start();
		enterprisesServiceAsync.getActivitiesCCC(workplaceId, domain,
				new AsyncCallbackWrapper<ActivitiesCCC>(callback));
	}

	@Override
	public void getActivityInfoDataBase(Integer activityId, String domain, AsyncCallback<ActivityInfo> callback) {
		AON.start();
		enterprisesServiceAsync.getActivityInfoDataBase(activityId, domain,
				new AsyncCallbackWrapper<ActivityInfo>(callback));
	}

	@Override
	public void updateActivityInfoDataBase(ActivityInfo activityInfo, String domain, AsyncCallback<ActivityInfo> callback) {
		AON.start();
		enterprisesServiceAsync.updateActivityInfoDataBase(activityInfo, domain,
				new AsyncCallbackWrapper<ActivityInfo>(callback));
	}

	@Override
	public void createActivityInfoDataBase(ActivityInfo activityInfo, String domain, AsyncCallback<ActivityInfo> callback) {
		AON.start();
		enterprisesServiceAsync.createActivityInfoDataBase(activityInfo, domain,
				new AsyncCallbackWrapper<ActivityInfo>(callback));
	}

	@Override
	public void getCNAE2009(String domain, AsyncCallback<Map<String, String>> callback) {
		AON.start();
		enterprisesServiceAsync.getCNAE2009(domain,
				new AsyncCallbackWrapper<Map<String, String>>(callback));
	}

	@Override
	public void getEnterpiseAddresses(Integer enterpriseId, String domain, AsyncCallback<Map<Integer, String>> callback) {
		AON.start();
		enterprisesServiceAsync.getEnterpiseAddresses(enterpriseId, domain,
				new AsyncCallbackWrapper<Map<Integer, String>>(callback));
	}

	@Override
	public void getEnterpiseCalendars(Integer enterpriseId, String domain, AsyncCallback<Map<Integer, String>> callback) {
		AON.start();
		enterprisesServiceAsync.getEnterpiseCalendars(enterpriseId, domain,
				new AsyncCallbackWrapper<Map<Integer, String>>(callback));
	}

	@Override
	public void getEnterpiseActivities(Integer enterpriseId, String domain, AsyncCallback<Map<Integer, String>> callback) {
		AON.start();
		enterprisesServiceAsync.getEnterpiseActivities(enterpriseId, domain,
				new AsyncCallbackWrapper<Map<Integer, String>>(callback));
	}

	@Override
	public void createWorkplaceInfo(WorkplaceInfo workplaceInfo, Integer enterpriseId, String domain, AsyncCallback<WorkplaceInfo> callback) {
		AON.start();
		enterprisesServiceAsync.createWorkplaceInfo(workplaceInfo, enterpriseId, domain,
				new AsyncCallbackWrapper<WorkplaceInfo>(callback));
	}

	@Override
	public void getEnterpiseScopes(Integer enterpriseId, String domain, AsyncCallback<Map<Integer, String>> callback) {
		AON.start();
		enterprisesServiceAsync.getEnterpiseScopes(enterpriseId, domain,
				new AsyncCallbackWrapper<Map<Integer, String>>(callback));
	}

	@Override
	public void getEnterpriseInfo(Integer enterpriseId, String domain, AsyncCallback<EnterpriseInfo> callback) {
		AON.start();
		enterprisesServiceAsync.getEnterpriseInfo(enterpriseId, domain,
				new AsyncCallbackWrapper<EnterpriseInfo>(callback));
	}

	@Override
	public void updateEnterprise(EnterpriseInfo enterpriseInfo, String domain, AsyncCallback<EnterpriseInfo> callback) {
		AON.start();
		enterprisesServiceAsync.updateEnterprise(enterpriseInfo, domain,
				new AsyncCallbackWrapper<EnterpriseInfo>(callback));
	}
	
	@Override
	public void getCCCEmployees(String domain, Date month, List<Integer> cccIds,
			AsyncCallback<List<Employee>> callback) {
		AON.start();
		enterprisesServiceAsync.getCCCEmployees(domain, month, cccIds, new AsyncCallbackWrapper<List<Employee>>(callback));
		
	}

	@Override
	public void getAgrarianJourney(long findingDate, List<String> cccList, String domain, AsyncCallback<Map<Integer, List<AgrarianJourney>>> callback) {
		AON.start();
		enterprisesServiceAsync.getAgrarianJourney(findingDate, cccList, domain, new AsyncCallbackWrapper<Map<Integer, List<AgrarianJourney>>>(callback));
		
	}

	@Override
	public void getCRAs(String domain, AsyncCallback<List<CRA>> callback) {
		AON.start();
		enterprisesServiceAsync.getCRAs(domain, new AsyncCallbackWrapper<List<CRA>>(callback));
	}

	@Override
	public void createNewCRA(String domainName, long findingDate, List<String> ccc, Integer cccId, String type, AsyncCallback<String> callback) {
		AON.start();
		enterprisesServiceAsync.createNewCRA(domainName, findingDate, ccc, cccId, type, new AsyncCallbackWrapper<String>(callback));
	}

	@Override
	public void deleteCRA(String domainName, Integer code,
			AsyncCallback<String> callback) {
		AON.start();
		enterprisesServiceAsync.deleteCRA(domainName, code, new AsyncCallbackWrapper<String>(callback));
	}

	@Override
	public void getEmployeePeculiarities(String domain, Integer contractId,
			AsyncCallback<Peculiarities> callback) {
		AON.start();
		enterprisesServiceAsync.getEmployeePeculiarities(domain, contractId, new AsyncCallbackWrapper<Peculiarities>(callback));
	}

	@Override
	public void setEmployeePeculiarities(String domain, Integer contractId, Peculiarities peculiarities,
			AsyncCallback<String> callback) {
		AON.start();
		enterprisesServiceAsync.setEmployeePeculiarities(domain, contractId, peculiarities, new AsyncCallbackWrapper<String>(callback));
	}

	@Override
	public void getEmployeeSSBonuses(String domain, Integer contractId, AsyncCallback<List<SSBonusData>> callback) {
		AON.start();
		enterprisesServiceAsync.getEmployeeSSBonuses(domain, contractId, new AsyncCallbackWrapper<List<SSBonusData>>(callback));
	}

	@Override
	public void getBonusConcepts(String domain, AsyncCallback<List<SSBonusData>> callback) {
		AON.start();
		enterprisesServiceAsync.getBonusConcepts(domain, new AsyncCallbackWrapper<List<SSBonusData>>(callback));
	}

	@Override
	public void setEmployeeSSBonuses(String domain, Integer contractId, List<SSBonusData> ssBonuses,
			AsyncCallback<List<SSBonusData>> callback) {
		AON.start();
		enterprisesServiceAsync.setEmployeeSSBonuses(domain, contractId, ssBonuses, new AsyncCallbackWrapper<List<SSBonusData>>(callback));	
	}

	@Override
	public void setEmployeeAFIChanges(String domain, Integer contractId, AFIChanges afiChangesMap, AsyncCallback<String> callback) {
		AON.start();
		enterprisesServiceAsync.setEmployeeAFIChanges(domain, contractId, afiChangesMap, new AsyncCallbackWrapper<String>(callback));
	}

	@Override
	public void getEmployeeAFIChanges(String domain, Integer contractId, AsyncCallback<AFIChanges> callback) {
		AON.start();
		enterprisesServiceAsync.getEmployeeAFIChanges(domain, contractId, new AsyncCallbackWrapper<AFIChanges>(callback));
	}

	@Override
	public void getDomainMailAccounts(String currentDomainName, String currentUser, AsyncCallback<List<MailAccount>> callback) {
		AON.start();
		enterprisesServiceAsync.getDomainMailAccounts(currentDomainName, currentUser, new AsyncCallbackWrapper<List<MailAccount>>(callback));
	}

	@Override
	public void getPayrollEmailSendTo(String currentDomainName, Integer enterpriseID, AsyncCallback<String> callback) {
		AON.start();
		enterprisesServiceAsync.getPayrollEmailSendTo(currentDomainName, enterpriseID, new AsyncCallbackWrapper<String>(callback));
	}

	@Override
	public void getPayrollEmailBody(String currentDomainName, String paramsBase64, AsyncCallback<String> callback) {
		AON.start();
		enterprisesServiceAsync.getPayrollEmailBody(currentDomainName, paramsBase64, new AsyncCallbackWrapper<String>(callback));
	}

	@Override
	public void sendPayrollEmail(String currentDomainName, String from, String to, String cc, String cco, String bodyHTML, AsyncCallback<String> callback) {
		AON.start();
		enterprisesServiceAsync.sendPayrollEmail(currentDomainName, from, to, cc, cco, bodyHTML, new AsyncCallbackWrapper<String>(callback));
	}

	@Override
	public void checkEmployeesEmails(String currentDomainName, ArrayList<Integer> salaryIds, AsyncCallback<String> callback) {
		AON.start();
		enterprisesServiceAsync.checkEmployeesEmails(currentDomainName, salaryIds, new AsyncCallbackWrapper<String>(callback));
	}

	@Override
	public void sendPayrollEmailToEmployees(String currentDomainName, String from, String cc, String cco,
			String bodyHTML, String completeURL, AsyncCallback<String> callback) {
		AON.start();
		enterprisesServiceAsync.sendPayrollEmailToEmployees(currentDomainName, from, cc, cco, bodyHTML, completeURL, new AsyncCallbackWrapper<String>(callback));
	}

}
