package solutions.aon.seg.social.exceptions.statusCode;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import solutions.aon.seg.social.exceptions.SegSocialException;

public class StatusCodeException extends SegSocialException{

	public static void HandleStatusCodeException(FailingHttpStatusCodeException e) throws StatusCodeException 
	{
		switch (e.getStatusCode()) {
		case 403:
			throw new ForbiddenException();
		default:
			throw new StatusCodeException();
		}
	}
	
}
