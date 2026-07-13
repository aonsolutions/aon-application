package solutions.aon.seg.social.toolkit;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.URIResolver;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.logging.LogFactory;
import org.htmlunit.BrowserVersion;
import org.htmlunit.DefaultCredentialsProvider;
import org.htmlunit.ElementNotFoundException;
import org.htmlunit.FailingHttpStatusCodeException;
import org.htmlunit.IncorrectnessListener;
import org.htmlunit.NicelyResynchronizingAjaxController;
import org.htmlunit.Page;
import org.htmlunit.ScriptException;
import org.htmlunit.StringWebResponse;
import org.htmlunit.WebClient;
import org.htmlunit.WebClientOptions;
import org.htmlunit.WebRequest;
import org.htmlunit.WebResponse;
import org.htmlunit.WebResponseData;
import org.htmlunit.WebWindow;
import org.htmlunit.cssparser.parser.CSSErrorHandler;
import org.htmlunit.cssparser.parser.CSSException;
import org.htmlunit.html.DomElement;
import org.htmlunit.html.DomNode;
import org.htmlunit.html.DomNodeList;
import org.htmlunit.html.HtmlAnchor;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlListItem;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.HtmlSelect;
import org.htmlunit.html.parser.HTMLParser;
import org.htmlunit.html.parser.HTMLParserListener;
import org.htmlunit.javascript.JavaScriptErrorListener;
import org.htmlunit.util.WebConnectionWrapper;
import org.htmlunit.util.WebResponseWrapper;
import org.htmlunit.xml.XmlPage;

import solutions.aon.seg.social.exception.CSSParseException;
import solutions.aon.seg.social.exception.InternalException;
import solutions.aon.seg.social.exception.InvalidCertificateException;
import solutions.aon.seg.social.exception.OutOfServiceException;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.StatusCodeException;
import solutions.aon.seg.social.exception.invalid.InvalidDataException;
import solutions.aon.seg.social.exception.invalid.NoMoreDataException;

public class HtmlUnitToolkit {
	
	public static final String PGIS_LOGIN_URL = "https://idp.seg-social.es/PGIS/Login";
	public static final String MENU_AFI_DIRECTO = "menuAFI-DIRECTO";
	public static final String MENU_AFI_DIRECTO_URL = "https://w2sp.seg-social.es/M/menuAFI-DIRECTO.html";


	// WAIT FOR A SPECIFIC HTML ELEMENT
	public static <HtmlPage, R> Optional<R> wait4(HtmlPage htmlPage, Function<HtmlPage, R> function)
			throws InterruptedException {
		// try 20 times to wait .5 second each for filling the page.
		for (int i = 0; i < 20; i++) {
			R r = function.apply(htmlPage);
			if (r != null)
				return Optional.of(r);
			synchronized (htmlPage) {
				htmlPage.wait(500);
			}
		}
		return Optional.empty();
	}

	public static WebClient getWebClient(final byte[] certificateData, final String certificatePassword,
			final String certificateType) throws SegSocialException, IOException {
		try ( ByteArrayInputStream certificateInputStream = new ByteArrayInputStream(certificateData)) {
			return getWebClientCert(certificateInputStream, certificatePassword, certificateType);
		}
	}

	// GET THE WEB CLIENT OF HTMLUNIT
	public static WebClient getWebClient(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType) throws InvalidCertificateException {
		try {
			WebClient webClient = new WebClient(BrowserVersion.BEST_SUPPORTED);
			disableLogging(webClient);
			webClient.getOptions().setCssEnabled(false);
			webClient.getOptions().setDownloadImages(false);
			webClient.setJavaScriptTimeout(10000);
			webClient.setAjaxController(new NicelyResynchronizingAjaxController());
			webClient.getOptions().setSSLClientCertificateKeyStore(certificateInputStream, certificatePassword,
					certificateType);


			return webClient;
		} catch (RuntimeException e) {
			throw new InvalidCertificateException();
		}
	}
	
	public static WebClient getWebClientCert(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType) throws SegSocialException {
		try {
			WebClient webClient = new WebClient(BrowserVersion.BEST_SUPPORTED);
			webClient.getOptions().setCssEnabled(false);
			webClient.getOptions().setDownloadImages(false);
			webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
			webClient.getOptions().setSSLClientCertificateKeyStore(certificateInputStream, certificatePassword,
					certificateType);
			webClient.setJavaScriptTimeout(10000);
			webClient.setAjaxController(new NicelyResynchronizingAjaxController());
			return webClient;
		} catch (RuntimeException e) {
			throw new SegSocialException(e.getMessage());
		}
	}

	// GET THE WEB CLIENT OF HTMLUNIT
	public static WebClient getWebClient(final String user, final String password) throws InvalidCertificateException {
		try {
			WebClient webClient = new WebClient(BrowserVersion.BEST_SUPPORTED);
			webClient.getOptions().setCssEnabled(false);
			webClient.getOptions().setDownloadImages(false);
			webClient.setJavaScriptTimeout(10000);
			webClient.setAjaxController(new NicelyResynchronizingAjaxController());

			DefaultCredentialsProvider creds = new DefaultCredentialsProvider();
			creds.addCredentials(user, password.toCharArray());
			webClient.setCredentialsProvider(creds);

			return webClient;
		} catch (RuntimeException e) {
			throw new InvalidCertificateException();
		}
	}
	
