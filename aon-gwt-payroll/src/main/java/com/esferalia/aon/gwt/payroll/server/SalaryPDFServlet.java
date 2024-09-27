package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.jooq.Condition;
import org.jooq.impl.DSL;
import org.json.JSONObject;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.company.enumeration.SalaryTemplate;
import com.code.aon.report.OutputFormat;
import com.code.aon.report.ReportException;
import com.code.aon.ui.report.controller.ReportManager;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.payroll.report.StatelessReportManager;
import com.esferalia.aon.gwt.payroll.server.PayrollServletUtils.SiteFilter;
import com.esferalia.aon.gwt.payroll.shared.PayrollPrintService;
import com.esferalia.aon.in.payroll.pdf.jooq.JooqPayrollBuilder;
import com.esferalia.aon.jooq.tables.Salary;
import com.esferalia.aon.jooq.tables.Workplace;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Company;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.ui.payroll.utils.ReportUtils;
import com.esferalia.aon.watson.util.AonStringUtils;
import com.google.api.client.util.Base64;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.aonsolutions.aon.api.ewok.IConstants;

@SuppressWarnings("serial")
@WebServlet(name = "Salary-PDF", 
	urlPatterns = { 
			"/ms/api/salary_exporter/*",
			"/aon_gwt_aio/salary_exporter/*",
			"/aon_gwt_payroll/salary_exporter/*" 
	})
public class SalaryPDFServlet extends HttpServlet {
	
	private static Map<String, OutputFormat> OUTPUT_FORMATS = 
			new HashMap<String, OutputFormat>(){
		{
			put("pdf", OutputFormat.PDF);
		}
	};
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException{
		doPost(req, resp);
	}
	
	
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException {
		String path = req.getPathInfo();
		
		try {
			
			if(AonStringUtils.equalsIgnoreCase(path, "/")) getSalaryAttachPdf(req, resp);
			else if(AonStringUtils.equalsIgnoreCase(path, "/salary")) getSalaryPdf(req, resp)	;
			else getSalaryAttachPdf(req, resp);
		
		} catch (Exception e) {
			System.err.println(e.getMessage());
			throw new ServletException(e);
		}
		
	}

	private void getSalaryAttachPdf(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String domain = req.getServerName();
		String requestURI = req.getRequestURI();
		String extension = AonServletUtils.getExtn(requestURI);
		String salaryRequestStr = AonServletUtils.getFileName(requestURI);
		
		try {
			// TODO : SalaryType????
			SalaryType salaryType = req.getParameter(PayrollPrintService.Parameter.TYPE.getName()).equals("settle") ? SalaryType.SETTLE : SalaryType.SALARY;
			Integer enterpriseID = Integer.parseInt(req.getParameter(PayrollPrintService.Parameter.ENTERPRISE.getName()));
			String salaryReport = getReportKey(domain, enterpriseID, salaryType);
			if ( AonStringUtils.equalsIgnoreCase(salaryReport, SalaryTemplate.AON_SOLUTIONS_MACLEOD.getValue() )) {
				String macLeodPath = req.getServletPath().replace("salary_exporter", "salary_connor_macleod");
				RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(macLeodPath);
				dispatcher.forward(req, resp);
				return;
			} else if (AonStringUtils.equalsIgnoreCase(salaryReport, SalaryTemplate.AON_SOLUTIONS_DEFAULT.getValue() )) {
				String macLeodPath = req.getServletPath().replace("salary_exporter", "salary_connor_macleod");
				req.setAttribute(PayrollPrintService.Parameter.PAYROLL_TYPE.getName(), PayrollPrintService.PayrollType.CLASSIC.getName());
				RequestDispatcher dispatcher = getServletContext().getRequestDispatcher(macLeodPath);
				dispatcher.forward(req, resp);
				return;				
			}
			Integer selectedSalaries = req.getParameterValues(PayrollPrintService.Parameter.ID.getName()).length;
			Condition condition = getConditionSalaryIds(req.getParameterValues(PayrollPrintService.Parameter.ID.getName()), selectedSalaries);
			extension = "pdf";
			
			DSL.orderBy(Workplace.WORKPLACE.ID, Salary.SALARY.EMPLOYEE_NAME);
			
			ReportManager reportManager = new StatelessReportManager();
			OutputFormat outputFormat = getOutputFormat(extension);
			reportManager.setOutputFormat(outputFormat);
			
			reportManager.setCollectionProvider(
			new PayrollServletUtils.SalaryProvider(
			domain, 
			condition, 
			new SiteFilter(), 
			Workplace.WORKPLACE.ID.asc(), 
			Salary.SALARY.EMPLOYEE_NAME.asc()));
			
			MimeType mimeType = MimeType.getByExtension(extension);
			resp.setContentType(mimeType.getName());
			
			OutputStream os = resp.getOutputStream();
			
			
			// TODO: Bufff !!!!!!!!!!!!!!!
			ReportUtils.domain.set(domain);
			
			reportManager.execute(os, salaryReport);
			
			os.flush();
			
		} catch (SQLException e) {
			throw new ServletException(e);
		} catch (ReportException e) {
			throw new ServletException(e);
		}
	}

