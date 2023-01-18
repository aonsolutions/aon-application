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
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>EnterprisesService</code>.
 */
public interface EnterprisesServiceAsync {
	void getDomain(String domain, AsyncCallback<Integer> callback);
	void getDomain(String domain, String user, AsyncCallback<Domain> callback);
	void getContext(String domain, AsyncCallback<ContextDescriptor> callback);
	void saveBonusConcept(String domain, Bonus bonus, AsyncCallback<Bonus> callback);
	void savePaymentConcept(String domain, Payment payment, AsyncCallback<Payment> callback);
	void saveDeductionConcept(String domain, Deduction deduction, AsyncCallback<Deduction> callback);
	void deleteBonusConcept(String domain, Bonus bonus, AsyncCallback<Void> callback);
	void deletePaymentConcept(String domain, Payment payment, AsyncCallback<Void> callback);
	void deleteDeductionConcept(String domain, Deduction deduction, AsyncCallback<Void> callback);
	void deleteAgreement(String domain, Agreement agreement, AsyncCallback<Void> callback);
	void deleteAgreements(String currentDomainName, List<Integer> agreementIds, AsyncCallback<Void> callback) throws IllegalArgumentException;
	void updateAgreementId(String domain, Agreement agreement, AsyncCallback<Void> callback);
	void getDeleteAgreementMessage(String domain, Agreement agreement, AsyncCallback<String> callback);
	void getAgreementUsedInfo(String currentDomainName, Integer agreementId, String agreementDescription, AsyncCallback<String> callback) throws IllegalArgumentException;
	void copyAgreement(String domain, Agreement agreement, AsyncCallback<Agreement> callback);
	void getWorkplacesExtras(String domain, List<Integer> workplaceIds, AsyncCallback<List<Extra>> callback);
	void getAgreement(String domain, Integer agreementId, AsyncCallback<Agreement> callback);
	void getAgreements(String domain, int offset , int limit, AsyncCallback<List<Agreement>> callback);
	void getAgreements(String domain, boolean allAgreements, AsyncCallback<List<Agreement>> callback);
	void getTrashAgreements(String currentDomainName, int offset, int limit, AsyncCallback<List<Agreement>> callback);
	void getEnterprises(String domain, String user,int offset , int limit, AsyncCallback<List<Enterprise>> callback);
	void getEnterprisesCosts(String domain, List<Integer> enterpriseIds, AsyncCallback<List<Cost>> callback);
	void getBonusConcepts(String domain, int offset , int limit, AsyncCallback<List<Bonus>> callback);
	void getCCCEmployees(String domain, Date month, List<Integer> cccIds, AsyncCallback<List<Employee>> callback ); 
	void getPaymentConcepts(String domain, int offset , int limit, AsyncCallback<List<Payment>> callback);
	void getDeductionConcepts(String domain, int offset , int limit, AsyncCallback<List<Deduction>> callback);
	void moveAgreement2Parent(String domain, Agreement agreement, AsyncCallback<Void> callback);
	void moveAgreement2Child(String domain, Integer agreementId, AsyncCallback<Void> callback) throws IllegalArgumentException;
	void getParentDomain(String domain, AsyncCallback<Integer> callback);
	void getWorkplaceInfo(String domain, Integer workplaceId, AsyncCallback<WorkplaceInfo> asyncCallback);
	void setWorkplaceInfo(String domain, WorkplaceInfo workplaceInfo, AsyncCallback<WorkplaceInfo> asyncCallback);
	void getWorkplaces(String domain, AsyncCallback<List<Workplace>> asyncCallback);
	void getPayMethods(String domain, AsyncCallback<Map<String, String>> asyncCallback);
	void getActivityCCC(String domain, AsyncCallback<ActivitiesCCC> asyncCallback);
	void getActivityInfoDataBase(Integer activityId, String domain, AsyncCallback<ActivityInfo> asyncCallback);
	void updateActivityInfoDataBase(ActivityInfo activityInfo, String domain, AsyncCallback<ActivityInfo> asyncCallback);
	void createActivityInfoDataBase(ActivityInfo activityInfo, String domain, AsyncCallback<ActivityInfo> asyncCallback);
	void getDeleteCCCMessage(String currentDomainName, ArrayList<Integer> cccIds, AsyncCallback<String> asyncCallback);
	void getCNAE2009(String domain, AsyncCallback<Map<Integer, String>> asyncCallback);
	void getEnterpiseAddresses(Integer enterpriseId, String domain, AsyncCallback<Map<Integer, String>> asyncCallback);
	void getEnterpiseCalendars(Integer enterpriseId, String domain, AsyncCallback<Map<Integer, String>> asyncCallback);
	void getEnterpiseActivities(Integer enterpriseId, String domain, AsyncCallback<Map<Integer, String>> asyncCallback);
	void createWorkplaceInfo(WorkplaceInfo workplaceInfo, Integer enterpriseId, String domain, AsyncCallback<WorkplaceInfo> asyncCallback);
	void getEnterpiseScopes(Integer enterpriseId, String domain, AsyncCallback<Map<Integer, String>> asyncCallback);
	void getAgrarianJourney(long findingDate, List<String> cccList, String domain, AsyncCallback<Map<Integer, List<AgrarianJourney>>> asyncCallback);
	void getCRAs(String domain, String string, long liquidDateTime, AsyncCallback<List<CRA>> asyncCallback);
	void createNewCRA(String domainName, String user, long findingDate, List<String> ccc, ArrayList<Integer> cccIdList, Integer cccId, String type, AsyncCallback<String> asyncCallback);
	void deleteCRA(String currentDomainName, Integer code, AsyncCallback<Void> asyncCallback);
	void getEmployeePeculiarities(String currentDomainName, Integer contractId, AsyncCallback<Peculiarities> asyncCallback);
	void setEmployeePeculiarities(String currentDomainName, Integer contractId, Peculiarities peculiarities,
			AsyncCallback<String> asyncCallback);
	void getEmployeeSSPECs(String currentDomainName, String currentUser, Integer contractId, AsyncCallback<List<SSPECData>> asyncCallback) throws IllegalArgumentException;
	void syncEmployeeSSPECs(String currentDomainName, String currentUser, Integer contractId, Date startDate, Date endDate, AsyncCallback<List<SSPECData>> asyncCallback) throws IllegalArgumentException;
	void getBonusConcepts(String currentDomainName, AsyncCallback<List<SSBonusData>> asyncCallback);
	void setEmployeeSSBonuses(String currentDomainName, Integer contractId, List<SSBonusData> ssBonuses,
			AsyncCallback<List<SSBonusData>> asyncCallback);
	void setEmployeeAFIChanges(String currentDomainName, Integer contractId, AFIChanges afiChangesMap,
			AsyncCallback<Void> asyncCallback);
	void getEmployeeAFIChanges(String currentDomainName, Integer contractId, AsyncCallback<AFIChanges> asyncCallback);
	void getDomainMailAccounts(String currentDomainName, String currentUser, AsyncCallback<List<MailAccount>> asyncCallback);
	void getPayrollEmailSendTo(String currentDomainName, AsyncCallback<String> asyncCallback);
	void getPayrollEmailBody(String currentDomainName, Type type, HashMap<String, String> params, AsyncCallback<String> asyncCallback);
	void sendPayrollEmail(String currentDomainName, Type type, HashMap<String, String> params, String from, String to, String cc, String cco, String bodyHTML,
			AsyncCallback<String> asyncCallback) throws IllegalArgumentException;
	void checkEmployeesEmails(String currentDomainName, ArrayList<Integer> salaryIds, AsyncCallback<String> asyncCallback);
	void checkEnterprisesEmails(String currentDomainName, HashSet<Integer> enterpriseIds, AsyncCallback<String> asyncCallback);
	void getSettlePDF(String currentDomainName, String user, Integer settleId, AsyncCallback<String> asyncCallback) throws IllegalArgumentException;
	void getSalariesPDF(String currentDomainName, String currentUser, Integer enterpriseId, List<Integer> salaryIds, AsyncCallback<String> asyncCallback) throws IllegalArgumentException;
	void checkCreateNewCRA(String currentDomainName, long findingDate, ArrayList<Integer> cccList,
			AsyncCallback<String> asyncCallback);
	void getEnterprisesCCCInfo(String currentDomainName, String user, long findPeriodTime, AsyncCallback<List<CCCInfo>> asyncCallback);
	void getEmployeesInfo(String currentDomainName, Boolean allEmployees, AsyncCallback<List<EmployeeContractInfo>> asyncCallback);
	void getEmployeesITInfo(String currentDomainName, Boolean allEmployees,
			AsyncCallback<List<ITEmployee>> asyncCallback);
	void getEmployeesITInfo(String currentDomainName, Integer ids [],
			AsyncCallback<List<ITEmployee>> asyncCallback);
	void deleteIT(String currentDomainName, Integer itId, AsyncCallback<String> asyncCallback);
	void createUpdateITEmployee(String currentDomainName, ITEmployee employeeITInfo,
			AsyncCallback<String> asyncCallback);
	void getEnterpriseStatus(String domain, String user, Integer enterpriseId , AsyncCallback<EnterpriseStatus> callback);

