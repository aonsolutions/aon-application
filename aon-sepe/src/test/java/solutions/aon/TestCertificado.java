package solutions.aon;


import java.io.InputStream;
import java.util.Base64;
import java.util.Date;
import org.junit.Ignore;
import org.junit.Test;

import aon.sepe.objects.Certificates;
import solutions.aon.sepe.Certificado;
import solutions.aon.sepe.toolkit.Toolkit;

public class TestCertificado {
	
	@Test
	@Ignore
	public void testCertEnterprisePdf() {
		try (final InputStream certificateInputStream = TestCertificado.class.getResourceAsStream("SEPE.p12")) {			
			String certificatePassword = "aon@FNMT";
			String certificateType = "pkcs12";
			String nif = "72740703Y";
			@SuppressWarnings("deprecation")
			Date fecha =  new Date("2016/05/06");
			byte[] pdf = Certificado.certEnterprisePdf(certificateInputStream, certificatePassword, certificateType, nif, fecha);
			System.out.println( new String(Base64.getEncoder().encode(pdf)));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	@Ignore
	public void testCertEnterprise() {
		try (final InputStream certificateInputStream = TestCertificado.class.getResourceAsStream("SEPE.p12")) {			
			String certificatePassword = "aon@FNMT";
			String certificateType = "pkcs12";
			Certificates certificates = null;
			Certificado.certEnterprise(certificateInputStream, certificatePassword, certificateType, certificates);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
