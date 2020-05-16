package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jooq.tools.json.JSONObject;

import com.esferalia.aon.gwt.payroll.jooq.JooqEmployeeAFI;

@SuppressWarnings("serial")
@WebServlet(name = "Employee-AFI", urlPatterns = { "/aon_gwt_payroll/employee_afi/*" })
public class EmployeeAFIServlet extends HttpServlet {
	
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		//Get Request Parametrers
		String _domainId = request.getParameter("domainId");
		String _contractId = request.getParameter("contractId");
		String _workplaceId = request.getParameter("workplaceId");
		Boolean _isStartContract = request.getParameter("isStartContract").equals("1") ? true : false;
		Boolean _isEndContract = request.getParameter("isEndContract").equals("1") ? true : false;
		Boolean _isChangeContract = request.getParameter("isChangeContract").equals("1") ? true : false;
		Boolean _isQuoteContract = request.getParameter("isQuoteContract").equals("1") ? true : false;
		Boolean _isOcupationContract = request.getParameter("isOcupationContract").equals("1") ? true : false;
		
		//Get domain Name
		String _domainName = request.getServerName();
		
		//Este JSON lo deberia obtener del Request cuando me llaman al Servlet
		JSONObject employeeJSON = null;
		
		try {
			Date currentDate = new Date();
			String day = currentDate.getDate() < 10 ? "0"+currentDate.getDate() : currentDate.getDate()+"";
			String month = (currentDate.getMonth()+1) < 10 ? "0"+(currentDate.getMonth()+1) : (currentDate.getMonth()+1)+"";
			String hour = currentDate.getHours() < 10 ? "0"+currentDate.getHours() : currentDate.getHours()+"";
			String minutes = currentDate.getMinutes() < 10 ? "0"+currentDate.getMinutes() : currentDate.getMinutes()+"";
			String fileName = day + month + hour + minutes;
			dateFormatter.applyPattern("yyyy/MM/dd");
			response.setContentType("text/html;charset=utf-8"/*MimeType.MIME_RTF.getName()*/);
			response.setHeader("Content-disposition", "attachment; filename=\""
					+ fileName + ".AFI\"");
			
			ServletOutputStream output = response.getOutputStream();
			
//			output.write("Generando AFI Employee".getBytes());
			
			employeeJSON = JooqEmployeeAFI.getEmployeeAFIInfo(_domainId, _domainName, _contractId, _workplaceId, _isStartContract, _isEndContract, _isChangeContract, _isQuoteContract, _isOcupationContract);
			String employeeAFI = EmployeeAFIGeneration.generateEmployeeAFI(employeeJSON);
			output.write(employeeAFI.getBytes());
			
			response.flushBuffer();
		
		}catch (IOException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
		
	}

}
