package com.esferalia.aon.gwt.payroll.server;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.code.aon.company.enumeration.SalaryTemplate;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.payroll.jooq.JooqAgreement;
import com.esferalia.aon.gwt.payroll.jooq.JooqContrataContract;
import com.esferalia.aon.gwt.payroll.jooq.JooqMainCCC;
import com.esferalia.aon.gwt.payroll.jooq.JooqPayrollSalaries;
import com.esferalia.aon.gwt.payroll.shared.Agreement;
import com.esferalia.aon.gwt.payroll.shared.CCCInfo;
import com.esferalia.aon.gwt.payroll.shared.MainCCCInfo;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfo;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfoFilter;
import com.esferalia.aon.in.payroll.excel.EnterprisePayrollExcel;
import com.esferalia.aon.in.payroll.excel.EnterprisePayrollExcel.EnterprisePayrollExcelParams;
import com.esferalia.aon.in.payroll.excel.ExcelType;
import com.esferalia.aon.in.payroll.pdf.jooq.JooqPayrollBuilder;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.PAYROLL;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.json.ContractDataJSON;
import com.esferalia.aon.occam.api.json.EmployeeJSON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Filter;
import com.esferalia.aon.occam.api.model.IJsonNames;
import com.esferalia.aon.occam.api.model.Person;
import com.esferalia.aon.occam.api.model.Properties.EmployeeProperties;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.payroll.Contract;
import com.esferalia.aon.occam.api.model.payroll.ContractData;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonNumberUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;
import solutions.aon.seg.social.toolkit.Toolkit;

