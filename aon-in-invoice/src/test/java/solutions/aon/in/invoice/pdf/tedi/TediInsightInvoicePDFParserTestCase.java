package solutions.aon.in.invoice.pdf.tedi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.junit.Test;

import es.translogia.tedi.ewok.TediInsightInvoice;
import solutions.aon.in.invoice.UnknownInvoiceException;
import solutions.aon.in.invoice.pdf.InvoicePDFParser;
import solutions.aon.in.invoice.tedi.TediInsightInvoiceBuilder;

public class TediInsightInvoicePDFParserTestCase {

	
	@Test 
	public void testAMAZON_1() throws IOException, UnknownInvoiceException {testTemplates( TestTemplates.AMAZON_1 );}
	@Test public void testAMAZON_2() throws IOException, UnknownInvoiceException {testTemplates( TestTemplates.AMAZON_2 );}
	@Test public void testAMAZON_3() throws IOException, UnknownInvoiceException {testTemplates( TestTemplates.AMAZON_3 );}
	@Test public void testAMAZON_4() throws IOException, UnknownInvoiceException {testTemplates( TestTemplates.AMAZON_4  );}
	@Test public void testAMAZON_5() throws IOException, UnknownInvoiceException {testTemplates( TestTemplates.AMAZON_5  );}
	@Test public void testAMAZON_6() throws IOException, UnknownInvoiceException {testTemplates( TestTemplates.AMAZON_6  );}
	@Test public void testAMAZON_7() throws IOException, UnknownInvoiceException {testTemplates( TestTemplates.AMAZON_7  );}
	@Test public void testAON_01_AUSARTA() throws IOException, UnknownInvoiceException {testTemplates( TestTemplates.AON_01_AUSARTA );} 
	@Test public void testAON_02_BNP() throws IOException, UnknownInvoiceException {testTemplates( TestTemplates.AON_02_BNP  );}
	@Test public void testAON_04_BIP_DRIVE() throws IOException, UnknownInvoiceException {testTemplates( TestTemplates.AON_04_BIP_DRIVE );} 
	@Test public void testAON_05_BIP_DRIVE() throws IOException, UnknownInvoiceException {testTemplates( TestTemplates.AON_05_BIP_DRIVE  );}
	@Test public void testAON_06_PSA() throws IOException, UnknownInvoiceException {testTemplates( TestTemplates.AON_06_PSA  );}
	@Test public void testAON_06_TRANSLOGIA() throws IOException, UnknownInvoiceException {testTemplates( TestTemplates.AON_06_TRANSLOGIA );} 
	@Test public void testAON_07_TRANSLOGIA() throws IOException, UnknownInvoiceException {testTemplates( TestTemplates.AON_07_TRANSLOGIA  );}
	@Test public void testAON_08_TOLEDO() throws IOException, UnknownInvoiceException {testTemplates( TestTemplates.AON_08_TOLEDO  );}
	@Test public void testAON_09_VODAFONE() throws IOException, UnknownInvoiceException {testTemplates( TestTemplates.AON_09_VODAFONE  );}
	@Test public void testAON_10_VUELING() throws IOException, UnknownInvoiceException {testTemplates( TestTemplates.AON_10_VUELING  );}
	@Test public void testAYSER_1() throws IOException, UnknownInvoiceException {testTemplates( TestTemplates.AYSER_1  );}
	@Test public void testDOS_IVAS_1() throws IOException, UnknownInvoiceException {testTemplates( TestTemplates.DOS_IVAS_1 );} 
	@Test public void testDOS_IVAS_2() throws IOException, UnknownInvoiceException {testTemplates( TestTemplates.DOS_IVAS_2  );}
	@Test public void testDOS_IVAS_3() throws IOException, UnknownInvoiceException {testTemplates( TestTemplates.DOS_IVAS_3  );}
	@Test public void testIBERDROLA_1() throws IOException, UnknownInvoiceException {testTemplates( TestTemplates.IBERDROLA_1  );}
	@Test public void testIBERDROLA_2() throws IOException, UnknownInvoiceException {testTemplates( TestTemplates.IBERDROLA_2  );}
	@Test public void testMOVISTAR_1() throws IOException, UnknownInvoiceException {testTemplates( TestTemplates.MOVISTAR_1  );}
	@Test public void testMOVISTAR_2() throws IOException, UnknownInvoiceException {testTemplates( TestTemplates.MOVISTAR_2  );}
	@Test public void testMOVISTAR_3() throws IOException, UnknownInvoiceException {testTemplates( TestTemplates.MOVISTAR_3  );}
	@Test public void testMOVISTAR_4() throws IOException, UnknownInvoiceException {testTemplates( TestTemplates.MOVISTAR_4  );}
	@Test public void testMOVISTAR_5() throws IOException, UnknownInvoiceException {testTemplates( TestTemplates.MOVISTAR_5  );}
	@Test public void testMOVISTAR_6() throws IOException, UnknownInvoiceException {testTemplates( TestTemplates.MOVISTAR_6  );}
	@Test public void testMOVISTAR_7() throws IOException, UnknownInvoiceException {testTemplates( TestTemplates.MOVISTAR_7  );}
	@Test public void testNATURGY_1() throws IOException, UnknownInvoiceException {testTemplates( TestTemplates.NATURGY_1  );}
	@Test public void testNATURGY_2() throws IOException, UnknownInvoiceException {testTemplates( TestTemplates.NATURGY_2  );}
	@Test public void testNATURGY_3() throws IOException, UnknownInvoiceException {testTemplates( TestTemplates.NATURGY_3  );}
	@Test public void testORANGE_1() throws IOException, UnknownInvoiceException {testTemplates( TestTemplates.ORANGE_1  );}
	@Test public void testORANGE_2() throws IOException, UnknownInvoiceException {testTemplates( TestTemplates.ORANGE_2  );}
	@Test public void testORANGE_3() throws IOException, UnknownInvoiceException {testTemplates( TestTemplates.ORANGE_3  );}
	@Test public void testRETENCION_1() throws IOException, UnknownInvoiceException {testTemplates( TestTemplates.RETENCION_1  );}
	@Test public void testAON_2021_02_03_AMAZON() throws IOException, UnknownInvoiceException {testTemplates( TestTemplates.AON_2021_02_03_AMAZON );}
	
