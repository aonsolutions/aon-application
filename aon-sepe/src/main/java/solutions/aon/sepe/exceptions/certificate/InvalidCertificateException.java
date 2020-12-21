package solutions.aon.sepe.exceptions.certificate;

import java.io.InputStream;

import solutions.aon.sepe.exceptions.SepeException;

public class InvalidCertificateException extends SepeException{
	public static void checkCertificate(InputStream is) throws CertificateNotFoundException {
		if(is==null)
			throw new CertificateNotFoundException();
	}
	
}
