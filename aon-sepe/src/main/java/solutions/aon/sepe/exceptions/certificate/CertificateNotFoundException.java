package solutions.aon.sepe.exceptions.certificate;

public class CertificateNotFoundException extends InvalidCertificateException {
	public CertificateNotFoundException() {
	}

	public CertificateNotFoundException(String msg) {
		super(msg);
	}
}
