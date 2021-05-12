package solutions.aon.seg.social;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

import solutions.aon.seg.social.SistemaREDI;
import solutions.aon.seg.social.exception.CertificateNotFoundException;
import solutions.aon.seg.social.exception.InvalidCertificateException;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.invalid.DataDoesNotExist;
import solutions.aon.seg.social.exception.invalid.InvalidDataException;
import solutions.aon.seg.social.exception.invalid.NotAllowedContributionAccount;
import solutions.aon.seg.social.exception.invalid.UnfilledMandatory;
import solutions.aon.seg.social.exception.invalid.WrongIdentifierException;
import solutions.aon.seg.social.exception.invalid.WrongRegimeException;
import solutions.aon.seg.social.exception.invalid.invalidCccException;
import solutions.aon.seg.social.object.Idc;
import solutions.aon.seg.social.object.SituacionEmpresa;

public class TestSistemaREDI {

	@Test
	public void testSituacionEmpresaOk() {
		try (final InputStream certificateInputStream =TestSistemaREDI.class.getResourceAsStream("FNMT.p12"))
		  { SituacionEmpresa se=SistemaREDI.getSituacionEmpresa(certificateInputStream, "jg@FNMT",
		  "pkcs12", "0111", "01105360062");
		  	if(se.getCcc()!=null) {
		  		assertTrue(true);
		  	}
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
			fail("Wrong data");
		}
	}
	
