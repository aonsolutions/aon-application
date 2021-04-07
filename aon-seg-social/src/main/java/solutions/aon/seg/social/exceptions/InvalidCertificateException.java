package solutions.aon.seg.social.exceptions;

import java.io.InputStream;

public class InvalidCertificateException extends SegSocialException{
	public static void checkCertificate(InputStream is) throws CertificateNotFoundException {
		if(is==null)
			throw new CertificateNotFoundException();
	}
	
}
