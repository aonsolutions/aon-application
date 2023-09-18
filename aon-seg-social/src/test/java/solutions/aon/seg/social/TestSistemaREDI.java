package solutions.aon.seg.social;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.Assert;
import org.junit.Test;

import org.htmlunit.FailingHttpStatusCodeException;

import solutions.aon.seg.social.exception.CertificateNotFoundException;
import solutions.aon.seg.social.exception.InvalidCertificateException;
import solutions.aon.seg.social.exception.OutOfServiceException;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.StatusCodeException;
import solutions.aon.seg.social.exception.invalid.DataDoesNotExist;
import solutions.aon.seg.social.exception.invalid.InvalidDataException;
import solutions.aon.seg.social.exception.invalid.NotAllowedContributionAccount;
import solutions.aon.seg.social.exception.invalid.UnfilledMandatory;
import solutions.aon.seg.social.exception.invalid.WrongIdentifierException;
import solutions.aon.seg.social.exception.invalid.WrongRegimeException;
import solutions.aon.seg.social.exception.invalid.InvalidCccException;
import solutions.aon.seg.social.object.Idc;
import solutions.aon.seg.social.object.SituacionEmpresa;

//@Ignore
public class TestSistemaREDI extends SegSocialTest{

	@Test
	public void testSituacionEmpresaOk() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ACR69&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			SituacionEmpresa se = SistemaREDI.getSituacionEmpresa(certificateInputStream, "jg@FNMT", "pkcs12", "0111",
					"01105360062");
			if (se.getCcc() != null) {
				assertTrue(true);
			}
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (MalformedURLException e) {
			fail("Wrong url");
		} catch (IOException e) {
			fail("Certificate input problem");
			e.printStackTrace();
		} catch (InterruptedException e) {
			fail("Interrupted exception");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail("Wrong data");
		}
	}

	@Test
	public void testSituacionEmpresaWrongRegime() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ACR69&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			SituacionEmpresa se = SistemaREDI.getSituacionEmpresa(certificateInputStream, "jg@FNMT", "pkcs12", "0161",
					"01105360062");
			fail("Shouldn't finish");
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (WrongRegimeException e) {
			assertTrue(true);
		} catch (MalformedURLException e) {
			fail("Wrong url");
		} catch (IOException e) {
			fail("Certificate input problem");
			e.printStackTrace();
		} catch (InterruptedException e) {
			fail("Interrupted exception");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail("Wrong data");
		}
	}

	@Test
	public void testSituacionEmpresaUnfilledCCC() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ACR69&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			SituacionEmpresa se = SistemaREDI.getSituacionEmpresa(certificateInputStream, "jg@FNMT", "pkcs12", "0111",
					"");
			fail("Shouldn't finish");
		} catch (UnfilledMandatory e) {
			assertTrue(true);
		} catch (MalformedURLException e) {
			fail("Wrong url");
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (IOException e) {
			fail("Certificate input problem");
			e.printStackTrace();
		} catch (InterruptedException e) {
			fail("Interrupted exception");
		} catch (SegSocialException e) {
			e.printStackTrace();
			System.out.println(e.getClass());
			fail("Wrong data");
		}
	}

	@Test
	public void testSituacionEmpresaWrongCert() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ACR69&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNM2")) {
			SituacionEmpresa se = SistemaREDI.getSituacionEmpresa(certificateInputStream, "jg@FNMT", "pkcs12", "0111",
					"01105360062");
			fail("Shouldn't finish");
		} catch (CertificateNotFoundException e) {
			assertTrue(true);
		} catch (MalformedURLException e) {
			fail("Wrong url");
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (IOException e) {
			fail("Certificate input problem");
			e.printStackTrace();
		} catch (InterruptedException e) {
			fail("Interrupted exception");
		} catch (SegSocialException e) {
			e.printStackTrace();
			System.out.println(e.getClass());
			fail("Wrong data");
		}
	}

	@Test
	public void testSituacionEmpresaWrongCertKey() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ACR69&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			SituacionEmpresa se = SistemaREDI.getSituacionEmpresa(certificateInputStream, "jgfFNMT", "pkcs12", "0111",
					"01105360062");
			fail("Shouldn't finish");
		} catch (InvalidCertificateException e) {
			assertTrue(true);
		} catch (MalformedURLException e) {
			fail("Wrong url");
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (IOException e) {
			fail("Certificate input problem");
			e.printStackTrace();
		} catch (InterruptedException e) {
			fail("Interrupted exception");
		} catch (SegSocialException e) {
			e.printStackTrace();
			System.out.println(e.getClass());
			fail("Wrong data");
		}
	}

	@Test
	public void testSituacionEmpresaWrongCertType() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ACR69&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			SituacionEmpresa se = SistemaREDI.getSituacionEmpresa(certificateInputStream, "jg@FNMT", "pkcs15", "0111",
					"01105360062");
			fail("Shouldn't finish");
		} catch (InvalidCertificateException e) {
			assertTrue(true);
		} catch (MalformedURLException e) {
			fail("Wrong url");
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (IOException e) {
			fail("Certificate input problem");
			e.printStackTrace();
		} catch (InterruptedException e) {
			fail("Interrupted exception");
		} catch (SegSocialException e) {
			e.printStackTrace();
			System.out.println(e.getClass());
			fail("Wrong data");
		}
	}

