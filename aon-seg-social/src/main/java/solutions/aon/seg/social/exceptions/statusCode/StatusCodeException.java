package solutions.aon.seg.social.exceptions.statusCode;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import solutions.aon.seg.social.exceptions.SegSocialException;

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
