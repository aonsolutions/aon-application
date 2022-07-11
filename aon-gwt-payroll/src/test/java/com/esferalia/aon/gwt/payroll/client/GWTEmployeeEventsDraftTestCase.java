/**
 * 
 */
package com.esferalia.aon.gwt.payroll.client;

import static com.esferalia.aon.gwt.common.shared.DateUtils.getFirstDayOfYear;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.junit.Before;

import com.esferalia.aon.gwt.common.shared.DateUtils;
import com.esferalia.aon.gwt.payroll.client.AbstractEventsDraft.DateField;
import com.esferalia.aon.gwt.payroll.client.AbstractEventsDraftObject.EventMetaData;
import com.esferalia.aon.gwt.payroll.client.EmployeeEventsDraftObject_COPIA.Callback;
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
import com.esferalia.aon.gwt.payroll.shared.Events;
import com.esferalia.aon.gwt.payroll.shared.EventsWorkplace;
import com.esferalia.aon.gwt.payroll.shared.ITEmployee;
import com.esferalia.aon.gwt.payroll.shared.Period;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfo;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfoFilter;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceEmployees;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.rpc.AsyncCallback;

import junit.framework.Assert;

/**
 * 
 * @author amtzdelagos
 *
 */

public class GWTEmployeeEventsDraftTestCase extends GWTTestCase {

	
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
	 * @throws InterruptedException
	 * 
	 */
	public void testSimple() {

		EmployeesServiceAsync employeesServiceAsync = new AbstractEmployeesServiceAsync() {
			@Override
			public void getEvents(String domain, Integer workplaceId, Date startDate,
					Date endDate, int offset, int limit, String[] names,
					AsyncCallback<Events> callback)
					throws IllegalArgumentException {
				// TODO Auto-generated method stub
				super.getEvents(domain, workplaceId, startDate, endDate, offset, limit,
						names, callback);
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
			public void getWorkplaceEmployees(String domain, Workplace workplaceId, AsyncCallback<WorkplaceEmployees> asyncCallback) {
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
			public void generateCertifaca2(String currentDomainName, String user, Integer contractId,
					AsyncCallback<Void> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void fillContract(String currentDomainName, Integer contractId, Integer contractType, String formativeLvl, AsyncCallback<Void> callback) throws IllegalArgumentException {
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
					AsyncCallback<Void> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void deleteContractExtension(String currentDomainName, Integer contractId,
					AsyncCallback<Void> callback) {
				// TODO Auto-generated method stub
				
			}



			@Override
			public void contractTransform(String currentDomainName, ContractTransform contractTransform,
					AsyncCallback<Integer> callback) {
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
			
		};

		//@formatter:off
		Employee employee = new Employee()
				.setId(0)
				.setPerson(0)
				.setDocument("DOCUMENT").setName("NAME")
				.setFirstSurname("FIRST_SURNAME")
				.setSecondSurName("SECOND_SURNAME")
				.setStartDate(getFirstDayOfYear())
				.setSocialSecurity("SOCIAL_SECURITY");
		//@formatter:on
		
		DomainEmployeesServiceAsync domainEmployeesServiceAsync = DomainEmployeesServiceAsync.newInstance(employeesServiceAsync);

		EmployeeEventsDraftObject_COPIA employeeDraftObject = new EmployeeEventsDraftObject_COPIA(
				employee, domainEmployeesServiceAsync, new EventMetaData(
						"DIAS_TRABAJADOS", DateField.DAY),
				new AbstractEventsDraftObject.BooleanEventMetaData(
						"DIAS_EFECTIVOS", DateField.DAY),
				new AbstractEventsDraftObject.BooleanEventMetaData("DIAS_ERE",
						DateField.DAY),
				new AbstractEventsDraftObject.BooleanEventMetaData(
						"DIAS_HUELGA", DateField.DAY),
				new AbstractEventsDraftObject.BooleanEventMetaData(
						"DIAS_AUSENCIA", DateField.DAY),
				new AbstractEventsDraftObject.DecimalEventMetaData(
						"HORAS_TRABAJADAS", DateField.DAY));

		employeeDraftObject.setPeriod(
				DateUtils.getFirstDayOfWorkWeek(new Date()),
				DateUtils.getLastDayOfWorkWeek(new Date()), new Callback() {

					@Override
					public void onSucces() {
						// TODO Auto-generated method stub

					}

					@Override
					public void onFailure(Throwable throwable) {
						// TODO Auto-generated method stub

					}
				});

		final EmployeeEventsDraft_COPIA employeeEventsDraft = new EmployeeEventsDraft_COPIA();
		// employeeEventsDraft
		employeeEventsDraft.setEventsDraftObject(employeeDraftObject);

		Assert.assertEquals(8, employeeEventsDraft.eventsTable.getRowCount());
		Assert.assertEquals(9, employeeEventsDraft.eventsTable.getCellCount(2));
		Assert.assertEquals("Semana",
				employeeEventsDraft.dateRangeListBox.getSelectedItemText());

		// Assert.assertEquals("DIAS_EFECTIVOS" ,
		// employeeEventsDraft.eventsTable.getWidget(2, 0).getTitle());

		for (int i = 2; i < employeeEventsDraft.eventsTable.getRowCount(); i++)
			System.out.println("Incidencia: "
					+ employeeEventsDraft.eventsTable.getText(i, 0));

		Assert.assertEquals("HORAS_TRABAJADAS",
				employeeEventsDraft.eventsTable.getText(2, 0));
		Assert.assertEquals("DIAS_AUSENCIA",
				employeeEventsDraft.eventsTable.getText(3, 0));
		Assert.assertEquals("DIAS_HUELGA",
				employeeEventsDraft.eventsTable.getText(4, 0));

		checkRangeDates(employeeEventsDraft);

	}

	private void checkRangeDates(EmployeeEventsDraft_COPIA employeeEventsDraft) {
		
		Date startDate = null;
		Date endDate = null;
		Date aux = null;
		Date today = new Date();
		
		startDate = DateUtils.getFirstDayOfWorkWeek(today);
		endDate = DateUtils.getLastDayOfWorkWeek(today);
		
		Assert.assertEquals(getDateRangeLabelText(startDate, endDate),
				employeeEventsDraft.dateRangeLabel.getText());
		
		employeeEventsDraft.onPreviousDateRangeButton(null);
		endDate = DateUtils.addDays2Date(startDate, -1);
		startDate = DateUtils.getFirstDayOfWorkWeek(endDate);
		
		Assert.assertEquals(getDateRangeLabelText(startDate, endDate),
				employeeEventsDraft.dateRangeLabel.getText());

		employeeEventsDraft.onPreviousDateRangeButton(null);
		endDate = DateUtils.addDays2Date(startDate, -1);
		startDate = DateUtils.getFirstDayOfWorkWeek(endDate);

		employeeEventsDraft.onPreviousDateRangeButton(null);
		endDate = DateUtils.addDays2Date(startDate, -1);
		startDate = DateUtils.getFirstDayOfWorkWeek(endDate);
		
		Assert.assertEquals(getDateRangeLabelText(startDate, endDate),
				employeeEventsDraft.dateRangeLabel.getText());

		employeeEventsDraft.onPreviousDateRangeButton(null);
		endDate = DateUtils.addDays2Date(startDate, -1);
		startDate = DateUtils.getFirstDayOfWorkWeek(endDate);
		
		employeeEventsDraft.onPreviousDateRangeButton(null);
		endDate = DateUtils.addDays2Date(startDate, -1);
		startDate = DateUtils.getFirstDayOfWorkWeek(endDate);

		employeeEventsDraft.onNextDateRangeButton(null);
		startDate = DateUtils.addDays2Date(endDate, 1);
		endDate = DateUtils.getLastDayOfWorkWeek(startDate);
		
		Assert.assertEquals(getDateRangeLabelText(startDate, endDate),
				employeeEventsDraft.dateRangeLabel.getText());

		employeeEventsDraft.dateRangeListBox.setSelectedIndex(1);
		employeeEventsDraft.onDateRangeListBoxChanged(null);
		Assert.assertEquals("Mes",
				employeeEventsDraft.dateRangeListBox.getSelectedItemText());
		
		aux = DateUtils.copyDateOnly(startDate);		
		startDate = DateUtils.getFirstDayOfMonth(aux);
		endDate = DateUtils.getLastDayOfMonth(aux);

		Assert.assertEquals(getDateRangeLabelText(startDate, endDate),
				employeeEventsDraft.dateRangeLabel.getText());

		employeeEventsDraft.onNextDateRangeButton(null);
		startDate = DateUtils.addDays2Date(endDate, 1);
		endDate = DateUtils.getLastDayOfMonth(startDate);
		
		employeeEventsDraft.onNextDateRangeButton(null);
		startDate = DateUtils.addDays2Date(endDate, 1);
		endDate = DateUtils.getLastDayOfMonth(startDate);
		
		Assert.assertEquals(getDateRangeLabelText(startDate, endDate),
				employeeEventsDraft.dateRangeLabel.getText());

		employeeEventsDraft.onNextDateRangeButton(null);
		startDate = DateUtils.addDays2Date(endDate, 1);
		endDate = DateUtils.getLastDayOfMonth(startDate);

		employeeEventsDraft.onNextDateRangeButton(null);
		startDate = DateUtils.addDays2Date(endDate, 1);
		endDate = DateUtils.getLastDayOfMonth(startDate);

		Assert.assertEquals(getDateRangeLabelText(startDate, endDate),
				employeeEventsDraft.dateRangeLabel.getText());

		employeeEventsDraft.onPreviousDateRangeButton(null);
		endDate = DateUtils.addDays2Date(startDate, -1);
		startDate = DateUtils.getFirstDayOfMonth(endDate);
		
		Assert.assertEquals(getDateRangeLabelText(startDate, endDate),
				employeeEventsDraft.dateRangeLabel.getText());

		employeeEventsDraft.onPreviousDateRangeButton(null);
		endDate = DateUtils.addDays2Date(startDate, -1);
		startDate = DateUtils.getFirstDayOfMonth(endDate);
		
		Assert.assertEquals(getDateRangeLabelText(startDate, endDate),
				employeeEventsDraft.dateRangeLabel.getText());

		employeeEventsDraft.onPreviousDateRangeButton(null);
		endDate = DateUtils.addDays2Date(startDate, -1);
		startDate = DateUtils.getFirstDayOfMonth(endDate);
		
		Assert.assertEquals(getDateRangeLabelText(startDate, endDate),
				employeeEventsDraft.dateRangeLabel.getText());
		
		employeeEventsDraft.onPreviousDateRangeButton(null);
		endDate = DateUtils.addDays2Date(startDate, -1);
		startDate = DateUtils.getFirstDayOfMonth(endDate);

		Assert.assertEquals(getDateRangeLabelText(startDate, endDate),
				employeeEventsDraft.dateRangeLabel.getText());

		employeeEventsDraft.onPreviousDateRangeButton(null);
		endDate = DateUtils.addDays2Date(startDate, -1);
		startDate = DateUtils.getFirstDayOfMonth(endDate);

		Assert.assertEquals(getDateRangeLabelText(startDate, endDate),
				employeeEventsDraft.dateRangeLabel.getText());
		employeeEventsDraft.onPreviousDateRangeButton(null);
		endDate = DateUtils.addDays2Date(startDate, -1);
		startDate = DateUtils.getFirstDayOfMonth(endDate);

		Assert.assertEquals(getDateRangeLabelText(startDate, endDate),
				employeeEventsDraft.dateRangeLabel.getText());

		employeeEventsDraft.onPreviousDateRangeButton(null);
		endDate = DateUtils.addDays2Date(startDate, -1);
		startDate = DateUtils.getFirstDayOfMonth(endDate);

		Assert.assertEquals(getDateRangeLabelText(startDate, endDate),
				employeeEventsDraft.dateRangeLabel.getText());
		
		employeeEventsDraft.onPreviousDateRangeButton(null);
		endDate = DateUtils.addDays2Date(startDate, -1);
		startDate = DateUtils.getFirstDayOfMonth(endDate);

		Assert.assertEquals(getDateRangeLabelText(startDate, endDate),
				employeeEventsDraft.dateRangeLabel.getText());
		
		employeeEventsDraft.onPreviousDateRangeButton(null);
		endDate = DateUtils.addDays2Date(startDate, -1);
		startDate = DateUtils.getFirstDayOfMonth(endDate);

		Assert.assertEquals(getDateRangeLabelText(startDate, endDate),
				employeeEventsDraft.dateRangeLabel.getText());

		employeeEventsDraft.dateRangeListBox.setSelectedIndex(0);
		employeeEventsDraft.onDateRangeListBoxChanged(null);
		
		Assert.assertEquals("Semana",
				employeeEventsDraft.dateRangeListBox.getSelectedItemText());
		aux = DateUtils.copyDateOnly(startDate);
		startDate = DateUtils.getFirstDayOfWorkWeek(aux);
		endDate = DateUtils.getLastDayOfWorkWeek(aux);
		
		Assert.assertEquals(getDateRangeLabelText(startDate, endDate),
				employeeEventsDraft.dateRangeLabel.getText());

		employeeEventsDraft.onPreviousDateRangeButton(null);
		endDate = DateUtils.addDays2Date(startDate, -1);
		startDate = DateUtils.getFirstDayOfWorkWeek(endDate);
		
		Assert.assertEquals(getDateRangeLabelText(startDate, endDate),
				employeeEventsDraft.dateRangeLabel.getText());
		
		employeeEventsDraft.onNextDateRangeButton(null);
		startDate = DateUtils.addDays2Date(endDate, 1);
		endDate = DateUtils.getLastDayOfWorkWeek(startDate);
		
		employeeEventsDraft.onNextDateRangeButton(null);
		startDate = DateUtils.addDays2Date(endDate, 1);
		endDate = DateUtils.getLastDayOfWorkWeek(startDate);
		
		employeeEventsDraft.onNextDateRangeButton(null);
		startDate = DateUtils.addDays2Date(endDate, 1);
		endDate = DateUtils.getLastDayOfWorkWeek(startDate);
		
		employeeEventsDraft.onNextDateRangeButton(null);
		startDate = DateUtils.addDays2Date(endDate, 1);
		endDate = DateUtils.getLastDayOfWorkWeek(startDate);
		
		Assert.assertEquals(getDateRangeLabelText(startDate, endDate),
				employeeEventsDraft.dateRangeLabel.getText());

	}
	// ------------------------------------------------------------------------
	

	private String getDateRangeLabelText(Date start, Date end) {
		return DateTimeFormat.getFormat("dd").format(start)
				+ " - "
				+ DateTimeFormat.getFormat("dd 'de' MMMM 'de' yyyy")
						.format(end);
	}
}
