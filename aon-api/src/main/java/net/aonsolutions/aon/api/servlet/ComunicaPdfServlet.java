package net.aonsolutions.aon.api.servlet;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.logging.Logger;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AON_SOLUTIONS;
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.security.Certificate;
import com.esferalia.aon.occam.api.model.security.User;
import com.esferalia.aon.occam.api.model.type.MimeType;
import solutions.aon.seg.social.toolkit.Toolkit;
import solutions.aon.sepe.Sepe;
import solutions.aon.sepe.exceptions.SepeException;
import solutions.aon.seg.social.SistemaRED;
import solutions.aon.seg.social.exceptions.SegSocialException;


@SuppressWarnings("serial")
@WebServlet(name = "ComunicaPdfServlet", urlPatterns = {"/ms/api/comunica/pdf/*"})

public class ComunicaPdfServlet extends AonApiHttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(ComunicaPdfServlet.class.getName());
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)  {
		LOGGER.info("AON COMUNCIAPDF - GET METHOD");
		super.doGet(req, resp);
		try {

			String[] pathInfo = req.getPathInfo() != null || "null".equalsIgnoreCase(req.getPathInfo()) ? req.getPathInfo().split("/") : null;

			byte[] PDF = null;

			if(pathInfo != null) {
				if( pathInfo.length == 3 && pathInfo[2].equalsIgnoreCase("sepe"))	{
					PDF = this.routerSepe(pathInfo[1], getDomain(), getToken());
				} else {
					PDF = this.routerSegSocial(pathInfo[1], getDomain(), getToken());
				}
				File file = File.createTempFile("informe", "");
				try(OutputStream os = new FileOutputStream(file)){
		            os.write(PDF);
					responseFile(req, resp, file, MimeType.PDF);
				}

			}
		} catch (Exception e) {
			error(req, resp, e);
		}

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)  {
		LOGGER.info("COMUNICAPDF SERVLET - POST METHOD");
		doGet(req, resp);
	}
	
	//router
	private byte[] routerSegSocial(String route, Domain domain, String token) throws SegSocialException, Exception {
		byte[] PDF = null;
		User user = AON_SOLUTIONS.getUser(domain, token);
		Certificate certificate = AON.getCertificate(domain.getName(), domain.getId(), user.getLogin(), user.getId());
		if(certificate.getCertificate()==null) throw new Exception("Certificate Null");
		final InputStream certificateInputStream = new ByteArrayInputStream(certificate.getCertificate());
		switch (route) {
			case "get-ta":
				LOGGER.info("GET-TA");
				PDF =  getTA(certificateInputStream, certificate.getPassword(), certificate.getType());
				break;
			case "get-idc":
				LOGGER.info("GET-IDC");
				PDF = getIDC(certificateInputStream, certificate.getPassword(), certificate.getType());
				break;
			case "cert-corriente":
				LOGGER.info("CERT-CORRIENTE");
				PDF = getCertCorriente(certificateInputStream, certificate.getPassword(), certificate.getType());
				break;
			default:
				break;
		}
		return PDF;
	}

	//router sepe
	private byte[] routerSepe(String route, Domain domain, String token)  throws SepeException, Exception {
		byte[] PDF = null;
		User user = AON_SOLUTIONS.getUser(domain, token);
		Certificate certificate = AON.getCertificateSEPE(domain.getName(), domain.getId(),  user.getLogin());
		if(certificate.getCertificate()==null) throw new Exception("Certificate Null");
		final InputStream certificateInputStream =  new ByteArrayInputStream(certificate.getCertificate());
		switch (route) {
			case "get-contrato":
				LOGGER.info("GET-CONTRATO");
				PDF =  getContratoPdf(certificateInputStream, certificate.getPassword(), certificate.getType());
				break;
			case "get-copy-basic":
				LOGGER.info("GET-COPY-BASIC");
				PDF = getCopyBasicPdf(certificateInputStream, certificate.getPassword(), certificate.getType());
				break;
			default:
				break;
		}
		return PDF;
	}

	private byte[] getTA(final InputStream certificateInputStream, final String certificatePassword,
			  final String certificateType) throws SegSocialException {
		
		String regimen = getParams().optString("regime");
		String ccc = getParams().getString("ctaCti");
		String nss = getParams().getString("nss");
		Date fecha = Toolkit.parseDate(getParams().getString("fra"), "yyyy-MM-dd");
		
	    return SistemaRED.getTA(certificateInputStream, certificatePassword, certificateType, regimen, ccc, nss, fecha);		
	}
	
	private byte[] getIDC(final InputStream certificateInputStream, final String certificatePassword,
			  final String certificateType) throws SegSocialException {
		
		String regimen = getParams().getString("regime");
		String ccc = getParams().getString("ctaCti");
		String nss = getParams().getString("nss");
		Date fecha = Toolkit.parseDate(getParams().getString("fra"), "yyyy-MM-dd");
		
	    return SistemaRED.getIDC(certificateInputStream, certificatePassword, certificateType, regimen, ccc, nss, fecha);	
	}
	
	private byte[] getCertCorriente(final InputStream certificateInputStream, final String certificatePassword,
			  final String certificateType) throws SegSocialException {
		String regimen = getParams().getString("regimen");
		String ccc = getParams().getString("ccc");
	    return SistemaRED.getUp2DateSS(certificateInputStream, certificatePassword, certificateType, regimen, ccc);	
	}
	
	private byte[] getContratoPdf(final InputStream certificateInputStream, final String certificatePassword,
			  final String certificateType) throws Exception {
		
		String ipf = getParams().getString("ipf");
		Date fecha = Toolkit.parseDate(getParams().getString("fecha"), "yyyy-MM-dd");
		
		return Sepe.getContratoPdf(certificateInputStream, certificatePassword, certificateType, ipf, fecha, fecha);

	}
	
	private byte[] getCopyBasicPdf(final InputStream certificateInputStream, final String certificatePassword,
			  final String certificateType) throws Exception {
		
		String ipf = getParams().getString("ipf");
		Date fecha = Toolkit.parseDate(getParams().getString("fecha"), "yyyy-MM-dd");
		
		return Sepe.getCopyBasicPdf(certificateInputStream, certificatePassword, certificateType, ipf, fecha, fecha);	
	}
	
	
}
