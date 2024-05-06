package solutions.aon.circe.toolkit;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Optional;
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
import org.htmlunit.util.WebResponseWrapper;
import org.htmlunit.xml.XmlPage;

import solutions.aon.circe.exception.CSSParseException;
import solutions.aon.circe.exception.InternalException;
import solutions.aon.circe.exception.InvalidCertificateException;
import solutions.aon.circe.exception.OutOfServiceException;
import solutions.aon.circe.exception.SegSocialException;
import solutions.aon.circe.exception.StatusCodeException;

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
	
	
	
	public static <P extends Page> void checkStatusAndDown(P page) throws SegSocialException {
		int statusCode = page.getWebResponse().getStatusCode();
		if (statusCode >= 300)
			throw new StatusCodeException(statusCode);
		isSiteDown(page);
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
