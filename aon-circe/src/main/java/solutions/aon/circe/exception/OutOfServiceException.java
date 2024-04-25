package solutions.aon.circe.exception;

import org.htmlunit.FailingHttpStatusCodeException;
import org.htmlunit.html.HtmlPage;

public class OutOfServiceException extends SegSocialException{

	public OutOfServiceException(String string) {
		super(string);
	}
	
	public OutOfServiceException(String string, Throwable motivation) {
		super(string, motivation);
	}
	
	


	
}
