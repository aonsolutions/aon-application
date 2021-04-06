package solutions.aon;
import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

import org.junit.Ignore;
import org.junit.Test;

import solutions.aon.seg.social.exceptions.SegSocialException;
import solutions.aon.seg.social.exceptions.certificate.CertificateNotFoundException;
import solutions.aon.seg.social.exceptions.certificate.InvalidCertificateException;
import solutions.aon.seg.social.exceptions.invalidData.UnfilledMandatory;
import solutions.aon.seg.social.exceptions.paternity.PaternityException;
import solutions.aon.seg.social.exceptions.paternity.PaternityNotFoundException;
import solutions.aon.seg.social.exceptions.paternity.PaternityWrongDataException;
import solutions.aon.seg.social.exceptions.statusCode.ForbiddenException;
import solutions.aon.seg.social.exceptions.statusCode.StatusCodeException;
import solutions.aon.seg.social.objects.PaternityCertificate;
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
			//Date endDate= new SimpleDateFormat("dd-MM-yyyy").parse("21-11-3000");
			LocalDate endDateaux=LocalDate.now();
			endDateaux.plusYears(2);
			Date endDate=Date.from(endDateaux.atStartOfDay(ZoneId.systemDefault()).toInstant());
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
	
	/*@Test
	public void testGrabarConsultarBorrarCertificadoOk() {
		try(final InputStream certificateInputStream=TestPaternity.class.getResourceAsStream("FNMT.p12")){
			
			Date startDate = new SimpleDateFormat("dd-MM-yyyy").parse("07-12-2020");
			Date endDate = new SimpleDateFormat("dd-MM-yyyy").parse("28-03-2021");
			
			Date creationDate = new SimpleDateFormat("dd-MM-yyyy").parse("16-12-2020");
			
			String cccType = "0111";
			String ccc = "01105360062";
			
			String naf = "281468615302";
			String document = "47227931F";
			
			boolean isSaved = Paternity.grabarCertificado(certificateInputStream, "jg@FNMT", "pkcs12", naf, cccType, ccc, ID_TYPE[0], document, APPLICANT_TYPE[0], FATHER_REASON[0], startDate, endDate, 2300, 2300, 30);	
			
			if(isSaved) {
				byte[] pdfBytes = Paternity.getCertificatePdf(certificateInputStream, "jg@FNMT", "pkcs12", naf, cccType, ccc, creationDate, creationDate, Optional.of(startDate));
				FileOutputStream out = new FileOutputStream(new File("/Users/sergio/Desktop/ParteIT.pdf"));
				out.write(pdfBytes);
				out.flush();
				out.close();
				
				Paternity.voidPaternity(certificateInputStream, "jg@FNMT", "pkcs12", naf, cccType, ccc, creationDate, creationDate, Optional.of(startDate));
			}
				
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
	public void testConsultCertificatesPdfOk() {
		try(final InputStream certificateInputStream=TestPaternity.class.getResourceAsStream("FNMT.p12")){
			
			Date startDate= new SimpleDateFormat("dd-MM-yyyy").parse("28-10-2020");
			Date endDate= new SimpleDateFormat("dd-MM-yyyy").parse("04-11-2020");
			try {
				Collection<PaternityCertificate> pcCol=Paternity.consultCertificates(certificateInputStream, "jg@FNMT", "pkcs12", "011017250195", "0111", "01105577910", startDate, endDate, Optional.empty());	
				for (PaternityCertificate pc : pcCol) {
					if(!(pc.getPdf().length>0))
						fail("Did not create a pdf");		
				}
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
	public void testConsultCertificatesUnfilledCCC() {
		try(final InputStream certificateInputStream=TestPaternity.class.getResourceAsStream("FNMT.p12")){
			
			Date startDate= new SimpleDateFormat("dd-MM-yyyy").parse("28-10-2020");
			Date endDate= new SimpleDateFormat("dd-MM-yyyy").parse("04-11-2020");
			try {
				Collection<PaternityCertificate> pcCol=Paternity.consultCertificates(certificateInputStream, "jg@FNMT", "pkcs12", "", "0111", "01105577910", startDate, endDate, Optional.empty());	
				for (PaternityCertificate pc : pcCol) {
					if(!(pc.getPdf().length>0))
						fail("Did not create a pdf");			
				}
			}catch (PaternityException e) {
				fail("Should have done it");
			}
		} catch (InvalidCertificateException e) {
			fail("Invalid certificate");
		} catch (StatusCodeException sce) {
			assertTrue(true);
		} catch (UnfilledMandatory e) {
			assertTrue(true);
		}catch(SegSocialException e) {
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
	public void testGetCertificateWrongStartDate() {
		try(final InputStream certificateInputStream=TestPaternity.class.getResourceAsStream("FNMT.p12")){
			
			Date startDate= new SimpleDateFormat("dd-MM-yyyy").parse("05-11-2020");
			Date endDate= new SimpleDateFormat("dd-MM-yyyy").parse("04-11-2020");
			try {
				Paternity.consultCertificates(certificateInputStream, "jg@FNMT", "pkcs12", "011017250195", "0111", "01105577910", startDate, endDate, Optional.empty());	
				
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
	public void testConsultCertificatesWrongEndDate() {
		try(final InputStream certificateInputStream=TestPaternity.class.getResourceAsStream("FNMT.p12")){
			
			Date startDate= new SimpleDateFormat("dd-MM-yyyy").parse("01-11-2020");
			Date endDate= new SimpleDateFormat("dd-MM-yyyy").parse("31-10-2020");
			try {
				Paternity.consultCertificates(certificateInputStream, "jg@FNMT", "pkcs12", "011017250195", "0111", "01105577910", startDate, endDate, Optional.empty());
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
	public void testConsultCertificatesNoDataFound() {
		try(final InputStream certificateInputStream=TestPaternity.class.getResourceAsStream("FNMT.p12")){
			
			Date startDate= new SimpleDateFormat("dd-MM-yyyy").parse("01-11-2019");
			Date endDate= new SimpleDateFormat("dd-MM-yyyy").parse("04-11-2019");
			try {
				Paternity.consultCertificates(certificateInputStream, "jg@FNMT", "pkcs12", "011017250195", "0111", "01105577910", startDate, endDate, Optional.empty());
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
	public void testConsultCertificatesWrongCertificate() {
		try(final InputStream certificateInputStream=TestPaternity.class.getResourceAsStream("FN")){
			
			Date startDate= new SimpleDateFormat("dd-MM-yyyy").parse("28-10-2020");
			Date endDate= new SimpleDateFormat("dd-MM-yyyy").parse("04-11-2020");
			Paternity.consultCertificates(certificateInputStream, "jg@FNMT", "pkcs12", "011017250195", "0111", "01105577910", startDate, endDate, Optional.empty());	
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
	public void testConsultCertificatesWrongCertificateType() {
		try(final InputStream certificateInputStream=TestPaternity.class.getResourceAsStream("FN")){
			
			Date startDate= new SimpleDateFormat("dd-MM-yyyy").parse("28-10-2020");
			Date endDate= new SimpleDateFormat("dd-MM-yyyy").parse("04-11-2020");
			Paternity.consultCertificates(certificateInputStream, "jg@FNMT", "pkcs12", "011017250195", "0111", "01105577910", startDate, endDate, Optional.empty());	
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
	public void testConsultCertificatesWrongCertificateKey() {
		try(final InputStream certificateInputStream=TestPaternity.class.getResourceAsStream("FN")){
			
			Date startDate= new SimpleDateFormat("dd-MM-yyyy").parse("28-10-2020");
			Date endDate= new SimpleDateFormat("dd-MM-yyyy").parse("04-11-2020");
			Paternity.consultCertificates(certificateInputStream, "jg@FNMT", "pkcs12", "011017250195", "0111", "01105577910", startDate, endDate, Optional.empty());	
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
	
	
	
	
	
	
	
	
}
