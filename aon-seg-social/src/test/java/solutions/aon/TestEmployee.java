package solutions.aon;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import solutions.aon.seg.social.SistemaRedEmployee;
import solutions.aon.seg.social.exceptions.SegSocialException;
import solutions.aon.seg.social.exceptions.certificate.InvalidCertificateException;
import solutions.aon.seg.social.exceptions.invalidData.SyntaxException;
import solutions.aon.seg.social.exceptions.invalidData.WrongRegimeException;
import solutions.aon.seg.social.exceptions.invalidData.invalidCccException;
import solutions.aon.seg.social.exceptions.statusCode.StatusCodeException;
import solutions.aon.seg.social.toolkit.Toolkit;

public class TestEmployee {

	@Test
	public void getEmployeesCertificateTest() {
		
		
		
		
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {
			
			
			
			
			SistemaRedEmployee.getEmployees(certificateInputStream,"jg@FNT","pkcs12","0111","01105360062");					
		}
		catch (InvalidCertificateException e) {}
		catch (StatusCodeException e) {}
		catch (SegSocialException e) {fail("unexpected SegSocialException");}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
		catch (Exception e) {fail("unknown exception");}
	}

	@Test
	public void getEmployeesCertificatePasswordTest() {
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {
			SistemaRedEmployee.getEmployees(certificateInputStream,"jg@FNMT","pkcs102","0111","01105360062");					
		}
		catch (InvalidCertificateException e) {}
		catch (StatusCodeException e) {}
		catch (SegSocialException e) {fail("unexpected SegSocialException");}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
		catch (Exception e) {fail("unknown exception");}
	}
	
	@Test 
	public void getEmployeesRegimeTest() {
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {
			SistemaRedEmployee.getEmployees(certificateInputStream,"jg@FNMT","pkcs12","0211","01105360062");					
		}
		catch (WrongRegimeException e) {}
		catch (StatusCodeException e) {}
		catch (SegSocialException e) {fail("unexpected SegSocialException");}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
		catch (Exception e) {fail("unknown exception");}
	}
	
	@Test
	public void getEmployeesRegimeCcc() {
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {
			SistemaRedEmployee.getEmployees(certificateInputStream,"jg@FNMT","pkcs12","0211","011205360062");					
		}
		catch (invalidCccException e) {}
		catch (StatusCodeException e) {}
		catch (SegSocialException e) {fail("unexpected SegSocialException");}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
		catch (Exception e) {fail("unknown exception");}
	}
	
	@Test
	public void getEmployeeCertificateTest() {
		
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {
			SistemaRedEmployee.getEmployee(certificateInputStream,"jg@FNT","pkcs12","0111","01105360062", "011005185924");					
		}
		catch (InvalidCertificateException e) {}
		catch (StatusCodeException e) {}
		catch (SegSocialException e) {fail("unexpected SegSocialException");}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
		catch (Exception e) {fail("unknown exception");}
	}
	
	@Test
	public void getEmployeeCertificatePasswordTest() {
		
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {
			SistemaRedEmployee.getEmployee(certificateInputStream,"jg@FNMT","pkcsx12","0111","01105360062", "011005185924");					
		}
		catch (InvalidCertificateException e) {}
		catch (StatusCodeException e) {}
		catch (SegSocialException e) {fail("unexpected SegSocialException");}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
		catch (Exception e) {fail("unknown exception");}
	}

	@Test
	public void getEmployeeRegimeTest() {
		
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {
			SistemaRedEmployee.getEmployee(certificateInputStream,"jg@FNMT","pkcs12","0211","01105360062", "011005185924");					
		}
		catch (WrongRegimeException e) {}
		catch (StatusCodeException e) {}
		catch (SegSocialException e) {fail("unexpected SegSocialException");}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
		catch (Exception e) {fail("unknown exception");}
	}

	@Test
	public void getEmployeeCccTest() {
		
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {
			SistemaRedEmployee.getEmployee(certificateInputStream,"jg@FNMT","pkcs12","0211","01105360z062", "011005185924");					
		}
		catch (invalidCccException e) {}
		catch (StatusCodeException e) {}
		catch (SegSocialException e) {fail("unexpected SegSocialException");}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
		catch (Exception e) {fail("unknown exception");}
	}
	
	@Test
	public void getEmployeeNssTest() {
		
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {
			SistemaRedEmployee.getEmployee(certificateInputStream,"jg@FNMT","pkcs12","0211","01105360062", "011z005185924");					
		}
		catch (SyntaxException e) {}
		catch (StatusCodeException e) {}
		catch (SegSocialException e) {fail("unexpected SegSocialException");}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
		catch (Exception e) {fail("unknown exception");}
	}
	
	@Test
	public void getCccLiquidationCertificateTest() {
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {
			SistemaRedEmployee.getCccLiquidation(certificateInputStream, "g@FNMT", "pkcs12", "0111", "01105360062",Toolkit.parseDate("2-9-2020", "dd-MM-yyyy"));				
		}
		catch (InvalidCertificateException e) {}
		catch (StatusCodeException e) {}
		catch (SegSocialException e) {fail("unexpected SegSocialException");}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
		catch (Exception e) {fail("unknown exception");}
		
	}
	
	@Test
	public void getCccLiquidationCertificatePasswordTest() {
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {
			SistemaRedEmployee.getCccLiquidation(certificateInputStream, "jg@FNMT", "ppkcs12", "0111", "01105360062",Toolkit.parseDate("2-9-2020", "dd-MM-yyyy"));				
		}
		catch (InvalidCertificateException e) {}
		catch (StatusCodeException e) {}
		catch (SegSocialException e) {fail("unexpected SegSocialException");}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
		catch (Exception e) {fail("unknown exception");}
		
	}
	
	@Test
	public void getCccLiquidationRegimeTest() {
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {
			SistemaRedEmployee.getCccLiquidation(certificateInputStream, "jg@FNMT", "pkcs12", "0z111", "01105360062",Toolkit.parseDate("2-9-2020", "dd-MM-yyyy"));				
		}
		catch (WrongRegimeException e) {}
		catch (StatusCodeException e) {}
		catch (SegSocialException e) {fail("unexpected SegSocialException");}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
		catch (Exception e) {fail("unknown exception");}
		
	}
	
	@Test
	public void getCccLiquidationCccTest() {
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {
			SistemaRedEmployee.getCccLiquidation(certificateInputStream, "jg@FNMT", "pkcs12", "0111", "z01105360062",Toolkit.parseDate("2-9-2020", "dd-MM-yyyy"));				
		}
		catch (invalidCccException e) {}
		catch (StatusCodeException e) {}
		catch (SegSocialException e) {fail("unexpected SegSocialException");}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
		catch (Exception e) {fail("unknown exception");}
		
	}
	
	@Test
	public void getCccLiquidationDateTest() {
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {
			assertTrue(SistemaRedEmployee.getCccLiquidation(certificateInputStream, "jg@FNMT", "pkcs12", "0111", "01105360062",Toolkit.parseDate("2-9-200", "dd-MM-yyyy")).length!=0);				
		}
		catch (WrongRegimeException e) {}
		catch (StatusCodeException e) {}
		catch (SegSocialException e) {fail("unexpected SegSocialException");}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
		catch (Exception e) {fail("unknown exception");}
		
	}
	
}
