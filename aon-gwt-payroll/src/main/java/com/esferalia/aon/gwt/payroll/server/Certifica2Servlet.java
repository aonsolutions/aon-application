package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.common.enumeration.MimeType;
import com.esferalia.aon.gwt.payroll.jooq.JooqCertifica2;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.watson.util.AonStringUtils;

@MultipartConfig
@SuppressWarnings("serial")
@WebServlet(name = "Certifica2-Servlet", urlPatterns = { "/aon_gwt_payroll/certifica2/*" })
public class Certifica2Servlet extends HttpServlet {
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		doGet(req, res);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			
			// Get currentUser
			String currentUser = req.getParameter("currentUser");
			
			// Get currentUser
			String domainName = req.getParameter("currentDomain");
			
			if(AonStringUtils.isEmpty(currentUser)) {
				String token = req.getParameter("token");
				Domain domain = AON.getDomain(domainName, 0, "", f -> f.getNameProperty().eq(domainName));
				currentUser = AON_SOLUTIONS.getUser(domain, token).getLogin();
			}
			
			// Get contractId
			String contractIdStr = req.getParameter("contractId");
			Integer contractId = Integer.parseInt(contractIdStr);
			
			// Get employee documente
			String document = req.getParameter("document");
			
			// Set MimeType and header
			res.setContentType(MimeType.MIME_XML.getName());
			res.setHeader("Content-disposition", "attachment; filename=\"Certifica2_" + document + ".xml\"");
			
			// Get Servlet outputStream
			ServletOutputStream output = res.getOutputStream();
			
			// Try to get Certifica2
			byte[] data = JooqCertifica2.getCertitica2Data(domainName, contractId);
			
			// If not exist, create it and get it
			if(null == data) {
				JooqCertifica2.generateCertifica2(domainName, contractId);
				data = JooqCertifica2.getCertitica2Data(domainName, contractId);
			}
			
			output.write(data);
			res.flushBuffer();
		
		}catch (IOException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
		
	}

}
