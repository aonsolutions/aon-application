package solutions.aon.sepe;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Date;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import aon.sepe.objects.Certificates;
import aon.sepe.objects.Contract;
import aon.sepe.objects.CopyBasic;
import solutions.aon.sepe.exceptions.SepeException;

public class Sepe {
	
	public static byte[] getContratoPdf( final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, Date fini, Date fend) throws SepeException {
		return Contrata.contratoPdf(certificateInputStream, certificatePassword, certificateType, ipf, fini, fend);
	}

	public static byte[] getCopyBasicPdf(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, Date fini, Date fend) throws SepeException {
			return Contrata.getCopyBasicPdf(certificateInputStream, certificatePassword, certificateType, ipf, fini, fend);
	}
	
	public static byte[] transformacionsPdf(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, Date fini) throws SepeException {
			return Contrata.transformacionsPdf(certificateInputStream, certificatePassword, certificateType, ipf, fini);
	}
	
	public static byte[] certEnterprisePdf(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String nif, Date fecha) throws SepeException {
			return Certificado.certEnterprisePdf(certificateInputStream, certificatePassword, certificateType, nif, fecha);
	}

	public static byte[] certEnterprise(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, Certificates certificates) throws SepeException {
			return Certificado.certEnterprise(certificateInputStream, certificatePassword, certificateType, certificates);
	}
	
	public static String sendContract(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, Contract cto) throws SepeException  {
		return Contrata.sendContrata(certificateInputStream, certificatePassword, certificateType, cto);
	}
	
	public static String sendContratoCopyBasic(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType,  String ipf, Date fini, Date fend, CopyBasic.FirmType firmType, String workAddress, String restContract) throws SepeException  {
		CopyBasic copyBasic = new CopyBasic()
		.setIpf(ipf)
		.setFini(fini)
		.setFend(fend)
		.setFirmType(firmType)
		.setWorkAddress(workAddress)
		.setRestContract(restContract);
		return sendCopyBasic(certificateInputStream, certificatePassword, certificateType, copyBasic);
	}
	
	public static String sendCopyBasic(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType,  CopyBasic copyBasic) throws SepeException  {
		return Contrata.sendCopyBasic(certificateInputStream, certificatePassword, certificateType, copyBasic);
	}
	
	public static void sendTransformation(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, Contract cto, CopyBasic copyBasic) throws SepeException  {
		Contrata.sendTransformation(certificateInputStream, certificatePassword, certificateType, cto, copyBasic);
	}
	
	public static void removeContrato(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType,  String ide) throws SepeException  {
		Contrata.removeContrato(certificateInputStream, certificatePassword, certificateType, ide);
	}
	
	public static void removeTransformation(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType,  String ide) throws SepeException  {
		Contrata.removeTransformation(certificateInputStream, certificatePassword, certificateType, ide);
	}
	
	public static Contract getContractData(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, Date fini, Date fend) throws SepeException {
		return Contrata.getContractData(certificateInputStream, certificatePassword, certificateType, ipf, fini, fend);
	}
	
	public static void validateCert(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType) throws SepeException{
		Contrata.validateCert(certificateInputStream, certificatePassword, certificateType);
	}
	
	public static void main(String[] args)
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
	}

}
