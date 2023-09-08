package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.jooq.tools.json.JSONObject;

import com.esferalia.aon.gwt.payroll.jooq.JooqEmployeeAFI;
import com.esferalia.aon.watson.util.AonStringUtils;

@SuppressWarnings("serial")
@WebServlet(name = "Employee-AFI", urlPatterns = { "/aon_gwt_payroll/employee_afi/*" })
public class EmployeeAFIServlet extends HttpServlet {
	
	private final SimpleDateFormat dateFormatter = new SimpleDateFormat("ddHHmmss");
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//Get domain Name
		String domainName = request.getServerName();		
		
		//Get Request Parametrers
		String domainId = request.getParameter("domainId");
		String contractId = request.getParameter("contractId");
		Boolean isStartContract = AonStringUtils.equalsIgnoreCase(request.getParameter("isStartContract"), "1");
		Boolean isEndContract = AonStringUtils.equalsIgnoreCase(request.getParameter("isEndContract"), "1");
		Boolean isChangeContract = AonStringUtils.equalsIgnoreCase(request.getParameter("isChangeContract"), "1");
		Boolean isQuoteContract = AonStringUtils.equalsIgnoreCase(request.getParameter("isQuoteContract"), "1");
		Boolean isOcupationContract = AonStringUtils.equalsIgnoreCase(request.getParameter("isOcupationContract"), "1");
		Boolean isPartialityCoefContract = AonStringUtils.equalsIgnoreCase(request.getParameter("isPartialityCoefContract"), "1");
		Boolean isCnoContract = AonStringUtils.equalsIgnoreCase(request.getParameter("isCnoContract"), "1");
		String settleReason = request.getParameter("settleReason");
		
		//Este JSON lo deberia obtener del Request cuando me llaman al Servlet
		JSONObject employeeJSON = null;
		
		try {
			Calendar currentDate = Calendar.getInstance();
			
			String fileName = dateFormatter.format(currentDate.getTime());
			
			response.setContentType("text/html;charset=utf-8");
			response.setHeader("Content-disposition", "attachment; filename=\""+ fileName + ".AFI\"");
			
			ServletOutputStream output = response.getOutputStream();
			
			employeeJSON = JooqEmployeeAFI.getEmployeeAFIInfo(
					domainId,
					domainName, 
					contractId,
					fileName,
					isStartContract, 
					isEndContract, 
					isChangeContract, 
					isQuoteContract, 
					isOcupationContract, 
					isPartialityCoefContract,
					isCnoContract,
					settleReason);
			
			
			String employeeAFI = EmployeeAFIGeneration.generateEmployeeAFI(employeeJSON);
			
			output.write(employeeAFI.getBytes());
			response.flushBuffer();
		
		} catch (IOException e) {
			// Catch Exception
		}
		
	}

}
