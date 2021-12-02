package solutions.aon.seg.social;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.xml.sax.SAXException;

import solutions.aon.seg.social.exception.InvalidCertificateException;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.object.SecondaryUser;
import solutions.aon.seg.social.toolkit.Toolkit;

public class ServicioREDSecondaryUser extends ServicioREDRegeXML {
	
	public static List<SecondaryUser> getSecondaryUsers(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType) throws SegSocialException{
		SSLContext sslContext = null;
		try {
			sslContext = SSLContexts.custom().loadKeyMaterial(Toolkit.readStore(certificateInputStream, certificatePassword, certificateType), certificatePassword.toCharArray()).build();
		} catch (Exception e1) {
			throw new InvalidCertificateException();
		}
		String link = "";
		String ticket = "";
		HttpPost httpPost = null;
		try (CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).build()) {
			
			String body = Toolkit.getBodyGET(httpClient, "https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV24P003");
			
			link = "https://w2.seg-social.es"+Toolkit.getAttribute(Toolkit.getElementByAttribute(body, "id", "FORMULARIO_1"), "action");

			ticket = Toolkit.getAttribute(Toolkit.getElementByAttribute(body, "name", "ARQ.SPM.TICKET"), "value");
			
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair(IServicioRedConstants.TICKET, ticket));
			params.add(new BasicNameValuePair(IServicioRedConstants.SPM_CONTEXT, IServicioRedConstants.INTERNET));
			params.add(new BasicNameValuePair("ARQ.SPM.OUT", "XML_STYLESHEET"));
			params.add(new BasicNameValuePair("radios", "A"));
			params.add(new BasicNameValuePair("situacion", "T"));
			params.add(new BasicNameValuePair("sitActUsuari", "T"));
			params.add(new BasicNameValuePair("tipoImpresion", "O"));
			params.add(new BasicNameValuePair("SPM.ACC.ACC_GENERAR_INFORME", "ACC_GENERAR_INFORME"));
			
