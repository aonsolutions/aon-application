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
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.io.AonIOUtils;
import solutions.aon.seg.social.toolkit.Toolkit;
import solutions.aon.seg.social.SistemaRED;
import solutions.aon.seg.social.exceptions.SegSocialException;


@SuppressWarnings("serial")
@WebServlet(name = "ComunicaPdfServlet", urlPatterns = {"/ms/api/comunica/pdf/*"})

public class ComunicaPdfServlet extends HttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(ComunicaServlet.class.getName());
	
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		LOGGER.info("AON COMUNCIAPDF - GET METHOD");
		String param = req.getParameter("json");
		param = new String(Base64.getDecoder().decode(param));
		JSONObject json = new JSONObject(param);
		String token = json.getString("session_id");
		String domainName = json.getString("domain_name");
		Integer domainId = json.getInt("domain_id");
		byte[] PDF = null;
//		Integer domainId = !"null".equalsIgnoreCase(req.getHeader("domain_id")) && AonNumberUtils.toInteger(req.getHeader("domain_id")) != null 
//				? AonNumberUtils.toInteger(req.getHeader("domain_id")) : 0;
		Domain domain = AON.getDomain(domainName, domainId, "", f -> f.getNameProperty().eq(domainName));

		try {

			String[] pathInfo = req.getPathInfo()!= null || "null".equalsIgnoreCase(req.getPathInfo()) ? req.getPathInfo().split("/") : null;
			
			Utils.addCorsHeader(resp);
			
			resp.setStatus(HttpServletResponse.SC_OK);

			if(pathInfo  != null) {

				if("get-ta".equalsIgnoreCase(pathInfo[1])) { // obtener TA
					LOGGER.info("GET-TA");
					String regimen = json.getString("regime");
					String ccc = json.getString("ctaCti");
					String nss = json.getString("nss");
					String fecha = json.getString("fra");
					PDF = getTA(regimen, ccc, nss, Toolkit.parseDate(fecha, "YYYY-MM-dd"));
				}
				else if("get-idc".equalsIgnoreCase(pathInfo[1])) { // obtener IDC
					LOGGER.info("GET-IDC");
					String regimen = json.getString("regime");
					String ccc = json.getString("ctaCti");
					String nss = json.getString("nss");
					String fecha = json.getString("fra");
					PDF = getIDC(regimen, ccc, nss, Toolkit.parseDate(fecha, "YYYY-MM-dd"));
				}
				
		        resp.setContentType(MimeType.PDF.getName());
				resp.setHeader("Content-disposition", "inline; filename=\"informe.pdf\";");
				ByteArrayInputStream fileInpurOs =  new ByteArrayInputStream(PDF);
				AonIOUtils.copy(fileInpurOs, resp.getOutputStream());
				resp.flushBuffer();
				
			}

		} 
		catch (Exception e) {
            e.printStackTrace(); 
			resp.setStatus(500);
		}

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		LOGGER.info("COMUNICAPDF SERVLET - POST METHOD");
		doGet(req, resp);
	}
	

	private byte[] getTA(String regimen, String ccc, String nss, Date date) throws SegSocialException {
		final InputStream certificateInputStream = ComunicaServlet.class.getResourceAsStream("FNMT.p12");
	    return SistemaRED.getTA(certificateInputStream, "jg@FNMT", "pkcs12", regimen, ccc, nss, date);	
	}
	
	private byte[] getIDC(String regimen, String ccc, String nss, Date date) throws SegSocialException {
		final InputStream certificateInputStream = ComunicaServlet.class.getResourceAsStream("FNMT.p12");
	    return SistemaRED.getIDC(certificateInputStream, "jg@FNMT", "pkcs12", regimen, ccc, nss, date);	
	}
}
