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

//@SuppressWarnings("serial")
//@WebServlet(name = "SalaryExporterServlet", urlPatterns = { "/aon_gwt_aio/salary/*", "/aon_gwt_payroll/salary/*" })
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
		
		Type[] types = typeList.toArray(Salary.Type[]::new);
		
		
		
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
	
//@Override
//protected void doGet(HttpServletRequest req, HttpServletResponse resp)
//	throws ServletException, IOException {
//
//	String domain = req.getServerName();
//	String requestURI = req.getRequestURI();
//	String extension = AonServletUtils.getExtn(requestURI);
//	String salaryRequestStr = AonServletUtils.getFileName(requestURI);
//	
//	String _selectedSalaries = req.getParameter("selectedSalaries");
//	try {
//		
//		Condition condition = null;
//		
//		if(null != _selectedSalaries) {
//			condition = getConditionSalaryIds(req, _selectedSalaries);
//			extension = "pdf";
//		}else {
//			condition =  getCondition(salaryRequestStr);
//		}
//		MimeType mimeType = MimeType.getByExtension(extension);
//		resp.setContentType(mimeType.getName());
//		try (AONContext aonContext = AONContext.getAONContext(domain, "")) {
//			DSLContext ctx = aonContext.getDslContext();
//			
//			Map<String, Map<String, EnterprisePayrollEntry>> map = new HashMap<String, Map<String,EnterprisePayrollEntry>>();
//			EnterprisePayrollEntry entry = new EnterprisePayrollEntry();
//			Map<String, Map<String, EnterprisePayrollEntry>> payrolls = getEnterprisePayrolls(ctx, condition);
//			
//			SalaryRecord record = ctx.select(SALARY.END_DATE, SALARY.ENTERPRISE_NAME).from(SALARY).innerJoin(CONTRACT).onKey()
//					.innerJoin(WORKPLACE).onKey().innerJoin(ENTERPRISE).onKey().where(condition).fetchAnyInto(SALARY);
//			
//			java.sql.Date sqlMonth = record.getEndDate();
//			Date month = new Date(sqlMonth.getTime());
//			
//			Attach attach1 = AON.getAttach(aonContext.getDomainName(), aonContext.getDomainId(), aonContext.getUser(),
//					f -> f.getTypeProperty().eq(RegistryAttachmentType.LOGO.value())
//							.and(f.getDomainProperty().eq(aonContext.getDomainId())),
//					AttachType.REGISTRY);
//			
//			byte[] byteLogo = attach1.getData();
//			
//			InputStream logo = null;
//			
//			try {
//				logo = new ByteArrayInputStream(byteLogo);
//			} catch (NullPointerException e) {
//			}
//			String subheader = "Empresa: ";
//			if (record.getEnterpriseName() != null) {
//				subheader = subheader.concat(record.getEnterpriseName());
//			}
//			EnterprisePayroll enterprisePayroll = new EnterprisePayroll(logo, month, "NÓMINA DE LA EMPRESA", subheader, payrolls, map);
//			PdfMaker.print_enterprise_payroll(enterprisePayroll, resp.getOutputStream(), Optional.of(new Locale("es")));
//		} catch (CanNotCreatePdfException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	
//		
//	} catch (ManagerBeanException e) {
//		throw new ServletException(e);
//	} 
//}

// ------------------------------------------------------------------------

	protected String getReportKey(String domain, final Integer enterpriseID, SalaryType salaryType)
			throws SQLException {
		return PayrollServletUtils.getSalaryReport(domain, enterpriseID, salaryType);
	}