			httpPost = new HttpPost(link);
			httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));	
			body = Toolkit.getBodyPOST(httpClient, httpPost);
			Toolkit.checkProsaError(body);
			
			return ServicioREDRegeXML.extractSecondaryUsers(body);
			
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
			throw new SegSocialException(e.getMessage());
		} 
	}
	
	
	public static void registerSecondaryUserByNie(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String typeIpf, final String nie,
			String naf) throws SegSocialException{
		SSLContext sslContext = null;
		try {
			sslContext = SSLContexts.custom().loadKeyMaterial(Toolkit.readStore(certificateInputStream, certificatePassword, certificateType), certificatePassword.toCharArray()).build();
		} catch (Exception e1) {
			throw new InvalidCertificateException();
		}
		String link = "";
		String ticket = "";
		HttpPost httpPost = null;
		try (CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).build()) {
			
			String body = Toolkit.getBodyGET(httpClient, "https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV24P004");
			
			link = IServicioRedConstants.BASE_URL_TGSS+Toolkit.getAttribute(Toolkit.getElementByAttribute(body, "id", "formulario_altaBajaMod"), "action");

			ticket = Toolkit.getAttribute(Toolkit.getElementByAttribute(body, "name", "ARQ.SPM.TICKET"), "value");
			
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair(IServicioRedConstants.TICKET, ticket));
			params.add(new BasicNameValuePair(IServicioRedConstants.SPM_CONTEXT, IServicioRedConstants.INTERNET));
			params.add(new BasicNameValuePair("ARQ.SPM.OUT", "XML_STYLESHEET"));
			params.add(new BasicNameValuePair("tipoNif", typeIpf));
			params.add(new BasicNameValuePair("numNif", nie));
			params.add(new BasicNameValuePair("naf", naf));
			params.add(new BasicNameValuePair("SPM.ACC.ACC_ALTA_USUARIO", "ACC_ALTA_USUARIO"));
			
			httpPost = new HttpPost(link);
			httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));	
			body = Toolkit.getBodyPOST(httpClient, httpPost);
			ServicioREDRegeXML.checkErrors(body);
			
			params = ServicioREDRegeXML.extractSecondaryFormValues(body);
			params.add(new BasicNameValuePair("SPM.ACC.ACC_CONTINUAR_ALTA", "ACC_CONTINUAR_ALTA"));
			NameValuePair urlParams = params.stream().filter(e-> e.getName().equals("url")).findAny().orElse(null);
			if(urlParams!=null) 
				link = IServicioRedConstants.BASE_URL_TGSS+urlParams.getValue();

			httpPost = new HttpPost(link);
			httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));	
			body = Toolkit.getBodyPOST(httpClient, httpPost);
			ServicioREDRegeXML.checkErrors(body);
			
			params = new ArrayList<>();
			params.add(new BasicNameValuePair(IServicioRedConstants.TICKET, ticket));
			params.add(new BasicNameValuePair(IServicioRedConstants.SPM_CONTEXT, IServicioRedConstants.INTERNET));
			params.add(new BasicNameValuePair("ARQ.SPM.OUT", "XML_STYLESHEET"));
			params.add(new BasicNameValuePair("SPM.ACC.ACC_CONTINUAR", "ACC_CONTINUAR"));
			
			httpPost = new HttpPost(link);
			httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));	
			body = Toolkit.getBodyPOST(httpClient, httpPost);
			ServicioREDRegeXML.checkErrors(body);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
			throw new SegSocialException(e.getMessage());
		} 
	}
	
	public static void deleteSecondaryUser(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, final String typeIpf, final String nie) throws SegSocialException{
		SSLContext sslContext = null;
		try {
			sslContext = SSLContexts.custom().loadKeyMaterial(Toolkit.readStore(certificateInputStream, certificatePassword, certificateType), certificatePassword.toCharArray()).build();
		} catch (Exception e1) {
			throw new InvalidCertificateException();
		}
		String link = "";
		String ticket = "";
		HttpPost httpPost = null;
		try (CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).build()) {
			
			String body = Toolkit.getBodyGET(httpClient, "https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV24P004");
			
			link = IServicioRedConstants.BASE_URL_TGSS+Toolkit.getAttribute(Toolkit.getElementByAttribute(body, "id", "formulario_altaBajaMod"), "action");

			ticket = Toolkit.getAttribute(Toolkit.getElementByAttribute(body, "name", "ARQ.SPM.TICKET"), "value");
			
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair(IServicioRedConstants.TICKET, ticket));
			params.add(new BasicNameValuePair(IServicioRedConstants.SPM_CONTEXT, IServicioRedConstants.INTERNET));
			params.add(new BasicNameValuePair("ARQ.SPM.OUT", "XML_STYLESHEET"));
			params.add(new BasicNameValuePair("tipoNif", typeIpf));
			params.add(new BasicNameValuePair("numNif", nie));
			params.add(new BasicNameValuePair("SPM.ACC.ACC_BAJA_USUARIO", "ACC_BAJA_USUARIO"));
			
			httpPost = new HttpPost(link);
			httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));	
			body = Toolkit.getBodyPOST(httpClient, httpPost);
			ServicioREDRegeXML.checkErrors(body);
			
			params = ServicioREDRegeXML.extractSecondaryFormValues(body);
			params.add(new BasicNameValuePair("SPM.ACC.ACC_CONTINUAR", "ACC_CONTINUAR"));
			NameValuePair urlParams = params.stream().filter(e-> e.getName().equals("url")).findAny().orElse(null);
			if(urlParams!=null) 
				link = IServicioRedConstants.BASE_URL_TGSS+urlParams.getValue();

			httpPost = new HttpPost(link);
			httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));	
			body = Toolkit.getBodyPOST(httpClient, httpPost);
			ServicioREDRegeXML.checkErrors(body);
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
			throw new SegSocialException(e.getMessage());
		} 
	}
	
}