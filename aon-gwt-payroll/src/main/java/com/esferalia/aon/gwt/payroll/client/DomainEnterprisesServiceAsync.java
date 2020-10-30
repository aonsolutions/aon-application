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
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Peculiarities;
import com.esferalia.aon.gwt.payroll.shared.SSBonusData;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceInfo;
import com.esferalia.aon.occam.api.model.MailAccount;
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

	public void getAgreement(Integer agreementId, AsyncCallback<Agreement> callback) {
		enterprisesServiceAsync.getAgreement(getCurrentDomainName(), agreementId, callback);
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
	
	public void getWorkplaces(Workplace workplace, AsyncCallback<List<Workplace>> asyncCallback) {
		enterprisesServiceAsync.getWorkplaces(workplace, getCurrentDomainName(), asyncCallback);
	}
	
	public void getActivitiesCCC(Workplace workplace, AsyncCallback<ActivitiesCCC> asyncCallback) {
		enterprisesServiceAsync.getActivitiesCCC(workplace, getCurrentDomainName(), asyncCallback);
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
	
	public void getEmployeeAgrarianJourney(long findingDate, List<String> cccList, AsyncCallback<Map<Integer, List<AgrarianJourney>>> asyncCallback) {
		enterprisesServiceAsync.getAgrarianJourney(findingDate, cccList, getCurrentDomainName(), asyncCallback);
	}
	
	public void getCRAs(AsyncCallback<List<CRA>> asyncCallback) {
		enterprisesServiceAsync.getCRAs(getCurrentDomainName(), getCurrentUser(), asyncCallback);
	}
	
	public void createNewCRA(long findingDate, List<String> cccList, ArrayList<Integer> cccIdList, Integer cccId, String type, AsyncCallback<String> asyncCallback) {
		enterprisesServiceAsync.createNewCRA(getCurrentDomainName(), findingDate, cccList, cccIdList, cccId, type, asyncCallback);
	}
	
	public void checkCreateNewCRA(long findingDate, ArrayList<Integer> cccList, AsyncCallback<String> asyncCallback) {
		enterprisesServiceAsync.checkCreateNewCRA(getCurrentDomainName(), findingDate, cccList, asyncCallback);
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
	
	public void getDomainMailAccounts(AsyncCallback<List<MailAccount>> asyncCallback) {
		enterprisesServiceAsync.getDomainMailAccounts(getCurrentDomainName(), getCurrentUser(), asyncCallback);
	}

	public void getPayrollEmailSendTo(Integer enterpriseID, AsyncCallback<String> asyncCallback) {
		enterprisesServiceAsync.getPayrollEmailSendTo(getCurrentDomainName(), enterpriseID, asyncCallback);
	}
	
	public void getPayrollEmailBody(String paramsBase64, AsyncCallback<String> asyncCallback) {
		enterprisesServiceAsync.getPayrollEmailBody(getCurrentDomainName(), paramsBase64, asyncCallback);
	}
	
	public void sendPayrollEmail(String from, String to, String cc, String cco, String bodyHTML, AsyncCallback<String> asyncCallback) {
		enterprisesServiceAsync.sendPayrollEmail(getCurrentDomainName(), from, to, cc, cco, bodyHTML, asyncCallback);
	}
	
	public void checkEmployeesEmails(ArrayList<Integer> salaryIds, AsyncCallback<String> asyncCallback) {
		enterprisesServiceAsync.checkEmployeesEmails(getCurrentDomainName(), salaryIds, asyncCallback);
	}
	
	public void sendPayrollEmailToEmployees(String from, String cc, String cco, String bodyHTML, String completeURL, AsyncCallback<String> asyncCallback) {
		enterprisesServiceAsync.sendPayrollEmailToEmployees(getCurrentDomainName(), from, cc, cco, bodyHTML, completeURL, asyncCallback);
	}
	
	public void getEnterprisesCCCInfo(long findPeriodTime, AsyncCallback<List<CCCInfo>> asyncCallback) {
		enterprisesServiceAsync.getEnterprisesCCCInfo(getCurrentDomainName(), getCurrentUser(), findPeriodTime, asyncCallback);
	}

	public void getEmployeesInfo(Boolean allEmployees, AsyncCallback<List<EmployeeContractInfo>> asyncCallback) {
		enterprisesServiceAsync.getEmployeesInfo(getCurrentDomainName(), allEmployees, asyncCallback);
	}
	
	public void getEmployeesITInfo(Boolean allEmployees, AsyncCallback<List<ITEmployee>> asyncCallback) {
		enterprisesServiceAsync.getEmployeesITInfo(getCurrentDomainName(), allEmployees, asyncCallback);
	}

	public void deleteIT(Integer itId, AsyncCallback<String> asyncCallback) {
		enterprisesServiceAsync.deleteIT(getCurrentDomainName(), itId, asyncCallback);
	}

	public void createUpdateITEmployee(ITEmployee employeeITInfo, AsyncCallback<String> asyncCallback) {
		enterprisesServiceAsync.createUpdateITEmployee(getCurrentDomainName(), employeeITInfo, asyncCallback);
	}
	void getEnterpriseStatus(Integer enterpriseId , AsyncCallback<EnterpriseStatus> asyncCallback) {
		enterprisesServiceAsync.getEnterpriseStatus(getCurrentDomainName(), getCurrentUser(), enterpriseId, asyncCallback);		
	}
	
	public void getContractAttachments(Integer contractId, AsyncCallback<List<ContractAttach>> asyncCallback) {
		enterprisesServiceAsync.getContractAttachments(getCurrentDomainName(), contractId, asyncCallback);
	}
	
	public void setContractAttachments(Integer contractId, List<ContractAttach> contractAttachments, AsyncCallback<List<ContractAttach>> asyncCallback) {
		enterprisesServiceAsync.setContractAttachments(getCurrentDomainName(), contractId, contractAttachments, asyncCallback);
	}

	public void createContractAttach(ContractAttach contractAttach, AsyncCallback<List<ContractAttach>> asyncCallback) {
		enterprisesServiceAsync.createContractAttach(getCurrentDomainName(), contractAttach, asyncCallback);	
	}
	
	public void deleteContractAttach(ContractAttach contractAttach, AsyncCallback<List<ContractAttach>> asyncCallback) {
		enterprisesServiceAsync.deleteContractAttach(getCurrentDomainName(), contractAttach, asyncCallback);
	}
	
	public void getContractClauses(Integer contractId, AsyncCallback<List<ContractClause>> asyncCallback) {
		enterprisesServiceAsync.getContractClauses(getCurrentDomainName(), contractId, asyncCallback);
	}
	
	public void setContractClauses(Integer contractId, List<ContractClause> contractClauses, AsyncCallback<List<ContractClause>> asyncCallback) {
		enterprisesServiceAsync.setContractClauses(getCurrentDomainName(), contractId, contractClauses, asyncCallback);
	}
	
	public void createContractClause(ContractClause contractClause, AsyncCallback<List<ContractClause>> asyncCallback) {
		enterprisesServiceAsync.createContractClause(getCurrentDomainName(), contractClause, asyncCallback);
	}

	public void deleteContractClause(ContractClause contractClause, AsyncCallback<List<ContractClause>> asyncCallback) {
		enterprisesServiceAsync.deleteContractClause(getCurrentDomainName(), contractClause, asyncCallback);
	}
	
	public void getContractOtherInfo(Integer contractId, String contractType, AsyncCallback<Map<String, String>> asyncCallback) {
		enterprisesServiceAsync.getContractOtherInfo(getCurrentDomainName(), contractId, contractType, asyncCallback);
	}
	
	public void setContractOtherInfo(Integer contractId, String contractType, Map<String, String> contractOtherData, AsyncCallback<Map<String, String>> asyncCallback) {
		enterprisesServiceAsync.setContractOtherInfo(getCurrentDomainName(), contractId, contractType, contractOtherData, asyncCallback);
	}
	
	public void getContractSpecificData(Integer contractId, AsyncCallback<ContractSpecificData> asyncCallback) {
		enterprisesServiceAsync.getContractSpecificData(getCurrentDomainName(), contractId, asyncCallback);
	}

	public void setContractSpecificData(EmployeeContractInfo employeeContractData, AsyncCallback<Void> asyncCallback) {
		enterprisesServiceAsync.setContractSpecificData(getCurrentDomainName(), employeeContractData, asyncCallback);
	}
	
	public void getCNOs(AsyncCallback<Map<String, CNO>> asyncCallback) {
		enterprisesServiceAsync.getCNOs(getCurrentDomainName(), asyncCallback);
	}
	
	public void getDigitalCertificates(AsyncCallback<List<DigitalCertificate>> asyncCallback) {
		enterprisesServiceAsync.getDigitalCertificates(getCurrentDomainName(), getCurrentUser(), asyncCallback);
	}
	
	public void setDigitalCertificates(List<DigitalCertificate> digitalCertificateList, AsyncCallback<Void> asyncCallback) {
		enterprisesServiceAsync.setDigitalCertificates(getCurrentDomainName(), getCurrentUser(), digitalCertificateList, asyncCallback);
	}
	
	// ----------------------------------------------------------------- static
	
	private static String getCurrentUser() {
		return Wnd.getCurrentUser();
	}

	private static String getCurrentDomainName() {
		return Wnd.getCurrentDomainNameURL();
	}

}
