package solutions.aon.seg.social.exception;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class OutOfServiceException extends SegSocialException{

	public OutOfServiceException(String string) {
		super(string);
	}
	
	public OutOfServiceException(String string, Throwable motivation) {
		super(string, motivation);
	}
	
	


	
}
