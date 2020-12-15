package solutions.aon.sepe.exceptions.certificate;

import java.io.InputStream;

import solutions.aon.seg.social.exceptions.SegSocialException;

public class InvalidCertificateException extends SegSocialException{
	public static void checkCertificate(InputStream is) throws CertificateNotFoundException {
		if(is==null)
			throw new CertificateNotFoundException();
	}
	
}
