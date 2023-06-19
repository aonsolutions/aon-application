package solutions.aon.seg.social.toolkit;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.LogFactory;
import org.htmlunit.BrowserVersion;
import org.htmlunit.DefaultCredentialsProvider;
import org.htmlunit.ElementNotFoundException;
import org.htmlunit.FailingHttpStatusCodeException;
import org.htmlunit.IncorrectnessListener;
import org.htmlunit.NicelyResynchronizingAjaxController;
import org.htmlunit.Page;
import org.htmlunit.ScriptException;
import org.htmlunit.WebClient;
import org.htmlunit.WebClientOptions;
import org.htmlunit.WebRequest;
import org.htmlunit.WebResponse;
import org.htmlunit.WebResponseData;
import org.htmlunit.cssparser.parser.CSSErrorHandler;
import org.htmlunit.cssparser.parser.CSSException;
import org.htmlunit.html.DomElement;
import org.htmlunit.html.DomNode;
import org.htmlunit.html.DomNodeList;
import org.htmlunit.html.HtmlAnchor;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlListItem;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.parser.HTMLParserListener;
import org.htmlunit.javascript.JavaScriptErrorListener;

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
			webClient.getOptions().setSSLClientCertificate(certificateInputStream, certificatePassword,
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
			webClient.getOptions().setSSLClientCertificate(certificateInputStream, certificatePassword,
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
			creds.addCredentials(user, password);
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
			WebClient webClient = new WebClient(BrowserVersion.INTERNET_EXPLORER);
			disableLogging(webClient);
			webClient.getOptions().setCssEnabled(false);
			webClient.getOptions().setDownloadImages(false);
			webClient.setJavaScriptTimeout(10000);
			webClient.setAjaxController(new NicelyResynchronizingAjaxController());
			webClient.getOptions().setSSLClientCertificate(certificateInputStream, certificatePassword,
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
			Pattern pattern = Pattern.compile("\\s*PÁGINA\\s*NO\\s*DISPONIBLE\\s*", Pattern.CASE_INSENSITIVE);
			Matcher matcher = pattern.matcher(htmlPage.asXml());
			if (matcher.find()) {
				throw new OutOfServiceException("Página no disponible");
			}
			pattern = Pattern.compile("\\s*NO\\s*SE\\s*PUEDE\\s*ATENDER\\s*EN\\s*ESTE\\s*MOMENTO\\s*", Pattern.CASE_INSENSITIVE);
			if (matcher.find()) {
				throw new OutOfServiceException("Página no disponible");
			}
		}
	}
}
