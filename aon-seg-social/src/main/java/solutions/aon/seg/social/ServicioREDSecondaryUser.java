package solutions.aon.seg.social;

import static solutions.aon.seg.social.exception.StatusCodeException.HandleStatusCodeException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
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
import org.htmlunit.FailingHttpStatusCodeException;
import org.htmlunit.StringWebResponse;
import org.htmlunit.UnexpectedPage;
import org.htmlunit.WebClient;
import org.htmlunit.WebRequest;
import org.htmlunit.WebResponse;
import org.htmlunit.html.HtmlAnchor;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlInput;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlSubmitInput;
import org.htmlunit.xml.XmlPage;
import org.xml.sax.SAXException;

import solutions.aon.seg.social.exception.InvalidCertificateException;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.object.SecondaryUser;
import solutions.aon.seg.social.toolkit.HtmlUnitToolkit;
import solutions.aon.seg.social.toolkit.Toolkit;

public class ServicioREDSecondaryUser extends ServicioREDRegeXML {
	
	public static List<SecondaryUser> getSecondaryUsers(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType) throws SegSocialException{
		List<SecondaryUser> list =  new  LinkedList<>();
		SSLContext sslContext = Toolkit.getTrustedSSLContext(certificateInputStream, certificatePassword, certificateType);
		
		String link = "";
		String ticket = "";
		HttpPost httpPost = null;
		try (CloseableHttpClient httpClient = HttpClients.custom().setSSLContext(sslContext).build()) {
			String body = Toolkit.getBodyGET(httpClient, IServicioRedConstants.BASE_URL_TGSS+"/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV24P003");
			Toolkit.checkProsaError(body);
			checkAuthorization(body);
			ticket = Toolkit.getAttribute(Toolkit.getElementByAttribute(body, "name", IServicioRedConstants.TICKET), "value");
			String moreAut = Toolkit.getElementByAttribute(body, "class", "pr_enlaceLocal");
			if(null!=moreAut) {
				String newUrl =  Toolkit.getAttribute(moreAut, "href").replace("&amp;", "&");
				body = Toolkit.getBodyGET(httpClient, IServicioRedConstants.BASE_URL_TGSS+newUrl);
				String sessionStr = Toolkit.getElementByAttribute(body, "id", "SPM.IDSESSION");
				link = IServicioRedConstants.BASE_URL_TGSS+"/ProsaInternet/OnlineAccessUtf8"+Toolkit.getAttribute(sessionStr, "innerText");
			} else {
				String action = Toolkit.getAttribute(Toolkit.getElementByAttribute(body, "id", "FORMULARIO_1"), "action");
				if(action!=null)
					link = IServicioRedConstants.BASE_URL_TGSS+action;
				else 
					throw new SegSocialException("No hay datos disponibles");
			}				
		
			Boolean more = false;
			String paramNameSearch = "SPM.ACC.ACC_BUSCAR_CRITERIOS";
			String paramNameNext = "SPM.ACC.ACC_SIGUIENTE_USUARIO";
			List<NameValuePair> params = new ArrayList<>();
			params.add(new BasicNameValuePair(IServicioRedConstants.TICKET, ticket));
			params.add(new BasicNameValuePair(IServicioRedConstants.SPM_CONTEXT, IServicioRedConstants.INTERNET));
			params.add(new BasicNameValuePair("ARQ.SPM.OUT", "XML_STYLESHEET"));
			params.add(new BasicNameValuePair("radios", "A"));
			params.add(new BasicNameValuePair("situacion", "T"));
			params.add(new BasicNameValuePair("sitActUsuari", "T"));
			params.add(new BasicNameValuePair("tipoImpresion", "O"));
			params.add(new BasicNameValuePair(paramNameSearch, "ACC_BUSCAR_CRITERIOS"));
			
			do {
	
				httpPost = new HttpPost(link);
				httpPost.setEntity(new UrlEncodedFormEntity(params, ServicioREDRegeXML.DEFAULT_ENCODING));	
				body = Toolkit.getBodyPOST(httpClient, httpPost);
				ServicioREDRegeXML.checkErrors(body);
		
				more = false;
				params.removeIf(param-> param.getName().equals(paramNameNext));
				
				List<SecondaryUser> users = ServicioREDRegeXML.extractSecondaryUsers(body);
	
				if(users.size()>0) {
					list.addAll(users);
					String elementSig = Toolkit.getTagXmlFirst(body, "pagSiguiente");
					if(null!=elementSig) {
						String nextStr = Toolkit.getAttribute(elementSig, "innerText");
						if(null!=nextStr && nextStr.equals("true")) {
							more = true;
							params.add(new BasicNameValuePair(paramNameNext, "ACC_SIGUIENTE_USUARIO"));
							params.removeIf(param-> param.getName().equals(paramNameSearch));
						}
					}
				}
			} while(Boolean.TRUE.equals(more));
			
		} catch (ParserConfigurationException | SAXException | IOException e) {
			e.printStackTrace();
			throw new SegSocialException(e.getMessage());
		}
		return list;
	}
	
