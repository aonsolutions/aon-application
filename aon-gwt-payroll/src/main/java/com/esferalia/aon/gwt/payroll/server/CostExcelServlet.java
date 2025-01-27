package com.esferalia.aon.gwt.payroll.server;

import static com.esferalia.aon.gwt.payroll.shared.CostExcelService.Params.DOMAIN;
import static com.esferalia.aon.gwt.payroll.shared.CostExcelService.Params.ENTERPRISE;
import static com.esferalia.aon.gwt.payroll.shared.CostExcelService.Params.EXCEL_TYPE;
import static com.esferalia.aon.gwt.payroll.shared.CostExcelService.Params.MONTH;
import static com.esferalia.aon.gwt.payroll.shared.CostExcelService.Params.USER;
import static com.esferalia.aon.gwt.payroll.shared.CostExcelService.Params.WORKPLACE;
import static com.esferalia.aon.gwt.payroll.shared.CostExcelService.Params.YEAR;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.code.aon.person.Person;
import com.esferalia.aon.gwt.payroll.util.Utilities;
import com.esferalia.aon.in.payroll.excel.EnterprisePayrollExcel;
import com.esferalia.aon.in.payroll.excel.EnterprisePayrollExcel.EnterprisePayrollExcelParams;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
@WebServlet(
		name = "Cost-Excel", 
		urlPatterns = { 
				"/aon_gwt_aio/cost_excel/*" ,
				"/aon_gwt_payroll/cost_excel/*" 
		}
)
public class CostExcelServlet extends HttpServlet {
	private SimpleDateFormat formatter = new SimpleDateFormat("dd_MM_yyyy");
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Integer enterpriseId;
		Integer workplaceId;
		
		String enterpriseName;
		String workplaceName;
		
		Integer month;
		Integer year;
		
		Integer monthEnd;
		Integer yearEnd;
		
		String domainName;
		String user;
		com.esferalia.aon.in.payroll.excel.ExcelType excelType;
		List<com.esferalia.aon.occam.api.model.type.SalaryType> types = new ArrayList<com.esferalia.aon.occam.api.model.type.SalaryType>() ;
		Boolean groupByWorkplace = false;
		
		//Picking up the parameters
		{
			if (req.getParameterValues("filter") != null)
				types = Arrays.stream(req.getParameterValues("filter")).map(str -> {
					Integer ordinal = Integer.parseInt(str);
					com.esferalia.aon.occam.api.model.type.SalaryType type = Utilities.typeOf(ordinal.byteValue(), com.esferalia.aon.occam.api.model.type.SalaryType.class);
					return type;
				}).collect(Collectors.toList());
			else
				types.add(com.esferalia.aon.occam.api.model.type.SalaryType.SALARY);
			
			enterpriseId = AonStringUtils.isNotBlank(req.getParameter(ENTERPRISE.getName())) ? Integer.parseInt(req.getParameter(ENTERPRISE.getName())) : null;
			workplaceId = AonStringUtils.isNotBlank(req.getParameter(WORKPLACE.getName())) ? Integer.parseInt(req.getParameter(WORKPLACE.getName())) : null;
			
			enterpriseName = req.getParameter("enterpriseName");
			workplaceName = req.getParameter("workplaceName");
			
			month = Integer.parseInt(req.getParameter(MONTH.getName()));
			year = Integer.parseInt(req.getParameter(YEAR.getName()));
			
			monthEnd = Integer.parseInt(req.getParameter(MONTH.getName() + "End"));
			yearEnd = Integer.parseInt(req.getParameter(YEAR.getName() + "End"));
			
			excelType = com.esferalia.aon.in.payroll.excel.ExcelType.valueOf(req.getParameter(EXCEL_TYPE.getName()));
			
			domainName = req.getParameter(DOMAIN.getName());
			user = req.getParameter(USER.getName());
			
			groupByWorkplace = Boolean.parseBoolean(req.getParameter("groupByWorkplace"));
			
			if(groupByWorkplace && excelType == com.esferalia.aon.in.payroll.excel.ExcelType.SUMMARY)
				excelType = com.esferalia.aon.in.payroll.excel.ExcelType.PERIOD_SUMMARY;
			else if(groupByWorkplace && excelType == com.esferalia.aon.in.payroll.excel.ExcelType.COMPLETE)
				excelType = com.esferalia.aon.in.payroll.excel.ExcelType.PERIOD_COMPLETE;	
		}
		
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, 1); // The first day of the month has value 1.
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Date startDate = calendar.getTime();
		
		Calendar calendarEnd = Calendar.getInstance();
		calendarEnd.set(Calendar.YEAR, yearEnd);
		calendarEnd.set(Calendar.MONTH, monthEnd);
		calendarEnd.set(Calendar.DAY_OF_MONTH, 1); // The first day of the month has value 1.
		calendarEnd.set(Calendar.HOUR, 0);
		calendarEnd.set(Calendar.MINUTE, 0);
		calendarEnd.set(Calendar.SECOND, 0);
		Date endDate = AonDateUtils.getMonthLastDay(calendarEnd.getTime());
		
		resp.setContentType(MimeType.MS_EXCEL.getName());
		String fileName = "Costes_" + enterpriseName + "_" + (AonStringUtils.isBlank(workplaceName) ? "" : workplaceName + "_") + formatter.format(startDate) + "." +  MimeType.MS_EXCEL_2007.getExtension();
		resp.setHeader("Content-disposition", "attachment; filename=\"" + fileName + "\";");

		try (ServletOutputStream sos = resp.getOutputStream()) {
			EnterprisePayrollExcelParams params = new EnterprisePayrollExcelParams()
					.setDomainName(domainName)
					.setLogin(user)
					.setOs(sos)
					.setEnterpriseId(enterpriseId)
					.setWorkplaceId(workplaceId)
					.setExcelType(excelType);
			
			Person person[] = {};
			
			if(groupByWorkplace) 
				EnterprisePayrollExcel.enterprisePayrollGeneratorByPeriod(params, startDate, endDate, types.toArray(com.esferalia.aon.occam.api.model.type.SalaryType[]::new), person);
			else 
				EnterprisePayrollExcel.enterprisePayrollGeneratorByEmployee(params, startDate, endDate, types.toArray(com.esferalia.aon.occam.api.model.type.SalaryType[]::new), person);
			
				
//			EnterprisePayrollExcel.simpleEnterprisePayrollGenerator(params, startDate, types);
			resp.getOutputStream().flush();
			resp.flushBuffer();
		}
		
	}
	
}