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
import com.esferalia.aon.occam.api.model.CertificateInfo;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.MailAccount;
import com.esferalia.aon.occam.api.model.aonsolutions.DomainUserRoles;
import com.esferalia.aon.occam.api.model.mod145.Mod145;
import com.esferalia.aon.occam.api.model.payroll.Activity;
import com.esferalia.aon.occam.api.model.payroll.ContractData;
import com.esferalia.aon.occam.api.model.security.CertificateType;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("enterprises")
public interface EnterprisesService extends RemoteService {
	
	Integer getDomain(String domain);
	
	Domain getDomain(String domain, String user);

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
	
	String getDeleteAgreementMessage(String domain, Agreement agreement);
	
	String getAgreementUsedInfo(String currentDomainName, Integer agreementId, String agreementDescription);

	void deleteBonusConcept(String domain, Bonus bonus);

	void deletePaymentConcept(String domain, Payment payment);

	void deleteDeductionConcept(String domain, Deduction deduction);
	
	void deleteAgreement(String domain, Agreement agreement);
	
	void deleteAgreements(String currentDomainName, List<Integer> agreementIds) throws IllegalArgumentException;
	
	void moveAgreement2Parent(String domain, Agreement agreement);
	
	void moveAgreement2Child(String domain, Integer agreementId) throws IllegalArgumentException;

	Agreement copyAgreement(String domain, Agreement agreement);

	Agreement getAgreement(String domain, Integer agreementId);

	List<Agreement> getAgreements(String domain, int offset, int limit) ;
	
	List<Agreement> getAgreements(String domain, boolean allAgreements);
	
	List<Agreement> getTrashAgreements(String currentDomainName, int offset, int limit);

	List<Enterprise> getEnterprises(String domain, String user, String condition, int offset, int limit) ;

	List<Bonus> getBonusConcepts(String domain, int offset, int limit) ;

	List<Payment> getPaymentConcepts(String domain, int offset, int limit) ;

	List<Deduction> getDeductionConcepts(String domain, int offset, int limit) ;
	
	List<Cost> getEnterprisesCosts(String domain, List<Integer> enterpriseIds); 
	
	List<Extra> getWorkplacesExtras(String domain, List<Integer> workplaceIds) ;

	List<Employee> getCCCEmployees(String domain, Date month, List<Integer> cccIds); 

	Integer getParentDomain(String domain );

	WorkplaceInfo getWorkplaceInfo(String domain, Integer workplaceId);

	WorkplaceInfo setWorkplaceInfo(String domain, WorkplaceInfo workplaceInfo);

	List<Workplace> getWorkplaces(String domain);
	
	Map<String, String> getPayMethods(String domain);

	ActivitiesCCC getActivityCCC(String domain);

	ActivityInfo getActivityInfoDataBase(Integer activityId, String domain);

	ActivityInfo updateActivityInfoDataBase(ActivityInfo activityInfo, String domain);

	ActivityInfo createActivityInfoDataBase(ActivityInfo activityInfo, String domain);

	Map<Integer, String> getCNAE2009(String domain);

	Map<Integer, String> getEnterpiseAddresses(Integer enterpriseId, String domain);
	
	Map<Integer, String> getEnterpiseCalendars(Integer enterpriseId, String domain);
	
	Map<Integer, String> getEnterpiseActivities(Integer enterpriseId, String domain);

	WorkplaceInfo createWorkplaceInfo(WorkplaceInfo workplaceInfo, Integer enterpriseId, String domain);

	Map<Integer, String> getEnterpiseScopes(Integer enterpriseId, String domain);

	Map<Integer, List<AgrarianJourney>> getAgrarianJourney(long findingDate, List<String> cccList, String domain);

	List<CRA> getCRAs(String domain, String string, long liquidDateTime);

	void createNewCRA(String domainName, String user, long findingDate, List<String> ccc, ArrayList<Integer> cccIdList, Integer cccId, String type) throws IllegalArgumentException;

	void deleteCRA(String currentDomainName, Integer code);

	Peculiarities getEmployeePeculiarities(String currentDomainName, Integer contractId);

	String setEmployeePeculiarities(String currentDomainName, Integer contractId, Peculiarities peculiarities);
	
	List<SSPECData> getEmployeeSSPECs(String currentDomainName, String currentUser, Integer contractId) throws IllegalArgumentException;
	
	List<SSPECData> syncEmployeeSSPECs(String currentDomainName, String currentUser, Integer contractId, Date startDate, Date endDate) throws IllegalArgumentException;

	List<SSBonusData> getBonusConcepts(String currentDomainName);

	List<SSBonusData> setEmployeeSSBonuses(String currentDomainName, Integer contractId, List<SSBonusData> ssBonuses);

