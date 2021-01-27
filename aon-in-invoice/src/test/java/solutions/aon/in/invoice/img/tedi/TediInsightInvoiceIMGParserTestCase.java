package solutions.aon.in.invoice.img.tedi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import es.translogia.tedi.ewok.TediInsightInvoice;
import solutions.aon.in.invoice.UnknownInvoiceException;
import solutions.aon.in.invoice.img.InvoiceIMGParser;
import solutions.aon.in.invoice.tedi.TediInsightInvoiceBuilder;

public class TediInsightInvoiceIMGParserTestCase {
	

	public void testTemplates() throws IOException, UnknownInvoiceException {
		StringBuilder out = new StringBuilder();
		out.append("-----------------");
		out.append("\n");
		out.append("TIEMPOS DE PARSEO");
		out.append("\n");
		out.append("-----------------");
		out.append("\n");
		for (TestTemplates template : TestTemplates.values()) {
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
		}
		out.append("-----------------");
		out.append("\n");
		System.out.println( out.toString() );
	}
}
