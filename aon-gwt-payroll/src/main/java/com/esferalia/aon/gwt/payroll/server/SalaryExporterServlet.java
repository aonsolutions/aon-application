package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.common.BeanManager;
import com.code.aon.common.IManagerBean;
import com.code.aon.common.ManagerBeanException;
import com.code.aon.common.enumeration.MimeType;
import com.code.aon.ql.Criteria;
import com.code.aon.report.OutputFormat;
import com.code.aon.report.ReportException;
import com.code.aon.ui.report.controller.ReportManager;
import com.esferalia.aon.entity.IEntityAlias;
import com.esferalia.aon.gwt.common.bean.GWT;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.common.shared.Constants;
import com.esferalia.aon.gwt.payroll.server.PayrollServletUtils.SiteFilter;
import com.esferalia.aon.salary.enumeration.SalaryType;
import com.esferalia.aon.watson.util.AonStringUtils;

@SuppressWarnings("serial")
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

		String requestURI = req.getRequestURI();
		String extension = AonServletUtils.getExtn(requestURI);
		String salaryRequestStr = AonServletUtils.getFileName(requestURI);
		
		try {
			ServletContext ctx = getServletContext();
			AonServletUtils.initFacesContext(ctx, req, resp);
			FacesContext fCtx = FacesContext.getCurrentInstance();
			fCtx.getViewRoot().setLocale(new Locale("es", "ES"));
			
			Criteria criteria =  getCriteria(salaryRequestStr);
			
			ReportManager reportManager = new ReportManager();
			OutputFormat outputFormat = getOutputFormat(extension);
			reportManager.setOutputFormat(outputFormat);
			
			reportManager.setCollectionProvider(new PayrollServletUtils.SalaryProvider(criteria, new SiteFilter()));
			
			
			MimeType mimeType = MimeType.getByExtension(extension);
			resp.setContentType(mimeType.getName());
			
			OutputStream os = resp.getOutputStream();
			
			int enterpriseId = AonRemoteServiceServlet.getEnterpriseID();
			// TODO : SalaryType????
			SalaryType salaryType = getSalaryType(req);
			String salaryReport = PayrollServletUtils.getSalaryReport(enterpriseId, salaryType); 
			
			reportManager.execute(os, salaryReport);
			
			os.flush();
			
		} catch (SQLException e) {
			throw new ServletException(e);
		} catch (ReportException e) {
			throw new ServletException(e);
		}catch (ManagerBeanException e) {
			throw new ServletException(e);
		} finally{
			AonServletUtils.releaseFacesContext();
		}
	}

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

	private static Criteria getCriteria(String request) throws ManagerBeanException {
		IManagerBean beanManager = BeanManager
				.getManagerBean(com.esferalia.aon.payroll.Salary.class);
		Criteria criteria = new Criteria();

		Matcher matcher = 
				MONTH_PATTERN.matcher(request);
		
		if ( !matcher.matches() ) {
			int salaryId = Integer.parseInt(request);
			criteria.addEqualExpression(
					beanManager.getFieldName(IEntityAlias.SALARY_ID ),
					salaryId);
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
				criteria.addEqualExpression(
						beanManager.getFieldName(IEntityAlias.SALARY_CONTRACT_WORK_PLACE_ID ),
						workplaceId);
			}
			else {
				criteria.addEqualExpression(
						beanManager.getFieldName(IEntityAlias.SALARY_CONTRACT_WORK_PLACE_ENTERPRISE_ID ),
						enterpriseId );
			}
			
			criteria.addBetweenExpression(
					beanManager.getFieldName(IEntityAlias.SALARY_END_DATE),
					startDate, 
					endDate );
			
			criteria.addOrder(beanManager.getFieldName(IEntityAlias.SALARY_CONTRACT_WORK_PLACE_ID));
			criteria.addOrder(beanManager.getFieldName(IEntityAlias.SALARY_EMPLOYEE_NAME));
		}
		return criteria;
	}


	private static String getEntryPoint(HttpServletRequest request){
		return ((GWT) request.getSession().getAttribute("gwt")).getEntryPoint();
	}

	private static boolean isAtEnterpriseSite( HttpServletRequest request){
		return Constants.ENTERPRISE_SITE_ENTRY_POINT.equals(getEntryPoint(request));
	}
	
}
