package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.gwt.payroll.shared.AgrarianAFIGeneration;
import com.google.gwt.json.client.JSONObject;

@WebServlet(name = "Agrarian-AFI", urlPatterns = { "/aon_gwt_payroll/reports/agrarian_afi" })
public class AgrarianAFIServlet extends HttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		
		//Este JSON lo deberia obtener del Request cuando me llaman al Servlet
		JSONObject agrarianJSON = null; 
		
		String agrarianAFI = AgrarianAFIGeneration.generateAgrarianAFI(agrarianJSON);
		
		OutputStream os = resp.getOutputStream();
		if(agrarianAFI == null)
			os.write("Fallo al crear el archivo".getBytes());
		else
			os.write(agrarianAFI.getBytes());
		
		os.flush();
	}

}
