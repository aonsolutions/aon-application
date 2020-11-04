package solutions.aon;
import static org.junit.Assert.*;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import solutions.aon.seg.social.SistemaRedEmployee;
import solutions.aon.seg.social.exceptions.ForbiddenException;
import solutions.aon.seg.social.exceptions.InvalidCertificateException;
import solutions.aon.seg.social.exceptions.SegSocialException;
import solutions.aon.seg.social.exceptions.SyntaxException;
import solutions.aon.seg.social.exceptions.WrongRegimeException;
import solutions.aon.seg.social.exceptions.invalidCccException;

public class TestEmployee {

	@Test
	public void getEmployeesCertificateTest() {
		
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {
			SistemaRedEmployee.getEmployees(certificateInputStream,"jg@FNT","pkcs12","0111","01105360062");					
		}
		catch (InvalidCertificateException e) {}
		catch (ForbiddenException e) {}
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
		catch (ForbiddenException e) {}
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
		catch (ForbiddenException e) {}
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
		catch (ForbiddenException e) {}
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
		catch (ForbiddenException e) {}
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
		catch (ForbiddenException e) {}
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
		catch (ForbiddenException e) {}
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
		catch (ForbiddenException e) {}
		catch (SegSocialException e) {fail("unexpected SegSocialException");}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
		catch (Exception e) {fail("unknown exception");}
	}
	
	@Test
	public void getEmployeeNssTest() {
		
		try (final InputStream certificateInputStream =TestEmployee.class.getResourceAsStream("FNMT.p12")) {
			SistemaRedEmployee.getEmployee(certificateInputStream,"jg@FNMT","pkcs12","0211","01105360062", "011z005185924");					
		}
		catch (SyntaxException e) {}
		catch (ForbiddenException e) {}
		catch (SegSocialException e) {fail("unexpected SegSocialException");}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
		catch (Exception e) {fail("unknown exception");}
	}
	
	
	
}
