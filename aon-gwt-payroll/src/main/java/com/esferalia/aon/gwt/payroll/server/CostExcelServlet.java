package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.gwt.payroll.shared.ExcelType;
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
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String excelType = request.getParameter("excelType");
		String _domainName = request.getServerName();
		String _month = request.getParameter("month");
		String _year = request.getParameter("year");
		String _enterpriseId = request.getParameter("enterpriseId");
		String _workplaceId = request.getParameter("workplaceId");
//		String _salary = request.getParameter("salary");
//		String _extra = request.getParameter("extra");
//		String _settle = request.getParameter("settle");
//		String _delay = request.getParameter("delay");

		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, Integer.parseInt(_year));
		calendar.set(Calendar.MONTH, Integer.parseInt(_month));
		calendar.set(Calendar.DAY_OF_MONTH, 1);
		
		Date startDate = calendar.getTime();
		
		response.setContentType(MimeType.MS_EXCEL.getName());
		response.setHeader("Content-disposition", "attachment; filename=\"Costes."+ MimeType.MS_EXCEL_2007.getExtension()+ "\";");

		try (ServletOutputStream sos = response.getOutputStream()) {
			ExcelType type = ExcelType.SUMMARY;
			if (excelType != null && excelType.equals("complete"))
				type = ExcelType.COMPLETE;
			Integer wpId = null;
			Integer eId = null;
			try {
				wpId = Integer.parseInt(_workplaceId);
				eId = Integer.parseInt(_enterpriseId);
			}
			catch (NullPointerException | NumberFormatException e) {}
			
			EnterprisePayrollExcel.simpleEnterprisePayrollGenerator(
					_domainName
					, sos
					, Optional.ofNullable(eId)
					, Optional.ofNullable(wpId)
					, startDate
					, com.esferalia.aon.in.payroll.excel.ExcelType.valueOf(type.name())
					);
			
			sos.flush();
			response.flushBuffer();
		}
	}
	
	
}