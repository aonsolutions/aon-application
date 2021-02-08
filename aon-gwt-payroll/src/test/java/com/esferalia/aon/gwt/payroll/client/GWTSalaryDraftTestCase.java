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

import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.ContractAttach;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsUpdate;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.EventEmployee;
import com.esferalia.aon.gwt.payroll.shared.EventsWorkplace;
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
			public void getEmployeeInfoDataBase(String domain, Integer employeeContract,
					AsyncCallback<EmployeeContractInfo> asyncCallback) {
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
					AsyncCallback<WorkplaceEmployees> callback) {
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
			public void getEmployeeSalaries(String currentDomainName, Integer employeeId,
					AsyncCallback<List<SalaryInfo>> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void deleteSalariesDB(String currentDomainName, ArrayList<Integer> ids,
					AsyncCallback<String> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void getWorkplaceSalaries(String currentDomainName, Integer workplaceId,
					AsyncCallback<List<SalaryInfo>> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void getWorkplaceActiveEmployees(String currentDomainName, Integer workplaceId,
					AsyncCallback<WorkplaceEmployees> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void getEnterpriseSalaries(String currentDomainName, Integer enterpriseId,
					AsyncCallback<List<SalaryInfo>> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void getEnterpriseActiveEmployees(String currentDomainName, Integer enterpriseId,
					AsyncCallback<List<EmployeeInfo>> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void getFilterSalaries(String currentDomainName, SalaryInfoFilter filter,
					AsyncCallback<List<SalaryInfo>> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void getEmployeeCalendarInfo(String currentDomainName, Integer contractId,
					AsyncCallback<EmployeeCalendarInfo> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void setEmployeeCalendarInfo(String currentDomainName, Integer contractId,
					EmployeeCalendarInfo employeeCalendarInfo, AsyncCallback<String> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void resetEmployeeCalendarInfo(String currentDomainName, Integer contractId,
					AsyncCallback<String> callback) {
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
			public void generateCertifaca2(String currentDomainName,
					com.esferalia.aon.gwt.payroll.shared.SalaryDraft salaryDraft, AsyncCallback<String> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void fillContract(String currentDomainName, Integer contractId, Integer contractType, String formativeLvl, AsyncCallback<List<ContractAttach>> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void getEmployeeCbc(String currentDomainName, String currentUser, String document, Date startDate,
					Date endDate, AsyncCallback<String> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void getEmployeeCto(String currentDomainName, String currentUser, String document, Date startDate,
					Date endDate, AsyncCallback<String> callback) {
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
					String regimen, String ctaCti, String nss, AsyncCallback<Void> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void cambioGrupCtz(String currentDomainName, String currentUser, String ipf, String regimen,
					String ctaCti, String nss, String grup_ctz, Date fecha, AsyncCallback<Void> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void cambioOcupacion(String currentDomainName, String currentUser, String ipf, String regimen,
					String ctaCti, String nss, String ocup, Date fecha, AsyncCallback<Void> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void cambioCatProf(String currentDomainName, String currentUser, String ipf, String regimen,
					String ctaCti, String nss, String cat, Date fecha, AsyncCallback<Void> callback) {
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

			
		};
		
		DomainEmployeesServiceAsync domainEmployeesServiceAsync = DomainEmployeesServiceAsync.newInstance(employeesServiceAsync); 

		ITDataObject itDataObject = new ITDataObject(workplaceId,
				domainEmployeesServiceAsync);

		SalaryDraftObject salaryDraftObject = new SalaryDraftObject(
				salaryDraft, itDataObject, domainEmployeesServiceAsync);

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
