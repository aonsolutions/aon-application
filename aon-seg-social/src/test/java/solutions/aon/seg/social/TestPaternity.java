package solutions.aon.seg.social;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.junit.Ignore;
import org.junit.Test;

import solutions.aon.seg.social.Paternity;
import solutions.aon.seg.social.exception.CertificateNotFoundException;
import solutions.aon.seg.social.exception.InvalidCertificateException;
import solutions.aon.seg.social.exception.PaternityException;
import solutions.aon.seg.social.exception.PaternityNotFoundException;
import solutions.aon.seg.social.exception.PaternityWrongDataException;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.StatusCodeException;
import solutions.aon.seg.social.exception.invalid.UnfilledMandatory;
import solutions.aon.seg.social.object.PaternityCertificate;

public class TestPaternity {
	final static String[] ID_TYPE={"NIF", "NIE"};
	//M -> Madre, P -> 'Otro progenitor', A -> Primer adoptante, B -> Segundo adoptante
	final static String[] APPLICANT_TYPE= {"M", "P", "A", "B"};
	final static String[] MOTHER_REASON= {"Nacimiento de hijo","Fallecimiento de la madre","Cesión/Opción en favor del otro progenitor",
			"Parto múltiple","Inicio del descanso antes del parto (solo para madre biológica ET)"};
	final static String[] FATHER_REASON= {"Nacimiento de hijo","Parto múltiple"};
	//ADOPTERS es válido para las opciones del primer y segundo adoptante
	final static String ADOPTERS= "Adopción/Tutela/Acogimiento";
	final static String CERTIFICATE="src/test/resources/aon/solutions/FNMT.p12";
	
	private static final String CERTIFICATE_PASSWORD = "1234";
	private static final String CERTIFICATE_TYPE = "pkcs12"; 
	private static final String CERTIFICATE_PATH =  System.getProperty("user.home")+"/CERT.pfx"; 

