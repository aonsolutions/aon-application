/**
 * 
 */
package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.esferalia.aon.gwt.common.client.AON;
import com.esferalia.aon.gwt.payroll.client.AgreementsCleanDialog.AgreementCleanType;
import com.esferalia.aon.gwt.payroll.client.PayrollEmailDialog.Type;
import com.esferalia.aon.gwt.payroll.shared.AFIChanges;
import com.esferalia.aon.gwt.payroll.shared.ActivitiesCCC;
import com.esferalia.aon.gwt.payroll.shared.ActivityInfo;
import com.esferalia.aon.gwt.payroll.shared.AgrarianJourney;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.AgreementInfo;
import com.esferalia.aon.gwt.payroll.shared.AgreementsClean;
import com.esferalia.aon.gwt.payroll.shared.Attach;
import com.esferalia.aon.gwt.payroll.shared.Bonus;
import com.esferalia.aon.gwt.payroll.shared.CCCInfo;
import com.esferalia.aon.gwt.payroll.shared.CNO;
import com.esferalia.aon.gwt.payroll.shared.CRA;
import com.esferalia.aon.gwt.payroll.shared.ComunicaEnterpriseSettings;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.ContractClause;
import com.esferalia.aon.gwt.payroll.shared.ContractConcepts;
import com.esferalia.aon.gwt.payroll.shared.ContractSpecificData;
import com.esferalia.aon.gwt.payroll.shared.Cost;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeSegSocial;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseContext;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseITStatus;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseITStatus.ItNotExist;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseStatus;
import com.esferalia.aon.gwt.payroll.shared.Extra;
import com.esferalia.aon.gwt.payroll.shared.IT;
import com.esferalia.aon.gwt.payroll.shared.ITEmployee;
import com.esferalia.aon.gwt.payroll.shared.ITPart;
import com.esferalia.aon.gwt.payroll.shared.MainCCCInfo;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Peculiarities;
import com.esferalia.aon.gwt.payroll.shared.Result;
import com.esferalia.aon.gwt.payroll.shared.SSBonusData;
import com.esferalia.aon.gwt.payroll.shared.SSPECData;
import com.esferalia.aon.gwt.payroll.shared.SecondaryUserCertificate;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceInfo;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.Certificate.CertificateType;
import com.esferalia.aon.occam.api.model.CertificateInfo;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.mod145.Mod145;
import com.esferalia.aon.occam.api.model.payroll.Activity;
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
	public void getDomain(String domain, String user, AsyncCallback<Domain> callback) {
		AON.start();
		enterprisesServiceAsync.getDomain(domain, user, new AsyncCallbackWrapper<Domain>(callback));
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
	public void deleteAgreements(String domain, List<Integer> agreementIds, AsyncCallback<Void> callback)
			throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.deleteAgreements(domain, agreementIds,  
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
	public void getAgreements(String domain, boolean allAgreements, AsyncCallback<List<Agreement>> callback) {
		AON.start();
		enterprisesServiceAsync.getAgreements(domain, allAgreements, new AsyncCallbackWrapper<List<Agreement>>(callback));
	}
	
	@Override
	public void getTrashAgreements(String domain, int offset, int limit,
			AsyncCallback<List<Agreement>> callback) {
		AON.start();
		enterprisesServiceAsync.getTrashAgreements(domain, offset, limit,
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
	public void getDeleteAgreementMessage(String domain, Agreement agreement, 
			AsyncCallback<String> callback) {
		AON.start();
		enterprisesServiceAsync.getDeleteAgreementMessage(domain, agreement, 
				new AsyncCallbackWrapper<String>(callback));
	}
	
	@Override
	public void getAgreementUsedInfo(String domain, Integer agreementId, String agreementDescription, AsyncCallback<String> callback) throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.getAgreementUsedInfo(domain, agreementId, agreementDescription, new AsyncCallbackWrapper<String>(callback));
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
	public void moveAgreement2Child(String domain, Integer agreementId,
			AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.moveAgreement2Child(domain, agreementId, new 
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
	public void getWorkplaces(String domain, AsyncCallback<List<Workplace>> callback) {
		AON.start();
		enterprisesServiceAsync.getWorkplaces(domain, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getPayMethods(String domain, AsyncCallback<Map<String, String>> callback) {
		AON.start();
		enterprisesServiceAsync.getPayMethods(domain, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getActivityCCC(String domain, AsyncCallback<ActivitiesCCC> callback) {
		AON.start();
		enterprisesServiceAsync.getActivityCCC(domain, new AsyncCallbackWrapper<>(callback));
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
	public void getDeleteCCCMessage(String domainName, ArrayList<Integer> cccIds, AsyncCallback<String> callback) {
		AON.start();
		enterprisesServiceAsync.getDeleteCCCMessage(domainName, cccIds,
				new AsyncCallbackWrapper<String>(callback));
	}

	@Override
	public void getCNAE2009(String domain, AsyncCallback<Map<Integer, String>> callback) {
		AON.start();
		enterprisesServiceAsync.getCNAE2009(domain, new AsyncCallbackWrapper<Map<Integer, String>>(callback));
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
	public void getCRAs(String domain, String user, long liquidDateTime, AsyncCallback<List<CRA>> callback) {
		AON.start();
		enterprisesServiceAsync.getCRAs(domain, user, liquidDateTime, new AsyncCallbackWrapper<List<CRA>>(callback));
	}

	@Override
	public void createNewCRA(String domainName, String user, long findingDate, List<String> ccc, ArrayList<Integer> cccIds, Integer cccId, String type, AsyncCallback<String> callback) {
		AON.start();
		enterprisesServiceAsync.createNewCRA(domainName, user, findingDate, ccc, cccIds, cccId, type, new AsyncCallbackWrapper<String>(callback));
	}
	
	@Override
	public void checkCreateNewCRA(String domainName, long findingDate, ArrayList<Integer> cccList, AsyncCallback<String> callback) {
		AON.start();
		enterprisesServiceAsync.checkCreateNewCRA(domainName, findingDate, cccList, new AsyncCallbackWrapper<String>(callback));
	}

	@Override
	public void deleteCRA(String domainName, Integer code, AsyncCallback<Void> callback) {
		AON.start();
		enterprisesServiceAsync.deleteCRA(domainName, code, new AsyncCallbackWrapper<Void>(callback));
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
	public void getEmployeeSSPECs(String domain, String user, Integer contractId, AsyncCallback<List<SSPECData>> callback) throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.getEmployeeSSPECs(domain, user, contractId, new AsyncCallbackWrapper<List<SSPECData>>(callback));
	}
	
	@Override
	public void syncEmployeeSSPECs(String domain, String user, Integer contractId, Date startDate, Date endDate, AsyncCallback<List<SSPECData>> callback) throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.syncEmployeeSSPECs(domain, user, contractId, startDate, endDate, new AsyncCallbackWrapper<List<SSPECData>>(callback));
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
	public void setEmployeeAFIChanges(String domain, Integer contractId, AFIChanges afiChangesMap, AsyncCallback<Void> callback) {
		AON.start();
		enterprisesServiceAsync.setEmployeeAFIChanges(domain, contractId, afiChangesMap, new AsyncCallbackWrapper<Void>(callback));
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
	public void getPayrollEmailSendTo(String currentDomainName, AsyncCallback<String> callback) {
		AON.start();
		enterprisesServiceAsync.getPayrollEmailSendTo(currentDomainName, new AsyncCallbackWrapper<String>(callback));
	}

	@Override
	public void getPayrollEmailBody(String currentDomainName, Type type, HashMap<String, String> params, AsyncCallback<String> callback) {
		AON.start();
		enterprisesServiceAsync.getPayrollEmailBody(currentDomainName, type, params, new AsyncCallbackWrapper<String>(callback));
	}

	@Override
	public void sendPayrollEmail(String currentDomainName, Type type, HashMap<String, String> params, String from, String to, String cc, String cco, String bodyHTML, AsyncCallback<String> callback) throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.sendPayrollEmail(currentDomainName, type, params, from, to, cc, cco, bodyHTML, new AsyncCallbackWrapper<String>(callback));
	}

	@Override
	public void checkEmployeesEmails(String currentDomainName, ArrayList<Integer> salaryIds, AsyncCallback<String> callback) {
		AON.start();
		enterprisesServiceAsync.checkEmployeesEmails(currentDomainName, salaryIds, new AsyncCallbackWrapper<String>(callback));
	}
	
	@Override
	public void checkEnterprisesEmails(String currentDomainName, HashSet<Integer> enterpriseIds, AsyncCallback<String> callback) {
		AON.start();
		enterprisesServiceAsync.checkEnterprisesEmails(currentDomainName, enterpriseIds, new AsyncCallbackWrapper<String>(callback));
	}
	
	@Override
	public void getSettlePDF(String currentDomainName, String user, Integer settleId, AsyncCallback<String> callback) throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.getSettlePDF(currentDomainName, user, settleId, new AsyncCallbackWrapper<String>(callback));
	}

	@Override
	public void getSalariesPDF(String currentDomainName, String user, Integer enterpriseId, List<Integer> salaryIds, AsyncCallback<String> callback) throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.getSalariesPDF(currentDomainName, user, enterpriseId, salaryIds, new AsyncCallbackWrapper<String>(callback));
	}

	@Override
	public void getEnterprisesCCCInfo(String currentDomainName, String user, long findPeriodTime, AsyncCallback<List<CCCInfo>> callback) {
		AON.start();
		enterprisesServiceAsync.getEnterprisesCCCInfo(currentDomainName, user, findPeriodTime, new AsyncCallbackWrapper<List<CCCInfo>>(callback));
	}

	@Override
	public void getEmployeesInfo(String currentDomainName, Boolean allEmployees, AsyncCallback<List<EmployeeContractInfo>> callback) {
		AON.start();
		enterprisesServiceAsync.getEmployeesInfo(currentDomainName, allEmployees, new AsyncCallbackWrapper<List<EmployeeContractInfo>>(callback));
	}
	
	@Override
	public void getEmployeeInfo(String currentDomainName, Integer contractId, AsyncCallback<EmployeeContractInfo> callback) {
		AON.start();
		enterprisesServiceAsync.getEmployeeInfo(currentDomainName, contractId, new AsyncCallbackWrapper<EmployeeContractInfo>(callback));
	}
	
	@Override
	public void getEnterpriseStatus(String domain, String user, Integer enterpriseId,
			AsyncCallback<EnterpriseStatus> callback) {
		AON.start();
		enterprisesServiceAsync.getEnterpriseStatus(domain, user, enterpriseId, new AsyncCallbackWrapper<EnterpriseStatus>(callback));
	}

	@Override
	public void getEnterpriseITStatus(String domain, String user, 
			AsyncCallback<EnterpriseITStatus> callback) {
		AON.start();
		enterprisesServiceAsync.getEnterpriseITStatus(domain, user, new AsyncCallbackWrapper<EnterpriseITStatus>(callback));
	}


	@Override
	public void getEmployeesITInfo(String currentDomainName, Boolean allEmployees, AsyncCallback<List<ITEmployee>> callback) {
		AON.start();
		enterprisesServiceAsync.getEmployeesITInfo(currentDomainName, allEmployees, new AsyncCallbackWrapper<List<ITEmployee>>(callback));
	}
	
	@Override
	public void getWorkplaceEmployeeITInfo(String currentDomainName, Boolean allEmployees, Integer workplaceId, AsyncCallback<List<ITEmployee>> callback) {
		AON.start();
		enterprisesServiceAsync.getWorkplaceEmployeeITInfo(currentDomainName, allEmployees, workplaceId, new AsyncCallbackWrapper<List<ITEmployee>>(callback));
	}
	
	@Override
	public void getEmployeeITInfo(String currentDomainName, Integer contractId, AsyncCallback<List<ITEmployee>> callback) {
		AON.start();
		enterprisesServiceAsync.getEmployeeITInfo(currentDomainName, contractId, new AsyncCallbackWrapper<List<ITEmployee>>(callback));
	}

	@Override
	public void getEmployeesITInfo(String currentDomainName, Integer ids [], AsyncCallback<List<ITEmployee>> callback) {
		AON.start();
		enterprisesServiceAsync.getEmployeesITInfo(currentDomainName, ids, new AsyncCallbackWrapper<List<ITEmployee>>(callback));
	}

	@Override
	public void deleteIT(String currentDomainName, Integer itId, AsyncCallback<String> callback) {
		AON.start();
		enterprisesServiceAsync.deleteIT(currentDomainName, itId, new AsyncCallbackWrapper<String>(callback));
	}

	@Override
	public void createUpdateITEmployee(String currentDomainName, ITEmployee employeeITInfo, AsyncCallback<String> callback) {
		AON.start();
		enterprisesServiceAsync.createUpdateITEmployee(currentDomainName, employeeITInfo, new AsyncCallbackWrapper<String>(callback));
	}
	
	// ------------------------------------------------ Contract Attachments
	
	@Override
	public void getContractAttachments(String currentDomainName, String login, Integer contractId, AsyncCallback<List<Attach>> callback) throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.getContractAttachments(currentDomainName, login, contractId, new AsyncCallbackWrapper<List<Attach>>(callback));
	}
	
	@Override
	public void deleteContractAttach(String currentDomainName, String login, Integer attachId, AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.deleteContractAttach(currentDomainName, login, attachId, new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void getAttachData(String currentDomainName, String login, Integer attachId, AsyncCallback<String> callback) throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.getAttachData(currentDomainName, login, attachId, new AsyncCallbackWrapper<String>(callback));
	}
	
	@Override
	public void setAttachData(String currentDomainName, String login, Integer attachId, String base64, AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.setAttachData(currentDomainName, login, attachId, base64, new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void sendAttachEmail(String currentDomainName, String login, MailAccount emailFrom, String emailTo, List<String> ccTo, List<String> bccTo, String subject, String emailBody, List<Integer> attachIds, AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.sendAttachEmail(currentDomainName, login, emailFrom, emailTo, ccTo, bccTo, subject, emailBody, attachIds, new AsyncCallbackWrapper<Void>(callback));
	}
	
	// ------------------------------------------------ Contract Clauses
	
	@Override
	public void getContractClauses(String currentDomainName, Integer contractId, AsyncCallback<List<ContractClause>> callback) throws IllegalArgumentException  {
		AON.start();
		enterprisesServiceAsync.getContractClauses(currentDomainName, contractId, new AsyncCallbackWrapper<List<ContractClause>>(callback));
	}
	
	@Override
	public void setContractClauses(String currentDomainName, Integer contractId, List<ContractClause> contractClauses, AsyncCallback<Void> callback) throws IllegalArgumentException  {
		AON.start();
		enterprisesServiceAsync.setContractClauses(currentDomainName, contractId, contractClauses, new AsyncCallbackWrapper<Void>(callback));
	}
	
	@Override
	public void deleteContractClause(String currentDomainName, Integer clauseId, AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.deleteContractClause(currentDomainName, clauseId, new AsyncCallbackWrapper<Void>(callback));
	}
	
	@Override
	public void importContractClauses(String currentDomainName, List<Integer> clausesIds, Integer contractId, AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.importContractClauses(currentDomainName, clausesIds, contractId, new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void getDomainClauses(String currentDomainName, AsyncCallback<List<ContractClause>> callback)
			throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.getDomainClauses(currentDomainName, new AsyncCallbackWrapper<List<ContractClause>>(callback));
	}
	
	@Override
	public void getContractOtherInfo(String currentDomainName, Integer contractId, Integer contractType, AsyncCallback<Map<String, String>> callback) {
		AON.start();
		enterprisesServiceAsync.getContractOtherInfo(currentDomainName, contractId, contractType, new AsyncCallbackWrapper<Map<String, String>>(callback));
	}
	
	@Override
	public void setContractOtherInfo(String currentDomainName, Integer contractId, String contractType, Map<String, String> contractOtherData, AsyncCallback<Map<String, String>> callback) {
		AON.start();
		enterprisesServiceAsync.setContractOtherInfo(currentDomainName, contractId, contractType, contractOtherData, new AsyncCallbackWrapper<Map<String, String>>(callback));
	}
	
	@Override
	public void getContractSpecificData(String currentDomainName, Integer contractId, AsyncCallback<ContractSpecificData> callback) throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.getContractSpecificData(currentDomainName, contractId, new AsyncCallbackWrapper<ContractSpecificData>(callback));
	}

	@Override
	public void setContractSpecificData(String currentDomainName, EmployeeContractInfo employeeContractData, AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.setContractSpecificData(currentDomainName, employeeContractData, new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void getCNOs(String currentDomainName, AsyncCallback<Map<String, CNO>> callback) {
		AON.start();
		enterprisesServiceAsync.getCNOs(currentDomainName, new AsyncCallbackWrapper<Map<String, CNO>>(callback));
	}
	
	@Override
	public void getMainCCCInfoDataBase(String currentDomainName, String currentUser, AsyncCallback<MainCCCInfo> callback) {
		AON.start();
		enterprisesServiceAsync.getMainCCCInfoDataBase(currentDomainName, currentUser, new AsyncCallbackWrapper<MainCCCInfo>(callback));
	}

	@Override
	public void setMainCCCInfoDataBase(String currentDomainName, String currentUser, MainCCCInfo mainCCCInfo, AsyncCallback<Void> callback) {
		AON.start();
		enterprisesServiceAsync.setMainCCCInfoDataBase(currentDomainName, currentUser, mainCCCInfo, new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void getContractBonus(String currentDomainName, Integer contractId, AsyncCallback<List<SSBonusData>> callback) {
		AON.start();
		enterprisesServiceAsync.getContractBonus(currentDomainName, contractId, new AsyncCallbackWrapper<List<SSBonusData>>(callback));
	}

	@Override
	public void setContractBonus(String currentDomainName, EmployeeContractInfo employeeContractData, AsyncCallback<Void> callback) {
		AON.start();
		enterprisesServiceAsync.setContractBonus(currentDomainName, employeeContractData, new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void getSecondaryUsers(String currentDomainName, String currentUser, Integer rattachId, AsyncCallback<List<SecondaryUserCertificate>> callback) throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.getSecondaryUsers(currentDomainName, currentUser, rattachId, new AsyncCallbackWrapper<List<SecondaryUserCertificate>>(callback));
	}

	@Override
	public void deleteSecondaryUser(String currentDomainName, String currentUser, Integer rattachId, String ipfType, String ipf, AsyncCallback<Void> callback) {
		AON.start();
		enterprisesServiceAsync.deleteSecondaryUser(currentDomainName, currentUser, rattachId, ipfType, ipf, new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void createSecondaryUser(String currentDomainName, String currentUser, Integer rattachId, String ipfType, String ipf, String naf, AsyncCallback<Void> callback) {
		AON.start();
		enterprisesServiceAsync.createSecondaryUser(currentDomainName, currentUser, rattachId, ipfType, ipf, naf, new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void getIpfxNaf(String currentDomainName, String currentUser, ArrayList<String> nssList, AsyncCallback<EmployeeSegSocial> callback) {
		AON.start();
		enterprisesServiceAsync.getIpfxNaf(currentDomainName, currentUser, nssList, new AsyncCallbackWrapper<EmployeeSegSocial>(callback));
	}

	@Override
	public void getNafxIpf(String currentDomainName, String currentUser, String ipf, String apellido1, String apellido2, AsyncCallback<EmployeeSegSocial> callback) {
		AON.start();
		enterprisesServiceAsync.getNafxIpf(currentDomainName, currentUser, ipf, apellido1, apellido2, new AsyncCallbackWrapper<EmployeeSegSocial>(callback));
	}

	@Override
	public void createITCertificate(String currentDomainName, String currentUser, String affiliationNumber,
			String regime, String contributionAccount, String docType, String docNum, String applicantType,
			String reason, Date dateFrom, Date dateTo, float baseCC, float baseCP, int days,
			AsyncCallback<Boolean> callback) {
		AON.start();
		enterprisesServiceAsync.createITCertificate(currentDomainName, currentUser, affiliationNumber,
				regime, contributionAccount, docType, docNum, applicantType,
				reason, dateFrom, dateTo, baseCC, baseCP, days, new AsyncCallbackWrapper<Boolean>(callback));
	}

	@Override
	public void deleteComunicateIT(String currentDomainName, String currentUser, String affiliationNumber,
			String regime, String contributionAccount, Date dateFrom, Date dateTo, Date startDate,
			AsyncCallback<Void> callback) {
		AON.start();
		enterprisesServiceAsync.deleteComunicateIT(currentDomainName, currentUser, affiliationNumber,
				regime, contributionAccount, dateFrom, dateTo, startDate, new AsyncCallbackWrapper<Void>(callback));
	}
	
	@Override
	public void syncITs(String currentDomainName, String currentUser, AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.syncITs(currentDomainName, currentUser, new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void setComunicationIT(String currentDomainName, String currentUser, ITEmployee itEmployee, IT it,
			AsyncCallback<Void> callback) {
		AON.start();
		enterprisesServiceAsync.setComunicationIT(currentDomainName, currentUser, itEmployee,
				it, new AsyncCallbackWrapper<Void>(callback));
	
	}

	@Override
	public void getServiAgreement(String currentDomainName, String userLogin, String serviAgreementCode, List<Integer> selectedDates, AsyncCallback<Integer> callback) throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.getServiAgreement(currentDomainName, userLogin, serviAgreementCode, selectedDates, new AsyncCallbackWrapper<Integer>(callback));
	}
	
	@Override
	public void getServiAgreementDates(String serviAgreementCode, AsyncCallback<List<Integer>> callback) throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.getServiAgreementDates(serviAgreementCode, new AsyncCallbackWrapper<List<Integer>>(callback));
	}

	@Override
	public void checkIfRectificative(String currentDomainName, Date findingDate, ArrayList<Integer> selectedCCCList,
			AsyncCallback<Boolean> callback) {
		AON.start();
		enterprisesServiceAsync.checkIfRectificative(currentDomainName, findingDate, selectedCCCList, new AsyncCallbackWrapper<Boolean>(callback));
	}

	@Override
	public void registerITBaja(String domainName, String userLogin, String regime, String ccc, String naf,
			String contingency, String situation_employee, String licenseNumber,
			String cias, String occupation, Date startdate, String contractType,
			float baseCot, int cotDays, Date fATEP, String accidentType,
			AsyncCallback<Void> callback) {
		AON.start();
		enterprisesServiceAsync.registerITBaja(domainName, userLogin, regime, ccc, naf, contingency, situation_employee, licenseNumber,
				cias, occupation, startdate, contractType, baseCot, cotDays, fATEP, accidentType, new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void registerITConfirmation(String domainName, String userLogin, String regime, String ccc, String naf,
			String contingency, String situation_employee, String licenseNumber,
			String cias, Date fbaja, Date fconfirmation, String npartConfimation,
			AsyncCallback<Void> callback) {
		AON.start();
		enterprisesServiceAsync.registerITConfirmation(domainName, userLogin, regime, ccc, naf,
				contingency, situation_employee, licenseNumber, cias, fbaja, fconfirmation, npartConfimation, new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void registerITAlta(String domainName, String userLogin, String regime, String ccc, String naf,
			String contingency, String situation_employee, String licenseNumber,
			String cias, Date fbaja, Date falta, Date fATEP, String accidentType,
			String causeType, AsyncCallback<Void> callback) {
		AON.start();
		enterprisesServiceAsync.registerITAlta(domainName, userLogin, regime, ccc, naf, contingency, situation_employee, licenseNumber,
				cias, fbaja, falta, fATEP, accidentType, causeType, new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void getContratoSepe(String domainName, String userLogin, String ipf, Date startDate, Date endDate, AsyncCallback<String> callback) {
		AON.start();
		enterprisesServiceAsync.getContratoSepe(domainName, userLogin, ipf, startDate, endDate, new AsyncCallbackWrapper<String>(callback));
	}

	@Override
	public void getTrashEmployeesInfo(String domainName, AsyncCallback<List<EmployeeContractInfo>> callback) {
		AON.start();
		enterprisesServiceAsync.getTrashEmployeesInfo(domainName, new AsyncCallbackWrapper<List<EmployeeContractInfo>>(callback));
	}

	@Override
	public void restoreContract(String domainName, Integer contractId, AsyncCallback<Void> callback) {
		AON.start();
		enterprisesServiceAsync.restoreContract(domainName, contractId, new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void delete4EverContract(String domainName, Integer contractId, AsyncCallback<Void> callback) {
		AON.start();
		enterprisesServiceAsync.delete4EverContract(domainName, contractId, new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void getServiAgreements(String domainName, String currentUser, AsyncCallback<Map<String, String>> callback) {
		AON.start();
		enterprisesServiceAsync.getServiAgreements(domainName, currentUser, new AsyncCallbackWrapper<Map<String, String>>(callback));
	}

	@Override
	public void getDomainUserRoles(String domainName, String currentUser, AsyncCallback<DomainUserRoles> callback) {
		AON.start();
		enterprisesServiceAsync.getDomainUserRoles(domainName, currentUser, new AsyncCallbackWrapper<DomainUserRoles>(callback));
	}

	@Override
	public void hasCertificateSEPE(String domainName, String currentUser, AsyncCallback<Boolean> callback) {
		AON.start();
		enterprisesServiceAsync.hasCertificateSEPE(domainName, currentUser, new AsyncCallbackWrapper<Boolean>(callback));
	}

	@Override
	public void getComunicaEnterpriseSettings(String domainName, String currentUser, AsyncCallback<ComunicaEnterpriseSettings> callback) {
		AON.start();
		enterprisesServiceAsync.getComunicaEnterpriseSettings(domainName, currentUser, new AsyncCallbackWrapper<ComunicaEnterpriseSettings>(callback));
	}

	@Override
	public void setComunicaEnterpriseSettings(String domainName, String currentUser, ComunicaEnterpriseSettings comunicaEnterpriseSettings, AsyncCallback<Void> callback) {
		AON.start();
		enterprisesServiceAsync.setComunicaEnterpriseSettings(domainName, currentUser, comunicaEnterpriseSettings, new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void getEnterpriseId(String domainName, AsyncCallback<Integer> callback) {
		AON.start();
		enterprisesServiceAsync.getEnterpriseId(domainName, new AsyncCallbackWrapper<Integer>(callback));
	}

	@Override
	public void verifyCertificate(String domainName, String currentUser, com.esferalia.aon.gwt.payroll.shared.DigitalCertificate.CertificateType certificateType, AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.verifyCertificate(domainName, currentUser, certificateType, new AsyncCallbackWrapper<Void>(callback));
	}

	@Override
	public void getAllConcepts(String domainName, String currentUser, AsyncCallback<ContractConcepts> callback) {
		AON.start();
		enterprisesServiceAsync.getAllConcepts(domainName, currentUser, new AsyncCallbackWrapper<>(callback));
	}

	// --------------------------- Certificates

	@Override
	public void getCertificates(String domain, String login, boolean withParent, AsyncCallback<List<Certificate>> callback) throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.getCertificates(domain, login, withParent, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void deleteCertificate(String domain, String login, Certificate certificate, AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.deleteCertificate(domain, login, certificate, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getCertificateInfo(String domain, String login, Integer certitificateId, AsyncCallback<CertificateInfo> callback) throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.getCertificateInfo(domain, login, certitificateId, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void verifyCertificate(String currentDomainName, String currentUser, Integer rattachId, List<CertificateType> tags, AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.verifyCertificate(currentDomainName, currentUser, rattachId, tags, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getEnterpriseContext(String currentDomainName, AsyncCallback<EnterpriseContext> callback) {
		AON.start();
		enterprisesServiceAsync.getEnterpriseContext(currentDomainName, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void getSecondaryUsers(String currentDomainName, String currentUser, AsyncCallback<List<SecondaryUserCertificate>> callback) throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.getSecondaryUsers(currentDomainName, currentUser, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void deleteSecondaryUser(String currentDomainName, String currentUser, String ipfType, String ipf, AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.deleteSecondaryUser(currentDomainName, currentUser, ipfType, ipf, new AsyncCallbackWrapper<>(callback));
	}

	@Override
	public void createSecondaryUser(String currentDomainName, String currentUser, String ipfType, String ipf, String naf, AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.createSecondaryUser(currentDomainName, currentUser, ipfType, ipf, naf, new AsyncCallbackWrapper<>(callback));
	}
	
	// ------------------------------------------------ SSBonus
	
	@Override
	public void syncSSBonus(String domain, String user, Integer contractId, AsyncCallback<List<SSBonusData>> callback)  throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.syncSSBonus(domain, user, contractId, new AsyncCallbackWrapper<List<SSBonusData>>(callback));
	}

	@Override
	public void getEmployeeSSBonuses(String domain, Integer contractId, AsyncCallback<List<SSBonusData>> callback) throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.getEmployeeSSBonuses(domain, contractId, new AsyncCallbackWrapper<List<SSBonusData>>(callback));
	}

	@Override
	public void communicateITPart(String currentDomainName, String currentUser, ITEmployee itEmployee, IT it, ITPart part,
			AsyncCallback<Void> asyncCallback) throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.communicateITPart(currentDomainName, currentUser, itEmployee, it, part, asyncCallback);
	}

	@Override
	public void saveITParts(String currentDomainName, String currentUser, List<ItNotExist> list, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.saveITParts(currentDomainName, currentUser, list, asyncCallback);
	}
	
	@Override
	public void removeITParts(String currentDomainName, String currentUser, List<ItNotExist> list, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.removeITParts(currentDomainName, currentUser, list, asyncCallback);
	}
	
	// ------------------------------------------------ Agreement Clean

	@Override
	public void getAgreementsClean(String domainName, AgreementCleanType cleanType, AsyncCallback<List<AgreementsClean>> asyncCallback) throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.getAgreementsClean(domainName, cleanType, asyncCallback);
	}
	
	// ------------------------------------------------ Agreement Tab (New)

	@Override
	public void getAgreementInfo(String domainName, Integer agreementId, boolean withContracts, AsyncCallback<AgreementInfo> callback) throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.getAgreementInfo(domainName, agreementId, withContracts, callback);
	}

	@Override
	public void setAgreementInfo(String domainName, AgreementInfo agreementInfo, AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.setAgreementInfo(domainName, agreementInfo, callback);
	}

	@Override
	public void getAgreementVariables(String domainName, AgreementInfo agreement, AsyncCallback<Set<String>> callback) throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.getAgreementVariables(domainName, agreement, callback);
	}

	@Override
	public void getAgreementDraftReceipt(String domainName, AgreementInfo agreement, List<Variable> context, int levelId, String mime, AsyncCallback<String> callback) throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.getAgreementDraftReceipt(domainName, agreement, context, levelId, mime, callback);
	}

	@Override
	public void checkAndUpdateServiAgreement(String domainName, String currentUser, AgreementInfo agreement, AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.checkAndUpdateServiAgreement(domainName, currentUser, agreement, callback);
	}
	
	@Override
	public void deletePayments(String domainName, List<Integer> paymentIds, AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.deletePayments(domainName, paymentIds, callback);
	}

	@Override
	public void getContext(String domainName, AgreementInfo agreementInfo, AsyncCallback<ContextDescriptor> callback) throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.getContext(domainName, agreementInfo, callback);
	}
	
	@Override
	public void eval(String domainName, String expression, AgreementInfo agreementInfo, AsyncCallback<List<Result>> callback) throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.eval(domainName, expression, agreementInfo, callback);
	}
	
	// ------------------------------------------------ Enterprise (API)

	@Override
	public void getEnterprise(String domainName, String user, Integer id,
			AsyncCallback<com.esferalia.aon.occam.api.model.payroll.Enterprise> callback)
			throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.getEnterprise(domainName, user, id, callback);
	}

	@Override
	public void saveEnterprise(String domainName, String user,
			com.esferalia.aon.occam.api.model.payroll.Enterprise enterprise, AsyncCallback<Void> callback)
			throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.saveEnterprise(domainName, user, enterprise, callback);
	}
	
	// ------------------------------------------------ EnterpriseActivity (API)

	@Override
	public void getActivity(String domainName, String user, Integer id,
			AsyncCallback<com.esferalia.aon.occam.api.model.payroll.Activity> callback)
			throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.getActivity(domainName, user, id, callback);
	}

	@Override
	public void saveActivity(String domainName, String user,
			com.esferalia.aon.occam.api.model.payroll.Activity activity, AsyncCallback<Void> callback)
			throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.saveActivity(domainName, user, activity, callback);
	}

	@Override
	public void getActivities(String domainName, String user, AsyncCallback<List<Activity>> callback)
			throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.getActivities(domainName, user, callback);
	}

	@Override
	public void saveActivities(String domainName, String user, List<Activity> activities,
			AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.saveActivities(domainName, user, activities, callback);
	}
	
	// --------------------------- Mod 145 (API)

	@Override
	public void getMod145List(String domainName, String user, Integer contractId,
			AsyncCallback<List<Mod145>> callback) throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.getMod145List(domainName, user, contractId, callback);
	}

	@Override
	public void getMod145(String domainName, String user, Integer id, AsyncCallback<Mod145> callback)
			throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.getMod145(domainName, user, id, callback);
	}

	@Override
	public void saveMod145(String domainName, String user, Mod145 mod145,
			AsyncCallback<Void> callback) throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.saveMod145(domainName, user, mod145, callback);
	}

	@Override
	public void printMod145(String domainName, String user, Mod145 mod145,
			AsyncCallback<String> callback) throws IllegalArgumentException {
		AON.start();
		enterprisesServiceAsync.printMod145(domainName, user, mod145, callback);
	}

}
