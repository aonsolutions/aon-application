package net.aonsolutions.aon.tedi.test.pdf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.junit.Test;

import com.esferalia.aon.occam.impl.jooq.dao.ConfigurationDAO;

import es.translogia.tedi.ewok.TediInvoice;
import es.translogia.tedi.ewok.TediInvoiceTax;
import es.translogia.tedi.ewok.TediTaxType;
import es.translogia.tedi.json.TediInvoiceJSON;
import net.aonsolutions.aon.tedi.TediContext;
import net.aonsolutions.aon.tedi.TediInvoiceBuilder;
import net.aonsolutions.aon.tedi.test.AbstractTediTest;
import solutions.aon.in.invoice.UnknownInvoiceException;
import solutions.aon.in.invoice.pdf.InvoicePDFParser;

public class TediInvoicePDFParserAonDemoTestCase extends AbstractTediTest {

	@Test
	public void test01AUSARTA() throws IOException, UnknownInvoiceException, ClassNotFoundException {
		testTemplate(TestTemplates.AON_01_AUSARTA);
	}
	@Test
	public void test02BNP() throws IOException, UnknownInvoiceException, ClassNotFoundException {
		testTemplate(TestTemplates.AON_02_BNP);
	}
	@Test
	public void test03BIP_DRIVE() throws IOException, UnknownInvoiceException, ClassNotFoundException {
		testTemplate(TestTemplates.AON_03_BIP_DRIVE);
	}
	@Test
	public void test04BIP_DRIVE() throws IOException, UnknownInvoiceException, ClassNotFoundException {
		testTemplate(TestTemplates.AON_04_BIP_DRIVE);
	}
	@Test
	public void test05TRANSLOGIA() throws IOException, UnknownInvoiceException, ClassNotFoundException {
		testTemplate(TestTemplates.AON_05_TRANSLOGIA);
	}
	@Test
	public void test06TRANSLOGIA() throws IOException, UnknownInvoiceException, ClassNotFoundException {
		testTemplate(TestTemplates.AON_06_TRANSLOGIA);
	}
	@Test
	public void test07TOLEDO() throws IOException, UnknownInvoiceException, ClassNotFoundException {
		testTemplate(TestTemplates.AON_07_TOLEDO);
	}
	@Test
	public void test08VODAFONE() throws IOException, UnknownInvoiceException, ClassNotFoundException {
		testTemplate(TestTemplates.AON_08_VODAFONE);
	}
	@Test
	public void test09VUELING() throws IOException, UnknownInvoiceException, ClassNotFoundException {
		testTemplate(TestTemplates.AON_09_VUELING);
	}
	@Test
	public void testAON_2021_02_03_AMAZON() throws IOException, UnknownInvoiceException, ClassNotFoundException {
		testTemplate(TestTemplates.AON_2021_02_03_AMAZON);
	}
	
	@Test
	public void testAON_2020_12_31_BK() throws IOException, UnknownInvoiceException, ClassNotFoundException {
		testTemplate(TestTemplates.AON_2020_12_31_BK);
	}
	
	@Test
	public void testAON_2021_02_01_RCR() throws IOException, UnknownInvoiceException, ClassNotFoundException {
		testTemplate(TestTemplates.AON_2021_02_01_RCR );
	}
	
	private void testTemplate(TestTemplates template) throws IOException, UnknownInvoiceException, ClassNotFoundException {
		testTemplate(template, false);
	}
		
