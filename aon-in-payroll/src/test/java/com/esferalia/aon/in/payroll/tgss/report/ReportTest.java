package com.esferalia.aon.in.payroll.tgss.report;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.junit.Test;

import com.esferalia.aon.in.payroll.pdf.UnknownPDFException;

public class ReportTest {

	@Test
	public void test() throws IOException, UnknownPDFException {
		try ( InputStream is = ReportTest.class.getResourceAsStream("cccvidalaboral.pdf") ){
			CCCVidaLaboral.parse(is);
		}
	}

}
