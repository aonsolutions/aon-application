package com.esferalia.aon.gwt.payroll.server;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;

import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.json.IJsonNames;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.security.Certificate;
import com.esferalia.aon.occam.api.model.type.MimeType;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
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
		
		JSONObject params = api.getData();

		//PARAMS
		String type = params.optString(IJsonNames.TYPE);
		String regime = params.optString(IJsonNames.REGIME);
		String ccc = params.optString("ccc");

		try {
	
			Certificate certificate = AON.getCertificate(api.getDomain(), api.getUser(), "TGSS");
			
			byte[] dataURI = null;
			
			Integer typeIdx = Integer.parseInt(type);
			RequestType requestType = RequestType.values()[typeIdx];

			switch (requestType) {
				case UPDATE_CERT:
					dataURI = SistemaRED.getUp2DateSS(certificate.getData(), certificate.getPassword(), certificate.getType(), regime, ccc);
					break;
				case WORKING_EMPLOYEE:
					dataURI = SistemaRED.getReportAffiliateInAlta(certificate.getData(), certificate.getPassword(), certificate.getType(), regime, ccc);
					break;
				case PRE_MOV_EMPLOYEE:
					dataURI = SistemaRED.getReportAffiliateInMovPrev(certificate.getData(), certificate.getPassword(), certificate.getType(), regime, ccc);
					break;
				case IDC:
					dataURI = SistemaRED.getIDCCCC(certificate.getData(), certificate.getPassword(), certificate.getType(), regime, ccc, new Date());
					break;
				default:
					throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
			
			Attach attach = new Attach()
					.setData(dataURI)
					.setMimeType(MimeType.PDF)
					.setDescription(requestType.getFileName());
			
			responseFile(response, attach);
		} catch (Exception e) {
			error(request, response, e);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		doGet(request, response);
	}
	
}
