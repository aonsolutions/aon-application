package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import javax.servlet.annotation.WebServlet;

import org.jooq.Condition;
import org.jooq.DSLContext;
import org.jooq.impl.DSL;

import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.report.OutputFormat;
import com.code.aon.report.ReportException;
import com.code.aon.ui.report.controller.ReportManager;
import com.esferalia.aon.gwt.common.bean.GWT;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.common.shared.Constants;
import com.esferalia.aon.gwt.payroll.report.StatelessReportManager;
import com.esferalia.aon.gwt.payroll.server.PayrollServletUtils.SiteFilter;
import com.esferalia.aon.jooq.tables.Contract;
import com.esferalia.aon.jooq.tables.Enterprise;
import com.esferalia.aon.jooq.tables.Salary;
import com.esferalia.aon.jooq.tables.Workplace;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.ui.payroll.utils.ReportUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

@SuppressWarnings("serial")
@WebServlet(
		name = "SalaryExporterServlet", 
		urlPatterns = { 
				"/aon_gwt_aio/salary/*",
				"/aon_gwt_payroll/salary/*"
		}
)
public class SalaryExporterServlet extends HttpServlet {
	
	
	private static Map<String, OutputFormat> OUTPUT_FORMATS = 
			new HashMap<String, OutputFormat>(){
		{
			put("pdf", OutputFormat.PDF);
		}
	};
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String domain = req.getServerName();
		String requestURI = req.getRequestURI();
		String extension = AonServletUtils.getExtn(requestURI);
		String salaryRequestStr = AonServletUtils.getFileName(requestURI);
		
		String _selectedSalaries = req.getParameter("selectedSalaries");
		
