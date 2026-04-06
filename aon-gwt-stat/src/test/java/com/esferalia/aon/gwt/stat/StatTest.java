package com.esferalia.aon.gwt.stat;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.stat.StatFilterItem;
import com.esferalia.aon.occam.api.model.stat.StatFilterItem.StatFilterType;
import com.esferalia.aon.occam.api.model.stat.StatParams;
import com.esferalia.aon.occam.api.model.type.InvoiceType;
import com.esferalia.aon.watson.server.AonDateUtils;

public class StatTest {

	private static final int DOMAIN_ID = 5;
	private static final String DOMAIN_NAME = "sig.aonsolutions.es";
	private static final String USER_NAME = "jgarcia";
	
	private static CloseableAONContext ctx;
	
	@BeforeAll
	public static void beforeClass() {
		ctx = AONContext.getAONContext( DOMAIN_NAME, DOMAIN_ID, USER_NAME );
		
	}
	
	//@Test
	public void testInvoices() throws ClassNotFoundException, SQLException, IOException {
		StatParams params = new StatParams();
		params.getFilterItems().add(
				new StatFilterItem()
					.setType(StatFilterType.INVOICE_TYPE)
					.setId(InvoiceType.EXPENSES.ordinal()));
		
		params.getFilterItems().add(
				new StatFilterItem()
					.setType(StatFilterType.PRODUCT_CATEGORY)
					.setId(10));

		params.getFilterItems().add(
				new StatFilterItem()
					.setType(StatFilterType.WORKPLACE)
					.setId(4));
		params.getFilterItems().add(
				new StatFilterItem()
					.setType(StatFilterType.WORKPLACE)
					.setId(5));
		params.getFilterItems().add(
				new StatFilterItem()
					.setType(StatFilterType.WORKPLACE)
					.setId(12));

		params.setFrom( AonDateUtils.getYearFirstDay(2016));
		params.setTo( AonDateUtils.getYearLastDay(2016));
		FileWriter writer = new FileWriter("/tmp/invoices.html");
		writer.write(AON.getInvoicesReport(DOMAIN_NAME, DOMAIN_ID, USER_NAME, params));
		writer.flush();
		writer.close();
	}
	
	@AfterAll
	public static void afterClass() {
		ctx.close();
	}

	
}
