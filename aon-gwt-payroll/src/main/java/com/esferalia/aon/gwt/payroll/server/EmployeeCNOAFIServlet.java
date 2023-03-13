package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jooq.tools.json.JSONObject;

import com.esferalia.aon.gwt.payroll.jooq.JooqEmployeeAFI;

@SuppressWarnings("serial")
@WebServlet(name = "Employee-CNO-AFI", urlPatterns = { "/aon_gwt_payroll/employee_cno_afi/*" })
public class EmployeeCNOAFIServlet extends HttpServlet {
	
	private final SimpleDateFormat dateFormatter = new SimpleDateFormat("ddHHmmss");
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//Get domain Name
		String domainName = request.getServerName();		
		
		//Get Request Parametrers
		Integer domainId = Integer.parseInt(request.getParameter("domainId"));
		
		//Este JSON lo deberia obtener del Request cuando me llaman al Servlet
		JSONObject employeesJSON = null;
		
		try {
			Calendar currentDate = Calendar.getInstance();
			
			String fileName = dateFormatter.format(currentDate.getTime());
			
			response.setContentType("text/html;charset=utf-8");
			response.setHeader("Content-disposition", "attachment; filename=\""+ fileName + ".AFI\"");
			
			ServletOutputStream output = response.getOutputStream();
			
			employeesJSON = JooqEmployeeAFI.getEmployeesCNOAFIInfo(
					domainId,
					domainName, 
					fileName);
			
			
			String employeeAFI = EmployeeAFIGeneration.generateEmployeesCNOAFI(employeesJSON);
			
			output.write(employeeAFI.getBytes());
			response.flushBuffer();
		
		} catch (IOException e) {
			// Catch Exception
		}
		
	}

}
