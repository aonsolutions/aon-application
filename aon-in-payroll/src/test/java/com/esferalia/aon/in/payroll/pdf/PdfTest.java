package com.esferalia.aon.in.payroll.pdf;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.esferalia.aon.payroll.SalaryBuilder;

public class PdfTest {

	@Test
	public void testA3() throws IOException, UnknownPDFException {
		try ( InputStream is = PdfTest.class.getResourceAsStream("a3.pdf") ){
			SalaryPDFParser.parse(is, new SalaryBuilder() {
				@Override
				public void setEmployeeName(String employeeName) {
					System.out.println(employeeName);
				}
			});
		}
	}

	@Test
	public void testAltai() throws IOException, UnknownPDFException {
		try ( InputStream is = PdfTest.class.getResourceAsStream("altai.pdf") ){
			SalaryPDFParser.parse(is, new SalaryBuilder() {
				@Override
				public void setEmployeeName(String employeeName) {
					System.out.println(employeeName);
				}
			});
		}
	}
	
	
}
