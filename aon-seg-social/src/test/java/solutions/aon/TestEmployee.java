package solutions.aon;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.junit.Test;

import solutions.aon.seg.social.SistemaRED_Employee;
import solutions.aon.seg.social.exceptions.SegSocialException;
import solutions.aon.seg.social.exceptions.certificate.InvalidCertificateException;
import solutions.aon.seg.social.exceptions.invalidData.InvalidDataException;
import solutions.aon.seg.social.exceptions.invalidData.InvalidDateException;
import solutions.aon.seg.social.exceptions.invalidData.SyntaxException;
import solutions.aon.seg.social.exceptions.invalidData.WrongRegimeException;
import solutions.aon.seg.social.exceptions.invalidData.invalidCccException;
import solutions.aon.seg.social.exceptions.statusCode.StatusCodeException;
import solutions.aon.seg.social.objects.Employee;
import solutions.aon.seg.social.toolkit.Toolkit;

public class TestEmployee {

	//GET TOTAL EMPLOYEES
	@Test
	public void getTotalEmployeesCertificateTest() throws SegSocialException, IOException {
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {
			SistemaRED_Employee.getTotalEmployees(certificateInputStream,"jg@FNMT","pkcs12","0111","01105360062");
		}
		catch (InvalidCertificateException e) {}
		catch (StatusCodeException e) {}
		catch (SegSocialException e) {fail("unexpected SegSocialException");}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
		catch (Exception e) {fail("unknown exception");}
	}
	
	//GET EMPLOYEES TESTS
	
	@Test
	public void getEmployeesCertificateTest() {
		
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {
			SistemaRED_Employee.getEmployees(certificateInputStream,"jg@FNMT","pkcs12","0111","01105360062");					
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
			SistemaRED_Employee.getEmployees(certificateInputStream,"jg@FNMT","pkcs102","0111","01105360062");					
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
			SistemaRED_Employee.getEmployees(certificateInputStream,"jg@FNMT","pkcs12","0211","01105360062");					
		}
		catch (WrongRegimeException e) {
			System.out.println(e.getMessage());
		}
		catch (StatusCodeException e) {}
		catch (SegSocialException e) {fail("unexpected SegSocialException");}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
		catch (Exception e) {fail("unknown exception");}
	}
	
	@Test 
	public void getEmployeesRegimeEmptyTest() {
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {
			SistemaRED_Employee.getEmployees(certificateInputStream,"jg@FNMT","pkcs12",null,"01105360062");					
		}
		catch (InvalidDataException e) {}
		catch (StatusCodeException e) {}
		catch (SegSocialException e) {fail("unexpected SegSocialException");}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
		catch (Exception e) {fail("unknown exception");}
	}	
	
	@Test
	public void getEmployeesCccTest() {
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {
			SistemaRED_Employee.getEmployees(certificateInputStream,"jg@FNMT","pkcs12","0111","011205360062");					
		}
		catch (invalidCccException e) {}
		catch (StatusCodeException e) {}
		catch (InvalidCertificateException e) {fail("unexpected certificate exception");}
		catch (SegSocialException e) {fail("unexpected SegSocialException");}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
		catch (Exception e) {fail("unknown exception");}
	}
	
	@Test
	public void getEmployeesCccEmptyTest() {
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {
			SistemaRED_Employee.getEmployees(certificateInputStream,"jg@FNMT","pkcs12","0211",null);					
		}
		catch (InvalidDataException e) {}
		catch (StatusCodeException e) {}
		catch (InvalidCertificateException e) {fail("unexpected certificate exception");}
		catch (SegSocialException e) {fail("unexpected SegSocialException");}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
		catch (Exception e) {fail("unknown exception");}
	}
	
	@Test 
	public void getEmployeesObjectTypeTest() {
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {
			ArrayList<Employee> employees = (ArrayList<Employee>) SistemaRED_Employee.getEmployees(certificateInputStream,"jg@FNMT","pkcs12","0211","011205360062");	
			for (Employee employee : employees) {
				if(employee == null || !(employee instanceof Employee)) assertTrue(false);
			}
		}
		catch (invalidCccException e) {}
		catch (StatusCodeException e) {}
		catch (InvalidCertificateException e) {fail("unexpected certificate exception");}
		catch (SegSocialException e) {fail("unexpected SegSocialException");}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
		catch (Exception e) {fail("unknown exception");}
	}

	//GET EMPLOYEE TESTS
	