	@Test
	@Ignore
	public void testGrabarCertificadoWrongPeriod() {
		try(final InputStream certificateInputStream=TestPaternity.class.getResourceAsStream("FNMT.p12")){
			
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.DAY_OF_MONTH, 29);
			calendar.set(Calendar.MONTH, Calendar.OCTOBER);
			calendar.set(Calendar.YEAR, 2020);
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			
			Date startDate= calendar.getTime();
			
			calendar = Calendar.getInstance();
			calendar.add(Calendar.YEAR, 10);
			
			Date endDate= calendar.getTime();
			
			assertFalse("Should throw an exception", Paternity.grabarCertificado(certificateInputStream, "jg@FNMT", "pkcs12", "011017250195", "0111", "01105577910", ID_TYPE[0], "58025118M", APPLICANT_TYPE[1], FATHER_REASON[0], startDate, endDate, 1200, 1200, 28));	
			
		} catch (CertificateNotFoundException e) {
			fail("Certificate path is wrong");
		} catch (StatusCodeException sce) {
			assertTrue(true);
		} catch(SegSocialException e) {
			assertTrue(true);
		} catch (FileNotFoundException e1) {
			fail("Certificate file does not exist");
		} catch (IOException e1) {
			fail("Certificate error");
		} 
	}
	
	@Ignore
	@Test
	public void testVoidCertificateOk() {
		try(final InputStream certificateInputStream=TestPaternity.class.getResourceAsStream("FNMT.p12")){
			
			Date startDate= new SimpleDateFormat("dd-MM-yyyy").parse("28-10-2020");
			Date endDate= new SimpleDateFormat("dd-MM-yyyy").parse("04-11-2020");
			try {
				Paternity.voidPaternity(certificateInputStream, "jg@FNMT", "pkcs12", "011017250195", "0111", "01105577910", startDate, endDate, Optional.empty());	
			}catch (PaternityException e) {
				e.printStackTrace();
				fail("Should have done it");
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
	public void testVoidCertificateWrongEndDate() {
		try(final InputStream certificateInputStream=TestPaternity.class.getResourceAsStream("FNMT.p12")){
			
			Date startDate= new SimpleDateFormat("dd-MM-yyyy").parse("28-10-2020");
			LocalDate endDateaux=LocalDate.now();
			endDateaux = endDateaux.plusYears(2);
			Date endDate=Date.from(endDateaux.atStartOfDay(ZoneId.systemDefault()).toInstant());
			System.out.println(endDate);
			try {
				Paternity.voidPaternity(certificateInputStream, "jg@FNMT", "pkcs12", "011017250195", "0111", "01105577910", startDate, endDate, Optional.empty());	
			}catch (PaternityWrongDataException e) {
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
	public void testVoidCertificateWrongStartDate() {
		try(final InputStream certificateInputStream=TestPaternity.class.getResourceAsStream("FNMT.p12")){
			
			Date startDate= new SimpleDateFormat("dd-MM-yyyy").parse("09-11-2020");
			Date endDate= new SimpleDateFormat("dd-MM-yyyy").parse("04-11-2020");
			try {
				Paternity.voidPaternity(certificateInputStream, "jg@FNMT", "pkcs12", "011017250195", "0111", "01105577910", startDate, endDate, Optional.empty());	
			}catch (PaternityWrongDataException e) {
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
	public void testVoidCertificateNotFound() {
		try(final InputStream certificateInputStream=TestPaternity.class.getResourceAsStream("FNMT.p12")){
			
			Date startDate= new SimpleDateFormat("dd-MM-yyyy").parse("01-10-2020");
			Date endDate= new SimpleDateFormat("dd-MM-yyyy").parse("04-10-2020");
			try {
				Paternity.voidPaternity(certificateInputStream, "jg@FNMT", "pkcs12", "011017250195", "0111", "01105577910", startDate, endDate, Optional.empty());	
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
	public void testGrabarCertificadoWrongCertificateKey() {
		try(final InputStream certificateInputStream=TestPaternity.class.getResourceAsStream("FNMT.p12")){
			
			Date startDate= new SimpleDateFormat("dd-MM-yyyy").parse("28-10-2020");
			Date endDate= new SimpleDateFormat("dd-MM-yyyy").parse("24-11-2020");
			Paternity.grabarCertificado(certificateInputStream, "jr@FlMT", "pkcs12", "011017250195", "0111", "01105577910", ID_TYPE[0], "58025118M", APPLICANT_TYPE[1], FATHER_REASON[0], startDate, endDate, 1200, 1200, 28);
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
	public void testGrabarCertificadoWrongCertificateType() {
		try(final InputStream certificateInputStream=TestPaternity.class.getResourceAsStream("FNMT.p12")){
			
			Date startDate= new SimpleDateFormat("dd-MM-yyyy").parse("28-10-2020");
			Date endDate= new SimpleDateFormat("dd-MM-yyyy").parse("24-11-2020");
			Paternity.grabarCertificado(certificateInputStream, "jg@FNMT", "pkcs32", "011017250195", "0111", "01105577910", ID_TYPE[0], "58025118M", APPLICANT_TYPE[1], FATHER_REASON[0], startDate, endDate, 1200, 1200, 28);
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
	public void testGrabarCertificadoWrongCertificate() {
		try(final InputStream certificateInputStream=TestPaternity.class.getResourceAsStream("FNMp12")){
			
			Date startDate= new SimpleDateFormat("dd-MM-yyyy").parse("28-10-2020");
			Date endDate= new SimpleDateFormat("dd-MM-yyyy").parse("24-11-2020");
			Paternity.grabarCertificado(certificateInputStream, "jg@FNMT", "pkcs12", "011017250195", "0111", "01105577910", ID_TYPE[0], "58025118M", APPLICANT_TYPE[1], FATHER_REASON[0], startDate, endDate, 1200, 1200, 28);
			fail("Should have failed");
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
	public void testVoidCertificateWrongCertificateKey() {
		try(final InputStream certificateInputStream=TestPaternity.class.getResourceAsStream("FNMT.p12")){
			
			Date startDate= new SimpleDateFormat("dd-MM-yyyy").parse("28-10-2020");
			Date endDate= new SimpleDateFormat("dd-MM-yyyy").parse("04-11-2020");
				Paternity.voidPaternity(certificateInputStream, "jg@FrMT", "pkcs12", "011017250195", "0111", "01105577910", startDate, endDate, Optional.empty());	
				fail("Should't let do");
		} catch (CertificateNotFoundException e) {
			fail("Certificate path is wrong");
		} catch (InvalidCertificateException e) {
			assertTrue(true);
		}
		catch (StatusCodeException sce) {
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
	public void testVoidCertificateWrongCertificateType() {
		try(final InputStream certificateInputStream=TestPaternity.class.getResourceAsStream("FNMT.p12")){
			
			Date startDate= new SimpleDateFormat("dd-MM-yyyy").parse("28-10-2020");
			Date endDate= new SimpleDateFormat("dd-MM-yyyy").parse("04-11-2020");
				Paternity.voidPaternity(certificateInputStream, "jg@FNMT", "pkc412", "011017250195", "0111", "01105577910", startDate, endDate, Optional.empty());	
				fail("Shouldn't let do");
		} catch (CertificateNotFoundException e) {
			fail("Certificate path is wrong");
		} catch (InvalidCertificateException e) {
			assertTrue(true);
		}
		catch (StatusCodeException sce) {
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
	public void testVoidCertificateWrongCertificate() {
		try(final InputStream certificateInputStream=TestPaternity.class.getResourceAsStream("FNM2")){
			
			Date startDate= new SimpleDateFormat("dd-MM-yyyy").parse("28-10-2020");
			Date endDate= new SimpleDateFormat("dd-MM-yyyy").parse("04-11-2020");
				Paternity.voidPaternity(certificateInputStream, "jg@FNMT", "pkcs12", "011017250195", "0111", "01105577910", startDate, endDate, Optional.empty());	
				fail("Shouldn't let do");
		} catch (InvalidCertificateException e) {
			assertTrue(true);
		}
		catch (StatusCodeException sce) {
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
	public void testGetCertificatePdfOk() {
		try(final InputStream certificateInputStream=TestPaternity.class.getResourceAsStream("FNMT.p12")){
			
			Date startDate= new SimpleDateFormat("dd-MM-yyyy").parse("28-10-2020");
			Date endDate= new SimpleDateFormat("dd-MM-yyyy").parse("04-11-2020");
			try {
				byte[] pdf=Paternity.getCertificatePdf(certificateInputStream, "jg@FNMT", "pkcs12", "011017250195", "0111", "01105577910", startDate, endDate, Optional.empty());	
				if(pdf.length>0)
					assertTrue(true);
				else
					fail("Did not create a pdf");
			}catch (PaternityException e) {
				fail("Should have done it");
			}
		} catch (InvalidCertificateException e) {
			fail("Invalid certificate");
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
	public void testGetCertificatePdfWrongStartDate() {
		try(final InputStream certificateInputStream=TestPaternity.class.getResourceAsStream("FNMT.p12")){
			
			Date startDate= new SimpleDateFormat("dd-MM-yyyy").parse("05-11-2020");
			Date endDate= new SimpleDateFormat("dd-MM-yyyy").parse("04-11-2020");
			try {
				byte[] pdf=Paternity.getCertificatePdf(certificateInputStream, "jg@FNMT", "pkcs12", "011017250195", "0111", "01105577910", startDate, endDate, Optional.empty());	
				fail("Should not create anything");
			}catch (PaternityWrongDataException e) {
				assertTrue(true);
			}
		} catch (InvalidCertificateException e) {
			fail("Invalid certificate");
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
	public void testGetCertificatePdfWrongEndDate() {
		try(final InputStream certificateInputStream=TestPaternity.class.getResourceAsStream("FNMT.p12")){
			
			Date startDate= new SimpleDateFormat("dd-MM-yyyy").parse("01-11-2020");
			Date endDate= new SimpleDateFormat("dd-MM-yyyy").parse("31-10-2020");
			try {
				byte[] pdf=Paternity.getCertificatePdf(certificateInputStream, "jg@FNMT", "pkcs12", "011017250195", "0111", "01105577910", startDate, endDate, Optional.empty());	
				fail("Should not create anything");
			}catch (PaternityWrongDataException e) {
				assertTrue(true);
			}
		} catch (InvalidCertificateException e) {
			fail("Invalid certificate");
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
	public void testGetCertificatePdfNoDataFound() {
		try(final InputStream certificateInputStream=TestPaternity.class.getResourceAsStream("FNMT.p12")){
			
			Date startDate= new SimpleDateFormat("dd-MM-yyyy").parse("01-11-2019");
			Date endDate= new SimpleDateFormat("dd-MM-yyyy").parse("04-11-2019");
			try {
				byte[] pdf=Paternity.getCertificatePdf(certificateInputStream, "jg@FNMT", "pkcs12", "011017250195", "0111", "01105577910", startDate, endDate, Optional.empty());	
				fail("Should not create anything");
			}catch (PaternityNotFoundException e) {
				assertTrue(true);
			}
		} catch (InvalidCertificateException e) {
			fail("Invalid certificate");
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
	public void testGetCertificatePdfWrongCertificate() {
		try(final InputStream certificateInputStream=TestPaternity.class.getResourceAsStream("FN")){
			
			Date startDate= new SimpleDateFormat("dd-MM-yyyy").parse("28-10-2020");
			Date endDate= new SimpleDateFormat("dd-MM-yyyy").parse("04-11-2020");
			byte[] pdf=Paternity.getCertificatePdf(certificateInputStream, "jg@FNMT", "pkcs12", "011017250195", "0111", "01105577910", startDate, endDate, Optional.empty());	
			fail("Shouldn't do anything");
		} catch (CertificateNotFoundException e) {
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
	public void testGetCertificatePdfWrongCertificateType() {
		try(final InputStream certificateInputStream=TestPaternity.class.getResourceAsStream("FN")){
			
			Date startDate= new SimpleDateFormat("dd-MM-yyyy").parse("28-10-2020");
			Date endDate= new SimpleDateFormat("dd-MM-yyyy").parse("04-11-2020");
			byte[] pdf=Paternity.getCertificatePdf(certificateInputStream, "jg@FNMT", "pk8s12", "011017250195", "0111", "01105577910", startDate, endDate, Optional.empty());	
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
	public void testGetCertificatePdfWrongCertificateKey() {
		try(final InputStream certificateInputStream=TestPaternity.class.getResourceAsStream("FN")){
			
			Date startDate= new SimpleDateFormat("dd-MM-yyyy").parse("28-10-2020");
			Date endDate= new SimpleDateFormat("dd-MM-yyyy").parse("04-11-2020");
			byte[] pdf=Paternity.getCertificatePdf(certificateInputStream, "jg@FNMT4", "pkcs12", "011017250195", "0111", "01105577910", startDate, endDate, Optional.empty());	
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
