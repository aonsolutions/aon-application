package solutions.aon.seg.social.toolkit;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
			webClient.setAjaxController(new NicelyResynchronizingAjaxController());
			webClient.getOptions().setSSLClientCertificateKeyStore(certificateInputStream, certificatePassword,
					certificateType);

			return webClient;
		} catch (RuntimeException e) {
			throw new InvalidCertificateException();
		}
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
			Pattern pattern = Pattern.compile("\\s*P�GINA\\s*NO\\s*DISPONIBLE\\s*", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(htmlPage.asXml());
			if (matcher.find()) {
				throw new OutOfServiceException("P�gina no disponible");
			}
			pattern = Pattern.compile("\\s*NO\\s*SE\\s*PUEDE\\s*ATENDER\\s*EN\\s*ESTE\\s*MOMENTO\\s*", Pattern.CASE_INSENSITIVE);
			if (matcher.find()) {
				throw new OutOfServiceException("P�gina no disponible");
			}
		}
	}

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

	    htmlParser.parse(webClient, webResponse, page, false, false);
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

	public static WebConnectionWrapper transformXmlPage(WebClient webClient, byte[] certificateData, String certificatePassword,
			String certificateType, Map<String,String> variables) {
		return transformXmlPage(webClient, certificateData, certificatePassword, certificateType, variables, (request, response ) -> response );
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
						} 
						
						return response;

					} catch ( Exception e ) {
					}
				}
				
				throw new IOException(request.getUrl().toExternalForm());
				
			}
		};
	}
	
	private static void trace(WebClient webClient) {
		new WebConnectionWrapper(webClient) {
			@Override
			public WebResponse getResponse(WebRequest request) throws IOException {
				System.out.println(request.getUrl());
				return super.getResponse(request);
			}
		};
	}
}
