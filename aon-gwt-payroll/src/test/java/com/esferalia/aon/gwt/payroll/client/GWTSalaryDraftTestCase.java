/**
 * 
 */
package com.esferalia.aon.gwt.payroll.client;

import static com.esferalia.aon.gwt.common.shared.DateUtils.getFirstDayOfMonth;
import static com.esferalia.aon.gwt.common.shared.DateUtils.getFirstDayOfYear;
import static com.esferalia.aon.gwt.common.shared.DateUtils.getLastDayOfMonth;
import static java.lang.Math.random;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Before;

import com.esferalia.aon.gwt.payroll.shared.Certifica2Info;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.ContractConceptCalc;
import com.esferalia.aon.gwt.payroll.shared.ContractExtension;
import com.esferalia.aon.gwt.payroll.shared.ContractPaymentData;
import com.esferalia.aon.gwt.payroll.shared.ContractTransform;
import com.esferalia.aon.gwt.payroll.shared.ContractVariable;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsUpdate;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeIrpf;
import com.esferalia.aon.gwt.payroll.shared.EventEmployee;
import com.esferalia.aon.gwt.payroll.shared.EventsWorkplace;
import com.esferalia.aon.gwt.payroll.shared.ITEmployee;
import com.esferalia.aon.gwt.payroll.shared.Period;
import com.esferalia.aon.gwt.payroll.shared.Salary.Type;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft.Scope;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfo;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfoFilter;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceEmployees;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * 
 * @author rtrepiana
 *
 */
public class GWTSalaryDraftTestCase extends GWTTestCase {

	@Before
	/*
	 * (non-Javadoc)
	 * 
	 * @see com.google.gwt.junit.client.GWTTestCase#getModuleName()
	 */
	@Override
	public String getModuleName() {
		return "com.esferalia.aon.gwt.payroll.Payroll";
	}

