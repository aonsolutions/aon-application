package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;
import java.util.Optional;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.code.aon.common.enumeration.MimeType;
import com.esferalia.aon.gwt.payroll.shared.PayrollPrintService;
import com.esferalia.aon.in.payroll.pdf.jooq.JooqPayrollBuilder;
import com.esferalia.aon.watson.util.AonStringUtils;

//http://ayudat.aonsolutions.net:8080/aon-aio/aon_gwt_payroll//print_payroll/

//@SuppressWarnings("serial")
//@WebServlet(name = "Salary-PDF", 
//	urlPatterns = { 
//			"/aon_gwt_aio/salary_exporter/*",
//			"/aon_gwt_payroll/salary_exporter/*" 
//	})
@SuppressWarnings("serial")
@WebServlet(name = "Salary-MacLeod", 
	urlPatterns = { 
			"/aon_gwt_aio/salary_connor_macleod/*",
			"/aon_gwt_payroll/salary_connor_macleod/*" 
	})
public class PayrollPrintServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
	}
	
	
	
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Integer[] ids = new Integer[req.getParameterValues("id").length];
		for (int i = 0; i < ids.length; i++)
			ids[i] = Integer.parseInt(req.getParameterValues("id")[i]);
		resp.setContentType(MimeType.MIME_PDF.getName());
		String cLimitStr = req.getParameter(PayrollPrintService.Parameter.COMPLEMENTARY_LIMIT.getName());
		Double cLimit = null;
		try {
			if (cLimitStr != null) {
				cLimit = Double.parseDouble(cLimitStr);
			}
		} catch (NumberFormatException e) {
			cLimit = null;
		}
		String payrollType = (String) req.getAttribute(PayrollPrintService.Parameter.PAYROLL_TYPE.getName());
		resp.setHeader("Content-disposition", "attachment; filename=\""+req.getParameter("name")+"\";");
		
		if (AonStringUtils.equalsIgnoreCase(payrollType, PayrollPrintService.PayrollType.CLASSIC.getName())) {
			JooqPayrollBuilder.generateClassicPayroll(Integer.parseInt(req.getParameter(PayrollPrintService.Parameter.ENTERPRISE.getName()))
					, req.getParameter(PayrollPrintService.Parameter.DOMAIN.getName())
					, resp.getOutputStream()
					, Optional.ofNullable(cLimit)
					, ids);
		} else {
			JooqPayrollBuilder.generatePayroll(Integer.parseInt(req.getParameter(PayrollPrintService.Parameter.ENTERPRISE.getName()))
					, req.getParameter(PayrollPrintService.Parameter.DOMAIN.getName())
					, resp.getOutputStream()
					, Optional.ofNullable(cLimit)
					, ids);
		}
		
	}
}
