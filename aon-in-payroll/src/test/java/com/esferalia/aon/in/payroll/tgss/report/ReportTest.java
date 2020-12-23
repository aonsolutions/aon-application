package com.esferalia.aon.in.payroll.tgss.report;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;
import com.esferalia.aon.in.payroll.tgss.report.Employee.EmployeeBuilder;

public class ReportTest {

//	@Test
//	public void test() {
//		byte[] pdf = new byte[1];
//		try {pdf = SistemaRED.getCccLaboralLife(ReportTest.class.getResourceAsStream("FNMT.p12"),"jg@FNMT","pkcs12","0111","01105360062", Toolkit.parseDate("01-01-2015","dd-MM-yyyy"),Toolkit.parseDate("01-01-2020","dd-MM-yyyy")); }
//		catch 	(InvalidCertificateException e) {fail("Invalid certificate");}
//		catch	(InvalidDataException e)		{fail("Invalid data");}
//		catch 	(SegSocialException e) 			{fail("Unknown seg social exception: " + e);}
//
//		Toolkit.buildFile(pdf,"PDF.pdf");
//		try ( InputStream is = new ByteArrayInputStream(pdf)) {
//			ArrayList<Employee> employees = (ArrayList<Employee>) CCCVidaLaboral.parse(is,new Employee.EmployeeBuilder());
//			if(employees.size() == 0) fail("Not employees found.");
//
//			for (Employee emp : employees) {
//				if(emp.getSituation() == null) 	fail("Empty situation");
//				if(emp.getNss() == null) 		fail("Empty nss");
//				if(emp.getIpf() == null) 		fail("Empty ipf");
//				if(emp.getName() == null) 		fail("Empty name");
//				if(emp.getCotDays() == null) 	fail("Empty cotDays");
//				if(emp.getCtaCti() == null) 	fail("Empty ccc");
//				if(emp.getAt() == null) 		fail("Empty at");
//				if(emp.getIms() == null) 		fail("Empty ims");
//				if(emp.getTotal() == null) 		fail("Empty total");
//				System.out.println(emp);
//			}
//		}
//		catch(UnknownPDFException e){fail("Unknown PDF type.");}
//		catch(IOException e){fail("PDF corrupted.");}
//	}
	
	@Test
	public void testCCCVidaLaboral() throws IOException, UnknownPDFException {
		try (InputStream is = ReportTest.class.getResourceAsStream("cccvidalaboral.pdf")){
			Collection<Employee> employees = CCCLaboralLife.parse(is, new EmployeeBuilder());
			Map<String, List<Employee> > map = 
			employees.stream()
			.collect(Collectors.toMap(e -> e.getNss(), e -> Collections.singletonList(e), ReportTest::union ));
			
			List<Employee> employee = map.get("010019805355");
			Assert.assertEquals(1, employee.size());

			employee = map.get("011000572259");
			Assert.assertEquals(1, employee.size());

			employee = map.get("011001022503");
			Assert.assertEquals(2, employee.size());

			employee = map.get("011005151164");
			Assert.assertEquals(1, employee.size());
		}
	}

	@Test
	public void testCCCVidaLaboralUltra() throws IOException, UnknownPDFException {
		try (InputStream is = ReportTest.class.getResourceAsStream("cccvidalaboral.pdf")){
			Collection<Employee> employees = CCCLaboralLife.parse(is, new EmployeeBuilder());
			Map<String, List<Employee> > map =
					employees.stream()
							.collect(Collectors.toMap(e -> e.getNss(), e -> Collections.singletonList(e), ReportTest::union ));

			List<Employee> employee = map.get("010019805355");

			employee = map.get("011001022503");
			Assert.assertEquals(2, employee.size());

			System.out.println(employee);
		}
	}

	@Test
	public void testCCCVidaLaboralI() throws IOException, UnknownPDFException {
		try (InputStream is = ReportTest.class.getResourceAsStream("cccvidalaboralI.pdf")){
			Collection<Employee> employees = CCCLaboralLife.parse(is, new EmployeeBuilder());
			int i = 0;
			for (Employee employee : employees) {
				System.out.printf( "%d-.%s\r\n" , ++i , employee.getName() );
			}
		}
	}
	
	private static <T extends Object> List<T> union ( Collection<T> c1, Collection<T> c2 ) {
		 List<T> l = new ArrayList<T>(c1); 
		 l.addAll(c2);
		 return l;
	}

}
