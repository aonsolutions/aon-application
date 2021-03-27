package com.esferalia.aon.gwt.payroll.server;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Connection;
import java.util.Date;

import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.security.Certificate;

import net.aonsolutions.aon.api.servlet.AonApiHttpServlet;
import solutions.aon.seg.social.SistemaRED;

@SuppressWarnings("serial")
@WebServlet(name = "SistemaREDCCC-Servlet", urlPatterns = { "/aon_gwt_payroll/sistema_red_ccc/*" })
public class SistemaREDCCCServlet extends AonApiHttpServlet {
	
	enum RequestType implements Serializable {
		UPDATE_CERT("CertificadoCorrienteTGSS"),
		WORKING_EMPLOYEE("TrabajadoresAlta"),
		PRE_MOV_EMPLOYEE("MovPrevTrabajadores"),
		IDC("Idc")
		;
		
		private String fileName;
		
		private RequestType(String fileName) {
			this.fileName = fileName;
		}
		
		public String getFileName() {
			return this.fileName;
		}
	}
	
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response){
		// Request Type		
		super.doGet(request, response);
		String type = request.getParameter("type");
		Integer typeIdx = Integer.parseInt(type);
		RequestType requestType = RequestType.values()[typeIdx];
		
		// Enterprise CCC
		String regime = request.getParameter("regime");
		String ccc = request.getParameter("ccc");
		
		try {
			// Domian and User
			String userLogin = request.getParameter("userLogin");
			String domainName = request.getParameter("domainName");
			
			Connection connection = AonServletUtils.getConnection(domainName);
			
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName); 
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);
			
			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId);
			final InputStream certificateInputStream =  new ByteArrayInputStream(certificate.getCertificate());
			
			byte[] dataURI = null;
			
			switch (requestType) {
				case UPDATE_CERT:
					dataURI = SistemaRED.getUp2DateSS(certificateInputStream, certificate.getPassword(), certificate.getType(), regime, ccc);
					break;
				case WORKING_EMPLOYEE:
					dataURI = SistemaRED.getReportAffiliateInAlta(certificateInputStream, certificate.getPassword(), certificate.getType(), regime, ccc);
					break;
				case PRE_MOV_EMPLOYEE:
					dataURI = SistemaRED.getReportAffiliateInMovPrev(certificateInputStream, certificate.getPassword(), certificate.getType(), regime, ccc);
					break;
				case IDC:
					dataURI = SistemaRED.getIDCCCC(certificateInputStream, certificate.getPassword(), certificate.getType(), regime, ccc, new Date());
					break;
				default:
					throw new Exception("El tipo introducido es incorrecto.");
			}
		
			ServletOutputStream out = response.getOutputStream();
			out.write(dataURI);
			response.flushBuffer();
		
		} catch (Exception e) {
			error(request, response, e);
		} 
		
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		doGet(request, response);
	}
	
}
