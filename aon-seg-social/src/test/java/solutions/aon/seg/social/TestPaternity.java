package solutions.aon.seg.social;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.Ignore;
import org.junit.Test;

import solutions.aon.seg.social.exception.CertificateNotFoundException;
import solutions.aon.seg.social.exception.InvalidCertificateException;
import solutions.aon.seg.social.exception.PaternityNotFoundException;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.StatusCodeException;
import solutions.aon.seg.social.object.PaternityCertificate;

public class TestPaternity {
	private static final String CERTIFICATE_PASSWORD = "1234";
	private static final String CERTIFICATE_TYPE = "pkcs12"; 
	private static final String CERTIFICATE_PATH =  System.getProperty("user.home")+"/CERT.pfx"; 
	

	@Test
	@Ignore
	public void testGrabarCertificadoWrongCertificateType() {
		try(final InputStream certificateInputStream=TestPaternity.class.getResourceAsStream("FNMT.p12")){
			
			Date startDate= new SimpleDateFormat("dd-MM-yyyy").parse("28-10-2020");
			Date endDate= new SimpleDateFormat("dd-MM-yyyy").parse("24-11-2020");
			Paternity.sendPaternity(certificateInputStream, "jg@FNMT", "pkcs32", "011017250195", "0111", "01105360062", "58025118M", 
					PaternityCertificate.ApplicantType.MADRE_BIOLOGICA, PaternityCertificate.ReasonType.NACIMIENTO_HIJO, startDate, endDate, 1200, 1200, 28);
			fail("Should have failed");
		} catch (CertificateNotFoundException e) {
			fail("Certificate path is wrong");
		} catch(InvalidCertificateException e) {
			assertTrue(true);
		} catch (StatusCodeException sce) {
			assertTrue(true);
		} catch(SegSocialException e) {
			fail("Wrong data");
		} catch (ParseException e) {
			fail("Date typed wrong");
		} catch (FileNotFoundException e1) {
			fail("Certificate file does not exist");
		} catch (IOException e1) {
			fail("Certificate error");
		}
	}

	@Test
	@Ignore
	public void testVoidCertificateNotFound() {
		try(final InputStream certificateInputStream=TestPaternity.class.getResourceAsStream("FNMT.p12")){
			
			Date startDate= new SimpleDateFormat("dd-MM-yyyy").parse("01-10-2020");
			Date endDate= new SimpleDateFormat("dd-MM-yyyy").parse("04-10-2020");
			try {
				Paternity.removePaternity(certificateInputStream, "jg@FNMT", "pkcs12", "011017250195", "0111", "01105360062", startDate, endDate, Optional.empty());	
			}catch (PaternityNotFoundException e) {
				assertTrue(true);
			}
		} catch (CertificateNotFoundException e) {
			fail("Certificate path is wrong");
		} catch (StatusCodeException sce) {
			assertTrue(true);
		} catch(SegSocialException e) {
			fail("Wrong data");
		} catch (ParseException e) {
			fail("Date typed wrong");
		} catch (FileNotFoundException e1) {
			fail("Certificate file does not exist");
		} catch (IOException e1) {
			fail("Certificate error");
		}
	}
	
	@Test
	@Ignore
	public void testGetCertificatePdfWrongCertificateKey() {
		try(final InputStream certificateInputStream=TestPaternity.class.getResourceAsStream("FN")){
			
			Date startDate= new SimpleDateFormat("dd-MM-yyyy").parse("28-10-2020");
			Date endDate= new SimpleDateFormat("dd-MM-yyyy").parse("04-11-2020");
			Paternity.getCertificatePdf(certificateInputStream, "jg@FNMT4", "pkcs12", "011017250195", "0111", "01105360062", startDate, endDate, Optional.empty());	
			fail("Shouldn't do anything");
		} catch (InvalidCertificateException e) {
			assertTrue(true);
		} catch (StatusCodeException sce) {
			assertTrue(true);
		} catch(SegSocialException e) {
			fail("Wrong data");
		} catch (ParseException e) {
			fail("Date typed wrong");
		} catch (FileNotFoundException e1) {
			fail("Certificate file does not exist");
		} catch (IOException e1) {
			fail("Certificate error");
		}
	}
	
	@Test
	@Ignore
	public void testConsultCertificatesPdfOk() {
		try (final InputStream certificateInputStream = new FileInputStream(CERTIFICATE_PATH) ) {	
			
			Date startDate= new SimpleDateFormat("dd-MM-yyyy").parse("01-01-2020");
			Date endDate= new SimpleDateFormat("dd-MM-yyyy").parse("10-01-2022");
			Optional<String> nss = Optional.of("281468615302");
		    List<PaternityCertificate> paternityCertificates = Paternity.getPaternitys(certificateInputStream, CERTIFICATE_PASSWORD, CERTIFICATE_TYPE, "0111", "01105360062", startDate, endDate, nss, Optional.empty());	
			for (PaternityCertificate paternity : paternityCertificates) {
				System.out.println(paternity);
			}
			System.out.println(paternityCertificates.size());
		} catch (Exception e) {
			e.printStackTrace();
		} 
	}
	
}
