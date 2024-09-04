/**
 * 
 */
package com.esferalia.aon.gwt.payroll.client;

//import static com.esferalia.aon.gwt.common.shared.DateUtils.getFirstDayOfYear;
//
//import java.util.ArrayList;
//import java.util.Date;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Map;
//import java.util.Set;
//
//import org.junit.jupiter.api.BeforeEach;
//
//import com.esferalia.aon.gwt.common.shared.DateUtils;
//import com.esferalia.aon.gwt.common.shared.EvalException;
//import com.esferalia.aon.gwt.payroll.shared.CalendarDraft.DayType;
//import com.esferalia.aon.gwt.payroll.shared.Certifica2Info;
//import com.esferalia.aon.gwt.payroll.shared.ContextDescriptor;
//import com.esferalia.aon.gwt.payroll.shared.ContractConceptCalc;
//import com.esferalia.aon.gwt.payroll.shared.ContractExtension;
//import com.esferalia.aon.gwt.payroll.shared.ContractInfo;
//import com.esferalia.aon.gwt.payroll.shared.ContractPaymentData;
//import com.esferalia.aon.gwt.payroll.shared.ContractTransform;
//import com.esferalia.aon.gwt.payroll.shared.ContractVariable;
//import com.esferalia.aon.gwt.payroll.shared.EmployeeCalendarInfo;
//import com.esferalia.aon.gwt.payroll.shared.EmployeeContractInfo;
//import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsData;
//import com.esferalia.aon.gwt.payroll.shared.EmployeeEventsUpdate;
//import com.esferalia.aon.gwt.payroll.shared.EmployeeInfo;
//import com.esferalia.aon.gwt.payroll.shared.EmployeeIrpf;
//import com.esferalia.aon.gwt.payroll.shared.EmployeeStatus;
//import com.esferalia.aon.gwt.payroll.shared.EventEmployee;
//import com.esferalia.aon.gwt.payroll.shared.EventsWorkplace;
//import com.esferalia.aon.gwt.payroll.shared.Extra;
//import com.esferalia.aon.gwt.payroll.shared.ITEmployee;
//import com.esferalia.aon.gwt.payroll.shared.Payment;
//import com.esferalia.aon.gwt.payroll.shared.Period;
//import com.esferalia.aon.gwt.payroll.shared.Result;
//import com.esferalia.aon.gwt.payroll.shared.Salary;
//import com.esferalia.aon.gwt.payroll.shared.SalaryInfo;
//import com.esferalia.aon.gwt.payroll.shared.SalaryInfoFilter;
//import com.esferalia.aon.gwt.payroll.shared.Workplace;
//import com.esferalia.aon.gwt.payroll.shared.WorkplaceEmployees;
//import com.google.gwt.core.client.GWT;
//import com.google.gwt.junit.client.GWTTestCase;
//import com.google.gwt.user.client.rpc.AsyncCallback;
//import com.ibm.icu.impl.Assert;

/**
 * 
 * @author rtrepiana
 *
 */
