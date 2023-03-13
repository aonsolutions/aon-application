package solutions.aon.seg.social.exception;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

public class HttpInternalServerError extends SegSocialOutOfService {

	public HttpInternalServerError() {

	}

	public HttpInternalServerError(int code) {

	}

	public static void throwHttpInternalServerError(FailingHttpStatusCodeException e)
			throws HttpInternalServerError, SegSocialException {

		switch (e.getStatusCode()) {
		case 500:
			throw new ForbiddenException();
		default:
			throw new StatusCodeException(e.getStatusCode());

		}
	}

}
