package com.esferalia.aon.in.payroll.tgss.report;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;
import com.esferalia.aon.in.payroll.tgss.report.Employee.EmployeeBuilder;

public class ReportTest {

	@Test
	public void testCCCVidaLaboral() throws IOException, UnknownPDFException {
		try (InputStream is = ReportTest.class.getResourceAsStream("cccvidalaboral.pdf")){
			Collection<Employee> employees = CCCLaboralLife.parse(is, new EmployeeBuilder());
			Map<String, List<Employee> > map = 
			employees.stream()
			.collect(Collectors.toMap(e -> e.getNss(), e -> Collections.singletonList(e), ReportTest::union ));
			
			List<Employee> employee = map.get("010019805355");
			assertEquals(1, employee.size());

			employee = map.get("011000572259");
			assertEquals(1, employee.size());

			employee = map.get("011001022503");
			assertEquals(2, employee.size());

			employee = map.get("011005151164");
			assertEquals(1, employee.size());
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
			assertEquals(2, employee.size());
		}
	}

	@Test
	@Disabled
	public void testCCCVidaLaboralI() throws IOException, UnknownPDFException {
		try (InputStream is = ReportTest.class.getResourceAsStream("cccvidalaboralI.pdf")){
			Collection<Employee> employees = CCCLaboralLife.parse(is, new EmployeeBuilder());
			int i = 0;
		}
	}
	
	private static <T extends Object> List<T> union ( Collection<T> c1, Collection<T> c2 ) {
		 List<T> l = new ArrayList<T>(c1); 
		 l.addAll(c2);
		 return l;
	}

}
