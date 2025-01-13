package net.aonsolutions.occam.impl.handler;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.text.MessageFormat;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.watson.util.AonChronometer;

import net.aonsolutions.occam.api.model.AccountingInvoice;
import net.aonsolutions.occam.api.model.AonRandom;
import net.aonsolutions.occam.api.model.Invoice;
import net.aonsolutions.occam.api.model.util.InvoiceTextPrinter;
import net.aonsolutions.occam.impl.AbstractOccamImplTest;
import net.aonsolutions.occam.impl.handler.InvoiceFaker.InvoiceFakerTypes;

class InvoiceRecorderHandlerTest extends AbstractOccamImplTest {

	@Test
	void SALES_NATIONALTest() {
		test( InvoiceFakerTypes.SALES_NATIONAL );
	}
	@Test
	void SALES_NATIONAL_SURCHARGETest() {
		test( InvoiceFakerTypes.SALES_NATIONAL_SURCHARGE );
	}
	@Test
	void SALES_NATIONAL_ACCRUAL_PAYMENTTest() {
		test( InvoiceFakerTypes.SALES_NATIONAL_ACCRUAL_PAYMENT );
	}
	@Test
	void SALES_CAN_CEU_MELTest() {
		test( InvoiceFakerTypes.SALES_CAN_CEU_MEL );
	}
	@Test
	void SALES_CAN_CEU_MEL_SERVICETest() {
		test( InvoiceFakerTypes.SALES_CAN_CEU_MEL_SERVICE );
	}
	@Test
	void PURCHASE_NATIONALTest() {
		test( InvoiceFakerTypes.PURCHASE_NATIONAL );
	}
	@Test
	void PURCHASE_NATIONAL_SERVICETest() {
		test( InvoiceFakerTypes.PURCHASE_NATIONAL_SERVICE );
	}
	@Test
	void PURCHASE_NATIONAL_RETENTION_SERVICETest() {
		test( InvoiceFakerTypes.PURCHASE_NATIONAL_RETENTION_SERVICE );
	}
	@Test
	void PURCHASE_NATIONAL_ACCRUAL_PAYMENTTest() {
		test( InvoiceFakerTypes.PURCHASE_NATIONAL_ACCRUAL_PAYMENT );
	}
	@Test
	void PURCHASE_INTRACOMMUNITYTest() {
		test( InvoiceFakerTypes.PURCHASE_INTRACOMMUNITY );
	}
	
	private void test(InvoiceFakerTypes invoiceFakerType) {
		AonChronometer ch = new AonChronometer();
		ch.start();
		int times = AonRandom.integer(0, 100);
		IntStream.range(0, times)
			.forEach(i -> {
				Invoice inv = invoiceFakerType.build(ctx, DOMAIN_ID);
				AccountingInvoice ai = InvoiceRecorderHandler.recordInvoice(ctx, DOMAIN_ID, inv);
				assertNotNull( ai  ,"Apunte no generado" );
				assertNotNull( ai.getAccountEntry()  ,"Apunte no generado" );
				try {
				
					assertTrue( ai.getAccountEntry().isSettled(), "Apunte descuadrado" );
				} catch (Throwable e) {
					System.out.println();
					System.out.println( "------------------------------------------------------- [INVOICE] --");
					System.out.println();
					InvoiceTextPrinter.print(inv);
					System.out.println();
					System.out.println( "---------------------------------------------------[ACCOUNT ENTRY] -");
					System.out.println();
					AccountEntryPrinter.print(System.out, ai.getAccountEntry());
					fail( e.getMessage() );
				} 
			}
		);
		ch.stop();
		
		System.out.println(MessageFormat.format(" {0} Apuntes generados en {1} segundos de tipo \"{2}\" ."
				,times
				,ch.getSeconds()
				,invoiceFakerType.name()
			));
	}

}
