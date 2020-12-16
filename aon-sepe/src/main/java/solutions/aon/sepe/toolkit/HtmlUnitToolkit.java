package solutions.aon.sepe.toolkit;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Optional;
import java.util.function.Function;

import com.gargoylesoftware.css.parser.CSSException;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;

import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.ScriptException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.*;
import com.gargoylesoftware.htmlunit.javascript.JavaScriptErrorListener;

import solutions.aon.seg.social.exceptions.SegSocialException;
import solutions.aon.sepe.exceptions.certificate.InvalidCertificateException;
import solutions.aon.seg.social.exceptions.internal.CSSParseException;
import solutions.aon.seg.social.exceptions.internal.InternalException;
import solutions.aon.seg.social.exceptions.invalidData.InvalidDataException;

public class HtmlUnitToolkit {

	//WAIT FOR A SPECIFIC HTML ELEMENT
	public static <HtmlPage, R> Optional<R> wait4(HtmlPage htmlPage, Function<HtmlPage, R> function)
			throws InterruptedException {
		// try 20 times to wait .5 second each for filling the page.
		for (int i = 0; i < 20; i++) {
			R r = function.apply(htmlPage);
			if (r != null)  return Optional.of(r);
			synchronized (htmlPage) {htmlPage.wait(500);}
		}
		return Optional.empty();
	}

	//GET THE WEB CLIENT OF HTMLUNIT
	public static WebClient getWebClient(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType) throws InvalidCertificateException {
		try {
			WebClient webClient = new WebClient(BrowserVersion.BEST_SUPPORTED);
			webClient.getOptions().setCssEnabled(false);
			webClient.getOptions().setJavaScriptEnabled(true);
			webClient.getOptions().setDownloadImages(false);
			webClient.setJavaScriptTimeout(15000);
			webClient.setAjaxController(new NicelyResynchronizingAjaxController());
			webClient.getOptions().setSSLClientCertificate(certificateInputStream, certificatePassword,
					certificateType);
			
			webClient.getOptions().setThrowExceptionOnScriptError(false);
			webClient.setJavaScriptErrorListener(jascriptFunctionExceptionError());
			return webClient;	
		}
		catch(RuntimeException e) {throw new InvalidCertificateException();}
	}
	
	private static JavaScriptErrorListener jascriptFunctionExceptionError() {
		return new JavaScriptErrorListener() {
			
			@Override
			public void warn(String message, String sourceName, int line, String lineSource, int lineOffset) {
				System.out.println("warn");
			}
			
			@Override
			public void timeoutError(HtmlPage page, long allowedTime, long executionTime) {
				System.out.println("timeoutError");
			}
			
			@Override
			public void scriptException(HtmlPage page, ScriptException scriptException) {
				System.out.println("scriptException");
			}
			
			@Override
			public void malformedScriptURL(HtmlPage page, String url,
					MalformedURLException malformedURLException) {
				System.out.println("malformedScriptURL");
			}
			
			@Override
			public void loadScriptError(HtmlPage page, URL scriptUrl, Exception exception) {
				System.out.println("loadScriptError");
			}
		};
	}
	

	
	//GET TRIMMED STRING FROM HTML ELEMENT
	public static String getTrimmedById(HtmlPage htmlPage, String id) {
		return Toolkit.removeNBSP(htmlPage.getElementById(id).getTextContent());
	}
	
	//GETS THE SS STATUS CODE
	public static Integer getSSCode(HtmlPage htmlPage) throws SegSocialException {
		String status;
		try {
			status=HtmlUnitToolkit.getTrimmedById(htmlPage, "DIL");
			status=Toolkit.removeNBSP(status).replace(" ", "");
			
			if((status.length()>0)&&(status.charAt(0)=='*'))	status=status.substring(1);
			if(status.equals(""))	return 3083;
			if(!status.contains("*"))
				if(!status.contains("-"))	throw new SegSocialException (status);
				else return Integer.parseInt(status.substring(0, status.indexOf("-")));

			return Integer.parseInt(status.substring(0, status.indexOf("*")));
		}catch (ElementNotFoundException e) {return 3083;}
		catch(NumberFormatException nfe) {throw new SegSocialException ();}
	}

	//GETS THE SS STATUS CODE
	public static String getSSmessage(HtmlPage htmlPage) throws SegSocialException {
		try { return HtmlUnitToolkit.getTrimmedById(htmlPage, "DIL"); }
		catch (ElementNotFoundException e) {return "";}
		catch(NumberFormatException e) {throw new SegSocialException (e);}
	}

	//MANAGES THE EXCEPTIONS
	public static void manageStatusCode(HtmlPage htmlPage) throws SegSocialException {
		Integer code = getSSCode(htmlPage);
		String msg = getSSmessage(htmlPage);
		System.out.println(code);
		InvalidDataException.checkCode(code,msg);
	}

	//MANAGES THE EXCEPTIONS OF NEW UI
	public static void manageStatusMessage(HtmlPage document) throws SegSocialException {
		DomNodeList<DomNode> errors = document.querySelectorAll(".mensajeError");
		if(errors.size() == 0) return;

		StringBuilder errorList = new StringBuilder();
		for (DomNode err: errors) {
			HtmlListItem error = (HtmlListItem) err;
			errorList.append(error.getValueAttribute()).append(" ");
		}
		throw new InvalidDataException(errorList.toString());
	}

	//SHOW HTML ELEMENTS AS XML
	public static void showAsXML(HtmlElement ...elements) {
		for (HtmlElement e : elements) { if(e != null) System.out.println(e.asXml());}
	}
	
	//SHOW HTML ELEMENTS AS TEXT
	public static void showAsText(HtmlElement ...elements) {
		for (HtmlElement e : elements) { if(e != null) System.out.println(e.asText());}
	}

	//GET ELEMENT BY NAME
	public static HtmlElement getByName(HtmlPage page, String name) throws InternalException {
		try{return page.querySelector("*[name = " + name + "]");}
		catch(CSSException e){ throw new CSSParseException();}
		catch(Exception e){ throw new InternalException();}
	}

	//GET ELEMENT BY ID
	public static HtmlElement getById(HtmlPage page, String id) throws InternalException {
		try{return page.querySelector("#" + id);}
		catch(CSSException e){ throw new CSSParseException();}
		catch(Exception e){ throw new InternalException();}
	}

	//GET ELEMENT BY CLASS
	public static HtmlElement getByClass(HtmlPage page, String _class) throws InternalException {
		try{return page.querySelector("." + _class);}
		catch(CSSException e){ throw new CSSParseException();}
		catch(Exception e){ throw new InternalException();}
	}
	
	public static HtmlElement createButton(HtmlPage page, HtmlForm form) {
	      //create submit 
	      HtmlElement button = (HtmlElement) page.createElement("button");
	      button.setAttribute("type", "submit");
	      
	     form.appendChild(button);
	     return button;
	}

}