		try {
			
			Condition condition = null;
			
			if(null != _selectedSalaries) {
				condition = getConditionSalaryIds(req, _selectedSalaries);
				extension = "pdf";
			}else {
				condition =  getCondition(salaryRequestStr);
			}
			
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
			
			// TODO : SalaryType????
			SalaryType salaryType = getSalaryType(req);
			Integer enterpriseID;
			if(null != _selectedSalaries) {
				enterpriseID = Integer.parseInt(req.getParameter("enterprise"));
			}else {
				enterpriseID = getEnterpriseID(domain, salaryRequestStr);
			}
			String salaryReport = getReportKey(domain, enterpriseID, salaryType); 
			
			// TODO: Bufff !!!!!!!!!!!!!!!
			ReportUtils.domain.set(domain);
			
			reportManager.execute(os, salaryReport);
			
			os.flush();
			
		} catch (SQLException e) {
			throw new ServletException(e);
		} catch (ReportException e) {
			throw new ServletException(e);
		}catch (ManagerBeanException e) {
			throw new ServletException(e);
		} 
	}

	// ------------------------------------------------------------------------
	
	protected String getReportKey(String domain, final Integer enterpriseID,
			SalaryType salaryType) throws SQLException {
		return PayrollServletUtils.getSalaryReport(domain, enterpriseID, salaryType);
	}

	// ------------------------------------------------------------------------

	private static OutputFormat getOutputFormat(String extension) {
		return OUTPUT_FORMATS.get(extension);
	}
	
	
	private static Pattern MONTH_PATTERN = 
			Pattern.compile("(\\d{1,2})_(\\d{4})_(\\d+)_(\\d+)");

	private static SalaryType getSalaryType(HttpServletRequest req) {
		String queryString = req.getQueryString();
		if ( AonStringUtils.isBlank(queryString) ) 
			return SalaryType.SALARY;

		String typeName = queryString.trim();
		try {
			return SalaryType.valueOf(typeName);
		} catch ( Throwable t ) {
		}
		
		return SalaryType.SALARY;
	}

	private static Integer getEnterpriseID(String domain, String request) throws SQLException {
		Connection conn = null;
		try {
			conn = AonServletUtils.getConnection(domain);
			AONContext aonContext = new AONContext(conn);
			DSLContext dslContext = aonContext.getDslContext();
			
			Matcher matcher = 
					MONTH_PATTERN.matcher(request);
			
			if ( !matcher.matches() ) {
				int salaryId = Integer.parseInt(request);
				return dslContext.select()
				.from(Salary.SALARY)
				.innerJoin(Contract.CONTRACT).onKey()
				.innerJoin(Workplace.WORKPLACE).onKey()
				.where(Salary.SALARY.ID.eq(salaryId))
				.fetchOne(Workplace.WORKPLACE.ENTERPRISE)
				;
			} else {
				int workplaceId = Integer.parseInt(matcher.group(4));
		
				if ( workplaceId != 0  )
					return dslContext.select()
					.from(Workplace.WORKPLACE)
					.where(Workplace.WORKPLACE.ID.eq(workplaceId))
					.fetchOne(Workplace.WORKPLACE.ENTERPRISE)
					;
				
				int enterpriseId = Integer.parseInt(matcher.group(3));
				return enterpriseId;
			}
			
		}  finally {
			if (conn != null) {
				conn.close();
			}
		}
	}
	
	private Condition getConditionSalaryIds(HttpServletRequest req, String selectedSalaries) {
		ArrayList<Integer> _selectedSalaries = new ArrayList<>();

		Integer numSalaries = Integer.parseInt(selectedSalaries);
		if(0 != numSalaries) {
			for(int i=0; i<numSalaries; i++) {
				String idString = req.getParameter("salary"+i+"Id");
				if(idString.contains("."))
					idString = idString.split("\\.")[0];
				
				_selectedSalaries.add(Integer.parseInt(idString));
			}
		}
		
		Condition condition ;
		condition = Salary.SALARY.ID.in(_selectedSalaries);
		
		return condition;
	}

	private static Condition getCondition(String request) throws ManagerBeanException {
		Condition condition ;

		Matcher matcher = 
				MONTH_PATTERN.matcher(request);
		
		if ( !matcher.matches() ) {
			int salaryId = Integer.parseInt(request);
			condition = Salary.SALARY.ID.eq(salaryId);
		} else {

			int month = Integer.parseInt(matcher.group(1));
			int year = Integer.parseInt(matcher.group(2));
	
			Calendar calendar = Calendar.getInstance();
			calendar.set( Calendar.YEAR, year);
			calendar.set( Calendar.MONTH, month);
			calendar.set( Calendar.DAY_OF_MONTH, 1); // The first day of the month has value 1.
			calendar.set( Calendar.HOUR, 0);
			calendar.set( Calendar.MINUTE, 0);
			calendar.set( Calendar.SECOND, 0);
			Date startDate = calendar.getTime();
			calendar.set(Calendar.DAY_OF_MONTH, 
					calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			Date endDate = calendar.getTime();
	
			int enterpriseId = Integer.parseInt(matcher.group(3));
			int workplaceId = Integer.parseInt(matcher.group(4));
	
			if ( workplaceId != 0  ){
				condition = Workplace.WORKPLACE.ID.eq(workplaceId);
			}
			else {
				condition = Enterprise.ENTERPRISE.REGISTRY.eq(enterpriseId);
			}
			
			java.sql.Date sqlStartDate = new java.sql.Date(startDate.getTime());
			java.sql.Date sqlEndDate = new java.sql.Date(endDate.getTime());
			
			condition = condition.and(Salary.SALARY.END_DATE.between(sqlStartDate, sqlEndDate));
			
		}
		return condition;
	}


	private static String getEntryPoint(HttpServletRequest request){
		return ((GWT) request.getSession().getAttribute("gwt")).getEntryPoint();
	}

	private static boolean isAtEnterpriseSite( HttpServletRequest request){
		return Constants.ENTERPRISE_SITE_ENTRY_POINT.equals(getEntryPoint(request));
	}
	
}
