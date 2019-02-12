package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.faces.event.AbortProcessingException;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jooq.tools.json.JSONObject;

import com.code.aon.common.enumeration.MimeType;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.payroll.jooq.JooqAgrarian;

@SuppressWarnings("serial")
@WebServlet(name = "Agrarian-AFI", urlPatterns = { "/aon_gwt_payroll/agrarian_afi/*" })
public class AgrarianAFIServlet extends HttpServlet {
	
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
		String _selectedEmployees = request.getParameter("selectedEmployees");
		ArrayList<Integer> _selectedContracts = new ArrayList<>();
		
		Integer numEmployee = Integer.parseInt(_selectedEmployees);
		if(0 != numEmployee) {
			for(int i=0; i<numEmployee; i++) {
				_selectedContracts.add(Integer.parseInt(request.getParameter("employee"+i+"Id")));
			}
		}
		
		//Este JSON lo deberia obtener del Request cuando me llaman al Servlet
		JSONObject agrarianJSON = null; 
		
		//Get domain Name
		String domainName = AonServletUtils.getRequestDomainName(request);
		
		try {
			String fileName = "agrarianAFI";
			dateFormatter.applyPattern("yyyy/MM/dd");
			response.setContentType(MimeType.MIME_RTF.getName());
			response.setHeader("Content-disposition", "attachment; filename=\""
					+ fileName + ".afi\"");
			ServletOutputStream output = response.getOutputStream();
			
			if(0 == numEmployee)
				output.write("Fallo al crear el archivo -> No hay empleados seleccionados".getBytes());
			else {
//				output.write("Generando archivo AFI".getBytes());
				agrarianJSON = JooqAgrarian.getAgrarianInfo(_domainId, domainName, _enterpriseId, _enterpriseName, _ccc, _startDate, _endDate, _selectedContracts);
				String agrarianAFI = AgrarianAFIGeneration.generateAgrarianAFI(agrarianJSON);
				output.write(agrarianAFI.getBytes());
			}
			
			response.flushBuffer();
		
		}catch (IOException e) {
			throw new AbortProcessingException(e.getMessage(), e);
		}
		
	}

}
