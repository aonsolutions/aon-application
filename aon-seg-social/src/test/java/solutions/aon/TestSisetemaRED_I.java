package solutions.aon;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;

import solutions.aon.seg.social.*;
import solutions.aon.seg.social.exceptions.SegSocialException;
import solutions.aon.seg.social.exceptions.certificate.CertificateNotFoundException;
import solutions.aon.seg.social.exceptions.certificate.InvalidCertificateException;
import solutions.aon.seg.social.exceptions.invalidData.InvalidDataException;
import solutions.aon.seg.social.exceptions.invalidData.InvalidDateException;
import solutions.aon.seg.social.exceptions.invalidData.UnfilledMandatory;
import solutions.aon.seg.social.exceptions.invalidData.WrongRegimeException;
import solutions.aon.seg.social.exceptions.invalidData.invalidCccException;
import solutions.aon.seg.social.exceptions.statusCode.StatusCodeException;
import solutions.aon.seg.social.objects.SituacionEmpresa;

import org.junit.Test;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;

public class TestSisetemaRED_I {

	@Test
	public void testSituacionEmpresaOk() {
		try (final InputStream certificateInputStream =TestPaternity.class.getResourceAsStream("FNMT.p12"))
		  { SituacionEmpresa se=SistemaRED_I.getSituacionEmpresa(certificateInputStream, "jg@FNMT",
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
		try (final InputStream certificateInputStream =TestPaternity.class.getResourceAsStream("FNMT.p12"))
		  { SituacionEmpresa se=SistemaRED_I.getSituacionEmpresa(certificateInputStream, "jg@FNMT",
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
		try (final InputStream certificateInputStream =TestPaternity.class.getResourceAsStream("FNMT.p12"))
		  { SituacionEmpresa se=SistemaRED_I.getSituacionEmpresa(certificateInputStream, "jg@FNMT",
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
		try (final InputStream certificateInputStream =TestPaternity.class.getResourceAsStream("FNM2"))
		  { SituacionEmpresa se=SistemaRED_I.getSituacionEmpresa(certificateInputStream, "jg@FNMT",
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
		try (final InputStream certificateInputStream =TestPaternity.class.getResourceAsStream("FNMT.p12"))
		  { SituacionEmpresa se=SistemaRED_I.getSituacionEmpresa(certificateInputStream, "jgfFNMT",
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
		try (final InputStream certificateInputStream =TestPaternity.class.getResourceAsStream("FNMT.p12"))
		  { SituacionEmpresa se=SistemaRED_I.getSituacionEmpresa(certificateInputStream, "jg@FNMT",
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
		try (final InputStream certificateInputStream = TestPaternity.class.getResourceAsStream("FNMT.p12")){
		  Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
		  byte[] pdf=SistemaRED_I.getTADuplicate(certificateInputStream, "jg@FNMT", "pkcs12","011005185924", "0111", "01105360062", d);
		  if(!(pdf.length>0))
			  fail("Should have returned a pdf");
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
		try (final InputStream certificateInputStream = TestPaternity.class.getResourceAsStream("FNMT.p12")){
			Calendar c=Calendar.getInstance();
			c.add(Calendar.YEAR, 1);
		  Date d=c.getTime();
		  byte[] pdf=SistemaRED_I.getTADuplicate(certificateInputStream, "jg@FNMT", "pkcs12","011005185924", "0111", "01105360062", d);
		} catch(InvalidDataException e) {
			assertTrue(true);
		} catch (SegSocialException e) {
			fail("SegSocialException");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		}
	}
	
	
	@Test
	public void testGetTADuplicateWrongCCC() {
		try (final InputStream certificateInputStream = TestPaternity.class.getResourceAsStream("FNMT.p12")){
			Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
		  byte[] pdf=SistemaRED_I.getTADuplicate(certificateInputStream, "jg@FNMT", "pkcs12","011005185924", "0111", "01105760562", d);
		} catch(invalidCccException e) {
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
		try (final InputStream certificateInputStream = TestPaternity.class.getResourceAsStream("FNMp12")){
		  Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
		  byte[] pdf=SistemaRED_I.getTADuplicate(certificateInputStream, "jg@FNMT", "pkcs12","011005185924", "0111", "01105360062", d);
		  fail("Should have failed");
		} catch (CertificateNotFoundException e) {
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
		try (final InputStream certificateInputStream = TestPaternity.class.getResourceAsStream("FNMT.p12")){
		  Date d=new SimpleDateFormat("dd-MM-yyyy").parse("01-08-2020");
		  byte[] pdf=SistemaRED_I.getTADuplicate(certificateInputStream, "jg@NMT", "pkcs12","011005185924", "0111", "01105360062", d);
		  fail("Should have failed");
		} catch (InvalidCertificateException e) {
			assertTrue(true);
		} catch (SegSocialException e) {
			fail("SegSocialException");
		} catch (ParseException e) {
			fail("Wrong date given");
		} catch (IOException e1) {
			fail("Error with the certificate input");
		}
	}
	
	
	

}
