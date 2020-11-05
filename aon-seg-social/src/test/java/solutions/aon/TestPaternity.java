package solutions.aon;
import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

import org.junit.Test;

import solutions.aon.seg.social.exceptions.SegSocialException;
import solutions.aon.seg.social.exceptions.certificate.CertificateNotFoundException;
import solutions.aon.seg.social.exceptions.certificate.InvalidCertificateException;
import solutions.aon.seg.social.exceptions.paternity.PaternityException;
import solutions.aon.seg.social.exceptions.paternity.PaternityNotFoundException;
import solutions.aon.seg.social.exceptions.paternity.PaternityWrongDataException;
import solutions.aon.seg.social.exceptions.statusCode.ForbiddenException;
import solutions.aon.seg.social.exceptions.statusCode.StatusCodeException;
import solutions.aon.seg.social.toolkit.Toolkit;
import solutions.aon.seg.social.*;

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

	@Test
	public void testGrabarCertificadoWrongPeriod() {
		try(final InputStream certificateInputStream=TestPaternity.class.getResourceAsStream("FNMT.p12")){
			
			Date startDate= new SimpleDateFormat("dd-MM-yyyy").parse("29-10-2020");
			Date endDate= new SimpleDateFormat("dd-MM-yyyy").parse("21-11-2020");
			assertFalse("Should throw an exception", Paternity.grabarCertificado(certificateInputStream, "jg@FNMT", "pkcs12", "011017250195", "0111", "01105577910", ID_TYPE[0], "58025118M", APPLICANT_TYPE[1], FATHER_REASON[0], startDate, endDate, 1200, 1200, 28));	
			
		} catch (CertificateNotFoundException e) {
			fail("Certificate path is wrong");
		} catch (StatusCodeException sce) {
			assertTrue(true);
		} catch(SegSocialException e) {
			assertTrue(true);
		} catch (ParseException e) {
			fail("Date typed wrong");
		} catch (FileNotFoundException e1) {
			fail("Certificate file does not exist");
		} catch (IOException e1) {
			fail("Certificate error");
		} 
	}
	
	/*@Test
	public void testGrabarCertificadoOk() {
		try(final InputStream certificateInputStream=TestPaternity.class.getResourceAsStream("FNMT.p12")){
			
			Date startDate= new SimpleDateFormat("dd-MM-yyyy").parse("28-10-2020");
			Date endDate= new SimpleDateFormat("dd-MM-yyyy").parse("24-11-2020");
			assertTrue("Should throw an exception", Paternity.grabarCertificado(certificateInputStream, "jg@FNMT", "pkcs12", "011017250195", "0111", "01105577910", ID_TYPE[0], "58025118M", APPLICANT_TYPE[1], FATHER_REASON[0], startDate, endDate, 1200, 1200, 28));	
			
		}catch(SegSocialException e) {
			fail("Wrong data");
		} catch (ParseException e) {
			fail("Date typed wrong");
		} catch (FileNotFoundException e1) {
			fail("Certificate file does not exist");
		} catch (IOException e1) {
			fail("Certificate error");
		}
	}*/

	
	@Test
	public void testVoidCertificateOk() {
		try(final InputStream certificateInputStream=TestPaternity.class.getResourceAsStream("FNMT.p12")){
			
			Date startDate= new SimpleDateFormat("dd-MM-yyyy").parse("28-10-2020");
			Date endDate= new SimpleDateFormat("dd-MM-yyyy").parse("04-11-2020");
			try {
				Paternity.voidPaternity(certificateInputStream, "jg@FNMT", "pkcs12", "011017250195", "0111", "01105577910", startDate, endDate, Optional.empty());	
			}catch (PaternityException e) {
				fail("Should have done it");
			}
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
	public void testVoidCertificateWrongEndDate() {
		try(final InputStream certificateInputStream=TestPaternity.class.getResourceAsStream("FNMT.p12")){
			
			Date startDate= new SimpleDateFormat("dd-MM-yyyy").parse("28-10-2020");
			Date endDate= new SimpleDateFormat("dd-MM-yyyy").parse("30-11-2020");
			try {
				Paternity.voidPaternity(certificateInputStream, "jg@FNMT", "pkcs12", "011017250195", "0111", "01105577910", startDate, endDate, Optional.empty());	
			}catch (PaternityWrongDataException e) {
				assertTrue(true);
			}
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
	public void testVoidCertificateWrongStartDate() {
		try(final InputStream certificateInputStream=TestPaternity.class.getResourceAsStream("FNMT.p12")){
			
			Date startDate= new SimpleDateFormat("dd-MM-yyyy").parse("09-11-2020");
			Date endDate= new SimpleDateFormat("dd-MM-yyyy").parse("04-11-2020");
			try {
				Paternity.voidPaternity(certificateInputStream, "jg@FNMT", "pkcs12", "011017250195", "0111", "01105577910", startDate, endDate, Optional.empty());	
			}catch (PaternityWrongDataException e) {
				assertTrue(true);
			}
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
	public void testVoidCertificateNotFound() {
		try(final InputStream certificateInputStream=TestPaternity.class.getResourceAsStream("FNMT.p12")){
			
			Date startDate= new SimpleDateFormat("dd-MM-yyyy").parse("01-10-2020");
			Date endDate= new SimpleDateFormat("dd-MM-yyyy").parse("04-10-2020");
			try {
				Paternity.voidPaternity(certificateInputStream, "jg@FNMT", "pkcs12", "011017250195", "0111", "01105577910", startDate, endDate, Optional.empty());	
			}catch (PaternityNotFoundException e) {
				assertTrue(true);
			}
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
	public void testGrabarCertificadoWrongCertificateKey() {
		try(final InputStream certificateInputStream=TestPaternity.class.getResourceAsStream("FNMT.p12")){
			
			Date startDate= new SimpleDateFormat("dd-MM-yyyy").parse("28-10-2020");
			Date endDate= new SimpleDateFormat("dd-MM-yyyy").parse("24-11-2020");
			Paternity.grabarCertificado(certificateInputStream, "jr@FlMT", "pkcs12", "011017250195", "0111", "01105577910", ID_TYPE[0], "58025118M", APPLICANT_TYPE[1], FATHER_REASON[0], startDate, endDate, 1200, 1200, 28);
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
	public void testGrabarCertificadoWrongCertificateType() {
		try(final InputStream certificateInputStream=TestPaternity.class.getResourceAsStream("FNMT.p12")){
			
			Date startDate= new SimpleDateFormat("dd-MM-yyyy").parse("28-10-2020");
			Date endDate= new SimpleDateFormat("dd-MM-yyyy").parse("24-11-2020");
			Paternity.grabarCertificado(certificateInputStream, "jg@FNMT", "pkcs32", "011017250195", "0111", "01105577910", ID_TYPE[0], "58025118M", APPLICANT_TYPE[1], FATHER_REASON[0], startDate, endDate, 1200, 1200, 28);
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
	public void testVoidCertificateWrongCertificateKey() {
		try(final InputStream certificateInputStream=TestPaternity.class.getResourceAsStream("FNMT.p12")){
			
			Date startDate= new SimpleDateFormat("dd-MM-yyyy").parse("28-10-2020");
			Date endDate= new SimpleDateFormat("dd-MM-yyyy").parse("04-11-2020");
				Paternity.voidPaternity(certificateInputStream, "jg@FrMT", "pkcs12", "011017250195", "0111", "01105577910", startDate, endDate, Optional.empty());	
				fail("Should't let do");
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
	public void testVoidCertificateWrongCertificateType() {
		try(final InputStream certificateInputStream=TestPaternity.class.getResourceAsStream("FNMT.p12")){
			
			Date startDate= new SimpleDateFormat("dd-MM-yyyy").parse("28-10-2020");
			Date endDate= new SimpleDateFormat("dd-MM-yyyy").parse("04-11-2020");
				Paternity.voidPaternity(certificateInputStream, "jg@FNMT", "pkc412", "011017250195", "0111", "01105577910", startDate, endDate, Optional.empty());	
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
}