	// GET THE WEB CLIENT OF INTERNET_EXPLOTER
	public static WebClient getWebClientExplorer(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType) throws InvalidCertificateException {
		try {
			WebClient webClient = new WebClient(BrowserVersion.EDGE);
			disableLogging(webClient);
			webClient.getOptions().setCssEnabled(false);
			webClient.getOptions().setDownloadImages(false);
			webClient.setJavaScriptTimeout(10000);
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
			webClient.setAjaxController(new NicelyResynchronizingAjaxController());
			webClient.getOptions().setSSLClientCertificateKeyStore(certificateInputStream, certificatePassword,
					certificateType);

			return webClient;
		} catch (RuntimeException e) {
			throw new InvalidCertificateException();
		}
	}
	
	public static WebClient getWebClientTgss(final InputStream certificateInputStream,
	        final String certificatePassword, final String certificateType) throws InvalidCertificateException {
	    try {
	        char[] password = certificatePassword.toCharArray();

	        KeyStore keyStore = KeyStore.getInstance(certificateType);
	        keyStore.load(certificateInputStream, password);

	        // Solo completar cadena si es necesario
	        KeyStore finalKeyStore = needsChainCompletion(keyStore, password)
	                ? addChainFromAIA(keyStore, password)
	                : keyStore;

	        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
	        kmf.init(finalKeyStore, password);

	        TrustManager[] trustAll = new TrustManager[]{
	            new X509TrustManager() {
	                public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
	                public void checkClientTrusted(X509Certificate[] c, String a) {}
	                public void checkServerTrusted(X509Certificate[] c, String a) {}
	            }
	        };

	        SSLContext sslContext = SSLContext.getInstance("TLS");
	        sslContext.init(kmf.getKeyManagers(), trustAll, new SecureRandom());
	        
	        WebClient webClient = new WebClient(BrowserVersion.BEST_SUPPORTED);
	        disableLogging(webClient);
	        webClient.getOptions().setCssEnabled(false);
	        webClient.getOptions().setDownloadImages(false);
	        webClient.getOptions().setUseInsecureSSL(true);
	        
	        webClient.getOptions().setJavaScriptEnabled(true);
	        webClient.getOptions().setThrowExceptionOnScriptError(false);
	        webClient.getOptions().setThrowExceptionOnFailingStatusCode(false);
	        webClient.getOptions().setRedirectEnabled(true);
	        webClient.setJavaScriptTimeout(20000);
	        webClient.setAjaxController(new NicelyResynchronizingAjaxController());

	        SSLContext.setDefault(sslContext);
	        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

	        Path tempCert = Files.createTempFile("cert_", ".p12");
	        try (OutputStream os = Files.newOutputStream(tempCert)) {
	            finalKeyStore.store(os, password);
	        }
	        tempCert.toFile().deleteOnExit();

	        webClient.getOptions().setSSLClientCertificateKeyStore(
	            tempCert.toUri().toURL(),
	            certificatePassword,
	            certificateType
	        );

	        return webClient;
	    } catch (Exception e) {
	        e.printStackTrace();
	        throw new InvalidCertificateException();
	    }
	}
	 
	/** Convierte cualquier Page devuelta por HtmlUnit en HtmlPage. 
	 * @throws IOException */
	private static HtmlPage asHtmlPage(Page page) throws SegSocialException, IOException {
		if (page instanceof HtmlPage)
			return (HtmlPage) page;
		if (page instanceof XmlPage) {
			try {
				return transformXmlPage((XmlPage) page);
			} catch (TransformerException e) {
				throw new SegSocialException(e);
			}
		}
		throw new SegSocialException("Página inesperada: " + page.getClass().getName());
	}

	private static boolean needsChainCompletion(KeyStore keyStore, char[] password) throws Exception {
	    String alias = keyStore.aliases().nextElement();
	    Certificate[] chain = keyStore.getCertificateChain(alias);
	    boolean needs = chain == null || chain.length <= 1;
	    System.out.println("Chain length: " + (chain == null ? 0 : chain.length) + " -> " 
	        + (needs ? "completar cadena vï¿½a AIA" : "cadena completa, no es necesario completar"));
	    return needs;
	}
	
	private static KeyStore addChainFromAIA(KeyStore original, char[] password) throws Exception {
	    String alias = original.aliases().nextElement();
	    PrivateKey privateKey = (PrivateKey) original.getKey(alias, password);
	    X509Certificate userCert = (X509Certificate) original.getCertificate(alias);

	    List<Certificate> chain = new ArrayList<>();
	    chain.add(userCert);

	    List<X509Certificate> intermediates = downloadIntermediates(userCert);
	    if (intermediates.isEmpty()) {
	        System.out.println("No se encontraron intermedios, se usa el certificado tal cual.");
	    } else {
	        chain.addAll(intermediates);
	    }

	    KeyStore newKs = KeyStore.getInstance("PKCS12");
	    newKs.load(null, password);
	    newKs.setKeyEntry(alias, privateKey, password, chain.toArray(new Certificate[0]));

	    System.out.println("Cadena final:");
	    chain.forEach(c -> System.out.println("  Subject: " + ((X509Certificate) c).getSubjectX500Principal()));

	    return newKs;
	}

