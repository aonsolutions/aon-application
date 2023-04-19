package com.esferalia.aon.gwt.payroll.server;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.code.aon.common.enumeration.MimeType;
import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.gwt.payroll.jooq.JooqCertifica2;
import com.esferalia.aon.gwt.payroll.jooq.JooqContractAttach;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.security.CertificateNotFoundException;
import com.esferalia.aon.watson.util.AonStringUtils;

import solutions.aon.sepe.Sepe;
import solutions.aon.sepe.exceptions.SepeException;

@MultipartConfig
@SuppressWarnings("serial")
@WebServlet(name = "Certifica2-Servlet", urlPatterns = { "/aon_gwt_payroll/certifica2/*" })
public class Certifica2Servlet extends HttpServlet {
	
	private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			doGet(req, res);
		} catch (Exception e) {
			// Nothing to do here
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		try {
			// Get currentUser
			String userLogin = req.getParameter("userLogin");
			
			// Get currentUser
			String domainName = req.getParameter("currentDomain");
			
			// Get contractId
			String contractIdStr = req.getParameter("contractId");
			Integer contractId = Integer.parseInt(contractIdStr);
			
			// Get employee document
			String document = req.getParameter("document");
			
			// Get employee type
			String type = req.getParameter("type");
			
			// Get suspensionReasonCode
			String suspensionReasonCode = req.getParameter("suspensionReasonCode");
			
			// Get suspensionReason
			String suspensionReason = req.getParameter("suspensionReason");
			
			// Get Servlet outputStream
			ServletOutputStream output = res.getOutputStream();	
			
			byte[] data = new byte[0];
			
			// Set MimeType and header
			if(AonStringUtils.equalsIgnoreCase(type, "XML")) {
				res.setContentType(MimeType.MIME_XML.getName());
				res.setHeader("Content-disposition", "attachment; filename=\"Certifica2_" + document + ".xml\"");
				
				// Try to get Certifica2
				data = JooqCertifica2.getCertitica2Data(domainName, contractId);
				
				// If not exist, create it and get it
				if(null == data) {
					JooqCertifica2.createCertifica2DBServlet(domainName, userLogin, contractId);
					data = JooqCertifica2.getCertitica2Data(domainName, contractId);
				}
				
			} else if(AonStringUtils.equalsIgnoreCase(type, "PDF")) {
				res.setContentType(MimeType.MIME_PDF.getName());
				res.setHeader("Content-disposition", "attachment; filename=\"Certifica2_" + document + ".pdf\"");
				
				// Get employee endDate
				String endDateStr = req.getParameter("endDate");
				Date endDate = formatEndDate(endDateStr);
				
				data = getCertEnterprisePDF(domainName, userLogin, contractId, document, endDate);
			} else if(AonStringUtils.equalsIgnoreCase(type, "MANUAL")) {
				res.setContentType(MimeType.MIME_PDF.getName());
				res.setHeader("Content-disposition", "attachment; filename=\"Certifica2_" + document + ".pdf\"");
				
				data = JooqCertifica2.createCertEnterprisePDF(domainName, userLogin, contractId, suspensionReasonCode, suspensionReason);
			}
			
			res.setStatus(HttpServletResponse.SC_OK);
			output.write(data);
			output.flush();
			output.close();
		
		} catch (IOException | NumberFormatException e) {
			e.printStackTrace();
		}
		
	}

	private byte[] getCertEnterprisePDF(String domainName, String userLogin, Integer contractId, String document, Date endDate) {
		try (Connection connection = AonServletUtils.getConnection(domainName)) {
			// Get domain id
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName);
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);

			// Copy Contract
			byte[] pdfBytes = JooqContractAttach.getCertifica2PDF(connection, contractId);

			// If not exist download
			if (null == pdfBytes) {
				Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "SEPE");
				pdfBytes = Sepe.certEnterprisePdf(new ByteArrayInputStream(certificate.getData()),
						certificate.getPassword(), certificate.getType(), document, endDate);

				JooqContractAttach.setCertifica2PDF(connection, domainId, contractId, pdfBytes);
			}

			return pdfBytes;
			
		} catch (CertificateNotFoundException e) {
			throw new IllegalArgumentException(
					"No existe certificado SEPE. Por favor introduzcalo desde el apartado Gesti\u00F3n Certificados");
		} catch (SQLException | SepeException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	private Date formatEndDate(String endDateStr) {
		try {
			return dateFormat.parse(endDateStr);
		} catch (ParseException e) {
			e.printStackTrace();
			throw new IllegalArgumentException(e);
		}
	}

}