	void setEmployeeAFIChanges(String currentDomainName, Integer contractId, AFIChanges afiChangesMap);

	AFIChanges getEmployeeAFIChanges(String currentDomainName, Integer contractId);

	List<MailAccount> getDomainMailAccounts(String currentDomainName, String currentUser);

	String getPayrollEmailSendTo(String currentDomainName);

	String getPayrollEmailBody(String currentDomainName, Type type, HashMap<String, String> params);

	String sendPayrollEmail(String currentDomainName, Type type, HashMap<String, String> params, String from, String to,
			String cc, String cco, String bodyHTML) throws IllegalArgumentException;

	String checkEmployeesEmails(String currentDomainName, ArrayList<Integer> salaryIds);
	
	String checkEnterprisesEmails(String currentDomainName, HashSet<Integer> enterpriseIds);
	
	String getSettlePDF(String currentDomainName, String user, Integer settleId) throws IllegalArgumentException;
	
	String getSalariesPDF(String currentDomainName, String currentUser, Integer enterpriseId, List<Integer> salaryIds) throws IllegalArgumentException;

	void checkCreateNewCRA(String currentDomainName, long findingDate, ArrayList<Integer> cccList) throws IllegalArgumentException;

	List<CCCInfo> getEnterprisesCCCInfo(String currentDomainName, String user, long findPeriodTime);

	List<EmployeeContractInfo> getEmployeesInfo(String currentDomainName, Boolean allEmployees);
	
	List<EmployeeContractInfo> getFJEmployeesInfo(String currentDomainName);

	List<ITEmployee> getEmployeesITInfo(String currentDomainName, Boolean allEmployees);

	List<ITEmployee> getEmployeesITInfo(String currentDomainName, Integer ids []);

	String deleteIT(String currentDomainName, Integer itId);

	String createUpdateITEmployee(String currentDomainName, ITEmployee employeeITInfo);
	
	EnterpriseStatus getEnterpriseStatus(String domain, String user, Integer enterpriseId );

	EnterpriseITStatus getEnterpriseITStatus(String domain, String user);

	// ------------------------------------------------ Contract Attachments
	
	List<Attach> getContractAttachments(String currentDomainName, String login, Integer contractId) throws IllegalArgumentException;
	
	void deleteContractAttach(String currentDomainName, String login, Integer attachId) throws IllegalArgumentException;

	String getAttachData(String currentDomainName, String currentUser, Integer attachId) throws IllegalArgumentException;

	void setAttachData(String currentDomainName, String currentUser, Integer attachId, String base64);

	void sendAttachEmail(String currentDomainName, String login, MailAccount emailFrom, String emailTo,
			List<String> ccTo, List<String> bccTo, String subject, String emailBody, List<Integer> attachIds);

	// ------------------------------------------------ Contract Clauses
	
	List<ContractClause> getContractClauses(String currentDomainName, Integer contractId) throws IllegalArgumentException ;

	void setContractClauses(String currentDomainName, Integer contractId, List<ContractClause> contractClauses) throws IllegalArgumentException ;
	
	void deleteContractClause(String currentDomainName, Integer clauseId) throws IllegalArgumentException;

	void importContractClauses(String currentDomainName, List<Integer> clausesIds, Integer contractId) throws IllegalArgumentException;

	List<ContractClause> getDomainClauses(String currentDomainName) throws IllegalArgumentException;
	
	Map<String, String> getContractOtherInfo(String currentDomainName, Integer contractId, Integer contractType);
	
	Map<String, String> setContractOtherInfo(String currentDomainName, Integer contractId, String contractType,
			Map<String, String> contractOtherData);
	
	ContractSpecificData getContractSpecificData(String currentDomainName, Integer contractId) throws IllegalArgumentException;

	void setContractSpecificData(String currentDomainName, EmployeeContractInfo employeeContractData) throws IllegalArgumentException;	
	
	Map<String, CNO> getCNOs(String currentDomainName);

	MainCCCInfo getMainCCCInfoDataBase(String currentDomainName, String currentUser);

	void setMainCCCInfoDataBase(String currentDomainName, String currentUser, MainCCCInfo mainCCCInfo);

	List<SSBonusData> getContractBonus(String currentDomainName, Integer contractId);

	void setContractBonus(String currentDomainName, EmployeeContractInfo employeeContractData);

	List<SecondaryUserCertificate> getSecondaryUsers(String currentDomainName, String currentUser, Integer rattachId) throws IllegalArgumentException;

	void deleteSecondaryUser(String currentDomainName, String currentUser, Integer rattachId, String ipfType, String ipf);

	void createSecondaryUser(String currentDomainName, String currentUser, Integer rattachId, String ipfType, String ipf, String naf);

	EmployeeSegSocial getIpfxNaf(String currentDomainName, String currentUser, ArrayList<String> nssList);

