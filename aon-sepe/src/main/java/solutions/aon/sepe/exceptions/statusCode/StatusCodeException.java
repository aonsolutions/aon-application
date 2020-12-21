package solutions.aon.sepe.exceptions.statusCode;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import solutions.aon.sepe.exceptions.SepeException;

public class StatusCodeException extends SepeException{

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
