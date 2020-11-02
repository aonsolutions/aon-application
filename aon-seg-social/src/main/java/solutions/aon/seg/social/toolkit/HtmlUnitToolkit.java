package solutions.aon.seg.social.toolkit;

import java.io.InputStream;
import java.util.Optional;
import java.util.function.Function;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;

import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

import solutions.aon.seg.social.exceptions.InvalidCertificateException;
import solutions.aon.seg.social.exceptions.InvalidDataException;
import solutions.aon.seg.social.exceptions.SegSocialException;

public class HtmlUnitToolkit {

	//WAIT FOR A SPECIFIC HTML ELEMENT
	public static <HtmlPage, R> Optional<R> wait4(HtmlPage htmlPage, Function<HtmlPage, R> function)
			throws InterruptedException {
		// try 20 times to wait .5 second each for filling the page.
		for (int i = 0; i < 20; i++) {
			R r = function.apply(htmlPage);
			if (r != null) {
				return Optional.of(r);
			}
			synchronized (htmlPage) {
				htmlPage.wait(500);
			}
		}
		return Optional.empty();
	}

	//GET THE WEB CLIENT OF HTMLUNIT
	public static WebClient getWebClient(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType) throws InvalidCertificateException {
		try {
			WebClient webClient = new WebClient(BrowserVersion.BEST_SUPPORTED);
			webClient.getOptions().setCssEnabled(false);
			webClient.getOptions().setDownloadImages(false);
			webClient.setJavaScriptTimeout(10000);
			webClient.setAjaxController(new NicelyResynchronizingAjaxController());
			webClient.getOptions().setSSLClientCertificate(certificateInputStream, certificatePassword,
					certificateType);
			
			return webClient;	
		}catch(RuntimeException e) {throw new InvalidCertificateException();}
	}
	
	//GET TRIMMED STRING FROM HTML ELEMENT
	public static String getTrimmedById(HtmlPage htmlPage, String id) {
		return Toolkit.removeNBSP(htmlPage.getElementById(id).getTextContent()).trim();
	}
	
	//GETS THE SS STATUS CODE
	public static Integer getSSCode(HtmlPage htmlPage) throws SegSocialException {
		try {
			String status=HtmlUnitToolkit.getTrimmedById(htmlPage, "DIL");
			status=Toolkit.removeNBSP(status).replace(" ", "");
			
			if((status.length()>0)&&(status.charAt(0)=='*'))	status=status.substring(1);
			if(status.equals(""))	return 3083;
			if(status.indexOf("*")==-1) 
				if(status.indexOf("-" ) == -1)	throw new SegSocialException ();
				else return Integer.parseInt(status.substring(0, status.indexOf("-")));

			return Integer.parseInt(status.substring(0, status.indexOf("*")));
		}catch (ElementNotFoundException e) {return 3083;}
		catch(NumberFormatException nfe) {throw new SegSocialException ();}
	}

	//MANAGES THE EXCEPTIONS
	public static void manageStatusCode(HtmlPage htmlPage) throws SegSocialException {
		Integer code = getSSCode(htmlPage);
		InvalidDataException.checkCode(code);
	}

}
