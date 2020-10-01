package solutions.aon.in.invoice.pdf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;

import org.junit.Test;

import solutions.aon.in.invoice.InvoiceBuilder;
import solutions.aon.in.invoice.UnknownInvoiceException;
import solutions.aon.in.invoice.templates.Document;

public class InvoicePDFParserTestCase {
	
	static class InvoiceAssert implements InvoiceBuilder<Object> {
		
		private Collection<Document> nifs;
		private Collection<Date> dates;
		private Collection<Double> amounts;

		@Override
		public void setInsightNifs(Collection<Document> nifs) {
			if (this.nifs == null) {
				this.nifs = nifs;
			} else {
				this.nifs.addAll(nifs);
			}
		}

		@Override
		public void setInsightDates(Collection<Date> dates) {
			if (this.dates == null) {
				this.dates = dates;
			} else {
				this.dates.addAll(dates);
			}
		}

		@Override
		public void setInsightAmounts(Collection<Double> amounts) {
			if (this.amounts == null) {
				this.amounts = amounts;
			} else {
				this.amounts.addAll(amounts);
			}
		}

		public Collection<Document> getNifs() {
			return nifs;
		}

		public Collection<Date> getDates() {
			return dates;
		}

		public Collection<Double> getAmounts() {
			return amounts;
		}
	}

	private enum TestTemplates {
		AMAZON_1 ("AMAZON_1.pdf", 3, 2 ),
		AMAZON_2 ("AMAZON_2.pdf", 4, 2 ),
		AMAZON_3 ("AMAZON_3.pdf", 4, 2 ),
		AMAZON_4 ("AMAZON_4.pdf", 6, 2 ),
		AMAZON_5 ("AMAZON_5.pdf", 6, 2 ),
		AMAZON_6 ("AMAZON_6.pdf", 6, 2 ),
		AMAZON_7 ("AMAZON_7.pdf", 5, 2 ),
		;

		private String file;
		private int documentsNumber;
		private int datesNumber;
		
		private TestTemplates(String file, int documentsNumber, int datesNumber) {
			this.file = file;
			this.documentsNumber = documentsNumber;
			this.datesNumber = datesNumber;
		}
		
		public String getFile() {
			return file;
		}
		public int getDocumentsNumber() {
			return documentsNumber;
		}
		public int getDatesNumber() {
			return datesNumber;
		}
	}
	
	@Test
	public void testTemplates() throws IOException, UnknownInvoiceException {
		for (TestTemplates template : TestTemplates.values()) {
			try (InputStream is = InvoicePDFParserTestCase.class.getResourceAsStream(template.getFile())) {
				InvoiceAssert invoiceAssert = new InvoiceAssert();
				InvoicePDFParser.parse(is,invoiceAssert);
				assertNotNull(template.getFile() + " Not parsed!",invoiceAssert);
				assertNotNull(template.getFile() + " has no documents!",invoiceAssert.getNifs());
				assertEquals(template.getFile() + " must parse " + template.getDocumentsNumber() + " documents!"
						,template.getDocumentsNumber(),invoiceAssert.getNifs().size());
				assertNotNull(template.getFile() + " has no dates!",invoiceAssert.getDates());
				assertEquals(template.getFile() + " must parse " + template.getDocumentsNumber() + " dates!"
						,template.getDatesNumber(),invoiceAssert.getDates().size());
			}
		}
	}

}
