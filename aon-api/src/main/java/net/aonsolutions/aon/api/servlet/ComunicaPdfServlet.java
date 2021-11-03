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
import com.esferalia.aon.occam.api.model.Domain;
import com.esferalia.aon.occam.api.model.security.Certificate;
import com.esferalia.aon.occam.api.model.type.MimeType;
import com.esferalia.aon.watson.server.AonDateUtils;

import net.aonsolutions.aon.api.error.AonApiError;
import net.aonsolutions.aon.api.error.AonApiException;
import net.aonsolutions.aon.api.ewok.AonApiData;
import solutions.aon.seg.social.SistemaRED;
import solutions.aon.sepe.Sepe;


@SuppressWarnings("serial")
@WebServlet(name = "ComunicaPdfServlet", urlPatterns = {"/ms/api/comunica/pdf/*"})

public class ComunicaPdfServlet extends AonApiHttpServlet{
		
	private static final Logger LOGGER  = Logger.getLogger(ComunicaPdfServlet.class.getName());
	private static final String FORMAT_DATE = "yyyy-MM-dd"; 
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)  {
		LOGGER.info("AON COMUNCIAPDF - GET METHOD");
		try {
			AonApiData api = initialize(req, resp);
			byte[] PDF = null;

			switch (api.getPath()) {
				case "/cert-corriente":
					LOGGER.info("CERT-CORRIENTE");
					PDF = getCertCorriente(api);
					break;
				case "/get-ta":
					LOGGER.info("GET-TA");
					PDF =  getTA(api);
					break;
				case "/get-idc":
					LOGGER.info("GET-IDC");
					PDF = getIDC(api);
					break;
				case "/get-contrato":
					LOGGER.info("GET-CONTRATO");
					PDF =  getContratoPdf(api);
					break;
				case "/get-copy-basic":
					LOGGER.info("GET-COPY-BASIC");
					PDF = getCopyBasicPdf(api);
					break;
				case "/get-report-affiliate-in-alta":
					LOGGER.info("GET-COPY-BASIC");
					PDF = getReportAffiliateInAlta(api);
					break;
				case "/get-report-affiliate-in-mov-prev":
					LOGGER.info("GET-COPY-BASIC");
					PDF = getReportAffiliateInMovPrev(api);
					break;
				case "/get-idc-ccc":
					LOGGER.info("GET-IDC-CCC");
					PDF = getIdcCcc(api);
					break;
				default:
					throw new AonApiException(AonApiError.ROUTE_ERROR.getMessage());
				} 
			
			File file = File.createTempFile("informe", "");
			try(OutputStream os = new FileOutputStream(file)){
	            os.write(PDF);
				responseFile(req, resp, file, MimeType.PDF);
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

	private byte[] getTA(AonApiData api) throws Exception {
		Certificate certificate = getCert(api, "SEG_SOCIAL");
		final InputStream certificateInputStream = new ByteArrayInputStream(certificate.getCertificate());
		String regime = api.getParams().optString("regime");
		String ccc = api.getParams().getString("ctaCti");
		String nss = api.getParams().getString("nss");
		Date fecha = AonDateUtils.parse(api.getParams().getString("fra"), FORMAT_DATE);
		
	    return SistemaRED.getTA(certificateInputStream, certificate.getPassword(), certificate.getType(), regime, ccc, nss, fecha);		
	}
	
	private byte[] getIDC(AonApiData api) throws Exception {
		Certificate certificate = getCert(api, "SEG_SOCIAL");
		final InputStream certificateInputStream = new ByteArrayInputStream(certificate.getCertificate());
		String regime = api.getParams().getString("regime");
		String ccc = api.getParams().getString("ctaCti");
		String nss = api.getParams().getString("nss");
		Date fecha =AonDateUtils.parse(api.getParams().getString("fra"), FORMAT_DATE);
		
	    return SistemaRED.getIDC(certificateInputStream, certificate.getPassword(), certificate.getType(), regime, ccc, nss, fecha);	
	}
	
	private byte[] getCertCorriente(AonApiData api) throws Exception {
		Certificate certificate = getCert(api, "SEG_SOCIAL");
		final InputStream certificateInputStream = new ByteArrayInputStream(certificate.getCertificate());
		String regime = api.getParams().getString("regime");
		String ccc = api.getParams().getString("ccc");
	    return SistemaRED.getUp2DateSS(certificateInputStream, certificate.getPassword(), certificate.getType(), regime, ccc);	
	}
	
	private byte[] getContratoPdf(AonApiData api) throws Exception {
		Certificate certificate = getCert(api, "SEPE");
		final InputStream certificateInputStream = new ByteArrayInputStream(certificate.getCertificate());
		String ipf = api.getParams().getString("ipf");
		Date fecha = AonDateUtils.parse(api.getParams().getString("fecha"), FORMAT_DATE); 
		
		return Sepe.getContratoPdf(certificateInputStream, certificate.getPassword(), certificate.getType(), ipf, fecha, fecha);

	}
	
	private byte[] getCopyBasicPdf(AonApiData api) throws Exception {
		Certificate certificate = getCert(api, "SEPE");
		final InputStream certificateInputStream = new ByteArrayInputStream(certificate.getCertificate());
		String ipf = api.getParams().getString("ipf");
		Date fecha = AonDateUtils.parse(api.getParams().getString("fecha"), FORMAT_DATE); 
		
		return Sepe.getCopyBasicPdf(certificateInputStream, certificate.getPassword(), certificate.getType(), ipf, fecha, fecha);	
	}
	
	private byte[] getReportAffiliateInAlta(AonApiData api) throws Exception {
		Certificate certificate = getCert(api, "SEG_SOCIAL");
		final InputStream certificateInputStream = new ByteArrayInputStream(certificate.getCertificate());
		String regime = api.getParams().getString("regime");
		String ccc = api.getParams().getString("ccc");
	
	    return SistemaRED.getReportAffiliateInAlta(certificateInputStream, certificate.getPassword(), certificate.getType(), regime, ccc);	
	}
	
	private byte[] getReportAffiliateInMovPrev(AonApiData api) throws Exception {
		Certificate certificate = getCert(api, "SEG_SOCIAL");
		final InputStream certificateInputStream = new ByteArrayInputStream(certificate.getCertificate());
		String regime = api.getParams().getString("regime");
		String ccc = api.getParams().getString("ccc");
	    return SistemaRED.getReportAffiliateInMovPrev(certificateInputStream, certificate.getPassword(), certificate.getType(), regime, ccc);	
	}
	
	private byte[] getIdcCcc(AonApiData api) throws Exception {
		Certificate certificate = getCert(api, "SEG_SOCIAL");
		final InputStream certificateInputStream = new ByteArrayInputStream(certificate.getCertificate());
		String regime = api.getParams().getString("regime");
		String ccc = api.getParams().getString("ccc");
		Date fecha = AonDateUtils.parse(api.getParams().getString("fecha"), FORMAT_DATE); 
		
	    return SistemaRED.getIDCCCC(certificateInputStream, certificate.getPassword(), certificate.getType(), regime, ccc, fecha);	
	}

	private Certificate getCert(AonApiData api, String typeCert) throws Exception {
		Domain domain = api.getDomain();
		Certificate certificate = null;
		if(typeCert.equals("SEPE")) {
			certificate = AON.getCertificateSEPE(domain.getName(), domain.getId(),  api.getUser().getLogin());
		} else {
			certificate = AON.getCertificate(domain.getName(), domain.getId(), api.getUser().getLogin(), api.getUser().getId());
		}
		if(certificate.getCertificate()==null) throw new Exception("Certificate Null");

		return certificate;
	}
	
}
