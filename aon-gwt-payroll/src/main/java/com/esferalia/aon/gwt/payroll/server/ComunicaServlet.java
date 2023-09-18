package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import jakarta.servlet.ServletException;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.code.aon.common.enumeration.MimeType;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.watson.util.AonStringUtils;

import solutions.aon.seg.social.exception.SegSocialException;

@MultipartConfig
@SuppressWarnings("serial")
@WebServlet(name = "Comunica-File-Servlet", urlPatterns = { "/aon_gwt_payroll/comunica_file/*" })
public class ComunicaServlet extends HttpServlet {
	
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
			
			// Get employee document
			String document = req.getParameter("document");
			
			// Get file type
			String fileType = req.getParameter("fileType");
			
			// Get resources
			Connection connection = AonServletUtils.getConnection(domainName);
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, currentUser, domainId, parentDomainId);
			
			// Set MimeType and header
			res.setContentType(MimeType.MIME_PDF.getName());
			res.setHeader("Content-disposition", "attachment; filename=\"" + fileType + "_" + document + ".pdf\"");
			
			// Get Servlet outputStream
			ServletOutputStream output = res.getOutputStream();
			
			// Try to get fileType
			String base64Pdf = "";
			
			if(AonStringUtils.equalsIgnoreCase(fileType, "IDC"))
				base64Pdf = EmployeesServiceHelper.getIDC(connection, domainName, domainId, currentUser, userId, contractId, new Date());
			
			if(AonStringUtils.equalsIgnoreCase(fileType, "TA"))
				base64Pdf = EmployeesServiceHelper.getTA(connection, domainName, domainId, currentUser, userId, contractId);
			
			output.write(base64Pdf.getBytes());
			res.flushBuffer();
		
		}catch (IOException | SQLException | SegSocialException e) {
			throw new IllegalArgumentException(e.getMessage(), e);
		}
		
	}

}
