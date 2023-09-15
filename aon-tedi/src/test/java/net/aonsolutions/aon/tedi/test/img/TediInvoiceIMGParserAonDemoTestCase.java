package net.aonsolutions.aon.tedi.test.img;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.impl.jooq.dao.ConfigurationDAO;

import es.translogia.tedi.ewok.TediInvoice;
import es.translogia.tedi.ewok.TediInvoiceTax;
import es.translogia.tedi.ewok.TediTaxType;
import es.translogia.tedi.json.TediInvoiceJSON;
import net.aonsolutions.aon.tedi.TediContext;
import net.aonsolutions.aon.tedi.TediInvoiceBuilder;
import net.aonsolutions.aon.tedi.test.AbstractTediTest;
import solutions.aon.in.invoice.UnknownInvoiceException;
import solutions.aon.in.invoice.img.InvoiceIMGParser;

public class TediInvoiceIMGParserAonDemoTestCase  extends AbstractTediTest { 

	@Test
	public void test_AON_01_RESTAURANTE_7() throws IOException, UnknownInvoiceException, ClassNotFoundException {
		testTemplate( TestTemplates.AON_01_RESTAURANTE_7);
	}
	@Test
	public void test_AON_02_QUINTANAPALLA_AREAS() throws IOException, UnknownInvoiceException, ClassNotFoundException {
		testTemplate( TestTemplates.AON_02_QUINTANAPALLA_AREAS);
	}
	@Test
	public void test_AON_03_ERKIAGA() throws IOException, UnknownInvoiceException, ClassNotFoundException {
		testTemplate( TestTemplates.AON_03_ERKIAGA);
	}
	
/*	
	@Test
	public void test_AON_01_AREAS() throws IOException, UnknownInvoiceException, ClassNotFoundException {
		testTemplate( TestTemplates.AON_01_AREAS );
	}
	@Test
	public void test_AON_02_ARTEPAN() throws IOException, UnknownInvoiceException, ClassNotFoundException {
		testTemplate( TestTemplates.AON_02_ARTEPAN );
	}
	@Test
	public void test_AON_03_BM_SMALL() throws IOException, UnknownInvoiceException, ClassNotFoundException {
		testTemplate( TestTemplates.AON_03_BM_SMALL );
	}
	@Test
	public void test_AON_04_BM() throws IOException, UnknownInvoiceException, ClassNotFoundException {
		testTemplate( TestTemplates.AON_03_BM);
	}
	@Test
	public void test_AON_04_Conforama20I() throws IOException, UnknownInvoiceException, ClassNotFoundException {
		testTemplate( TestTemplates.AON_04_Conforama20I);
	}
 */	
		
	
	