	private static List<X509Certificate> downloadIntermediates(X509Certificate userCert) {
	    String issuerCN = userCert.getIssuerX500Principal().getName();
	    System.out.println("Issuer detectado: " + issuerCN);

	    // Mapa de emisores conocidos con sus cadenas de intermedios (orden: intermedio -> raï¿½z)
	    if (issuerCN.contains("UANATACA CA1 2021")) {
	        return downloadCertChain(
	            "https://web.uanataca.com/common/project/pdf/autoridad-certificacion/07_subordinada-ca1-2021.cer",
	            "https://web.uanataca.com/common/project/pdf/autoridad-certificacion/01_raiz-ca-2016.cer"
	        );
	    }

	    // Aï¿½adir aquï¿½ otros emisores conocidos si aparecen en el futuro:
	    // if (issuerCN.contains("OTRO EMISOR")) { return downloadCertChain(...); }

	    System.out.println("Emisor no reconocido, no se aï¿½aden intermedios.");
	    return Collections.emptyList();
	}

	private static List<X509Certificate> downloadCertChain(String... urls) {
	    List<X509Certificate> certs = new ArrayList<>();
	    CertificateFactory cf;
	    try {
	        cf = CertificateFactory.getInstance("X.509");
	    } catch (Exception e) {
	        e.printStackTrace();
	        return certs;
	    }

	    for (String url : urls) {
	        try {
	            System.out.println("Descargando: " + url);
	            HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
	            conn.setConnectTimeout(5000);
	            conn.setReadTimeout(5000);
	            try (InputStream is = conn.getInputStream()) {
	                X509Certificate cert = (X509Certificate) cf.generateCertificate(is);
	                certs.add(cert);
	                System.out.println("  OK: " + cert.getSubjectX500Principal());
	            }
	        } catch (Exception e) {
	            System.err.println("Error descargando " + url + ": " + e.getMessage());
	        }
	    }
	    return certs;
	}

	// ENSURE CLAVE AUTHENTICATION (DNIe OR CERTIFICATE)
	public static HtmlPage ensureClaveAuth(WebClient webClient, HtmlPage page, String target) throws Exception {
	    DomElement certOption = page.getElementById("IPCEIdP");
	    if (certOption == null) {
	        // No estamos en Cl@ve. Si además ya es el menú, connectTgss lo validará.
	        return page;
	    }
	    System.out.println("Pantalla Cl@ve detectada -> seleccionando 'DNIe o certificado'");

	    // El click dispara la cadena Cl@ve (PGIS/IPCE/PostLogin) que establece CookieSMS.
	    certOption.click();
	    webClient.waitForBackgroundJavaScript(20000);

	    // NO damos por buena la página a la que el auto-redirect nos dejó (cae en
	    // "Usuario No Autorizado" por la cookie perdida en el 302 de GetAccess).
	    // Re-navegamos al recurso siguiendo los 302 a mano: ahora CookieSMS ya está
	    // en el CookieManager y GetAccess podrá consolidar AUTH_SESSION_ID.
	    HtmlPage result = getPageFollowingRedirects(webClient, new URL(target));
	    webClient.waitForBackgroundJavaScript(10000);

	    if (!isAfiDirectoMenu(result)) {
	        throw new SegSocialException(
	            "Cl@ve completó pero GetAccess no consolidó la sesión. URL=" + result.getUrl()
	            + " título=" + result.getTitleText()
	            + (isNotAuthorized(result) ? " (Usuario No Autorizado)" : ""));
	    }
	    return result;
	}

	// GET TRIMMED STRING FROM HTML ELEMENT
	public static String getTrimmedById(HtmlPage htmlPage, String id) {
		if (htmlPage.getElementById(id) != null) {
			String text = Toolkit.removeNBSP(htmlPage.getElementById(id).getTextContent());
			return text != null ? text.trim() : text;
		} else
			return null;
	}
	public static String getTrimmedBySelector(HtmlPage htmlPage, String selector) {
		if (htmlPage.querySelector(selector) != null)
			return Toolkit.removeNBSP(htmlPage.querySelector(selector).getVisibleText());
		else
			return null;
	}

	// GETS THE SS STATUS CODE
	public static Integer getSSCode(HtmlPage htmlPage) throws SegSocialException {
		String status;
		try {
			status = HtmlUnitToolkit.getTrimmedById(htmlPage, "DIL");
			
			if (status != null) {
				status = Toolkit.removeNBSP(status).replace(" ", "");
				if ((status.length() > 0) && (status.charAt(0) == '*'))
					status = status.substring(1);
				if (status.equals("") || status.contains("0350"))
					return 3083;
				if (!status.contains("*"))
					if (!status.contains("-"))
						throw new SegSocialException(status);
					else
						return Integer.parseInt(status.substring(0, status.indexOf("-")));
				
				int statusCode = Integer.parseInt(status.substring(0, status.indexOf("*")));
				
				if(statusCode == 3037) throw new NoMoreDataException();
				else return statusCode;
			
			} else
				return 0;
			
		} catch (ElementNotFoundException e) {
			return 3083;
		} catch (NumberFormatException nfe) {
			throw new SegSocialException();
		}
	}

	// GETS THE SS STATUS CODE
	public static String getSSmessage(HtmlPage htmlPage) throws SegSocialException {
		try {
			String msg = HtmlUnitToolkit.getTrimmedById(htmlPage, "DIL");
			if(msg==null) {
				msg = getTrimmedBySelector(htmlPage, ".cuerpo_noautorizado p, #content p");
			}
			return msg;
		} catch (ElementNotFoundException e) {
			return "";
		} catch (NumberFormatException e) {
			throw new SegSocialException(e);
		}
	}

