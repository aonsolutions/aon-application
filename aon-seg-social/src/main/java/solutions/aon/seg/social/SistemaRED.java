package solutions.aon.seg.social;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.Collection;
import java.util.Date;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import solutions.aon.seg.social.exceptions.ForbiddenException;
import solutions.aon.seg.social.exceptions.SegSocialException;
import solutions.aon.seg.social.objects.Employee;

public class SistemaRED {

	public SistemaRED() {}
	
	public static Collection<Employee> getEmployees(final byte certificateData [], final String certificatePassword,
			final String certificateType, String regimen, String ccc) throws SegSocialException 
	{
		try  ( InputStream certificateInputStream = new ByteArrayInputStream(certificateData) ) {
			return SistemaRedEmployee.getEmployees(certificateInputStream, certificatePassword, certificateType, regimen, ccc);
		} catch ( IOException e ) {
			throw new SegSocialException(e);
		}
	}

	public static Collection<Employee> getEmployees(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String regimen, String ccc) throws SegSocialException 
	{
		return SistemaRedEmployee.getEmployees(certificateInputStream, certificatePassword, certificateType, regimen, ccc);
	}

	public static Employee getEmployee(final byte certificateData [], final String certificatePassword,
			final String certificateType, String regimen, String ccc, String nss) throws SegSocialException {
		try  ( InputStream certificateInputStream = new ByteArrayInputStream(certificateData) ) {		
			return SistemaRedEmployee.getEmployee(certificateInputStream, certificatePassword, certificateType, regimen, ccc, nss);
		} catch ( IOException e ) {
			throw new SegSocialException(e);
		}
	}	

	public static Employee getEmployee(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String regimen, String ccc, String nss) throws SegSocialException {
		
		return SistemaRedEmployee.getEmployee(certificateInputStream, certificatePassword, certificateType,regimen,  ccc, nss);
	}
	
	
	public static byte[] getTA(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String regimen, String ccc, String nss, Date date) throws SegSocialException {
		return SistemaRED_I.getTADuplicate(certificateInputStream, certificatePassword, certificateType, nss, regimen, ccc, date);
	}

	public static byte[] getTA(final byte certificateData [], final String certificatePassword,
			final String certificateType, String regimen, String ccc, String nss, Date date) throws SegSocialException{
		try  ( InputStream certificateInputStream = new ByteArrayInputStream(certificateData) ) {		
			return SistemaRED_I.getTADuplicate(certificateInputStream, certificatePassword, certificateType, nss, regimen, ccc, date);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}

	public static byte[] getIDC(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String regimen, String ccc, String nss, Date date) throws SegSocialException {
		return SistemaRED_I.getContributionInformation(certificateInputStream, certificatePassword, certificateType, nss, regimen, ccc, date);
	}
	
	public static byte[] getIDC(final byte certificateData [], final String certificatePassword,
			final String certificateType, String regimen, String ccc, String nss, Date date) throws SegSocialException{
		try  ( InputStream certificateInputStream = new ByteArrayInputStream(certificateData) ) {		
			return SistemaRED_I.getContributionInformation(certificateInputStream, certificatePassword, certificateType, nss, regimen, ccc, date);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}
	}
	
	public static byte[] getUp2DateSS(final InputStream certificateInputStream, final String certificatePassword,
			final String certificateType, String regimen, String ccc ) throws SegSocialException {
		return SistemaRED_I.getObligationAwarenessCertificate(certificateInputStream, certificatePassword, certificateType, regimen, ccc);
	}

	public static byte[] getUp2DateSS(final byte certificateData [], final String certificatePassword,
			final String certificateType, String regimen, String ccc ) throws SegSocialException {
		try  ( InputStream certificateInputStream = new ByteArrayInputStream(certificateData) ) {		
			return SistemaRED_I.getObligationAwarenessCertificate(certificateInputStream, certificatePassword, certificateType, regimen, ccc);
		} catch (IOException e) {
			throw new SegSocialException(e);
		}		
	}

	private static void evalSwitch(FailingHttpStatusCodeException e) throws ForbiddenException, SegSocialException {
		switch (e.getStatusCode()) {
		case 403:
			throw new ForbiddenException();
		default:
			throw new SegSocialException(e);
		}
	}

	public static void main(String[] args)
			throws FailingHttpStatusCodeException, MalformedURLException, IOException, InterruptedException {

	}

}