	private void getSalaryPdf(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		String param = req.getParameter("json");
		param = new String(java.util.Base64.getDecoder().decode(param));
		JSONObject json = new JSONObject(param);

		String domainName = json.optString("domain_name");
		Integer domainId = json.optInt("domain_id");
		
		Integer salaryId = json.optInt("id");
		
		Company company = AON.getCompany(domainName, domainId, "", f->f.getDomainProperty().eq(domainId));
		
		String salaryReport = null;
		try {			
			salaryReport = PayrollServletUtils.getSalaryReport(domainName, company.getId(), SalaryType.SALARY);
		} catch (Exception e) {}
		
		if (AonStringUtils.equalsIgnoreCase(salaryReport, SalaryTemplate.AON_SOLUTIONS_DEFAULT.getValue()))
			JooqPayrollBuilder.generateClassicPayroll(company.getId() > 0 ? company.getId() : null, domainName, resp.getOutputStream(), Optional.empty(), salaryId);
		else
			JooqPayrollBuilder.generatePayroll(company.getId() > 0 ? company.getId() : null, domainName, resp.getOutputStream(), Optional.empty(), salaryId);
		
		resp.addHeader(IConstants.ACCESS_CONTROL_ALLOW_ORIGIN, "*");
		resp.addHeader(IConstants.ACCESS_CONTROL_ALLOW_METHODS, "POST, GET, OPTIONS, PUT, DELETE, HEAD");
		resp.addHeader(IConstants.ACCESS_CONTROL_ALLOW_HEADERS, "*");
		resp.addHeader(IConstants.ACCESS_CONTROL_MAX_AGE, "1728000");
		
        resp.setContentType(com.esferalia.aon.occam.api.model.type.MimeType.PDF.getName());
		resp.setHeader(IConstants.CONTENT_DISPOSITION, "inline; filename=\"" + "nomina" + "." + com.esferalia.aon.occam.api.model.type.MimeType.PDF.getExtension() +"\";");
		resp.flushBuffer();
	}


	private Condition getConditionSalaryIds(String[] ids, Integer selectedSalaries) {
		ArrayList<Integer> _selectedSalaries = new ArrayList<>();

		for (String strId : ids) {
			try {
				_selectedSalaries.add(Integer.parseInt(strId));
			} catch (NumberFormatException e) {}
		}
		
		Condition condition ;
		condition = Salary.SALARY.ID.in(_selectedSalaries);
		
		return condition;
	}

	protected String getReportKey(String domain, final Integer enterpriseID,
			SalaryType salaryType) throws SQLException {
		return PayrollServletUtils.getSalaryReport(domain, enterpriseID, salaryType);
	}

	// ------------------------------------------------------------------------

	private static OutputFormat getOutputFormat(String extension) {
		return OUTPUT_FORMATS.get(extension);
	}
	
	public String decode(byte[] value){
		String decode = "";
		decode = new String(Base64.decodeBase64(value));
		return decode;
	}

}
