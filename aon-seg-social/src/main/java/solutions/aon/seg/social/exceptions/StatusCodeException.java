package solutions.aon.seg.social.exceptions;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

public class StatusCodeException extends SegSocialException{

	public StatusCodeException(){}
	public StatusCodeException(int code){super("Status code : " + code);}

	public static void HandleStatusCodeException(FailingHttpStatusCodeException e) throws StatusCodeException 
	{
		switch (e.getStatusCode()) {
		case 403:	throw new ForbiddenException();
		default:	throw new StatusCodeException(e.getStatusCode());
		}
	}
	
}
