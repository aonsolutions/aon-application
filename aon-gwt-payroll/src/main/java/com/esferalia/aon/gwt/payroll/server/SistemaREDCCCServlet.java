package com.esferalia.aon.gwt.payroll.server;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.security.Certificate;

import net.aonsolutions.aon.api.ewok.AonApiData;
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
		// super.doGet(request, response);
		AonApiData api = initialize(request, false); // Provisional: false para que no compruebe el token
		String type = api.getData().get("type").toString();
		Integer typeIdx = Integer.parseInt(type);
		RequestType requestType = RequestType.values()[typeIdx];
		
		// Enterprise CCC
		String regime = api.getData().get("regime").toString();
		String ccc = api.getData().get("ccc").toString();
		Connection connection = null;
		try {
			// Domian and User
			String userLogin = api.getData().get("domain_login").toString();
			String domainName = api.getData().get("domain_name").toString();
			
			connection = AonServletUtils.getConnection(domainName);
			
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName); 
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);
			
			// Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId);
			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "TGSS");
			
			byte[] dataURI = null;
			
			switch (requestType) {
				case UPDATE_CERT:
					dataURI = SistemaRED.getUp2DateSS(certificate.getCertificate(), certificate.getPassword(), certificate.getType(), regime, ccc);
					break;
				case WORKING_EMPLOYEE:
					dataURI = SistemaRED.getReportAffiliateInAlta(certificate.getCertificate(), certificate.getPassword(), certificate.getType(), regime, ccc);
					break;
				case PRE_MOV_EMPLOYEE:
					dataURI = SistemaRED.getReportAffiliateInMovPrev(certificate.getCertificate(), certificate.getPassword(), certificate.getType(), regime, ccc);
					break;
				case IDC:
					dataURI = SistemaRED.getIDCCCC(certificate.getCertificate(), certificate.getPassword(), certificate.getType(), regime, ccc, new Date());
					break;
				default:
					throw new Exception("El tipo introducido es incorrecto.");
			}
			
			// Prepare response
			response.setContentType("application/pdf");
			response.setHeader("Content-Disposition", "attachment;filename=" + requestType.getFileName() + ".pdf");
		
			ServletOutputStream out = response.getOutputStream();
			out.write(dataURI);
			response.flushBuffer();
		
		} catch (Exception e) {
			error(request, response, e);
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		doGet(request, response);
	}
	
}
