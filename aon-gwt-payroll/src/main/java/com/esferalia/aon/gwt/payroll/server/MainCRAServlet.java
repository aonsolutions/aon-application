package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.faces.event.AbortProcessingException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jooq.tools.json.JSONObject;

import com.esferalia.aon.gwt.common.server.AonServletUtils;

@SuppressWarnings("serial")
@WebServlet(name = "Main-CRA", urlPatterns = { "/aon_gwt_payroll/main_cra/*" })
public class MainCRAServlet extends HttpServlet {
	
	private SimpleDateFormat dateFormatter = new SimpleDateFormat("dd/MM/yyyy");
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		//Get Request Parametrers
		String _domainId = request.getParameter("domainId");
		String _enterpriseId = request.getParameter("enterpriseId");
		String _enterpriseName = request.getParameter("enterpriseName");
		String _startDate = request.getParameter("startDate");
		String _endDate = request.getParameter("endDate");
		String _ccc = request.getParameter("ccc");
		
		//Este JSON lo deberia obtener del Request cuando me llaman al Servlet
		JSONObject agrarianJSON = null; 
		
		//Get domain Name
		String domainName = AonServletUtils.getRequestDomainName(request);
		
		try {
			Date currentDate = new Date();
			String day = currentDate.getDate() < 10 ? "0"+currentDate.getDate() : currentDate.getDate()+"";
			String month = (currentDate.getMonth()+1) < 10 ? "0"+(currentDate.getMonth()+1) : (currentDate.getMonth()+1)+"";
			String hour = currentDate.getHours() < 10 ? "0"+currentDate.getHours() : currentDate.getHours()+"";
			String minutes = currentDate.getMinutes() < 10 ? "0"+currentDate.getMinutes() : currentDate.getMinutes()+"";
			String fileName = day + month + hour + minutes;
			dateFormatter.applyPattern("yyyy/MM/dd");
			response.setContentType("text/html;charset=utf-8");
			response.setHeader("Content-disposition", "attachment; filename=\""
					+ fileName + ".CRA\"");
			ServletOutputStream output = response.getOutputStream();
			
			output.write("Fallo al crear el archivo -> No hay empleados seleccionados".getBytes());
			
//			agrarianJSON = JooqAgrarian.getAgrarianInfo(_domainId, domainName, _enterpriseId, _enterpriseName, _ccc, _startDate, _endDate, _selectedContracts);
//			String agrarianAFI = AgrarianAFIGeneration.generateAgrarianAFI(agrarianJSON);
//			output.write(agrarianAFI.getBytes());
			
			
			response.flushBuffer();
		
		}catch (IOException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
		
	}

}
