package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.gwt.payroll.jooq.JooqContrataContract; 

@MultipartConfig
@SuppressWarnings("serial")
@WebServlet(name = "CONTRACT-EXPORT", urlPatterns = { "/aon_gwt_payroll/contract_export/*" })
public class ContractExportServlet extends HttpServlet {
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		// Get Attach Id
		String contractTypeStr = req.getParameter("contractType");
		
		// Get extension
		String domainName = req.getParameter("domainName");
		
		String employeeFullName = req.getParameter("employeeFullName");
		
		// Get Attach Id
		String contractIdStr = req.getParameter("contractId");
		Integer contractId = Integer.parseInt(contractIdStr);
		
		// Formative Level
		String formativeLevelCode = req.getParameter("formativeLevel");
		
		// Make sure to show the download dialog
        res.setHeader("Content-Disposition", "attachment; filename=\"" + employeeFullName + ".pdf\"");
        
        String fileType = "application/pdf";
        res.setContentType(fileType);
		
		try {
			ServletOutputStream output = res.getOutputStream();
			byte[] data = JooqContrataContract.contractFill(domainName, contractId, contractTypeStr, formativeLevelCode);
			output.write(data);
			res.flushBuffer();
		} catch (Exception e) {}
	}

}
