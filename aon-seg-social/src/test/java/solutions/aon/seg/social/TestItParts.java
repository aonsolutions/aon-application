package solutions.aon.seg.social;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.Optional;

import org.junit.Ignore;
import org.junit.Test;

import solutions.aon.seg.social.SistemaREDITParts;
import solutions.aon.seg.social.exception.InvalidCertificateException;
import solutions.aon.seg.social.exception.SegSocialException;
import solutions.aon.seg.social.exception.StatusCodeException;
import solutions.aon.seg.social.exception.invalid.InvalidDataException;
import solutions.aon.seg.social.exception.invalid.InvalidDateException;
import solutions.aon.seg.social.object.It;
import solutions.aon.seg.social.toolkit.Toolkit;

public class TestItParts {
	
	Date unreachableDate = Toolkit.getUnreachableDate();

	@Test
	@Ignore
	public void testGetItsCertificateTest() {
		
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {			
			Collection<It> its = SistemaREDITParts.getIts(certificateInputStream, 
					"jg@FNMT", "pkcs12", "0111","01105360062", 
					Toolkit.parseDate("01-01-2015", "dd-MM-yyyy"), Toolkit.parseDate("01-01-2020", "dd-MM-yyyy"),
					Optional.empty()
			);
			for (It it : its) {
				System.out.println("BAJA >> "+it.getStart());
				System.out.println("ALTA >> "+it.getEnd());
				System.out.println("CONFIRMACION >> "+it.getConfirmations().toString());
			}
		}
//		catch (StatusCodeException | InvalidCertificateException e) {} catch (SegSocialException e) {fail("unexpected SegSocialException" + e);}
//		catch (FileNotFoundException e) {fail("File not found");}
//		catch (IOException e) {fail("IOException");}
		catch (Exception e) {e.printStackTrace();}

	}
	
	@Test
	@Ignore
	public void testGetItsUnreachableDateTest() {
		
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {			
			SistemaREDITParts.getIts(certificateInputStream, "jg@FNMT", "pkcs12", "0111","01105360062", Toolkit.parseDate("1-1-2015", "dd-MM-yyyy"), unreachableDate, Optional.empty());
		}
		catch (InvalidDateException | StatusCodeException e) {} catch (InvalidCertificateException e) {fail("unexpected certificate exception");}
		catch (SegSocialException e) {fail("unexpected SegSocialException" + e);}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
	}
	
	@Test
	@Ignore
	public void testGetItsEmptyDateTest() {
		
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {			
			SistemaREDITParts.getIts(certificateInputStream, "jg@FNMT", "pkcs12", "0111","01105360062", Toolkit.parseDate("1-1-2015", "dd-MM-yyyy"), null,  Optional.empty());
		}
		catch (InvalidDataException | StatusCodeException e) {} 
		catch (InvalidCertificateException e) {fail("unexpected certificate exception");}
		catch (SegSocialException e) {fail("unexpected SegSocialException" + e);}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
	}

	@Test
	@Ignore
	public void testGetItsRegimeTest() {
		
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {			
			SistemaREDITParts.getIts(certificateInputStream, "jg@FNMT", "pkcs12", "0211","01105360062", Toolkit.parseDate("1-1-2015", "dd-MM-yyyy"), new Date(), Optional.empty());
		}
		catch (StatusCodeException | InvalidDataException e) {} catch (InvalidCertificateException e) {fail("unexpected certificate exception");}
		catch (SegSocialException e) {fail("unexpected SegSocialException" + e);}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
	}
	
	@Test
	@Ignore
	public void testGetItsRegimeEmptyTest() {
		
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {			
			SistemaREDITParts.getIts(certificateInputStream, "jg@FNMT", "pkcs12", null,"01105360062", Toolkit.parseDate("1-12-2015", "dd-MM-yyyy"), new Date(), Optional.empty());
		}
		catch (StatusCodeException | InvalidDataException e) {} catch (InvalidCertificateException e) {fail("unexpected certificate exception");}
		catch (SegSocialException e) {fail("unexpected SegSocialException" + e);}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
	}
	
	@Test
	@Ignore
	public void testGetItsCccTest() {
		
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {			
			SistemaREDITParts.getIts(certificateInputStream, "jg@FNMT", "pkcs12", "0111","0X105360062", Toolkit.parseDate("1-1-2015", "dd-MM-yyyy"), new Date(), Optional.empty());
		}
		catch (StatusCodeException | InvalidDataException e) {} catch (InvalidCertificateException e) {fail("unexpected certificate exception");}
		catch (SegSocialException e) {fail("unexpected SegSocialException" + e);}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
	}	
	
}