	EmployeeSegSocial getNafxIpf(String currentDomainName, String currentUser, String ipf, String apellido1,
			String apellido2);

	boolean createITCertificate(String currentDomainName, String currentUser, String affiliationNumber, String regime,
			String contributionAccount, String docType, String docNum, String applicantType, String reason,
			Date dateFrom, Date dateTo, float baseCC, float baseCP, int days);

	void deleteComunicateIT(String currentDomainName, String currentUser, String affiliationNumber, String regime,
			String contributionAccount, Date dateFrom, Date dateTo, Date startDate) throws IllegalArgumentException;

	void syncITs(String currentDomainName, String currentUser) throws IllegalArgumentException;
	
	void communicateITPart(String currentDomainName, String currentUser, ITEmployee itEmployee ,IT it, ITPart part) throws IllegalArgumentException;

	void saveITParts(String currentDomainName, String currentUser, List<ItNotExist> itNotExist) throws IllegalArgumentException;
	
	void removeITParts(String currentDomainName, String currentUser, List<ItNotExist> itNotExist) throws IllegalArgumentException;

	void setComunicationIT(String currentDomainName, String currentUser, ITEmployee itEmployee, IT it);

	int getServiAgreement(String currentDomainName, String userLogin, String serviAgreementCode,
			List<Integer> selectedDates);
	
	List<Integer> getServiAgreementDates(String serviAgreementCode) throws IllegalArgumentException;

	boolean checkIfRectificative(String currentDomainName, Date findingDate, ArrayList<Integer> selectedCCCList);

	void registerITBaja(String domainName, String userLogin, String regime, String ccc, String naf, String contingency,
			String situation_employee, String licenseNumber, String cias, String occupation, Date startdate,
			String contractType, float baseCot, int cotDays, Date fATEP, String accidentType);

	void registerITConfirmation(String domainName, String userLogin, String regime, String ccc, String naf, String contingency,
			String situation_employee, String licenseNumber, String cias, Date fbaja,
			Date fconfirmation, String npartConfimation);

	void registerITAlta(String domainName, String userLogin, String regime, String ccc, String naf, String contingency,
			String situation_employee, String licenseNumber, String cias, Date fbaja,
			Date falta, Date fATEP, String accidentType, String causeType);

	EmployeeContractInfo getEmployeeInfo(String currentDomainName, Integer contractId);

	String getContratoSepe(String currentDomainName, String currentUser, String ipf, Date startDate, Date endDate);

	List<EmployeeContractInfo> getTrashEmployeesInfo(String currentDomainName);

	void restoreContract(String currentDomainName, Integer contractId);

	void delete4EverContract(String currentDomainName, Integer contractId);

	Map<String, String> getServiAgreements(String currentDomainName, String currentUser);

	DomainUserRoles getDomainUserRoles(String currentDomainName, String currentUser);

	boolean hasCertificateSEPE(String currentDomainName, String currentUser);

	String getDeleteCCCMessage(String currentDomainName, ArrayList<Integer> cccIds);

	List<ITEmployee> getWorkplaceEmployeeITInfo(String currentDomainName, Boolean allEmployees, Integer workplaceId);

	ComunicaEnterpriseSettings getComunicaEnterpriseSettings(String currentDomainName, String currentUser) throws IllegalArgumentException;

	void setComunicaEnterpriseSettings(String currentDomainName, String currentUser,
			ComunicaEnterpriseSettings comunicaEnterpriseSettings);

	Integer getEnterpriseId(String currentDomainName);

	void verifyCertificate(String currentDomainName, String currentUser, com.esferalia.aon.gwt.payroll.shared.DigitalCertificate.CertificateType certificateType) throws IllegalArgumentException;

	List<ITEmployee> getEmployeeITInfo(String currentDomainName, Integer contractId);

	ContractConcepts getAllConcepts(String currentDomainName, String currentUser);
	
	// --------------------------- Certificates

	List<Certificate> getCertificates(String domain, String login, boolean withParent);

	void deleteCertificate(String domain, String login, Certificate certificate) throws IllegalArgumentException;
	
	void downloadCertificate(String domain, String login, Integer certificateId, String filePath) throws IllegalArgumentException;

	CertificateInfo getCertificateInfo(String domain, String login, Integer certitificateId) throws IllegalArgumentException ;

	void verifyCertificate(String currentDomainName, String currentUser, Integer rattachId, List<CertificateType> tags) throws IllegalArgumentException;

	List<SecondaryUserCertificate> getSecondaryUsers(String currentDomainName, String currentUser) throws IllegalArgumentException;

	void deleteSecondaryUser(String currentDomainName, String currentUser, String ipfType, String ipf) throws IllegalArgumentException;

