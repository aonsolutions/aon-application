package com.esferalia.aon.occam.jooq.test;


import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.stream.Collectors;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import net.aonsolutions.core.pool.AonConnectionException;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.model.fiscal.VatContext;
import com.esferalia.aon.occam.api.model.fiscal.VatSummaryContext;
import com.esferalia.aon.occam.api.model.type.Period;
import com.esferalia.aon.occam.impl.jooq.dao.VATDAO;
import com.esferalia.aon.occam.impl.jooq.dao.VATFormatter;
import com.esferalia.aon.occam.server.fiscal.FiscalUtils;
import com.esferalia.aon.watson.server.AonDateUtils;


public class VatReportTest {

	private static AONContext ctx;
	private static String DOMAIN_NAME = "sig.aonsolutions.es";
	private static Integer DOMAIN_ID = 5;
	private static String LOGIN = "jgarcia";
	
	

	@BeforeClass
	public static void beforeClass() throws ClassNotFoundException, SQLException, AonConnectionException {
		Class.forName( org.gjt.mm.mysql.Driver.class.getName() );
		ctx = AONContext.getAONContext(DOMAIN_NAME, DOMAIN_ID,LOGIN);
	}
	
	@Test
	public void testBreakdown() throws IOException {
		
		final Date fromDate = AonDateUtils.getYearFirstDay(2017);
		final Date toDate = FiscalUtils.getPeriodEnd(2017, Period.T1);
		LinkedList<VatContext> list = VATDAO.getVatBreakdown(ctx, fromDate, toDate)
				.collect(Collectors.toCollection(LinkedList::new));
		FileWriter writer =  new FileWriter("/home/ecastellano/vatBreakdown.html");
		writer.write("<pre>");
		writer.write(VATFormatter.formatInvoices("LISTADO IVA", "DESGLOSE", list));
		writer.write("</pre>");
		writer.flush();
		writer.close();
		System.out.println( "FINISH BREAKDOWN");
	}
		
	@Test
	public void testSummary() throws IOException {
		
		final Date fromDate = AonDateUtils.getYearFirstDay(2017);
		final Date toDate = FiscalUtils.getPeriodEnd(2017, Period.T1);
		
		LinkedList<VatSummaryContext> list = VATDAO.getVatSummary(ctx, fromDate, toDate, null);
		FileWriter writer =  new FileWriter("/home/ecastellano/vatSummary.html");
		writer.write("<pre>");
		writer.write(VATFormatter.formatSummary("RESUMEN IVA", list));	
		writer.write("</pre>");
		writer.flush();
		writer.close();
		System.out.println( "FINISH SUMMARY");
	}

	@AfterClass
	public static void afterClass() {
		ctx.finalize();
	}
	
}
