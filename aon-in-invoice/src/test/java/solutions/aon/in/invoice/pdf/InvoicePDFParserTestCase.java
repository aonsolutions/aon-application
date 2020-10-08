package solutions.aon.in.invoice.pdf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;

import org.junit.Test;

import solutions.aon.in.invoice.InvoiceBuilder;
import solutions.aon.in.invoice.UnknownInvoiceException;
import solutions.aon.in.invoice.templates.Document;
import solutions.aon.in.invoice.templates.InvoiceTax;

public class InvoicePDFParserTestCase {
	
	static class InvoiceAssert implements InvoiceBuilder<Object> {
		
		private Collection<Document> nifs;
		private Collection<Date> dates;
		private Collection<Double> amounts;
		
		private Double total;
		private Collection<InvoiceTax> taxes;

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
		
		@Override
		public void setTotal(double total) {
			this.total = total;
		}
		
		@Override
		public void setTax(InvoiceTax tax) {
			if (this.taxes == null) {
				this.taxes = new LinkedList<InvoiceTax>();
			} 
			this.taxes.add(tax);
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
		public Double getTotal() {
			return total;
		}
		public Collection<InvoiceTax> getTaxes() {
			return taxes;
		}
	}

	private enum TestTemplates {
//		AMAZON_1 ("AMAZON_1.pdf", 3, 2 ),
//		AMAZON_2 ("AMAZON_2.pdf", 4, 2 ),
//		AMAZON_3 ("AMAZON_3.pdf", 4, 2 ),
//		AMAZON_4 ("AMAZON_4.pdf", 6, 2 ),
//		AMAZON_5 ("AMAZON_5.pdf", 6, 2 ),
//		AMAZON_6 ("AMAZON_6.pdf", 6, 2 ),
//		AMAZON_7 ("AMAZON_7.pdf", 5, 2 ),
//		
//		MOVISTAR_1 ("MOVISTAR_1.pdf", 1, 6 ),
//		MOVISTAR_2 ("MOVISTAR_2.pdf", 1, 5 ),
//		MOVISTAR_3 ("MOVISTAR_3.pdf", 1, 6 ),
//		MOVISTAR_4 ("MOVISTAR_4.pdf", 1, 5 ),
//		MOVISTAR_5 ("MOVISTAR_5.pdf", 1, 4 ),
//		MOVISTAR_6 ("MOVISTAR_6.pdf", 1, 5 ),
//		MOVISTAR_7 ("MOVISTAR_7.pdf", 1, 6 ),
//		
//		ORANGE_1 ("ORANGE_1.pdf", 5, 5 ),
//		ORANGE_2 ("ORANGE_2.pdf", 6, 5 ),
//		ORANGE_3 ("ORANGE_3.pdf", 5, 5 ),
//
//		NATURGY_1 ("NATURGY_1.pdf", 2, 34 ),
//		NATURGY_2 ("NATURGY_2.pdf", 2, 10 ),
//		NATURGY_3 ("NATURGY_3.pdf", 2, 9 ),
//	
//		IBERDROLA_1 ("IBERDROLA_1.pdf", 5, 45 ),
//		IBERDROLA_2 ("IBERDROLA_2.pdf", 5, 45 ),
		
		AYSER_1 ("AYSER_1.pdf") {
			public int getDocumentsNumber(){ return 3; }
			public int getDatesNumber(){ return 5; }
			public int getAmountNumber(){ return 5; }
			public double getTotal(){ return 86.83; }
			public int getVatsNumber(){ return 1; }
		},
		DOS_IVAS_1 ("DOS_IVAS_1.pdf") {
			public int getDocumentsNumber(){ return 2; }
			public int getDatesNumber(){ return 1; }
			public int getAmountNumber(){ return 13; }
			public double getTotal(){ return 6549.72; }
			public int getVatsNumber(){ return 2; }
		},
//		RETENCION_1 ("RETENCION_1.pdf") {
//			public int getDocumentsNumber(){ return 2; }
//			public int getDatesNumber(){ return 1; }
//			public int getAmountNumber(){ return 5; }
//			public double getTotal(){ return 867.00; }
//			public int getVatsNumber(){ return 2; }
//		}
		;

		private String file;
		
		private TestTemplates(String file) {
			this.file = file;
		}
		
		public String getFile() {
			return file;
		}
		
		public abstract int getDocumentsNumber();
		public abstract int getDatesNumber();
		public abstract int getAmountNumber();
		public abstract double getTotal();
		public abstract int getVatsNumber();
	}
	
	@Test
	public void testTemplates() throws IOException, UnknownInvoiceException {
		for (TestTemplates template : TestTemplates.values()) {
			try (InputStream is = InvoicePDFParserTestCase.class.getResourceAsStream(template.getFile())) {
				System.out.println("\t Parsing " + template.getFile() + " ....");
				InvoiceAssert invoiceAssert = new InvoiceAssert();
				InvoicePDFParser.parse(is,invoiceAssert);
				assertNotNull(template.getFile() + " Not parsed!",invoiceAssert);
				assertNotNull(template.getFile() + " has no documents!",invoiceAssert.getNifs());
				
				System.out.println( "\tDocuments: (expected: " + template.getDocumentsNumber() + ")" );
				int i = 1;
				for (Document doc : invoiceAssert.getNifs()) {
					System.out.println( "\t\t"+i+++".-\t"+doc.getData() );
				}
				assertEquals(template.getFile() + " must parse " + template.getDocumentsNumber() + " documents!"
						,template.getDocumentsNumber(),invoiceAssert.getNifs().size());
				assertNotNull(template.getFile() + " has no dates!",invoiceAssert.getDates());
				
				System.out.println( "\tDates: (expected: " + template.getDatesNumber() + ")" );
				i = 1;
				for (Date date : invoiceAssert.getDates()) {
					System.out.println( "\t\t"+i+++".-\t"+date);
				}
				assertEquals(template.getFile() + " must parse " + template.getDatesNumber() + " dates!"
						,template.getDatesNumber(),invoiceAssert.getDates().size());
				
				assertNotNull(template.getFile() + " has no amounts!",invoiceAssert.getAmounts());
				System.out.println( "\tAmounts: (expected: " + template.getAmountNumber() + ")" );
				i = 1;
				for (Double amount: invoiceAssert.getAmounts()) {
					System.out.println( "\t\t"+i+++".-\t"+amount);
				}
				
				System.out.println( "\tTAXES: (expected: " + template.getVatsNumber() + ")" );
				assertNotNull(template.getFile() + " has no taxes!",invoiceAssert.getTaxes());
				i = 1;
				for (InvoiceTax vat: invoiceAssert.getTaxes()) {
					System.out.println( "\t\t"+i+++".-\t"
						+vat.getType() + " \t"
						+vat.getBase() + " \t"
						+vat.getPercent() +"% \t"
						+vat.getQuota() + " \t"
					);
				}
				
				System.out.println( "\tTOTAL: (expected: " + template.getTotal() + ")" );
				assertNotNull(template.getFile() + " has no total!",invoiceAssert.getTotal());
				assertEquals(template.getFile() + " total not match: ",template.getTotal() , invoiceAssert.getTotal().doubleValue(),0);
				System.out.println( "\t\tTOTAL ...: " +invoiceAssert.getTotal());
			}
		}
	}

}