	@Test
	public void getEmployeeCertificateTest() {
		
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {
			SistemaRED_Employee.getEmployee(certificateInputStream,"jg@FNT","pkcs12","0111","01105360062", "011005185924");					
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
			SistemaRED_Employee.getEmployee(certificateInputStream,"jg@FNMT","pkcsx12","0111","01105360062", "011005185924");					
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
			SistemaRED_Employee.getEmployee(certificateInputStream,"jg@FNMT","pkcs12","0211","01105360062", "011005185924");
		}
		catch (WrongRegimeException e) {}
		catch (StatusCodeException e) {}
		catch (InvalidCertificateException e) {fail("unexpected certificate exception");}
		catch (SegSocialException e) {fail("unexpected SegSocialException");}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
		catch (Exception e) {fail("unknown exception");}
	}

	@Test
	public void getEmployeeCccTest() {
		
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {
			SistemaRED_Employee.getEmployee(certificateInputStream,"jg@FNMT","pkcs12","0211","01105360z062", "011005185924");					
		}
		catch (invalidCccException e) {}
		catch (StatusCodeException e) {}
		catch (InvalidCertificateException e) {fail("unexpected certificate exception");}
		catch (SegSocialException e) {fail("unexpected SegSocialException");}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
		catch (Exception e) {fail("unknown exception");}
	}
	
	@Test
	public void getEmployeeNssTest() {
		
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {
			SistemaRED_Employee.getEmployee(certificateInputStream,"jg@FNMT","pkcs12","0211","01105360062", "011z005185924");					
		}
		catch (SyntaxException e) {}
		catch (StatusCodeException e) {}
		catch (InvalidCertificateException e) {fail("unexpected certificate exception");}
		catch (SegSocialException e) {fail("unexpected SegSocialException");}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
		catch (Exception e) {fail("unknown exception");}
	}
	
	//GET CCC LIQUIDATION TEST
	
	@Test
	public void getCccLiquidationCertificateTest() {
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {
			SistemaRED_Employee.getCccLiquidation(certificateInputStream, "g@FNMT", "pkcs12", "0111", "01105360062",Toolkit.parseDate("2-9-2020", "dd-MM-yyyy"));				
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
			SistemaRED_Employee.getCccLiquidation(certificateInputStream, "jg@FNMT", "ppkcs12", "0111", "01105360062",Toolkit.parseDate("2-9-2020", "dd-MM-yyyy"));				
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
			SistemaRED_Employee.getCccLiquidation(certificateInputStream, "jg@FNMT", "pkcs12", "0z111", "01105360062",Toolkit.parseDate("2-9-2020", "dd-MM-yyyy"));				
		}
		catch (WrongRegimeException e) {}
		catch (StatusCodeException e) {}
		catch (InvalidCertificateException e) {fail("unexpected certificate exception");}
		catch (SegSocialException e) {fail("unexpected SegSocialException");}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
		catch (Exception e) {fail("unknown exception");}
		
	}
	
	@Test
	public void getCccLiquidationCccTest() {
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {
			SistemaRED_Employee.getCccLiquidation(certificateInputStream, "jg@FNMT", "pkcs12", "0111", "z01105360062",Toolkit.parseDate("2-9-2020", "dd-MM-yyyy"));				
		}
		catch (invalidCccException e) {}
		catch (StatusCodeException e) {}
		catch (InvalidCertificateException e) {fail("unexpected certificate exception");}
		catch (SegSocialException e) {fail("unexpected SegSocialException");}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
		catch (Exception e) {fail("unknown exception");}
		
	}
	
	@Test
	public void getCccLiquidationDateTest() {
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {
			SistemaRED_Employee.getCccLiquidation(certificateInputStream, "jg@FNMT", "pkcs12", "0111", "01105360062",Toolkit.parseDate("2-9-200", "dd-MM-yyyy"));				
		}
		catch (InvalidDateException e) {}
		catch (StatusCodeException e) {}
		catch (InvalidCertificateException e) {fail("unexpected certificate exception");}
		catch (SegSocialException e) {fail("unexpected SegSocialException");}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
		catch (Exception e) {fail("unknown exception");}
		
	}
	
	@Test
	public void getCccLiquidationPdfTest() {
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {
			assertTrue(SistemaRED_Employee.getCccLiquidation(certificateInputStream, "jg@FNMT", "pkcs12", "0111", "01105360062",Toolkit.parseDate("2-9-200", "dd-MM-yyyy")).length!=0);				
		}
		catch (WrongRegimeException e) {}
		catch (StatusCodeException e) {}
		catch (InvalidCertificateException e) {fail("unexpected certificate exception");}
		catch (SegSocialException e) {fail("unexpected SegSocialException");}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
		catch (Exception e) {fail("unknown exception");}
		
	}

	
}
