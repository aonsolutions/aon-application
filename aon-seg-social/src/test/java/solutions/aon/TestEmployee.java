package solutions.aon;

import org.junit.Test;

import junit.framework.Assert;
import solutions.aon.seg.social.SistemaRED_Employee;
import solutions.aon.seg.social.exceptions.SegSocialException;
import solutions.aon.seg.social.exceptions.certificate.InvalidCertificateException;
import solutions.aon.seg.social.exceptions.invalidData.*;
import solutions.aon.seg.social.exceptions.statusCode.StatusCodeException;
import solutions.aon.seg.social.objects.Employee;
import solutions.aon.seg.social.toolkit.Toolkit;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class TestEmployee {

	//GET TOTAL EMPLOYEES
	@Test
	public void getTotalEmployeesCertificateTest() {
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {
			SistemaRED_Employee.getTotalEmployees(certificateInputStream,"jg@FNMT","pkcs12","0111","01105360062");
		}
		catch (InvalidCertificateException | StatusCodeException ignored) {}
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
		catch (InvalidCertificateException | StatusCodeException ignored) {}
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
		catch (InvalidCertificateException | StatusCodeException ignored) {}
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
		catch (WrongRegimeException | StatusCodeException ignored) {}
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
		catch (InvalidDataException | StatusCodeException ignored) {}
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
		catch (invalidCccException | StatusCodeException ignored) {}
		catch (InvalidCertificateException e) {fail("unexpected certificate exception");}
		catch (SegSocialException e) {fail("unexpected SegSocialException");}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
		catch (Exception e) {fail("unknown exception");}
	}
	
	@Test
	public void getEmployeesCcc2PagesTest() {
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMTI.p12")) {
			Collection<Employee> employees = SistemaRED_Employee.getEmployees(certificateInputStream,"123456","pkcs12","0111","11122534302");
			Map<String, Employee> employeesMap = employees.stream().collect( Collectors.toMap( e -> e.getName().orElse("-") , e -> e ));
			for (Employee employee : employees) {
				System.out.println( employee.getName().orElse("Unknown"));
			}
		}
		catch (invalidCccException | StatusCodeException ignored) {}
		catch (InvalidCertificateException e) {fail("unexpected certificate exception");}
		catch (SegSocialException e) {fail("unexpected SegSocialException");}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
		catch (Exception e) {fail(e.getMessage());}
	}

	@Test
	public void getEmployeesCccEmptyTest() {
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {
			SistemaRED_Employee.getEmployees(certificateInputStream,"jg@FNMT","pkcs12","0211",null);					
		}
		catch (InvalidDataException | StatusCodeException ignored) {}
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
			assert employees != null;
			for (Employee employee : employees) {
				if(employee == null) fail();
			}
		}
		catch (invalidCccException | StatusCodeException ignored) {}
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
		catch (InvalidCertificateException | StatusCodeException ignored) {}
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
		catch (InvalidCertificateException | StatusCodeException ignored) {}
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
		catch (WrongRegimeException | StatusCodeException ignored) {}
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
		catch (invalidCccException | StatusCodeException ignored) {}
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
		catch (SyntaxException | StatusCodeException ignored) {}
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
		catch (InvalidCertificateException | StatusCodeException ignored) {}
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
		catch (InvalidCertificateException | StatusCodeException ignored) {}
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
		catch (WrongRegimeException | StatusCodeException ignored) {}
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
		catch (invalidCccException | StatusCodeException ignored) {}
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
		catch (InvalidDateException | StatusCodeException ignored) {}
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
		catch (WrongRegimeException | StatusCodeException ignored) {}
		catch (InvalidCertificateException e) {fail("unexpected certificate exception");}
		catch (SegSocialException e) {fail("unexpected SegSocialException");}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
		catch (Exception e) {fail("unknown exception");}
		
	}

	@Test
	public void getCccLaboralLifeTest() {
		try (final InputStream certificateInputStream = TestEmployee.class.getResourceAsStream("FNMT.p12")) {
			SistemaRED_Employee.getCccLaboralLife(certificateInputStream,"jg@FNMT","pkcs12","0111","01105360062",new Date(),new Date());
		}
		catch (WrongRegimeException | StatusCodeException ignored) {}
		catch (InvalidCertificateException e) {fail("unexpected certificate exception");}
		catch (SegSocialException e) {fail("unexpected SegSocialException");}
		catch (FileNotFoundException e) {fail("File not found");}
		catch (IOException e) {fail("IOException");}
		catch (Exception e) {fail("unknown exception: " + e);}

	}
	
}