	public static byte[] getSecondaryUsersPDF(final InputStream certificateInputStream, final String certificatePassword, final String certificateType) throws SegSocialException, IOException{
//		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)) {
		
		byte[] certificateData = certificateInputStream.readAllBytes();
		
		try (WebClient webClient = HtmlUnitToolkit.getWebClient(certificateData, certificatePassword, certificateType);
				/*WebConnectionWrapper wrapper = HtmlUnitToolkit.transformXmlPage(webClient, certificateData, certificatePassword, certificateType, Collections.emptyMap(), ServicioREDSecondaryUser::skipDateFormatError)*/) {
				
			webClient.getOptions().setUseInsecureSSL(true);
			webClient.getOptions().setRedirectEnabled(true);
			webClient.getOptions().setJavaScriptEnabled(true);
			
			XmlPage xmlPage = webClient.getPage("https://w2.seg-social.es/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV24P003");
			HtmlPage document = HtmlUnitToolkit.transformXmlPage(xmlPage);
			
			HtmlInput sitUsuSec = document.querySelector("#sitUsuSec_1");
			sitUsuSec.click();
			webClient.waitForBackgroundJavaScript(10000);
			
			document = HtmlUnitToolkit.selectOption(document, "situacion", "T");
			webClient.waitForBackgroundJavaScript(10000);
			
			// Al elegir la situacion se actualizan los valores de este input, pero no carga nada...
			document = HtmlUnitToolkit.selectOption(document, "sitActUsuari", "T");
			
			document = HtmlUnitToolkit.selectOption(document, "tipImpresio", "O");
			
			HtmlSubmitInput continueButton = document.querySelector("#INFORME");

			// Check if we have more than one CCC for this person
			try {
				document = continueButton.click();
				
				// Check table
				HtmlAnchor docButton = document.querySelector("section#SECCION_1 a");
				
				if(docButton != null)
					return getPDFDocument(docButton);
				else
					return null;
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		} catch (FailingHttpStatusCodeException e) {
			HandleStatusCodeException(e);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static byte[] getPDFDocument(HtmlElement linkElement) throws IllegalArgumentException {
		try {
			UnexpectedPage docPage = linkElement.click();
			return docPage.getWebResponse().getContentAsStream().readAllBytes();
		} catch (IOException e) {
			// Exception
			throw new IllegalArgumentException(e.getMessage());
		}
	}
	
	private static WebResponse skipDateFormatError (WebRequest request, WebResponse response) {
		
		if ( request.getUrl().getFile().endsWith("prosa.min.js")) {
			String content = response.getContentAsString();
			//content = content.replaceAll("a\s*=\s*E\\(.*msgErrorFormaFecha.*dd/mm/aaaa\"\\)\\]\\)", "a=!0");
			content = content.replaceAll("\"chrome\"", "\":-o\"");
			return new StringWebResponse(content, request.getUrl());
		}
		
		return response;
		
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
			
			String body = Toolkit.getBodyGET(httpClient, IServicioRedConstants.BASE_URL_TGSS+"/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV24P004");
			
			link = IServicioRedConstants.BASE_URL_TGSS+Toolkit.getAttribute(Toolkit.getElementByAttribute(body, "id", "formulario_altaBajaMod"), "action");

			ticket = Toolkit.getAttribute(Toolkit.getElementByAttribute(body, "name", IServicioRedConstants.TICKET), "value");
			
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
			
			String body = Toolkit.getBodyGET(httpClient, IServicioRedConstants.BASE_URL_TGSS+"/ProsaInternet/OnlineAccess?ARQ.SPM.ACTION=LOGIN&ARQ.SPM.APPTYPE=SERVICE&ARQ.IDAPP=XV24P004");
			
			link = IServicioRedConstants.BASE_URL_TGSS+Toolkit.getAttribute(Toolkit.getElementByAttribute(body, "id", "formulario_altaBajaMod"), "action");

			ticket = Toolkit.getAttribute(Toolkit.getElementByAttribute(body, "name", IServicioRedConstants.TICKET), "value");
			
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