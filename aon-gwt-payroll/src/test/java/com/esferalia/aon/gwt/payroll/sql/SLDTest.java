package com.esferalia.aon.gwt.payroll.sql;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import org.junit.Ignore;
import org.junit.Test;

import com.esferalia.aon.gwt.payroll.util.SLD;
import com.esferalia.aon.in.payroll.pdf.jooq.JooqPDFSalaryBuilder;
import com.esferalia.aon.occam.api.AONContext;

import solutions.aon.seg.social.SistemaRED_I.Regime;
import solutions.aon.seg.social.exceptions.SegSocialException;
@Ignore
public class SLDTest {

	@Test
	public void testGetSLDCosts() throws FileNotFoundException, SegSocialException, SQLException {
//		try (Connection connection = DriverManager
//				.getConnection("jdbc:mysql://172.17.0.2:3306/ayudat-aonsolutions-net", "root", "root");
//		AONContext aonContext = new AONContext(connection)) {
//			JooqPDFSalaryBuilder builder = new JooqPDFSalaryBuilder(aonContext.getDslContext(),
//					"ayudat.aonsolutions.net");
//			Calendar c = Calendar.getInstance();
//			c.set(Calendar.YEAR, 2020);
//			c.set(Calendar.MONTH, 11);
//			c.set(Calendar.DATE, 1);
//			Date dateFrom = c.getTime();
//			c.set(Calendar.DATE, 31);
//			Date dateTo = c.getTime();
//			SLD.getSLDCosts(builder
//					, aonContext.getDslContext()
//					, new FileInputStream("/home/igonzalez/eclipse-workspace/aon.parent/aon-seg-social/src/test/resources/solutions/aon/FNMT.p12")
//					, "jg@FNMT"
//					, "pkcs12"
//					, "01105577910"
//					, Regime.GENERAL
//					, dateFrom
//					, dateTo);
//		}
				
		try (Connection connection = DriverManager
				.getConnection("jdbc:mysql://172.17.0.2:3306/ayudat-aonsolutions-net", "root", "root");
		AONContext aonContext = new AONContext(connection)) {
			JooqPDFSalaryBuilder builder = new JooqPDFSalaryBuilder(aonContext.getDslContext(),
					"ayudat.aonsolutions.net");
			Calendar c = Calendar.getInstance();
			c.set(Calendar.YEAR, 2020);
			c.set(Calendar.MONTH, 9);
			c.set(Calendar.DATE, 1);
			Date dateFrom = c.getTime();
			c.set(Calendar.DATE, 31);
			Date dateTo = c.getTime();
			SLD.getSLDCostsByNAFs(builder
					, aonContext.getDslContext()
					, new FileInputStream("/home/igonzalez/eclipse-workspace/aon.parent/aon-seg-social/src/test/resources/solutions/aon/AyudaTFNMT.p12")
					, "123456"
					, "pkcs12"
					, "11122534302"
					, Regime.GENERAL
					, dateFrom
					, dateTo
					, "111016467058");
			builder.execute();
		}
	}
	
	@Ignore
	@Test
	public void testGetSLDCostsByNAFS() throws FileNotFoundException, SegSocialException, SQLException {
		try (Connection connection = DriverManager
				.getConnection("jdbc:mysql://172.17.0.2:3306/ayudat-aonsolutions-net", "root", "root");
		AONContext aonContext = new AONContext(connection)) {
			JooqPDFSalaryBuilder builder = new JooqPDFSalaryBuilder(aonContext.getDslContext(),
					"ayudat.aonsolutions.net");
			Calendar c = Calendar.getInstance();
			c.set(Calendar.YEAR, 2020);
			c.set(Calendar.MONTH, 9);
			c.set(Calendar.DATE, 1);
			Date dateFrom = c.getTime();
			c.set(Calendar.DATE, 31);
			Date dateTo = c.getTime();
			SLD.getSLDCostsByNAFs(builder
					, aonContext.getDslContext()
					, new FileInputStream("/home/igonzalez/eclipse-workspace/aon.parent/aon-seg-social/src/test/resources/solutions/aon/AyudaTFNMT.p12")
					, "123456"
					, "pkcs12"
					, "11122534302"
					, Regime.GENERAL
					, dateFrom
					, dateTo
					, "111016467058");
		}
	}

}
