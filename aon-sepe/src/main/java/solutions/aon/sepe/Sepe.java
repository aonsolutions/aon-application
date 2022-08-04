package solutions.aon.sepe;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Date;
import java.util.Optional;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import aon.sepe.objects.Certificates;
import aon.sepe.objects.Contract;
import aon.sepe.objects.ContractExtension;
import aon.sepe.objects.CopyBasic;
import solutions.aon.sepe.exceptions.SepeException;

public class Sepe {
	
	public static byte[] getContratoPdf( final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, Date fini, Date fend) throws SepeException {
		return Contrata.getContratoPdf(certificateInputStream, certificatePassword, certificateType, ipf, fini, fend, Optional.empty());
	}
	
	/**
	 * COPY BASIC CONTRACT
	 */
	public static byte[] getCopyBasicPdf(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, Date fini, Date fend) throws SepeException {
			return Contrata.getCopyBasicPdf(certificateInputStream, certificatePassword, certificateType, ipf, fini, fend, Optional.empty());
	}
	
	public static byte[] getTransformationPdf(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, String cif, Date fini) throws SepeException {
			return Contrata.getTransformationPdf(certificateInputStream, certificatePassword, certificateType, ipf, cif, fini, Optional.empty());
	}
	
	public static byte[] getTransformationCopyBasicPdf(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, String cif, Date fini) throws SepeException {
			return Contrata.getTransformationCopyBasicPdf(certificateInputStream, certificatePassword, certificateType, ipf, cif, fini, Optional.empty());
	}
	
	public static byte[] getContratoPdf( final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String sepeId) throws SepeException {
		return Contrata.getContratoPdf(certificateInputStream, certificatePassword, certificateType, null, null, null, Optional.ofNullable(sepeId));
	}
	
	public static byte[] getCopyBasicPdf(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String sepeId) throws SepeException {
			return Contrata.getCopyBasicPdf(certificateInputStream, certificatePassword, certificateType, null, null, null, Optional.ofNullable(sepeId));
	}
	
	public static byte[] getTransformationPdf(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String sepeId) throws SepeException {
			return Contrata.getTransformationPdf(certificateInputStream, certificatePassword, certificateType, null, null, null, Optional.ofNullable(sepeId));
	}
	
	public static byte[] getTransformationCopyBasicPdf(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String sepeId) throws SepeException {
			return Contrata.getTransformationCopyBasicPdf(certificateInputStream, certificatePassword, certificateType, null, null, null, Optional.ofNullable(sepeId));
	}

	public static byte[] certEnterprisePdf(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String nif, Date fecha) throws SepeException {
			return Certificado.getCertEnterprisePdf(certificateInputStream, certificatePassword, certificateType, nif, fecha);
	}

	public static byte[] certEnterprise(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, Certificates certificates) throws SepeException {
			return Certificado.sendCertEnterprise(certificateInputStream, certificatePassword, certificateType, certificates);
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
			final String certificateType, CopyBasic copyBasic) throws SepeException  {
		return Contrata.sendCopyBasic(certificateInputStream, certificatePassword, certificateType, copyBasic);
	}
	
	public static void sendTransformation(final InputStream certificateInputStream,
			final String certificatePassword, final String certificateType, Contract cto, CopyBasic copyBasic) throws SepeException  {
		Contrata.sendTransformation(certificateInputStream, certificatePassword, certificateType, cto, copyBasic);
	}
	
	public static String sendTransformationCopyBasic(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, Date fini, Date fend, CopyBasic.FirmType firmType, String workAddress, String restContract, String cif, Date startDateContract) throws SepeException  {
		CopyBasic copyBasic = new CopyBasic()
		.setIpf(ipf)
		.setFini(fini)
		.setFend(fend)
		.setFirmType(firmType)
		.setWorkAddress(workAddress)
		.setRestContract(restContract);
		return sendTransformationCopyBasic(certificateInputStream, certificatePassword, certificateType, copyBasic, cif, startDateContract);
	}
	
	public static String sendTransformationCopyBasic(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, CopyBasic copyBasic, String cif, Date startDateContract) throws SepeException  {
		return Contrata.sendTransformationCopyBasic(certificateInputStream, certificatePassword, certificateType, copyBasic, cif, startDateContract);
	}
	
	public static void removeContrato(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ide) throws SepeException  {
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
	
	public static Contract getTransformationData(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String ipf, String cif, Date oldDateIniContract, Optional<String> sepeId) throws SepeException {
		return Contrata.getTransformationData(certificateInputStream, certificatePassword, certificateType, ipf, cif, oldDateIniContract, sepeId);
	}
	
	public static void validateCert(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType) throws SepeException{
		Contrata.validateCert(certificateInputStream, certificatePassword, certificateType);
	}
	
	/**
	 * 
	 * @param certificateInputStream
	 * @param certificatePassword
	 * @param certificateType
	 * @param contractExtension
	 * @return String sepeId prorroga
	 * @throws SepeException
	 */
	public static String sendContractExtension(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, ContractExtension contractExtension) throws SepeException  {
		return Contrata.sendContrataExtension(certificateInputStream, certificatePassword, certificateType, contractExtension);
	}
	
	public static void main(String[] args)
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {
	}

}
