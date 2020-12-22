package solutions.aon.sepe;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Date;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import solutions.aon.sepe.exceptions.SepeException;

public class Sepe {
	
	public static byte[] getContratoPdf( final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, Date fini, Date fend) throws Exception {
		return Contrato.contratoPdf(certificateInputStream, certificatePassword, certificateType, ipf, fini, fend);
	}

	public static byte[] getCopyBasicPdf(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, Date fini, Date fend) throws Exception {
			return Contrato.getCopyBasicPdf(certificateInputStream, certificatePassword, certificateType, ipf, fini, fend);
	}
	
	public static byte[] transformacionsPdf(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, Date fini) throws Exception {
			return Contrato.transformacionsPdf(certificateInputStream, certificatePassword, certificateType, ipf, fini);
	}
	
	public static byte[] certEnterprisePdf(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String nif, Date fecha) throws SepeException {
			return Certificado.certEnterprisePdf(certificateInputStream, certificatePassword, certificateType, nif, fecha);
	}
	
	public static void main(String[] args)
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
	}

}
