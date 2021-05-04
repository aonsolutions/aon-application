package com.esferalia.aon.gwt.payroll.server;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.richfaces.json.JSONException;
import org.richfaces.json.JSONObject;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.payroll.jooq.JooqContrataContract;
import com.esferalia.aon.gwt.payroll.jooq.JooqMainCCC;
import com.esferalia.aon.gwt.payroll.jooq.JooqPayrollSalaries;
import com.esferalia.aon.gwt.payroll.shared.MainCCCInfo;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfo;
import com.esferalia.aon.gwt.payroll.shared.SalaryInfoFilter;
import com.esferalia.aon.gwt.payroll.util.JooqPayrollBuilder;
import com.esferalia.aon.in.payroll.excel.EnterprisePayrollExcel;
import com.esferalia.aon.in.payroll.excel.EnterprisePayrollExcel.EnterprisePayroll;
import com.esferalia.aon.in.payroll.excel.ExcelType;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.SECURITY;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.aonsolutions.AonToken;
import com.esferalia.aon.occam.api.model.security.Auth;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import net.aonsolutions.aon.api.ewok.AonApiData;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;
import solutions.aon.seg.social.toolkit.Toolkit;

@MultipartConfig
@SuppressWarnings("serial")
@WebServlet(name = "CONTRACT-SERVLET", urlPatterns = { "/ms/api/contract/*"})
public class ContractServlet extends AonApiHttpServlet {
	private static Logger LOGGER = Logger.getLogger(ContractServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)  {
		try {
			AonApiData api = initialize(req, resp);
			switch (api.getPath()) {
				case "/salary/pdf":
					responseFile(req, resp, getSalaryPdf(req, api), MimeType.PDF);
					break;
				case "/company/costs/excel":
					responseFile(req, resp, getCompanyCostsExcel(req, api), MimeType.MS_EXCEL);
					break;
				default:
					responseJson(req, resp);
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp){
		doGet(req, resp);
	}
	
	private void responseJson(HttpServletRequest req, HttpServletResponse resp) {
		try {
			AonApiData api = initialize(req, resp);
			switch (api.getPath()) {
				case "/":
					response(req, resp, getAllEmployeesInfo(api));
					break;
				case "/employee/workplace":
					response(req, resp, getAllEmployeesWorkplace(api));
					break;
				case "/employee/salaries":
					response(req, resp, getEmployeeSalaries(api));
					break;
				case "/enterprise/salaries":
					response(req, resp, getEnterpriseSalaries(api));
					break;
				case "/company/costs":
					response(req, resp, getCompanyCosts(req, api));
					break;
				case "/ccc/activity":
					response(req, resp, getCccForActivity( api));
					break;
				default:
					throw new Exception("La ruta introducida es incorrecta.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			error(req, resp, e);
		}
	}
	
	private Object getAllEmployeesInfo(AonApiData api) throws SQLException {
		LOGGER.info("[GET] EMPLOYEE INFO");
		Connection conn = AonServletUtils.getConnection(api.getDomain().getName());
		boolean allEmployees = api.getParams().optBoolean("allEmployees");  
		Gson gjson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		String jsonInString = gjson.toJson(JooqContrataContract.getEmployeesInfo(conn, api.getDomain().getId(), allEmployees));
		if(jsonInString!=null) return new JsonParser().parse(jsonInString);
		return new JSONObject();
	}
	
	private Object getAllEmployeesWorkplace(AonApiData api) throws SQLException {
		LOGGER.info("[GET] EMPLOYEE WORKPLACE");
		Connection conn = AonServletUtils.getConnection(api.getDomain().getName());
		boolean allEmployees = api.getParams().optBoolean("allEmployees");  
		Gson gjson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		Integer workplaceId =  api.getParams().optInt("workplace");  
		String jsonInString = gjson.toJson(JooqContrataContract.getEmployeesByWorkplace(conn, workplaceId, allEmployees));
		if(jsonInString!=null) return new JsonParser().parse(jsonInString);
		return new JSONObject();
	}
	
	private Object getEmployeeSalaries(AonApiData api) throws SQLException {
		LOGGER.info("[GET]  EMPLOYEE SALARIES");
		AonToken aonToken = SECURITY.getAonToken(api.getToken());
		Auth auth = AON_SOLUTIONS.getAuth(aonToken.getSchemaFirstDomain(), 0, aonToken.getAuth());
		
		String document = auth.getDocument(); 
		if(document==null) {
			document = AON.getRegistry(api.getDomain().getName(), api.getDomain().getId(), "", f->f.getIdProperty().eq(api.getUser().getRegistry())).getDocument();
		}
		
		Connection conn = AonServletUtils.getConnection(api.getDomain().getName());
		SalaryInfoFilter filter = getFilter(api);
		filter.setWorkplaceId(api.getDomain().getId());
		Gson gjson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		String jsonInString = gjson.toJson(JooqPayrollSalaries.getSalariesByDocument(conn, filter, document));
		if(jsonInString!=null) return new JsonParser().parse(jsonInString);
		return new JSONObject();
	}
	
	private Object getEnterpriseSalaries(AonApiData api) throws SQLException {
		LOGGER.info("[GET] ENTERPRISE SALARIES");
		Connection conn = AonServletUtils.getConnection(api.getDomain().getName());
		Company company = AON.getCompany(api.getDomain().getName(), api.getDomain().getId(), "", f->f.getDomainProperty().eq(api.getDomain().getId()));
		Gson gjson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		String jsonInString = gjson.toJson(getSalaries(api, conn, Optional.ofNullable(company.getId())));
		if(jsonInString!=null) return new JsonParser().parse(jsonInString);
		return new JSONObject();
	}
	
	private Object getCompanyCosts(HttpServletRequest req, AonApiData api) throws SQLException, IOException, JSONException{
		LOGGER.info("[GET] COMPANY COSTS");

		Gson gjson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		Company company = AON.getCompany(api.getDomain().getName(), api.getDomain().getId(), "", f->f.getDomainProperty().eq(api.getDomain().getId()));
		AONContext ctx = AONContext.getAONContext(api.getDomain().getName(), api.getDomain().getId(), "");
		Date endDate = new Date();
		Date startDate = new Date();
	
		if(api.getParams().optString("endDate").isEmpty()) {
			endDate = getEndDateSalary(api, Optional.ofNullable(company.getId()));
			startDate = AonDateUtils.getMonthFirstDay(endDate);
			endDate = AonDateUtils.getMonthLastDay(endDate);
		} else {
		    startDate = Toolkit.parseDate(api.getParams().optString("startDate"), "yyyy-MM-dd");
			endDate  = Toolkit.parseDate(api.getParams().optString("endDate"), "yyyy-MM-dd");
		}
		String [] startDateArray = Toolkit.dateString(startDate);
		String startDateStr =  startDateArray[2]+"-"+startDateArray[1]+"-"+startDateArray[0];
		String [] endDateArray = Toolkit.dateString(endDate);
		String endDateStr =  endDateArray[2]+"-"+endDateArray[1]+"-"+endDateArray[0];
		Integer workplaceId = 0;
		if(!api.getParams().optString("workplace").isEmpty()) workplaceId = api.getParams().optInt("workplace");
		
		List<EnterprisePayroll> costs = EnterprisePayrollExcel.getEnterprisePayrolls(ctx, startDate, endDate, company.getId(), workplaceId).collect(Collectors.toList());
		List<Object> list = new ArrayList<>();

		for(EnterprisePayroll cost: costs) {
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
			list.add( new JsonParser().parse(json.toString()));
		}
	
		String jsonInString = gjson.toJson(list);
		if(jsonInString!=null) return new JsonParser().parse(jsonInString);
		return new JSONObject();
	}
	
	private List<SalaryInfo> getSalaries(AonApiData api, Connection conn, Optional<Integer> companyId) {
		SalaryInfoFilter filter = getFilter(api);
		if(!api.getParams().optString("employee").isEmpty()) filter.setEmployeeId(api.getParams().optInt("employee")); //employee == contractId
		else if(!api.getParams().optString("workplace").isEmpty()) filter.setWorkplaceId(api.getParams().optInt("workplace"));
		else if(!companyId.isEmpty()) filter.setEnterpriseId(companyId.get().intValue());
		return JooqPayrollSalaries.getSalaries(conn, filter);
	}
	
	private File getSalaryPdf(HttpServletRequest req, AonApiData api) throws JSONException, IOException {
		LOGGER.info("[GET] SALARY PDF");

		Integer salaryId = api.getParams().optInt("salaryId");
	
		File file = File.createTempFile("nomina", "");
		JooqPayrollBuilder.generatePayroll(api.getDomain().getName(), new FileOutputStream(file), salaryId);
		return file;
	}

	private File getCompanyCostsExcel(HttpServletRequest req, AonApiData api) throws JSONException, IOException {
		LOGGER.info("[GET] COMPANY COSTS EXCEL");
		Company company = AON.getCompany(api.getDomain().getName(), api.getDomain().getId(), "", f->f.getDomainProperty().eq(api.getDomain().getId()));
		ExcelType excelType = ExcelType.COMPLETE;
		String excelParams = api.getParams().optString("excelType");
		if(excelParams.equalsIgnoreCase("SUMMARY")) excelType = ExcelType.SUMMARY;
		
		File file = File.createTempFile("companyCosts", "");
	
		Date startDate =Toolkit.parseDate(api.getParams().optString("startDate"), "yyyy-MM-dd");
		Integer workplaceId = 0;
		if(!api.getParams().optString("workplace").isEmpty()) workplaceId = api.getParams().optInt("workplace");

		if(!api.getParams().optString("endDate").isEmpty()) {
			Date endDate = Toolkit.parseDate(api.getParams().optString("endDate"), "yyyy-MM-dd");
			EnterprisePayrollExcel.simpleEnterprisePayrollGenerator(
					api.getDomain().getName(), new FileOutputStream(file), Optional.of(company.getId()), Optional.of(workplaceId), 
					startDate, endDate, excelType);
		} else {
			EnterprisePayrollExcel.simpleEnterprisePayrollGenerator(
					api.getDomain().getName(), new FileOutputStream(file), Optional.of(company.getId()), Optional.of(workplaceId), 
					startDate, excelType);
		}
	
		return file;
	}
	
	private Date getEndDateSalary(AonApiData api, Optional<Integer> companyId) throws SQLException {
		Connection conn = AonServletUtils.getConnection(api.getDomain().getName());
		SalaryInfoFilter filter = getFilter(api);
		if(!companyId.isEmpty()) filter.setEnterpriseId(companyId.get().intValue());
		SalaryInfo salaryInfo = JooqPayrollSalaries.getSalariesDateEnd(conn, filter);
		Date date = salaryInfo.getEndDate();
		if(date == null) date = new Date();
		return date;
	}
	
	private Object getCccForActivity(AonApiData api) throws SQLException {
		Connection conn = AonServletUtils.getConnection(api.getDomain().getName());
		MainCCCInfo mainCccInfo = JooqMainCCC.getMainCCCInfo(conn, api.getDomain().getId(), api.getUser().getId());

		Gson gjson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		String jsonInString = gjson.toJson(mainCccInfo);
		if(jsonInString!=null) return new JsonParser().parse(jsonInString);
		return new JSONObject();
	}
	
	private SalaryInfoFilter getFilter(AonApiData api) {
		SalaryInfoFilter filter = new SalaryInfoFilter();
		
		if(!api.getParams().optString("startDate").isEmpty()) {
			filter.setDateTillT(Toolkit.parseDate(api.getParams().optString("startDate"), "yyyy-MM-dd"));
		}
		if(!api.getParams().optString("endDate").isEmpty()) {
			filter.setDateTTo(Toolkit.parseDate(api.getParams().optString("endDate"), "yyyy-MM-dd")); 
		} 
		if(!api.getParams().optString("salaryType").isEmpty()) filter.setSalaryType(api.getParams().optInt("salaryType"));
	
		return filter;
	}

}
