package com.esferalia.aon.occam.test.finance.invoice.fee;

import static com.esferalia.aon.jooq.tables.Invoice.INVOICE;
import static com.esferalia.aon.jooq.tables.InvoiceDetail.INVOICE_DETAIL;
import static com.esferalia.aon.jooq.tables.InvoiceTax.INVOICE_TAX;
import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.jooq.Record;
import org.junit.Ignore;
import org.junit.Test;

import com.esferalia.aon.occam.api.AONContext;
import com.esferalia.aon.occam.api.AONContext.CloseableAONContext;
import com.esferalia.aon.occam.api.model.Occam;
import com.esferalia.aon.occam.api.model.finance.FeeBillingParams;
import com.esferalia.aon.occam.api.model.finance.InvoiceToString;
import com.esferalia.aon.occam.api.model.type.Month;
import com.esferalia.aon.occam.impl.jooq.dao.invoice.fee.FeeBillingDAO;
import com.esferalia.aon.watson.error.AonCoreException;
import com.esferalia.aon.watson.mutable.MutableInt;
import com.esferalia.aon.watson.server.AonDateUtils;
import com.esferalia.aon.watson.util.AonChronometer;
import com.esferalia.aon.watson.util.AonCollectionUtils;
import com.esferalia.aon.watson.util.AonStringUtils;

public class FeeBillingDAOTests {

	private final static String DOMAIN_NAME = "b72384936-ayudat.ecastellano.euk";
	private final static Integer DOMAIN_ID = 7138;
	private final static String DOMAIN_USER = "aon";

//	private final static String DOMAIN_NAME = "macayc-mac.ecastellano.pro";
//	private final static Integer DOMAIN_ID = 536;
//	private final static String DOMAIN_USER = "mac";
	
	private final static Occam OCCAM = new Occam()
		.setDomainName( DOMAIN_NAME )
		.setDomain( DOMAIN_ID )
		.setUser( DOMAIN_USER )
	;
	
	@Test
	public void test() {
		try ( CloseableAONContext ctx =  AONContext.getAONContext( OCCAM )) {
			// ******
			AonChronometer cr = new AonChronometer();
			cr.start();
			// ******
			
			FeeBillingParams params = new FeeBillingParams()
				.setDomainId( DOMAIN_ID )
				.setYear( 2025 )
				.setMonth( Month.NOVEMBER )
				// .setCustomer( 115848 )
				.setInvoiceActivity(4717)
				.setInvoiceSeries("NEW25")
				.setInvoiceNumber( 0 )
				.setInvoiceDate( AonDateUtils.getMonthLastDay( new Date() ) )
				.setConfidential( false )
				.setDryRun( false )
			;
			
			MutableInt fullInvoices = new MutableInt(1);
			ctx.transaction(trx -> {
				FeeBillingDAO.invoice(ctx, params)
					.forEach( i -> {
						fullInvoices.increment();
						System.out.println( InvoiceToString.print( i ).toString() );
					}
				);
			});
			// ******
			// ******
			// ******
			cr.stop();
			System.out.println();
			System.out.println( cr.getSeconds() + " segundos al buscar, ordenar y generar " + ( fullInvoices.getValue() - 1 ) + " facturas" );
			System.out.println();
			// ******
			// ******
			// ******
		}
	}


	@Test
	@Ignore
	public void testAsserts() {
		try ( CloseableAONContext ctx =  AONContext.getAONContext( OCCAM )) {
			String facesSeries = "OCT25";
			String occamSeries = "NEW25";
			ctx.getDslContext()
				.select()
				.from( INVOICE )
				.where( INVOICE.DOMAIN.eq(DOMAIN_ID))
				.and(INVOICE.SERIES.eq(facesSeries))
				.fetch()
				.stream()
				.forEach(r1 -> {
					int number = r1.getValue(INVOICE.NUMBER);
					Record r2 = ctx.getDslContext()
						.select()
						.from( INVOICE )
						.where( INVOICE.DOMAIN.eq(DOMAIN_ID))
						.and(INVOICE.SERIES.eq(occamSeries))
						.and(INVOICE.NUMBER.eq(number))
						.fetch()
						.stream()
						.findFirst()
						.orElseThrow(() -> new AonCoreException("Factura NEW25/"+number+" no encontrada."));
					checkInvoice(ctx, r1,r2);
				});
		}
	}

