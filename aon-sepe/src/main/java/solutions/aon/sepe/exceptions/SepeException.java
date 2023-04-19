
package solutions.aon.sepe.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.htmlunit.FailingHttpStatusCodeException;

import solutions.aon.sepe.exceptions.statusCode.ForbiddenException;

public class SepeException extends Exception {

	public SepeException() {
	}

	public SepeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

	public SepeException(String message, Throwable cause) {
		super(message, cause);
	}

	public SepeException(String message) {
		super(message);
	}

	public SepeException(Throwable cause) {
		super(cause);
	}

	private static interface ThrowSepeException {
		void throwSepeException(FailingHttpStatusCodeException e) throws SepeException;
	}

	public static final Map<Integer, ThrowSepeException> HTTP_MAP = new HashMap<Integer, ThrowSepeException>() {
		{
			put(403, e -> {
				throw new ForbiddenException();
			});
		}
	};

	public static void throwSepeException(FailingHttpStatusCodeException e) throws SepeException {
		HTTP_MAP.getOrDefault(e.getStatusCode(), ex -> {
			throw new SepeException(ex);
		}).throwSepeException(e);
	}

}
