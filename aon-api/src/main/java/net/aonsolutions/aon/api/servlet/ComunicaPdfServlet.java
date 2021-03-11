package net.aonsolutions.aon.api.servlet;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Date;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.security.Certificate;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import solutions.aon.seg.social.toolkit.Toolkit;
import solutions.aon.sepe.Sepe;
import solutions.aon.sepe.exceptions.SepeException;
import solutions.aon.seg.social.SistemaRED;
import solutions.aon.seg.social.exceptions.SegSocialException;


@SuppressWarnings("serial")
@WebServlet(name = "ComunicaPdfServlet", urlPatterns = {"/ms/api/comunica/pdf/*"})

public class ComunicaPdfServlet extends HttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(ComunicaPdfServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		LOGGER.info("AON COMUNCIAPDF - GET METHOD");
		String param = req.getParameter("json");
		param = new String(Base64.getDecoder().decode(param));
		JSONObject json = new JSONObject(param);
		String token = json.getString("session_id");
		String domainName = json.getString("domain_name");
		Integer domainId = json.getInt("domain_id");
		
		Domain domain = AON.getDomain(domainName, domainId, "", f -> f.getNameProperty().eq(domainName));

		try {

			String[] pathInfo = req.getPathInfo() != null || "null".equalsIgnoreCase(req.getPathInfo()) ? req.getPathInfo().split("/") : null;
			
			Utils.addCorsHeader(resp);
			
			byte[] PDF = null;

			resp.setStatus(HttpServletResponse.SC_OK);
			if(pathInfo  != null) {
				if( pathInfo.length == 3 && pathInfo[2].equalsIgnoreCase("sepe"))	{
					PDF = this.routerSepe(pathInfo[1], domain, token,  json);
				} else {
					PDF = this.routerSegSocial(pathInfo[1], domain, token,  json);
				}
					
		        resp.setContentType(MimeType.PDF.getName());
				resp.setHeader("Content-disposition", "inline; filename=\"informe.pdf\";");
				ByteArrayInputStream fileInpurOs =  new ByteArrayInputStream(PDF);
				AonIOUtils.copy(fileInpurOs, resp.getOutputStream());
				resp.flushBuffer();
			}
		} catch (Exception e) {
            e.printStackTrace(); 
			resp.setStatus(500);
            resp.getWriter().print("<html><head><title>Oops an error happened!</title></head>");
            resp.getWriter().print("<body>"+e.getClass().getSimpleName()+" "+ e.getMessage() +"</body>");
            resp.getWriter().println("</html>");
		}

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("COMUNICAPDF SERVLET - POST METHOD");
		doGet(req, resp);
	}
	
	//router
	private byte[] routerSegSocial(String route, Domain domain, String token, JSONObject json) throws SegSocialException, Exception {
		byte[] PDF = null;
		User user = AON_SOLUTIONS.getUser(domain, token);
		Certificate certificate = AON.getCertificate(domain.getName(), domain.getId(), user.getLogin(), user.getId());
		if(certificate.getCertificate()==null) throw new Exception("Certificate Null");
		final InputStream certificateInputStream = new ByteArrayInputStream(certificate.getCertificate());
		switch (route) {
			case "get-ta":
				LOGGER.info("GET-TA");
				PDF =  getTA(certificateInputStream, certificate.getPassword(), certificate.getType(), json);
				break;
			case "get-idc":
				LOGGER.info("GET-IDC");
				PDF = getIDC(certificateInputStream, certificate.getPassword(), certificate.getType(), json);
				break;
			case "cert-corriente":
				LOGGER.info("CERT-CORRIENTE");
				PDF = getCertCorriente(certificateInputStream, certificate.getPassword(), certificate.getType(), json);
				break;
			default:
				break;
		}
		return PDF;
	}

	//router sepe
	private byte[] routerSepe(String route, Domain domain, String token, JSONObject json)  throws SepeException, Exception {
		byte[] PDF = null;
		User user = AON_SOLUTIONS.getUser(domain, token);
		Certificate certificate = AON.getCertificateSEPE(domain.getName(), domain.getId(),  user.getLogin());
		if(certificate.getCertificate()==null) throw new Exception("Certificate Null");
		final InputStream certificateInputStream =  new ByteArrayInputStream(certificate.getCertificate());
		switch (route) {
			case "get-contrato":
				LOGGER.info("GET-CONTRATO");
				PDF =  getContratoPdf(certificateInputStream, certificate.getPassword(), certificate.getType(), json);
				break;
			case "get-copy-basic":
				LOGGER.info("GET-COPY-BASIC");
				PDF = getCopyBasicPdf(certificateInputStream, certificate.getPassword(), certificate.getType(), json);
				break;
			default:
				break;
		}
		return PDF;
	}

	private byte[] getTA(final InputStream certificateInputStream, final String certificatePassword,
			  final String certificateType, JSONObject json) throws SegSocialException {
		
		String regimen = json.getString("regime");
		String ccc = json.getString("ctaCti");
		String nss = json.getString("nss");
		Date fecha = Toolkit.parseDate(json.getString("fra"), "yyyy-MM-dd");
		
	    return SistemaRED.getTA(certificateInputStream, certificatePassword, certificateType, regimen, ccc, nss, fecha);		
	}
	
	private byte[] getIDC(final InputStream certificateInputStream, final String certificatePassword,
			  final String certificateType, JSONObject json) throws SegSocialException {
		
		String regimen = json.getString("regime");
		String ccc = json.getString("ctaCti");
		String nss = json.getString("nss");
		Date fecha = Toolkit.parseDate(json.getString("fra"), "yyyy-MM-dd");
		
	    return SistemaRED.getIDC(certificateInputStream, certificatePassword, certificateType, regimen, ccc, nss, fecha);	
	}
	
	private byte[] getCertCorriente(final InputStream certificateInputStream, final String certificatePassword,
			  final String certificateType, JSONObject json) throws SegSocialException {
		String regimen = json.getString("regimen");
		String ccc = json.getString("ccc");
	    return SistemaRED.getUp2DateSS(certificateInputStream, certificatePassword, certificateType, regimen, ccc);	
	}
	
	private byte[] getContratoPdf(final InputStream certificateInputStream, final String certificatePassword,
			  final String certificateType, JSONObject json) throws Exception {
		
		String ipf = json.getString("ipf");
		Date fecha = Toolkit.parseDate(json.getString("fecha"), "yyyy-MM-dd");
		
		return Sepe.getContratoPdf(certificateInputStream, certificatePassword, certificateType, ipf, fecha, fecha);

	}
	
	private byte[] getCopyBasicPdf(final InputStream certificateInputStream, final String certificatePassword,
			  final String certificateType, JSONObject json) throws Exception {
		
		String ipf = json.getString("ipf");
		Date fecha = Toolkit.parseDate(json.getString("fecha"), "yyyy-MM-dd");
		
		return Sepe.getCopyBasicPdf(certificateInputStream, certificatePassword, certificateType, ipf, fecha, fecha);	
	}
	
	
}
