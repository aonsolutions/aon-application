package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.common.enumeration.MimeType;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.payroll.shared.PayrollPrintService;
import com.esferalia.aon.gwt.payroll.util.JooqPayrollBuilder;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.Enterprise;
import com.google.api.client.util.Base64;

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
		System.out.println(Arrays.toString(ids));
		resp.setContentType(MimeType.MIME_PDF.getName());
//		resp.setHeader("Content-disposition", "attachment; filename=\""+req.getParameter("name")+"\";");
		JooqPayrollBuilder.generatePayroll(req.getParameter(PayrollPrintService.Parameter.DOMAIN.getName())
				, resp.getOutputStream()
				, Integer.parseInt(req.getParameter(PayrollPrintService.Parameter.ENTERPRISE.getName()))
				, ids);
		
	}
}