//	@Test
//	public void repeatTestGetTADuplicateOk() {
//		for (int i=0; i<50; i++)
//			testGetTADuplicateOk();
//	}

	@Test
	public void testGetTADuplicateOk() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR65&E=I&AP=AFIR");
		
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
			byte[] pdf = SistemaREDI.getTADuplicate(certificateInputStream, "jg@FNMT", "pkcs12", "011005185924", "0111",
					"01105360062", d);
			if (!(pdf.length > 0))
				fail("Should have returned a pdf");
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail("SegSocialException");
		}
	}

	@Test
	public void testGetTADuplicateOk2() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR65&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("09-09-2020");
			byte[] pdf = SistemaREDI.getTADuplicate(certificateInputStream, "jg@FNMT", "pkcs12", "291136796369", "0111",
					"01105360062", d);
			if (!(pdf.length > 0))
				fail("Should have returned a pdf");
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail("SegSocialException");
		}
	}

	@Test
	public void testGetTADuplicateWrongDate() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR65&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Calendar c = Calendar.getInstance();
			c.add(Calendar.YEAR, 1);
			Date d = c.getTime();
			byte[] pdf = SistemaREDI.getTADuplicate(certificateInputStream, "jg@FNMT", "pkcs12", "011005185924", "0111",
					"01105360062", d);
		} catch (InvalidDataException e) {
			assertTrue(true);
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail("SegSocialException");
		}
	}

	@Test
	public void testGetTADuplicateWrongCCC() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR65&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
			byte[] pdf = SistemaREDI.getTADuplicate(certificateInputStream, "jg@FNMT", "pkcs12", "011005185924", "0111",
					"01105760562", d);
		} catch (InvalidCccException e) {
			assertTrue(true);
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (ParseException e) {
			fail("Wrong data input");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail("SegSocialException");
		}
	}

	@Test
	public void testGetTADuplicateWrongCert() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR65&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMp12")) {
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
			byte[] pdf = SistemaREDI.getTADuplicate(certificateInputStream, "jg@FNMT", "pkcs12", "011005185924", "0111",
					"01105360062", d);
			fail("Should have failed");
		} catch (CertificateNotFoundException e) {
			assertTrue(true);
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail("SegSocialException");
		}
	}

	@Test
	public void testGetTADuplicateWrongCertKey() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR65&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
			byte[] pdf = SistemaREDI.getTADuplicate(certificateInputStream, "jg@NMT", "pkcs12", "011005185924", "0111",
					"01105360062", d);
			fail("Should have failed");
		} catch (InvalidCertificateException e) {
			assertTrue(true);
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail("SegSocialException");
		}
	}

	@Test
	public void testGetTADuplicateWrongCertType() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR65&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
			byte[] pdf = SistemaREDI.getTADuplicate(certificateInputStream, "jg@FNMT", "pk5s12", "011005185924", "0111",
					"01105360062", d);
			fail("Should have failed");
		} catch (InvalidCertificateException e) {
			assertTrue(true);
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail("SegSocialException");
		}
	}
	