	private void checkInvoice(AONContext ctx, Record r1, Record r2) {
		System.out.println( "Checking INVOICE: " + r1.getValue(INVOICE.REFERENCE_CODE));
		AonCollectionUtils.stream(INVOICE.fields())
			.filter(f -> f != INVOICE.ID)
			.filter(f -> f != INVOICE.SERIES)
			.filter(f -> f != INVOICE.REFERENCE_CODE)
			.filter(f -> f != INVOICE.COMMENTS)
			.filter(f -> f != INVOICE.CREATION_DATE)
			.filter(f -> f != INVOICE.MODIFICATION_DATE)
			.peek( f -> System.out.println( "\tChecking " + AonStringUtils.upperCase(f.getName()) ))
			.forEach(f -> assertEquals("Field " + AonStringUtils.upperCase(f.getName()), r1.getValue(f), r2.getValue(f)));
		checkInvoiceDetail(ctx, r1.getValue(INVOICE.ID),r2.getValue(INVOICE.ID));
	}

	private void checkInvoiceDetail(AONContext ctx, Integer id1, Integer id2) {
		ctx.getDslContext()
		.select()
		.from( INVOICE_DETAIL )
		.where( INVOICE_DETAIL.DOMAIN.eq(DOMAIN_ID))
		.and(INVOICE_DETAIL.INVOICE.eq(id1))
		.fetch()
		.stream()
		.forEach(r1 -> {
			short line = r1.getValue(INVOICE_DETAIL.LINE);
			Record r2 = ctx.getDslContext()
				.select()
				.from( INVOICE_DETAIL )
				.where( INVOICE_DETAIL.DOMAIN.eq(DOMAIN_ID))
				.and(INVOICE_DETAIL.INVOICE.eq(id2))
				.and(INVOICE_DETAIL.LINE.eq(line))
				.fetch()
				.stream()
				.findFirst()
				.orElseThrow(() -> new AonCoreException("Linea "+line+" no encontrada."));
			checkInvoiceDetail(ctx, r1,r2);
		});
	}


	private void checkInvoiceDetail(AONContext ctx, Record r1, Record r2) {
		System.out.println( "\tChecking INVOICE DETAIL: línea: " + r1.getValue(INVOICE_DETAIL.LINE));
		AonCollectionUtils.stream(INVOICE_DETAIL.fields())
			.filter(f -> f != INVOICE_DETAIL.ID)
			.filter(f -> f != INVOICE_DETAIL.INVOICE)
			.filter(f -> f != INVOICE_DETAIL.CREATION_DATE)
			.filter(f -> f != INVOICE_DETAIL.MODIFICATION_DATE)
			.filter(f -> f != INVOICE_DETAIL.MODIFICATION_USER)
			.peek( f -> System.out.println( "\t\tChecking " + AonStringUtils.upperCase(f.getName()) ))
			.forEach(f -> assertEquals("Field " + AonStringUtils.upperCase(f.getName()), r1.getValue(f), r2.getValue(f)));
		checkInvoiceTax(ctx, r1.getValue(INVOICE_DETAIL.ID),r2.getValue(INVOICE_DETAIL.ID));
	}

	private void checkInvoiceTax(AONContext ctx, Integer id1, Integer id2) {
		ctx.getDslContext()
		.select()
		.from( INVOICE_TAX )
		.where( INVOICE_TAX.DOMAIN.eq(DOMAIN_ID))
		.and(INVOICE_TAX.INVOICE_DETAIL.eq(id1))
		.fetch()
		.stream()
		.forEach(r1 -> {
			byte line = r1.getValue(INVOICE_TAX.TAX_TYPE );
			Record r2 = ctx.getDslContext()
				.select()
				.from( INVOICE_TAX )
				.where( INVOICE_TAX.DOMAIN.eq(DOMAIN_ID))
				.and(INVOICE_TAX.INVOICE_DETAIL.eq(id2))
				.and(INVOICE_TAX.TAX_TYPE.eq(line))
				.fetch()
				.stream()
				.findFirst()
				.orElseThrow(() -> new AonCoreException("Tax "+line+" no encontrada."));
			checkInvoiceTax(ctx, r1,r2);
		});
	}


	private void checkInvoiceTax(AONContext ctx, Record r1, Record r2) {
		System.out.println( "\t\tChecking INVOICE TAX: tax: " + r1.getValue(INVOICE_TAX.TAX_TYPE));
		AonCollectionUtils.stream(INVOICE_TAX.fields())
			.filter(f -> f != INVOICE_TAX.ID)
			.filter(f -> f != INVOICE_TAX.INVOICE_DETAIL)
			.peek( f -> System.out.println( "\t\t\tChecking " + AonStringUtils.upperCase(f.getName()) ))
			.forEach(f -> assertEquals("Field " + AonStringUtils.upperCase(f.getName()), r1.getValue(f), r2.getValue(f)));
	}
}
