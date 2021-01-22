package solutions.aon.sepe;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Date;
import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import aon.sepe.objects.Contract;
import solutions.aon.sepe.Contrato.FirmType;
import solutions.aon.sepe.exceptions.SepeException;

public class Sepe {
	
	public static byte[] getContratoPdf( final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, Date fini, Date fend) throws Exception {
		return Contrato.contratoPdf(certificateInputStream, certificatePassword, certificateType, ipf, fini, fend);
	}

	public static byte[] getCopyBasicPdf(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, Date fini, Date fend) throws SepeException {
			return Contrato.getCopyBasicPdf(certificateInputStream, certificatePassword, certificateType, ipf, fini, fend);
	}
	
	public static byte[] transformacionsPdf(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, Date fini) throws SepeException {
			return Contrato.transformacionsPdf(certificateInputStream, certificatePassword, certificateType, ipf, fini);
	}
	
	public static byte[] certEnterprisePdf(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String nif, Date fecha) throws SepeException {
			return Certificado.certEnterprisePdf(certificateInputStream, certificatePassword, certificateType, nif, fecha);
	}
	
	public static String sendContracto(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, Contract cto) throws SepeException  {
		return Contrato.contrato(certificateInputStream, certificatePassword, certificateType, cto);
	}
	
	public static String sendContratoCopyBasic(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType,  String ipf, Date fini, Date ffin, FirmType typeFirm, String workAddress, String restContract) throws SepeException  {
		return Contrato.contratoCopyBasic(certificateInputStream, certificatePassword, certificateType, ipf, fini, ffin, typeFirm, workAddress, restContract);
	}
	
	public static void removeContrato(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType,  String ide) throws SepeException  {
		Contrato.removeContrato(certificateInputStream, certificatePassword, certificateType, ide);
	}
	
	public static void removeTransformation(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType,  String ide) throws SepeException  {
		Contrato.removeTransformation(certificateInputStream, certificatePassword, certificateType, ide);
	}
	
	public static String getCi(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, Date fini, Date fend) throws SepeException {
		return Contrato.getContratoId(certificateInputStream, certificatePassword, certificateType, ipf, fini, fend);
	}
	
	public static void main(String[] args)
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
	}

}