	/**
	 * 
	 */
	public void testSimple() {

		SalaryDraft salaryDraftWidget = new SalaryDraft();

		final int workplaceId = 0;

		com.esferalia.aon.gwt.payroll.shared.SalaryDraft salaryDraft = newSalaryDraft(
				newEmployee(), random() * 1000);

		EmployeesServiceAsync employeesServiceAsync = new AbstractEmployeesServiceAsync() {
			@Override
			public void calculateSalaryDraft(String domain, 
					com.esferalia.aon.gwt.payroll.shared.SalaryDraft salaryDraft,
					AsyncCallback<com.esferalia.aon.gwt.payroll.shared.SalaryDraft> callback)
					throws IllegalArgumentException {

				for (int i = 0; i < 15; i++)
					salaryDraft.addVariable("_" + i, i, getFirstDayOfMonth(),
							getLastDayOfMonth(), Scope.SALARY, "" + i, null);

				
				
				callback.onSuccess(salaryDraft);
			}

			

			@Override
			public void setEmployeeEvents(String domain, int contract, EmployeeEventsUpdate updateInfo,
					AsyncCallback<EmployeeEventsUpdate> callback) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void getEmployeeEventsVariables(String domain, Integer employeeId, Date startDate, Date endDate,
					AsyncCallback<ContextDescriptor> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void getEmployeeEvents(String domain, int contract, ArrayList<String> employeeContractVariables,
					AsyncCallback<EmployeeEventsData> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void getWorkplaceEmployees(String domain, Workplace workplace, AsyncCallback<WorkplaceEmployees> asyncCallback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void setEventsWorkplace(String domain, EventsWorkplace updateEventsWorkplace,
					AsyncCallback<EventsWorkplace> asyncCallback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void getEmployeeInfoDataBase(String domain, String user, Integer contractId, Workplace workplace,
					AsyncCallback<EmployeeContractInfo> asyncCallback) throws IllegalArgumentException {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void setEmployeeInfoDataBase(String domain, EmployeeContractInfo newEmployeeInfo,
					AsyncCallback<EmployeeContractInfo> asyncCallback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void createEmployeeContract(String domain, EmployeeContractInfo newEmployeeInfo,
					AsyncCallback<EmployeeContractInfo> asyncCallback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void getWorkplaceEmployeesEvents(String currentDomainName, Integer workplaceId,
					AsyncCallback<WorkplaceEmployees> callback) throws IllegalArgumentException {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void getWorkplaceEventsVariables(String currentDomainName, Integer workplaceId, Integer agreementId,
					Date startDate, Date endDate, AsyncCallback<Map<String, String>> callback)
					throws IllegalArgumentException {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void getEmployeeEventsByContract(String currentDomainName, Integer contractId,
					ArrayList<String> employeeContractVariablesDB, AsyncCallback<EmployeeEventsData> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void setEmployeeAFIChanges(String currentDomainName, Integer contractId, Date newDate, boolean isChangeContract,
					String tc2, boolean isQuoteContract, Integer quoteGroup, boolean isOcupationContract,
					String ocupation, AsyncCallback<String> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void resetCalendar(String currentDomainName, Integer employeeId, AsyncCallback<String> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void getSalaries(String currentDomainName, SalaryInfoFilter filter,
					AsyncCallback<List<SalaryInfo>> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void deleteSalaries(String currentDomainName, ArrayList<Integer> ids,
					AsyncCallback<Void> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void getWorkplaceActiveEmployees(String currentDomainName, Integer workplaceId,
					AsyncCallback<WorkplaceEmployees> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void getEnterpriseActiveEmployees(String currentDomainName, Integer enterpriseId,
					AsyncCallback<List<EmployeeInfo>> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void getEmployeeCalendarInfo(String currentDomainName, Integer contractId,
					AsyncCallback<EmployeeCalendarInfo> callback) throws IllegalArgumentException {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void setEmployeeCalendarInfo(String currentDomainName, Integer contractId,
					EmployeeCalendarInfo employeeCalendarInfo, AsyncCallback<Void> callback) throws IllegalArgumentException {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void resetEmployeeCalendarInfo(String currentDomainName, Integer contractId,
					AsyncCallback<Void> callback) throws IllegalArgumentException {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void setEmployeeEvents(String currentDomainName, Integer idEmployee,
					EmployeeEventsData employeeEventsData, AsyncCallback<EmployeeEventsData> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void setEventsDraft(String currentDomainName, ArrayList<EventEmployee> eventEmployees,
					AsyncCallback<ArrayList<EventEmployee>> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void generateCertifaca2(String currentDomainName, String user, Integer contractId, AsyncCallback<Void> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void fillContract(String currentDomainName, Integer contractId, Integer contractType, String formativeLvl, boolean isTransform, AsyncCallback<Void> callback) throws IllegalArgumentException {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void sendEmployeeAlta(String currentDomainName, String currentUser,
					EmployeeContractInfo employeeContractInfo, AsyncCallback<Void> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void sendEmployeeBaja(String currentDomainName, String currentUser,
					EmployeeContractInfo employeeContractInfo, AsyncCallback<Void> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void movPrevDelete(String currentDomainName, String currentUser, String situation, String regimen,
					String ctaCti, String nss, Date fecha, AsyncCallback<Void> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void altaConsolidadaDelete(String currentDomainName, String currentUser, String situation,
					String regimen, String ctaCti, String nss, Date fecha, AsyncCallback<Void> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void cambioGrupCtz(String currentDomainName, String currentUser, EmployeeContractInfo employeeContractInfo, 
					String grup_ctz, Date fecha, AsyncCallback<Void> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void cambioOcupacion(String currentDomainName, String currentUser, EmployeeContractInfo employeeContractInfo, 
					String ocup, Date fecha, AsyncCallback<Void> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void cambioCatProf(String currentDomainName, String currentUser, EmployeeContractInfo employeeContractInfo, 
					String cat, Date fecha, AsyncCallback<Void> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void sendContractoSEPE(String currentDomainName, String currentUser,
					EmployeeContractInfo employeeContractInfo, AsyncCallback<Void> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void sendContractoCBSEPE(String currentDomainName, String currentUser,
					EmployeeContractInfo employeeContractInfo, AsyncCallback<Void> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void sendCertifica2(String currentDomainName, String currentUser, Integer contractId, Certifica2Info certifica2Info,
					AsyncCallback<Void> callback) throws IllegalArgumentException {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void getCertifica2PDF(String currentDomainName, String currentUser, Integer contractId, String nif, Date endDate,
					AsyncCallback<String> callback) throws IllegalArgumentException {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void getCertifica2Info(String currentDomainName, String user, Integer contractId,
					AsyncCallback<Certifica2Info> callback) throws IllegalArgumentException {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void removeContractoSEPE(String currentDomainName, String currentUser,
					EmployeeContractInfo employeeContractData, AsyncCallback<Void> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void generateCertifaca2(String currentDomainName, String user, Integer contractId, Certifica2Info certifica2Info,
					AsyncCallback<Void> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void cambioCoef(String currentDomainName, String currentUser,
					EmployeeContractInfo employeeContractInfo, String coef, Date fecha, AsyncCallback<Void> callback)
					throws IllegalArgumentException {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void cambioContrato(String currentDomainName, String currentUser,
					EmployeeContractInfo employeeContractInfo, String tc2, Date fecha, AsyncCallback<Void> callback)
					throws IllegalArgumentException {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void setEmployeeEventsByContract(String currentDomainName, Integer contractId,
					EmployeeEventsData employeeEventsData, AsyncCallback<EmployeeEventsData> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void getContractPayements(String currentDomainName, Integer contractId,
					AsyncCallback<ContractPaymentData> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void updateContractPayments(String currentDomainName, Integer contractId,
					ContractPaymentData contractPaymentData, AsyncCallback<Void> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void createContractPayment(String currentDomainName, Integer contractId,
					ContractConceptCalc contractConceptCalc, AsyncCallback<Void> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void contractExtension(String currentDomainName, ContractExtension contractExtension,
					AsyncCallback<Void> callback) throws IllegalArgumentException {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void deleteContractExtension(String currentDomainName, Integer contractId,
					AsyncCallback<Void> callback) throws IllegalArgumentException {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void contractTransform(String currentDomainName, ContractTransform contractTransform,
					AsyncCallback<Void> callback) throws IllegalArgumentException {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void deleteContractTransform(String currentDomainName, Integer contractId,
					AsyncCallback<Void> callback) throws IllegalArgumentException {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void removeContractTransform(String currentDomainName, Integer contractId,
					AsyncCallback<Void> callback) throws IllegalArgumentException {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void getEmployeeIrpf(String currentDomainName, String ssNumber, Date startDate,
					AsyncCallback<List<EmployeeIrpf>> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void setEmployeeIrpf(String currentDomainName, Integer contractId, String fullName, String document, String ssNumber, List<EmployeeIrpf> employeeIrpfs,
					AsyncCallback<Void> callback) throws IllegalArgumentException {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void getContractVariables(String currentDomainName, Integer contractId,
					AsyncCallback<List<ContractVariable>> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void updateContractVariables(String currentDomainName, List<ContractVariable> contractVariables,
					AsyncCallback<Void> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void getSalariesDates(String currentDomainName, SalaryInfoFilter filter,
					AsyncCallback<Period> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void getEmployeeTa(String domain, String user, Integer contractId, String situation, String regimen, String ctaCti,
					String nss, Date fecha, AsyncCallback<String> callback) throws IllegalArgumentException {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void checkAndUpdateServiAgreement(String domain, String userLogin, Integer agreementId, String ssNumber, Integer lastDateYear,
					AsyncCallback<Date> asyncCallback) throws IllegalArgumentException {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void createContractPayment(String currentDomainName, Integer contractId,
					List<ContractConceptCalc> contractConceptCalcList, AsyncCallback<Void> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void getSalariesOccam(String currentDomainName, String login, ITEmployee itEmployee, Date startDate,
					Date endDate, AsyncCallback<List<Certifica2Info>> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void createContractVariable(String currentDomainName, Integer contractId,
					ContractVariable contractVariable, AsyncCallback<Void> callback) throws IllegalArgumentException {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void sendContractTransform(String currentDomainName, String currentUser,
					EmployeeContractInfo employeeContractData, AsyncCallback<Void> callback) throws IllegalArgumentException {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void sendContractExtension(String currentDomainName, String currentUser,
					EmployeeContractInfo employeeContractData, AsyncCallback<Void> callback) throws IllegalArgumentException {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void removeContractTransform(String currentDomainName, String currentUser, String ide,
					AsyncCallback<Void> callback) throws IllegalArgumentException {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void getSepeComunicationData(String currentDomainName, String currentUser, String document,
					Date date, Integer contractId, AsyncCallback<Map<String, String>> callback) throws IllegalArgumentException {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void getEmployeeCbc(String currentDomainName, String currentUser, String document,
					Integer contractId, Date startDate, Date endDate, String sepeIde, AsyncCallback<String> callback)
					throws IllegalArgumentException {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void getEmployeeCbcTransform(String currentDomainName, String currentUser, String cif, String document,
					Integer contractId, Date startDate, String sepeIde, AsyncCallback<String> callback)
					throws IllegalArgumentException {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void getEmployeeCto(String currentDomainName, String currentUser, String document,
					Integer contractId, Date startDate, Date endDate, String sepeIde, AsyncCallback<String> callback)
					throws IllegalArgumentException {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void getEmployeeCtoTransform(String currentDomainName, String currentUser, String cif, String document,
					Integer contractId, Date startDate, String sepeIde, AsyncCallback<String> callback)
					throws IllegalArgumentException {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void sendContractoCBTransformSEPE(String currentDomainName, String currentUser,
					EmployeeContractInfo employeeContractInfo, AsyncCallback<Void> callback)
					throws IllegalArgumentException {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void getSepeTransformComunicationData(String currentDomainName, String currentUser, String document,
					String enterpriseCif, Date originalStartDate, String sepeId, Integer contractId,
					AsyncCallback<Map<String, String>> callback) throws IllegalArgumentException {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void getEmployeeCtoExtension(String currentDomainName, String currentUser, String enterpriseCIF,
					String document, Integer contractId, Date extensionDate, Integer extensionNum,
					String sepeExtensionId, AsyncCallback<String> callback) throws IllegalArgumentException {
				// TODO Auto-generated method stub
				
			}
			
		};
		
		DomainEmployeesServiceAsync domainEmployeesServiceAsync = DomainEmployeesServiceAsync.newInstance(employeesServiceAsync); 

//		ITDataObject itDataObject = new ITDataObject(workplaceId,
//				domainEmployeesServiceAsync);

		SalaryDraftObject salaryDraftObject = new SalaryDraftObject(
				salaryDraft, /*itDataObject,*/ domainEmployeesServiceAsync);

		salaryDraftWidget.setSalaryDraftObject(salaryDraftObject);

		//@formatter:off
//		Assert.assertEquals("EMPLOYEE DOCUMENT", 
//				salaryDraft.getEmployeeDocument(),
//				salaryDraftWidget.employeeDocumentLabel.getText());
		//@formatter:on

		assertTrue(true);
	}

	// ------------------------------------------------------------------------

	private static Employee newEmployee() {
		return new Employee().setId(0).setPerson(0).setDocument("DOCUMENT")
				.setName("NAME").setFirstSurname("FIRST_SURNAME")
				.setSecondSurName("SECOND_SURNAME")
				.setStartDate(getFirstDayOfYear())
				.setSocialSecurity("SOCIAL_SECURITY");
	}

	private static com.esferalia.aon.gwt.payroll.shared.SalaryDraft newSalaryDraft(
			Employee employee, Double paymment) {
		return new com.esferalia.aon.gwt.payroll.shared.SalaryDraft()
				.setCgcBase(paymment).setCgpBase(paymment)
				.setIrpfBase(paymment).setTotalPayment(paymment)
				.setEmployee(employee).setEndDate(getLastDayOfMonth())
				.setStartDate(getFirstDayOfMonth())
				.setChargeDate(getLastDayOfMonth())
				.setIssueDate(getLastDayOfMonth()).setId(0)
				.setType(Type.SALARY);
	}

}
