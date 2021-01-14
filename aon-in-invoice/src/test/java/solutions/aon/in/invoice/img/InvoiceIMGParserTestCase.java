package solutions.aon.in.invoice.img;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.Date;

import org.junit.Test;

import solutions.aon.in.invoice.InvoiceBuilder;
import solutions.aon.in.invoice.templates.Document;

public class InvoiceIMGParserTestCase {
	
	
	private static class InvoiceTester implements InvoiceBuilder<Void> {
		
		private Double expectedTotal ;

		@Override
		public Void get() {
			return null;
		}

		@Override
		public void addInsightNifs(Collection<Document> nifs) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void addInsightDates(Collection<Date> dates) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void addInsightAmounts(Collection<Double> amounts) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setInsightIssueDate(Date issueDate) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void setInsightTotal(Double total) {
			assertEquals(expectedTotal, total);
		}

		@Override
		public void setReference(String reference) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public String[] getReferencePatterns() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void finalizeParse() {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	@Test
	public void test7() {
		InvoiceIMGParser.parse(InvoiceIMGParserTestCase.class.getResourceAsStream("7.jpg"), new InvoiceTester() );
	}

}
