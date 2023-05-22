package com.esferalia.aon.gwt.payroll.server;

import static com.esferalia.aon.gwt.payroll.shared.CostExcelService.Params.DOMAIN;
import static com.esferalia.aon.gwt.payroll.shared.CostExcelService.Params.ENTERPRISE;
import static com.esferalia.aon.gwt.payroll.shared.CostExcelService.Params.EXCEL_TYPE;
import static com.esferalia.aon.gwt.payroll.shared.CostExcelService.Params.MONTH;
import static com.esferalia.aon.gwt.payroll.shared.CostExcelService.Params.USER;
import static com.esferalia.aon.gwt.payroll.shared.CostExcelService.Params.WORKPLACE;
import static com.esferalia.aon.gwt.payroll.shared.CostExcelService.Params.YEAR;
import static com.esferalia.aon.gwt.payroll.shared.CostExcelService.Params.FILTER;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.esferalia.aon.in.payroll.excel.EnterprisePayrollExcel;
import com.esferalia.aon.in.payroll.excel.EnterprisePayrollExcel.EnterprisePayrollExcelParams;
import com.esferalia.aon.occam.api.model.type.MimeType;
import java.util.Calendar;

@SuppressWarnings("serial")
@WebServlet(
		name = "Cost-Excel", 
		urlPatterns = { 
				"/aon_gwt_aio/cost_excel/*" ,
				"/aon_gwt_payroll/cost_excel/*" 
		}
)
public class CostExcelServlet extends HttpServlet {
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Integer enterpriseId;
		Integer workplaceId;
		Integer month;
		Integer year;
		String domainName;
		String user;
		com.esferalia.aon.in.payroll.excel.ExcelType type;
		Collection<Integer> types;
		
		//Picking up the parameters
		{
			if (req.getParameterValues(FILTER.getName()) != null)
				types = Arrays.stream(req.getParameterValues(FILTER.getName()))
				.map(str -> Integer.parseInt(str))
				.collect(Collectors.toList());
			else
				types = Collections.unmodifiableList(new ArrayList<Integer>());
			
			enterpriseId = req.getParameter(ENTERPRISE.getName()) != null ? Integer.parseInt(req.getParameter(ENTERPRISE.getName())) : null;
			workplaceId = req.getParameter(WORKPLACE.getName()) != null ? Integer.parseInt(req.getParameter(WORKPLACE.getName())) : null;
			month = Integer.parseInt(req.getParameter(MONTH.getName()));
			year = Integer.parseInt(req.getParameter(YEAR.getName()));
			type = com.esferalia.aon.in.payroll.excel.ExcelType.valueOf(req.getParameter(EXCEL_TYPE.getName()));
			domainName = req.getParameter(DOMAIN.getName());
			user = req.getParameter(USER.getName());
		}
		
		
		resp.setContentType(MimeType.MS_EXCEL.getName());
		resp.setHeader("Content-disposition", "attachment; filename=\"Costes."+ MimeType.MS_EXCEL_2007.getExtension()+ "\";");

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month);
		calendar.set(Calendar.DAY_OF_MONTH, 1); // The first day of the month has value 1.
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		Date startDate = calendar.getTime();
		try (ServletOutputStream sos = resp.getOutputStream()) {
			EnterprisePayrollExcelParams params = new EnterprisePayrollExcelParams()
					.setDomainName(domainName)
					.setLogin(user)
					.setOs(sos)
					.setEnterpriseId(enterpriseId)
					.setWorkplaceId(workplaceId)
					.setExcelType(type);
			
			EnterprisePayrollExcel.simpleEnterprisePayrollGenerator(params, startDate, types);
			resp.getOutputStream().flush();
			resp.flushBuffer();
		}
		
	}
	
}