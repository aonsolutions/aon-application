package solutions.aon.in.invoice.pdf;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

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
	
	private enum TestTemplates {
		AMAZON_1 ("AMAZON_1.pdf") {
			public int getDocumentsNumber(){ return 3; }
			public int getDatesNumber(){ return 2; }
			public int getAmountNumber(){ return 9; }
			public int getTaxNumber(){ return 1; }
			public int getTotalNumber(){ return 1; }
			public Double getTotal(){ return 14.00; }
		},
	
		AMAZON_2 ("AMAZON_2.pdf") {
			public int getDocumentsNumber(){ return 4; }
			public int getDatesNumber(){ return 2; }
			public int getAmountNumber(){ return 21; }
			public Double getTotal(){ return 58.88; }
			public int getTotalNumber(){ return 1; }
			public int getTaxNumber(){ return 1; }
		},
		AMAZON_3 ("AMAZON_3.pdf") {
			public int getDocumentsNumber(){ return 4; }
			public int getDatesNumber(){ return 2; }
			public int getAmountNumber(){ return 9; }
			public Double getTotal(){ return 31.70; }
			public int getTotalNumber(){ return 1; }
			public int getTaxNumber(){ return 1; }
		},
		AMAZON_4 ("AMAZON_4.pdf") {
			public int getDocumentsNumber(){ return 6; }
			public int getDatesNumber(){ return 2; }
			public int getAmountNumber(){ return 12; }
			public Double getTotal(){ return 259.82; }
			public int getTotalNumber(){ return 1; }
			public int getTaxNumber(){ return 1; }
		},
		AMAZON_5 ("AMAZON_5.pdf") {
			public int getDocumentsNumber(){ return 6; }
			public int getDatesNumber(){ return 2; }
			public int getAmountNumber(){ return 9; }
			public Double getTotal(){ return 13.45; }
			public int getTotalNumber(){ return 1; }
			public int getTaxNumber(){ return 1; }
		},
		AMAZON_6 ("AMAZON_6.pdf") {
			public int getDocumentsNumber(){ return 6; }
			public int getDatesNumber(){ return 2; }
			public int getAmountNumber(){ return 9; }
			public Double getTotal(){ return 19.90; }
			public int getTotalNumber(){ return 1; }
			public int getTaxNumber(){ return 1; }
		},
		AMAZON_7 ("AMAZON_7.pdf") {
			public int getDocumentsNumber(){ return 5; }
			public int getDatesNumber(){ return 2; }
			public int getAmountNumber(){ return 12; }
			public Double getTotal(){ return 18.98; }
			public int getTotalNumber(){ return 1; }
			public int getTaxNumber(){ return 1; }
		},
		AYSER_1 ("AYSER_1.pdf") {
			public int getDocumentsNumber(){ return 3; }
			public int getDatesNumber(){ return 5; }
			public int getAmountNumber(){ return 5; }
			public Double getTotal(){ return 86.83; }
			public int getTotalNumber(){ return 1; }
			public int getTaxNumber(){ return 1; }
		},
		DOS_IVAS_1 ("DOS_IVAS_1.pdf") {
			public int getDocumentsNumber(){ return 2; }
			public int getDatesNumber(){ return 1; }
			public int getAmountNumber(){ return 13; }
			public Double getTotal(){ return 6549.72; }
			public int getTotalNumber(){ return 0; }
			public int getTaxNumber(){ return 2; }
		},
		DOS_IVAS_2 ("DOS_IVAS_2.pdf") {
			public int getDocumentsNumber(){ return 4; }
			public int getDatesNumber(){ return 7; }
			public int getAmountNumber(){ return 20; }
			public Double getTotal(){ return 309.57; }
			public int getTotalNumber(){ return 1; }
			public int getTaxNumber(){ return 2; }
		},
		
//		
//		[ISSUE: InvoiceTaxParser. Discrimiar tipos de IVA #2439]
//		DOS_IVAS_3 ("DOS_IVAS_3.pdf") {
//			public int getDocumentsNumber(){ return 4; }
//			public int getDatesNumber(){ return 7; }
//			public int getAmountNumber(){ return 24; }
//			public double getTotal(){ return 349.28; }
//			public int getTaxNumber(){ return 2; }
//		},

		IBERDROLA_1 ("IBERDROLA_1.pdf") {
			public int getDocumentsNumber(){ return 5; }
			public int getDatesNumber(){ return 45; }
			public int getAmountNumber(){ return 74; }
			public int getTaxNumber(){ return 2; }			
			public int getTotalNumber(){ return 3; }
			public Double getTotal(){ return 835.95; }
		},
		IBERDROLA_2 ("IBERDROLA_2.pdf") {
			public int getDocumentsNumber(){ return 5; }
			public int getDatesNumber(){ return 45; }
			public int getAmountNumber(){ return 73; }
			public int getTaxNumber(){ return 2; }			
			public int getTotalNumber(){ return 3; }
			public Double getTotal(){ return 585.87; }
		},
		MOVISTAR_1 ("MOVISTAR_1.pdf") {
			public int getDocumentsNumber(){ return 1; }
			public int getDatesNumber(){ return 6; }
			public int getAmountNumber(){ return 11; }
			public Double getTotal(){ return 20.66; }
			public int getTotalNumber(){ return 1; }
			public int getTaxNumber(){ return 2; }
		},
		MOVISTAR_2 ("MOVISTAR_2.pdf") {
			public int getDocumentsNumber(){ return 1; }
			public int getDatesNumber(){ return 5; }
			public int getAmountNumber(){ return 9; }
			public Double getTotal(){ return 100.18; }
			public int getTotalNumber(){ return 1; }
			public int getTaxNumber(){ return 1; }
		},
		MOVISTAR_3 ("MOVISTAR_3.pdf") {
			public int getDocumentsNumber(){ return 1; }
			public int getDatesNumber(){ return 6; }
			public int getAmountNumber(){ return 9; }
			public Double getTotal(){ return 84.58; }
			public int getTotalNumber(){ return 1; }
			public int getTaxNumber(){ return 1; }
		},
		MOVISTAR_4 ("MOVISTAR_4.pdf") {
			public int getDocumentsNumber(){ return 1; }
			public int getDatesNumber(){ return 5; }
			public int getAmountNumber(){ return 7; }
			public Double getTotal(){ return 89.12; }
			public int getTotalNumber(){ return 1; }
			public int getTaxNumber(){ return 1; }
		},
		MOVISTAR_5 ("MOVISTAR_5.pdf") {
			public int getDocumentsNumber(){ return 1; }
			public int getDatesNumber(){ return 4; }
			public int getAmountNumber(){ return 8; }
			public Double getTotal(){ return 87.99; }
			public int getTotalNumber(){ return 1; }
			public int getTaxNumber(){ return 1; }
		},
		
//		[ISSUE: Total factura inferior a total a pagar #2440]
		MOVISTAR_6 ("MOVISTAR_6.pdf") {
			public int getDocumentsNumber(){ return 1; }
			public int getDatesNumber(){ return 5; }
			public int getAmountNumber(){ return 8; }
			public Double getTotal(){ return 96.59; }
			public int getTotalNumber(){ return 1; }
			public int getTaxNumber(){ return 1; }
		},
		
		MOVISTAR_7 ("MOVISTAR_7.pdf") {
			public int getDocumentsNumber(){ return 1; }
			public int getDatesNumber(){ return 5; }
			public int getAmountNumber(){ return 28; }
			public Double getTotal(){ return 258.50; }
			public int getTotalNumber(){ return 1; }
			public int getTaxNumber(){ return 1; }
		},
		
		NATURGY_1 ("NATURGY_1.pdf") {
			public int getDocumentsNumber(){ return 2; }
			public int getDatesNumber(){ return 34; }
			public int getAmountNumber(){ return 89; }
			public Double getTotal(){ return 429.86; }
			public int getTotalNumber(){ return 1; }
			public int getTaxNumber(){ return 1; }
		},
		
		NATURGY_2 ("NATURGY_2.pdf") {
			public int getDocumentsNumber(){ return 2; }
			public int getDatesNumber(){ return 10; }
			public int getAmountNumber(){ return 77; }
			public Double getTotal(){ return 421.39; }
			public int getTotalNumber(){ return 1; }
			public int getTaxNumber(){ return 1; }
		},
		
//		[ISSUE: Total factura negativo #2441]
		NATURGY_3 ("NATURGY_3.pdf") {
			public int getDocumentsNumber(){ return 2; }
			public int getDatesNumber(){ return 9; }
			public int getAmountNumber(){ return 77; }
			public Double getTotal(){ return -428.46; }
			public int getTotalNumber(){ return 1; }
			public int getTaxNumber(){ return 2; }
		},
		
		ORANGE_1 ("ORANGE_1.pdf") {
			public int getDocumentsNumber(){ return 5; }
			public int getDatesNumber(){ return 5; }
			public int getAmountNumber(){ return 452; }
			public Double getTotal(){ return null; }	// 247.61
			public int getTotalNumber(){ return 6; }
			public int getTaxNumber(){ return 2; }
		},
		ORANGE_2 ("ORANGE_2.pdf") {
			public int getDocumentsNumber(){ return 6; }
			public int getDatesNumber(){ return 5; }
			public int getAmountNumber(){ return 495; }
			public Double getTotal(){ return null; }	// 151.71
			public int getTotalNumber(){ return 7; }
			public int getTaxNumber(){ return 12; }
		},
		ORANGE_3 ("ORANGE_3.pdf") {
			public int getDocumentsNumber(){ return 5; }
			public int getDatesNumber(){ return 5; }
			public int getAmountNumber(){ return 444; }
			public Double getTotal(){ return null; }	// 151.71
			public int getTotalNumber(){ return 5; }
			public int getTaxNumber(){ return 3; }
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
		public abstract int getTaxNumber();
		public abstract int getTotalNumber();
		public abstract Double getTotal();
	}

	static class InvoiceAssert implements InvoiceBuilder<Object> {
		
		private Collection<Document> nifs;
		private Collection<Date> dates;
		private Collection<Double> amounts;
		private Collection<Double> totals;
		
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
		public void setInsightTotals(Collection<Double> totals) {
			if (this.totals == null) {
				this.totals = totals;
			} else {
				this.totals.addAll(totals);
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
		public Collection<Double> getTotals() {
			return totals;
		}
		public Double getTotal() {
			return total;
		}
		public Collection<InvoiceTax> getTaxes() {
			return taxes;
		}
	}

	@Test
	public void testTemplates() throws IOException, UnknownInvoiceException {
		int templ = 1;
		for (TestTemplates template : TestTemplates.values()) {
			try (InputStream is = InvoicePDFParserTestCase.class.getResourceAsStream(template.getFile())) {
				System.out.println("-----------------------------------------------");
				System.out.println("\t\tParsing " + template.getFile() + " ....");
				System.out.println("-----------------------------------------------");
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
				assertEquals(template.getFile() + " must parse " + template.getAmountNumber() + " amounts!"
						,template.getAmountNumber(),invoiceAssert.getAmounts().size());

				System.out.println( "\tTAXES: (expected: " + template.getTaxNumber() + ")" );
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
				assertEquals(template.getFile() + " must parse " + template.getTaxNumber() + " taxes!"
						,template.getTaxNumber(),invoiceAssert.getTaxes().size());

				System.out.println( "\tTOTALS: (expected: " + template.getTotalNumber() + ")" );
				assertNotNull(template.getFile() + " has no totals!",invoiceAssert.getTotals());
				i = 1;
				for (Double total: invoiceAssert.getTotals()) {
					System.out.println( "\t\t"+i+++".-\t TOTAL : " + total );
				}
				assertEquals(template.getFile() + " must parse " + template.getTotalNumber() + " totals!"
						,template.getTotalNumber(),invoiceAssert.getTotals().size());
				
				
				System.out.println( "\tTOTAL: (expected: " + template.getTotal() + ")" );
				if (template.getTotal() == null) {
					assertNull(template.getFile() + " must have no total!",invoiceAssert.getTotal());
				} else {
					assertNotNull(template.getFile() + " has no total!",invoiceAssert.getTotal());
					assertEquals(template.getFile() + " total not match: ",template.getTotal() , invoiceAssert.getTotal().doubleValue(),0);
				}
				System.out.println( "\t\tTOTAL ...: " +invoiceAssert.getTotal());
				System.out.println("-----------------------------------------------");
				System.out.println();
				
				templ++;
			}
			System.out.println( "\t\t " + templ + " INVOICES PARSED.");
		}
	}

}
