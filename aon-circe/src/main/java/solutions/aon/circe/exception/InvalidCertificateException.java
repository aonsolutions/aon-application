package solutions.aon.circe.exception;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.cert.X509Certificate;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;

public class InvalidCertificateException extends SegSocialException{
    public InvalidCertificateException(String msg){super(msg);}
    public InvalidCertificateException(){}
    
	public static void checkCertificate(InputStream is) throws CertificateNotFoundException {
		if(is==null)
			throw new CertificateNotFoundException();
	}
	
	public static void checkCertificate(byte[] certificateData, String certificatePassword)
			throws SegSocialException {
		if(certificateData==null)
			throw new CertificateNotFoundException();
		try {
			InputStream certificateInputStream = new ByteArrayInputStream(certificateData);
			KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
			keystore.load(certificateInputStream, certificatePassword.toCharArray());
			Enumeration<?> aliases = keystore.aliases();
			Date expiryDate = null;
			Calendar cal = Calendar.getInstance();
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			for (; aliases.hasMoreElements();) {
				String alias = (String) aliases.nextElement();
				expiryDate = ((X509Certificate) keystore.getCertificate(alias)).getNotAfter();
				if (expiryDate.compareTo(cal.getTime()) < 0)
					throw new InvalidCertificateException("El certificado ha expirado");
			}
		} catch (Exception e) {
			throw new SegSocialException(e);
		}
	}
}