	// MANAGES THE EXCEPTIONS
	public static void manageStatusCode(HtmlPage htmlPage) throws SegSocialException {
		Integer code = getSSCode(htmlPage);
		String msg = getSSmessage(htmlPage);
		InvalidDataException.checkCode(code, msg);
		Toolkit.checkCertificateRevoked(htmlPage.asXml());
	}
	
	public static void handleNewSegSocialExceptions(HtmlPage htmlPage) throws InvalidDataException {
		try {
			DomNode error = htmlPage.querySelector(".ERROR.mensaje");
			if (error != null && !error.getVisibleText().isEmpty()) {				
				throw new InvalidDataException(error.getVisibleText());
			}
		} catch (NullPointerException e) {}
	}
	
	public static String getMessageSuccess(HtmlPage htmlPage) {
		DomNode message = htmlPage.querySelector(".INFO.mensaje");
		if (message != null && !message.getVisibleText().isEmpty()) {				
			return message.getVisibleText();
		}
		return "";
	}
	
	// MANAGES THE EXCEPTIONS OF NEW UI
	public static void manageStatusMessage(HtmlPage document) throws SegSocialException {
		DomNodeList<DomNode> errors = document.querySelectorAll(".mensajeError");
		if (errors.isEmpty())
			return;

		StringBuilder errorList = new StringBuilder();
		for (DomNode err : errors) {
			HtmlListItem error = (HtmlListItem) err;
			errorList.append(error.getValueAttribute()).append(" ");
		}
		throw new InvalidDataException(errorList.toString());
	}

	// SHOW HTML ELEMENTS AS XML
	public static void showAsXML(HtmlElement... elements) {
		for (HtmlElement e : elements) {
			if (e != null)
				System.out.println(e.asXml());
		}
	}

	// SHOW HTML ELEMENTS AS TEXT
	public static void showAsText(HtmlElement... elements) {
		for (HtmlElement e : elements) {
			if (e != null)
				System.out.println(e.asNormalizedText());
		}
	}

	// GET ELEMENT BY NAME
	public static HtmlElement getByName(HtmlPage page, String name) throws InternalException {
		try {
			return page.querySelector("*[name = " + name + "]");
		} catch (CSSException e) {
			throw new CSSParseException();
		} catch (Exception e) {
			throw new InternalException();
		}
	}

	// GET ELEMENT BY ID
	public static HtmlElement getById(HtmlPage page, String id) throws InternalException {
		try {
			return page.querySelector("#" + id);
		} catch (CSSException e) {
			throw new CSSParseException();
		} catch (Exception e) {
			throw new InternalException();
		}
	}

	// GET ELEMENT BY CLASS
	public static HtmlElement getByClass(HtmlPage page, String _class) throws InternalException {
		try {
			return page.querySelector("." + _class);
		} catch (CSSException e) {
			throw new CSSParseException();
		} catch (Exception e) {
			throw new InternalException();
		}
	}

	public static HtmlAnchor setUrlParse(HtmlPage htmlPage, HtmlAnchor anchor) throws IOException {
		String newUrl = htmlPage.getFullyQualifiedUrl(anchor.getHrefAttribute()).toString().replaceAll("\\s+", "");
		anchor.setAttribute("href", newUrl);
		return anchor;
	}

	public static DomNode getElConstains(HtmlPage htmlPage, String selector, String text) {
		DomNodeList<DomNode> els = htmlPage.querySelectorAll(selector);
		DomNode elem = null;
		for (DomNode el : els) {
			if (el.asNormalizedText().toString().indexOf(text) >= 0) {
				elem = el;
				break;
			}
		}
		return elem;
	}
	
	
	public static void simulateStatusCode(int code) {
		
		WebResponseData data = new WebResponseData(new byte[0], code, "Simulated " + code + " status", new ArrayList<>());
		WebResponse response;
		try {
			response = new WebResponse(data, new WebRequest(new URL("http://www.fakeURL.com")), (long)10);
			FailingHttpStatusCodeException exception = new FailingHttpStatusCodeException(response);
			throw exception;
		} catch (MalformedURLException ignore) {}

	}
	
	public static <P extends Page> void checkStatusAndDown(P page) throws SegSocialException {
		int statusCode = page.getWebResponse().getStatusCode();
		if (statusCode >= 300)
			throw new StatusCodeException(statusCode);
		isSiteDown(page);
	}
	
	public static <P extends Page> P clickAndCheckCode (DomElement element) throws IOException, SegSocialException {
		P page = element.click();
		checkStatusAndDown(page);
		return page;
	}
	
	public static <P extends Page> P doubleClickAndCheckCode (DomElement element) throws IOException, SegSocialException {
		P page = element.dblClick();
		checkStatusAndDown(page);
		return page;
	}
	
