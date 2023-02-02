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
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>EmployeesService</code>.
 */
public interface EmployeesServiceAsync extends AgreementServiceAsync, StatisticsServiceAsync,
		CalendarServiceAsync, EmployeeEventsServiceAsync{
	void getEnterprise(String domain, String user, AsyncCallback<Enterprise> callback)
			throws IllegalArgumentException;

	void getEnterprises(String domain, String user, AsyncCallback<Enterprise[]> callback)
			throws IllegalArgumentException;


	void getAvailableDeductions(String domain, int employeeId,
			AsyncCallback<List<Deduction>> callback)
			throws IllegalArgumentException;

	void getAvailableBonuses(String domain, int employeeId,
			AsyncCallback<List<Bonus>> callback)
			throws IllegalArgumentException;

	void getWorkplaceCosts(String domain, int workplaceId, AsyncCallback<List<Cost>> callback)
			throws IllegalArgumentException;

	void getEnterpriseCosts(String domain, int enterpriseId, AsyncCallback<List<Cost>> callback)
			throws IllegalArgumentException;

	void getSalaries(String domain, Employee employee, AsyncCallback<List<Salary>> callback)
			throws IllegalArgumentException;
	
	void getIrpfs(String domain, Employee employee, AsyncCallback<List<Irpf>> callback)
			throws IllegalArgumentException;

	void getExtras(String domain, List<Employee> employees, AsyncCallback<List<Extra>> callback)
			throws IllegalArgumentException;

	void getCostReceiptPDF(String domain, Cost cost, Salary.Type[] types,
			AsyncCallback<String> callback) throws IllegalArgumentException;

	void getCostReceiptHTML(String domain, Cost cost, Salary.Type[] types, int zoom,
			AsyncCallback<String> callback) throws IllegalArgumentException;

	void getSLDCalcReceiptHTML(String domain, String user, Cost cost, Salary.Type[] types, int zoom,
			AsyncCallback<String> callback) throws IllegalArgumentException;

	void getIrpfReceiptHTML(String domain, Irpf irpf, int zoom, AsyncCallback<String> callback)
			throws IllegalArgumentException;

	void getSalaryReceiptHTML(String domain, Cost cost, Salary.Type[] types, int zoom,
			AsyncCallback<String> callback) throws IllegalArgumentException;

	void getSalaryReceiptHTML(String domain, Salary salary, int zoom,
			AsyncCallback<String> callback) throws IllegalArgumentException;

	void saveSalaryDraft(String domain, SalaryDraft salaryDraft, AsyncCallback<Void> callback)
			throws IllegalArgumentException;

	void saveSalary(String domain, String user, SalaryDraft salaryDraft, AsyncCallback<SalaryDraft> callback)
			throws IllegalArgumentException;

	void saveSalary(String domain, SalaryDraft salaryDraft, Date[] sections, AsyncCallback<SalaryDraft> callback)
			throws IllegalArgumentException;

	void calculateIrpf(String domain, SalaryDraft salaryDraft, AsyncCallback<Double> callback)
			throws IllegalArgumentException;

	void calculateSalaryDraft(String domain, SalaryDraft salaryDraft,
			AsyncCallback<SalaryDraft> callback)
			throws IllegalArgumentException;

	void calculateSalaryDraft(String domain, SalaryDraft salaryDraft, 
			Date[] sections,
			AsyncCallback<SalaryDraft> callback)
			throws IllegalArgumentException;

	void syncSalaryDraft(String domain, String user,
			SalaryDraft salaryDraft, AsyncCallback<SalaryDraft> callback)
			throws IllegalArgumentException;

	void eval(String domain, String expression, SalaryDraft salaryDraft,
			AsyncCallback<List<Result>> callback)
			throws IllegalArgumentException, EvalException;

	void getContext(String domain, SalaryDraft salaryDraft,
			AsyncCallback<ContextDescriptor> callback)
			throws IllegalArgumentException;

	void getSalaryDraftReceipt(String domain, SalaryDraft salaryDraft, String mime,
			AsyncCallback<String> callback) throws IllegalArgumentException;

	void getSettleDraftReceipt(String domain, SalaryDraft salaryDraft, String mime,
			AsyncCallback<String> callback) throws IllegalArgumentException;
	
	void getSalaryDraftReceiptHTML(String domain, SalaryDraft salaryPreview, int zoom,
			AsyncCallback<String> callback) throws IllegalArgumentException;

	void getIrpfDraftReceipt(String domain, SalaryDraft salaryDraft, String mime,
			AsyncCallback<String> callback) throws IllegalArgumentException;

	void getIrpfDraftReceiptHTML(String domain, SalaryDraft salaryPreview, int zoom,
			AsyncCallback<String> callback) throws IllegalArgumentException;

	void getSalaryPreviewReceiptHTML(String domain, SalaryPreview salaryPreview, int zoom,
			AsyncCallback<String> callback) throws IllegalArgumentException;

	void getEmployee(String domain, int employeeId, AsyncCallback<Employee> callback) 
			throws IllegalArgumentException;

	void getEmployees(String domain, int workplaceId, Date endDate, String pattern,
			int offset, int limit, AsyncCallback<List<Employee>> callback)
			throws IllegalArgumentException;

	void getTrashEmployees(String domain, int workplaceId,
			AsyncCallback<List<Employee>> callback)
			throws IllegalArgumentException;

	void saveEvents(String domain, Events events, Date startDate, Date endDate,
			AsyncCallback<Void> callback) throws IllegalArgumentException;

	void getEvents(String domain, Integer workplaceId, Date startDate, Date endDate,
			int offset, int limit, String[] names,
			AsyncCallback<Events> callback) throws IllegalArgumentException;

	void getAvailPeriod(String domain, Integer workplaceId, String name,
			AsyncCallback<Period> callback) throws IllegalArgumentException;

	void getEventsVariables(String domain, Integer workplaceId, Integer agreementId,
			Date startDate, Date endDate,
			AsyncCallback<Map<String, String>> callback)
			throws IllegalArgumentException;
	
	void getWorkplaceEventsVariables(String currentDomainName, Integer workplaceId, Integer agreementId, Date startDate,
			Date endDate, AsyncCallback<Map<String, String>> callback) throws IllegalArgumentException;
	
	void getEmployeeEventsVariables(String domain, Integer employeeId, Date startDate, Date endDate,
			AsyncCallback<ContextDescriptor> callback);

	void getVariables(String domain, SalaryDraft salaryDraft, Date startDate, Date endDate,
			String[] names, AsyncCallback<List<Variable>> callback)
			throws IllegalArgumentException;

	void delete(String domain, Salary[] salaries, AsyncCallback<Void> callback)
			throws IllegalArgumentException;

	void pasteContract(String domain, int workplaceId, int contractId, String document,
			Date startDate, Date endDate, boolean check,
			AsyncCallback<Employee> callback) throws IllegalArgumentException;

	void moveContractId(String domain, Employee employee, AsyncCallback<Void> callback)
			throws IllegalArgumentException;

	void deleteContract(String domain, Employee employee, AsyncCallback<Void> callback)
			throws IllegalArgumentException;

	void insertPerson(String domain, Employee employee, AsyncCallback<Void> callback)
			throws IllegalArgumentException;

	void getAvaiableEmployees(String domain, AsyncCallback<Map<String, String>> callback)
			throws IllegalArgumentException;

	void getWorkplaceEmployees(String domain, Workplace workplace, AsyncCallback<WorkplaceEmployees> asyncCallback);

	void setEventsWorkplace(String domain, EventsWorkplace updateEventsWorkplace, AsyncCallback<EventsWorkplace> asyncCallback);

	void getEmployeeInfoDataBase(String domain, String user, Integer contractId, Workplace workplace, AsyncCallback<EmployeeContractInfo> asyncCallback) throws IllegalArgumentException;

	void setEmployeeInfoDataBase(String domain, EmployeeContractInfo employeeContractData,
			AsyncCallback<EmployeeContractInfo> asyncCallback);

	void createEmployeeContract(String domain, EmployeeContractInfo employeeContractData,
			AsyncCallback<EmployeeContractInfo> asyncCallback);

	void getWorkplaceEmployeesEvents(String currentDomainName, Integer workplaceId, AsyncCallback<WorkplaceEmployees> callback) throws IllegalArgumentException;

	void getEmployeeEventsByContract(String currentDomainName, Integer contractId,
			ArrayList<String> employeeContractVariablesDB, AsyncCallback<EmployeeEventsData> callback);

	void setEmployeeEventsByContract(String currentDomainName, Integer contractId,
			EmployeeEventsData employeeEventsData, AsyncCallback<EmployeeEventsData> callback);
	
	void setEmployeeAFIChanges(String currentDomainName, Integer contractId, Date newDate, boolean isChangeContract, String tc2,
			boolean isQuoteContract, Integer quoteGroup, boolean isOcupationContract, String ocupation,
			AsyncCallback<String> callback);

	void resetCalendar(String currentDomainName, Integer employeeId, AsyncCallback<String> callback);

	void getSalariesDates(String currentDomainName, SalaryInfoFilter filter, AsyncCallback<Period> callback);
	
	void getSalaries(String currentDomainName, SalaryInfoFilter filter, AsyncCallback<List<SalaryInfo>> callback);

	void deleteSalaries(String currentDomainName, ArrayList<Integer> ids, AsyncCallback<Void> callback);

	void getWorkplaceActiveEmployees(String currentDomainName, Integer workplaceId, AsyncCallback<WorkplaceEmployees> callback);

	void getEnterpriseActiveEmployees(String currentDomainName, Integer enterpriseId, AsyncCallback<List<EmployeeInfo>> callback);

	void getEmployeeCalendarInfo(String currentDomainName, Integer contractId, AsyncCallback<EmployeeCalendarInfo> callback) throws IllegalArgumentException;

	void setEmployeeCalendarInfo(String currentDomainName, Integer contractId, EmployeeCalendarInfo employeeCalendarInfo, AsyncCallback<Void> callback) throws IllegalArgumentException;

	void resetEmployeeCalendarInfo(String currentDomainName, Integer contractId, AsyncCallback<Void> callback) throws IllegalArgumentException;

	void setEmployeeEvents(String currentDomainName, Integer idEmployee, EmployeeEventsData employeeEventsData,
			AsyncCallback<EmployeeEventsData> callback);

	void setEventsDraft(String currentDomainName, ArrayList<EventEmployee> eventEmployees,
			AsyncCallback<ArrayList<EventEmployee>> callback);

	void generateCertifaca2(String currentDomainName, String user, Integer contractId, AsyncCallback<Void> callback) throws IllegalArgumentException;
	
	void generateCertifaca2(String currentDomainName, String user, Integer contractId, Certifica2Info certifica2Info,
			AsyncCallback<Void> callback) throws IllegalArgumentException;
	
	// ------------------------------------------------- TGSS Files
	
	void getEmployeeTa(String domain,  String user, Integer contractId, String situation, String regimen, String ctaCti, String nss, Date fecha, AsyncCallback<String> callback) throws IllegalArgumentException;

	void getEmployeeIdc(String domain,  String user, Integer contractId, Date date, AsyncCallback<String> callback) throws IllegalArgumentException;

	void checkEmployeeIdc(String domain, String user, Integer contractId, Date date, String idc, AsyncCallback<String> callback) throws IllegalArgumentException;

	void getEmployeeIdcPlNss(String domain,  String user, Integer contractId, Date date, AsyncCallback<String> callback) throws IllegalArgumentException ;
	
	void getEmployeeIdcDates(String domain,  String user, Integer contractId, Date date, AsyncCallback<List<Date>> callback) throws IllegalArgumentException;

	void getEmployeeStatus(String domain, String user, Integer contractId, AsyncCallback<EmployeeStatus> callback);

	void fillContract(String currentDomainName, Integer contractId, Integer contractType, String formativeLvl, boolean isTransform, AsyncCallback<Void> callback) throws IllegalArgumentException;
	
	void setData(String currentDomainName, String user, Integer contractId, ArrayList<Variable> data, AsyncCallback<Void> callback);

	// ------------------------------------------------- SEPE Files
	
	void getEmployeeCbc(String currentDomainName, String currentUser, String document, Integer contractId, Date startDate, Date endDate, String sepeIde, AsyncCallback<String> callback) throws IllegalArgumentException;

	void getEmployeeCbcTransform(String currentDomainName, String currentUser, String cif, String document, Integer contractId, Date startDate, String sepeIde, AsyncCallback<String> callback) throws IllegalArgumentException;
	
	void getEmployeeCto(String currentDomainName, String currentUser, String document, Integer contractId, Date startDate, Date endDate, String sepeIde, AsyncCallback<String> callback) throws IllegalArgumentException;
	
	void getEmployeeCtoTransform(String currentDomainName, String currentUser, String cif, String document, Integer contractId, Date startDate, String sepeIde, AsyncCallback<String> callback) throws IllegalArgumentException;
	
	void getEmployeeCtoExtension(String currentDomainName, String currentUser, String enterpriseCIF, String document, Integer contractId, Date extensionDate, Integer extensionNum, String sepeExtensionId, AsyncCallback<String> callback) throws IllegalArgumentException;
	
	void getCertifica2PDF(String currentDomainName, String currentUser, Integer contractId, String nif, Date endDate, AsyncCallback<String> callback) throws IllegalArgumentException;

	// ------------------------------------------------- TGSS Comunications
	
	void sendEmployeeAlta(String currentDomainName, String currentUser, EmployeeContractInfo employeeContractInfo,
			AsyncCallback<Void> callback) throws IllegalArgumentException;

	void sendEmployeeBaja(String currentDomainName, String currentUser, EmployeeContractInfo employeeContractInfo,
			AsyncCallback<Void> callback) throws IllegalArgumentException;

	void movPrevDelete(String currentDomainName, String currentUser, String situation, String regimen, String ctaCti,
			String nss, Date fecha, AsyncCallback<Void> callback) throws IllegalArgumentException;

	void altaConsolidadaDelete(String currentDomainName, String currentUser, String situation, String regimen,
			String ctaCti, String nss, Date fecha, AsyncCallback<Void> callback) throws IllegalArgumentException;

	void cambioCoef(String currentDomainName, String currentUser, EmployeeContractInfo employeeContractInfo, 
			String coef, Date fecha, AsyncCallback<Void> callback) throws IllegalArgumentException;

	void cambioContrato(String currentDomainName, String currentUser, EmployeeContractInfo employeeContractInfo, 
			String tc2, Date fecha, AsyncCallback<Void> callback) throws IllegalArgumentException;
	
	void cambioGrupCtz(String currentDomainName, String currentUser, EmployeeContractInfo employeeContractInfo, 
			String grupCtz, Date fecha, AsyncCallback<Void> callback) throws IllegalArgumentException;

	void cambioOcupacion(String currentDomainName, String currentUser, EmployeeContractInfo employeeContractInfo,
			String ocup, Date fecha, AsyncCallback<Void> callback) throws IllegalArgumentException;

	void cambioCatProf(String currentDomainName, String currentUser, EmployeeContractInfo employeeContractInfo,
			String cat, Date fecha, AsyncCallback<Void> callback) throws IllegalArgumentException;

	// ------------------------------------------------- SEPE Comunications
	
	void sendContractoSEPE(String currentDomainName, String currentUser, EmployeeContractInfo employeeContractInfo, AsyncCallback<Void> callback) throws IllegalArgumentException;

	void sendContractoCBSEPE(String currentDomainName, String currentUser, EmployeeContractInfo employeeContractInfo, AsyncCallback<Void> callback) throws IllegalArgumentException;

	void sendContractoCBTransformSEPE(String currentDomainName, String currentUser, EmployeeContractInfo employeeContractInfo, AsyncCallback<Void> callback) throws IllegalArgumentException;
	
	void removeContractoSEPE(String currentDomainName, String currentUser, EmployeeContractInfo employeeContractData, AsyncCallback<Void> callback) throws IllegalArgumentException;
	
	void sendCertifica2(String currentDomainName, String currentUser, Integer contractId, Certifica2Info certifica2Info, AsyncCallback<Void> callback) throws IllegalArgumentException;

	void sendContractTransform(String currentDomainName, String currentUser, EmployeeContractInfo employeeContractData, AsyncCallback<Void> callback) throws IllegalArgumentException;

	void sendContractExtension(String currentDomainName, String currentUser, EmployeeContractInfo employeeContractData, AsyncCallback<Void> callback) throws IllegalArgumentException;

	void removeContractTransform(String currentDomainName, String currentUser, String ide, Integer contractId, AsyncCallback<Void> callback) throws IllegalArgumentException;
	
	void getSepeComunicationData(String currentDomainName, String currentUser, String document, Date date, Integer contractId,
			AsyncCallback<Map<String, String>> callback) throws IllegalArgumentException;
	
	void getSepeTransformComunicationData(String currentDomainName, String currentUser, String document, String enterpriseCif, 
			Date originalStartDate, String sepeId, Integer contractId, AsyncCallback<Map<String, String>> callback) throws IllegalArgumentException;

	void getSepeExtensionComunicationData(String currentDomainName, String currentUser, String document, String enterpriseCif, 
			Date originalStartDate, String sepeId, Integer contractId, AsyncCallback<Map<String, String>> callback) throws IllegalArgumentException;

	// ------------------------------------------------- SEPE Methods
	
	void getCertifica2Info(String currentDomainName, String user, Integer contractId, AsyncCallback<Certifica2Info> callback) throws IllegalArgumentException;
	
	// ------------------------------------------------- EmployeeContractPayments
	
	void getContractPayements(String currentDomainName, Integer contractId, AsyncCallback<ContractPaymentData> callback);

	void updateContractPayments(String currentDomainName, Integer contractId, ContractPaymentData contractPaymentData, AsyncCallback<Void> callback);

	void createContractPayment(String currentDomainName, Integer contractId, ContractConceptCalc contractConceptCalc, AsyncCallback<Void> callback);
	
	void createContractPayment(String currentDomainName, Integer contractId, List<ContractConceptCalc> contractConceptCalcList, AsyncCallback<Void> callback);

	// ------------------------------------------------- ContractExtension
	
	void contractExtension(String currentDomainName, ContractExtension contractExtension, AsyncCallback<Void> callback) throws IllegalArgumentException;

	void deleteContractExtension(String currentDomainName, Integer contractId, AsyncCallback<Void> callback) throws IllegalArgumentException;
	
	// ------------------------------------------------- ContractTransform
	
	void contractTransform(String currentDomainName, ContractTransform contractTransform, AsyncCallback<Void> callback) throws IllegalArgumentException;

	void deleteContractTransform(String currentDomainName, Integer contractId, AsyncCallback<Void> callback) throws IllegalArgumentException;
	
	void removeContractTransform(String currentDomainName, Integer contractId, AsyncCallback<Void> callback) throws IllegalArgumentException;

	// ------------------------------------------------- EmployeeIrpf
	
	void getEmployeeIrpf(String currentDomainName, String ssNumber, String document, Date startDate, AsyncCallback<List<EmployeeIrpf>> callback) throws IllegalArgumentException;

	void setEmployeeIrpf(String currentDomainName, Integer contractId, String fullName, String document, String ssNumber, List<EmployeeIrpf> employeeIrpfs, AsyncCallback<Void> callback) throws IllegalArgumentException;

	// ------------------------------------------------- ContractVariables
	
	void getContractVariables(String currentDomainName, Integer contractId, AsyncCallback<List<ContractVariable>> callback) throws IllegalArgumentException;

	void updateContractVariables(String currentDomainName, List<ContractVariable> contractVariables, AsyncCallback<Void> callback) throws IllegalArgumentException;
	
	void createContractVariable(String currentDomainName, Integer contractId, ContractVariable contractVariable, AsyncCallback<Void> callback) throws IllegalArgumentException;
	
	void getSalariesOccam(String currentDomainName, String login, ITEmployee itEmployee, Date startDate, Date endDate,
			AsyncCallback<List<Certifica2Info>> callback) throws IllegalArgumentException;

	// ------------------------------------------------- Agreement ContextProvider
	
	void getAgreementContext(String currentDomainName, int fxLevel, Date startDate, Date endDate, AsyncCallback<ContextDescriptor> callback) throws IllegalArgumentException;
	
	void evalAgreement(String currentDomainName, String expression, Date startDate, int fxLevel, AsyncCallback<List<Result>> callback) throws IllegalArgumentException, EvalException;

}
