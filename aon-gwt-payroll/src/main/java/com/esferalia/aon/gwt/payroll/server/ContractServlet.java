package com.esferalia.aon.gwt.payroll.server;

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
import com.esferalia.aon.gwt.payroll.jooq.JooqPayrollSalaries;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;
import solutions.aon.seg.social.toolkit.Toolkit;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

@MultipartConfig
@SuppressWarnings("serial")
@WebServlet(name = "CONTRACT-SERVLET", urlPatterns = { "/ms/api/contract/*"})
public class ContractServlet extends AonApiHttpServlet {
	private static Logger LOGGER = Logger.getLogger(ContractServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)  {
		super.doGet(req, resp);
		try {
			switch (getPath()) {
				case "/salary/pdf":
					responseFile(req, resp, getSalaryPdf(req), MimeType.PDF);
					break;
				case "/company/costs/excel":
					responseFile(req, resp, getCompanyCostsExcel(req), MimeType.MS_EXCEL);
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
			switch (getPath()) {
				case "/":
					response(req, resp, getAllEmployeesInfo());
					break;
				case "/employee/workplace":
					response(req, resp, getAllEmployeesWorkplace());
					break;
				case "/employee/salaries":
					response(req, resp, getEmployeeSalaries());
					break;
				case "/enterprise/salaries":
					response(req, resp, getEnterpriseSalaries());
					break;
				case "/company/costs":
					response(req, resp, getCompanyCosts(req));
					break;
				default:
					throw new Exception("La ruta introducida es incorrecta.");
			}
		} catch (Exception e) {
			e.printStackTrace();
			error(req, resp, e);
		}
	}
	
	private Object getAllEmployeesInfo() throws SQLException {
		LOGGER.info("[GET] EMPLOYEE INFO");
		Connection conn = AonServletUtils.getConnection(getDomain().getName());
		boolean allEmployees = getParams().optBoolean("allEmployees");  
		Gson gjson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		String jsonInString = gjson.toJson(JooqContrataContract.getEmployeesInfo(conn, getDomain().getId(), allEmployees));
		if(jsonInString!=null) return new JsonParser().parse(jsonInString);
		return new JSONObject();
	}
	
	private Object getAllEmployeesWorkplace() throws SQLException {
		LOGGER.info("[GET] EMPLOYEE WORKPLACE");
		Connection conn = AonServletUtils.getConnection(getDomain().getName());
		boolean allEmployees = getParams().optBoolean("allEmployees");  
		Gson gjson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		Integer workplaceId =  getParams().optInt("workplace");  
		String jsonInString = gjson.toJson(JooqContrataContract.getEmployeesByWorkplace(conn, workplaceId, allEmployees));
		if(jsonInString!=null) return new JsonParser().parse(jsonInString);
		return new JSONObject();
	}
	
	private Object getEmployeeSalaries() throws SQLException {
		LOGGER.info("[GET]  EMPLOYEE SALARIES");
		AonToken aonToken = SECURITY.getAonToken(getToken());
		Auth auth = AON_SOLUTIONS.getAuth(aonToken.getSchemaFirstDomain(), 0, aonToken.getAuth());
		
		String document = auth.getDocument(); 
		if(document==null) {
			document = AON.getRegistry(getDomain().getName(), getDomain().getId(), "", f->f.getIdProperty().eq(getUser().getRegistry())).getDocument();
		}
		
		Connection conn = AonServletUtils.getConnection(getDomain().getName());
		SalaryInfoFilter filter = getFilter();
		filter.setWorkplaceId(getDomain().getId());
		Gson gjson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		String jsonInString = gjson.toJson(JooqPayrollSalaries.getSalariesByDocument(conn, filter, document));
		if(jsonInString!=null) return new JsonParser().parse(jsonInString);
		return new JSONObject();
	}
	
	private Object getEnterpriseSalaries() throws SQLException {
		LOGGER.info("[GET] ENTERPRISE SALARIES");
		Connection conn = AonServletUtils.getConnection(getDomain().getName());
		Company company = AON.getCompany(getDomain().getName(), getDomain().getId(), "", f->f.getDomainProperty().eq(getDomain().getId()));
		Gson gjson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		String jsonInString = gjson.toJson(getSalaries(conn, Optional.ofNullable(company.getId())));
		if(jsonInString!=null) return new JsonParser().parse(jsonInString);
		return new JSONObject();
	}
	
	private Object getCompanyCosts(HttpServletRequest req) throws SQLException, IOException, JSONException{
		LOGGER.info("[GET] COMPANY COSTS");

		Gson gjson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		Company company = AON.getCompany(getDomain().getName(), getDomain().getId(), "", f->f.getDomainProperty().eq(getDomain().getId()));
		AONContext ctx = AONContext.getAONContext(getDomain().getName(), getDomain().getId(), "");
		Date endDate = new Date();
		Date startDate = new Date();
	
		if(getParams().optString("endDate").isEmpty()) {
			endDate = getEndDateSalary(Optional.ofNullable(company.getId()));
			startDate = endDate;
		} else {
		    startDate = Toolkit.parseDate(getParams().optString("startDate"), "yyyy-MM-dd");
			endDate  = Toolkit.parseDate(getParams().optString("endDate"), "yyyy-MM-dd");
		}
		String [] startDateArray = Toolkit.dateString(startDate);
		String startDateStr =  startDateArray[2]+"-"+startDateArray[1]+"-"+startDateArray[0];
		String [] endDateArray = Toolkit.dateString(endDate);
		String endDateStr =  endDateArray[2]+"-"+endDateArray[1]+"-"+endDateArray[0];
		Integer workplaceId = 0;
		if(!getParams().optString("workplace").isEmpty()) workplaceId = getParams().optInt("workplace");
		
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
	
	private List<SalaryInfo> getSalaries(Connection conn, Optional<Integer> companyId) {
		SalaryInfoFilter filter = getFilter();
		if(!getParams().optString("employee").isEmpty()) filter.setEmployeeId(getParams().optInt("employee")); //employee == contractId
		else if(!getParams().optString("workplace").isEmpty()) filter.setWorkplaceId(getParams().optInt("workplace"));
		else if(!companyId.isEmpty()) filter.setEnterpriseId(companyId.get().intValue());
		return JooqPayrollSalaries.getSalaries(conn, filter);
	}
	
	private File getSalaryPdf(HttpServletRequest req) throws JSONException, IOException {
		LOGGER.info("[GET] SALARY PDF");

		Integer salaryId = getParams().optInt("salaryId");
	
		File file = File.createTempFile("nomina", "");
		JooqPayrollBuilder.generatePayroll(getDomain().getName(), new FileOutputStream(file), salaryId);
		return file;
	}

	private File getCompanyCostsExcel(HttpServletRequest req) throws JSONException, IOException {
		LOGGER.info("[GET] COMPANY COSTS EXCEL");
		Company company = AON.getCompany(getDomain().getName(), getDomain().getId(), "", f->f.getDomainProperty().eq(getDomain().getId()));
		ExcelType excelType = ExcelType.COMPLETE;
		String excelParams = getParams().optString("excelType");
		if(excelParams.equalsIgnoreCase("SUMMARY")) excelType = ExcelType.SUMMARY;
		
		File file = File.createTempFile("companyCosts", "");
	
		Date startDate =Toolkit.parseDate(getParams().optString("startDate"), "yyyy-MM-dd");
		Integer workplaceId = 0;
		if(!getParams().optString("workplace").isEmpty()) workplaceId = getParams().optInt("workplace");

		if(!getParams().optString("endDate").isEmpty()) {
			Date endDate = Toolkit.parseDate(getParams().optString("endDate"), "yyyy-MM-dd");
			EnterprisePayrollExcel.simpleEnterprisePayrollGenerator(
					getDomain().getName(), new FileOutputStream(file), Optional.of(company.getId()), Optional.of(workplaceId), 
					startDate, endDate, excelType);
		} else {
			EnterprisePayrollExcel.simpleEnterprisePayrollGenerator(
					getDomain().getName(), new FileOutputStream(file), Optional.of(company.getId()), Optional.of(workplaceId), 
					startDate, excelType);
		}
	
		return file;
	}
	
	private Date getEndDateSalary(Optional<Integer> companyId) throws SQLException {
		Connection conn = AonServletUtils.getConnection(getDomain().getName());
		SalaryInfoFilter filter = getFilter();
		if(!companyId.isEmpty()) filter.setEnterpriseId(companyId.get().intValue());
		SalaryInfo salaryInfo = JooqPayrollSalaries.getSalariesDateEnd(conn, filter);
		Date date = salaryInfo.getEndDate();
		if(date == null) date = new Date();
		return date;
	}
	
	private SalaryInfoFilter getFilter() {
		SalaryInfoFilter filter = new SalaryInfoFilter();
		
		if(!getParams().optString("startDate").isEmpty()) {
			filter.setDateTillT(Toolkit.parseDate(getParams().optString("startDate"), "yyyy-MM-dd"));
		}
		if(!getParams().optString("endDate").isEmpty()) {
			filter.setDateTTo(Toolkit.parseDate(getParams().optString("endDate"), "yyyy-MM-dd")); 
		} 
		if(!getParams().optString("salaryType").isEmpty()) filter.setSalaryType(getParams().optInt("salaryType"));
	
		return filter;
	}

}
