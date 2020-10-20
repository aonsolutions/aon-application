package solutions.aon.seg.social;

import java.io.InputStream;
import java.util.Optional;
import java.util.function.Function;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.ElementNotFoundException;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.sun.org.apache.xerces.internal.util.Status;

import solutions.aon.seg.social.exceptions.SegSocialException;

public class HtmlUnitToolkit {

	//WAIT FOR A SPECIFIC HTML ELEMENT
	static <HtmlPage, R> Optional<R> wait4(HtmlPage htmlPage, Function<HtmlPage, R> function)
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
	static WebClient getWebClient(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType) {
			WebClient webClient = new WebClient(BrowserVersion.BEST_SUPPORTED);
			webClient.getOptions().setCssEnabled(false);
			webClient.getOptions().setDownloadImages(false);
			webClient.setJavaScriptTimeout(10000);
			webClient.setAjaxController(new NicelyResynchronizingAjaxController());
			webClient.getOptions().setSSLClientCertificate(certificateInputStream, certificatePassword,
					certificateType);
			return webClient;		
	}
	
	//GET TRIMMED STRING FROM HTML ELEMENT
	public static String getTrimmedById(HtmlPage htmlPage, String id) {
		return Toolkit.removeNBSP(htmlPage.getElementById(id).getTextContent()).trim();
	}
	
	public static Integer getSSCode(HtmlPage htmlPage) throws SegSocialException {
		try {
			String status=HtmlUnitToolkit.getTrimmedById(htmlPage, "DIL");
			return Integer.parseInt(status.substring(0, status.indexOf("*")));
			
		}catch (ElementNotFoundException e) {
			return 3083;
		}
		catch(NumberFormatException nfe) {
			throw new SegSocialException ();
		}
		
	}


}
