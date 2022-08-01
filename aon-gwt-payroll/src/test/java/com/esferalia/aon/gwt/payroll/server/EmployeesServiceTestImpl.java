package com.esferalia.aon.gwt.payroll.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import com.esferalia.aon.gwt.common.shared.EvalException;
import com.esferalia.aon.gwt.payroll.client.EmployeesService;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.AgreementDraft;
import com.esferalia.aon.gwt.payroll.shared.Bonus;
import com.esferalia.aon.gwt.payroll.shared.CalendarDraft;
import com.esferalia.aon.gwt.payroll.shared.Certifica2Info;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.ContractConceptCalc;
import com.esferalia.aon.gwt.payroll.shared.ContractExtension;
import com.esferalia.aon.gwt.payroll.shared.ContractPaymentData;
import com.esferalia.aon.gwt.payroll.shared.ContractTransform;
import com.esferalia.aon.gwt.payroll.shared.ContractVariable;
import com.esferalia.aon.gwt.payroll.shared.Cost;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarUpdate;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsUpdate;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeIrpf;
import com.esferalia.aon.gwt.payroll.shared.EmployeeStatus;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.EventEmployee;
import com.esferalia.aon.gwt.payroll.shared.Events;
import com.esferalia.aon.gwt.payroll.shared.EventsWorkplace;
import com.esferalia.aon.gwt.payroll.shared.Extra;
import com.esferalia.aon.gwt.payroll.shared.ITData;
import com.esferalia.aon.gwt.payroll.shared.ITDataPerson;
import com.esferalia.aon.gwt.payroll.shared.ITEmployee;
import com.esferalia.aon.gwt.payroll.shared.Irpf;
import com.esferalia.aon.gwt.payroll.shared.Payment;
import com.esferalia.aon.gwt.payroll.shared.Period;
import com.esferalia.aon.gwt.payroll.shared.Result;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.Salary.Type;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfo;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfoFilter;
import com.esferalia.aon.gwt.payroll.shared.SalaryPreview;
import com.esferalia.aon.gwt.payroll.shared.Statistics;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceEmployees;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class EmployeesServiceTestImpl extends RemoteServiceServlet implements
		EmployeesService {
	
	@Override
	public EmployeeCalendarData getEmployeeCalendar(String domain, int contract) {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}
	
	@Override
	public void setEmployeeCalendar(String domain, int contract, EmployeeCalendarUpdate updateInfo) {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
	}
	
	@Override
	public CalendarDraft getCalendar(String domain, int workplaceId, Integer pattern,
			Integer calendar) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public Map<Integer, String> getHolidayDescription(String domain)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public void saveHolidaysAndDays(String domain, int workplaceId, String holidayDescription,
			Integer holidayListBox, Map<Date, String> map, CalendarDraft.DayType daysTypes[])
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		
	}

	@Override
	public void deletePropertyHoliday(String domain, Integer id, Date date)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		
	}

	@Override
	public Statistics getWorkplaceStats(String domain, int workplaceId)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public Statistics getEnterpriseStats(String domain, int enterpriseId)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public ITData getEnterpriseITData(String domain, int enterpriseId)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public ITData getWorkplaceITData(String domain, int workplaceId)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public void saveITDataPerson(String domain,
			Map<Integer, LinkedHashMap<Integer, ITDataPerson>> inserts,
			Map<Integer, LinkedHashMap<Integer, ITDataPerson>> deletes,
			Map<Integer, LinkedHashMap<Integer, ITDataPerson>> updates)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		
	}

	@Override
	public Enterprise getEnterprise(String domain, String user ) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public Enterprise[] getEnterprises(String domain, String user) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public List<Payment> getAvailablePayments(String domain, int employeeId)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public List<Deduction> getAvailableDeductions(String domain, int employeeId)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}
	
	@Override
	public List<Bonus> getAvailableBonuses(String domain, int employeeId)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}
	
	@Override
	public List<Cost> getWorkplaceCosts(String domain, int workplaceId)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public List<Cost> getEnterpriseCosts(String domain, int enterpriseId)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public List<Salary> getSalaries(String domain, Employee employee)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public List<Irpf> getIrpfs(String domain, Employee employee)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public List<Extra> getExtras(String domain, List<Employee> employee)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public String getCostReceiptPDF(String domain, Cost cost, Type[] types)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public String getCostReceiptHTML(String domain, Cost cost, Type[] types, int zoom)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public String getSLDCalcReceiptHTML(String domain, String user, Cost cost, Type[] types, int zoom)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public String getIrpfReceiptHTML(String domain, Irpf irpf, int zoom)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public String getSalaryReceiptHTML(String domain, Cost cost, Type[] types, int zoom)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public String getSalaryReceiptHTML(String domain, Salary salary, int zoom)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public void saveSalaryDraft(String domain, SalaryDraft salaryDraft)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		
	}

	@Override
	public SalaryDraft saveSalary(String domain, String user, SalaryDraft salaryDraft)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public SalaryDraft saveSalary(String domain, SalaryDraft salaryDraft, Date sections [])
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public AgreementDraft saveAgreementDraft(String domain, String userLogin, 
			AgreementDraft agreementDraft)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public ContextDescriptor getContext(String domain, SalaryDraft salaryDraft)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public ContextDescriptor getContext(String domain, 
			AgreementDraft agreementDraft,
			int levelId) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public List<Result> eval(String domain, String expression, SalaryDraft salaryDraft)
			throws IllegalArgumentException, EvalException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public List<Result> eval(String domain, 
			String expression, AgreementDraft agreementDraft,
			int levelId) throws IllegalArgumentException, EvalException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public Double calculateIrpf(String domain, SalaryDraft salaryDraft)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public SalaryDraft calculateSalaryDraft(String domain, SalaryDraft salaryDraft)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public SalaryDraft calculateSalaryDraft(String domain, SalaryDraft salaryDraft, Date sections [])
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public SalaryDraft syncSalaryDraft(String domain, String user, SalaryDraft salaryDraft)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public AgreementDraft calculateAgreementDraft(String domain, AgreementDraft agreementDraft)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public String getSalaryDraftReceipt(String domain, SalaryDraft salaryDraft, String mime)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public String getSettleDraftReceipt(String domain, SalaryDraft salaryDraft, String mime)
			throws IllegalArgumentException {
		System.out.println("Auto-generated method stub");
		return null;
	}
	
	@Override
	public String getSalaryDraftReceiptHTML(String domain, SalaryDraft salaryDraft, int zoom)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public String getAgreementDraftReceiptHTML(String domain, 
			AgreementDraft agreementDraft,
			int levelId, Type type, int zoom) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public String getAgreementDraftReceipt(String domain, AgreementDraft agreementDraft, List<Variable> context, int levelId, Type type,
			String mime) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}


	@Override
	public String getIrpfDraftReceipt(String domain, SalaryDraft salaryDraft, String mime)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public String getIrpfDraftReceiptHTML(String domain, SalaryDraft salaryDraft, int zoom)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public String getSalaryPreviewReceiptHTML(String domain, SalaryPreview salaryPreview,
			int zoom) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public void insertPerson(String domain, Employee employee) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		
	}
	
	@Override
	public Employee getEmployee(String domain, int employeeId) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public List<Employee> getEmployees(String domain, int workplaceId, Date endDate,
			String pattern, int offset, int limit)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public List<Employee> getTrashEmployees(String domain, int workplaceId)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public void saveEvents(String domain, Events events, Date startDate, Date endDate)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		
	}

	@Override
	public Events getEvents(String domain, Integer workplaceId, Date startDate, Date endDate,
			int offset, int limit, String[] names)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public List<Variable> getVariables(String domain, SalaryDraft salaryDraft, Date startDate,
			Date endDate, String[] names) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public Period getAvailPeriod(String domain, Integer workplaceId, String name)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public Map<String, String> getEventsVariables(String domain, Integer workplaceId,
			Integer agreementId, Date startDate, Date endDate)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public SortedSet<Date> getChanges(String domain, Agreement agreement)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public Employee pasteContract(String domain, int workplaceId, int contractId,
			String document, Date startDate, Date endDate, boolean check)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public void moveContractId(String domain, Employee employee)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		
	}

	@Override
	public void deleteContract(String domain, Employee employee)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		
	}

	@Override
	public void delete(String domain, Salary[] salaries) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		
	}

	@Override
	public Map<String, String> getAvaiableEmployees(String domain)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	

	@Override
	public void setEmployeeEvents(String domain, int contract, EmployeeEventsUpdate updateInfo) {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		
	}

	@Override
	public ContextDescriptor getEmployeeEventsVariables(String domain, Integer employeeId, Date startDate, Date endDate)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public EmployeeEventsData getEmployeeEvents(String domain, int contract, ArrayList<String> employeeContractVariables) {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public WorkplaceEmployees getWorkplaceEmployees(String domain, Workplace workplaceId) {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public EventsWorkplace setEventsWorkplace(String domain, EventsWorkplace updateEventsWorkplace) {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public EmployeeContractInfo getEmployeeInfoDataBase(String domain, String user, Integer contractId, Workplace workplace) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public EmployeeContractInfo setEmployeeInfoDataBase(String domain, EmployeeContractInfo newEmployeeInfo) {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public EmployeeContractInfo createEmployeeContract(String domain, EmployeeContractInfo newEmployeeInfo) {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public WorkplaceEmployees getWorkplaceEmployeesEvents(String currentDomainName, Integer workplaceId) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public Map<String, String> getWorkplaceEventsVariables(String currentDomainName, Integer workplaceId,
			Integer agreementId, Date startDate, Date endDate) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public EmployeeEventsData getEmployeeEventsByContract(String currentDomainName, Integer contractId,
			ArrayList<String> employeeContractVariablesDB) {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public String setEmployeeAFIChanges(String currentDomainName, Integer contractId, Date newDate, boolean isChangeContract, String tc2,
			boolean isQuoteContract, Integer quoteGroup, boolean isOcupationContract, String ocupation) {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public String resetCalendar(String currentDomainName, Integer employeeId) {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public List<SalaryInfo> getSalaries(String currentDomainName, SalaryInfoFilter filter) {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public void deleteSalaries(String currentDomainName, ArrayList<Integer> ids) {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
	}

	@Override
	public WorkplaceEmployees getWorkplaceActiveEmployees(String currentDomainName, Integer workplaceId) {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public List<EmployeeInfo> getEnterpriseActiveEmployees(String currentDomainName, Integer enterpriseId) {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public EmployeeCalendarInfo getEmployeeCalendarInfo(String currentDomainName, Integer contractId) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public void setEmployeeCalendarInfo(String currentDomainName, Integer contractId,
			EmployeeCalendarInfo employeeCalendarInfo) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
	}

	@Override
	public void resetEmployeeCalendarInfo(String currentDomainName, Integer contractId) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
	}

	@Override
	public EmployeeEventsData setEmployeeEvents(String currentDomainName, Integer idEmployee,
			EmployeeEventsData employeeEventsData) {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public ArrayList<EventEmployee> setEventsDraft(String currentDomainName, ArrayList<EventEmployee> eventEmployees) {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public void generateCertifaca2(String currentDomainName, String user, Integer contractId) {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
	}
	
	@Override
	public String getEmployeeTa(String domain, String user, Integer contractId, String situation, String regimen, String ctaCti, String nss, Date fecha) {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public String getEmployeeIdc(String domain, String user,Integer contractId, Date date) {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}
	
	@Override
	public String checkEmployeeIdc(String domain, String user, Integer contractId, Date date, String idc)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public String getEmployeeIdcPlNss(String domain, String user,Integer contractId, Date date) {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public List<Date> getEmployeeIdcDates(String domain, String user,Integer contractId, Date date) {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public EmployeeStatus getEmployeeStatus(String domain, String user, Integer contractId) {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		return null;
	}

	@Override
	public void fillContract(String currentDomainName,  Integer contractId, Integer contractType, String formativeLvl, boolean isTransform) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
	}

	@Override
	public void setData(String currentDomainName, String user, Integer contractId, ArrayList<Variable> data) {
		// TODO Auto-generated method stub
		System.out.println("Auto-generated method stub");
		
	}

	@Override
	public void sendEmployeeAlta(String currentDomainName, String currentUser,
			EmployeeContractInfo employeeContractInfo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendEmployeeBaja(String currentDomainName, String currentUser,
			EmployeeContractInfo employeeContractInfo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void movPrevDelete(String currentDomainName, String currentUser, String situation, String regimen,
			String ctaCti, String nss, Date fecha) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void altaConsolidadaDelete(String currentDomainName, String currentUser, String situation, String regimen,
			String ctaCti, String nss, Date fecha) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cambioGrupCtz(String currentDomainName, String currentUser, EmployeeContractInfo employeeContractInfo, 
			String grup_ctz, Date fecha) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cambioOcupacion(String currentDomainName, String currentUser, EmployeeContractInfo employeeContractInfo, 
			String ocup, Date fecha) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cambioCatProf(String currentDomainName, String currentUser, EmployeeContractInfo employeeContractInfo, 
			String cat, Date fecha) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendContractoSEPE(String currentDomainName, String currentUser,
			EmployeeContractInfo employeeContractInfo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendContractoCBSEPE(String currentDomainName, String currentUser,
			EmployeeContractInfo employeeContractInfo) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendCertifica2(String currentDomainName, String currentUser, Integer contractId, Certifica2Info certifica2Info)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String getCertifica2PDF(String currentDomainName, String currentUser, Integer contractId, String nif, Date endDate)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Certifica2Info getCertifica2Info(String currentDomainName, String user, Integer contractId)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removeContractoSEPE(String currentDomainName, String currentUser,
			EmployeeContractInfo employeeContractData) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void generateCertifaca2(String currentDomainName, String user, Integer contractId, Certifica2Info certifica2Info) {
		// TODO Auto-generated method stub
	}

	@Override
	public void cambioCoef(String currentDomainName, String currentUser, EmployeeContractInfo employeeContractInfo,
			String coef, Date fecha) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cambioContrato(String currentDomainName, String currentUser, EmployeeContractInfo employeeContractInfo,
			String tc2, Date fecha) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public EmployeeEventsData setEmployeeEventsByContract(String currentDomainName, Integer contractId,
			EmployeeEventsData employeeEventsData) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ContractPaymentData getContractPayements(String currentDomainName, Integer contractId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateContractPayments(String currentDomainName, Integer contractId,
			ContractPaymentData contractPaymentData) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createContractPayment(String currentDomainName, Integer contractId,
			ContractConceptCalc contractConceptCalc) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void contractExtension(String currentDomainName, ContractExtension contractExtension) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteContractExtension(String currentDomainName, Integer contractId) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int contractTransform(String currentDomainName, ContractTransform contractTransform) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<EmployeeIrpf> getEmployeeIrpf(String currentDomainName, String ssNumber, Date startDate) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setEmployeeIrpf(String currentDomainName, Integer contractId, String fullName, String document, String ssNumber, List<EmployeeIrpf> employeeIrpfs) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<ContractVariable> getContractVariables(String currentDomainName, Integer contractId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateContractVariables(String currentDomainName, List<ContractVariable> contractVariables) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Period getSalariesDates(String currentDomainName, SalaryInfoFilter filter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Date checkAndUpdateServiAgreement(String domain, String userLogin, Integer agreementId, String ssNumber, Integer lastDateYear)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createContractPayment(String currentDomainName, Integer contractId,
			List<ContractConceptCalc> contractConceptCalcList) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Certifica2Info> getSalariesOccam(String domainName, String login,
		ITEmployee itEmployee, Date startDate, Date endDate) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createContractVariable(String currentDomainName, Integer contractId, ContractVariable contractVariable)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendContractTransform(String currentDomainName, String currentUser,
			EmployeeContractInfo employeeContractData)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void sendContractExtension(String currentDomainName, String currentUser,
			EmployeeContractInfo employeeContractData)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeContractTransform(String currentDomainName, String currentUser, String ide)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, String> getSepeComunicationData(String currentDomainName, String currentUser, String document,
			Date date, Integer contractId) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEmployeeCbc(String currentDomainName, String currentUser, String document, Integer contractId,
			Date startDate, Date endDate, String sepeIde) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEmployeeCbcTransform(String currentDomainName, String currentUser, String cif, String document,
			Integer contractId, Date startDate, String sepeIde) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEmployeeCto(String currentDomainName, String currentUser, String document, Integer contractId,
			Date startDate, Date endDate, String sepeIde) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getEmployeeCtoTransform(String currentDomainName, String currentUser, String cif, String document,
			Integer contractId, Date startDate, String sepeIde) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void sendContractoCBTransformSEPE(String currentDomainName, String currentUser,
			EmployeeContractInfo employeeContractInfo) throws IllegalArgumentException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, String> getSepeTransformComunicationData(String currentDomainName, String currentUser,
			String document, String enterpriseCif, Date originalStartDate, String sepeId, Integer contractId) {
		// TODO Auto-generated method stub
		return null;
	}
}