	void createSecondaryUser(String currentDomainName, String currentUser, String ipfType, String ipf, String naf) throws IllegalArgumentException;

	// --------------------------- Enterprise Context
	
	EnterpriseContext getEnterpriseContext(String currentDomainName);

	List<SSBonusData> syncSSBonus(String currentDomainName, String currentUser, Integer contractId) throws IllegalArgumentException;

	List<SSBonusData> getEmployeeSSBonuses(String currentDomainName, Integer contractId) throws IllegalArgumentException;

	// --------------------------- Agreements Clean
	
	List<AgreementsClean> getAgreementsClean(String currentDomainName, AgreementCleanType cleanType) throws IllegalArgumentException;
	
	// --------------------------- Agreements Tabs (New)
	
	AgreementInfo getAgreementInfo(String currentDomainName, Integer agreementId, boolean withContracts);

	void setAgreementInfo(String currentDomainName, AgreementInfo agreementInfo) throws IllegalArgumentException;

	Set<String> getAgreementVariables(String currentDomainName, AgreementInfo agreementInfo) throws IllegalArgumentException;
	
	String getAgreementDraftReceipt(String currentDomainName, AgreementInfo agreement, List<Variable> context, int levelId, String mime) throws IllegalArgumentException;

	void checkAndUpdateServiAgreement(String currentDomainName, String currentUser, AgreementInfo agreement) throws IllegalArgumentException;
	
	boolean canUpdateServiAgreement(String currentDomainName, String currentUser, AgreementInfo agreement) throws IllegalArgumentException;

	void deletePayments(String currentDomainName, List<Integer> paymentIds) throws IllegalArgumentException;
	
	ContextDescriptor getContext(String currentDomainName, AgreementInfo agreementInfo) throws IllegalArgumentException;
	
	List<Result> eval(String currentDomainName, String expression, AgreementInfo agreementInfo) throws IllegalArgumentException;

	// --------------------------- Enterprise (API)
	
	com.esferalia.aon.occam.api.model.payroll.Enterprise getEnterprise(String currentDomainName, String user, Integer id) throws IllegalArgumentException;
	
	void saveEnterprise(String currentDomainName, String user, com.esferalia.aon.occam.api.model.payroll.Enterprise enterprise) throws IllegalArgumentException;

	// --------------------------- EnterpriseActivity (API)
	
	Activity getActivity(String currentDomainName, String user, Integer id) throws IllegalArgumentException;

	void saveActivity(String currentDomainName, String user, Activity activity) throws IllegalArgumentException;

	List<Activity> getActivities(String currentDomainName, String user) throws IllegalArgumentException;

	void saveActivities(String currentDomainName, String user, List<Activity> activity) throws IllegalArgumentException;

	// --------------------------- Mod 145 (API)
	
	List<Mod145> getMod145List(String currentDomainName, String currentUser, Integer contractId) throws IllegalArgumentException;

	Mod145 getMod145(String currentDomainName, String currentUser, Integer id) throws IllegalArgumentException;

	void saveMod145(String currentDomainName, String currentUser, Mod145 mod145) throws IllegalArgumentException;

	String printMod145(String currentDomainName, String currentUser, Mod145 mod145) throws IllegalArgumentException;

	// ------------------------------------------------  SistemaRED
	
	String getCCCLaboralLife(String currentDomainName, String currentUser, String regime, String ccc, Date from, Date to) throws IllegalArgumentException;

	String getLaboralLife(String currentDomainName, String currentUser, String regime, String ccc, String nss) throws IllegalArgumentException;
	
	String getIdcCCC(String currentDomainName, String currentUser, String regime, String ccc, Date date) throws IllegalArgumentException;
	
	String getEmployeePrevMov(String currentDomainName, String currentUser, String regime, String ccc) throws IllegalArgumentException;

	String getEmployeesWorking(String currentDomainName, String currentUser, String regime, String ccc) throws IllegalArgumentException;

	String getUpdateCert(String currentDomainName, String currentUser, String regime, String ccc) throws IllegalArgumentException;

	// ------------------------------------------------ Country/Province
	
	List<Country> getCountries(String currentDomainName) throws IllegalArgumentException;
	
	// ------------------------------------------------ MainMassiveContracts

	List<ContractData> getMassiveCNOs(String currentDomainName, String currentUser) throws IllegalArgumentException;

	void updateMassiveCNOs(String currentDomainName, String currentUser, List<ContractData> contractDatas) throws IllegalArgumentException;
	
	void duplicateContract(String currentDomainName, String currentUser, EmployeeContractInfo employee, Date newStartDate) throws IllegalArgumentException;
	
	void duplicateContract(String currentDomainName, String currentUser, List<EmployeeContractInfo> employees, Date newStartDate) throws IllegalArgumentException;

}
