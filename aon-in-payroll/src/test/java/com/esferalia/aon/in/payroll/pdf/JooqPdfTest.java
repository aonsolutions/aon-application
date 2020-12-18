package com.esferalia.aon.in.payroll.pdf;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.Ignore;
import org.junit.Test;

import com.esferalia.aon.in.payroll.pdf.jooq.JooqPDFSalaryBuilder;
import com.esferalia.aon.occam.api.AONContext;

public class JooqPdfTest {

	@Test
	@Ignore("This can't be commited")
	public void testA3() throws IOException, UnknownPDFException, SQLException {
		try ( InputStream is = PdfTest.class.getResourceAsStream("a3.pdf") ;
			Connection connection = DriverManager.getConnection("jdbc:mysql://172.17.0.2:3306/test-aonsolutions-org", "root", "root");
			AONContext aonContext = new AONContext(connection))
		{
			SalaryPDFParser.parse(is, new JooqPDFSalaryBuilder(aonContext.getDslContext(), "payroll-test.aonsolutions.org"));
		}
	}
	

}
