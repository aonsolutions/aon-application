package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.payroll.shared.AFIChanges;
import com.esferalia.aon.gwt.payroll.shared.ActivitiesCCC;
import com.esferalia.aon.gwt.payroll.shared.ActivityInfo;
import com.esferalia.aon.gwt.payroll.shared.AgrarianJourney;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.Bonus;
import com.esferalia.aon.gwt.payroll.shared.CCCInfo;
import com.esferalia.aon.gwt.payroll.shared.CNO;
import com.esferalia.aon.gwt.payroll.shared.CRA;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.ContractAttach;
import com.esferalia.aon.gwt.payroll.shared.ContractClause;
import com.esferalia.aon.gwt.payroll.shared.ContractSpecificData;
import com.esferalia.aon.gwt.payroll.shared.Cost;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.DigitalCertificate;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseInfo;
import com.esferalia.aon.gwt.payroll.shared.EnterpriseStatus;
import com.esferalia.aon.gwt.payroll.shared.Extra;
import com.esferalia.aon.gwt.payroll.shared.ITEmployee;
import com.esferalia.aon.gwt.payroll.shared.MainCCCInfo;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Peculiarities;
import com.esferalia.aon.gwt.payroll.shared.SSBonusData;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceInfo;
import com.esferalia.aon.occam.api.model.MailAccount;
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

	Agreement getAgreement(String domain, Integer agreementId) ;

	List<Agreement> getAgreements(String domain, int offset, int limit) ;

	List<Enterprise> getEnterprises(String domain, String user, int offset, int limit) ;

	List<Bonus> getBonusConcepts(String domain, int offset, int limit) ;

	List<Payment> getPaymentConcepts(String domain, int offset, int limit) ;

	List<Deduction> getDeductionConcepts(String domain, int offset, int limit) ;
	
	List<Cost> getEnterprisesCosts(String domain, List<Integer> enterpriseIds); 
	
	List<Extra> getWorkplacesExtras(String domain, List<Integer> workplaceIds) ;

	List<Employee> getCCCEmployees(String domain, Date month, List<Integer> cccIds); 

	Integer getParentDomain(String domain );

	WorkplaceInfo getWorkplaceInfo(String domain, Integer workplaceId);

	WorkplaceInfo setWorkplaceInfo(String domain, WorkplaceInfo workplaceInfo);

	List<Workplace> getWorkplaces(Workplace workplace, String domain);

	ActivitiesCCC getActivitiesCCC(Workplace workplaceId, String currentDomainName);

	ActivityInfo getActivityInfoDataBase(Integer activityId, String domain);

	ActivityInfo updateActivityInfoDataBase(ActivityInfo activityInfo, String domain);

	ActivityInfo createActivityInfoDataBase(ActivityInfo activityInfo, String domain);

	Map<String, String> getCNAE2009(String domain);

	Map<Integer, String> getEnterpiseAddresses(Integer enterpriseId, String domain);
	
	Map<Integer, String> getEnterpiseCalendars(Integer enterpriseId, String domain);
	
	Map<Integer, String> getEnterpiseActivities(Integer enterpriseId, String domain);

	WorkplaceInfo createWorkplaceInfo(WorkplaceInfo workplaceInfo, Integer enterpriseId, String domain);

	Map<Integer, String> getEnterpiseScopes(Integer enterpriseId, String domain);

	EnterpriseInfo getEnterpriseInfo(Integer enterpriseId, String domain);

	EnterpriseInfo updateEnterprise(EnterpriseInfo enterpriseInfo, String domain);

	Map<Integer, List<AgrarianJourney>> getAgrarianJourney(long findingDate, List<String> cccList, String domain);

	List<CRA> getCRAs(String domain, String string, long liquidDateTime);

	String createNewCRA(String domainName, long findingDate, List<String> ccc, ArrayList<Integer> cccIdList,
			Integer cccId, String type);

	String deleteCRA(String currentDomainName, Integer code);

	Peculiarities getEmployeePeculiarities(String currentDomainName, Integer contractId);

	String setEmployeePeculiarities(String currentDomainName, Integer contractId, Peculiarities peculiarities);

	List<SSBonusData> getEmployeeSSBonuses(String currentDomainName, String currentUser, Integer contractId);

	List<SSBonusData> getBonusConcepts(String currentDomainName);

	List<SSBonusData> setEmployeeSSBonuses(String currentDomainName, Integer contractId, List<SSBonusData> ssBonuses);

	String setEmployeeAFIChanges(String currentDomainName, Integer contractId, AFIChanges afiChangesMap);

	AFIChanges getEmployeeAFIChanges(String currentDomainName, Integer contractId);

	List<MailAccount> getDomainMailAccounts(String currentDomainName, String currentUser);

	String getPayrollEmailSendTo(String currentDomainName, Integer enterpriseID);

	String getPayrollEmailBody(String currentDomainName, String paramsBase64);

	String sendPayrollEmail(String currentDomainName, String from, String to, String cc, String cco, String bodyHTML);

	String checkEmployeesEmails(String currentDomainName, ArrayList<Integer> salaryIds);

	String sendPayrollEmailToEmployees(String currentDomainName, String from, String cc, String cco, String bodyHTML,
			String completeURL);

	String checkCreateNewCRA(String currentDomainName, long findingDate, ArrayList<Integer> cccList);

	List<CCCInfo> getEnterprisesCCCInfo(String currentDomainName, String user, long findPeriodTime);

	List<EmployeeContractInfo> getEmployeesInfo(String currentDomainName, Boolean allEmployees);

	List<ITEmployee> getEmployeesITInfo(String currentDomainName, Boolean allEmployees);

	List<ITEmployee> getEmployeesITInfo(String currentDomainName, Integer ids []);

	String deleteIT(String currentDomainName, Integer itId);

	String createUpdateITEmployee(String currentDomainName, ITEmployee employeeITInfo);
	
	EnterpriseStatus getEnterpriseStatus(String domain, String user, Integer enterpriseId );

	List<ContractAttach> getContractAttachments(String currentDomainName, Integer contractId);
	
	List<ContractAttach> setContractAttachments(String currentDomainName, Integer contractId,
			List<ContractAttach> contractAttachments);

	List<ContractAttach> createContractAttach(String currentDomainName, ContractAttach contractAttach);

	List<ContractAttach> deleteContractAttach(String currentDomainName, ContractAttach contractAttach);

	List<ContractClause> getContractClauses(String currentDomainName, Integer contractId);

	List<ContractClause> setContractClauses(String currentDomainName, Integer contractId,
			List<ContractClause> contractClauses);
	
	List<ContractClause> createContractClause(String currentDomainName, ContractClause contractClause);

	List<ContractClause> deleteContractClause(String currentDomainName, ContractClause contractClause);

	Map<String, String> getContractOtherInfo(String currentDomainName, Integer contractId, String contractType);
	
	Map<String, String> setContractOtherInfo(String currentDomainName, Integer contractId, String contractType,
			Map<String, String> contractOtherData);
	
	ContractSpecificData getContractSpecificData(String currentDomainName, Integer contractId);

	void setContractSpecificData(String currentDomainName, EmployeeContractInfo employeeContractData);	
	
	Map<String, CNO> getCNOs(String currentDomainName);

	List<DigitalCertificate> getDigitalCertificates(String currentDomainName, String currentUser, String token);

	void setDigitalCertificates(String currentDomainName, String currentUser, String token, List<DigitalCertificate> digitalCertificateList);

	MainCCCInfo getMainCCCInfoDataBase(String currentDomainName, String currentUser);

	void setMainCCCInfoDataBase(String currentDomainName, String currentUser, MainCCCInfo mainCCCInfo);

	List<SSBonusData> getContractBonus(String currentDomainName, Integer contractId);

	void setContractBonus(String currentDomainName, EmployeeContractInfo employeeContractData);

	Map<String, String> getPayMethods(String currentDomainName);

	void deleteDigitalCertificate(String currentDomainName, String currentUser, Byte type);
	
}