// ------------------------------------------------------------------------
//	@Deprecated
//	public static Map<String, Map<String, EnterprisePayrollEntry>> getEnterprisePayrolls(DSLContext ctx, Condition condition)
//			throws IOException {
//
//		Map<Integer, Double> bonusesMap = ctx.select().from(SALARY).innerJoin(CONTRACT)
//				.on(CONTRACT.ID.eq(SALARY.CONTRACT)).innerJoin(WORKPLACE).on(CONTRACT.WORKPLACE.eq(WORKPLACE.ID))
//				.innerJoin(ENTERPRISE).onKey()
//				.innerJoin(SALARY_BONUS).on(SALARY.ID.eq(SALARY_BONUS.SALARY)).where(condition)
//				.fetchStreamInto(SALARY_BONUS)
//				.collect(Collectors.toMap(s -> s.getSalary(), s -> s.getAmount(), (a1, a2) -> a1 + a2));
//
//		Map<Integer, Map<String, Double>> deductions = new HashMap<Integer, Map<String, Double>>();
//		ctx.select().from(SALARY).innerJoin(CONTRACT).on(CONTRACT.ID.eq(SALARY.CONTRACT)).innerJoin(WORKPLACE)
//				.on(CONTRACT.WORKPLACE.eq(WORKPLACE.ID)).innerJoin(ENTERPRISE).onKey().innerJoin(SALARY_DEDUCTION)
//				.on(SALARY.ID.eq(SALARY_DEDUCTION.SALARY)).where(condition).fetchStream().forEach(s -> {
//					if (deductions.get(s.get(SALARY.ID)) != null) {
//						deductions.get(s.get(SALARY.ID)).put(s.get(SALARY_DEDUCTION.DEDUCTION_CONCEPT),
//								s.get(SALARY_DEDUCTION.AMOUNT));
//					} else {
//						Map<String, Double> map = new HashMap<String, Double>();
//						map.put(s.get(SALARY_DEDUCTION.DEDUCTION_CONCEPT), s.get(SALARY_DEDUCTION.AMOUNT));
//						deductions.put(s.get(SALARY.ID), map);
//
//					}
//				});
//
//		Map<String, Map<String, EnterprisePayrollEntry>> map = new HashMap<String, Map<String, EnterprisePayrollEntry>>();
//		ctx.select().from(SALARY).innerJoin(CONTRACT).onKey()
//			.innerJoin(WORKPLACE).onKey().innerJoin(ENTERPRISE).onKey().where(condition).orderBy(WORKPLACE.DESCRIPTION).fetchStream()
//			.forEach(r -> {
//				SalaryType st = typeOf(r.get(SALARY.TYPE), SalaryType.class);
//				
//				Double totalCost = null;
//				Double totalSS = null;
//				
//				try {
//					totalCost = r.get(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS) + r.get(SALARY.TOTAL_ENTERPRISE);
//				} catch (NullPointerException e) {}
//				try {
//					totalSS = totalCost + r.get(SALARY.TOTAL_IRPF);
//				} catch (NullPointerException e) {}
//				
//				EnterprisePayrollEntry entry = new EnterprisePayrollEntry(
//						EnterprisePayrollEntry.EnterpriseEntryType.AON_SYSTEM
//						, r.get(SALARY.EMPLOYEE_NAME)
//						, st.getName(new Locale("es"))
//						, r.get(SALARY.TOTAL_PAYMENT)
//						, r.get(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS)
//						, r.get(SALARY.TOTAL_IRPF)
//						, r.get(SALARY.TOTAL_DEDUCTION)
//						, r.get(SALARY.TOTAL_LIQUID)
//						, r.get(SALARY.TOTAL_ENTERPRISE)
//						, totalCost
//						, totalSS
//						, bonusesMap.get(SALARY.ID));
//				entry.setEmpleado(Optional.ofNullable(r.get(SALARY.EMPLOYEE_NAME)));
//				entry.setTipo(Optional.ofNullable(st.getName(new Locale("es"))));
//				entry.setDevengado(Optional.ofNullable(r.get(SALARY.TOTAL_PAYMENT)));
//				entry.setSsTrab(Optional.ofNullable(r.get(SALARY.SOCIAL_SECURITY_CONTRIBUTIONS)));
//				entry.setIrpf(Optional.ofNullable(r.get(SALARY.TOTAL_IRPF)));
//				entry.setDeducciones(Optional.ofNullable(r.get(SALARY.TOTAL_DEDUCTION)));
//				entry.setLiquido(Optional.ofNullable(r.get(SALARY.TOTAL_LIQUID)));
//				entry.setSsEmpr(Optional.ofNullable(r.get(SALARY.TOTAL_ENTERPRISE)));
//				entry.setSsTotal(Optional.ofNullable(entry.getSsEmpr().orElse(0d) + entry.getSsTrab().orElse(0d)));
//				entry.setCosteTotal(
//						Optional.ofNullable(entry.getIrpf().orElse(0d) + entry.getSsTotal().orElse(0d)));
//				if (!map.containsKey(r.get(WORKPLACE.DESCRIPTION)))
//					map.put(r.get(WORKPLACE.DESCRIPTION), new HashMap<String, EnterprisePayrollEntry>());
//				map.get(r.get(WORKPLACE.DESCRIPTION)).put(String.valueOf(r.get(SALARY.ID)), entry);
//				
//			});
//
//		return map;
//	}
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
