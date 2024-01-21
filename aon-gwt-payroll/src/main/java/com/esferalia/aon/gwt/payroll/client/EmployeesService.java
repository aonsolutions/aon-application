package com.esferalia.aon.gwt.payroll.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.esferalia.aon.gwt.common.shared.EvalException;
import com.esferalia.aon.gwt.payroll.shared.Bonus;
import com.esferalia.aon.gwt.payroll.shared.Certifica2Info;
import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
import com.esferalia.aon.gwt.payroll.shared.ContractConceptCalc;
import com.esferalia.aon.gwt.payroll.shared.ContractExtension;
import com.esferalia.aon.gwt.payroll.shared.ContractInfo;
import com.esferalia.aon.gwt.payroll.shared.ContractPaymentData;
import com.esferalia.aon.gwt.payroll.shared.ContractTransform;
import com.esferalia.aon.gwt.payroll.shared.ContractVariable;
import com.esferalia.aon.gwt.payroll.shared.Cost;
import com.esferalia.aon.gwt.payroll.shared.Deduction;
import com.esferalia.aon.gwt.payroll.shared.Employee;
import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsData;
import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
import com.esferalia.aon.gwt.payroll.shared.EmployeeIrpf;
import com.esferalia.aon.gwt.payroll.shared.EmployeeStatus;
import com.esferalia.aon.gwt.payroll.shared.Enterprise;
import com.esferalia.aon.gwt.payroll.shared.EventEmployee;
import com.esferalia.aon.gwt.payroll.shared.Events;
import com.esferalia.aon.gwt.payroll.shared.EventsWorkplace;
import com.esferalia.aon.gwt.payroll.shared.Extra;
import com.esferalia.aon.gwt.payroll.shared.ITEmployee;
import com.esferalia.aon.gwt.payroll.shared.Irpf;
import com.esferalia.aon.gwt.payroll.shared.Period;
import com.esferalia.aon.gwt.payroll.shared.Result;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.SalaryDraft;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfo;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfoFilter;
import com.esferalia.aon.gwt.payroll.shared.SalaryPreview;
import com.esferalia.aon.gwt.payroll.shared.Variable;
import com.esferalia.aon.gwt.payroll.shared.Workplace;
import com.esferalia.aon.gwt.payroll.shared.WorkplaceEmployees;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("employees")
public interface EmployeesService  extends RemoteService, CalendarService, EmployeeEventsService,
		StatisticsService, AgreementService {
	
	Enterprise getEnterprise(String domain, String user) throws IllegalArgumentException;

	Enterprise[] getEnterprises(String domain, String user) throws IllegalArgumentException;

	List<Deduction> getAvailableDeductions(String domain, int employeeId)
			throws IllegalArgumentException;

	List<Bonus> getAvailableBonuses(String domain, int employeeId)
			throws IllegalArgumentException;

	List<Cost> getWorkplaceCosts(String domain, int workplaceId)
			throws IllegalArgumentException;

	List<Cost> getEnterpriseCosts(String domain, int enterpriseId)
			throws IllegalArgumentException;

	List<Salary> getSalaries(String domain, Employee employee) throws IllegalArgumentException;

	List<Irpf> getIrpfs(String domain, Employee employee) throws IllegalArgumentException;

	List<Extra> getExtras(String domain, List<Employee> employees) throws IllegalArgumentException;

	String getCostReceiptPDF(String domain, Cost cost, Salary.Type[] types)
			throws IllegalArgumentException;

	String getCostReceiptHTML(String domain, Cost cost, Salary.Type[] types, int zoom)
			throws IllegalArgumentException;

	String getSLDCalcReceiptHTML(String domain, String user, Cost cost, Salary.Type[] types, int zoom)
			throws IllegalArgumentException;

	String getIrpfReceiptHTML(String domain, Irpf irpf, int zoom)
			throws IllegalArgumentException;

	String getSalaryReceiptHTML(String domain, Cost cost, Salary.Type[] type, int zoom)
			throws IllegalArgumentException;

	String getSalaryReceiptHTML(String domain, Salary salary, int zoom)
			throws IllegalArgumentException;

	void saveSalaryDraft(String domain,SalaryDraft salaryDraft)
			throws IllegalArgumentException;

	SalaryDraft saveSalary(String domain,  String user, SalaryDraft salaryDraft)
			throws IllegalArgumentException;
	
	SalaryDraft saveSalary(String domain, SalaryDraft salaryDraft, Date[] sections)
			throws IllegalArgumentException;

	ContextDescriptor getContext(String domain, SalaryDraft salaryDraft)
			throws IllegalArgumentException;

	List<Result> eval(String domain, String expression, SalaryDraft salaryDraft)
			throws IllegalArgumentException, EvalException;

	Double calculateIrpf(String domain, SalaryDraft salaryDraft)
			throws IllegalArgumentException;

	SalaryDraft calculateSalaryDraft(String domain, SalaryDraft salaryDraft)
			throws IllegalArgumentException;

	SalaryDraft calculateSalaryDraft(String domain, SalaryDraft salaryDraft, Date[] sections)
			throws IllegalArgumentException;

	SalaryDraft syncSalaryDraft(String domain, String user, SalaryDraft salaryDraft)
			throws IllegalArgumentException;

	String getSalaryDraftReceipt(String domain, SalaryDraft salaryDraft, String mime)
			throws IllegalArgumentException;
	
	String getSettleDraftReceipt(String domain, SalaryDraft salaryDraft, String mime)
			throws IllegalArgumentException;

	String getSalaryDraftReceiptHTML(String domain, SalaryDraft salaryDraft, int zoom)
			throws IllegalArgumentException;

	String getIrpfDraftReceipt(String domain, SalaryDraft salaryDraft, String mime)
			throws IllegalArgumentException;

	String getIrpfDraftReceiptHTML(String domain, SalaryDraft salaryDraft, int zoom)
			throws IllegalArgumentException;

	String getSalaryPreviewReceiptHTML(String domain, SalaryPreview salaryPreview, int zoom)
			throws IllegalArgumentException;

	void insertPerson(String domain, Employee employee) throws IllegalArgumentException;

	Employee getEmployee(String domain, int employeeId) throws IllegalArgumentException;

	List<Employee> getEmployees(String domain, int workplaceId, Date endDate, String pattern,
			int offset, int limit) throws IllegalArgumentException;

	List<Employee> getTrashEmployees(String domain, int workplaceId)
			throws IllegalArgumentException;

	void saveEvents(String domain, Events events, Date startDate, Date endDate)
			throws IllegalArgumentException;

	Events getEvents(String domain, Integer workplaceId, Date startDate, Date endDate,
			int offset, int limit, String[] names)
			throws IllegalArgumentException;

	List<Variable> getVariables(String domain, SalaryDraft salaryDraft, Date startDate, Date endDate, String[] names)
			throws IllegalArgumentException;

	Period getAvailPeriod(String domain, Integer workplaceId, String name)
			throws IllegalArgumentException;

	Map<String, String> getEventsVariables(String domain, Integer workplaceId,
			Integer agreementId, Date startDate, Date endDate)
			throws IllegalArgumentException;
	
	Map<String, String> getWorkplaceEventsVariables(String currentDomainName, Integer workplaceId, Integer agreementId,
			Date startDate, Date endDate) throws IllegalArgumentException;
	
	ContextDescriptor getEmployeeEventsVariables(String domain, Integer employeeId,
			Date startDate, Date endDate)
			throws IllegalArgumentException;

	Employee pasteContract(String domain, int workplaceId, int contractId, String document,
			Date startDate, Date endDate, boolean check)
			throws IllegalArgumentException;

	void moveContractId(String domain, Employee employee) throws IllegalArgumentException;

	void deleteContract(String domain, Employee employee) throws IllegalArgumentException;

	void delete(String domain, Salary[] salaries) throws IllegalArgumentException;

	Map<String, String> getAvaiableEmployees(String domain) throws IllegalArgumentException;

	WorkplaceEmployees getWorkplaceEmployees(String domain, Workplace workplace);

	EventsWorkplace setEventsWorkplace(String domain, 
			com.esferalia.aon.gwt.payroll.shared.EventsWorkplace updateEventsWorkplace);

	EmployeeContractInfo getEmployeeInfoDataBase(String domain, String user, Integer contractId, Workplace workplace);

	EmployeeContractInfo setEmployeeInfoDataBase(String domain, EmployeeContractInfo employeeContractData);

	EmployeeContractInfo createEmployeeContract(String domain, EmployeeContractInfo employeeContractData);

	WorkplaceEmployees getWorkplaceEmployeesEvents(String currentDomainName, Integer workplaceId);

	EmployeeEventsData getEmployeeEventsByContract(String currentDomainName, Integer contractId,
			ArrayList<String> employeeContractVariablesDB);

	EmployeeEventsData setEmployeeEventsByContract(String currentDomainName, Integer contractId,
			EmployeeEventsData employeeEventsData);
	
	
	String setEmployeeAFIChanges(String currentDomainName, Integer contractId, Date newDate, boolean isChangeContract,
			String tc2, boolean isQuoteContract, Integer quoteGroup, boolean isOcupationContract, String ocupation);

	String resetCalendar(String currentDomainName, Integer employeeId);
	
	Period getSalariesDates(String currentDomainName, SalaryInfoFilter filter);
	
	List<SalaryInfo> getSalaries(String currentDomainName, SalaryInfoFilter filter);

	void deleteSalaries(String currentDomainName, ArrayList<Integer> ids);

	WorkplaceEmployees getWorkplaceActiveEmployees(String currentDomainName, Integer workplaceId);

	List<EmployeeInfo> getEnterpriseActiveEmployees(String currentDomainName, Integer enterpriseId);

	EmployeeCalendarInfo getEmployeeCalendarInfo(String currentDomainName, Integer contractId) throws IllegalArgumentException;

	void setEmployeeCalendarInfo(String currentDomainName, Integer contractId, EmployeeCalendarInfo employeeCalendarInfo) throws IllegalArgumentException;

	void resetEmployeeCalendarInfo(String currentDomainName, Integer contractId) throws IllegalArgumentException;

	EmployeeEventsData setEmployeeEvents(String currentDomainName, Integer idEmployee,
			EmployeeEventsData employeeEventsData);

	ArrayList<EventEmployee> setEventsDraft(String currentDomainName, ArrayList<EventEmployee> eventEmployees);

	void generateCertifaca2(String currentDomainName, String user, Integer contractId) throws IllegalArgumentException;
	
	void generateCertifaca2(String currentDomainName, String user, Integer contractId, Certifica2Info certifica2Info) throws IllegalArgumentException;

	// Sistema RED w2.seg-social.es
	
//	EmployeeStatus register(String domain, Integer contractId);
//	
//	EmployeeStatus unregister(String domain, Integer contractId);
	
	// ------------------------------------------------- TGSS Files
	
	String getEmployeeTa(String domain, String user, Integer contractId, String situation, String regimen, String ctaCti, String nss, Date fecha) throws IllegalArgumentException;

	String getEmployeeIdc(String domain, String user, Integer contractId, Date date) throws IllegalArgumentException;

	String getEmployeeIdcPlNss(String domain, String user, Integer contractId, Date date) throws IllegalArgumentException;
	
	List<Date> getEmployeeIdcDates(String domain, String user, Integer contractId, Date date) throws IllegalArgumentException;

	String checkEmployeeIdc(String domain, String user, Integer contractId, Date date, String idc) throws IllegalArgumentException;

	EmployeeStatus getEmployeeStatus(String domain, String user, Integer contractId);

	void fillContract(String currentDomainName, Integer contractId, Integer contractType, String formativeLvl, boolean isTransform) throws IllegalArgumentException;
	
	void fillBasicCopy(String currentDomainName, Integer contractId, Integer contractType, String formativeLvl) throws IllegalArgumentException;
	
	void fillContractExtension(String currentDomainName, EmployeeInfo employeeData, ContractInfo contractData) throws IllegalArgumentException;
	
	void fillContractRelocation(String currentDomainName, Integer contractId, Map<String, String> contractRelocationInfo) throws IllegalArgumentException;
	
	void setData(String currentDomainName, String user, Integer contractId, ArrayList<Variable> data);

	// ------------------------------------------------- SEPE Files
	
	String getEmployeeCbc(String currentDomainName, String currentUser, String document, Integer contractId, Date startDate, Date endDate, String sepeIde) throws IllegalArgumentException;

	String getEmployeeCbcTransform(String currentDomainName, String currentUser, String cif, String document, Integer contractId, Date startDate, String sepeIde) throws IllegalArgumentException;
	
	String getEmployeeCto(String currentDomainName, String currentUser, String document, Integer contractId, Date startDate, Date endDate, String sepeIde) throws IllegalArgumentException;

	String getEmployeeCtoTransform(String currentDomainName, String currentUser, String cif, String document, Integer contractId, Date startDate, String sepeIde) throws IllegalArgumentException;
	
	String getEmployeeCtoExtension(String currentDomainName, String currentUser, String enterpriseCIF, String document, Integer contractId, Date extensionDate, Integer extensionNum, String sepeExtensionId) throws IllegalArgumentException;
	
	String getCertifica2PDF(String currentDomainName, String currentUser, Integer contractId, String nif, Date endDate) throws IllegalArgumentException;

	// ------------------------------------------------- TGSS Comunications
	
	void sendEmployeeAlta(String currentDomainName, String currentUser, EmployeeContractInfo employeeContractInfo) throws IllegalArgumentException;

	void sendEmployeeBaja(String currentDomainName, String currentUser, EmployeeContractInfo employeeContractInfo) throws IllegalArgumentException;

	void movPrevDelete(String currentDomainName, String currentUser, String situation, String regimen, String ctaCti,
			String nss, Date fecha) throws IllegalArgumentException;

	void altaConsolidadaDelete(String currentDomainName, String currentUser, String situation, String regimen,
			String ctaCti, String nss, Date fecha) throws IllegalArgumentException;

	void cambioCoef(String currentDomainName, String currentUser, EmployeeContractInfo employeeContractInfo,
			String coef, Date fecha) throws IllegalArgumentException;

	void cambioContrato(String currentDomainName, String currentUser, EmployeeContractInfo employeeContractInfo,
			String tc2, Date fecha) throws IllegalArgumentException;

	void cambioGrupCtz(String currentDomainName, String currentUser, EmployeeContractInfo employeeContractInfo,
			String grupCtz, Date fecha) throws IllegalArgumentException;

	void cambioOcupacion(String currentDomainName, String currentUser, EmployeeContractInfo employeeContractInfo, 
			String ocup, Date fecha) throws IllegalArgumentException;
	
	void cambioCno(String currentDomainName, String currentUser, EmployeeContractInfo employeeContractInfo, 
			String cno, Date fecha) throws IllegalArgumentException;

	void cambioCatProf(String currentDomainName, String currentUser, EmployeeContractInfo employeeContractInfo,
			String cat, Date fecha) throws IllegalArgumentException;

	// ------------------------------------------------- SEPE Comunications
	
	void sendLlamamientoSEPE(String currentDomainName, String currentUser, EmployeeContractInfo employeeContractInfo) throws IllegalArgumentException;

	void sendContractoSEPE(String currentDomainName, String currentUser, EmployeeContractInfo employeeContractInfo) throws IllegalArgumentException;

	void sendContractoCBSEPE(String currentDomainName, String currentUser, EmployeeContractInfo employeeContractInfo) throws IllegalArgumentException;

	void sendContractoCBTransformSEPE(String currentDomainName, String currentUser, EmployeeContractInfo employeeContractInfo) throws IllegalArgumentException;
	
	void removeContractoSEPE(String currentDomainName, String currentUser, EmployeeContractInfo employeeContractData) throws IllegalArgumentException;
	
	void sendCertifica2(String currentDomainName, String currentUser, Integer contractId, Certifica2Info certifica2Info)  throws IllegalArgumentException;

	void sendContractTransform(String currentDomainName, String currentUser, EmployeeContractInfo employeeContractData) throws IllegalArgumentException;

	void sendContractExtension(String currentDomainName, String currentUser, EmployeeContractInfo employeeContractData) throws IllegalArgumentException;
	
	void removeContractTransform(String currentDomainName, String currentUser, String ide, Integer contractId) throws IllegalArgumentException;

	Map<String, String> getSepeComunicationData(String currentDomainName, String currentUser, String document,
			Date date, Integer contractId) throws IllegalArgumentException;
	
	Map<String, String> getSepeTransformComunicationData(String currentDomainName, String currentUser, String document,
			String enterpriseCif, Date originalStartDate, String sepeId, Integer contractId) throws IllegalArgumentException;
	
	Map<String, String> getSepeExtensionComunicationData(String currentDomainName, String currentUser, String document,
			String enterpriseCif, Date originalStartDate, String sepeId, Integer contractId) throws IllegalArgumentException;
	
	// ------------------------------------------------- SEPE Methods
	
	Certifica2Info getCertifica2Info(String currentDomainName, String user, Integer contractId) throws IllegalArgumentException;

	// ------------------------------------------------- EmployeeContractPayments

	ContractPaymentData getContractPayements(String currentDomainName, Integer contractId);

	void updateContractPayments(String currentDomainName, Integer contractId, ContractPaymentData contractPaymentData);

	void createContractPayment(String currentDomainName, Integer contractId, ContractConceptCalc contractConceptCalc);

	void createContractPayment(String currentDomainName, Integer contractId, List<ContractConceptCalc> contractConceptCalcList);
	
	// ------------------------------------------------- ContractExtension
	
	void contractExtension(String currentDomainName, ContractExtension contractExtension) throws IllegalArgumentException;

	void deleteContractExtension(String currentDomainName, Integer contractId) throws IllegalArgumentException;

	// ------------------------------------------------- ContractTransform
	
	void contractTransform(String currentDomainName, ContractTransform contractTransform) throws IllegalArgumentException;
	
	void deleteContractTransform(String currentDomainName, Integer contractId) throws IllegalArgumentException;

	void removeContractTransform(String currentDomainName, Integer contractId) throws IllegalArgumentException;

	// ------------------------------------------------- EmployeeIrpf
	
	List<EmployeeIrpf> getEmployeeIrpf(String currentDomainName, String ssNumber, String document, Date startDate);

	void setEmployeeIrpf(String currentDomainName, Integer contractId, String fullName, String document, String ssNumber, List<EmployeeIrpf> employeeIrpfs) throws IllegalArgumentException;

	// ------------------------------------------------- ContractVariables
	
	List<ContractVariable> getContractVariables(String currentDomainName, Integer contractId) throws IllegalArgumentException;

	void updateContractVariables(String currentDomainName, List<ContractVariable> contractVariables) throws IllegalArgumentException;
	
	void createContractVariable(String currentDomainName, Integer contractId, ContractVariable contractVariable) throws IllegalArgumentException;
	
	List<Certifica2Info> getSalariesOccam(String currentDomainName, String login, ITEmployee itEmployee, Date startDate,
			Date endDate);

	// ------------------------------------------------- Agreement ContextProvider
	
	ContextDescriptor getAgreementContext(String currentDomainName, int fxLevel, Date startDate, Date endDate) throws IllegalArgumentException;

	List<Result> evalAgreement(String currentDomainName, String expression, Date startDate, int fxLevel) throws IllegalArgumentException, EvalException;

}
