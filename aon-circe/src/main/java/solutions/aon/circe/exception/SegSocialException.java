
package solutions.aon.circe.exception;

import java.util.HashMap;
import java.util.Map;

import org.htmlunit.FailingHttpStatusCodeException;

public class SegSocialException extends Exception{

	
	public SegSocialException() {}

	public SegSocialException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public SegSocialException(String message, Throwable cause) {
		super(message, cause);
	}

	public SegSocialException(String message) {
		super(message);
	}

	public SegSocialException(Throwable cause) {
		super(cause);
	}

	
	private static interface ThrowSegSocialException {
		void throwSegSocialException ( FailingHttpStatusCodeException e) throws SegSocialException;
	}
	
	public static final Map<Integer, ThrowSegSocialException> HTTP_MAP =
	new HashMap<Integer, ThrowSegSocialException>() {
		{
			put(403, e -> { throw new ForbiddenException();});
		}
	};

	public static void throwSegSocialException(FailingHttpStatusCodeException e) throws SegSocialException{
		HTTP_MAP.getOrDefault(e.getStatusCode(), ex -> {throw new SegSocialException(ex); }).throwSegSocialException(e);
	}
	
	
}
