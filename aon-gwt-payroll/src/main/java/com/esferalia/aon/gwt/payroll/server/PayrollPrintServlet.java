package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;
import java.util.ArrayList;
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
import com.esferalia.aon.gwt.payroll.util.JooqPayrollBuilder;
import com.google.api.client.util.Base64;

//http://ayudat.aonsolutions.net:8080/aon-aio/aon_gwt_payroll//print_payroll/

//@SuppressWarnings("serial")
//@WebServlet(name = "Salary-PDF", 
//	urlPatterns = { 
//			"/aon_gwt_aio/salary_exporter/*",
//			"/aon_gwt_payroll/salary_exporter/*" 
//	})
public class PayrollPrintServlet extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		doPost(req, resp);
//		
//		
//		String requestURI = req.getRequestURI();
//		String salaryRequestStr = AonServletUtils.getFileName(requestURI);
//		String paramsStr = decode(salaryRequestStr.getBytes());
//		Map<String, String> params = createParams(paramsStr);
//		String fileName = params.get("name");
//		
//		Integer selectedSalaries = Integer.parseInt(params.get("selectedSalaries"));
//		resp.setContentType(MimeType.MIME_PDF.getName());
//		resp.setHeader("Content-disposition", "attachment; filename=\""+fileName+"\";");
//		JooqPayrollBuilder.generatePayroll(req.getServerName(), resp.getOutputStream(), getSalaryIds(params, selectedSalaries).toArray(new Integer[selectedSalaries]));
	}
	
	
	
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		Integer[] ids = new Integer[req.getParameterValues("id").length];	
		for (int i = 0; i < ids.length; i++)
			ids[i] = Integer.parseInt(req.getParameterValues("id")[i]);
		
		resp.setContentType(MimeType.MIME_PDF.getName());
//		resp.setHeader("Content-disposition", "attachment; filename=\""+req.getParameter("name")+"\";");
		JooqPayrollBuilder.generatePayroll(req.getServerName(), resp.getOutputStream(), ids);
		
	}




	private Collection<Integer> getSalaryIds(Map<String, String> params, Integer selectedSalaries) {
		ArrayList<Integer> _selectedSalaries = new ArrayList<>();

		if(0 != selectedSalaries) {
			for(int i=0; i<selectedSalaries; i++) {
				String idString = params.get("salary"+i+"Id");
				
				if(idString.contains("."))
					idString = idString.split("\\.")[0];
				
				_selectedSalaries.add(Integer.parseInt(idString));
			}
		}
		return _selectedSalaries;
	}
	
	private Map<String, String> createParams(String paramsStr) {
		HashMap<String, String> paramsMap = new HashMap<String, String>();
		
		String params = paramsStr.substring(1);
		
		String[] paramsArr = params.split("&");
		for(int i=0; i < paramsArr.length; i++) {
			String key = paramsArr[i].split("=")[0];
			String value = paramsArr[i].split("=")[1];
			
			paramsMap.put(key, value);
		}
		
		return paramsMap;
	}
	
	public String decode(byte[] value){
		String decode = "";
		decode = new String(Base64.decodeBase64(value));
		System.out.println(decode);
		return decode;
	}

}