	@Test public void testAON_2021_01_29_TERMOFUEL() throws IOException, UnknownInvoiceException {testTemplates( TestTemplates.AON_2021_01_29_TERMOFUEL);}

	// @Test public void test
	
	public void testTemplates(TestTemplates template) throws IOException, UnknownInvoiceException {
		StringBuilder out = new StringBuilder();
		out.append("-----------------");
		out.append("\n");
		out.append("TIEMPOS DE PARSEO");
		out.append("\n");
		out.append("-----------------");
		out.append("\n");
		Date start = new Date();
		try (InputStream is = TediInsightInvoicePDFParserTestCase.class.getResourceAsStream(template.getFile())) {
			TediInsightInvoiceBuilder tediInsightInvoiceBuilder = new TediInsightInvoiceBuilder( );
			InvoicePDFParser.parse(is , tediInsightInvoiceBuilder);
			String file = "["+ template.getFile() +"]. "; 
			TediInsightInvoice insight = tediInsightInvoiceBuilder.getInsight();
			assertNotNull(file + "Not parsed!", insight);
			if (template.getIssueDate() != null) {
				assertNotNull(file + "Invoice has no date!",insight.getIssueDate());
				assertEquals(file + " date does not match!", template.getIssueDate(), insight.getIssueDate());
			} else {
				assertNull(file + "Invoice has date!",insight.getIssueDate());
			}
			assertEquals(file  + " must parse " + template.getDocumentsNumber() + " documents!" ,template.getDocumentsNumber(), insight.getNifs().length );
			assertNotNull(file + " has no dates!",insight.getDates());
			assertEquals(file + " must parse " + template.getDatesNumber() + " dates!" ,template.getDatesNumber(),insight.getDates().length);
			assertNotNull(file + " has no amounts!",insight.getAmounts());
			assertEquals(file + " must parse " + template.getAmountNumber() + " amounts!" ,template.getAmountNumber(),insight.getAmounts().length);
			if (template.getTotal() != null) {
				assertNotNull(file + " has no total!",insight.getTotal());
				assertEquals(file + " total not match: ",template.getTotal() , insight.getTotal().doubleValue(),0);
			} else {
				assertNull(file + "Invoice has total!",insight.getTotal());
			}
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
}
