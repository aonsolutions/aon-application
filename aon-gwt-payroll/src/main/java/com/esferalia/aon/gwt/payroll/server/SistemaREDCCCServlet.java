package com.esferalia.aon.gwt.payroll.server;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.Writer;
import java.sql.Connection;
import java.util.Base64;
import java.util.Date;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.esferalia.aon.gwt.common.server.AonServletUtils;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.model.attachment.Attach;
import com.esferalia.aon.occam.api.model.Certificate;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.util.AonStringUtils;

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
		IDC("Idc"),
		LABORAL_LIFE("LaboralLife")
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
		AonApiData api = initialize(request, false, "GET"); // Provisional: false para que no compruebe el token

		String type = api.getData().getString("type");
		Integer typeIdx = Integer.parseInt(type);
		RequestType requestType = RequestType.values()[typeIdx];
		
		// Enterprise CCC
		String regime = api.getData().getString("regime");
		String ccc = api.getData().getString("ccc");
		
		JSONObject params = api.getData();
		// Domian and User
		String userLogin =  !params.optString("login").isEmpty() ? params.getString("login") : params.getString("userLogin");
		String domainName = !params.optString("domain").isEmpty() ? params.getString("domain") : params.getString("domainName"); 
		
		// Empty option or equal 0 get file response. Equals 1 downloadAttachment
		String attachmentType = api.getData().getString("attachmentType");
		Boolean downloadAttachment = AonStringUtils.isBlank(attachmentType) || AonStringUtils.equals(attachmentType, "0") ? false : true;

		try (Connection connection = AonServletUtils.getConnection(domainName)){
			
			Integer domainId = AonServletUtils.getDomainID(domainName);
			Integer parentDomainId = AonServletUtils.getParentDomainID(domainName); 
			Integer userId = AonServletUtils.getUserID(connection, userLogin, domainId, parentDomainId);
			
			// Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId);
			Certificate certificate = AON.getCertificate(domainName, domainId, userLogin, userId, "TGSS");
			
			byte[] data = null;

			switch (requestType) {
				case UPDATE_CERT:
					data = SistemaRED.getUp2DateSS(certificate.getData(), certificate.getPassword(), certificate.getType(), regime, ccc);
					break;
				case WORKING_EMPLOYEE:
					data = SistemaRED.getReportAffiliateInAlta(certificate.getData(), certificate.getPassword(), certificate.getType(), regime, ccc);
					break;
				case PRE_MOV_EMPLOYEE:
					data = SistemaRED.getReportAffiliateInMovPrev(certificate.getData(), certificate.getPassword(), certificate.getType(), regime, ccc);
					break;
				case IDC:
					data = SistemaRED.getIDCCCC(certificate.getData(), certificate.getPassword(), certificate.getType(), regime, ccc, new Date());
					break;
				case LABORAL_LIFE:
					data = SistemaRED.getCccLaboralLife(certificate.getData(), certificate.getPassword(), certificate.getType(), regime, ccc, new Date(), new Date());
					break;
				default:
					throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
			}
			
			if(null == data)
				throw new AonApiException("No existe documento");
			
			if(downloadAttachment) {
				Attach attach = new Attach()
						.setData(data)
						.setMimeType(MimeType.PDF)
						.setDescription(requestType.getFileName());
				
				responseFile(response, attach);
			} else {
				try ( OutputStream os = response.getOutputStream();
					  Writer writer = new OutputStreamWriter(os)) {
						
					response.setStatus(HttpServletResponse.SC_OK);
					String base64 = Base64.getEncoder().encodeToString(data);
					encodeURIComponent("application/pdf", base64, writer);
					
				}
			}
			
		} catch (Exception e) {
			error(request, response, e);
		}
	}
	
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) {
		doGet(request, response);
	}
	
	protected static void encodeURIComponent(String mime, String base64, Writer writer ) throws IOException {
		// data:[<MIME-type>][;charset=<encoding>][;base64],<data>
		writer.write("data:");
		writer.write(mime);
		writer.write(";base64,");
		base64 = base64.replace('$', '+');
		base64 = base64.replace('_', '/');
		writer.write(base64);
	}
	
}