	private void testTemplate( TestTemplates template ) throws IOException, UnknownInvoiceException, ClassNotFoundException {
		StringBuilder out = new StringBuilder();
		out.append("-----------------");
		out.append("\n");
		out.append("TIEMPOS DE PARSEO");
		out.append("\n");
		out.append("-----------------");
		out.append("\n");
		
		TediContext tctx = new TediContext()
				.setDomain(DOMAIN_ID)
				.setDomainName(DOMAIN_NAME)
				.setAONContext ( ctx )
				;
		tctx.setAonConfiguration(ConfigurationDAO.getConfiguration(tctx.getAONContext()));
		tctx.getAonConfiguration().getCompany().setDocument("B01487271");
		
		Date start = new Date();
		try (InputStream is = TediInvoiceIMGParserAonDemoTestCase.class.getResourceAsStream(template.getFile())) {
			TediInvoiceBuilder tediInvoiceBuilder = new TediInvoiceBuilder( tctx );
			InvoiceIMGParser.parse(is , tediInvoiceBuilder);
			String file = "["+ template.getFile() +"]. ";
			System.out.println( file );
			TediInvoice invoice = tediInvoiceBuilder.get();
			
			invoice.setInsight(null);
			System.out.println( TediInvoiceJSON.toJSON( tediInvoiceBuilder.get() ).toString(2) );

			
			assertNotNull(invoice, file + "Invoice not parsed!");

			// FECHA DE EMISIÓN
			assertNotNull(invoice.getDate(),file + " Invoice has no date!");
			assertEquals(template.getDate(), invoice.getDate(),file + " Date does not match!");
			// DOCUMENTO SENDER
			assertNotNull(invoice.getSender(),file + " Invoice has no sender!");
			assertNotNull(invoice.getSender().getDocument(),file + " Invoice has no document sender!");
			assertEquals(template.getSenderDocument(), invoice.getSender().getDocument(),file + " Sender document does not match!");
			// DOCUMENTO RECEIVER
			assertNotNull(invoice.getReceiver(),file + " Invoice has no receiver!");
			assertNotNull(invoice.getReceiver().getDocument(),file + " Invoice has no document receiver!");
			assertEquals(template.getReceiverDocument(), invoice.getReceiver().getDocument(),file + " Receiver document does not match!");
			// TAXES
			assertNotNull(invoice.getTaxes(),file + " Invoice has no taxes!");
			assertEquals(template.getTaxNumber(), invoice.getTaxes().size(),file + " Invoice taxes number does not match!");

			// BASE AL 10%
			if (template.getTaxBase10() == null) {
				assertNull(getTaxBase(invoice, TediTaxType.IVA, 10 ),file + " Invoice has 10% VAT base!");	
			} else {
				assertNotNull(getTaxBase(invoice, TediTaxType.IVA, 10 ),file + " Invoice has no 10% VAT base!");
				assertEquals(template.getTaxBase10() , getTaxBase(invoice, TediTaxType.IVA, 10 ),0,file + " Invoice 10% VAT base not match: ");
			}

			// CUOTA AL 10%
			if (template.getTaxQuota10() == null) {
				assertNull(getTaxQuota(invoice, TediTaxType.IVA, 10 ),file + " Invoice has 10% VAT quota!");	
			} else {
				assertNotNull(getTaxQuota(invoice, TediTaxType.IVA, 10 ),file + " Invoice has no 10% VAT quota!");
				assertEquals(template.getTaxQuota10() , getTaxQuota(invoice, TediTaxType.IVA, 10 ),0,file + " Invoice 10% VAT quota not match: ");
			}

			// BASE AL 21%
			if (template.getTaxBase21() == null) {
				assertNull(getTaxBase(invoice, TediTaxType.IVA, 21 ),file + " Invoice has 21% VAT base!");	
			} else {
				assertNotNull(getTaxBase(invoice, TediTaxType.IVA, 21 ),file + " Invoice has no 21% VAT base!");
				assertEquals(template.getTaxBase21() , getTaxBase(invoice, TediTaxType.IVA, 21 ),0,file + " Invoice 21% VAT base not match: ");
			}
			
			// CUOTA AL 21%
			if (template.getTaxQuota21() == null) {
				assertNull(getTaxQuota(invoice, TediTaxType.IVA, 21 ),file + " Invoice has 21% VAT quota!");	
			} else {
				assertNotNull(getTaxQuota(invoice, TediTaxType.IVA, 21 ),file + " Invoice has no 21% VAT quota!");
				assertEquals(template.getTaxQuota21() , getTaxQuota(invoice, TediTaxType.IVA, 21 ),0,file + " Invoice 21% VAT quota not match: ");
			}
			
			// TOTAL
			assertNotNull(invoice.getTotal(),file + " Invoice has no total!");
			assertEquals(template.getTotal() , invoice.getTotal().doubleValue(),0,file + " Invoice total not match: ");
		} finally {
			Date end = new Date();
			out.append("*  ");
			out.append(end.getTime() - start.getTime());
			out.append(" ms.\t");
			out.append(template.getFile());
			out.append("\n");
		}
		out.append("-----------------");
		out.append("\n");
		System.out.println( out.toString() );
	}
	
	private TediInvoiceTax getTax(TediInvoice invoice, TediTaxType taxType, double percent) {
		if ( invoice.getTaxes() != null) {
			for ( TediInvoiceTax tax : invoice.getTaxes()) {
				if (tax.getTaxType() == taxType && percent == tax.getPercentage() ) {
					return tax;
				}
			}
		}
		return null;
	}
	
	private Double getTaxBase(TediInvoice invoice, TediTaxType taxType, double percent) {
		TediInvoiceTax tax = getTax(invoice, taxType, percent);
		return tax == null? null : tax.getBase();
	}
	private Double getTaxQuota(TediInvoice invoice, TediTaxType taxType, double percent) {
		TediInvoiceTax tax = getTax(invoice, taxType, percent);
		return tax == null? null : tax.getQuota();
	}
	
}
