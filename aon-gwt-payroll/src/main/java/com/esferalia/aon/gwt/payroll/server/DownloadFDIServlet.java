package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.payroll.tgss.fdi.FDI;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
@WebServlet(name = "Download-FDI", urlPatterns = { "/aon_gwt_payroll/download_fdi/*" })
public class DownloadFDIServlet extends HttpServlet {
	
	private final SimpleDateFormat dateFormatter = new SimpleDateFormat("ddMMHHmm");
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//Get Request Parametrers
		Integer contractId = Integer.parseInt( request.getParameter("contract") );
		Integer contractLeaveId = Integer.parseInt( request.getParameter("it") );
		
		String quoteDays = request.getParameter("quoteDays");
		
		//Get domain Name
		String domainName = request.getServerName();
		
		try {
			String fileName = dateFormatter.format(new Date());
			response.setContentType("text/html;charset=utf-8");
			response.setHeader("Content-disposition", "attachment; filename=\"" + fileName + ".FDI\"");
			
			// Output & Connection
			ServletOutputStream output = response.getOutputStream();
			Connection connection = AonServletUtils.getConnection(domainName);
			
			String fieFile = FDI.getFDI(connection, contractId, contractLeaveId, quoteDays, fileName);
			
			output.write(fieFile.getBytes());
			
			response.flushBuffer();
		
		} catch (Exception e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
		
	}

}
