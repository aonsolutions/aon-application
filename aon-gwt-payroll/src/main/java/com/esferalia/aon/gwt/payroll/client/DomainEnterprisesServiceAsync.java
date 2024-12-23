package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.esferalia.aon.gwt.payroll.shared.Country;
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
import com.esferalia.aon.gwt.payroll.shared.ItParams;
import com.esferalia.aon.gwt.payroll.shared.Mail;
import com.esferalia.aon.gwt.payroll.shared.MainCCCInfo;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Peculiarities;
import com.esferalia.aon.gwt.payroll.shared.Period;
import com.esferalia.aon.gwt.payroll.shared.Result;
import com.esferalia.aon.gwt.payroll.shared.SSBonusData;
import com.esferalia.aon.gwt.payroll.shared.SSPECData;
import com.esferalia.aon.gwt.payroll.shared.SecondaryUserCertificate;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceInfo;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.CertificateInfo;
import com.esferalia.aon.occam.api.model.ContractParams;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.EnterpriseCCC;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.mod145.Mod145;
import com.esferalia.aon.occam.api.model.payroll.ContractData;
import com.esferalia.aon.occam.api.model.security.CertificateType;
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
	
	public void getDomainDetails(AsyncCallback<Domain> callback) {
		enterprisesServiceAsync.getDomain(getCurrentDomainName(), getCurrentUser(), callback);
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
	
	public void deleteAgreements(List<Integer> agreementIds, AsyncCallback<Void> callback) throws IllegalArgumentException {
		enterprisesServiceAsync.deleteAgreements(getCurrentDomainName(), agreementIds, callback);
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

	public void getAgreement(Integer agreementId, AsyncCallback<Agreement> callback) {
		enterprisesServiceAsync.getAgreement(getCurrentDomainName(), agreementId, callback);
	}

	public void getAgreements(int offset, int limit, AsyncCallback<List<Agreement>> callback) {
		enterprisesServiceAsync.getAgreements(getCurrentDomainName(), offset, limit, callback);
	}
	
	public void getAgreements(boolean allAgreements, AsyncCallback<List<Agreement>> callback) {
		enterprisesServiceAsync.getAgreements(getCurrentDomainName(), allAgreements, callback);
	}
	
	public void getTrashAgreements(int offset, int limit, AsyncCallback<List<Agreement>> callback) {
		enterprisesServiceAsync.getTrashAgreements(getCurrentDomainName(), offset, limit, callback);
	}
	
	public void getDeleteAgreementMessage(Agreement agreement, AsyncCallback<String> callback) {
		enterprisesServiceAsync.getDeleteAgreementMessage(getCurrentDomainName(), agreement, callback);
	}
	
	public void getAgreementUsedInfo(Integer agreementId, String agreementDescription, AsyncCallback<String> callback) throws IllegalArgumentException {
		enterprisesServiceAsync.getAgreementUsedInfo(getCurrentDomainName(), agreementId, agreementDescription, callback);
	}

	public void getEnterprises(int offset, int limit, AsyncCallback<List<Enterprise>> callback) {
		enterprisesServiceAsync.getEnterprises(getCurrentDomainName(), getCurrentUser(), " 1 = 1", offset, limit, callback);
	}

	public void getEnterprises(int offset, int limit, String condition, AsyncCallback<List<Enterprise>> callback) {
		enterprisesServiceAsync.getEnterprises(getCurrentDomainName(), getCurrentUser(), condition, offset, limit, callback);
	}

	public void getEnterprisesCosts(List<Integer> enterpriseIds, AsyncCallback<List<Cost>> callback) {
		enterprisesServiceAsync.getEnterprisesCosts(getCurrentDomainName(), enterpriseIds, callback);
	}

	public void getCCCEmployees(Date startMonth, Date endMonth, List<Integer> cccIds, AsyncCallback<List<Employee>> callback) {
		enterprisesServiceAsync.getCCCEmployees(getCurrentDomainName(), startMonth, endMonth, cccIds, callback);
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
	
	public void moveAgreement2Child(Integer agreementId, AsyncCallback<Void> callback) throws IllegalArgumentException {
		enterprisesServiceAsync.moveAgreement2Child(getCurrentDomainName(), agreementId, callback);
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
	
	public void getWorkplaces(AsyncCallback<List<Workplace>> asyncCallback) {
		enterprisesServiceAsync.getWorkplaces(getCurrentDomainName(), asyncCallback);
	}
	
	public void getPayMethods(AsyncCallback<Map<String, String>> asyncCallback) {
		enterprisesServiceAsync.getPayMethods(getCurrentDomainName(), asyncCallback);
	}

	public void getActivityCCC(AsyncCallback<ActivitiesCCC> asyncCallback) {
		enterprisesServiceAsync.getActivityCCC(getCurrentDomainName(), asyncCallback);
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
	
	public void getDeleteCCCMessage(ArrayList<Integer> cccIds, AsyncCallback<String> asyncCallback) {
		enterprisesServiceAsync.getDeleteCCCMessage(getCurrentDomainName(), cccIds, asyncCallback);
	}
	
	public void getCNAE2009(AsyncCallback<Map<Integer, String>> asyncCallback) {
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
	
	public void getEmployeeAgrarianJourney(long findingDate, List<String> cccList, AsyncCallback<Map<Integer, List<AgrarianJourney>>> asyncCallback) {
		enterprisesServiceAsync.getAgrarianJourney(findingDate, cccList, getCurrentDomainName(), asyncCallback);
	}
	
	public void getCRAs(long liquidDateTime, AsyncCallback<List<CRA>> asyncCallback) {
		enterprisesServiceAsync.getCRAs(getCurrentDomainName(), getCurrentUser(), liquidDateTime, asyncCallback);
	}
	
	public void getMinMaxCraDate(AsyncCallback<Period> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.getMinMaxCraDate(getCurrentDomainName(), getCurrentUser(), asyncCallback);
	}
	
	public void createNewCRA(long findingDate, List<String> cccList, ArrayList<Integer> cccIdList, Integer cccId, String type, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.createNewCRA(getCurrentDomainName(), getCurrentUser(), findingDate, cccList, cccIdList, cccId, type, asyncCallback);
	}
	
	public void checkCreateNewCRA(long findingDate, ArrayList<Integer> cccList, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.checkCreateNewCRA(getCurrentDomainName(), findingDate, cccList, asyncCallback);
	}
	
	public void deleteCRA(Integer code, AsyncCallback<Void> asyncCallback) {
		enterprisesServiceAsync.deleteCRA(getCurrentDomainName(), code, asyncCallback);
	}
	
	public void getEmployeePeculiarities(Integer contractId, AsyncCallback<Peculiarities> asyncCallback) {
		enterprisesServiceAsync.getEmployeePeculiarities(getCurrentDomainName(), contractId, asyncCallback);
	}
	
	public void setEmployeePeculiarities(Integer contractId, Peculiarities peculiarities, AsyncCallback<String> asyncCallback) {
		enterprisesServiceAsync.setEmployeePeculiarities(getCurrentDomainName(), contractId, peculiarities, asyncCallback);
	}
	
	public void getEmployeeSSPECs(Integer contractId, AsyncCallback<List<SSPECData>> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.getEmployeeSSPECs(getCurrentDomainName(), getCurrentUser(), contractId, asyncCallback);
	}
	
	public void syncEmployeeSSPECs(Integer contractId, Date startDate, Date endDate, AsyncCallback<List<SSPECData>> asyncCallback)throws IllegalArgumentException {
		enterprisesServiceAsync.syncEmployeeSSPECs(getCurrentDomainName(), getCurrentUser(), contractId, startDate, endDate, asyncCallback);
	}

	public void getBonusConcepts(AsyncCallback<List<SSBonusData>> asyncCallback) {
		enterprisesServiceAsync.getBonusConcepts(getCurrentDomainName(), asyncCallback);
	}
	
	public void setEmployeeSSBonuses(Integer contractId, List<SSBonusData> ssBonuses, AsyncCallback<List<SSBonusData>> asyncCallback) {
		enterprisesServiceAsync.setEmployeeSSBonuses(getCurrentDomainName(),  contractId, ssBonuses, asyncCallback);
	}
	
	public void setEmployeeAFIChanges(Integer contractId, AFIChanges afiChangesMap, AsyncCallback<Void> asyncCallback) {
		enterprisesServiceAsync.setEmployeeAFIChanges(getCurrentDomainName(), contractId, afiChangesMap, asyncCallback);
	}
	
	public void getEmployeeAFIChanges(Integer contractId, AsyncCallback<AFIChanges> asyncCallback) {
		enterprisesServiceAsync.getEmployeeAFIChanges(getCurrentDomainName(), contractId, asyncCallback);
	}
	
	public void getDomainMailAccounts(AsyncCallback<List<MailAccount>> asyncCallback) {
		enterprisesServiceAsync.getDomainMailAccounts(getCurrentDomainName(), getCurrentUser(), asyncCallback);
	}

	public void getPayrollEmailSendTo(AsyncCallback<String> asyncCallback) {
		enterprisesServiceAsync.getPayrollEmailSendTo(getCurrentDomainName(), asyncCallback);
	}
	
	public void getPayrollEmailBody(Type type, HashMap<String, String> params, boolean isPassword, AsyncCallback<String> asyncCallback) {
		enterprisesServiceAsync.getPayrollEmailBody(getCurrentDomainName(), type, params, isPassword, asyncCallback);
	}
	
	public void sendPayrollEmail(Type type, HashMap<String, String> params, Mail mail, AsyncCallback<String> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.sendPayrollEmail(getCurrentDomainName(), type, params, mail, asyncCallback);
	}
	
	public void checkEmployeesEmails(ArrayList<Integer> salaryIds, AsyncCallback<String> asyncCallback) {
		enterprisesServiceAsync.checkEmployeesEmails(getCurrentDomainName(), salaryIds, asyncCallback);
	}
	
	public void checkEnterprisesEmails(HashSet<Integer> enterpriseIds, AsyncCallback<String> asyncCallback) {
		enterprisesServiceAsync.checkEnterprisesEmails(getCurrentDomainName(), enterpriseIds, asyncCallback);
	}
	
	public void getSettlePDF(Integer settleId, AsyncCallback<String> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.getSettlePDF(getCurrentDomainName(), getCurrentUser(), settleId, asyncCallback);
	}

	public void getSalariesPDF(Integer enterpriseId, List<Integer> salaryIds, AsyncCallback<String> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.getSalariesPDF(getCurrentDomainName(), getCurrentUser(), enterpriseId, salaryIds, asyncCallback);
	}
	
	public void getEnterprisesCCCInfo(long findPeriodTime, AsyncCallback<List<CCCInfo>> asyncCallback) {
		enterprisesServiceAsync.getEnterprisesCCCInfo(getCurrentDomainName(), getCurrentUser(), findPeriodTime, asyncCallback);
	}

	public void getEmployeesInfo(Boolean allEmployees, AsyncCallback<List<EmployeeContractInfo>> asyncCallback) {
		enterprisesServiceAsync.getEmployeesInfo(getCurrentDomainName(), allEmployees, asyncCallback);
	}
	
	public void getEmployees(ContractParams params, AsyncCallback<List<EmployeeContractInfo>> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.getEmployees(getCurrentDomainName(), getCurrentUser(), params, asyncCallback);
	}
	
	public void getContractListCount(ContractParams params, AsyncCallback<Integer> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.getContractListCount(getCurrentDomainName(), getCurrentUser(), params, asyncCallback);
	}
	
	public void getFJEmployeesInfo(AsyncCallback<List<EmployeeContractInfo>> asyncCallback) {
		enterprisesServiceAsync.getFJEmployeesInfo(getCurrentDomainName(), asyncCallback);
	}
	
	public void getEmployeeInfo(Integer contractId, AsyncCallback<EmployeeContractInfo> asyncCallback) {
		enterprisesServiceAsync.getEmployeeInfo(getCurrentDomainName(), contractId, asyncCallback);
	}
	
	public void getEmployeeItList(ItParams params, AsyncCallback<List<ITEmployee>> asyncCallback) throws IllegalArgumentException  {
		enterprisesServiceAsync.getEmployeeItList(getCurrentDomainName(), params, asyncCallback);
	}
	
	public void getWorkplaceEmployeeITInfo(Boolean allEmployees, Integer workplaceId, AsyncCallback<List<ITEmployee>> asyncCallback) {
		enterprisesServiceAsync.getWorkplaceEmployeeITInfo(getCurrentDomainName(), allEmployees, workplaceId, asyncCallback);
	}
	
	public void getEmployeeITInfo(Integer contractId, AsyncCallback<List<ITEmployee>> asyncCallback) {
		enterprisesServiceAsync.getEmployeeITInfo(getCurrentDomainName(), contractId, asyncCallback);
	}

	public void getEmployeesITInfo(Integer ids [], AsyncCallback<List<ITEmployee>> asyncCallback) {
		enterprisesServiceAsync.getEmployeesITInfo(getCurrentDomainName(), ids, asyncCallback);
	}

	public void deleteIT(Integer itId, AsyncCallback<String> asyncCallback) {
		enterprisesServiceAsync.deleteIT(getCurrentDomainName(), itId, asyncCallback);
	}

	public void createUpdateITEmployee(ITEmployee employeeITInfo, AsyncCallback<String> asyncCallback) {
		enterprisesServiceAsync.createUpdateITEmployee(getCurrentDomainName(), employeeITInfo, asyncCallback);
	}
	void getEnterpriseStatus(Integer enterpriseId , AsyncCallback<EnterpriseStatus> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.getEnterpriseStatus(getCurrentDomainName(), getCurrentUser(), enterpriseId, asyncCallback);		
	}
	
	// ------------------------------------------------ Contract Attachments
	
	public void getContractAttachments(Integer contractId, AsyncCallback<List<Attach>> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.getContractAttachments(getCurrentDomainName(), getCurrentUser(), contractId, asyncCallback);
	}
	
	public void deleteContractAttach(Integer attachId, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.deleteContractAttach(getCurrentDomainName(), getCurrentUser(), attachId, asyncCallback);
	}
	
	public void getAttachData(Integer attachId, AsyncCallback<String> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.getAttachData(getCurrentDomainName(), getCurrentUser(), attachId, asyncCallback);
	}
	
	public void setAttachData(Integer attachId, String base64, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.setAttachData(getCurrentDomainName(), getCurrentUser(), attachId, base64, asyncCallback);
	}
	
	public void sendAttachEmail(MailAccount emailFrom, String emailTo, List<String> ccTo, List<String> bccTo, String subject, String emailBody, List<Integer> attachIds, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.sendAttachEmail(getCurrentDomainName(), getCurrentUser(), emailFrom, emailTo, ccTo, bccTo, subject, emailBody, attachIds, asyncCallback);
	}
	
	// ------------------------------------------------ Contract Clauses
	
	public void getContractClauses(Integer contractId, AsyncCallback<List<ContractClause>> asyncCallback) throws IllegalArgumentException  {
		enterprisesServiceAsync.getContractClauses(getCurrentDomainName(), contractId, asyncCallback);
	}
	
	public void setContractClauses(Integer contractId, List<ContractClause> contractClauses, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException  {
		enterprisesServiceAsync.setContractClauses(getCurrentDomainName(), contractId, contractClauses, asyncCallback);
	}
	
	public void deleteContractClause(Integer clauseId, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.deleteContractClause(getCurrentDomainName(), clauseId, asyncCallback);
	}
	
	public void importContractClauses(List<Integer> clausesIds, Integer contractId, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.importContractClauses(getCurrentDomainName(), clausesIds, contractId, asyncCallback);
	}
	
	public void getDomainClauses(AsyncCallback<List<ContractClause>> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.getDomainClauses(getCurrentDomainName(), asyncCallback);
	}

	public void getContractOtherInfo(Integer contractId, Integer contractType, AsyncCallback<Map<String, String>> asyncCallback) {
		enterprisesServiceAsync.getContractOtherInfo(getCurrentDomainName(), contractId, contractType, asyncCallback);
	}
	
	public void setContractOtherInfo(Integer contractId, String contractType, Map<String, String> contractOtherData, AsyncCallback<Map<String, String>> asyncCallback) {
		enterprisesServiceAsync.setContractOtherInfo(getCurrentDomainName(), contractId, contractType, contractOtherData, asyncCallback);
	}
	
	public void getContractSpecificData(Integer contractId, AsyncCallback<ContractSpecificData> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.getContractSpecificData(getCurrentDomainName(), contractId, asyncCallback);
	}

	public void setContractSpecificData(EmployeeContractInfo employeeContractData, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.setContractSpecificData(getCurrentDomainName(), employeeContractData, asyncCallback);
	}
	
	public void getContractBonus(Integer contractId, AsyncCallback<List<SSBonusData>> asyncCallback) {
		enterprisesServiceAsync.getContractBonus(getCurrentDomainName(), contractId, asyncCallback);
	}

	public void setContractBonus(EmployeeContractInfo employeeContractData, AsyncCallback<Void> asyncCallback) {
		enterprisesServiceAsync.setContractBonus(getCurrentDomainName(), employeeContractData, asyncCallback);
	}
	
	public void getCNOs(AsyncCallback<Map<String, CNO>> asyncCallback) {
		enterprisesServiceAsync.getCNOs(getCurrentDomainName(), asyncCallback);
	}
		
	public void getMainCCCInfoDataBase(AsyncCallback<MainCCCInfo> asyncCallback) {
		enterprisesServiceAsync.getMainCCCInfoDataBase(getCurrentDomainName(), getCurrentUser(), asyncCallback);
	}
	
	public void setMainCCCInfoDataBase(MainCCCInfo mainCCCInfo, AsyncCallback<Void> asyncCallback) {
		enterprisesServiceAsync.setMainCCCInfoDataBase(getCurrentDomainName(), getCurrentUser(), mainCCCInfo, asyncCallback);
	}
	
	public void getSecondaryUsers(Integer rattachId, AsyncCallback<List<SecondaryUserCertificate>> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.getSecondaryUsers(getCurrentDomainName(), getCurrentUser(), rattachId, asyncCallback);
	}
	
	public void getSecondaryUsersPDF(Integer rattachId, AsyncCallback<String> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.getSecondaryUsersPDF(getCurrentDomainName(), getCurrentUser(), rattachId, asyncCallback);
	}
	
	public void getAssignedCCCsPDF(Integer rattachId, AsyncCallback<String> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.getAssignedCCCsPDF(getCurrentDomainName(), getCurrentUser(), rattachId, asyncCallback);
	}
	
	public void deleteSecondaryUser(Integer rattachId, String ipfType, String ipf, AsyncCallback<Void> asyncCallback) {
		enterprisesServiceAsync.deleteSecondaryUser(getCurrentDomainName(), getCurrentUser(), rattachId, ipfType, ipf, asyncCallback);
	}
	
	public void createSecondaryUser(Integer rattachId, String ipfType, String ipf, String naf, AsyncCallback<Void> asyncCallback) {
		enterprisesServiceAsync.createSecondaryUser(getCurrentDomainName(), getCurrentUser(), rattachId, ipfType, ipf, naf, asyncCallback);
	}
	
	public void getIpfxNaf(ArrayList<String> nssList, AsyncCallback<EmployeeSegSocial> asyncCallback) {
		enterprisesServiceAsync.getIpfxNaf(getCurrentDomainName(), getCurrentUser(), nssList, asyncCallback);
	}
	
	public void getNafxIpf(String ipf, String apellido1, String apellido2, AsyncCallback<EmployeeSegSocial> asyncCallback) {
		enterprisesServiceAsync.getNafxIpf(getCurrentDomainName(), getCurrentUser(), ipf, apellido1, apellido2, asyncCallback);
	}
	
	public void createITCertificate(String affiliationNumber, String regime, String contributionAccount, String docType,
			String docNum, String applicantType, String reason, Date dateFrom, Date dateTo, float baseCC, float baseCP,
			int days, AsyncCallback<Boolean> asyncCallback) {
		enterprisesServiceAsync.createITCertificate(getCurrentDomainName(), getCurrentUser(), affiliationNumber, regime, contributionAccount, docType,
				docNum, applicantType, reason, dateFrom, dateTo, baseCC, baseCP,
				days, asyncCallback);
	}
	
	public void syncITs(AsyncCallback<Void> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.syncITs(getCurrentDomainName(), getCurrentUser(), asyncCallback);
	}
	
	public void setComunicationIT(ITEmployee itEmployee, IT it, AsyncCallback<Void> asyncCallback) {
		enterprisesServiceAsync.setComunicationIT(getCurrentDomainName(), getCurrentUser(), itEmployee, it, asyncCallback);
	}
	
	public void getServiAgreement(String serviAgreementCode, List<Integer> selectedDates, AsyncCallback<Integer> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.getServiAgreement(getCurrentDomainName(), getCurrentUser(), serviAgreementCode, selectedDates, asyncCallback);
	}
	
	public void getServiAgreementDates(String serviAgreementCode, AsyncCallback<List<Integer>> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.getServiAgreementDates(serviAgreementCode, asyncCallback);
	}
	
	public void checkIfRectificative(Date findingDate, ArrayList<Integer> selectedCCCList, AsyncCallback<Boolean> asyncCallback) {
		enterprisesServiceAsync.checkIfRectificative(getCurrentDomainName(), findingDate, selectedCCCList, asyncCallback);
	}
	
	public void getContratoSepe(String ipf, Date startDate, Date endDate, AsyncCallback<String> asyncCallback) {
		enterprisesServiceAsync.getContratoSepe(getCurrentDomainName(), getCurrentUser(), ipf, startDate, endDate, asyncCallback);
	}
	
	public void getTrashEmployeesInfo(AsyncCallback<List<EmployeeContractInfo>> asyncCallback) {
		enterprisesServiceAsync.getTrashEmployeesInfo(getCurrentDomainName(), asyncCallback);
	}

	public void restoreContract(Integer contractId, AsyncCallback<Void> asyncCallback) {
		enterprisesServiceAsync.restoreContract(getCurrentDomainName(), contractId, asyncCallback);
	}

	public void delete4EverContract(Integer contractId, AsyncCallback<Void> asyncCallback) {
		enterprisesServiceAsync.delete4EverContract(getCurrentDomainName(), contractId, asyncCallback);
	}
	
	public void getServiAgreements(AsyncCallback<Map<String, String>> asyncCallback) {
		enterprisesServiceAsync.getServiAgreements(getCurrentDomainName(), getCurrentUser(), asyncCallback);
	}
	
	public void getDomainUserRoles(AsyncCallback<DomainUserRoles> asyncCallback) {
		enterprisesServiceAsync.getDomainUserRoles(getCurrentDomainName(), getCurrentUser(), asyncCallback);
	}
	
	public void hasCertificateSEPE(AsyncCallback<Boolean> asyncCallback) {
		enterprisesServiceAsync.hasCertificateSEPE(getCurrentDomainName(), getCurrentUser(), asyncCallback);
	}
	
	public void getComunicaEnterpriseSettings(AsyncCallback<ComunicaEnterpriseSettings> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.getComunicaEnterpriseSettings(getCurrentDomainName(), getCurrentUser(), asyncCallback);
	}

	public void setComunicaEnterpriseSettings(ComunicaEnterpriseSettings comunicaEnterpriseSettings, AsyncCallback<Void> asyncCallback) {
		enterprisesServiceAsync.setComunicaEnterpriseSettings(getCurrentDomainName(), getCurrentUser(), comunicaEnterpriseSettings, asyncCallback);
	}
	
	public void getEnterpriseId(AsyncCallback<Integer> asyncCallback) {
		enterprisesServiceAsync.getEnterpriseId(getCurrentDomainName(), asyncCallback);
	}
	
	public void verifyCertificate(com.esferalia.aon.gwt.payroll.shared.DigitalCertificate.CertificateType certificateType, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.verifyCertificate(getCurrentDomainName(), getCurrentUser(), certificateType, asyncCallback);
	}
	
	public void getAllConcepts(AsyncCallback<ContractConcepts> asyncCallback) {
		enterprisesServiceAsync.getAllConcepts(getCurrentDomainName(), getCurrentUser(), asyncCallback);
	}
	
	// --------------------------- Certificates
	
	public void getCertificates(boolean withParent, AsyncCallback<List<Certificate>> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.getCertificates(getCurrentDomainName(), getCurrentUser(), withParent, asyncCallback);
	}
	
	public void deleteCertificate(Certificate certificate, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.deleteCertificate(getCurrentDomainName(), getCurrentUser(), certificate, asyncCallback);
	}
	
	public void downloadCertificate(Integer certificateId, String filePath, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.downloadCertificate(getCurrentDomainName(), getCurrentUser(), certificateId, filePath, asyncCallback);
	}
	
	public void getCertificateInfo(Integer certificateId, AsyncCallback<CertificateInfo> asyncCallback)  throws IllegalArgumentException  {
		enterprisesServiceAsync.getCertificateInfo(getCurrentDomainName(), getCurrentUser(), certificateId, asyncCallback);
	}
	
	public void verifyCertificate(Integer rattachId, List<CertificateType> tags, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.verifyCertificate(getCurrentDomainName(), getCurrentUser(), rattachId, tags, asyncCallback);
	}

	public void getSecondaryUsers(AsyncCallback<List<SecondaryUserCertificate>> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.getSecondaryUsers(getCurrentDomainName(), getCurrentUser(), asyncCallback);
	}

	public void deleteSecondaryUser(String ipfType, String ipf, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.deleteSecondaryUser(getCurrentDomainName(), getCurrentUser(), ipfType, ipf, asyncCallback);
	}

	public void createSecondaryUser(String ipfType, String ipf, String naf, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.createSecondaryUser(getCurrentDomainName(), getCurrentUser(), ipfType, ipf, naf, asyncCallback);
	}
	
	// --------------------------- Enterprise Context
	
	public void getEnterpriseContext(AsyncCallback<EnterpriseContext> asyncCallback) throws IllegalArgumentException  {
		enterprisesServiceAsync.getEnterpriseContext(getCurrentDomainName(), asyncCallback);
	}
	
	// ------------------------------------------------ SSBonus
	
	public void syncSSBonus(Integer contractId, AsyncCallback<List<SSBonusData>> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.syncSSBonus(getCurrentDomainName(), getCurrentUser(), contractId, asyncCallback);
	}
	
	public void getEmployeeSSBonuses(Integer contractId, AsyncCallback<List<SSBonusData>> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.getEmployeeSSBonuses(getCurrentDomainName(), contractId, asyncCallback);
	}
	

	public void sendEconomicData(ITEmployee itEmployee, IT it, ITPart part, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException{
		enterprisesServiceAsync.sendEconomicData(getCurrentDomainName(), getCurrentUser(),itEmployee, it, part, asyncCallback);
	}

	public void saveITParts(List<ItNotExist>itNotExist, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException{
		enterprisesServiceAsync.saveITParts(getCurrentDomainName(), getCurrentUser(), itNotExist, asyncCallback);
	}

	public void removeITParts(List<ItNotExist>itNotExist, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException{
		enterprisesServiceAsync.removeITParts(getCurrentDomainName(), getCurrentUser(), itNotExist, asyncCallback);
	}
	
	// ------------------------------------------------ Agreements Clean
	
	public void getAgreementsClean(AgreementCleanType cleanType, AsyncCallback<List<AgreementsClean>> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.getAgreementsClean(getCurrentDomainName(), cleanType, asyncCallback);
	}
	
	// ------------------------------------------------ Agreements Tabs (New)
	
	public void getAgreementInfo(Integer agreementId, boolean withContracts, AsyncCallback<AgreementInfo> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.getAgreementInfo(getCurrentDomainName(), agreementId, withContracts, asyncCallback);
	}
	
	public void setAgreementInfo(AgreementInfo agreementInfo, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.setAgreementInfo(getCurrentDomainName(), agreementInfo, asyncCallback);
	}
	
	public void getAgreementVariables(AgreementInfo agreementInfo, AsyncCallback<Set<String>> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.getAgreementVariables(getCurrentDomainName(), agreementInfo, asyncCallback);
	}
	
	public void getAgreementDraftReceipt(AgreementInfo agreement, List<Variable> context, int levelId, String mime, AsyncCallback<String> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.getAgreementDraftReceipt(getCurrentDomainName(), agreement, context, levelId, mime, asyncCallback);
	}
	
	public void checkAndUpdateServiAgreement(AgreementInfo agreement, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.checkAndUpdateServiAgreement(getCurrentDomainName(), getCurrentUser(), agreement, asyncCallback);
	}
	
	public void canUpdateServiAgreement(AgreementInfo agreement, AsyncCallback<Boolean> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.canUpdateServiAgreement(getCurrentDomainName(), getCurrentUser(), agreement, asyncCallback);
	}
	
	public void deletePayments(List<Integer> paymentIds, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.deletePayments(getCurrentDomainName(), paymentIds, asyncCallback);
	}
	
	public void getContext(AgreementInfo agreementInfo, AsyncCallback<ContextDescriptor> callback) throws IllegalArgumentException {
		enterprisesServiceAsync.getContext(getCurrentDomainName(), agreementInfo, callback);
	}
	
	public void eval(String expression, AgreementInfo agreementInfo, AsyncCallback<List<Result>> callback) throws IllegalArgumentException {
		enterprisesServiceAsync.eval(getCurrentDomainName(), expression, agreementInfo, callback);
	}
	
	// ------------------------------------------------ Enterprise (API)

	public void getEnterprise(Integer id, AsyncCallback<com.esferalia.aon.occam.api.model.payroll.Enterprise> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.getEnterprise(getCurrentDomainName(), getCurrentUser(), id, asyncCallback);
	}

	public void saveEnterprise(com.esferalia.aon.occam.api.model.payroll.Enterprise enterprise, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.saveEnterprise(getCurrentDomainName(), getCurrentUser(), enterprise, asyncCallback);
	}
	
	// ------------------------------------------------ EnterpriseActivity (API)
	
	public void getActivity(Integer id, AsyncCallback<com.esferalia.aon.occam.api.model.payroll.Activity> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.getActivity(getCurrentDomainName(), getCurrentUser(), id, asyncCallback);
	}

	public void saveActivity(com.esferalia.aon.occam.api.model.payroll.Activity activity, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.saveActivity(getCurrentDomainName(), getCurrentUser(), activity, asyncCallback);
	}
	
	public void getActivities(AsyncCallback<List<com.esferalia.aon.occam.api.model.payroll.Activity>> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.getActivities(getCurrentDomainName(), getCurrentUser(), asyncCallback);
	}

	public void saveActivities(List<com.esferalia.aon.occam.api.model.payroll.Activity> activities, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.saveActivities(getCurrentDomainName(), getCurrentUser(), activities, asyncCallback);
	}
	
	public void deleteCCC(Integer cccId, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.deleteCCC(getCurrentDomainName(), getCurrentUser(), cccId, asyncCallback);
	}
	
	public void saveCCC(EnterpriseCCC ccc, AsyncCallback<EnterpriseCCC> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.saveCCC(getCurrentDomainName(), getCurrentUser(), ccc, asyncCallback);
	}
	
	// ------------------------------------------------ Mod145 (API)
	
	public void getMod145List(Integer contractId, AsyncCallback<List<Mod145>> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.getMod145List(getCurrentDomainName(), getCurrentUser(), contractId, asyncCallback);
	}
	
	public void getMod145(Integer id, AsyncCallback<Mod145> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.getMod145(getCurrentDomainName(), getCurrentUser(), id, asyncCallback);
	}
	
	public void saveMod145(Mod145 mod145, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.saveMod145(getCurrentDomainName(), getCurrentUser(), mod145, asyncCallback);
	}
	
	public void printMod145(Mod145 mod145, AsyncCallback<String> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.printMod145(getCurrentDomainName(), getCurrentUser(), mod145, asyncCallback);
	}
	
	// ------------------------------------------------ SistemaRED
	
	public void getCCCLaboralLife(String regime, String ccc, Date from, Date to, AsyncCallback<String> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.getCCCLaboralLife(getCurrentDomainName(), getCurrentUser(), regime, ccc, from, to, asyncCallback);
	}
	
	public void getLaboralLife(String regime, String ccc, String nss, AsyncCallback<String> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.getLaboralLife(getCurrentDomainName(), getCurrentUser(), regime, ccc, nss, asyncCallback);
	}
	
	public void getIdcCCC(String regime, String ccc, Date date, AsyncCallback<String> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.getIdcCCC(getCurrentDomainName(), getCurrentUser(), regime, ccc, date, asyncCallback);
	}
	
	public void getEmployeePrevMov(String regime, String ccc, AsyncCallback<String> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.getEmployeePrevMov(getCurrentDomainName(), getCurrentUser(), regime, ccc, asyncCallback);
	}
	
	public void getEmployeesWorking(String regime, String ccc, AsyncCallback<String> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.getEmployeesWorking(getCurrentDomainName(), getCurrentUser(), regime, ccc, asyncCallback);
	}
	
	public void getUpdateCert(String regime, String ccc, AsyncCallback<String> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.getUpdateCert(getCurrentDomainName(), getCurrentUser(), regime, ccc, asyncCallback);
	}
	
	// ------------------------------------------------ Country/Province
	
	public void getCountries(AsyncCallback<List<Country>> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.getCountries(getCurrentDomainName(), asyncCallback);
	}
	
	// ------------------------------------------------ MainMassiveContracts

	public void getMassiveCNOs(AsyncCallback<List<ContractData>> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.getMassiveCNOs(getCurrentDomainName(), getCurrentUser(), asyncCallback);
	}

	public void updateMassiveCNOs(List<ContractData> contractDatas, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.updateMassiveCNOs(getCurrentDomainName(), getCurrentUser(), contractDatas, asyncCallback);
	}
	
	public void duplicateContract(EmployeeContractInfo employee, Date newStartDate, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.duplicateContract(getCurrentDomainName(), getCurrentUser(), employee, newStartDate, asyncCallback);
	}
	
	public void duplicateContract(List<EmployeeContractInfo> employees, Date newStartDate, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.duplicateContract(getCurrentDomainName(), getCurrentUser(), employees, newStartDate, asyncCallback);
	}
	
	// ------------------------------------------------ Pension Plan AFI
	
	public void checkPensionPlanAFI(long date, List<Integer> cccIdList, AsyncCallback<String> asyncCallback) throws IllegalArgumentException {
		enterprisesServiceAsync.checkPensionPlanAFI(getCurrentDomainName(), getCurrentUser(), date, cccIdList, asyncCallback);
	}
	
	// ----------------------------------------------------------------- static
	
	private static String getToken() {
		return Wnd.getToken();
	}
	
	private static String getCurrentUser() {
		return Wnd.getCurrentUser();
	}

	private static String getCurrentDomainName() {
		return Wnd.getCurrentDomainNameURL();
	}

	

}
