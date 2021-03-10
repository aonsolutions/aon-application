package com.esferalia.aon.gwt.payroll.server;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;
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
import com.esferalia.aon.occam.api.AON;
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
		try {
			String path = req.getPathInfo() != null || "null".equalsIgnoreCase(req.getPathInfo()) ? req.getPathInfo() : "/";
			switch (path) {
				case "/salary/pdf":
					responseFile(req, resp, getSalaryPdf(req), MimeType.PDF);
					break;
				default:
					responseJson(req, resp, path);
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp){
		doGet(req, resp);
	}
	
	private void responseJson(HttpServletRequest req, HttpServletResponse resp, String path) {
		super.doGet(req, resp);
		try {
			switch (path) {
			case "/":
				super.doGet(req, resp);
				response(req, resp, getAllEmployeesInfo());
				break;
			case "/employee/salaries":
				super.doGet(req, resp);
				response(req, resp, getEmployeeSalaries());
				break;
			case "/enterprise/salaries":
				super.doGet(req, resp);
				response(req, resp, getEnterpriseSalaries());
				break;
			default:
				throw new Exception("La ruta introducida es incorrecta.");
			}
		} catch (Exception e) {
			error(req, resp, e);
		}
	}
	
	private Object getAllEmployeesInfo() throws SQLException {
		LOGGER.info("[GET] EMPLOYEE INFO");
		Connection conn = AonServletUtils.getConnection(getDomain().getName());
		boolean allEmployees = getParams().optBoolean("allEmployees");  
		Gson gjson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();
		String jsonInString = gjson.toJson(JooqContrataContract.getAllEmployeesInfo(conn, getDomain().getId(), allEmployees));
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
		String jsonInString = gjson.toJson(getSalaries(conn, Optional.ofNullable(company.getId()), Optional.empty()));
		if(jsonInString!=null) return new JsonParser().parse(jsonInString);
		return new JSONObject();
	}
	
	private List<SalaryInfo> getSalaries(Connection conn, Optional<Integer> companyId, Optional<Integer> contractId) {
		SalaryInfoFilter filter = getFilter();
		if(!companyId.isEmpty()) filter.setEnterpriseId(companyId.get().intValue());
		else if(!contractId.isEmpty())filter.setEmployeeId(contractId.get().intValue());
		
		return JooqPayrollSalaries.getSalaries(conn, filter);
	}
	
	private File getSalaryPdf(HttpServletRequest req) throws JSONException, IOException {
		LOGGER.info("[GET] SALARY PDF");
		String param = new String(Base64.getDecoder().decode(req.getParameter("json")));
		
		JSONObject json = new JSONObject(param);
		String domainName = json.getString("domain_name");
		Integer salaryId = json.optInt("salaryId");
	
		File file = File.createTempFile("nomina", "pdf");
		JooqPayrollBuilder.generatePayroll(domainName,  new FileOutputStream(file), salaryId);
		return file;
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