public class GWTAgreementDraftTestCase { // extends GWTTestCase {
//
//	private static final class EmployeesServiceImpl extends
//			AbstractEmployeesServiceAsync {
//		@Override
//		public void getContext(String domain,
//				com.esferalia.aon.gwt.payroll.shared.AgreementDraft agreementDraft,
//				int levelId,
//				AsyncCallback<ContextDescriptor> callback)
//				throws IllegalArgumentException {
//			ContextDescriptor contextDescriptor = new ContextDescriptor();
//			callback.onSuccess(contextDescriptor);
//		}
//
//		@Override
//		public void getAvailablePayments(String domain, int employeeId,
//				AsyncCallback<List<Payment>> callback)
//				throws IllegalArgumentException {
//			List<Payment> concepts = new ArrayList<Payment>();
//			callback.onSuccess(concepts);
//		}
//
//		@Override
//		public void calculateAgreementDraft(String domain,
//				com.esferalia.aon.gwt.payroll.shared.AgreementDraft draft,
//				AsyncCallback<com.esferalia.aon.gwt.payroll.shared.AgreementDraft> callback)
//				throws IllegalArgumentException {
//			try {
//				Set<Payment> dbPayments = getDbPayments(1);
//				Set<Extra> dbExtras = getDbExtras(dbPayments);
//				
//				Set<Extra> allExtras = new HashSet<Extra>(dbExtras);
//				Set<Payment> allPayments = new HashSet<Payment>(dbPayments);
//				
//
//				draft.setPayments(allPayments);
//				draft.setExtras(allExtras);
//				
//				callback.onSuccess(draft);
//			} catch (Throwable t) {
//				Assert.fail(t.getMessage());
//			}
//		}
//
//		
//
//		@Override
//		public void setEmployeeEvents(String domain, int contract, EmployeeEventsUpdate updateInfo,
//				AsyncCallback<EmployeeEventsUpdate> callback) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void getEmployeeEventsVariables(String domain, Integer employeeId, Date startDate, Date endDate,
//				AsyncCallback<ContextDescriptor> callback) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void getEmployeeEvents(String domain, int contract, ArrayList<String> employeeContractVariables,
//				AsyncCallback<EmployeeEventsData> callback) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void getWorkplaceEmployees(String domain, Workplace workplace, AsyncCallback<WorkplaceEmployees> asyncCallback) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void setEventsWorkplace(String domain, EventsWorkplace updateEventsWorkplace,
//				AsyncCallback<EventsWorkplace> asyncCallback) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void getEmployeeInfoDataBase(String domain, String user, Integer contractId, Workplace workplace,
//				AsyncCallback<EmployeeContractInfo> asyncCallback) throws IllegalArgumentException {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void setEmployeeInfoDataBase(String domain, EmployeeContractInfo newEmployeeInfo,
//				AsyncCallback<EmployeeContractInfo> asyncCallback) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void createEmployeeContract(String domain, EmployeeContractInfo newEmployeeInfo,
//				AsyncCallback<EmployeeContractInfo> asyncCallback) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void getWorkplaceEmployeesEvents(String currentDomainName, Integer workplaceId,
//				AsyncCallback<WorkplaceEmployees> callback) throws IllegalArgumentException {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void getWorkplaceEventsVariables(String currentDomainName, Integer workplaceId, Integer agreementId,
//				Date startDate, Date endDate, AsyncCallback<Map<String, String>> callback)
//				throws IllegalArgumentException {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void getEmployeeEventsByContract(String currentDomainName, Integer contractId,
//				ArrayList<String> employeeContractVariablesDB, AsyncCallback<EmployeeEventsData> callback) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void setEmployeeAFIChanges(String currentDomainName, Integer contractId, Date newDate, boolean isChangeContract, String tc2,
//				boolean isQuoteContract, Integer quoteGroup, boolean isOcupationContract, String ocupation,
//				AsyncCallback<String> callback) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void resetCalendar(String currentDomainName, Integer employeeId, AsyncCallback<String> callback) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void getSalaries(String currentDomainName, String userLogin, SalaryInfoFilter filter,
//				AsyncCallback<List<SalaryInfo>> callback) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void deleteSalaries(String currentDomainName, ArrayList<Integer> ids, AsyncCallback<Void> callback) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void getWorkplaceActiveEmployees(String currentDomainName, String userLogin, Integer workplaceId,
//				AsyncCallback<WorkplaceEmployees> callback) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void getEnterpriseActiveEmployees(String currentDomainName, String userLogin, Integer enterpriseId,
//				AsyncCallback<List<EmployeeInfo>> callback) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void getEmployeeCalendarInfo(String currentDomainName, Integer contractId,
//				AsyncCallback<EmployeeCalendarInfo> callback) throws IllegalArgumentException {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void setEmployeeCalendarInfo(String currentDomainName, Integer contractId,
//				EmployeeCalendarInfo employeeCalendarInfo, AsyncCallback<Void> callback) throws IllegalArgumentException {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void resetEmployeeCalendarInfo(String currentDomainName, Integer contractId,
//				AsyncCallback<Void> callback) throws IllegalArgumentException {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void setEmployeeEvents(String currentDomainName, Integer idEmployee,
//				EmployeeEventsData employeeEventsData, AsyncCallback<EmployeeEventsData> callback) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void setEventsDraft(String currentDomainName, ArrayList<EventEmployee> eventEmployees,
//				AsyncCallback<ArrayList<EventEmployee>> callback) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void generateCertifaca2(String currentDomainName, String user, Integer contractId,
//				AsyncCallback<Void> callback) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void fillContract(String currentDomainName, Integer contractId, Integer contractType, String formativeLvl, boolean isTransform,
//				AsyncCallback<Void> callback) throws IllegalArgumentException {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void sendEmployeeAlta(String currentDomainName, String currentUser,
//				EmployeeContractInfo employeeContractInfo, AsyncCallback<Void> callback) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void sendEmployeeBaja(String currentDomainName, String currentUser,
//				EmployeeContractInfo employeeContractInfo, AsyncCallback<Void> callback) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void movPrevDelete(String currentDomainName, String currentUser, String situation, String regimen,
//				String ctaCti, String nss, Date fecha, AsyncCallback<Void> callback) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void altaConsolidadaDelete(String currentDomainName, String currentUser, String situation,
//				String regimen, String ctaCti, String nss, Date fecha, AsyncCallback<Void> callback) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void cambioGrupCtz(String currentDomainName, String currentUser, EmployeeContractInfo employeeContractInfo, 
//				String grup_ctz, Date fecha, AsyncCallback<Void> callback) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void cambioOcupacion(String currentDomainName, String currentUser, EmployeeContractInfo employeeContractInfo, 
//				String ocup, Date fecha, AsyncCallback<Void> callback) {
//			// TODO Auto-generated method stub
//			
//		}
//		
//		@Override
//		public void cambioCno(String currentDomainName, String currentUser, EmployeeContractInfo employeeContractInfo, 
//				String cno, Date fecha, AsyncCallback<Void> callback) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void cambioCatProf(String currentDomainName, String currentUser, EmployeeContractInfo employeeContractInfo, 
//				String cat, Date fecha, AsyncCallback<Void> callback) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void sendLlamamientoSEPE(String currentDomainName, String currentUser,
//				EmployeeContractInfo employeeContractInfo, AsyncCallback<Void> callback) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void sendContractoSEPE(String currentDomainName, String currentUser,
//				EmployeeContractInfo employeeContractInfo, AsyncCallback<Void> callback) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void sendContractoCBSEPE(String currentDomainName, String currentUser,
//				EmployeeContractInfo employeeContractInfo, AsyncCallback<Void> callback) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void sendCertifica2(String currentDomainName, String currentUser, Integer contractId, Certifica2Info certifica2Info,
//				AsyncCallback<Void> callback) throws IllegalArgumentException {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void getCertifica2PDF(String currentDomainName, String currentUser, Integer contractId, String nif, Date endDate,
//				AsyncCallback<String> callback) throws IllegalArgumentException {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void getCertifica2Info(String currentDomainName, String user, Integer contractId,
//				AsyncCallback<Certifica2Info> callback) throws IllegalArgumentException {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void removeContractoSEPE(String currentDomainName, String currentUser,
//				EmployeeContractInfo employeeContractData, AsyncCallback<Void> callback) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void generateCertifaca2(String currentDomainName, String user, Integer contractId, Certifica2Info certifica2Info,
//				AsyncCallback<Void> callback) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void cambioCoef(String currentDomainName, String currentUser, EmployeeContractInfo employeeContractInfo,
//				String coef, Date fecha, AsyncCallback<Void> callback) throws IllegalArgumentException {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void setEmployeeEventsByContract(String currentDomainName, Integer contractId,
//				EmployeeEventsData employeeEventsData, AsyncCallback<EmployeeEventsData> callback) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void getContractPayements(String currentDomainName, Integer contractId,
//				AsyncCallback<ContractPaymentData> callback) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void updateContractPayments(String currentDomainName, Integer contractId,
//				ContractPaymentData contractPaymentData, AsyncCallback<Void> callback) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void createContractPayment(String currentDomainName, Integer contractId,
//				ContractConceptCalc contractConceptCalc, AsyncCallback<Void> callback) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void contractExtension(String currentDomainName, ContractExtension contractExtension,
//				AsyncCallback<Void> callback) throws IllegalArgumentException {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void deleteContractExtension(String currentDomainName, Integer contractId,
//				AsyncCallback<Void> callback) throws IllegalArgumentException {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void contractTransform(String currentDomainName, ContractTransform contractTransform,
//				AsyncCallback<Void> callback) throws IllegalArgumentException {
//			// TODO Auto-generated method stub
//			
//		}
//		
//		@Override
//		public void deleteContractTransform(String currentDomainName, Integer contractId,
//				AsyncCallback<Void> callback) throws IllegalArgumentException {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void removeContractTransform(String currentDomainName, Integer contractId,
//				AsyncCallback<Void> callback) throws IllegalArgumentException {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void getEmployeeIrpf(String currentDomainName, String ssNumber, String document, Date startDate,
//				AsyncCallback<List<EmployeeIrpf>> callback) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void setEmployeeIrpf(String currentDomainName, Integer contractId, String fullName, String document, String ssNumber, List<EmployeeIrpf> employeeIrpfs,
//				AsyncCallback<Void> callback) throws IllegalArgumentException {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void getContractVariables(String currentDomainName, Integer contractId,
//				AsyncCallback<List<ContractVariable>> callback) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void updateContractVariables(String currentDomainName, List<ContractVariable> contractVariables,
//				AsyncCallback<Void> callback) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void getSalariesDates(String currentDomainName, String userLogin, SalaryInfoFilter filter,
//				AsyncCallback<Period> callback) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void getEmployeeTa(String domain, String user, Integer contractId, String situation, String regimen, String ctaCti,
//				String nss, Date fecha, AsyncCallback<String> callback) throws IllegalArgumentException {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void checkAndUpdateServiAgreement(String domain, String userLogin, Integer agreementId, String ssNumber, Integer lastDateYear,
//				AsyncCallback<Date> asyncCallback) throws IllegalArgumentException {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void createContractPayment(String currentDomainName, Integer contractId,
//				List<ContractConceptCalc> contractConceptCalcList, AsyncCallback<Void> callback) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void getSalariesOccam(String currentDomainName, String login, ITEmployee itEmployee, Date startDate,
//				Date endDate, AsyncCallback<List<Certifica2Info>> callback) {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void createContractVariable(String currentDomainName, Integer contractId,
//				ContractVariable contractVariable, AsyncCallback<Void> callback) throws IllegalArgumentException {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void sendContractTransform(String currentDomainName, String currentUser,
//				EmployeeContractInfo employeeContractData, AsyncCallback<Void> callback) throws IllegalArgumentException {
//			// TODO Auto-generated method stub
//			
//		}
//		
//		@Override
//		public void sendContractExtension(String currentDomainName, String currentUser,
//				EmployeeContractInfo employeeContractData, AsyncCallback<Void> callback) throws IllegalArgumentException {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void removeContractTransform(String currentDomainName, String currentUser, String ide, Integer contractId,
//				AsyncCallback<Void> callback) throws IllegalArgumentException {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void getSepeComunicationData(String currentDomainName, String currentUser, String document, Date date,
//				Integer contractId, AsyncCallback<Map<String, String>> callback) throws IllegalArgumentException {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void getEmployeeCbc(String currentDomainName, String currentUser, String document, Integer contractId,
//				Date startDate, Date endDate, String sepeIde, AsyncCallback<String> callback)
//				throws IllegalArgumentException {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void getEmployeeCbcTransform(String currentDomainName, String currentUser, String cif, String document,
//				Integer contractId, Date startDate, String sepeIde, AsyncCallback<String> callback)
//				throws IllegalArgumentException {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void getEmployeeCto(String currentDomainName, String currentUser, String document, Integer contractId,
//				Date startDate, Date endDate, String sepeIde, AsyncCallback<String> callback)
//				throws IllegalArgumentException {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void getEmployeeCtoTransform(String currentDomainName, String currentUser, String cif, String document,
//				Integer contractId, Date startDate, String sepeIde, AsyncCallback<String> callback)
//				throws IllegalArgumentException {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void sendContractoCBTransformSEPE(String currentDomainName, String currentUser,
//				EmployeeContractInfo employeeContractInfo, AsyncCallback<Void> callback)
//				throws IllegalArgumentException {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void getSepeTransformComunicationData(String currentDomainName, String currentUser, String document,
//				String enterpriseCif, Date originalStartDate, String sepeId, Integer contractId,
//				AsyncCallback<Map<String, String>> callback) throws IllegalArgumentException {
//			// TODO Auto-generated method stub
//			
//		}
//		
//		@Override
//		public void getSepeExtensionComunicationData(String currentDomainName, String currentUser, String document,
//				String enterpriseCif, Date originalStartDate, String sepeId, Integer contractId,
//				AsyncCallback<Map<String, String>> callback) throws IllegalArgumentException {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void getEmployeeCtoExtension(String currentDomainName, String currentUser, String enterpriseCIF,
//				String document, Integer contractId, Date extensionDate, Integer extensionNum, String sepeExtensionId,
//				AsyncCallback<String> callback) throws IllegalArgumentException {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void getAgreementContext(String currentDomainName, int fxLevel, Date startDate, Date endDate,
//				AsyncCallback<ContextDescriptor> callback) throws IllegalArgumentException {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void evalAgreement(String currentDomainName, String expression, Date startDate, int fxLevel,
//				AsyncCallback<List<Result>> callback) throws IllegalArgumentException, EvalException {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void fillContractExtension(String currentDomainName, EmployeeInfo employeeData,
//				ContractInfo contractData, AsyncCallback<Void> callback) throws IllegalArgumentException {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void fillContractRelocation(String currentDomainName, Integer contractId,
//				Map<String, String> contractRelocationInfo, AsyncCallback<Void> callback)
//				throws IllegalArgumentException {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void fillBasicCopy(String currentDomainName, Integer contractId, Integer contractType,
//				String formativeLvl, AsyncCallback<Void> callback) throws IllegalArgumentException {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void cambioContrato(String currentDomainName, String currentUser,
//				EmployeeContractInfo employeeContractInfo, String tc2, String partialityCoef, Date fecha,
//				AsyncCallback<Void> callback) throws IllegalArgumentException {
//			// TODO Auto-generated method stub
//			
//		}
//
//		@Override
//		public void updateHolidayCalendar(String currentDomainName, int workplaceId, Integer holidayId, DayType[] dayTypes,
//				AsyncCallback<Void> callback) throws IllegalArgumentException {
//			// TODO Auto-generated method stub
//			
//		}		
//	}
//
//	@BeforeEach
//	/*
//	 * (non-Javadoc)
//	 * 
//	 * @see com.google.gwt.junit.client.GWTTestCase#getModuleName()
//	 */
//	@Override
//	public String getModuleName() {
//		return "com.esferalia.aon.gwt.payroll.TestingPayroll";
//	}
//
//	public void testEmptyAgreement() {
//		AgreementDraft agreementDraftWidget = new AgreementDraft();
//
//		final com.esferalia.aon.gwt.payroll.shared.AgreementDraft agreementDraft = new com.esferalia.aon.gwt.payroll.shared.AgreementDraft();
//
//		// only id, domain and draft dates,n no more
//		agreementDraft.setId(666);
//		agreementDraft.setDomain(999);
//		agreementDraft.setStartDate(DateUtils.getFirstDayOfMonth());
//		agreementDraft.setEndDate(DateUtils.getLastDayOfMonth());
//
//		final AgreementDraftObject agreementDraftObject = new AgreementDraftObject(
//				6969, "TODO: Domain Name", "TODO: User Login", agreementDraft, new AbstractEmployeesServiceAsync() {
//
//					@Override
//					public void getContext(
//							String domain,
//							com.esferalia.aon.gwt.payroll.shared.AgreementDraft agreementDraft,
//							int levelId,
//							AsyncCallback<ContextDescriptor> callback)
//							throws IllegalArgumentException {
//						ContextDescriptor contextDescriptor = new ContextDescriptor();
//						callback.onSuccess(contextDescriptor);
//					}
//
//					@Override
//					public void getAvailablePayments(String domain,int employeeId,
//							AsyncCallback<List<Payment>> callback)
//							throws IllegalArgumentException {
//						List<Payment> concepts = new ArrayList<Payment>();
//						concepts.add(newConcept(13, 999, null));
//						callback.onSuccess(concepts);
//					}
//
//					@Override
//					public void calculateAgreementDraft(String domain,
//							com.esferalia.aon.gwt.payroll.shared.AgreementDraft agreementDraft,
//							AsyncCallback<com.esferalia.aon.gwt.payroll.shared.AgreementDraft> callback)
//							throws IllegalArgumentException {
//
//						callback.onSuccess(agreementDraft);
//					}
//
//					
//
//					@Override
//					public void setEmployeeEvents(String domain, int contract, EmployeeEventsUpdate updateInfo,
//							AsyncCallback<EmployeeEventsUpdate> callback) {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void getEmployeeEventsVariables(String domain, Integer employeeId, Date startDate, Date endDate,
//							AsyncCallback<ContextDescriptor> callback) {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void getEmployeeEvents(String domain, int contract, ArrayList<String> employeeContractVariables,
//							AsyncCallback<EmployeeEventsData> callback) {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void getWorkplaceEmployees(String domain, Workplace workplace,
//							AsyncCallback<WorkplaceEmployees> asyncCallback) {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void setEventsWorkplace(String domain, EventsWorkplace updateEventsWorkplace,
//							AsyncCallback<EventsWorkplace> asyncCallback) {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void getEmployeeInfoDataBase(String domain, String user, Integer contractId, Workplace workplace,
//							AsyncCallback<EmployeeContractInfo> asyncCallback) throws IllegalArgumentException {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void setEmployeeInfoDataBase(String domain, EmployeeContractInfo newEmployeeInfo,
//							AsyncCallback<EmployeeContractInfo> asyncCallback) {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void createEmployeeContract(String domain, EmployeeContractInfo newEmployeeInfo,
//							AsyncCallback<EmployeeContractInfo> asyncCallback) {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void getWorkplaceEmployeesEvents(String currentDomainName, Integer workplaceId,
//							AsyncCallback<WorkplaceEmployees> callback) throws IllegalArgumentException {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void getWorkplaceEventsVariables(String currentDomainName, Integer workplaceId,
//							Integer agreementId, Date startDate, Date endDate,
//							AsyncCallback<Map<String, String>> callback) throws IllegalArgumentException {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void getEmployeeEventsByContract(String currentDomainName, Integer contractId,
//							ArrayList<String> employeeContractVariablesDB, AsyncCallback<EmployeeEventsData> callback) {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void setEmployeeAFIChanges(String currentDomainName, Integer contractId,
//							Date newDate,
//							boolean isChangeContract, String tc2, boolean isQuoteContract, Integer quoteGroup,
//							boolean isOcupationContract, String ocupation, AsyncCallback<String> callback) {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void resetCalendar(String currentDomainName, Integer employeeId,
//							AsyncCallback<String> callback) {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void getSalaries(String currentDomainName, String userLogin, SalaryInfoFilter filter,
//							AsyncCallback<List<SalaryInfo>> callback) {
//						// TODO Auto-generated method stub
//						
//					}
//					
//					@Override
//					public void deleteSalaries(String currentDomainName, ArrayList<Integer> ids,
//							AsyncCallback<Void> callback) {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void getWorkplaceActiveEmployees(String currentDomainName, String userLogin, Integer workplaceId,
//							AsyncCallback<WorkplaceEmployees> callback) {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void getEnterpriseActiveEmployees(String currentDomainName, String userLogin, Integer enterpriseId,
//							AsyncCallback<List<EmployeeInfo>> callback) {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void getEmployeeCalendarInfo(String currentDomainName, Integer contractId,
//							AsyncCallback<EmployeeCalendarInfo> callback) throws IllegalArgumentException {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void setEmployeeCalendarInfo(String currentDomainName, Integer contractId,
//							EmployeeCalendarInfo employeeCalendarInfo, AsyncCallback<Void> callback) throws IllegalArgumentException {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void resetEmployeeCalendarInfo(String currentDomainName, Integer contractId,
//							AsyncCallback<Void> callback) throws IllegalArgumentException {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void setEmployeeEvents(String currentDomainName, Integer idEmployee,
//							EmployeeEventsData employeeEventsData, AsyncCallback<EmployeeEventsData> callback) {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void setEventsDraft(String currentDomainName, ArrayList<EventEmployee> eventEmployees,
//							AsyncCallback<ArrayList<EventEmployee>> callback) {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void generateCertifaca2(String currentDomainName, String user, Integer contractId,
//							AsyncCallback<Void> callback) {
//						// TODO Auto-generated method stub
//						
//					}
//					
//					@Override
//					public void getEmployeeIdc(String domain, String user, Integer contractId, Date date,
//							AsyncCallback<String> callback) {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void getEmployeeIdcPlNss(String domain, String user, Integer contractId, Date date,
//							AsyncCallback<String> callback) {
//						// TODO Auto-generated method stub
//						
//					}
//					
//					@Override
//					public void getEmployeeStatus(String domain, String user, Integer contractId, AsyncCallback<EmployeeStatus> callback) {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void fillContract(String currentDomainName, Integer contractId, Integer contractType, String formativeLvl, boolean isTransform, AsyncCallback<Void> callback) throws IllegalArgumentException {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void sendEmployeeAlta(String currentDomainName, String currentUser,
//							EmployeeContractInfo employeeContractInfo, AsyncCallback<Void> callback) {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void sendEmployeeBaja(String currentDomainName, String currentUser,
//							EmployeeContractInfo employeeContractInfo, AsyncCallback<Void> callback) {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void movPrevDelete(String currentDomainName, String currentUser, String situation,
//							String regimen, String ctaCti, String nss, Date fecha, AsyncCallback<Void> callback) {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void altaConsolidadaDelete(String currentDomainName, String currentUser, String situation,
//							String regimen, String ctaCti, String nss, Date fecha, AsyncCallback<Void> callback) {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void cambioGrupCtz(String currentDomainName, String currentUser,EmployeeContractInfo employeeContractInfo, 
//							String grup_ctz, Date fecha, AsyncCallback<Void> callback) {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void cambioOcupacion(String currentDomainName, String currentUser, EmployeeContractInfo employeeContractInfo, 
//							String ocup, Date fecha, AsyncCallback<Void> callback) {
//						// TODO Auto-generated method stub
//						
//					}
//					
//					@Override
//					public void cambioCno(String currentDomainName, String currentUser, EmployeeContractInfo employeeContractInfo, 
//							String cno, Date fecha, AsyncCallback<Void> callback) {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void cambioCatProf(String currentDomainName, String currentUser, EmployeeContractInfo employeeContractInfo, 
//							String cat, Date fecha, AsyncCallback<Void> callback) {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void sendLlamamientoSEPE(String currentDomainName, String currentUser,
//							EmployeeContractInfo employeeContractInfo, AsyncCallback<Void> callback) {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void sendContractoSEPE(String currentDomainName, String currentUser,
//							EmployeeContractInfo employeeContractInfo, AsyncCallback<Void> callback) {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void sendContractoCBSEPE(String currentDomainName, String currentUser,
//							EmployeeContractInfo employeeContractInfo, AsyncCallback<Void> callback) {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void sendCertifica2(String currentDomainName, String currentUser, Integer contractId, Certifica2Info certifica2Info,
//							AsyncCallback<Void> callback) throws IllegalArgumentException {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void getCertifica2PDF(String currentDomainName, String currentUser, Integer contractId, String nif, Date endDate,
//							AsyncCallback<String> callback) throws IllegalArgumentException {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void getCertifica2Info(String currentDomainName, String user, Integer contractId,
//							AsyncCallback<Certifica2Info> callback) throws IllegalArgumentException {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void removeContractoSEPE(String currentDomainName, String currentUser,
//							EmployeeContractInfo employeeContractData, AsyncCallback<Void> callback) {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void generateCertifaca2(String currentDomainName, String user, Integer contractId,
//							Certifica2Info certifica2Info, AsyncCallback<Void> callback) {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void cambioCoef(String currentDomainName, String currentUser,
//							EmployeeContractInfo employeeContractInfo, String coef, Date fecha,
//							AsyncCallback<Void> callback) throws IllegalArgumentException {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void setEmployeeEventsByContract(String currentDomainName, Integer contractId,
//							EmployeeEventsData employeeEventsData, AsyncCallback<EmployeeEventsData> callback) {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void getContractPayements(String currentDomainName, Integer contractId,
//							AsyncCallback<ContractPaymentData> callback) {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void updateContractPayments(String currentDomainName, Integer contractId,
//							ContractPaymentData contractPaymentData, AsyncCallback<Void> callback) {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void createContractPayment(String currentDomainName, Integer contractId,
//							ContractConceptCalc contractConceptCalc, AsyncCallback<Void> callback) {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void contractExtension(String currentDomainName, ContractExtension contractExtension,
//							AsyncCallback<Void> callback) throws IllegalArgumentException {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void deleteContractExtension(String currentDomainName, Integer contractId,
//							AsyncCallback<Void> callback) throws IllegalArgumentException {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void contractTransform(String currentDomainName, ContractTransform contractTransform,
//							AsyncCallback<Void> callback) throws IllegalArgumentException {
//						// TODO Auto-generated method stub
//						
//					}
//					
//					@Override
//					public void deleteContractTransform(String currentDomainName, Integer contractId,
//							AsyncCallback<Void> callback) throws IllegalArgumentException {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void removeContractTransform(String currentDomainName, Integer contractId,
//							AsyncCallback<Void> callback) throws IllegalArgumentException {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void getEmployeeIrpf(String currentDomainName, String ssNumber, String document, Date startDate,
//							AsyncCallback<List<EmployeeIrpf>> callback) {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void setEmployeeIrpf(String currentDomainName, Integer contractId, String fullName, String document, String ssNumber,
//							List<EmployeeIrpf> employeeIrpfs, AsyncCallback<Void> callback) throws IllegalArgumentException {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void getContractVariables(String currentDomainName, Integer contractId,
//							AsyncCallback<List<ContractVariable>> callback) {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void updateContractVariables(String currentDomainName,
//							List<ContractVariable> contractVariables, AsyncCallback<Void> callback) {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void getSalariesDates(String currentDomainName, String userLogin, SalaryInfoFilter filter,
//							AsyncCallback<Period> callback) {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void getEmployeeTa(String domain, String user, Integer contractId, String situation, String regimen,
//							String ctaCti, String nss, Date fecha, AsyncCallback<String> callback)
//							throws IllegalArgumentException {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void checkAndUpdateServiAgreement(String domain, String userLogin, Integer agreementId, String ssNumber, Integer lastDateYear,
//							AsyncCallback<Date> asyncCallback) throws IllegalArgumentException {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void createContractPayment(String currentDomainName, Integer contractId,
//							List<ContractConceptCalc> contractConceptCalcList, AsyncCallback<Void> callback) {
//						// TODO Auto-generated method stub
//						
//					}
//					@Override
//					public void getSalariesOccam(String currentDomainName, String login, ITEmployee itEmployee,
//							Date startDate, Date endDate, AsyncCallback<List<Certifica2Info>> callback) {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void createContractVariable(String currentDomainName, Integer contractId,
//							ContractVariable contractVariable, AsyncCallback<Void> callback)
//							throws IllegalArgumentException {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void sendContractTransform(String currentDomainName, String currentUser,
//							EmployeeContractInfo employeeContractData, AsyncCallback<Void> callback) throws IllegalArgumentException {
//						// TODO Auto-generated method stub
//						
//					}
//					
//					@Override
//					public void sendContractExtension(String currentDomainName, String currentUser,
//							EmployeeContractInfo employeeContractData, AsyncCallback<Void> callback) throws IllegalArgumentException {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void removeContractTransform(String currentDomainName, String currentUser, String ide, Integer contractId,
//							AsyncCallback<Void> callback) throws IllegalArgumentException {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void getSepeComunicationData(String currentDomainName, String currentUser, String document,
//							Date date, Integer contractId, AsyncCallback<Map<String, String>> callback)
//							throws IllegalArgumentException {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void getEmployeeCbc(String currentDomainName, String currentUser, String document,
//							Integer contractId, Date startDate, Date endDate, String sepeIde,
//							AsyncCallback<String> callback) throws IllegalArgumentException {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void getEmployeeCbcTransform(String currentDomainName, String currentUser, String cif, String document,
//							Integer contractId, Date startDate, String sepeIde, AsyncCallback<String> callback)
//							throws IllegalArgumentException {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void getEmployeeCto(String currentDomainName, String currentUser, String document,
//							Integer contractId, Date startDate, Date endDate, String sepeIde,
//							AsyncCallback<String> callback) throws IllegalArgumentException {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void getEmployeeCtoTransform(String currentDomainName, String currentUser, String cif, String document,
//							Integer contractId, Date startDate, String sepeIde, AsyncCallback<String> callback)
//							throws IllegalArgumentException {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void sendContractoCBTransformSEPE(String currentDomainName, String currentUser,
//							EmployeeContractInfo employeeContractInfo, AsyncCallback<Void> callback)
//							throws IllegalArgumentException {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void getSepeTransformComunicationData(String currentDomainName, String currentUser,
//							String document, String enterpriseCif, Date originalStartDate, String sepeId,
//							Integer contractId, AsyncCallback<Map<String, String>> callback)
//							throws IllegalArgumentException {
//						// TODO Auto-generated method stub
//						
//					}
//					
//					@Override
//					public void getSepeExtensionComunicationData(String currentDomainName, String currentUser,
//							String document, String enterpriseCif, Date originalStartDate, String sepeId,
//							Integer contractId, AsyncCallback<Map<String, String>> callback)
//							throws IllegalArgumentException {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void getEmployeeCtoExtension(String currentDomainName, String currentUser,
//							String enterpriseCIF, String document, Integer contractId, Date extensionDate,
//							Integer extensionNum, String sepeExtensionId, AsyncCallback<String> callback)
//							throws IllegalArgumentException {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void getAgreementContext(String currentDomainName, int fxLevel, Date startDate, Date endDate,
//							AsyncCallback<ContextDescriptor> callback) throws IllegalArgumentException {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void evalAgreement(String currentDomainName, String expression, Date startDate, int fxLevel,
//							AsyncCallback<List<Result>> callback) throws IllegalArgumentException, EvalException {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void fillContractExtension(String currentDomainName, EmployeeInfo employeeData,
//							ContractInfo contractData, AsyncCallback<Void> callback) throws IllegalArgumentException {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void fillContractRelocation(String currentDomainName, Integer contractId,
//							Map<String, String> contractRelocationInfo, AsyncCallback<Void> callback)
//							throws IllegalArgumentException {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void fillBasicCopy(String currentDomainName, Integer contractId, Integer contractType,
//							String formativeLvl, AsyncCallback<Void> callback) throws IllegalArgumentException {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void cambioContrato(String currentDomainName, String currentUser,
//							EmployeeContractInfo employeeContractInfo, String tc2, String partialityCoef, Date fecha,
//							AsyncCallback<Void> callback) throws IllegalArgumentException {
//						// TODO Auto-generated method stub
//						
//					}
//
//					@Override
//					public void updateHolidayCalendar(String currentDomainName, int workplaceId, Integer holidayId, DayType[] dayTypes,
//							AsyncCallback<Void> callback) throws IllegalArgumentException {
//						// TODO Auto-generated method stub
//						
//					}
//				});
//
//		agreementDraftWidget.setAgreementDraftObject(agreementDraftObject);
//
//		// only two rows , one for headers and another one for add a new payment
//		Assert.assertEquals(2, agreementDraftWidget.paymentsTable.getRowCount());
//
//		// only two rows , one for headers and another one for add a new extra
//		Assert.assertEquals(2, agreementDraftWidget.extrasTable.getRowCount());
//
//		// only three rows , one for headers and another for add a new level
//		Assert.assertEquals(2, agreementDraftWidget.salaryTable.getRowCount());
//
//	}
//
//	public void testOverridePaymentsII() {
//
//		com.esferalia.aon.gwt.payroll.shared.AgreementDraft agreementDraft = new com.esferalia.aon.gwt.payroll.shared.AgreementDraft();
//		agreementDraft.setId(1);
//		agreementDraft.setDomain(2);
//		agreementDraft.setStartDate(DateUtils.getFirstDayOfMonth());
//		agreementDraft.setEndDate(DateUtils.getLastDayOfMonth());
//		agreementDraft.setDescription(String.valueOf(agreementDraft.getId()));
//		
//		EmployeesServiceAsync employeesService= GWT
//				.create(EmployeesService.class);
//		final AgreementDraftObject agreementDraftObject = new AgreementDraftObject(
//				6969, "TODO: Domain Name", "TODO: User Login", agreementDraft, employeesService);
//
//		AgreementDraft agreementDraftWidget = new AgreementDraft();
//		// agreementDraftWidget.setAgreementDraftObject(agreementDraftObject);
//
//		
//		/* since RPC calls are asynchronous, we will need to wait 
//		 for a response after this test method returns. This line 
//		 tells the test runner to wait up to 10 seconds 
//		 before timing out. */
//		// delayTestFinish(10000);
//
//		// assertEquals(1+5+1, agreementDraftWidget.paymentsTable.getRowCount());
//
//		// TextBox descriptionBox = (TextBox)
//		// agreementDraftWidget.paymentsTable.getWidget(2, 2);
//		// descriptionBox.setValue("OVERRIDE", true);
//
//		// Set<Payment> draftPayments =
//		// agreementDraftWidget.agreementDraftObject.getAgreementDraft().getDraftPayments();
//		// assertEquals(2, draftPayments.size());
//
//	}
//
//	// ------------------------------------------------------------------------
//
//	private static int newId() {
//		return (int) (Math.round(Math.random() * Integer.MAX_VALUE));
//	}
//
//	private static Payment newConcept(int id, int domain, String expression) {
//		return newConcept(id, domain, Payment.Type.CRA_0001, expression, "_P",
//				"_P");
//	}
//
//	private static Payment newPayment(int id, int domain, Payment concept) {
//		Payment payment = new Payment();
//
//		payment.setConceptId(concept.getId());
//		payment.setType(concept.getType());
//		payment.setName(concept.getName());
//		payment.setDescription(concept.getDescription());
//		payment.setExpression(concept.getExpression());
//		payment.setIrpfExpression(concept.getIrpfExpression());
//		payment.setQuoteExpression(concept.getQuoteExpression());
//
//		payment.setSalaryType(Salary.Type.SALARY);
//		payment.setStartDate(getFirstDayOfYear(new Date()));
//
//		return payment;
//	}
//
//
//	private static Payment newConcept(int id, int domain, Payment.Type type,
//			String expression, String irpfExpression, String quoteExpression) {
//		Payment payment = new Payment();
//		payment.setId(id);
//		payment.setType(type);
//		payment.setDomain(domain);
//		payment.setName("P0"+Integer.toString(id));
//		payment.setExpression(expression);
//		payment.setIrpfExpression(irpfExpression);
//		payment.setQuoteExpression(quoteExpression);
//		payment.setDescription(type.getDescription());
//		return payment;
//	}
//	
//	private static Set<Extra> getDbExtras(Set<Payment> payments) {
//		Set<Extra> dbExtras = new HashSet<Extra>();
//
//		for (Payment payment : payments) {
//			if ( payment.getSalaryType() != Salary.Type.EXTRA )
//				continue;
//			Extra extra = new Extra();
//			extra.setPaymentId(payment.getId());
//			extra.setDomain(payment.getDomain());
//			// d/M 
//			extra.setStartDate("01/01");
//			extra.setEndDate("31/12");
//			extra.setIssueDate("15/" + payment.getMonth()+1);
//		}
//		
//		return dbExtras;
//		
//	}
//
//	private static Set<Payment> getDbPayments(int domain) {
//		Set<Payment> dbPayments = new HashSet<Payment>();
//		
//		Payment salaryConcept = newConcept(1, 0, "/*user*/SALARIO_MENSUAL/**/* DIAS_TRABAJADOS / DIAS_MES");
//		Payment plusConcept = newConcept(2, 0, "/*user*//**/* DIAS_TRABAJADOS / DIAS_MES");
//		Payment extraConcept = newConcept(3, 0, Payment.Type.CRA_0004, null, "_P", "_P/12");
//		
//		Payment salary = newPayment(1, domain, salaryConcept);
//		dbPayments.add(salary);
//		
//		Payment plusOne = newPayment(2, domain, plusConcept);
//		plusOne.setExpression("/*user*/PLUS_ONE/**/* DIAS_TRABAJADOS / DIAS_MES");
//		dbPayments.add(plusOne);
//
//		Payment plusTwo = newPayment(3, domain, plusConcept);
//		plusTwo.setExpression("/*user*/PLUS_TWO/**/* DIAS_TRABAJADOS / DIAS_MES");
//		dbPayments.add(plusTwo);
//
//		Payment extraDecember = newPayment(4, domain, extraConcept);
//		extraDecember.setExpression("P01 + P02");
//		extraDecember.setMonth((short)11);
//		extraDecember.setSalaryType(Salary.Type.EXTRA);
//		dbPayments.add(extraDecember);
//
//		Payment extraJuly = newPayment(5, domain, extraConcept);
//		extraJuly.setExpression("P01 + P02");
//		extraJuly.setMonth((short)6);
//		extraJuly.setSalaryType(Salary.Type.EXTRA);
//		dbPayments.add(extraJuly);
//		
//		return dbPayments;
//	}
//
}