	private void testTemplate(TestTemplates template, boolean sales) throws IOException, UnknownInvoiceException, ClassNotFoundException {
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
		tctx.getAonConfiguration().getCompany().setDocument(sales?template.getReceiverDocument():template.getReceiverDocument());
		
		Date start = new Date();
		try (InputStream is = TediInvoicePDFParserAonDemoTestCase.class.getResourceAsStream(template.getFile())) {
			TediInvoiceBuilder tediInvoiceBuilder = new TediInvoiceBuilder( tctx );
			InvoicePDFParser.parse(is , tediInvoiceBuilder);
			String file = "["+ template.getFile() +"]. ";
			System.out.println( file );
			TediInvoice invoice = tediInvoiceBuilder.get();
			
			invoice.setInsight(null);
			System.out.println( TediInvoiceJSON.toJSON( tediInvoiceBuilder.get() ).toString(2) );

			
			assertNotNull(file + "Invoice not parsed!", invoice);

			// FECHA DE EMISIÓN
			assertNotNull(file + " Invoice has no date!",invoice.getDate());
			assertEquals(file + " Date does not match!", template.getDate(), invoice.getDate());
			// NUMERO DE FACTURA
/*				
				if (template.getReference() == null) {
					assertNull(file + " Invoice has reference!",invoice.getReference());	
				} else {
					assertNotNull(file + " Invoice has no reference!",invoice.getReference());
					assertEquals(file + "  Reference does not match!", template.getReference(), invoice.getReference());
				}
*/
			// DOCUMENTO SENDER
			assertNotNull(file + " Invoice has no sender!",invoice.getSender());
			assertNotNull(file + " Invoice has no document sender!",invoice.getSender().getDocument());
			assertEquals(file + " Sender document does not match!", template.getSenderDocument(), invoice.getSender().getDocument());
			// DOCUMENTO RECEIVER
			assertNotNull(file + " Invoice has no receiver!",invoice.getReceiver());
			assertNotNull(file + " Invoice has no document receiver!",invoice.getReceiver().getDocument());
			assertEquals(file + " Receiver document does not match!", template.getReceiverDocument(), invoice.getReceiver().getDocument());
			// TAXES
			assertNotNull(file + " Invoice has no taxes!",invoice.getTaxes());
			assertEquals(file + " Invoice taxes number does not match!", template.getTaxNumber(), invoice.getTaxes().size());

			// BASE AL 0%
			if (template.getTaxBase0() == null) {
				assertNull(file + " Invoice has 0% VAT base!", getTaxBase(invoice, TediTaxType.IVA, 0 ));	
			} else {
				assertNotNull(file + " Invoice has no 0% VAT base!", getTaxBase(invoice, TediTaxType.IVA, 0 ));
				assertEquals(file + " Invoice 0% VAT base not match: ",template.getTaxBase0() , getTaxBase(invoice, TediTaxType.IVA, 0 ),0);
			}

			// CUOTA AL 0%
			if (template.getTaxQuota0() == null) {
				assertNull(file + " Invoice has 0% VAT quota!", getTaxQuota(invoice, TediTaxType.IVA, 0 ));	
			} else {
				assertNotNull(file + " Invoice has no 0% VAT quota!", getTaxQuota(invoice, TediTaxType.IVA, 0 ));
				assertEquals(file + " Invoice 0% VAT quota not match: ",template.getTaxQuota0() , getTaxQuota(invoice, TediTaxType.IVA, 0 ),0);
			}

			// BASE AL 4%
			if (template.getTaxBase4() == null) {
				assertNull(file + " Invoice has 4% VAT base!", getTaxBase(invoice, TediTaxType.IVA, 4 ));	
			} else {
				assertNotNull(file + " Invoice has no 4% VAT base!", getTaxBase(invoice, TediTaxType.IVA, 4 ));
				assertEquals(file + " Invoice 4% VAT base not match: ",template.getTaxBase4() , getTaxBase(invoice, TediTaxType.IVA, 4 ),0);
			}

			// CUOTA AL 4%
			if (template.getTaxQuota4() == null) {
				assertNull(file + " Invoice has 4% VAT quota!", getTaxQuota(invoice, TediTaxType.IVA, 4 ));	
			} else {
				assertNotNull(file + " Invoice has no 4% VAT quota!", getTaxQuota(invoice, TediTaxType.IVA, 4 ));
				assertEquals(file + " Invoice 4% VAT quota not match: ",template.getTaxQuota4() , getTaxQuota(invoice, TediTaxType.IVA, 4 ),0);
			}

			// BASE AL 10%
			if (template.getTaxBase10() == null) {
				assertNull(file + " Invoice has 10% VAT base!", getTaxBase(invoice, TediTaxType.IVA, 10 ));	
			} else {
				assertNotNull(file + " Invoice has no 10% VAT base!", getTaxBase(invoice, TediTaxType.IVA, 10 ));
				assertEquals(file + " Invoice 10% VAT base not match: ",template.getTaxBase10() , getTaxBase(invoice, TediTaxType.IVA, 10 ),0);
			}

			// CUOTA AL 10%
			if (template.getTaxQuota10() == null) {
				assertNull(file + " Invoice has 10% VAT quota!", getTaxQuota(invoice, TediTaxType.IVA, 10 ));	
			} else {
				assertNotNull(file + " Invoice has no 10% VAT quota!", getTaxQuota(invoice, TediTaxType.IVA, 10 ));
				assertEquals(file + " Invoice 10% VAT quota not match: ",template.getTaxQuota10() , getTaxQuota(invoice, TediTaxType.IVA, 10 ),0);
			}

			// BASE AL 21%
			if (template.getTaxBase21() == null) {
				assertNull(file + " Invoice has 21% VAT base!", getTaxBase(invoice, TediTaxType.IVA, 21 ));	
			} else {
				assertNotNull(file + " Invoice has no 21% VAT base!", getTaxBase(invoice, TediTaxType.IVA, 21 ));
				assertEquals(file + " Invoice 21% VAT base not match: ",template.getTaxBase21() , getTaxBase(invoice, TediTaxType.IVA, 21 ),0);
			}
			
			// CUOTA AL 21%
			if (template.getTaxQuota21() == null) {
				assertNull(file + " Invoice has 21% VAT quota!", getTaxQuota(invoice, TediTaxType.IVA, 21 ));	
			} else {
				assertNotNull(file + " Invoice has no 21% VAT quota!", getTaxQuota(invoice, TediTaxType.IVA, 21 ));
				assertEquals(file + " Invoice 21% VAT quota not match: ",template.getTaxQuota21() , getTaxQuota(invoice, TediTaxType.IVA, 21 ),0);
			}
			
			// TOTAL
			assertNotNull(file + " Invoice has no total!",invoice.getTotal());
			assertEquals(file + " Invoice total not match: ",template.getTotal() , invoice.getTotal().doubleValue(),0);
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