	public static HtmlPage selectOption(HtmlPage htmlPage, String id, String value) throws IOException {
		HtmlSelect htmlSelect = (HtmlSelect) htmlPage.getElementById(id);
		htmlSelect.focus();
		htmlSelect.click();
		htmlPage = htmlSelect.getOptionByValue(value).click();
		htmlSelect.blur();
		return htmlPage;
	}
	
	
	//Method to disable all the HtmlUnit web client logs
	public static void disableLogging (WebClient webClient) {
		LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log", "org.apache.commons.logging.impl.NoOpLog");

		java.util.logging.Logger.getLogger("org.htmlunit").setLevel(Level.OFF); 
		java.util.logging.Logger.getLogger("org.apache.commons.httpclient").setLevel(Level.OFF);
		WebClientOptions options = webClient.getOptions();
		options.setCssEnabled(false);

		webClient.setIncorrectnessListener(new IncorrectnessListener() {

		    @Override
		    public void notify(String arg0, Object arg1) {
		        // TODO Auto-generated method stub

		    }
		});
		webClient.setCssErrorHandler(new CSSErrorHandler() {

			@Override
			public void warning(org.htmlunit.cssparser.parser.CSSParseException exception) throws CSSException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void error(org.htmlunit.cssparser.parser.CSSParseException exception) throws CSSException {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void fatalError(org.htmlunit.cssparser.parser.CSSParseException exception) throws CSSException {
				// TODO Auto-generated method stub
				
			}
		});
		webClient.setJavaScriptErrorListener(new JavaScriptErrorListener() {

		    @Override
		    public void timeoutError(HtmlPage arg0, long arg1, long arg2) {
		        // TODO Auto-generated method stub

		    }

		    @Override
		    public void scriptException(HtmlPage arg0, ScriptException arg1) {
		        // TODO Auto-generated method stub

		    }

		    @Override
		    public void malformedScriptURL(HtmlPage arg0, String arg1, MalformedURLException arg2) {
		        // TODO Auto-generated method stub

		    }

		    @Override
		    public void loadScriptError(HtmlPage arg0, URL arg1, Exception arg2) {
		        // TODO Auto-generated method stub

		    }

			@Override
			public void warn(String message, String sourceName, int line, String lineSource, int lineOffset) {
				// TODO Auto-generated method stub
				
			}
		});
		webClient.setHTMLParserListener(new HTMLParserListener() {

			@Override
			public void error(String message, URL url, String html, int line, int column, String key) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void warning(String message, URL url, String html, int line, int column, String key) {
				// TODO Auto-generated method stub
				
			}
		});

		options.setThrowExceptionOnFailingStatusCode(false);
		options.setThrowExceptionOnScriptError(false);
	}
	
	public static void isSiteDown (Page page) throws SegSocialException {
		if (page.isHtmlPage()) {
			HtmlPage htmlPage= (HtmlPage) page;
			Pattern pattern = Pattern.compile("\\s*Pï¿½GINA\\s*NO\\s*DISPONIBLE\\s*", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(htmlPage.asXml());
			if (matcher.find()) {
				throw new OutOfServiceException("Pï¿½gina no disponible");
			}
			pattern = Pattern.compile("\\s*NO\\s*SE\\s*PUEDE\\s*ATENDER\\s*EN\\s*ESTE\\s*MOMENTO\\s*", Pattern.CASE_INSENSITIVE);
			if (matcher.find()) {
				throw new OutOfServiceException("Pï¿½gina no disponible");
			}
		}
	}

	@Deprecated
	public static HtmlPage transformPage(Page page) throws IOException, TransformerException  {
		if(page instanceof HtmlPage)
			return HtmlUnitToolkit.secureTransformHtmlPage((HtmlPage) page);
		return HtmlUnitToolkit.transformXmlPage((XmlPage) page);
	}

	@Deprecated
	public static HtmlPage secureTransformHtmlPage(HtmlPage htmlXmlPage)  {
		try {
			return transformHtmlPage(htmlXmlPage);
		} catch ( Exception e ) {
			return htmlXmlPage; 
		}
	}

	@Deprecated
	public static HtmlPage transformHtmlPage(HtmlPage htmlXmlPage) throws IOException, TransformerException {
		WebClient webClient = htmlXmlPage.getWebClient();
	    
	    String xslStylesheet = getXslStylesheet(htmlXmlPage);

	    XmlPage xslPage = webClient.getPage(xslStylesheet);
	    Source xslSource = new DOMSource(xslPage.getXmlDocument());

	    Source xmlSource = new StreamSource( new StringReader(htmlXmlPage.getElementById("xml").getTextContent()));

	    StringWriter out = new StringWriter();
	    Result outputTarget = new StreamResult(out);

	    URL xslUrl = xslPage.getWebResponse().getWebRequest().getUrl();

	    URIResolver uriResolver = (href, base) -> {
		try {
		    XmlPage hrefPage = webClient.getPage(new URL( xslUrl, href));
		    return new DOMSource(hrefPage.getXmlDocument());
		} catch ( MalformedURLException e ) {
		    throw new TransformerException(e);
		}
		catch (FailingHttpStatusCodeException | IOException e) {
		    throw new TransformerException(e);
		}
	    };

	    TransformerFactory transformerFactory = TransformerFactory.newDefaultInstance();
	    transformerFactory.setURIResolver(uriResolver);

	    Transformer transformer = transformerFactory.newTransformer(xslSource);
	    transformer.setURIResolver(uriResolver);

	    transformer.transform(xmlSource, outputTarget);

	    URL xmlUrl = htmlXmlPage.getWebResponse().getWebRequest().getUrl();
	    
	    String path = xmlUrl.getPath().substring ( 0, xmlUrl.getPath().lastIndexOf("/"));
	    webClient.getPage(String.format("%s://%s%s", xmlUrl.getProtocol(), xmlUrl.getHost(), path));
	    
	    HtmlPage htmlPage = loadHtmlAndJsCodeIntoCurrentWindow(webClient, out.toString(), xmlUrl);
	    return htmlPage;
	}
	
	@Deprecated
	public static HtmlPage transformXmlPage(XmlPage xmlPage) throws IOException, TransformerException {
		WebClient webClient = xmlPage.getWebClient();
	    
	    String xslStylesheet = getXslStylesheet(xmlPage);

	    XmlPage xslPage = webClient.getPage(xslStylesheet);
	    Source xslSource = new DOMSource(xslPage.getXmlDocument());

	    Source xmlSource = new DOMSource(xmlPage.getXmlDocument());

	    StringWriter out = new StringWriter();
	    Result outputTarget = new StreamResult(out);

	    URL xslUrl = xslPage.getWebResponse().getWebRequest().getUrl();

	    URIResolver uriResolver = (href, base) -> {
		try {
		    XmlPage hrefPage = webClient.getPage(new URL( xslUrl, href));
		    return new DOMSource(hrefPage.getXmlDocument());
		} catch ( MalformedURLException e ) {
		    throw new TransformerException(e);
		}
		catch (FailingHttpStatusCodeException | IOException e) {
		    throw new TransformerException(e);
		}
	    };

	    TransformerFactory transformerFactory = TransformerFactory.newDefaultInstance();
	    transformerFactory.setURIResolver(uriResolver);

	    Transformer transformer = transformerFactory.newTransformer(xslSource);
	    transformer.setURIResolver(uriResolver);

	    transformer.transform(xmlSource, outputTarget);

	    URL xmlUrl = xmlPage.getWebResponse().getWebRequest().getUrl();
	    
	    String path = xmlUrl.getPath().substring ( 0, xmlUrl.getPath().lastIndexOf("/"));
	    webClient.getPage(String.format("%s://%s%s", xmlUrl.getProtocol(), xmlUrl.getHost(), path));
	    
	    HtmlPage htmlPage = loadHtmlCodeIntoCurrentWindow(webClient, out.toString(), xmlUrl);
	    return htmlPage;
	}
	
	public static WebResponse transformHtmlPage(WebClient webClient, WebResponse response, Map<String,String> variables, Map<URI,String> uriCache ) throws IOException, TransformerException {

	    URL xslUrl = getXslScript(response);

	    XmlPage xslPage = webClient.getPage(xslUrl);
	    Source xslSource = new DOMSource(xslPage.getXmlDocument());

	    Source xmlSource = new StreamSource( new StringReader(getXmlScript(response)));

	    StringWriter out = new StringWriter();
	    Result outputTarget = new StreamResult(out);

	    URIResolver uriResolver = (href, base) -> {
		try {
//			URI hrefURI = new URI(base).resolve(href);
//		    
//		    if ( uriCache.containsKey(hrefURI )) {
//			    return new StreamSource(new StringReader(uriCache.get(hrefURI)), hrefURI.toURL().toExternalForm());
//		    }

		    XmlPage hrefPage = webClient.getPage(new URL( xslUrl, href));
		    return new DOMSource(hrefPage.getXmlDocument());
		} catch ( MalformedURLException e ) {
		    throw new TransformerException(e);
		}
		catch (FailingHttpStatusCodeException | IOException e) {
		    throw new TransformerException(e);
		}
	    };

	    TransformerFactory transformerFactory = TransformerFactory.newDefaultInstance();
	    transformerFactory.setURIResolver(uriResolver);

	    Transformer transformer = transformerFactory.newTransformer(xslSource);
	    transformer.setURIResolver(uriResolver);

	    transformer.transform(xmlSource, outputTarget);

	    URL xmlUrl = response.getWebRequest().getUrl();
	    
	    String path = xmlUrl.getPath().substring ( 0, xmlUrl.getPath().lastIndexOf("/"));
	    webClient.getPage(String.format("%s://%s%s", xmlUrl.getProtocol(), xmlUrl.getHost(), path));
	    
	    return new WebResponseWrapper(response) {

	    	private String content = out.toString();
	    	
	    	@Override
	    	public String getContentType() {
	    		return super.getContentType();
	    	}
	    	
	    	@Override
	    	public long getContentLength() {
	    		return content.getBytes().length;
	    	}
	    	
	    	@Override
	    	public String getContentAsString() {
	    		return getContentAsString(getContentCharset());
	    	}
	    	
	    	@Override
	    	public String getContentAsString(Charset encoding) {
	    		return new String(content.getBytes(encoding));
	    	}

	    	@Override
	    	public InputStream getContentAsStream() throws IOException {
	    		return new ByteArrayInputStream(content.getBytes());
	    	}
	    	
	    };
	    
	}

	public static WebResponse transformXmlPage(WebClient webClient, WebResponse response, Map<String,String> variables, Map<URI,String> uriCache ) throws IOException, TransformerException {
	    URL xslURL = getXslStylesheet(response);
	    
	    XmlPage xslPage = webClient.getPage(xslURL);
	    Source xslSource = new DOMSource(xslPage.getXmlDocument(), xslURL.toExternalForm());

	    Source xmlSource = new StreamSource(response.getContentAsStream(), response.getWebRequest().getUrl().toExternalForm());
	    
	    StringWriter out = new StringWriter();
	    Result outputTarget = new StreamResult(out);
	    
	    URIResolver uriResolver = (href, base) -> {
			try {
				URI hrefURI = new URI(base).resolve(href);
			    
			    if ( uriCache.containsKey(hrefURI )) {
				    return new StreamSource(new StringReader(uriCache.get(hrefURI)), hrefURI.toURL().toExternalForm());
			    }

			    XmlPage hrefPage = webClient.getPage(hrefURI.toURL());
			    WebResponse hrefResponse = hrefPage.getWebResponse();
			    String hrefContent = hrefResponse.getContentAsString();
			    for (Map.Entry<String,String> variable : variables.entrySet()) {
			    	hrefContent = hrefContent.replace("$"+variable.getKey(), variable.getValue());
				}
			    uriCache.put(hrefURI, hrefContent);
			    return new StreamSource(new StringReader(hrefContent), hrefPage.getBaseURI() );
			    //return new DOMSource(hrefPage.getXmlDocument(), hrefPage.getBaseURI() );
			} catch (FailingHttpStatusCodeException | IOException | URISyntaxException e) {
			    throw new TransformerException(e);
			} 
	    };
	    
	    
	    TransformerFactory transformerFactory = TransformerFactory.newDefaultInstance();
	    transformerFactory.setURIResolver(uriResolver);
	    
	    Transformer transformer = transformerFactory.newTransformer(xslSource);
	    transformer.setURIResolver(uriResolver);
	    
	    transformer.transform(xmlSource, outputTarget);
	    
	    return new WebResponseWrapper(response) {

	    	private String content = out.toString();
	    	
	    	@Override
	    	public String getContentType() {
	    		return super.getContentType()
	    				.replace("xml", "html");
	    	}
	    	
	    	@Override
	    	public long getContentLength() {
	    		return content.getBytes().length;
	    	}
	    	
	    	@Override
	    	public String getContentAsString() {
	    		return getContentAsString(getContentCharset());
	    	}
	    	
	    	@Override
	    	public String getContentAsString(Charset encoding) {
	    		return new String(content.getBytes(encoding));
	    	}

	    	@Override
	    	public InputStream getContentAsStream() throws IOException {
	    		return new ByteArrayInputStream(content.getBytes());
	    	}
	    	
	    	
	    	
	    };
	    
	}

	public static HtmlPage loadHtmlCodeIntoCurrentWindow(final WebClient webClient,  final String htmlCode, final URL url) throws IOException {
	    final HTMLParser htmlParser = webClient.getPageCreator().getHtmlParser();
	    final WebWindow webWindow = webClient.getCurrentWindow();

	    final StringWebResponse webResponse = new StringWebResponse(htmlCode,url);
	    final HtmlPage page = new HtmlPage(webResponse, webWindow);
	    webWindow.setEnclosedPage(page);
	    
	    webClient.getJavaScriptEngine().initialize(webWindow, page);
	    
	    htmlParser.parse(webClient, webResponse, page, false, true);
	    return page;
	}	


	public static HtmlPage loadHtmlAndJsCodeIntoCurrentWindow(final WebClient webClient,  final String htmlCode, final URL url) throws IOException {

        // 1. Ensure JavaScript is enabled (true by default, but good to enforce)
        webClient.getOptions().setJavaScriptEnabled(true);
        webClient.getOptions().setThrowExceptionOnScriptError(false);

        // 2. Wrap your raw HTML string into a WebResponse object
	    final StringWebResponse webResponse = new StringWebResponse(htmlCode,url);

        // 3. Load the page via the WebClient window to trigger the JS engine
        HtmlPage page = (HtmlPage) webClient.loadWebResponseInto(webResponse, webClient.getCurrentWindow());

	    return page;
	}	
	
	public static String getXslStylesheet (XmlPage xmlPage) throws MalformedURLException {
	    
	    return getXslStylesheet(xmlPage.getWebResponse()).toString();
	    
	}

	public static URL getXslStylesheet (WebResponse response) throws MalformedURLException {
	    
	    Matcher matcher = Pattern.compile("xml-stylesheet\\s*type=\"text/xsl\"\\s*href\\s*=\\s*\"(?<href>.*)\"").matcher(response.getContentAsString());
	    matcher.find();
	    String href = matcher.group("href");

	    URL url = response.getWebRequest().getUrl();
	    return new URL(url, href);
	    
	}

	public static boolean hasXslStylesheet (WebResponse response) throws MalformedURLException {
	    return Pattern.compile("xml-stylesheet\\s*type=\"text/xsl\"\\s*href\\s*=\\s*\"(?<href>.*)\"").matcher(response.getContentAsString()).find();
	}

	public static String getXslStylesheet (HtmlPage htmlPage) throws MalformedURLException {
		
	    
	    return getXslScript(htmlPage.getWebResponse()).toString();
	    
	}
	
	public static boolean isXmlScriptPage ( Page page ) {
		return ( page instanceof HtmlPage htmlPage) && htmlPage.getElementById("xml") != null ;
	}

	public static XmlPage getXmlScriptPage ( Page page ) {
		HtmlPage htmlPage = (HtmlPage) page;
		String xml = htmlPage.getElementById("xml").getTextContent();
		try {
			return new XmlPage(new StringWebResponse(xml, htmlPage.getUrl()), htmlPage.getEnclosingWindow());
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static boolean hasXslScript (WebResponse response) throws MalformedURLException {
	    return Pattern.compile("script\\s*id=\"xslUri\"\\s*href\\s*=\\s*\"(?<href>[^\"]*)\"").matcher(response.getContentAsString()).find();
	}

	public static URL getXslScript (WebResponse response) throws MalformedURLException {
		
		//<script id="xslUri" href="/ServiciosAfiliacionRED/templates/afrd/fw4/CU300_ATR66ConsultaNumeroSegSocial/AfrdPaConsultaNumeroSegSocial_ES.xsl" type="text/plain">
	    
	    Matcher matcher = Pattern.compile("script\\s*id=\"xslUri\"\\s*href\\s*=\\s*\"(?<href>[^\"]*)\"").matcher(response.getContentAsString());
	    matcher.find();
	    String href = matcher.group("href");

	    URL url = response.getWebRequest().getUrl();
	    return new URL(url, href);
	    
	}

	public static String getXmlScript (WebResponse response) throws MalformedURLException {
		
		//<script id="xml" type="text/plain">
		// <ProsaXMLData FechaCreacion="09/07/2026 11:45:36.062" Version="1.0">
		// <SPM arqobj="ed"><ARQ.ANALYTICS id="ARQ.ANALYTICS"><content><page_name><![CDATA[fw4/crtr/nuevaconsultacalculos/CrtrPaObtencionAutorizacion]]></page_name>
		// ...
		// ...
		// </ProsaXMLData>
	    
		//Pattern prosaXmlDatapattern = Pattern.compile("script\\s*id=\"xml\"\\s*type=\"text/plain\">(?<xml>.*)</script>", Pattern.DOTALL);
		Pattern prosaXmlDatapattern = Pattern.compile("<ProsaXMLData.*</ProsaXMLData>", Pattern.DOTALL | Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
	    Matcher prosaXmlDataMatcher = prosaXmlDatapattern.matcher(response.getContentAsString());
	    prosaXmlDataMatcher.find();
	    String prosaXmlData = prosaXmlDataMatcher.group();

	    return prosaXmlData;
	    
	}

	public static WebConnectionWrapper transformXmlPage(WebClient webClient, byte[] certificateData, String certificatePassword,
			String certificateType, Map<String,String> variables) {
		return transformXmlPage(webClient, certificateData, certificatePassword, certificateType, variables, (request, response ) -> response );
	}

	public static WebConnectionWrapper transformXmlPage(WebClient webClient, byte[] certificateData, String certificatePassword,
			String certificateType) {
		return transformXmlPage(webClient, certificateData, certificatePassword, certificateType, new HashMap<>(), (request, response ) -> response );
	}

	public static WebConnectionWrapper transformXmlPage(WebClient webClient, byte[] certificateData, String certificatePassword,
			String certificateType, Map<String,String> variables, BiFunction<WebRequest, WebResponse, WebResponse> hacker ) {
		return 
		new WebConnectionWrapper(webClient) {
			Map<URI,String> cache = new HashMap<>();
			@Override
			public WebResponse getResponse(WebRequest request) throws IOException {

				for ( int i = 0; i < 2 ; i++ ) {
					try {
						WebResponse response = super.getResponse(request);
						response = hacker.apply(request, response);
						
						if ("text/xml".equals(response.getContentType()) && hasXslStylesheet(response) ){
							try (WebClient xmlClient = getWebClient(certificateData, certificatePassword, certificateType) ) {
								xmlClient.getOptions().setCssEnabled(false);
								xmlClient.getOptions().setDownloadImages(false);
								xmlClient.getOptions().setJavaScriptEnabled(false);
								xmlClient.getOptions().setUseInsecureSSL(true);
								
								response = transformXmlPage(xmlClient, response, variables, cache);
							} 
						} else if ("text/html".equals(response.getContentType()) && hasXslScript(response) ){
							try (WebClient xmlClient = getWebClient(certificateData, certificatePassword, certificateType) ) {
								xmlClient.getOptions().setCssEnabled(false);
								xmlClient.getOptions().setDownloadImages(false);
								xmlClient.getOptions().setUseInsecureSSL(true);
								
								xmlClient.getOptions().setJavaScriptEnabled(true);
								xmlClient.getOptions().setThrowExceptionOnScriptError(false);
								
								response = transformHtmlPage(xmlClient, response, variables, cache);
							} 
						} 
						
						return response;

					} catch ( Exception e ) {
					}
				}
				
				throw new IOException(request.getUrl().toExternalForm());
				
			}
		};
	}
	
	/**
	 * Pide una URL siguiendo los 302 UNO A UNO (redirect manual), de modo que
	 * cada petición vuelve a leer el CookieManager y adjunta las cookies recién
	 * emitidas. Necesario porque el auto-redirect de HtmlUnit no aplica la cookie
	 * que GetAccess fija en un 302 a la petición que sigue a ese mismo redirect.
	 */
	private static HtmlPage getPageFollowingRedirects(WebClient webClient, URL url)
	        throws IOException, SegSocialException {
	    boolean prev = webClient.getOptions().isRedirectEnabled();
	    webClient.getOptions().setRedirectEnabled(false);
	    try {
	        Page p = webClient.getPage(url);
	        int hops = 0;
	        while (p.getWebResponse().getStatusCode() / 100 == 3 && hops++ < 20) {
	            String loc = p.getWebResponse().getResponseHeaderValue("Location");
	            if (loc == null || loc.isEmpty())
	                break;
	            URL next = new URL(p.getUrl(), loc);   // resuelve absoluto o relativo
	            p = webClient.getPage(next);
	        }
	        return asHtmlPage(p);
	    } finally {
	        webClient.getOptions().setRedirectEnabled(prev);
	    }
	}

	/** El menú AFI-DIRECTO es el único que trae enlaces con ARQ.IDAPP=. */
	private static boolean isAfiDirectoMenu(HtmlPage page) {
	    return page.getAnchors().stream()
	            .anyMatch(a -> a.getHrefAttribute().contains("ARQ.IDAPP="));
	}

	/** Página de "Usuario No Autorizado" de la Sede. */
	private static boolean isNotAuthorized(HtmlPage page) {
	    return page.querySelector(".cuerpo_noautorizado") != null
	        || page.querySelector(".mensajeError") != null;
	}
	
}
