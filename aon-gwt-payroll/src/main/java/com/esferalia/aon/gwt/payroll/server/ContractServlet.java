package com.esferalia.aon.gwt.payroll.server;


import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
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
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.google.gson.Gson;
import com.google.gson.JsonParser;
import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;
import solutions.aon.seg.social.toolkit.Toolkit;


@MultipartConfig
@SuppressWarnings("serial")
@WebServlet(name = "CONTRACT-SERVLET", urlPatterns = { "/ms/api/contract/*"})
public class ContractServlet extends AonApiHttpServlet {
	private static Logger LOGGER = Logger.getLogger(ContractServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)  {
		LOGGER.info("[GET] CONTRACT SERVLET");
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
	
	private Object getAllEmployeesInfo() {
		System.out.println("GET GET EMPLOYEE INFO");
		try {
			Connection conn = AonServletUtils.getConnection(getDomain().getName());
			String jsonInString = null;
			boolean allEmployees = getParams().optBoolean("allEmployees");  
			jsonInString = new Gson().toJson(JooqContrataContract.getAllEmployeesInfo(conn, getDomain().getId(), allEmployees));
			return new JsonParser().parse(jsonInString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new JSONObject();
	}
	
	private Object getEmployeeSalaries() {
		try {
			Connection conn = AonServletUtils.getConnection(getDomain().getName());
			System.out.println("GET EMPLOYEE SALARIES");
			Integer contractId = JooqPayrollSalaries.getContractByRegistry(conn, getUser().getRegistry());
//			contractId = 18783;
			if(contractId!=null) {
				String jsonInString = new Gson().toJson(getSalaries(conn,  Optional.empty(),  Optional.ofNullable(contractId)));
				if(jsonInString!=null) return new JsonParser().parse(jsonInString);
			} 
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return new JSONObject();
	}
	
	private Object getEnterpriseSalaries() {
		System.out.println("GET ENTERPRISE SALARIES");
		try {
			Connection conn = AonServletUtils.getConnection(getDomain().getName());
			Company company = AON.getCompany(getDomain().getName(), getDomain().getId(), "", f->f.getDomainProperty().eq(getDomain().getId()));
			String jsonInString = new Gson().toJson(getSalaries(conn, Optional.ofNullable(company.getId()), Optional.empty()));
			if(jsonInString!=null) return new JsonParser().parse(jsonInString);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return new JSONObject();
	}
	
	private List<SalaryInfo> getSalaries(Connection conn, Optional<Integer> companyId, Optional<Integer> contractId) {
	
		SalaryInfoFilter filter = new SalaryInfoFilter();
		if(!companyId.isEmpty()) filter.setEnterpriseId(companyId.get().intValue());
		else if(!contractId.isEmpty())filter.setEmployeeId(contractId.get().intValue());
		
		if(!getParams().optString("startDate").isEmpty()) {
			filter.setDateTillT(Toolkit.parseDate(getParams().optString("startDate"), "yyyy-MM-dd"));
		}
		if(!getParams().optString("endDate").isEmpty()) {
			filter.setDateTTo(Toolkit.parseDate(getParams().optString("endDate"), "yyyy-MM-dd")); 
		} 
		
		if(!getParams().optString("salaryType").isEmpty()) filter.setSalaryType(getParams().optInt("salaryType"));
		return JooqPayrollSalaries.getSalaries(conn, filter);
	}
	
	private byte[] getSalaryPdf(HttpServletRequest req) {
		System.out.println("GET SALARY PDF");
		try {
			String param = req.getParameter("json");
			param = new String(Base64.getDecoder().decode(param));
			JSONObject json = new JSONObject(param);
			String domainName = json.getString("domain_name");
			Integer salaryId = json.optInt("salaryId");
			ByteArrayOutputStream outputstream = new ByteArrayOutputStream();
			JooqPayrollBuilder.generatePayroll(domainName, outputstream, salaryId);
			return outputstream.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
