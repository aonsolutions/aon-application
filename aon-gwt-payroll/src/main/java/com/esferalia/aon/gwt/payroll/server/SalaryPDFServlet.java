package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jooq.Condition;
import org.jooq.impl.DSL;

import com.code.aon.common.enumeration.MimeType;
import com.code.aon.report.OutputFormat;
import com.code.aon.report.ReportException;
import com.code.aon.ui.report.controller.ReportManager;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.payroll.report.StatelessReportManager;
import com.esferalia.aon.gwt.payroll.server.PayrollServletUtils.SiteFilter;
import com.esferalia.aon.jooq.tables.Salary;
import com.esferalia.aon.jooq.tables.Workplace;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.ui.payroll.utils.ReportUtils;
import com.google.api.client.util.Base64;

@SuppressWarnings("serial")
@WebServlet(name = "Salary-PDF", 
	urlPatterns = { 
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
		String domain = req.getServerName();
		String requestURI = req.getRequestURI();
		String extension = AonServletUtils.getExtn(requestURI);
		String salaryRequestStr = AonServletUtils.getFileName(requestURI);
		
		String paramsStr = decode(salaryRequestStr.getBytes());
		Map<String, String> params = createParams(paramsStr);
		
		try {
			
			Integer selectedSalaries = Integer.parseInt(params.get("selectedSalaries"));
			Condition condition = getConditionSalaryIds(params, selectedSalaries);
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
			
			// TODO: if want to download attachment instead open tab
//			resp.setHeader("Content-disposition", "attachment; filename=\""
//					+ params.get("name") + "\"");
			
			OutputStream os = resp.getOutputStream();
			
			// TODO : SalaryType????
			SalaryType salaryType = params.get("type").equals("settle") ? SalaryType.SETTLE : SalaryType.SALARY;
			Integer enterpriseID = Integer.parseInt(params.get("enterprise"));
			String salaryReport = getReportKey(domain, enterpriseID, salaryType); 
			
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
	
	private Condition getConditionSalaryIds(Map<String, String> params, Integer selectedSalaries) {
		ArrayList<Integer> _selectedSalaries = new ArrayList<>();

		if(0 != selectedSalaries) {
			for(int i=0; i<selectedSalaries; i++) {
				String idString = params.get("salary"+i+"Id");
				
				if(idString.contains("."))
					idString = idString.split("\\.")[0];
				
				_selectedSalaries.add(Integer.parseInt(idString));
			}
		}
		
		Condition condition ;
		condition = Salary.SALARY.ID.in(_selectedSalaries);
		
		return condition;
	}

	private Map<String, String> createParams(String paramsStr) {
		HashMap<String, String> paramsMap = new HashMap<String, String>();
		
		String params = paramsStr.substring(1);
		
		String[] paramsArr = params.split("&");
		for(int i=0; i < paramsArr.length; i++) {
			String key = paramsArr[i].split("=")[0];
			String value = paramsArr[i].split("=")[1];
			
			paramsMap.put(key, value);
		}
		
		return paramsMap;
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