	void getEnterpriseITStatus(String domain, String user, AsyncCallback<EnterpriseITStatus> callback);

	// ------------------------------------------------ Contract Attachments
	
	void getContractAttachments(String currentDomainName, String login, Integer contractId, AsyncCallback<List<Attach>> asyncCallback) throws IllegalArgumentException;
	void deleteContractAttach(String currentDomainName, String login, Integer attachId, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException;
	void getAttachData(String currentDomainName, String currentUser, Integer attachId, AsyncCallback<String> asyncCallback) throws IllegalArgumentException;
	void setAttachData(String currentDomainName, String currentUser, Integer attachId, String base64, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException;
	void sendAttachEmail(String currentDomainName, String login, MailAccount emailFrom, String emailTo, List<String> ccTo, List<String> bccTo, String subject, String emailBody, List<Integer> attachIds, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException;
	
	// ------------------------------------------------ Contract Caluses
	
	void getContractClauses(String currentDomainName, Integer contractId, AsyncCallback<List<ContractClause>> asyncCallback) throws IllegalArgumentException ;
	void setContractClauses(String currentDomainName, Integer contractId, List<ContractClause> contractClauses, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException ;
	void deleteContractClause(String currentDomainName, Integer clauseId, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException;
	void importContractClauses(String currentDomainName, List<Integer> clausesIds, Integer contractId, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException;
	void getDomainClauses(String currentDomainName, AsyncCallback<List<ContractClause>> asyncCallback) throws IllegalArgumentException;
	void getContractOtherInfo(String currentDomainName, Integer contractId, Integer contractType, AsyncCallback<Map<String, String>> asyncCallback);
	void setContractOtherInfo(String currentDomainName, Integer contractId, String contractType,
			Map<String, String> contractOtherData, AsyncCallback<Map<String, String>> asyncCallback);
	void getContractSpecificData(String currentDomainName, Integer contractId, AsyncCallback<ContractSpecificData> asyncCallback) throws IllegalArgumentException;
	void setContractSpecificData(String currentDomainName, EmployeeContractInfo employeeContractData, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException;
	void getCNOs(String currentDomainName, AsyncCallback<Map<String, CNO>> asyncCallback);
	void getMainCCCInfoDataBase(String currentDomainName, String currentUser, AsyncCallback<MainCCCInfo> asyncCallback);
	void setMainCCCInfoDataBase(String currentDomainName, String currentUser, MainCCCInfo mainCCCInfo, AsyncCallback<Void> asyncCallback);
	void getContractBonus(String currentDomainName, Integer contractId, AsyncCallback<List<SSBonusData>> asyncCallback);
	void setContractBonus(String currentDomainName, EmployeeContractInfo employeeContractData, AsyncCallback<Void> asyncCallback);
	void getSecondaryUsers(String currentDomainName, String currentUser, Integer rattachId, AsyncCallback<List<SecondaryUserCertificate>> asyncCallback) throws IllegalArgumentException;
	void deleteSecondaryUser(String currentDomainName, String currentUser, Integer rattachId, String ipfType, String ipf, AsyncCallback<Void> asyncCallback);
	void createSecondaryUser(String currentDomainName, String currentUser, Integer rattachId, String ipfType, String ipf, String naf, AsyncCallback<Void> asyncCallback);
	void getIpfxNaf(String currentDomainName, String currentUser, ArrayList<String> nssList, AsyncCallback<EmployeeSegSocial> asyncCallback);
	void getNafxIpf(String currentDomainName, String currentUser, String ipf, String apellido1, String apellido2, AsyncCallback<EmployeeSegSocial> asyncCallback);
	void createITCertificate(String currentDomainName, String currentUser, String affiliationNumber, String regime,
			String contributionAccount, String docType, String docNum, String applicantType, String reason,
			Date dateFrom, Date dateTo, float baseCC, float baseCP, int days, AsyncCallback<Boolean> asyncCallback);
	void deleteComunicateIT(String currentDomainName, String currentUser, String affiliationNumber, String regime,
			String contributionAccount, Date dateFrom, Date dateTo, Date startDate, AsyncCallback<Void> asyncCallback);
	void syncITs(String currentDomainName, String currentUser, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException;
	void setComunicationIT(String currentDomainName, String currentUser, ITEmployee itEmployee, IT it,
			AsyncCallback<Void> asyncCallback);
	void getServiAgreement(String currentDomainName, String userLogin, String serviAgreementCode, List<Integer> selectedDates, AsyncCallback<Integer> asyncCallback) throws IllegalArgumentException;
	void getServiAgreementDates(String serviAgreementCode, AsyncCallback<List<Integer>> asyncCallback) throws IllegalArgumentException;
	void checkIfRectificative(String currentDomainName, Date findingDate, ArrayList<Integer> selectedCCCList,
			AsyncCallback<Boolean> asyncCallback);
	void registerITBaja(String domainName, String userLogin, String regime, String ccc, String naf, String contingency,
			String situation_employee, String licenseNumber, String cias,
			String occupation, Date startdate, String contractType, float baseCot, int cotDays,
			Date fATEP, String accidentType, AsyncCallback<Void> asyncCallback);
	void registerITConfirmation(String domainName, String userLogin, String regime, String ccc, String naf, String contingency,
			String situation_employee, String licenseNumber, String cias, Date fbaja,
			Date fconfirmation, String npartConfimation, AsyncCallback<Void> asyncCallback);
	void registerITAlta(String domainName, String userLogin, String regime, String ccc, String naf, String contingency,
			String situation_employee, String licenseNumber, String cias, Date fbaja,
			Date falta, Date fATEP, String accidentType, String causeType,
			AsyncCallback<Void> asyncCallback);
	void getEmployeeInfo(String currentDomainName, Integer contractId,
			AsyncCallback<EmployeeContractInfo> asyncCallback);
	void getContratoSepe(String currentDomainName, String currentUser, String ipf, Date startDate, Date endDate,
			AsyncCallback<String> asyncCallback);
	void getTrashEmployeesInfo(String currentDomainName, AsyncCallback<List<EmployeeContractInfo>> asyncCallback);
	void restoreContract(String currentDomainName, Integer contractId, AsyncCallback<Void> asyncCallback);
	void delete4EverContract(String currentDomainName, Integer contractId, AsyncCallback<Void> asyncCallback);
	void getServiAgreements(String currentDomainName, String currentUser, AsyncCallback<Map<String, String>> asyncCallback);
	void getDomainUserRoles(String currentDomainName, String currentUser, AsyncCallback<DomainUserRoles> asyncCallback);
	void hasCertificateSEPE(String currentDomainName, String currentUser, AsyncCallback<Boolean> asyncCallback);
	void getWorkplaceEmployeeITInfo(String currentDomainName, Boolean allEmployees, Integer workplaceId, AsyncCallback<List<ITEmployee>> asyncCallback);
	void getComunicaEnterpriseSettings(String currentDomainName, String currentUser, AsyncCallback<ComunicaEnterpriseSettings> asyncCallback) throws IllegalArgumentException;
	void setComunicaEnterpriseSettings(String currentDomainName, String currentUser, ComunicaEnterpriseSettings comunicaEnterpriseSettings, AsyncCallback<Void> asyncCallback);
	void getEnterpriseId(String currentDomainName, AsyncCallback<Integer> asyncCallback);
	void verifyCertificate(String currentDomainName, String currentUser, com.esferalia.aon.gwt.payroll.shared.DigitalCertificate.CertificateType certificateType,AsyncCallback<Void> asyncCallback) throws IllegalArgumentException;
	void getEmployeeITInfo(String currentDomainName, Integer contractId, AsyncCallback<List<ITEmployee>> asyncCallback);
	void getAllConcepts(String currentDomainName, String currentUser, AsyncCallback<ContractConcepts> asyncCallback);
	
	// --------------------------- Certificates
	
	void getCertificates(String domain, String login, boolean withParent, AsyncCallback<List<Certificate>> asyncCallback) throws IllegalArgumentException;
	void deleteCertificate(String domain, String login, Certificate certificate, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException;
	void getCertificateInfo(String domain, String login, Integer certitificateId, AsyncCallback<CertificateInfo> asyncCallback) throws IllegalArgumentException ;
	void verifyCertificate(String currentDomainName, String currentUser, Integer rattachId, List<CertificateType> tags, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException ;
	void getSecondaryUsers(String currentDomainName, String currentUser, AsyncCallback<List<SecondaryUserCertificate>> asyncCallback) throws IllegalArgumentException;
	void deleteSecondaryUser(String currentDomainName, String currentUser, String ipfType, String ipf, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException;
	void createSecondaryUser(String currentDomainName, String currentUser, String ipfType, String ipf, String naf, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException;
	
	// --------------------------- Enterprise Context
	
	void getEnterpriseContext(String currentDomainName, AsyncCallback<EnterpriseContext> asyncCallback);
	
	void syncSSBonus(String currentDomainName, String currentUser, Integer contractId, AsyncCallback<List<SSBonusData>> asyncCallback) throws IllegalArgumentException;
	void getEmployeeSSBonuses(String currentDomainName, Integer contractId, AsyncCallback<List<SSBonusData>> asyncCallback) throws IllegalArgumentException;
	
	void communicateITPart(String currentDomainName, String currentUser, ITEmployee itEmployee, IT it, ITPart part, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException;	

	void saveITParts(String currentDomainName, String currentUser, List<ItNotExist> itNotExist, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException;

	void removeITParts(String currentDomainName, String currentUser, List<ItNotExist> itNotExist, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException;
	
	// --------------------------- Agreements Clean
	
	void getAgreementsClean(String currentDomainName, AgreementCleanType cleanType, AsyncCallback<List<AgreementsClean>> asyncCallback) throws IllegalArgumentException;
	
	// --------------------------- Agreements Tabs (New)
	
	void getAgreementInfo(String currentDomainName, Integer agreementId, boolean withContracts, AsyncCallback<AgreementInfo> asyncCallback) throws IllegalArgumentException;
	
	void setAgreementInfo(String currentDomainName, AgreementInfo agreementInfo, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException;
	
	void getAgreementVariables(String currentDomainName, AgreementInfo agreementInfo, AsyncCallback<Set<String>> asyncCallback) throws IllegalArgumentException;
	
	void getAgreementDraftReceipt(String currentDomainName, AgreementInfo agreement, List<Variable> context, int levelId, String mime, AsyncCallback<String> asyncCallback) throws IllegalArgumentException;
	
	void checkAndUpdateServiAgreement(String currentDomainName, String currentUser, AgreementInfo agreement, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException;
	
	void deletePayments(String currentDomainName, List<Integer> paymentIds, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException;
	
	void getContext(String currentDomainName, AgreementInfo agreementInfo, AsyncCallback<ContextDescriptor> callback) throws IllegalArgumentException;
	
	void eval(String currentDomainName, String expression, AgreementInfo agreementInfo, AsyncCallback<List<Result>> callback) throws IllegalArgumentException;
	
	// --------------------------- Enterprise (API)
	
	void getEnterprise(String currentDomainName, String user, Integer id, AsyncCallback<com.esferalia.aon.occam.api.model.payroll.Enterprise> asyncCallback) throws IllegalArgumentException;
	
	void saveEnterprise(String currentDomainName, String user, com.esferalia.aon.occam.api.model.payroll.Enterprise enterprise, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException;
	
	// --------------------------- EnterpriseActivity (API)
	
	void getActivity(String currentDomainName, String user, Integer id, AsyncCallback<com.esferalia.aon.occam.api.model.payroll.Activity> asyncCallback) throws IllegalArgumentException;
	
	void saveActivity(String currentDomainName, String user, com.esferalia.aon.occam.api.model.payroll.Activity activity, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException;
	
	void getActivities(String currentDomainName, String user, AsyncCallback<List<com.esferalia.aon.occam.api.model.payroll.Activity>> asyncCallback) throws IllegalArgumentException;
	
	void saveActivities(String currentDomainName, String user, List<com.esferalia.aon.occam.api.model.payroll.Activity> activity, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException;
	
	// --------------------------- Mod 145 (API)
	
	void getMod145List(String currentDomainName, String currentUser, Integer contractId, AsyncCallback<List<Mod145>> asyncCallback) throws IllegalArgumentException;
	
	void getMod145(String currentDomainName, String currentUser, Integer id, AsyncCallback<Mod145> asyncCallback) throws IllegalArgumentException;
	
	void saveMod145(String currentDomainName, String currentUser, Mod145 mod145, AsyncCallback<Void> asyncCallback) throws IllegalArgumentException;
	
	void printMod145(String currentDomainName, String currentUser, Mod145 mod145, AsyncCallback<String> asyncCallback) throws IllegalArgumentException;
	
	// ------------------------------------------------ SistemaRED
	
	void getCCCLaboralLife(String currentDomainName, String currentUser, String regime, String ccc, Date from, Date to, AsyncCallback<String> asyncCallback) throws IllegalArgumentException;
	
	void getLaboralLife(String currentDomainName, String currentUser, String regime, String ccc, String nss, AsyncCallback<String> asyncCallback) throws IllegalArgumentException;
	
	void getIdcCCC(String currentDomainName, String currentUser, String regime, String ccc, Date date, AsyncCallback<String> asyncCallback) throws IllegalArgumentException;
	
	void getEmployeePrevMov(String currentDomainName, String currentUser, String regime, String ccc, AsyncCallback<String> asyncCallback) throws IllegalArgumentException;
	
	void getEmployeesWorking(String currentDomainName, String currentUser, String regime, String ccc, AsyncCallback<String> asyncCallback) throws IllegalArgumentException;
	
	void getUpdateCert(String currentDomainName, String currentUser, String regime, String ccc, AsyncCallback<String> asyncCallback) throws IllegalArgumentException;
	
}
