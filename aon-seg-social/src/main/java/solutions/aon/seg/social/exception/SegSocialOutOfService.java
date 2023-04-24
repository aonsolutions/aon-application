package solutions.aon.seg.social.exception;

import java.util.HashMap;
import java.util.Map;

import org.htmlunit.FailingHttpStatusCodeException;

public class SegSocialOutOfService extends Exception {

	public SegSocialOutOfService(String string) {
		super(string);
	}

	public SegSocialOutOfService(String string, Throwable motivation ,boolean enableSuppression, boolean writableStackTrace) {
		super(string, motivation, enableSuppression, writableStackTrace);
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
		int statusCode = 500;
		HTTP_MAP.getOrDefault(e.getStatusCode(), ex -> {
			if (statusCode == e.getStatusCode() ) {
				System.out.println("La pagina esta caida");
				throw new SegSocialOutOfService(ex);
			}
			
		}).ThrowSegSocialOutOfService(e);
		
	}
}
