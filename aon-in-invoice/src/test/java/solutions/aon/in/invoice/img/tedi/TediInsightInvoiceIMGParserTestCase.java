package solutions.aon.in.invoice.img.tedi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThrows;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import org.junit.Test;

import es.translogia.tedi.ewok.TediInsightInvoice;
import solutions.aon.in.invoice.UnknownInvoiceException;
import solutions.aon.in.invoice.img.InvoiceIMGException;
import solutions.aon.in.invoice.img.InvoiceIMGParser;
import solutions.aon.in.invoice.tedi.TediInsightInvoiceBuilder;

public class TediInsightInvoiceIMGParserTestCase {
	
	@Test
	public void testT_01_RESTAURANTE_7() throws IOException, UnknownInvoiceException, ClassNotFoundException {
		testTemplate(TestTemplates.T_01_RESTAURANTE_7);
	}

	@Test
	public void testT_02_QUINTANAPALLA_AREAS() throws IOException, UnknownInvoiceException, ClassNotFoundException {
		testTemplate(TestTemplates.T_02_QUINTANAPALLA_AREAS);
	}
	
	@Test
	public void testT_03_QUINTANAPALLA_AREAS() throws IOException, UnknownInvoiceException, ClassNotFoundException {
		testTemplate(TestTemplates.T_03_ERKIAGA);
	}

	@Test
	public void testT_04_ARTEPAN() throws IOException, UnknownInvoiceException, ClassNotFoundException {
		testTemplate(TestTemplates.T_04_ARTEPAN);
	}

	@Test
	public void testT_05_GASOLINERA() throws IOException, UnknownInvoiceException, ClassNotFoundException {
		testTemplate(TestTemplates.T_05_GASOLINERA);
	}

	@Test
	public void testT_06_PUERTA_DE_BILBAO() throws IOException, UnknownInvoiceException, ClassNotFoundException {
		testTemplate(TestTemplates.T_06_PUERTA_DE_BILBAO);
	}
	
	@Test
	public void testMODELOS_EMAR_JPG() throws IOException, UnknownInvoiceException, ClassNotFoundException {
		testTemplate(TestTemplates.MODELOS_EMAR_JPG);
	}
	
 	@Test
	public void testMODELOS_EMAR_PNG() throws IOException, UnknownInvoiceException, ClassNotFoundException {
 		InvoiceIMGException e = assertThrows(InvoiceIMGException.class, () -> testTemplate(TestTemplates.MODELOS_EMAR_PNG) );
 		String msg = e.getMessage();
 		assertEquals( true, msg.contains("5MB") );
	}

	public void testTemplate(TestTemplates template) throws IOException, UnknownInvoiceException {
		StringBuilder out = new StringBuilder();
		out.append("-----------------");
		out.append("\n");
		out.append("TIEMPOS DE PARSEO");
		out.append("\n");
		out.append("-----------------");
		out.append("\n");
		Date start = new Date();
		try (InputStream is = TediInsightInvoiceIMGParserTestCase.class.getResourceAsStream(template.getFile())) {
			TediInsightInvoiceBuilder tediInsightInvoiceBuilder = new TediInsightInvoiceBuilder( );
			InvoiceIMGParser.parse(is , tediInsightInvoiceBuilder);
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
			//assertEquals(file + " must parse " + template.getAmountNumber() + " amounts!" ,template.getAmountNumber(),insight.getAmounts().length);
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
