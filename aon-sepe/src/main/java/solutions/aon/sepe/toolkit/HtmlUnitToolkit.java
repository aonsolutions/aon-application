package solutions.aon.sepe.toolkit;

import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.function.Function;
import java.util.logging.Level;

import org.apache.commons.logging.LogFactory;

import org.htmlunit.cssparser.parser.CSSErrorHandler;
import org.htmlunit.cssparser.parser.CSSException;
import org.htmlunit.BrowserVersion;
import org.htmlunit.ElementNotFoundException;
import org.htmlunit.IncorrectnessListener;
import org.htmlunit.NicelyResynchronizingAjaxController;
import org.htmlunit.ScriptException;
import org.htmlunit.WebClient;
import org.htmlunit.WebClientOptions;
import org.htmlunit.html.DomNode;
import org.htmlunit.html.DomNodeList;
import org.htmlunit.html.HtmlElement;
import org.htmlunit.html.HtmlListItem;
import org.htmlunit.html.HtmlPage;
import org.htmlunit.html.parser.HTMLParserListener;
import org.htmlunit.javascript.JavaScriptErrorListener;

import aon.sepe.exceptions.invalidData.InvalidDataException;
import solutions.aon.sepe.exceptions.SepeException;
import solutions.aon.sepe.exceptions.certificate.InvalidCertificateException;

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
			webClient.setJavaScriptTimeout(15000);
			webClient.setAjaxController(new NicelyResynchronizingAjaxController());
			webClient.getOptions().setSSLClientCertificate(certificateInputStream, certificatePassword,
					certificateType);

			return webClient;
		} catch (RuntimeException e) {
			throw new InvalidCertificateException();
		}
	}

	// GET THE WEB CLIENT OF HTMLUNIT
	public static WebClient getWebClientExplorer(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType) throws InvalidCertificateException {
		try {
			WebClient webClient = new WebClient(BrowserVersion.FIREFOX);
			disableLogging(webClient);
			webClient.getOptions().setCssEnabled(false);
			webClient.getOptions().setDownloadImages(false);
			webClient.setJavaScriptTimeout(15000);
			webClient.setAjaxController(new NicelyResynchronizingAjaxController());
			webClient.getOptions().setSSLClientCertificate(certificateInputStream, certificatePassword,
					certificateType);

			return webClient;
		} catch (RuntimeException e) {
			throw new InvalidCertificateException();
		}
	}

	public static WebClient getWebClientCert(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType) throws SepeException {
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
			throw new SepeException(e.getMessage());
		}
	}

	public static JavaScriptErrorListener jascriptFunctionExceptionError() {
		return new JavaScriptErrorListener() {
			@Override
			public void warn(String message, String sourceName, int line, String lineSource, int lineOffset) {
			}

			@Override
			public void timeoutError(HtmlPage page, long allowedTime, long executionTime) {
			}

			@Override
			public void scriptException(HtmlPage page, ScriptException scriptException) {
			}

			@Override
			public void malformedScriptURL(HtmlPage page, String url, MalformedURLException malformedURLException) {
			}

			@Override
			public void loadScriptError(HtmlPage page, URL scriptUrl, Exception exception) {
			}
		};
	}

	// GET TRIMMED STRING FROM HTML ELEMENT
	public static String getTrimmedById(HtmlPage htmlPage, String id) {
		return Toolkit.removeNBSP(htmlPage.getElementById(id).getTextContent());
	}

	// GETS THE SS STATUS CODE
	public static Integer getSSCode(HtmlPage htmlPage) throws SepeException {
		String status;
		try {
			status = HtmlUnitToolkit.getTrimmedById(htmlPage, "DIL");
			status = Toolkit.removeNBSP(status).replace(" ", "");

			if ((status.length() > 0) && (status.charAt(0) == '*'))
				status = status.substring(1);
			if (status.equals(""))
				return 3083;
			if (!status.contains("*"))
				if (!status.contains("-"))
					throw new SepeException(status);
				else
					return Integer.parseInt(status.substring(0, status.indexOf("-")));

			return Integer.parseInt(status.substring(0, status.indexOf("*")));
		} catch (ElementNotFoundException e) {
			return 3083;
		} catch (NumberFormatException nfe) {
			throw new SepeException();
		}
	}

	// GETS THE SS STATUS CODE
	public static String getSSmessage(HtmlPage htmlPage) {
		try {
			return HtmlUnitToolkit.getTrimmedById(htmlPage, "inicial");
		} catch (Exception e) {
		}
		return "";
	}

	// MANAGES THE EXCEPTIONS
	public static void manageStatusCode(HtmlPage htmlPage) throws SepeException {
		Integer code = htmlPage.getWebResponse().getStatusCode();
		String msg = null;
		InvalidDataException.checkCode(code, msg);
	}

	// MANAGES THE EXCEPTIONS OF NEW UI
	public static void manageStatusMessage(HtmlPage document) throws SepeException {
		DomNodeList<DomNode> errors = document.querySelectorAll(".mensajeError");
		if (errors.size() == 0)
			return;

		StringBuilder errorList = new StringBuilder();
		for (DomNode err : errors) {
			HtmlListItem error = (HtmlListItem) err;
			errorList.append(error.getValueAttribute()).append(" ");
		}
		throw new InvalidDataException(errorList.toString());
	}

	public static HtmlElement createButton(HtmlPage page) {
		// create submit
		HtmlElement el = (HtmlElement) page.createElement("button");
		String s = "submit";
		el.setTextContent(s);
		el.setAttribute("type", s);
		el.setAttribute("value", s);
		el.setAttribute("name", "submitCustom");
		return el;
	}

	// Method to disable all the HtmlUnit web client logs
	public static void disableLogging(WebClient webClient) {
		LogFactory.getFactory().setAttribute("org.apache.commons.logging.Log",
				"org.apache.commons.logging.impl.NoOpLog");

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

}
