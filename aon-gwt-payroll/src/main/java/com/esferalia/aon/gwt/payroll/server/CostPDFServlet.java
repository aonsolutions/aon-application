package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.report.OutputFormat;
import com.esferalia.aon.gwt.common.bean.GWT;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.common.shared.Constants;
import com.esferalia.aon.gwt.payroll.shared.Salary;
import com.esferalia.aon.gwt.payroll.shared.Salary.Type;
import com.esferalia.aon.gwt.payroll.util.JooqEnterpriseSalaryBuilder;
import com.esferalia.aon.gwt.payroll.util.Utilities;
import com.esferalia.aon.salary.enumeration.SalaryType;

@SuppressWarnings("serial")
@WebServlet(name = "Cost-PDF", 
			urlPatterns = { "/aon_gwt_aio/cost_pdf/*", 
							"/aon_gwt_payroll/cost_pdf/*" 
			})
public class CostPDFServlet extends HttpServlet {

	private static Map<String, OutputFormat> OUTPUT_FORMATS = new HashMap<String, OutputFormat>() {
		{
			put("pdf", OutputFormat.PDF);
		}
	};
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
		throws ServletException, IOException {
		String request = AonServletUtils.getFileName(req.getRequestURI());
		String selectedSalaries = req.getParameter("selectedSalaries");
		Integer[] salaryIds = getSalaryIds(req, selectedSalaries);
		LinkedList<Salary.Type> typeList = new LinkedList<Salary.Type>();
		
		
		Iterator<String> it = req.getParameterMap().keySet().iterator();
		while (it.hasNext()) {
			String n = it.next();
			System.out.println(n+": "+req.getParameterMap().get(n));
		}
		
		if (req.getParameter("salary") != null && req.getParameter("salary").equals("1"))
			typeList.add(Salary.Type.SALARY);
		if (req.getParameter("extra") != null && req.getParameter("extra").equals("1"))
			typeList.add(Salary.Type.EXTRA);
		if (req.getParameter("settle") != null && req.getParameter("settle").equals("1"))
			typeList.add(Salary.Type.SETTLE);
		if (req.getParameter("delay") != null && req.getParameter("delay").equals("1"))
			typeList.add(Salary.Type.DELAY);
		
		Type[] types = typeList.toArray(new Salary.Type[typeList.size()]);
		
		
		
		if (salaryIds.length > 0)
			JooqEnterpriseSalaryBuilder.generateEnterprisePayroll(resp.getOutputStream()
					, req.getServerName()
					, salaryIds
					, getMonth(AonServletUtils.getFileName(req.getRequestURI()))
					, getEnterpriseId(request)
					);
		else
			noIds(resp.getOutputStream(), req.getServerName(), request, types);
		
		
	}

	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Integer enterpriseId;
		Integer workplaceId;
		Integer month;
		Integer year;
		Salary.Type[] types;
		
		//Picking up the parameters
		{
			if (req.getParameterValues("filter") != null)
				types = Arrays.stream(req.getParameterValues("filter")).map(str -> {
					Integer ordinal = Integer.parseInt(str);
					Salary.Type type = Utilities.typeOf(ordinal.byteValue(), Salary.Type.class);
					return type;
				}).toArray(Salary.Type[]::new);
			else
				types = new Salary.Type[0];
			
			enterpriseId = req.getParameter("enterpriseId") != null ? Integer.parseInt(req.getParameter("enterpriseId")) : null;
			workplaceId = req.getParameter("workplaceId") != null ? Integer.parseInt(req.getParameter("workplaceId")) : null;
			month = Integer.parseInt(req.getParameter("month"));
			year = Integer.parseInt(req.getParameter("year"));
			
		}
		
		
		

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, 1); // The first day of the month has value 1.
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Date startDate = calendar.getTime();
		calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
		Date endDate = calendar.getTime();
		
		JooqEnterpriseSalaryBuilder.generateEnterprisePayroll(
				resp.getOutputStream()
				, req.getServerName()
				, startDate
				, endDate
				, enterpriseId
				, workplaceId
				, types
				);
		
		
		
	}







	private static Date getMonth (String request) {
		Calendar calendar = Calendar.getInstance();
		Matcher matcher = MONTH_PATTERN.matcher(request);
		Integer month = null;
		if (matcher.matches()) {
			calendar.set(Calendar.MONTH, Integer.parseInt(matcher.group("month"))+1);
			return calendar.getTime();
		} else
			return null;
	}
	
// ------------------------------------------------------------------------

	protected String getReportKey(String domain, final Integer enterpriseID, SalaryType salaryType)
			throws SQLException {
		return PayrollServletUtils.getSalaryReport(domain, enterpriseID, salaryType);
	}

	private static Pattern MONTH_PATTERN = Pattern.compile("(?<month>\\d{1,2})_(?<year>\\d{4})_(?<enterpriseid>\\d+)_(?<workplaceid>\\d+)");
	
	private Integer[] getSalaryIds(HttpServletRequest req, String selectedSalaries) {
		ArrayList<Integer> _selectedSalaries = new ArrayList<>();
		try {
			Integer numSalaries = Integer.parseInt(selectedSalaries);
			if (0 != numSalaries) {
				for (int i = 0; i < numSalaries; i++) {
					String idString = req.getParameter("salary" + i + "Id");
					if (idString.contains("."))
						idString = idString.split("\\.")[0];

					_selectedSalaries.add(Integer.parseInt(idString));
				}
			}
		} catch (Exception e) {}
		return _selectedSalaries.toArray(new Integer[_selectedSalaries.size()]);
	}
	
	
	private static void noIds (OutputStream outputStream, String domainName, String request, Salary.Type[] types) {

		Matcher matcher = MONTH_PATTERN.matcher(request);
		if (matcher.matches()) {
			int month = Integer.parseInt(matcher.group("month"));
			int year = Integer.parseInt(matcher.group("year"));
	
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.YEAR, year);
			calendar.set(Calendar.MONTH, month);
			calendar.set(Calendar.DAY_OF_MONTH, 1); // The first day of the month has value 1.
			calendar.set(Calendar.HOUR, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			Date startDate = calendar.getTime();
			calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
			Date endDate = calendar.getTime();
	
			int enterpriseId = Integer.parseInt(matcher.group("enterpriseid"));
			int workplaceId = Integer.parseInt(matcher.group("workplaceid"));
			
			JooqEnterpriseSalaryBuilder.generateEnterprisePayroll(outputStream
					, domainName
					, startDate
					, endDate
					, enterpriseId
					, workplaceId
					);
		}

	}
	
	
	private static Integer getEnterpriseId (String request) {
		Matcher matcher = MONTH_PATTERN.matcher(request);
		if (matcher.matches()) {
			return Integer.parseInt(matcher.group("enterpriseid"));
		}
		return null;
	}
	
	
	
	

	private static String getEntryPoint(HttpServletRequest request) {
		return ((GWT) request.getSession().getAttribute("gwt")).getEntryPoint();
	}

	private static boolean isAtEnterpriseSite(HttpServletRequest request) {
		return Constants.ENTERPRISE_SITE_ENTRY_POINT.equals(getEntryPoint(request));
	}

	private static <T extends Enum<?>> T typeOf(Byte ordinal, Class<T> type) {
		if (ordinal == null)
			return null;
		try {
			return type.getEnumConstants()[ordinal];
		} catch (Exception e) {
			return null;
		}
	}
}
