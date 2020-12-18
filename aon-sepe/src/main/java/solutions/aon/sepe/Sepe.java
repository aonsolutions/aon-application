package solutions.aon.sepe;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Date;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

public class Sepe {
	
	public static byte[] getContratoPdf( final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, Date fini, Date fend) throws Exception {
		return Contrato.contratoPdf(certificateInputStream, certificatePassword, certificateType, ipf, fini, fend);
	}

	public static byte[] getCopyBasicPdf(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, Date fini, Date fend) throws Exception {
			return Contrato.getCopyBasicPdf(certificateInputStream, certificatePassword, certificateType, ipf, fini, fend);
	}
	
	public static void main(String[] args)
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
	}

}
