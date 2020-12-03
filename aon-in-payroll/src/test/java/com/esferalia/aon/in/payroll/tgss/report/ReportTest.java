package com.esferalia.aon.in.payroll.tgss.report;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;

import org.junit.Test;

import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;
import solutions.aon.seg.social.SistemaRED;
import solutions.aon.seg.social.exceptions.SegSocialException;
import solutions.aon.seg.social.exceptions.certificate.InvalidCertificateException;
import solutions.aon.seg.social.exceptions.invalidData.InvalidDataException;
import solutions.aon.seg.social.toolkit.Toolkit;

import javax.tools.Tool;

import static org.junit.Assert.fail;

public class ReportTest {

	@Test
	public void test() {
		byte[] pdf = new byte[1];
		try {pdf = SistemaRED.getCccLaboralLife(ReportTest.class.getResourceAsStream("FNMT.p12"),"jg@FNMT","pkcs12","0111","01105360062", Toolkit.parseDate("01-01-2015","dd-MM-yyyy"),Toolkit.parseDate("01-01-2020","dd-MM-yyyy")); }
		catch 	(InvalidCertificateException e) {fail("Invalid certificate");}
		catch	(InvalidDataException e)		{fail("Invalid data");}
		catch 	(SegSocialException e) 			{fail("Unknown seg social exception: " + e);}

		Toolkit.buildFile(pdf,"PDF.pdf");
		try ( InputStream is = new ByteArrayInputStream(pdf)) {
			ArrayList<Employee> employees = (ArrayList<Employee>) CCCVidaLaboral.parse(is,new Employee.EmployeeBuilder());
			if(employees.size() == 0) fail("Not employees found.");

			for (Employee emp : employees) {
				if(emp.getSituation() == null) 	fail("Empty situation");
				if(emp.getNss() == null) 		fail("Empty nss");
				if(emp.getIpf() == null) 		fail("Empty ipf");
				if(emp.getName() == null) 		fail("Empty name");
				if(emp.getCotDays() == null) 	fail("Empty cotDays");
				if(emp.getCtaCti() == null) 	fail("Empty ccc");
				if(emp.getAt() == null) 		fail("Empty at");
				if(emp.getIms() == null) 		fail("Empty ims");
				if(emp.getTotal() == null) 		fail("Empty total");
				System.out.println(emp);
			}
		}
		catch(UnknownPDFException e){fail("Unknown PDF type.");}
		catch(IOException e){fail("PDF corrupted.");}
	}

}