//	@Test
//	public void repeatTestContributionInfoCCCDuplicateOk() {
//		for (int i=0; i< 50; i++) {
//			testContributionInfoCCCDuplicateOk();
//		}
//	}
	
	@Test
	public void testContributionInfoCCCDuplicateOk() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR38&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Date d = new Date();
			byte[] pdf = SistemaREDI.getContributionInformationCCC(certificateInputStream, "jg@FNMT", "pkcs12", "0111",
					"01105360062", d);
			if (!(pdf.length > 0))
				fail("Should have returned a pdf");
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail("SegSocialException");
		}
	}

	@Test
	public void testContributionInfoDuplicateOk() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR37&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
			byte[] pdf = SistemaREDI.getContributionInformation(certificateInputStream, "jg@FNMT", "pkcs12",
					"011005185924", "0111", "01105360062", d);
			if (!(pdf.length > 0))
				fail("Should have returned a pdf");
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetContributionInfoWrongDate() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR37&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Calendar c = Calendar.getInstance();
			c.add(Calendar.YEAR, 1);
			Date d = c.getTime();
			byte[] pdf = SistemaREDI.getContributionInformation(certificateInputStream, "jg@FNMT", "pkcs12",
					"011005185924", "0111", "01105360062", d);
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (InvalidDataException e) {
			assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetContributionInfoWrongCCC() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR37&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
			byte[] pdf = SistemaREDI.getContributionInformation(certificateInputStream, "jg@FNMT", "pkcs12",
					"011005185924", "0111", "01105760562", d);
		} catch (InvalidCccException e) {
			assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (ParseException e) {
			fail("Wrong data input");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail("SegSocialException");
		}
	}

	@Test
	public void testGetContributionInfoWrongCert() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR37&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMp12")) {
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
			byte[] pdf = SistemaREDI.getContributionInformation(certificateInputStream, "jg@FNMT", "pkcs12",
					"011005185924", "0111", "01105360062", d);
			fail("Should have failed");
		} catch (CertificateNotFoundException e) {
			assertTrue(true);
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail("SegSocialException");
		}
	}

	@Test
	public void testGetContributionInfoWrongCertKey() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR37&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
			byte[] pdf = SistemaREDI.getContributionInformation(certificateInputStream, "jg@NMT", "pkcs12",
					"011005185924", "0111", "01105360062", d);
			fail("Should have failed");
		} catch (InvalidCertificateException e) {
			assertTrue(true);
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetContributionInfoWrongCertType() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR37&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
			byte[] pdf = SistemaREDI.getContributionInformation(certificateInputStream, "jg@FNMT", "pk5s12",
					"011005185924", "0111", "01105360062", d);
			fail("Should have failed");
		} catch (InvalidCertificateException e) {
			assertTrue(true);
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail("SegSocialException");
		}
	}

	@Test
	public void testTACertificatePdfsOk() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR65&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
			Collection<byte[]> pdfs = SistemaREDI.getTACertificatePDFs(certificateInputStream, "jg@FNMT", "pkcs12",
					"011005185924", "0111", "01105360062", d);
			int i = 0;
			System.out.println(pdfs.size());
			for (byte[] pdf : pdfs) {
				FileOutputStream fos = new FileOutputStream("/home/igonzalez/Escritorio/pruebaMultiPdfs/pedefe" + i + ".pdf");
				i++;
				fos.write(pdf);
				fos.close();
				if (!(pdf.length > 0))
					fail("Should have returned a pdf");
			}
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (InterruptedException e) {
			fail("Interrupted exception");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail("SegSocialException");
		}
	}

	@Test
	public void testTACertificatePdfsWrongCCC() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR65&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
			Collection<byte[]> pdfs = SistemaREDI.getTACertificatePDFs(certificateInputStream, "jg@FNMT", "pkcs12",
					"011005185924", "0111", "01105368062", d);
			fail("Should have returned a pdf");
		} catch (InvalidCccException e) {
			assertTrue(true);
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (InterruptedException e) {
			fail("Interrupted exception");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testTACertificatePdfsNullCCC() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR65&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
			Collection<byte[]> pdfs = SistemaREDI.getTACertificatePDFs(certificateInputStream, "jg@FNMT", "pkcs12",
					"011005185924", "0111", "", d);
			fail("Should have returned a pdf");
		} catch (UnfilledMandatory e) {
			assertTrue(true);
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (InterruptedException e) {
			fail("Interrupted exception");
		} catch (SegSocialException e) {
			e.printStackTrace();
			System.out.println(e.getClass());
			fail(e.getMessage());
		}
	}

	@Test
	public void testTACertificatePdfsNullRegime() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR65&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
			Collection<byte[]> pdfs = SistemaREDI.getTACertificatePDFs(certificateInputStream, "jg@FNMT", "pkcs12",
					"011005185924", "", "01105360062", d);
			fail("Should have returned a pdf");
		} catch (UnfilledMandatory e) {
			assertTrue(true);
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (InterruptedException e) {
			fail("Interrupted exception");
		} catch (SegSocialException e) {
			e.printStackTrace();
			System.out.println(e.getClass());
			fail(e.getMessage());
		}
	}

	@Test
	public void testTACertificatePdfsWrongRegime() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR65&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
			Collection<byte[]> pdfs = SistemaREDI.getTACertificatePDFs(certificateInputStream, "jg@FNMT", "pkcs12",
					"011005185924", "0181", "01105360062", d);
			fail("Should have returned a pdf");
		} catch (NotAllowedContributionAccount e) {
			assertTrue(true);
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (InterruptedException e) {
			fail("Interrupted exception");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail("SegSocialException");
		}
	}
	
	@Test
	public void testTACertificatePdfsWrongDate() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR65&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Calendar c = Calendar.getInstance();
			c.add(Calendar.YEAR, 4);
			Date d = c.getTime();
			Collection<byte[]> pdfs = SistemaREDI.getTACertificatePDFs(certificateInputStream, "jg@FNMT", "pkcs12",
					"011005185924", "0111", "01105360062", d);
			fail("Should have thrown an exception");
		} catch (InvalidDataException e) {
			assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (InterruptedException e) {
			fail("Interrupted exception");
		} catch (SegSocialException e) {
			e.printStackTrace();
			System.out.println(e.getClass());
			fail("SegSocialException");
		} catch (NullPointerException e) {
			
		}
	}

	@Test
	public void testTACertificatePdfsWrongCert() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR65&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMp12")) {
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
			Collection<byte[]> pdfs = SistemaREDI.getTACertificatePDFs(certificateInputStream, "jg@FNMT", "pkcs12",
					"011005185924", "0111", "01105360062", d);
			fail("Should have returned failed");
		} catch (CertificateNotFoundException e) {
			assertTrue(true);
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (InterruptedException e) {
			fail("Interrupted exception");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testTACertificatePdfsWrongCertKey() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR65&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
			Collection<byte[]> pdfs = SistemaREDI.getTACertificatePDFs(certificateInputStream, "jg@NMT", "pkcs12",
					"011005185924", "0111", "01105360062", d);
			fail("Should have returned failed");
		} catch (InvalidCertificateException e) {
			assertTrue(true);
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (InterruptedException e) {
			fail("Interrupted exception");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testTACertificatePdfsWrongCertType() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR65&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
			Collection<byte[]> pdfs = SistemaREDI.getTACertificatePDFs(certificateInputStream, "jg@FNMT", "pk6s12",
					"011005185924", "0111", "01105360062", d);
			fail("Should have returned failed");
		} catch (InvalidCertificateException e) {
			assertTrue(true);
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (InterruptedException e) {
			fail("Interrupted exception");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testContributionPdfsOk() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR37&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
			Collection<byte[]> pdfs = SistemaREDI.getContributionPDFs(certificateInputStream, "jg@FNMT", "pkcs12",
					"011005185924", "0111", "01105360062", d);
			for (byte[] pdf : pdfs) {
				if (!(pdf.length > 0))
					fail("Should have returned a pdf");
			}
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (InterruptedException e) {
			fail("Interrupted exception");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testContributionPdfsWrongCCC() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR37&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
			Collection<byte[]> pdfs = SistemaREDI.getContributionPDFs(certificateInputStream, "jg@FNMT", "pkcs12",
					"011005185924", "0111", "01105368062", d);
			fail("Should have returned a pdf");
		} catch (InvalidCccException e) {
			assertTrue(true);
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (InterruptedException e) {
			fail("Interrupted exception");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testContributionPdfsNullCCC() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR37&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
			Collection<byte[]> pdfs = SistemaREDI.getContributionPDFs(certificateInputStream, "jg@FNMT", "pkcs12",
					"011005185924", "0111", "", d);
			fail("Should have returned a pdf");
		} catch (UnfilledMandatory e) {
			assertTrue(true);
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (InterruptedException e) {
			fail("Interrupted exception");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testContributionPdfsNullRegime() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR37&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
			Collection<byte[]> pdfs = SistemaREDI.getContributionPDFs(certificateInputStream, "jg@FNMT", "pkcs12",
					"011005185924", "", "01105360062", d);
			fail("Should have returned a pdf");
		} catch (UnfilledMandatory e) {
			assertTrue(true);
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (InterruptedException e) {
			fail("Interrupted exception");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testContributionPdfsWrongRegime() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR37&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
			Collection<byte[]> pdfs = SistemaREDI.getContributionPDFs(certificateInputStream, "jg@FNMT", "pkcs12",
					"011005185924", "0181", "01105360062", d);
			fail("Should have returned a pdf");
		} catch (WrongRegimeException e) {
			assertTrue(true);
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (InterruptedException e) {
			fail("Interrupted exception");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testContributionPdfsWrongDate() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR37&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Calendar c = Calendar.getInstance();
			c.add(Calendar.YEAR, 1);
			Date d = c.getTime();
			Collection<byte[]> pdfs = SistemaREDI.getContributionPDFs(certificateInputStream, "jg@FNMT", "pkcs12",
					"011005185924", "0111", "01105360062", d);
			fail("Should have thrown an exception");
		} catch (InvalidDataException e) {
			assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (InterruptedException e) {
			fail("Interrupted exception");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testContributionPdfsWrongCert() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR37&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMp12")) {
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
			Collection<byte[]> pdfs = SistemaREDI.getContributionPDFs(certificateInputStream, "jg@FNMT", "pkcs12",
					"011005185924", "0111", "01105360062", d);
			fail("Should have returned failed");
		} catch (CertificateNotFoundException e) {
			assertTrue(true);
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (InterruptedException e) {
			fail("Interrupted exception");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testContributionPdfsWrongCertKey() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR37&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
			Collection<byte[]> pdfs = SistemaREDI.getContributionPDFs(certificateInputStream, "jg@NMT", "pkcs12",
					"011005185924", "0111", "01105360062", d);
			fail("Should have returned failed");
		} catch (InvalidCertificateException e) {
			assertTrue(true);
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (InterruptedException e) {
			fail("Interrupted exception");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testContributionPdfsWrongCertType() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR37&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
			Collection<byte[]> pdfs = SistemaREDI.getContributionPDFs(certificateInputStream, "jg@FNMT", "pk6s12",
					"011005185924", "0111", "01105360062", d);
			fail("Should have returned failed");
		} catch (InvalidCertificateException e) {
			assertTrue(true);
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (InterruptedException e) {
			fail("Interrupted exception");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testObligationAwarenessOk() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=RCR92&E=I&AP=DEUR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			byte[] pdf = SistemaREDI.getObligationAwarenessCertificate(certificateInputStream, "jg@FNMT", "pkcs12",
					"0111", "01105360062");
			if (!(pdf.length > 0))
				fail("Should have returned a pdf");
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testObligationAwarenessWrongCCC() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=RCR92&E=I&AP=DEUR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			byte[] pdf = SistemaREDI.getObligationAwarenessCertificate(certificateInputStream, "jg@FNMT", "pkcs12",
					"0111", "01105367062");
			fail("Shouldn't have returned a pdf");
		} catch (WrongIdentifierException e) {
			assertTrue(true);
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testObligationAwarenessNullRegime() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=RCR92&E=I&AP=DEUR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			byte[] pdf = SistemaREDI.getObligationAwarenessCertificate(certificateInputStream, "jg@FNMT", "pkcs12", "",
					"01105360062");
			fail("Shouldn't have returned a pdf");
		} catch (UnfilledMandatory e) {
			assertTrue(true);
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testObligationAwarenessInvalidRegime() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=RCR92&E=I&AP=DEUR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			byte[] pdf = SistemaREDI.getObligationAwarenessCertificate(certificateInputStream, "jg@FNMT", "pkcs12",
					"sdsa", "01105360062");
			fail("Shouldn't have returned a pdf");
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (SegSocialException e) {
		}
	}

	@Test
	public void testObligationAwarenessWrongCert() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=RCR92&E=I&AP=DEUR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT2")) {
			byte[] pdf = SistemaREDI.getObligationAwarenessCertificate(certificateInputStream, "jg@FNMT", "pkcs12",
					"0111", "01105360062");
			fail("Shouldn't have returned a pdf");
		} catch (CertificateNotFoundException e) {
			assertTrue(true);
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testObligationAwarenessWrongCertKey() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=RCR92&E=I&AP=DEUR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			byte[] pdf = SistemaREDI.getObligationAwarenessCertificate(certificateInputStream, "jg@FNoT", "pkcs12",
					"0111", "01105360062");
			fail("Shouldn't have returned a pdf");
		} catch (InvalidCertificateException e) {
			assertTrue(true);
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testObligationAwarenessWrongCertType() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=RCR92&E=I&AP=DEUR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			byte[] pdf = SistemaREDI.getObligationAwarenessCertificate(certificateInputStream, "jg@FNMT", "pk4s12",
					"0111", "01105360062");
			fail("Shouldn't have returned a pdf");
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (InvalidCertificateException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testgetIdcsOk() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR65&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Collection<Idc> idcs = SistemaREDI.getIDCDates(certificateInputStream, "jg@FNMT", "pkcs12", "011005185924",
					"0111", "01105360062");
			for (Idc idc : idcs) {
				if (idc.getFecha() == null) {
					fail("Not taking well idc dates");
				}
			}
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testgetIDCNSS() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR39&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			byte data[] = SistemaREDI.getContributionInformationNSS(certificateInputStream, "jg@FNMT", "pkcs12", "0111",
					"01105360062", "011005185924", new Date());
			PDDocument doc = Loader.loadPDF(data);
			doc.close();
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetIdcsWrongCCC() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR65&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Collection<Idc> pdfs = SistemaREDI.getIDCDates(certificateInputStream, "jg@FNMT", "pkcs12", "011005185924",
					"0111", "01105368062");
			fail("Should have returned a pdf");
		} catch (InvalidCccException e) {
			assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetIdcsNullCCC() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR65&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Collection<Idc> pdfs = SistemaREDI.getIDCDates(certificateInputStream, "jg@FNMT", "pkcs12", "011005185924",
					"0111", "");
			fail("Should have returned a pdf");
		} catch (UnfilledMandatory e) {
			assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetIdcsNullRegime() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR65&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Collection<Idc> pdfs = SistemaREDI.getIDCDates(certificateInputStream, "jg@FNMT", "pkcs12", "011005185924",
					"", "01105360062");
			fail("Should have returned a pdf");
		} catch (UnfilledMandatory e) {
			assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetIdcsWrongRegime() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR65&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Collection<Idc> pdfs = SistemaREDI.getIDCDates(certificateInputStream, "jg@FNMT", "pkcs12", "011005185924",
					"0181", "01105360062");
			fail("Should have returned a pdf");
		} catch (NotAllowedContributionAccount e) {
			assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (WrongRegimeException e) {
			assertTrue(true);
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetIdcsWrongCert() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR65&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMp12")) {
			Collection<Idc> pdfs = SistemaREDI.getIDCDates(certificateInputStream, "jg@FNMT", "pkcs12", "011005185924",
					"0111", "01105360062");
			fail("Should have returned failed");
		} catch (CertificateNotFoundException e) {
			assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetIdcsWrongCertKey() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR65&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Collection<Idc> pdfs = SistemaREDI.getIDCDates(certificateInputStream, "jg@NMT", "pkcs12", "011005185924",
					"0111", "01105360062");
			fail("Should have returned failed");
		} catch (InvalidCertificateException e) {
			assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetIdcsWrongCertType() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR65&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Collection<Idc> pdfs = SistemaREDI.getIDCDates(certificateInputStream, "jg@FNMT", "pk6s12", "011005185924",
					"0111", "01105360062");
			fail("Should have returned failed");
		} catch (InvalidCertificateException e) {
			assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetDischargeDatesOk() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR37&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Collection<Date> dates = SistemaREDI.getDischargeDates(certificateInputStream, "jg@FNMT", "pkcs12",
					"011005185924", "0111", "01105360062");
			for (Date date : dates) {
				if (date == null) {
					fail("Not picking up some dates");
				}
			}
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetDischargeDatesWrongCCC() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR37&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Collection<Date> dates = SistemaREDI.getDischargeDates(certificateInputStream, "jg@FNMT", "pkcs12",
					"011005185924", "0111", "01105760562");
		} catch (InvalidCccException e) {
			assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetDischargeDatesWrongCert() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR37&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMp12")) {
			Collection<Date> dates = SistemaREDI.getDischargeDates(certificateInputStream, "jg@FNMT", "pkcs12",
					"011005185924", "0111", "01105360062");
			fail("Should have failed");
		} catch (CertificateNotFoundException e) {
			assertTrue(true);
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetDischargeDatesWrongCertKey() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR37&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Collection<Date> dates = SistemaREDI.getDischargeDates(certificateInputStream, "jg@NMT", "pkcs12",
					"011005185924", "0111", "01105360062");
			fail("Should have failed");
		} catch (InvalidCertificateException e) {
			assertTrue(true);
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testGetDischargeDatesWrongCertType() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR37&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Collection<Date> dates = SistemaREDI.getDischargeDates(certificateInputStream, "jg@FNMT", "pk5s12",
					"011005185924", "0111", "01105360062");
			fail("Should have failed");
		} catch (InvalidCertificateException e) {
			assertTrue(true);
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testContributionSettlementReportOk() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR39&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-11-2020");
			Optional<Date> opDate = Optional.of(d);
			byte[] pdf = SistemaREDI.getContributionSettlementReport(certificateInputStream, "jg@FNMT", "pkcs12",
					"011017250195", "0111", "01105577910", opDate);
			if (!(pdf.length > 0)) {
				fail("Didn't return a pdf");
			}
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (ParseException e) {
			fail("Wrongly written date");
		} catch (IOException e1) {
			fail("IO exception");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testContributionSettlementReportOkNoDate() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR39&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			// Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-11-2020");
			Optional<Date> opDate = Optional.empty();
			byte[] pdf = SistemaREDI.getContributionSettlementReport(certificateInputStream, "jg@FNMT", "pkcs12",
					"011017250195", "0111", "01105577910", opDate);
			if (!(pdf.length > 0)) {
				fail("Didn't return a pdf");
			}
		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (IOException e1) {
			fail("IO exception");
		} catch (DataDoesNotExist e) {
			
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testContributionSettlementReportWrongCCC() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR39&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-11-2020");
			Optional<Date> opDate = Optional.of(d);
			byte[] pdf = SistemaREDI.getContributionSettlementReport(certificateInputStream, "jg@FNMT", "pkcs12",
					"011017250195", "0111", "01100477910", opDate);
			fail("Shouldn't run");
		} catch (InvalidCccException e) {

		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (ParseException e) {
			fail("Wrongly written date");
		} catch (IOException e1) {
			fail("IO exception");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testContributionSettlementReportWrongDate() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR39&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-11-2001");
			Optional<Date> opDate = Optional.of(d);
			byte[] pdf = SistemaREDI.getContributionSettlementReport(certificateInputStream, "jg@FNMT", "pkcs12",
					"011017250195", "0111", "01105577910", opDate);
			fail("Shouldn't run");
		} catch (DataDoesNotExist e) {

		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (ParseException e) {
			fail("Wrongly written date");
		} catch (IOException e1) {
			fail("IO exception");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testContributionSettlementReportNoNAF() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR39&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-11-2020");
			Optional<Date> opDate = Optional.of(d);
			byte[] pdf = SistemaREDI.getContributionSettlementReport(certificateInputStream, "jg@FNMT", "pkcs12", "",
					"0111", "01105577910", opDate);
			fail("Shouldn't run");
		} catch (UnfilledMandatory e) {

		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (ParseException e) {
			fail("Wrongly written date");
		} catch (IOException e1) {
			fail("IO exception");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testContributionSettlementReportNoCCC() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR39&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-11-2020");
			Optional<Date> opDate = Optional.of(d);
			byte[] pdf = SistemaREDI.getContributionSettlementReport(certificateInputStream, "jg@FNMT", "pkcs12",
					"011017250195", "0111", "", opDate);
			fail("Shouldn't run");
		} catch (UnfilledMandatory e) {

		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (ParseException e) {
			fail("Wrongly written date");
		} catch (IOException e1) {
			fail("IO exception");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testContributionSettlementReportWrongCert() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR39&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FN12")) {
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-11-2020");
			Optional<Date> opDate = Optional.of(d);
			byte[] pdf = SistemaREDI.getContributionSettlementReport(certificateInputStream, "jg@FNMT", "pkcs12",
					"011017250195", "0111", "01105577910", opDate);
			fail("Shouldn't run");
		} catch (CertificateNotFoundException e) {

		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (ParseException e) {
			fail("Wrongly written date");
		} catch (IOException e1) {
			fail("IO exception");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testContributionSettlementReportWrongCertKey() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR39&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-11-2020");
			Optional<Date> opDate = Optional.of(d);
			byte[] pdf = SistemaREDI.getContributionSettlementReport(certificateInputStream, "jg@FNT", "pkcs12",
					"011017250195", "0111", "01105577910", opDate);
			fail("Shouldn't run");
		} catch (InvalidCertificateException e) {

		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (ParseException e) {
			fail("Wrongly written date");
		} catch (IOException e1) {
			fail("IO exception");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}

	@Test
	public void testContributionSettlementReportWrongCertType() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR39&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Date d = new SimpleDateFormat("dd-MM-yyyy").parse("01-11-2020");
			Optional<Date> opDate = Optional.of(d);
			byte[] pdf = SistemaREDI.getContributionSettlementReport(certificateInputStream, "jg@FNMT", "kcs12",
					"011017250195", "0111", "01105577910", opDate);
			fail("Shouldn't run");
		} catch (InvalidCertificateException e) {

		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (ParseException e) {
			fail("Wrongly written date");
		} catch (IOException e1) {
			fail("IO exception");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	public void testgetIDCDates() {
		testDisponibility("https://w2.seg-social.es/Xhtml?JacadaApplicationName=SGIRED&TRANSACCION=ATR65&E=I&AP=AFIR");
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			Collection<Idc> idcs = SistemaREDI.getIDCDates(certificateInputStream, "jg@FNMT", "pkcs12", "011007308507",
					"0111", "01105360062");
			Date dates [] = 
			idcs.stream()
			.map(i -> i.getFecha() )
			.sorted()
			.toArray(Date[]::new);
			
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, 0);
			calendar.set(Calendar.MINUTE, 0);
			calendar.set(Calendar.SECOND, 0);
			calendar.set(Calendar.MILLISECOND, 0);
			
			calendar.set(Calendar.YEAR, 2013);
			calendar.set(Calendar.DAY_OF_MONTH, 4);
			calendar.set(Calendar.MONTH, Calendar.SEPTEMBER);
			
			Date september42013 = calendar.getTime();
			Assert.assertEquals(september42013, dates[0]);
			
			calendar.set(Calendar.DAY_OF_MONTH, 3);
			calendar.set(Calendar.YEAR, 2016);
			
			Date september32016 = calendar.getTime();
			Assert.assertEquals(september32016, dates[1]);
			
			calendar.set(Calendar.DAY_OF_MONTH, 4);
			
			Date september42016 = calendar.getTime();
			Assert.assertEquals(september42016, dates[2]);
			
			Assert.assertEquals(3, dates.length);

		} catch (StatusCodeException | OutOfServiceException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (SegSocialException e) {
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	

}
