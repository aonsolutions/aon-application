package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.in.payroll.excel.EnterprisePayrollExcel;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.ibm.icu.util.Calendar;

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
		com.esferalia.aon.in.payroll.excel.ExcelType type;
		Collection<Integer> types;
		
		//Picking up the parameters
		{
			if (req.getParameterValues("filter") != null)
				types = Arrays.stream(req.getParameterValues("filter"))
				.map(str -> Integer.parseInt(str))
				.collect(Collectors.toUnmodifiableList());
			else
				types = Collections.unmodifiableList(new ArrayList<Integer>());
			
			enterpriseId = req.getParameter("enterpriseId") != null ? Integer.parseInt(req.getParameter("enterpriseId")) : null;
			workplaceId = req.getParameter("workplaceId") != null ? Integer.parseInt(req.getParameter("workplaceId")) : null;
			month = Integer.parseInt(req.getParameter("month"));
			year = Integer.parseInt(req.getParameter("year"));
			type = com.esferalia.aon.in.payroll.excel.ExcelType.valueOf(req.getParameter("excelType"));
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
			EnterprisePayrollExcel.simpleEnterprisePayrollGenerator(
				req.getServerName()
				, sos
				, Optional.ofNullable(enterpriseId)
				, Optional.ofNullable(workplaceId)
				, startDate
				, type
				, types
				);
			resp.getOutputStream().flush();
			resp.flushBuffer();
		}
		
	}
	
	
	
	
}