	@Test
	public void testSituacionEmpresaWrongRegime() {
		try (final InputStream certificateInputStream =TestSistemaREDI.class.getResourceAsStream("FNMT.p12"))
		  { SituacionEmpresa se=SistemaREDI.getSituacionEmpresa(certificateInputStream, "jg@FNMT",
		  "pkcs12", "0161", "01105360062");
		  	fail("Shouldn't finish");
		  } catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		  } catch(WrongRegimeException e) {
			  assertTrue(true);
		  } catch (MalformedURLException e) {
			fail("Wrong url");
		} catch (IOException e) {
			fail("Certificate input problem");
			e.printStackTrace();
		} catch (InterruptedException e) {
			fail("Interrupted exception");
		} catch (SegSocialException e) {
			fail("Wrong data");
		}
	}
	
	@Test
	public void testSituacionEmpresaUnfilledCCC() {
		try (final InputStream certificateInputStream =TestSistemaREDI.class.getResourceAsStream("FNMT.p12"))
		  { SituacionEmpresa se=SistemaREDI.getSituacionEmpresa(certificateInputStream, "jg@FNMT",
		  "pkcs12", "0111", "");
		  	fail("Shouldn't finish");
		  }catch(UnfilledMandatory e) {
			  assertTrue(true);
		  }
		catch (MalformedURLException e) {
			fail("Wrong url");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		  } catch (IOException e) {
			fail("Certificate input problem");
			e.printStackTrace();
		} catch (InterruptedException e) {
			fail("Interrupted exception");
		} catch (SegSocialException e) {
			System.out.println(e.getClass());
			fail("Wrong data");
		}
	}
	
	
	@Test
	public void testSituacionEmpresaWrongCert() {
		try (final InputStream certificateInputStream =TestSistemaREDI.class.getResourceAsStream("FNM2"))
		  { SituacionEmpresa se=SistemaREDI.getSituacionEmpresa(certificateInputStream, "jg@FNMT",
		  "pkcs12", "0111", "01105360062");
		  	fail("Shouldn't finish");
		  } catch (CertificateNotFoundException e) {
			  assertTrue(true);
		  }
		catch (MalformedURLException e) {
			fail("Wrong url");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		  } catch (IOException e) {
			fail("Certificate input problem");
			e.printStackTrace();
		} catch (InterruptedException e) {
			fail("Interrupted exception");
		} catch (SegSocialException e) {
			System.out.println(e.getClass());
			fail("Wrong data");
		}
	}
	
	@Test
	public void testSituacionEmpresaWrongCertKey() {
		try (final InputStream certificateInputStream =TestSistemaREDI.class.getResourceAsStream("FNMT.p12"))
		  { SituacionEmpresa se=SistemaREDI.getSituacionEmpresa(certificateInputStream, "jgfFNMT",
		  "pkcs12", "0111", "01105360062");
		  	fail("Shouldn't finish");
		  } catch (InvalidCertificateException e) {
			  assertTrue(true);
		  }
		catch (MalformedURLException e) {
			fail("Wrong url");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		  } catch (IOException e) {
			fail("Certificate input problem");
			e.printStackTrace();
		} catch (InterruptedException e) {
			fail("Interrupted exception");
		} catch (SegSocialException e) {
			System.out.println(e.getClass());
			fail("Wrong data");
		}
	}
	
	@Test
	public void testSituacionEmpresaWrongCertType() {
		try (final InputStream certificateInputStream =TestSistemaREDI.class.getResourceAsStream("FNMT.p12"))
		  { SituacionEmpresa se=SistemaREDI.getSituacionEmpresa(certificateInputStream, "jg@FNMT",
		  "pkcs15", "0111", "01105360062");
		  	fail("Shouldn't finish");
		  } catch (InvalidCertificateException e) {
			  assertTrue(true);
		  }
		catch (MalformedURLException e) {
			fail("Wrong url");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		  } catch (IOException e) {
			fail("Certificate input problem");
			e.printStackTrace();
		} catch (InterruptedException e) {
			fail("Interrupted exception");
		} catch (SegSocialException e) {
			System.out.println(e.getClass());
			fail("Wrong data");
		}
	}
	
	
	
	@Test
	public void testGetTADuplicateOk() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")){
		  Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
		  byte[] pdf=SistemaREDI.getTADuplicate(certificateInputStream, "jg@FNMT", "pkcs12","011005185924", "0111", "01105360062", d);
		  if(!(pdf.length>0))
			  fail("Should have returned a pdf");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		} catch (SegSocialException e) {
			fail("SegSocialException");
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		}
	}
	
	@Test
	public void testGetTADuplicateOk2() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")){
		  Date d=new SimpleDateFormat("dd-MM-yyyy").parse("09-09-2020");
		  byte[] pdf=SistemaREDI.getTADuplicate(certificateInputStream, "jg@FNMT", "pkcs12","291136796369", "0111", "01105360062", d);
		  if(!(pdf.length>0))
			  fail("Should have returned a pdf");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		} catch (SegSocialException e) {
			fail("SegSocialException");
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		}
	}
	
	@Test
	public void testGetTADuplicateWrongDate() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")){
			Calendar c=Calendar.getInstance();
			c.add(Calendar.YEAR, 1);
		  Date d=c.getTime();
		  byte[] pdf=SistemaREDI.getTADuplicate(certificateInputStream, "jg@FNMT", "pkcs12","011005185924", "0111", "01105360062", d);
		} catch(InvalidDataException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		} catch (SegSocialException e) {
			fail("SegSocialException");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		}
	}
	
	
	@Test
	public void testGetTADuplicateWrongCCC() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")){
			Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
		  byte[] pdf=SistemaREDI.getTADuplicate(certificateInputStream, "jg@FNMT", "pkcs12","011005185924", "0111", "01105760562", d);
		} catch(invalidCccException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		} catch (SegSocialException e) {
			fail("SegSocialException");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (ParseException e) {
			fail("Wrong data input");
		}
	}
	
	
	@Test
	public void testGetTADuplicateWrongCert() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMp12")){
		  Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
		  byte[] pdf=SistemaREDI.getTADuplicate(certificateInputStream, "jg@FNMT", "pkcs12","011005185924", "0111", "01105360062", d);
		  fail("Should have failed");
		} catch (CertificateNotFoundException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		} catch (SegSocialException e) {
			fail("SegSocialException");
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		}
	}
	
	
	@Test
	public void testGetTADuplicateWrongCertKey() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")){
		  Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
		  byte[] pdf=SistemaREDI.getTADuplicate(certificateInputStream, "jg@NMT", "pkcs12","011005185924", "0111", "01105360062", d);
		  fail("Should have failed");
		} catch (InvalidCertificateException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		} catch (SegSocialException e) {
			fail("SegSocialException");
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		}
	}
	
	@Test
	public void testGetTADuplicateWrongCertType() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")){
		  Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
		  byte[] pdf=SistemaREDI.getTADuplicate(certificateInputStream, "jg@FNMT", "pk5s12","011005185924", "0111", "01105360062", d);
		  fail("Should have failed");
		} catch (InvalidCertificateException e) {
			assertTrue(true);
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		} catch (SegSocialException e) {
			fail("SegSocialException");
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		}
	}
	

	
	
	
	
	
	@Test
	public void testContributionInfoCCCDuplicateOk() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")){
		  Date d=new Date();
		  byte[] pdf=SistemaREDI.getContributionInformationCCC(certificateInputStream, "jg@FNMT", "pkcs12","0111", "01105360062", d);
		  if(!(pdf.length>0))
			  fail("Should have returned a pdf");
		} catch (SegSocialException e) {
			fail("SegSocialException");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		}
	}
	
	
	@Test
	public void testContributionInfoDuplicateOk() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")){
		  Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
		  byte[] pdf=SistemaREDI.getContributionInformation(certificateInputStream, "jg@FNMT", "pkcs12","011005185924", "0111", "01105360062", d);
		  if(!(pdf.length>0))
			  fail("Should have returned a pdf");
		} catch (SegSocialException e) {
			fail("SegSocialException");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		}
	}
	
	@Test
	public void testGetContributionInfoWrongDate() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")){
			Calendar c=Calendar.getInstance();
			c.add(Calendar.YEAR, 1);
		  Date d=c.getTime();
		  byte[] pdf=SistemaREDI.getContributionInformation(certificateInputStream, "jg@FNMT", "pkcs12","011005185924", "0111", "01105360062", d);
		} catch(InvalidDataException e) {
			assertTrue(true);
		} catch (SegSocialException e) {
			fail("SegSocialException");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		}
	}
	
	
	@Test
	public void testGetContributionInfoWrongCCC() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")){
			Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
		  byte[] pdf=SistemaREDI.getContributionInformation(certificateInputStream, "jg@FNMT", "pkcs12","011005185924", "0111", "01105760562", d);
		} catch(invalidCccException e) {
			assertTrue(true);
		} catch (SegSocialException e) {
			fail("SegSocialException");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		} catch (ParseException e) {
			fail("Wrong data input");
		}
	}
	
	
	@Test
	public void testGetContributionInfoWrongCert() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMp12")){
		  Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
		  byte[] pdf=SistemaREDI.getContributionInformation(certificateInputStream, "jg@FNMT", "pkcs12","011005185924", "0111", "01105360062", d);
		  fail("Should have failed");
		} catch (CertificateNotFoundException e) {
			assertTrue(true);
		} catch (SegSocialException e) {
			fail("SegSocialException");
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		}
	}
	
	
	@Test
	public void testGetContributionInfoWrongCertKey() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")){
		  Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
		  byte[] pdf=SistemaREDI.getContributionInformation(certificateInputStream, "jg@NMT", "pkcs12","011005185924", "0111", "01105360062", d);
		  fail("Should have failed");
		} catch (InvalidCertificateException e) {
			assertTrue(true);
		} catch (SegSocialException e) {
			fail("SegSocialException");
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		}
	}
	
	@Test
	public void testGetContributionInfoWrongCertType() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")){
		  Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
		  byte[] pdf=SistemaREDI.getContributionInformation(certificateInputStream, "jg@FNMT", "pk5s12","011005185924", "0111", "01105360062", d);
		  fail("Should have failed");
		} catch (InvalidCertificateException e) {
			assertTrue(true);
		} catch (SegSocialException e) {
			fail("SegSocialException");
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		}
	}
	
	
	
	
	
	@Test
	public void testTACertificatePdfsOk() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")){
		  Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
		  Collection<byte[]> pdfs=SistemaREDI.getTACertificatePDFs(certificateInputStream, "jg@FNMT", "pkcs12","011005185924", "0111", "01105360062", d);
		  for(byte[] pdf:pdfs){
			  if(!(pdf.length>0))
				  fail("Should have returned a pdf");  
		  }
		} catch (SegSocialException e) {
			fail("SegSocialException");
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		} catch (InterruptedException e) {
			fail("Interrupted exception");
		}
	}
	
	
	@Test
	public void testTACertificatePdfsWrongCCC() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")){
		  Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
		  Collection<byte[]> pdfs=SistemaREDI.getTACertificatePDFs(certificateInputStream, "jg@FNMT", "pkcs12","011005185924", "0111", "01105368062", d);
			fail("Should have returned a pdf");  
		} catch (invalidCccException e) {
			assertTrue(true);
		} catch (SegSocialException e) {
			fail("SegSocialException");
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		} catch (InterruptedException e) {
			fail("Interrupted exception");
		}
	}
	
	@Test
	public void testTACertificatePdfsNullCCC() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")){
		  Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
		  Collection<byte[]> pdfs=SistemaREDI.getTACertificatePDFs(certificateInputStream, "jg@FNMT", "pkcs12","011005185924", "0111", "", d);
			fail("Should have returned a pdf");  
		} catch (UnfilledMandatory e) {
			assertTrue(true);
		} catch (SegSocialException e) {
			System.out.println(e.getClass());
			fail("SegSocialException");
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		} catch (InterruptedException e) {
			fail("Interrupted exception");
		}
	}
	
	
	@Test
	public void testTACertificatePdfsNullRegime() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")){
		  Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
		  Collection<byte[]> pdfs=SistemaREDI.getTACertificatePDFs(certificateInputStream, "jg@FNMT", "pkcs12","011005185924", "", "01105360062", d);
			fail("Should have returned a pdf");  
		} catch (UnfilledMandatory e) {
			assertTrue(true);
		} catch (SegSocialException e) {
			System.out.println(e.getClass());
			fail("SegSocialException");
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		} catch (InterruptedException e) {
			fail("Interrupted exception");
		}
	}
	
	
	@Test
	public void testTACertificatePdfsWrongRegime() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")){
		  Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
		  Collection<byte[]> pdfs=SistemaREDI.getTACertificatePDFs(certificateInputStream, "jg@FNMT", "pkcs12","011005185924", "0181", "01105360062", d);
			fail("Should have returned a pdf");  
		} catch (NotAllowedContributionAccount e) {
			assertTrue(true);
		} catch (SegSocialException e) {
			fail("SegSocialException");
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		} catch (InterruptedException e) {
			fail("Interrupted exception");
		}
	}
	
	
	@Test
	public void testTACertificatePdfsWrongDate() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")){
		  Calendar c=Calendar.getInstance();
		  c.add(Calendar.YEAR, 1);
			Date d=c.getTime();
		  Collection<byte[]> pdfs=SistemaREDI.getTACertificatePDFs(certificateInputStream, "jg@FNMT", "pkcs12","011005185924", "0111", "01105360062", d);
		  fail("Should have thrown an exception");
		} catch (InvalidDataException e) {
			assertTrue(true);
		} catch (SegSocialException e) {
			System.out.println(e.getClass());
			fail("SegSocialException");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		} catch (InterruptedException e) {
			fail("Interrupted exception");
		}
	}
	
	@Test
	public void testTACertificatePdfsWrongCert() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMp12")){
		  Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
		  Collection<byte[]> pdfs=SistemaREDI.getTACertificatePDFs(certificateInputStream, "jg@FNMT", "pkcs12","011005185924", "0111", "01105360062", d);
		  	fail("Should have returned failed");  
		} catch(CertificateNotFoundException e) {
			assertTrue(true);
		}
		catch (SegSocialException e) {
			fail("SegSocialException");
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		} catch (InterruptedException e) {
			fail("Interrupted exception");
		}
	}
	
	
	@Test
	public void testTACertificatePdfsWrongCertKey() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")){
		  Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
		  Collection<byte[]> pdfs=SistemaREDI.getTACertificatePDFs(certificateInputStream, "jg@NMT", "pkcs12","011005185924", "0111", "01105360062", d);
		  	fail("Should have returned failed");  
		} catch(InvalidCertificateException e) {
			assertTrue(true);
		}
		catch (SegSocialException e) {
			fail("SegSocialException");
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		} catch (InterruptedException e) {
			fail("Interrupted exception");
		}
	}
	
	@Test
	public void testTACertificatePdfsWrongCertType() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")){
		  Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
		  Collection<byte[]> pdfs=SistemaREDI.getTACertificatePDFs(certificateInputStream, "jg@FNMT", "pk6s12","011005185924", "0111", "01105360062", d);
		  	fail("Should have returned failed");  
		} catch(InvalidCertificateException e) {
			assertTrue(true);
		}
		catch (SegSocialException e) {
			fail("SegSocialException");
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		} catch (InterruptedException e) {
			fail("Interrupted exception");
		}
	}
	
	
	
	
	
	
	
	
	@Test
	public void testContributionPdfsOk() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")){
		  Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
		  Collection<byte[]> pdfs=SistemaREDI.getContributionPDFs(certificateInputStream, "jg@FNMT", "pkcs12","011005185924", "0111", "01105360062", d);
		  for(byte[] pdf:pdfs){
			  if(!(pdf.length>0))
				  fail("Should have returned a pdf");  
		  }
		} catch (SegSocialException e) {
			fail("SegSocialException");
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		} catch (InterruptedException e) {
			fail("Interrupted exception");
		}
	}
	
	
	@Test
	public void testContributionPdfsWrongCCC() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")){
		  Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
		  Collection<byte[]> pdfs=SistemaREDI.getContributionPDFs(certificateInputStream, "jg@FNMT", "pkcs12","011005185924", "0111", "01105368062", d);
			fail("Should have returned a pdf");  
		} catch (invalidCccException e) {
			assertTrue(true);
		} catch (SegSocialException e) {
			fail("SegSocialException");
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		} catch (InterruptedException e) {
			fail("Interrupted exception");
		}
	}
	
	@Test
	public void testContributionPdfsNullCCC() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")){
		  Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
		  Collection<byte[]> pdfs=SistemaREDI.getContributionPDFs(certificateInputStream, "jg@FNMT", "pkcs12","011005185924", "0111", "", d);
			fail("Should have returned a pdf");  
		} catch (UnfilledMandatory e) {
			assertTrue(true);
		} catch (SegSocialException e) {
			fail("SegSocialException");
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		} catch (InterruptedException e) {
			fail("Interrupted exception");
		}
	}
	
	
	@Test
	public void testContributionPdfsNullRegime() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")){
		  Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
		  Collection<byte[]> pdfs=SistemaREDI.getContributionPDFs(certificateInputStream, "jg@FNMT", "pkcs12","011005185924", "", "01105360062", d);
			fail("Should have returned a pdf");  
		} catch (UnfilledMandatory e) {
			assertTrue(true);
		} catch (SegSocialException e) {
			fail("SegSocialException");
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		} catch (InterruptedException e) {
			fail("Interrupted exception");
		}
	}
	
	
	@Test
	public void testContributionPdfsWrongRegime() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")){
		  Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
		  Collection<byte[]> pdfs=SistemaREDI.getContributionPDFs(certificateInputStream, "jg@FNMT", "pkcs12","011005185924", "0181", "01105360062", d);
			fail("Should have returned a pdf");  
		} catch (WrongRegimeException e) {
			assertTrue(true);
		} catch (SegSocialException e) {			
			fail("SegSocialException");
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		} catch (InterruptedException e) {
			fail("Interrupted exception");
		}
	}
	
	
	@Test
	public void testContributionPdfsWrongDate() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")){
		  Calendar c=Calendar.getInstance();
		  c.add(Calendar.YEAR, 1);
			Date d=c.getTime();
		  Collection<byte[]> pdfs=SistemaREDI.getContributionPDFs(certificateInputStream, "jg@FNMT", "pkcs12","011005185924", "0111", "01105360062", d);
		  fail("Should have thrown an exception");
		} catch (InvalidDataException e) {
			assertTrue(true);
		} catch (SegSocialException e) {
			System.out.println(e.getClass());
			fail("SegSocialException");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		} catch (InterruptedException e) {
			fail("Interrupted exception");
		}
	}
	
	@Test
	public void testContributionPdfsWrongCert() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMp12")){
		  Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
		  Collection<byte[]> pdfs=SistemaREDI.getContributionPDFs(certificateInputStream, "jg@FNMT", "pkcs12","011005185924", "0111", "01105360062", d);
		  	fail("Should have returned failed");  
		} catch(CertificateNotFoundException e) {
			assertTrue(true);
		}
		catch (SegSocialException e) {
			fail("SegSocialException");
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		} catch (InterruptedException e) {
			fail("Interrupted exception");
		}
	}
	
	
	@Test
	public void testContributionPdfsWrongCertKey() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")){
		  Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
		  Collection<byte[]> pdfs=SistemaREDI.getContributionPDFs(certificateInputStream, "jg@NMT", "pkcs12","011005185924", "0111", "01105360062", d);
		  	fail("Should have returned failed");  
		} catch(InvalidCertificateException e) {
			assertTrue(true);
		}
		catch (SegSocialException e) {
			fail("SegSocialException");
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		} catch (InterruptedException e) {
			fail("Interrupted exception");
		}
	}
	
	@Test
	public void testContributionPdfsWrongCertType() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")){
		  Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
		  Collection<byte[]> pdfs=SistemaREDI.getContributionPDFs(certificateInputStream, "jg@FNMT", "pk6s12","011005185924", "0111", "01105360062", d);
		  	fail("Should have returned failed");  
		} catch(InvalidCertificateException e) {
			assertTrue(true);
		}
		catch (SegSocialException e) {
			fail("SegSocialException");
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		} catch (InterruptedException e) {
			fail("Interrupted exception");
		}
	}
	
	
	
	@Test
	public void testObligationAwarenessOk() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")){		  
		  byte[] pdf=SistemaREDI.getObligationAwarenessCertificate(certificateInputStream, "jg@FNMT", "pkcs12", "0111", "01105360062");
		  if(!(pdf.length>0))
			  fail("Should have returned a pdf");
		} catch (SegSocialException e) {
			fail("SegSocialException");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		}
	}
	
	
	@Test
	public void testObligationAwarenessWrongCCC() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")){		  
		  byte[] pdf=SistemaREDI.getObligationAwarenessCertificate(certificateInputStream, "jg@FNMT", "pkcs12", "0111", "01105367062");
			  fail("Shouldn't have returned a pdf");
		} catch (WrongIdentifierException e) {
			assertTrue(true);
		} catch (SegSocialException e) {
			System.out.println(e.getClass());
			fail("SegSocialException");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		}
	}
	

	@Test
	public void testObligationAwarenessNullRegime() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")){		  
		  byte[] pdf=SistemaREDI.getObligationAwarenessCertificate(certificateInputStream, "jg@FNMT", "pkcs12", "", "01105360062");
			  fail("Shouldn't have returned a pdf");
		} catch (UnfilledMandatory e) {
			assertTrue(true);
		} catch (SegSocialException e) {
			fail("SegSocialException");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		}
	}
	
	
	@Test
	public void testObligationAwarenessInvalidRegime() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")){		  
		  byte[] pdf=SistemaREDI.getObligationAwarenessCertificate(certificateInputStream, "jg@FNMT", "pkcs12", "sdsa", "01105360062");
			  fail("Shouldn't have returned a pdf");
		} catch (SegSocialException e) {
			System.out.println(e.getMessage());
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		}
	}
	
	
	
	@Test
	public void testObligationAwarenessWrongCert() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT2")){		  
		  byte[] pdf=SistemaREDI.getObligationAwarenessCertificate(certificateInputStream, "jg@FNMT", "pkcs12", "0111", "01105360062");
		  fail("Shouldn't have returned a pdf");
		} catch(CertificateNotFoundException e) {
			assertTrue(true);
		} catch (SegSocialException e) {
			fail("SegSocialException");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		}
	}
	
	
	@Test
	public void testObligationAwarenessWrongCertKey() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")){		  
		  byte[] pdf=SistemaREDI.getObligationAwarenessCertificate(certificateInputStream, "jg@FNoT", "pkcs12", "0111", "01105360062");
		  fail("Shouldn't have returned a pdf");
		} catch(InvalidCertificateException e) {
			assertTrue(true);
		} catch (SegSocialException e) {
			fail("SegSocialException");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		}
	}
	
	
	
	@Test
	public void testObligationAwarenessWrongCertType() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")){		  
		  byte[] pdf=SistemaREDI.getObligationAwarenessCertificate(certificateInputStream, "jg@FNMT", "pk4s12", "0111", "01105360062");
		  fail("Shouldn't have returned a pdf");
		} catch(InvalidCertificateException e) {
			assertTrue(true);
		} catch (SegSocialException e) {
			fail("SegSocialException");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		}
	}
	
	
	
	@Test
	public void testgetIdcsOk() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")){		  
		  Collection<Idc> idcs=SistemaREDI.getIDCDates(certificateInputStream, "jg@FNMT", "pkcs12", "011005185924", "0111", "01105360062");
		  for (Idc idc : idcs) {
			if(idc.getFecha()==null) {
				fail("Not taking well idc dates");
			}
		}
		} catch (SegSocialException e) {
			System.out.println(e.getClass());
			fail("SegSocialException");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		}
	}
	
	@Test
	public void testgetIDCNSS() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")){		  
		  byte data [] = SistemaREDI.getContributionInformationNSS(
				  certificateInputStream, 
				  "jg@FNMT", 
				  "pkcs12", 
				  "0111", 
				  "01105360062",
				  "011005185924", 
				  new Date());
		  PDDocument.load(data);
		} catch (SegSocialException e) {
			System.out.println(e.getMessage());
			fail("SegSocialException");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		}
	}
	
	
	@Test
	public void testGetIdcsWrongCCC() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")){
		  Collection<Idc> pdfs=SistemaREDI.getIDCDates(certificateInputStream, "jg@FNMT", "pkcs12","011005185924", "0111", "01105368062");
			fail("Should have returned a pdf");  
		} catch (invalidCccException e) {
			assertTrue(true);
		} catch (SegSocialException e) {
			fail("SegSocialException");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		}
	}
	
	@Test
	public void testGetIdcsNullCCC() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")){
		  Collection<Idc> pdfs=SistemaREDI.getIDCDates(certificateInputStream, "jg@FNMT", "pkcs12","011005185924", "0111", "");
			fail("Should have returned a pdf");  
		} catch (UnfilledMandatory e) {
			assertTrue(true);
		} catch (SegSocialException e) {
			fail("SegSocialException");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		}
	}
	
	
	@Test
	public void testGetIdcsNullRegime() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")){
		  Collection<Idc> pdfs=SistemaREDI.getIDCDates(certificateInputStream, "jg@FNMT", "pkcs12","011005185924", "", "01105360062");
			fail("Should have returned a pdf");  
		} catch (UnfilledMandatory e) {
			assertTrue(true);
		} catch (SegSocialException e) {
			System.out.println(e.getClass());
			fail("SegSocialException");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		}
	}
	
	
	@Test
	public void testGetIdcsWrongRegime() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")){
		  Collection<Idc> pdfs=SistemaREDI.getIDCDates(certificateInputStream, "jg@FNMT", "pkcs12","011005185924", "0181", "01105360062");
			fail("Should have returned a pdf");  
		} catch (NotAllowedContributionAccount e) {
			assertTrue(true);
		} catch (SegSocialException e) {
			fail("SegSocialException");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		}
	}

	
	
	@Test
	public void testGetIdcsWrongCert() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMp12")){
		  Collection<Idc> pdfs=SistemaREDI.getIDCDates(certificateInputStream, "jg@FNMT", "pkcs12","011005185924", "0111", "01105360062");
		  	fail("Should have returned failed");  
		} catch(CertificateNotFoundException e) {
			assertTrue(true);
		}
		catch (SegSocialException e) {
			fail("SegSocialException");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		}
	}
	
	
	@Test
	public void testGetIdcsWrongCertKey() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")){
			Collection<Idc> pdfs=SistemaREDI.getIDCDates(certificateInputStream, "jg@NMT", "pkcs12","011005185924", "0111", "01105360062");
		  	fail("Should have returned failed");  
		} catch(InvalidCertificateException e) {
			assertTrue(true);
		}
		catch (SegSocialException e) {
			fail("SegSocialException");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		}
	}
	
	@Test
	public void testGetIdcsWrongCertType() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")){
			Collection<Idc> pdfs=SistemaREDI.getIDCDates(certificateInputStream, "jg@FNMT", "pk6s12","011005185924", "0111", "01105360062");
		  	fail("Should have returned failed");  
		} catch(InvalidCertificateException e) {
			assertTrue(true);
		}
		catch (SegSocialException e) {
			fail("SegSocialException");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Test
	public void testGetDischargeDatesOk() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")){
			Collection<Date> dates=SistemaREDI.getDischargeDates(certificateInputStream, "jg@FNMT", "pkcs12","011005185924", "0111", "01105360062");
		  for (Date date : dates) {
			if(date==null) {
				fail("Not picking up some dates");
			}
		}
		} catch (SegSocialException e) {
			fail("SegSocialException");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		}
	}
	
	
	
	@Test
	public void testGetDischargeDatesWrongCCC() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")){
			Collection<Date> dates=SistemaREDI.getDischargeDates(certificateInputStream, "jg@FNMT", "pkcs12","011005185924", "0111", "01105760562");
		} catch(invalidCccException e) {
			assertTrue(true);
		} catch (SegSocialException e) {
			fail("SegSocialException");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		}
	}
	
	
	@Test
	public void testGetDischargeDatesWrongCert() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMp12")){
			Collection<Date> dates=SistemaREDI.getDischargeDates(certificateInputStream, "jg@FNMT", "pkcs12","011005185924", "0111", "01105360062");
		  fail("Should have failed");
		} catch (CertificateNotFoundException e) {
			assertTrue(true);
		} catch (SegSocialException e) {
			fail("SegSocialException");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		}
	}
	
	
	@Test
	public void testGetDischargeDatesWrongCertKey() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")){
			Collection<Date> dates=SistemaREDI.getDischargeDates(certificateInputStream, "jg@NMT", "pkcs12","011005185924", "0111", "01105360062");
		  fail("Should have failed");
		} catch (InvalidCertificateException e) {
			assertTrue(true);
		} catch (SegSocialException e) {
			fail("SegSocialException");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		}
	}
	
	@Test
	public void testGetDischargeDatesWrongCertType() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")){
			Collection<Date> dates=SistemaREDI.getDischargeDates(certificateInputStream, "jg@FNMT", "pk5s12","011005185924", "0111", "01105360062");
		  fail("Should have failed");
		} catch (InvalidCertificateException e) {
			assertTrue(true);
		} catch (SegSocialException e) {
			fail("SegSocialException");
		} catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		} catch (IOException e1) {
			fail("Error with the certificate input");
		}
	}
	
	@Test
	public void testContributionSettlementReportOk() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			  Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-11-2020");
			  Optional<Date> opDate=Optional.of(d);
			  byte[] pdf=SistemaREDI.getContributionSettlementReport(certificateInputStream, "jg@FNMT", "pkcs12", "011017250195", "0111", "01105577910", opDate);
			  if(!(pdf.length>0)) {
				  fail("Didn't return a pdf");
			  }
		}  catch (FailingHttpStatusCodeException e) {
			  assertTrue(true);
		} catch (SegSocialException e) {
			fail(e.getMessage());
		} catch (ParseException e) {
			fail("Wrongly written date");
		} catch (IOException e1) {
			fail("IO exception");
		}
	}
	
	@Test
	public void testContributionSettlementReportOkNoDate() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			  //Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-11-2020");
			  Optional<Date> opDate=Optional.empty();
			  byte[] pdf=SistemaREDI.getContributionSettlementReport(certificateInputStream, "jg@FNMT", "pkcs12", "011017250195", "0111", "01105577910", opDate);
			  if(!(pdf.length>0)) {
				  fail("Didn't return a pdf");
			  }
		} catch (SegSocialException e) {
			fail(e.getMessage());
		} catch (IOException e1) {
			fail("IO exception");
		}
	}
	
	@Test
	public void testContributionSettlementReportWrongCCC() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			  Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-11-2020");
			  Optional<Date> opDate=Optional.of(d);
			  byte[] pdf=SistemaREDI.getContributionSettlementReport(certificateInputStream, "jg@FNMT", "pkcs12", "011017250195", "0111", "01100477910", opDate);
				  fail("Shouldn't run");
		} catch (invalidCccException e) {
			
		} catch (SegSocialException e) {
			fail(e.getMessage());
		} catch (ParseException e) {
			fail("Wrongly written date");
		} catch (IOException e1) {
			fail("IO exception");
		}
	}
	
	@Test
	public void testContributionSettlementReportWrongDate() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			  Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-11-2001");
			  Optional<Date> opDate=Optional.of(d);
			  byte[] pdf=SistemaREDI.getContributionSettlementReport(certificateInputStream, "jg@FNMT", "pkcs12", "011017250195", "0111", "01105577910", opDate);
				  fail("Shouldn't run");
		} catch (DataDoesNotExist e) {
			
		} catch (SegSocialException e) {
			fail(e.getMessage());
		} catch (ParseException e) {
			fail("Wrongly written date");
		} catch (IOException e1) {
			fail("IO exception");
		}
	}
	
	
	@Test
	public void testContributionSettlementReportNoNAF() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			  Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-11-2020");
			  Optional<Date> opDate=Optional.of(d);
			  byte[] pdf=SistemaREDI.getContributionSettlementReport(certificateInputStream, "jg@FNMT", "pkcs12", "", "0111", "01105577910", opDate);
				  fail("Shouldn't run");
		} catch (UnfilledMandatory e) {
			
		} catch (SegSocialException e) {
			fail(e.getMessage());
		} catch (ParseException e) {
			fail("Wrongly written date");
		} catch (IOException e1) {
			fail("IO exception");
		}
	}
	
	@Test
	public void testContributionSettlementReportNoCCC() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			  Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-11-2020");
			  Optional<Date> opDate=Optional.of(d);
			  byte[] pdf=SistemaREDI.getContributionSettlementReport(certificateInputStream, "jg@FNMT", "pkcs12", "011017250195", "0111", "", opDate);
				  fail("Shouldn't run");
		} catch (UnfilledMandatory e) {
			
		} catch (SegSocialException e) {
			fail(e.getMessage());
		} catch (ParseException e) {
			fail("Wrongly written date");
		} catch (IOException e1) {
			fail("IO exception");
		}
	}
	
	@Test
	public void testContributionSettlementReportWrongCert() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FN12")) {
			  Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-11-2020");
			  Optional<Date> opDate=Optional.of(d);
			  byte[] pdf=SistemaREDI.getContributionSettlementReport(certificateInputStream, "jg@FNMT", "pkcs12", "011017250195", "0111", "01105577910", opDate);
				  fail("Shouldn't run");
		} catch (CertificateNotFoundException e) {
			
		} catch (SegSocialException e) {
			fail(e.getMessage());
		} catch (ParseException e) {
			fail("Wrongly written date");
		} catch (IOException e1) {
			fail("IO exception");
		}
	}
	
	@Test
	public void testContributionSettlementReportWrongCertKey() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			  Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-11-2020");
			  Optional<Date> opDate=Optional.of(d);
			  byte[] pdf=SistemaREDI.getContributionSettlementReport(certificateInputStream, "jg@FNT", "pkcs12", "011017250195", "0111", "01105577910", opDate);
				  fail("Shouldn't run");
		} catch (InvalidCertificateException e) {
			
		} catch (SegSocialException e) {
			fail(e.getMessage());
		} catch (ParseException e) {
			fail("Wrongly written date");
		} catch (IOException e1) {
			fail("IO exception");
		}
	}
	
	@Test
	public void testContributionSettlementReportWrongCertType() {
		try (final InputStream certificateInputStream = TestSistemaREDI.class.getResourceAsStream("FNMT.p12")) {
			  Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-11-2020");
			  Optional<Date> opDate=Optional.of(d);
			  byte[] pdf=SistemaREDI.getContributionSettlementReport(certificateInputStream, "jg@FNMT", "kcs12", "011017250195", "0111", "01105577910", opDate);
				  fail("Shouldn't run");
		} catch (InvalidCertificateException e) {
			
		} catch (SegSocialException e) {
			fail(e.getMessage());
		} catch (ParseException e) {
			fail("Wrongly written date");
		} catch (IOException e1) {
			fail("IO exception");
		}
	}
	

}