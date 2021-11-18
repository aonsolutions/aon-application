package solutions.aon.seg.social;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.xml.sax.SAXException;

import solutions.aon.seg.social.exception.InvalidCertificateException;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.invalid.InvalidDataException;
import solutions.aon.seg.social.exception.invalid.UnfilledMandatory;
import solutions.aon.seg.social.object.Employee;
import solutions.aon.seg.social.toolkit.Toolkit;

public class ServicioREDMov extends ServicioREDRegeXML {
	
	public static List<Employee> ipfxnaf(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, List<String> nssList) throws SegSocialException, IOException, ParserConfigurationException {
		if (nssList == null || nssList.isEmpty()) {			
			throw new UnfilledMandatory("Faltan los NAF");
		} else if(nssList.size() >= 7)
			throw new SegSocialException("Número máximo de NAF permitidos: 7");
		
		
		SSLContext sslContext = null;

		try {
			sslContext = SSLContexts.custom().loadKeyMaterial(Toolkit.readStore(certificateInputStream, certificatePassword, certificateType), certificatePassword.toCharArray()).build();
		} catch (Exception e1) {
			throw new InvalidCertificateException();
		}
		String link = "";
		String ticket = "";
		String navegacion = "";
		
		try (CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).build()) {
			
			String body = Toolkit.getBodyGET(httpClient, "https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV24M00C");
			link ="https://w2.seg-social.es/" + Toolkit.getAttribute(Toolkit.getElementByAttributeFirstTag(body, "id", "FORMULARIO_1"), "action");
			ticket = Toolkit.getAttribute(Toolkit.getElementByAttributeFirstTag(body, "id", "ARQ_SPM_TICKET"), "value");
			navegacion = Toolkit.getAttribute(Toolkit.getElementByAttributeFirstTag(body, "id", "ARQ_SPM_TMS_NAVEGACION"), "value");
			
			HttpPost httpPost = new HttpPost(link);
			
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair(IServicioRedConstants.TICKET, ticket));
			params.add(new BasicNameValuePair("SPM.CONTEXT", IServicioRedConstants.INTERNET));
			params.add(new BasicNameValuePair("ARQ.SPM.TMS_NAVEGACION", navegacion));
			params.add(new BasicNameValuePair("ARQ.SPM.OUT", "XML_STYLESHEET"));
			params.add(new BasicNameValuePair("ES_FW4", "1"));
			params.add(new BasicNameValuePair("SPM.ISPOPUP", "0"));
			params.add(new BasicNameValuePair("SPM.HAYJS", "1"));
			for (int i=0; i<7; i++) {
				if (i <= nssList.size() - 1) {
					String nss = nssList.get(i);
					nss  = nss.length() > 10 ? nss.substring(0, 10) : nss;
					params.add(new BasicNameValuePair("NA1NumSegSocialSinDC" + (i + 1), nss));					
				}
				else
					params.add(new BasicNameValuePair("NA1NumSegSocialSinDC" + (i + 1), ""));
					
			}
			params.add(new BasicNameValuePair("SPM.ACC.Consultar", "Consultar"));
			
			
			
			httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));
			
			String xml = Toolkit.getBodyPOST(httpClient, httpPost);
			if (xml != null)
				xml = xml.trim();
			try {				
				return extractIpfXNafInfo(xml);
			} catch (SAXException e) {
				throw new InvalidDataException(e.getMessage());
			}
			
		}
	}
	
	public static Employee nafxipf(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, String apellido1, String apellido2) throws SegSocialException, IOException, ParserConfigurationException {
		SSLContext sslContext = null;
		if (ipf == null || ipf.isEmpty()) {
			throw new UnfilledMandatory("El IPF no puede estar vacío");
		}
		try {
			sslContext = SSLContexts.custom().loadKeyMaterial(Toolkit.readStore(certificateInputStream, certificatePassword, certificateType), certificatePassword.toCharArray()).build();
		} catch (Exception e1) {
			throw new InvalidCertificateException();
		}
		String link = "";
		String ticket = "";
		String navegacion = "";
		
		Integer ident  = 1; //NIF DEFAULT
	    if(ServicioREDRegeXML.identity(ipf).equals("6"))
	    	ident = 6; // NIE
		
		try (CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).build()) {
			String body = Toolkit.getBodyGET(httpClient, "https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV24M00D");
			link ="https://w2.seg-social.es/" + Toolkit.getAttribute(Toolkit.getElementByAttributeFirstTag(body, "id", "FORMULARIO_1"), "action");
			ticket = Toolkit.getAttribute(Toolkit.getElementByAttributeFirstTag(body, "id", "ARQ_SPM_TICKET"), "value");
			navegacion = Toolkit.getAttribute(Toolkit.getElementByAttributeFirstTag(body, "id", "ARQ_SPM_TMS_NAVEGACION"), "value");
			
			HttpPost httpPost = new HttpPost(link);
			
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair(IServicioRedConstants.TICKET, ticket));
			params.add(new BasicNameValuePair("SPM.CONTEXT", IServicioRedConstants.INTERNET));
			params.add(new BasicNameValuePair("ARQ.SPM.TMS_NAVEGACION", navegacion));
			params.add(new BasicNameValuePair("ARQ.SPM.OUT", "XML_STYLESHEET"));
			params.add(new BasicNameValuePair("ES_FW4", "1"));
			params.add(new BasicNameValuePair("SPM.ISPOPUP", "0"));
			params.add(new BasicNameValuePair("SPM.HAYJS", "1"));
			params.add(new BasicNameValuePair("tipo", String.valueOf(ident)));
			params.add(new BasicNameValuePair("ipf6NumeroDocumento", ipf));
			params.add(new BasicNameValuePair("primerApellido", apellido1 != null ? apellido1 : ""));
			if (apellido1 == null || apellido1.isEmpty()) {
				params.add(new BasicNameValuePair("checkApellido1", "1"));				
			}
			params.add(new BasicNameValuePair("segundoApellido", apellido2 != null ? apellido2 : ""));
			if (apellido2 == null || apellido2.isEmpty()) {
				params.add(new BasicNameValuePair("checkApellido2", "2"));								
			}
			params.add(new BasicNameValuePair(IServicioRedConstants.SPM_CONTINUE, IServicioRedConstants.CONTINUE));
			httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));
			
			String xml = Toolkit.getBodyPOST(httpClient, httpPost);
			
			try {				
				return extractNafXIpfInfo(xml);
			} catch (SAXException e) {
				throw new InvalidDataException(e.getMessage());
			}
		}
	}
	
	public static byte[] getReportAffiliateInAlta(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String regime, String ccc) throws SegSocialException, IOException {
		
		if (regime == null || regime.isEmpty()) {
			throw new UnfilledMandatory("El régimen no debe quedar vacío");
		} else if (ccc == null || ccc.isEmpty()) {
			throw new UnfilledMandatory("El CCC no debe quedar vacío");			
		}
		
		SSLContext sslContext = null;
		
		try {
			sslContext = SSLContexts.custom().loadKeyMaterial(Toolkit.readStore(certificateInputStream, certificatePassword, certificateType), certificatePassword.toCharArray()).build();
		} catch (Exception e1) {
			throw new InvalidCertificateException();
		}
		String link = "";
		
		try (CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).build()) {
			
			String body = Toolkit.getBodyGET(httpClient, "https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR64&E=I&AP=AFIR");
			link = Toolkit.getLink(body);
			
			HttpPost httpPost = new HttpPost(link);

			String txtSDFTESO62 = Toolkit.removeExtraZeros(ccc.length() > 2 ? ccc.substring(0, 2) : "");
			String txtSDFNUM62 = ccc.length() > 2 ? ccc.substring(2) : "";
			
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair(IServicioRedConstants.APP_NAME, IServicioRedConstants.LIBAFCON));
			params.add(new BasicNameValuePair(IServicioRedConstants.FORM_NAME, "ATRM6401"));
			params.add(new BasicNameValuePair("txt_SDFREG62_ayuda", Toolkit.removeExtraZeros(regime)));
			params.add(new BasicNameValuePair("txt_SDFTESO62", txtSDFTESO62));
			params.add(new BasicNameValuePair("txt_SDFNUM62", txtSDFNUM62));
			params.add(new BasicNameValuePair(IServicioRedConstants.PRINT_TYPE, IServicioRedConstants.ONLINE_PRINT));
			params.add(new BasicNameValuePair("btn_Sub2207601004", IServicioRedConstants.CONTINUE));
			httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));
			
			body = Toolkit.getBodyPOST(httpClient, httpPost);
			ServicioREDRegeXML.checkOldSsError(body);
			httpPost = Toolkit.reportGenerationForm(body);
			
			try (CloseableHttpResponse resp = httpClient.execute(httpPost)) {
				Toolkit.checkResponseStatus(resp);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				resp.getEntity().writeTo(baos);
				return baos.toByteArray();
			}
		}
	}
	
	public static byte[] getReportAffiliateInMovPrev(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String regime, String ccc) throws SegSocialException, IOException {
		if (regime == null || regime.isEmpty()) {
			throw new UnfilledMandatory("El régimen no debe quedar vacío");
		} else if (ccc == null || ccc.isEmpty()) {
			throw new UnfilledMandatory("El CCC no debe quedar vacío");			
		}
		
		SSLContext sslContext = null;
		
		try {
			sslContext = SSLContexts.custom().loadKeyMaterial(Toolkit.readStore(certificateInputStream, certificatePassword, certificateType), certificatePassword.toCharArray()).build();
		} catch (Exception e1) {
			throw new InvalidCertificateException();
		}
		String link = "";
		String sessionId = "";
		
		try (CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).build()) {
			
			String body = Toolkit.getBodyGET(httpClient, "https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR74&E=I&AP=AFIR");
			link = Toolkit.getLink(body);
			sessionId = Toolkit.getSessionId(body);
			
			HttpPost httpPost = new HttpPost(link);

			String txtSDFTESCCCENT = ccc.length() > 2 ? ccc.substring(0, 2) : "";
			String txtSDFCODCCCENT = ccc.length() > 2 ? ccc.substring(2) : "";
			
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair(IServicioRedConstants.APP_NAME, IServicioRedConstants.LIBAFCON));
			params.add(new BasicNameValuePair(IServicioRedConstants.FORM_NAME, "ATRM7400"));
			params.add(new BasicNameValuePair(IServicioRedConstants.SESSION_ID, sessionId));
			params.add(new BasicNameValuePair("txt_SDFREGENT_ayuda", regime));
			params.add(new BasicNameValuePair("txt_SDFTESCCCENT", txtSDFTESCCCENT));
			params.add(new BasicNameValuePair("txt_SDFCODCCCENT", txtSDFCODCCCENT));
			params.add(new BasicNameValuePair(IServicioRedConstants.PRINT_TYPE, IServicioRedConstants.ONLINE_PRINT));
			params.add(new BasicNameValuePair("btn_Sub2207501004", IServicioRedConstants.CONTINUE));
			httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));
			
			body = Toolkit.getBodyPOST(httpClient, httpPost);
			ServicioREDRegeXML.checkOldSsError(body);
			httpPost = Toolkit.reportGenerationForm(body);
			
			try (CloseableHttpResponse resp = httpClient.execute(httpPost)) {
				Toolkit.checkResponseStatus(resp);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				resp.getEntity().writeTo(baos);
				return baos.toByteArray();
			}
		}
	}
	
}
