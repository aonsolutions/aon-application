package solutions.aon.seg.social.exception;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import org.htmlunit.FailingHttpStatusCodeException;
import org.htmlunit.WebClient;
import org.htmlunit.html.DomElement;
import org.htmlunit.html.HtmlPage;

import solutions.aon.seg.social.toolkit.HtmlUnitToolkit;

public class UnknownAuthorizedException extends ElementNotFoundException{
	

	public UnknownAuthorizedException(String msg) {
		super(msg);
		}
	public UnknownAuthorizedException() {
	}
	
//	public static void checkAuthorizedElement( int i, DomElement authorizedElement ,final InputStream certificateInputStream, final String certificatePassword, final String certificateType) throws InvalidCertificateException, FailingHttpStatusCodeException, MalformedURLException, IOException, SegSocialException {
//		
//		if (authorizedElement == null) {
//			throw new UnknownAuthorizedException();
//		}
//		try(WebClient webClient = HtmlUnitToolkit.getWebClient(certificateInputStream, certificatePassword, certificateType)){
//			
//			HtmlPage htmlPage = webClient.getPage("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=APR01&E=I&AP=AFIR");
//			
//			 authorizedElement = htmlPage.getElementById("Sub0501210055_1_"+i);
//			 if (i > 5) {
//				throw new UnknownAuthorizedException("El elemento es incorrecto");
//			}
//			 
//}
//	}
}
