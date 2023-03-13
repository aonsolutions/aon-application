package solutions.aon.seg.social.exception;

import java.util.HashMap;
import java.util.Map;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

public class SegSocialOutOfService extends Exception {

	public SegSocialOutOfService(String string) {
		super(string);
	}

	public SegSocialOutOfService(String string, Throwable motivation) {
		super(string, motivation);
	}

	public SegSocialOutOfService(FailingHttpStatusCodeException ex) {
		super(ex);
	}

	public SegSocialOutOfService() {

	}

	private static interface ThrowSegSocialOutOfService {
		void ThrowSegSocialOutOfService(FailingHttpStatusCodeException e) throws SegSocialOutOfService;
	}

	public static final Map<Integer, ThrowSegSocialOutOfService> HTTP_MAP = new HashMap<Integer, ThrowSegSocialOutOfService>() {
		{
			put(500, e -> {
				throw new HttpInternalServerError();
			});
		}
	};

	public static void ThrowSegSocialOutOfService(FailingHttpStatusCodeException e) throws SegSocialOutOfService {
		HTTP_MAP.getOrDefault(e.getStatusCode(), ex -> {
			throw new SegSocialOutOfService(ex);
		}).ThrowSegSocialOutOfService(e);
		HTTP_MAP.getOrDefault(e.getMessage(), ex2 -> {
			throw new SegSocialOutOfService(ex2);
		}).ThrowSegSocialOutOfService(e);
	}
}