@MultipartConfig
@SuppressWarnings("serial")
@WebServlet(name = "CONTRACT-SERVLET", urlPatterns = { "/ms/api/contract/*"})
public class ContractServlet extends AonApiHttpServlet {
	private static Logger LOGGER = Logger.getLogger(ContractServlet.class.getName());
	private static final String FORMAT_DATE = "yyyy-MM-dd"; 
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)  {
		try {
			AonApiData api = initialize(req);
			switch (api.getPath()) {
				case "/salary/pdf":
					responseFile(resp, getSalaryPdf(api), MimeType.PDF);
					break;
				case "/company/costs/excel":
					responseFile(resp, getCompanyCostsExcel(api), MimeType.MS_EXCEL);
					break;
				default:
					responseJson(req, resp, api);
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp){
		doGet(req, resp);
	}
	
	private void responseJson(HttpServletRequest req, HttpServletResponse resp, AonApiData api) {
		try {
			switch (api.getPath()) {
				case "/":
					response(req, resp, getAllEmployeesInfo(api));
					break;
				case "/add":
					response(req, resp, addContract(api));
					break;
				case "/employee/workplace":
					response(req, resp, getAllEmployeesWorkplace(api));
					break;
				case "/agreements":
					response(req, resp, getAgreements(api));
					break;
				case "/employee/salaries":
					response(req, resp, getEmployeeSalaries(api));
					break;
				case "/enterprise/salaries":
					response(req, resp, getEnterpriseSalaries(api));
					break;
				case "/company/costs":
					response(req, resp, getCompanyCosts(api));
					break;
				case "/ccc/activity":
					response(req, resp, getCccForActivity(api));
					break;
				case "/save/vacation":
					response(req, resp, saveVacation(api));
					break;
				default:
					throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
		} catch (Exception e) {
			e.printStackTrace();
			error(req, resp, e);
		}
	}
	
	private JSONArray getAllEmployeesInfo(AonApiData api) throws Exception {
		LOGGER.info("[GET] EMPLOYEE INFO");
		JSONArray arr = new JSONArray();
		PAYROLL.getEmployees(api.getDomain().getName(), api.getDomain().getId(), api.getUser().getLogin(), f->filterEmployees(api, f))
		.forEach(employee-> arr.put( EmployeeJSON.toJSON(employee) ) );

		return arr;
	}
	
	private JSONArray getAllEmployeesWorkplace(AonApiData api) throws Exception {
		LOGGER.info("[GET] EMPLOYEE WORKPLACE");
		JSONArray arr = new JSONArray();
		JSONObject params = api.getData();
		
		try(Connection conn = AonServletUtils.getConnection(api.getDomain().getName())){
			boolean allEmployees = params.optBoolean("allEmployees");  
			Integer workplaceId = params.optInt("workplace");  
			JooqContrataContract.getEmployeesByWorkplace(conn, workplaceId, allEmployees).forEach(em->{
				JSONObject json = new JSONObject()
				.put("contractId", em.getContractId())
				.put("document", em.getDocument())
				.put("documentType", em.getDocumentType().intValue())
				.put("domain", em.getDomain())
				.put("employeeId", em.getEmployeeId())
				.put("name", em.getName())
				.put("nationality",  em.getNationalityCode())
				.put("payMethodTypeB", em.getPayMethodTypeB())
				.put("secondSurName", em.getSecondSurName())
				.put("ssNumber", em.getSsNumber())
				.put("surName", em.getSurName());
				arr.put(json);
			});
		}
		return arr;
	}

	private JSONArray getAgreements(AonApiData api) throws Exception {
		LOGGER.info("[GET] AGREEMENTS");
		String domainName = api.getDomain().getName();
		JSONArray arr = new JSONArray();
		try(Connection connection = AonServletUtils.getConnection(domainName)){
			Integer domainID = AonServletUtils.getDomainID(domainName);
			Integer parentDomainID = AonServletUtils.getParentDomainID(domainName);
			List<Agreement> agreements = JooqAgreement.getAgreements(connection, 0, Integer.MAX_VALUE,domainID, parentDomainID);
			for(Agreement agreement: agreements) {
				if(agreement.getId()>0 && agreement.getSSNumber()!=null) {
					JSONObject json = new JSONObject();
					json.put("description", agreement.getDescription());
					json.put("ssNumber", agreement.getSSNumber());
					arr.put(json);
				}
			}
		}
		return arr;
	}
	
	private JSONArray getEmployeeSalaries(AonApiData api) throws Exception {
		LOGGER.info("[GET]  EMPLOYEE SALARIES");
		AonToken aonToken = SECURITY.getAonToken(api.getToken());
		Auth auth = AON_SOLUTIONS.getAuth(aonToken.getSchemaFirstDomain(), 0, aonToken.getAuth());
		JSONArray arr = new JSONArray();
		String document = auth.getDocument(); 
		if(document==null) {
			document = AON.getRegistry(api.getDomain().getName(), api.getDomain().getId(), "", f->f.getIdProperty().eq(api.getUser().getRegistry().getId())).getDocument();
		}
			
		try(Connection conn = AonServletUtils.getConnection(api.getDomain().getName())){
			SalaryInfoFilter filter = getFilter(api.getData());
			filter.setWorkplaceId(api.getDomain().getId());
			JooqPayrollSalaries.getSalariesByDocument(conn, filter, document).stream()
			.sorted(Comparator.comparing(SalaryInfo::getStartDate))
			.forEach(lt -> arr.put(toJSONSalaryInfo(lt)) );
		} 

		return arr;
	}
	
	private JSONArray getEnterpriseSalaries(AonApiData api) throws Exception {
		LOGGER.info("[GET] ENTERPRISE SALARIES");
		JSONArray arr = new JSONArray();
		try(Connection conn = AonServletUtils.getConnection(api.getDomain().getName())){
			Company company = AON.getCompany(api.getDomain().getName(), api.getDomain().getId(), "", f->f.getDomainProperty().eq(api.getDomain().getId()));
			
			getSalaries(api, conn, Optional.ofNullable(company.getId())).stream()
			.forEach(lt -> arr.put(toJSONSalaryInfo(lt)) );
		}
		return arr;
	}
	
	private JSONArray getCompanyCosts(AonApiData api) throws Exception{
		LOGGER.info("[GET] COMPANY COSTS");
		JSONObject params = api.getData();
		JSONArray arr = new JSONArray();
		Company company = AON.getCompany(api.getDomain().getName(), api.getDomain().getId(), "", f->f.getDomainProperty().eq(api.getDomain().getId()));
		
		try ( CloseableAONContext ctx = AONContext.getAONContext(api.getDomain().getName(), api.getDomain().getId(), "") ) {
		
			Date endDate = new Date();
			Date startDate = new Date();
			Integer workplaceId = 0;
			
			if(!params.optString("workplace").isEmpty()) {
				workplaceId = params.optInt("workplace");
			}
				
			if(params.optString("endDate").isEmpty()) {
				endDate = getEndDateSalary(api, Optional.ofNullable(company.getId()));
				startDate = AonDateUtils.getMonthFirstDay(endDate);
				endDate = AonDateUtils.getMonthLastDay(endDate);
			} else {
			    startDate = Toolkit.parseDate(params.optString("startDate"), FORMAT_DATE);
				endDate  = Toolkit.parseDate(params.optString("endDate"), FORMAT_DATE);
			}
			String [] startDateArray = Toolkit.dateString(startDate);
			String startDateStr =  startDateArray[2]+"-"+startDateArray[1]+"-"+startDateArray[0];
			String [] endDateArray = Toolkit.dateString(endDate);
			String endDateStr =  endDateArray[2]+"-"+endDateArray[1]+"-"+endDateArray[0];
			
			EnterprisePayrollExcel.getEnterprisePayrolls(ctx, startDate, endDate, company.getId(), workplaceId).forEach(cost->{
				JSONObject json = new JSONObject();
				json.put("startDate", startDateStr);
				json.put("endDate", endDateStr);
				json.put("advancedPayment", cost.getAdvancedPayment());
				json.put("bonuses", cost.getBonuses());
				json.put("cgc", cost.getCgc());
				json.put("cgcBase", cost.getCgcBase());
				json.put("cgp", cost.getCgp());
				json.put("cgpEnterprise", cost.getCgpEnterprise());
				json.put("embargos", cost.getEmbargos());
				json.put("employee", cost.getEmployee());
				json.put("employeeSS", cost.getEmployeeSS());
				json.put("enterpriseSS", cost.getEnterpriseSS());
				json.put("estruc", cost.getEstruc());
				json.put("estrucEnterprise", cost.getEstrucEnterprise());
				json.put("fogasaEnterprise", cost.getFogasaEnterprise());
				json.put("irpf", cost.getIrpf());
				json.put("irpfBase", cost.getIrpfBase());
				json.put("jobTraining", cost.getJobTraining());
				json.put("jobTrainingEnterprise", cost.getJobTrainingEnterprise());
				json.put("liquid", cost.getLiquid());
				json.put("noEstruct", cost.getNoEstruct());
				json.put("noEstructEnterprise", cost.getNoEstructEnterprise());
				json.put("otherDeductions", cost.getOtherDeductions());
				json.put("raw", cost.getRaw());
				json.put("salaryType", cost.getSalaryType());
				json.put("totalCost", cost.getTotalCost());
				json.put("unemployment", cost.getUnemployment());
				json.put("unemploymentEnterprise", cost.getUnemploymentEnterprise());
				json.put("workplace", cost.getWorkplace());
				arr.put(json);
			});
		
			return arr;
		}
	}
	
	private List<SalaryInfo> getSalaries(AonApiData api, Connection conn, Optional<Integer> companyId) {
		JSONObject params = api.getData();
		SalaryInfoFilter filter = getFilter(params);
		
		if(!params.optString("employee").isEmpty()) {
			filter.setEmployeeId(params.optInt("employee")); //employee == contractId
		} else if(!params.optString("workplace").isEmpty()) {
			filter.setWorkplaceId(params.optInt("workplace"));
		} else if(companyId.isPresent()) {
			filter.setEnterpriseId(companyId.get().intValue());
		}

		return JooqPayrollSalaries.getSalaries(conn, api.getDomain().getId(), filter);
	}
	
	private File getSalaryPdf(AonApiData api) throws Exception {
		int entepriseId = api.getData().optInt("enterpriseId");
		LOGGER.info("[GET] SALARY PDF");
		JSONObject params = api.getData();
		Integer salaryId = params.optInt("salaryId");
		Integer enterpriseId = params.optInt("enterpriseId");
//		String salaryType = params.optString("type");
		String salaryReport = null;
		try {			
			salaryReport = PayrollServletUtils.getSalaryReport(api.getDomain().getName(), enterpriseId, SalaryType.SALARY);
		} catch (Exception e) {
		}
		
		File file = File.createTempFile("nomina", "");
		if (AonStringUtils.equalsIgnoreCase(salaryReport, SalaryTemplate.AON_SOLUTIONS_DEFAULT.getValue())) {
			JooqPayrollBuilder.generateClassicPayroll(entepriseId > 0 ? entepriseId : null, api.getDomain().getName(), new FileOutputStream(file), Optional.empty(), salaryId);
		} else {
			JooqPayrollBuilder.generatePayroll(entepriseId > 0 ? entepriseId : null, api.getDomain().getName(), new FileOutputStream(file), Optional.empty(), salaryId);			
		}
		
		return file;
	}

	private File getCompanyCostsExcel(AonApiData api) throws Exception {
		LOGGER.info("[GET] COMPANY COSTS EXCEL");
		JSONObject params = api.getData();
		Company company = AON.getCompany(api.getDomain().getName(), api.getDomain().getId(), "", f->f.getDomainProperty().eq(api.getDomain().getId()));
		ExcelType excelType = ExcelType.COMPLETE;
		String excelParams = params.optString("excelType");
		if(excelParams.equalsIgnoreCase("SUMMARY")) excelType = ExcelType.SUMMARY;
		
		File file = File.createTempFile("companyCosts", "");
	
		Date startDate =Toolkit.parseDate(params.optString("startDate"), FORMAT_DATE);
		Integer workplaceId = 0;
		if(!params.optString("workplace").isEmpty())
			workplaceId =params.optInt("workplace");

		EnterprisePayrollExcelParams enterpriseExcelParams = new EnterprisePayrollExcelParams()
				.setDomainName(api.getDomain().getName())
				.setLogin(api.getUser().getLogin())
				.setOs(new FileOutputStream(file))
				.setEnterpriseId(company.getId())
				.setWorkplaceId(workplaceId)
				.setExcelType(excelType);
		if(!params.optString("endDate").isEmpty()) {
			Date endDate = Toolkit.parseDate(params.optString("endDate"), FORMAT_DATE);
			
			
			EnterprisePayrollExcel.simpleEnterprisePayrollGenerator(enterpriseExcelParams, startDate, endDate);
		} else if (!params.optString("startDate").isEmpty()) {
			EnterprisePayrollExcel.simpleEnterprisePayrollGenerator(enterpriseExcelParams, startDate);
		}
	
		return file;
	}
	
	private Date getEndDateSalary(AonApiData api, Optional<Integer> companyId) throws Exception {
		Date date = null;
		try(Connection conn = AonServletUtils.getConnection(api.getDomain().getName())){
			SalaryInfoFilter filter = getFilter(api.getData());
			if(companyId.isPresent()) filter.setEnterpriseId(companyId.get().intValue());
			SalaryInfo salaryInfo = JooqPayrollSalaries.getSalariesDateEnd(conn,  api.getDomain().getId(), filter);
			date = salaryInfo.getEndDate();
			if(date == null) date = new Date();
		}

		return date;
	}
	
	private JSONObject getCccForActivity(AonApiData api) throws Exception {
		JSONObject json = new JSONObject();
		try(Connection conn = AonServletUtils.getConnection(api.getDomain().getName())){
			MainCCCInfo mainCccInfo = JooqMainCCC.getMainCCCInfo(conn, api.getDomain().getId(), api.getUser().getId());
			
			JSONObject activities = new JSONObject();			
			mainCccInfo.getActivities().entrySet().stream().forEach(a-> activities.put(a.getKey()+"", a.getValue()) );
			json.put("activities", activities);
			
			JSONObject cccs = new JSONObject();
			mainCccInfo.getCccs().entrySet().stream().forEach(a-> {
				CCCInfo ccc = a.getValue();
				
				JSONObject cccJson = new JSONObject()
				.put("activityId", ccc.getActivityId())
				.put("ccc", ccc.getCcc())
				.put("cccAccount", ccc.getCccAccount())
				.put("cccId", ccc.getCccId())
				.put("cccRegimeCode", ccc.getCccRegimeCode())
				.put("geozone", ccc.getGeozone())
				.put("geozoneCode", ccc.getGeozoneCode())
				.put("type", ccc.getType().intValue())
				.put("useByContracts", ccc.isUseByContracts())
				;
				cccs.put(a.getKey().toString(), cccJson); 
			});
			
			json.put("cccs", cccs);
		}

		return json;
	}
	
	private SalaryInfoFilter getFilter(JSONObject params) {
		SalaryInfoFilter filter = new SalaryInfoFilter();
		
		if(!params.optString("startDate").isEmpty()) {
			filter.setDateTillT(Toolkit.parseDate(params.optString("startDate"), FORMAT_DATE));
		}
		
		if(!params.optString("endDate").isEmpty()) {
			filter.setDateTTo(Toolkit.parseDate(params.optString("endDate"), FORMAT_DATE)); 
		} 
		
		if(!params.optString("salaryType").isEmpty()) {
			filter.setSalaryTypes(new ArrayList<>(params.optInt("salaryType")));
		}
	
		return filter;
	}

	private static JSONObject toJSONSalaryInfo(SalaryInfo salaryInfo) {
		return new JSONObject().put("contract", salaryInfo.getContract())
			.put("domain", salaryInfo.getDomain())
			.put("employeeName", salaryInfo.getEmployeeName())
			.put("enterpriseId",salaryInfo.getEnterpriseId())
			.put("enterpriseName", salaryInfo.getEnterpriseName())
			.put("id", salaryInfo.getId())
			.put("totalDeduction", salaryInfo.getTotalDecuction())
			.put("totalLiquid", salaryInfo.getTotalLiquid())
			.put("totalPayment", salaryInfo.getTotalPayment())
			.put("type", salaryInfo.getType())
			.put("workplaceId", salaryInfo.getWorkplaceId())
			.put("workplaceName", salaryInfo.getWorkplaceName())
			.put("startDate",  AonDateUtils.format( salaryInfo.getStartDate(), FORMAT_DATE))
	        .put("endDate",  AonDateUtils.format( salaryInfo.getEndDate(), FORMAT_DATE));
	}

	private JSONObject addContract(AonApiData api) throws Exception{
		JSONObject params = api.getData();
		
		Domain domain = api.getDomain();

		String doc = params.optString("ipf");
		String nss = params.optString("nss");
		Date fra = Toolkit.parseDate(params.optString("fra"), FORMAT_DATE);

		java.sql.Date fraSql = new java.sql.Date(fra.getTime());      
		//----------PERSON
		Optional<Contract> contract = Optional.empty();
		Person person = AON.getPerson(domain, "", f->f.getDomainProperty().eq(domain.getId()).and(f.getSocialSecurityNumProperty().eq(nss)));
		if(null!= person.getDocument()) {
		    contract = PAYROLL.getContract(domain.getName(), domain.getId(), "",
			f->f.getDomainProperty().eq(domain.getId())
			.and(f.getPersonProperty().eq(person.getId()))
			.and(f.getEndDateProperty().isNull().or(f.getEndDateProperty().ge(fraSql))));
		}

	    if(contract.isPresent()) { // CONTRACT NO EXIST
			com.esferalia.aon.occam.api.model.payroll.Employee employee = new com.esferalia.aon.occam.api.model.payroll.Employee();
			
			employee.setRegime(params.optString("regime"));
			employee.setCcc(params.optString("ctaCti"));
			employee.setDni(doc);
			employee.setName(params.optString("name"));
			employee.setNaf(nss);
			employee.setStartDate(fra);
			
			if(!params.optString("frb").isEmpty())
				employee.setEndDate(Toolkit.parseDate(params.optString("frb"), FORMAT_DATE));
			
			if(!params.optString("coef").isEmpty())
				employee.setFactor(params.optDouble("coef") / 1000);
			
			employee.setContractType(params.optString("contract"));
			employee.setQuoteGroup(params.optString("gc"));
			
			if(!params.optString("ocup").isEmpty())
				employee.setOccupation(params.optString("ocup"));
			
			if(!params.optString("rlce").isEmpty())
				employee.setRlce(params.optString("rlce"));
			
			PAYROLL.addEmployee(domain.getName(), domain.getId(), "", employee);
	    } else {
	    	throw new AonApiException("Ya existe un contrato activo.");
	    }
		return new JSONObject();
	}
	
	private JSONArray saveVacation(AonApiData api) throws Exception{
		JSONObject params = api.getData();
		Domain domain = api.getDomain();
		Integer registry = params.optInt(IJsonNames.REGISTRY);
		org.json.JSONArray dates = params.optJSONArray("dates");
		LinkedList <ContractData> contractDataArr = new LinkedList<>();
		if(dates!=null && dates.length()>0) {
	        java.sql.Date now=new java.sql.Date(Calendar.getInstance().getTime().getTime());  
			Optional<Contract> contract = PAYROLL.getContract(domain.getName(), domain.getId(), api.getUser().getLogin(),
					f->f.getDomainProperty().eq(domain.getId())
					.and(f.getPersonProperty().eq(registry))
					.and(f.getEndDateProperty().isNull().or(f.getEndDateProperty().ge(now))));
			
			if(contract.isPresent()) {
				for (int i = 0; i < dates.length(); i++) {
					org.json.JSONObject json = dates.getJSONObject(i);
					if(!json.optString("startDate").isEmpty() && !json.optString("endDate").isEmpty()) {
						ContractData cData = new ContractData();
						cData.setContract(contract.get().getId())
						.setDomain(domain.getId())
						.setName("DIAS_VACACIONES")
						.setStartDate(Toolkit.parseDate(json.optString("startDate"), FORMAT_DATE))
						.setEndDate(Toolkit.parseDate(json.optString("endDate"), FORMAT_DATE));
						
						long days = AonDateUtils.getDaysBetweenDates(cData.getStartDate(), cData.getEndDate());
						cData.setExpression(days+"");	
						contractDataArr.add(cData);
					}
				}
				if(!contractDataArr.isEmpty()) {
					return ContractDataJSON.toJSON(
							PAYROLL.saveContractData(domain.getName(), domain.getId(), api.getUser().getLogin(), 
							contractDataArr.toArray(new ContractData[contractDataArr.size()]))
					);
				}
			}
		}
		return new JSONArray();
	}
	
	private static Filter filterEmployees(AonApiData api, EmployeeProperties f) {
		JSONObject params = api.getData();
		Boolean contractAll = params.optBoolean("contractAll");
		Date startDate = params.optString("startDate").isEmpty() ? new Date() : Toolkit.parseDate(params.optString("startDate"), FORMAT_DATE);
		Date endDate =  null;
		Filter filter = f.getDomainProperty().eq(api.getDomain().getId());
		
		if(Boolean.TRUE.equals(contractAll)) {	
			
		} else if(params.optString("endDate").isEmpty()) {
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.DAY_OF_MONTH, 1);
			cal.add(Calendar.MONTH, -1);
			endDate = new Date(cal.getTimeInMillis());
			filter = filter.and( f.getEndDateProperty().isNull().or(f.getEndDateProperty().ge( new java.sql.Date(endDate.getTime()) ) ) );
		} else {
			endDate = Toolkit.parseDate(params.optString("endDate"), FORMAT_DATE);
			filter = filter.and( f.getStartDateProperty().eq( new java.sql.Date(startDate.getTime()) ).and(f.getEndDateProperty().eq( new java.sql.Date(endDate.getTime()) ) )  );
		}
		return filter;
	}
}