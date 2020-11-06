package solutions.aon;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.junit.Test;

import solutions.aon.seg.social.SistemaREDITParts;
import solutions.aon.seg.social.exceptions.SegSocialException;
import solutions.aon.seg.social.exceptions.certificate.InvalidCertificateException;
import solutions.aon.seg.social.exceptions.invalidData.InvalidDataException;
import solutions.aon.seg.social.exceptions.invalidData.InvalidDateException;
import solutions.aon.seg.social.exceptions.statusCode.StatusCodeException;
import solutions.aon.seg.social.toolkit.Toolkit;

public class TestItParts {
	
	Date unreachableDate = Toolkit.getUnreachableDate();

	@Test
	public void testGetItsCertificateTest() {
		
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMTT.p12")) {			
			SistemaREDITParts.getIts(certificateInputStream, "jg@FNMT", "pkcs12", "0111","01105360062", Toolkit.parseDate("1-1-2015", "dd-MM-yyyy"), Toolkit.parseDate("1-1-2020", "dd-MM-yyyy"));
		}
		catch (StatusCodeException e) {}
		catch (InvalidCertificateException e) {}
		catch (SegSocialException e) {fail("unexpected SegSocialException" + e);}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
		catch (Exception e) {fail("unknown exception");}

	}
	
	@Test
	public void testGetItsUnreachableDateTest() {
		
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {			
			SistemaREDITParts.getIts(certificateInputStream, "jg@FNMT", "pkcs12", "0111","01105360062", Toolkit.parseDate("1-1-2015", "dd-MM-yyyy"), unreachableDate);
		}
		catch (InvalidDateException e) {}
		catch (StatusCodeException e) {}
		catch (InvalidCertificateException e) {fail("unexpected certificate exception");}
		catch (SegSocialException e) {fail("unexpected SegSocialException" + e);}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
	}
	
	@Test
	public void testGetItsEmptyDateTest() {
		
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {			
			SistemaREDITParts.getIts(certificateInputStream, "jg@FNMT", "pkcs12", "0111","01105360062", Toolkit.parseDate("1-1-2015", "dd-MM-yyyy"), null);
		}
		catch (InvalidDataException e) {}
		catch (StatusCodeException e) {}
		catch (InvalidCertificateException e) {fail("unexpected certificate exception");}
		catch (SegSocialException e) {fail("unexpected SegSocialException" + e);}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
	}

	@Test
	public void testGetItsRegimeTest() {
		
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {			
			SistemaREDITParts.getIts(certificateInputStream, "jg@FNMT", "pkcs12", "0211","01105360062", Toolkit.parseDate("1-1-2015", "dd-MM-yyyy"), new Date());
		}
		catch (StatusCodeException e) {}
		catch (InvalidDataException e) {}
		catch (InvalidCertificateException e) {fail("unexpected certificate exception");}
		catch (SegSocialException e) {fail("unexpected SegSocialException" + e);}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
	}
	
	@Test
	public void testGetItsRegimeEmptyTest() {
		
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {			
			SistemaREDITParts.getIts(certificateInputStream, "jg@FNMT", "pkcs12", null,"01105360062", Toolkit.parseDate("1-1-2015", "dd-MM-yyyy"), new Date());
		}
		catch (StatusCodeException e) {}
		catch (InvalidDataException e) {}
		catch (InvalidCertificateException e) {fail("unexpected certificate exception");}
		catch (SegSocialException e) {fail("unexpected SegSocialException" + e);}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
	}
	
	@Test
	public void testGetItsCccTest() {
		
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {			
			SistemaREDITParts.getIts(certificateInputStream, "jg@FNMT", "pkcs12", "0111","0X105360062", Toolkit.parseDate("1-1-2015", "dd-MM-yyyy"), new Date());
		}
		catch (StatusCodeException e) {}
		catch (InvalidDataException e) {}
		catch (InvalidCertificateException e) {fail("unexpected certificate exception");}
		catch (SegSocialException e) {fail("unexpected SegSocialException" + e);}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
	}
	
	
	
}
