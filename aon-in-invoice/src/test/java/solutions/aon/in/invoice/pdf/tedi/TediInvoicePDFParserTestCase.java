package solutions.aon.in.invoice.pdf.tedi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import org.junit.Test;

import es.translogia.tedi.ewok.TediInvoice;
import es.translogia.tedi.ewok.TediInvoiceTax;
import es.translogia.tedi.ewok.TediTaxType;
import es.translogia.tedi.json.TediInvoiceJSON;
import solutions.aon.in.invoice.UnknownInvoiceException;
import solutions.aon.in.invoice.pdf.InvoicePDFParser;
import solutions.aon.in.invoice.tedi.TediInvoiceBuilder;
import solutions.aon.in.invoice.tedi.TediInvoiceContext;

public class TediInvoicePDFParserTestCase {
	
	private enum TestTemplates {

		AMAZON_1 ("/solutions/aon/in/invoice/pdf/AMAZON_1.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2019, 1, 10, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 3; }
			@Override public int getDatesNumber(){ return 2; }
			@Override public int getAmountNumber(){ return 9; }
			@Override public int getTaxNumber(){ return 1; }
			@Override public int getTotalNumber(){ return 1; }
			@Override public Double getTotal(){ return 14.00; }
		},
		AMAZON_2 ("/solutions/aon/in/invoice/pdf/AMAZON_2.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2019, 2, 18, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 4; }
			@Override public int getDatesNumber(){ return 2; }
			@Override public int getAmountNumber(){ return 21; }
			@Override public Double getTotal(){ return 58.88; }
			@Override public int getTotalNumber(){ return 1; }
			@Override public int getTaxNumber(){ return 1; }
		},
		AMAZON_3 ("/solutions/aon/in/invoice/pdf/AMAZON_3.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2019, 4, 26, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 4; }
			@Override public int getDatesNumber(){ return 2; }
			@Override public int getAmountNumber(){ return 9; }
			@Override public Double getTotal(){ return 31.70; }
			@Override public int getTotalNumber(){ return 1; }
			@Override public int getTaxNumber(){ return 1; }
		},
		AMAZON_4 ("/solutions/aon/in/invoice/pdf/AMAZON_4.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2019, 1, 8, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 6; }
			@Override public int getDatesNumber(){ return 2; }
			@Override public int getAmountNumber(){ return 12; }
			@Override public Double getTotal(){ return 259.82; }
			@Override public int getTotalNumber(){ return 1; }
			@Override public int getTaxNumber(){ return 1; }
		},
		AMAZON_5 ("/solutions/aon/in/invoice/pdf/AMAZON_5.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2019, 2, 9, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 6; }
			@Override public int getDatesNumber(){ return 2; }
			@Override public int getAmountNumber(){ return 9; }
			@Override public Double getTotal(){ return 13.45; }
			@Override public int getTotalNumber(){ return 1; }
			@Override public int getTaxNumber(){ return 1; }
		},
		AMAZON_6 ("/solutions/aon/in/invoice/pdf/AMAZON_6.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2018, 12, 28, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 6; }
			@Override public int getDatesNumber(){ return 2; }
			@Override public int getAmountNumber(){ return 9; }
			@Override public Double getTotal(){ return 19.90; }
			@Override public int getTotalNumber(){ return 1; }
			@Override public int getTaxNumber(){ return 1; }
		},
		AMAZON_7 ("/solutions/aon/in/invoice/pdf/AMAZON_7.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2019, 4, 16, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 5; }
			@Override public int getDatesNumber(){ return 2; }
			@Override public int getAmountNumber(){ return 12; }
			@Override public Double getTotal(){ return 18.98; }
			@Override public int getTotalNumber(){ return 1; }
			@Override public int getTaxNumber(){ return 1; }
		},
		AYSER_1 ("/solutions/aon/in/invoice/pdf/AYSER_1.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2019, 1, 1, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 3; }
			@Override public int getDatesNumber(){ return 5; }
			@Override public int getAmountNumber(){ return 5; }
			@Override public Double getTotal(){ return 86.83; }
			@Override public int getTotalNumber(){ return 1; }
			@Override public int getTaxNumber(){ return 1; }
		},
		DOS_IVAS_1 ("/solutions/aon/in/invoice/pdf/DOS_IVAS_1.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2018, 11, 29, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 2; }
			@Override public int getDatesNumber(){ return 1; }
			@Override public int getAmountNumber(){ return 13; }
			@Override public Double getTotal(){ return 6549.72; }
			@Override public int getTotalNumber(){ return 0; }
			@Override public int getTaxNumber(){ return 2; }
		},
		DOS_IVAS_2 ("/solutions/aon/in/invoice/pdf/DOS_IVAS_2.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2018, 6, 28, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 4; }
			@Override public int getDatesNumber(){ return 7; }
			@Override public int getAmountNumber(){ return 20; }
			@Override public Double getTotal(){ return 309.57; }
			@Override public int getTotalNumber(){ return 1; }
			@Override public int getTaxNumber(){ return 2; }
		},
		DOS_IVAS_3 ("/solutions/aon/in/invoice/pdf/DOS_IVAS_3.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2018, 12, 28, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 4; }
			@Override public int getDatesNumber(){ return 7; }
			@Override public int getAmountNumber(){ return 24; }
			@Override public Double getTotal(){ return 349.28; }
			@Override public int getTaxNumber(){ return 2; }
			@Override public int getTotalNumber() {return 2;}
		},
		IBERDROLA_1 ("/solutions/aon/in/invoice/pdf/IBERDROLA_1.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2018, 11, 19, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 5; }
			@Override public int getDatesNumber(){ return 45; }
			@Override public int getAmountNumber(){ return 74; }
			@Override public int getTaxNumber(){ return 2; }			
			@Override public int getTotalNumber(){ return 3; }
			@Override public Double getTotal(){ return 835.95; }
		},
		IBERDROLA_2 ("/solutions/aon/in/invoice/pdf/IBERDROLA_2.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2018, 11, 19, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 5; }
			@Override public int getDatesNumber(){ return 45; }
			@Override public int getAmountNumber(){ return 73; }
			@Override public int getTaxNumber(){ return 2; }			
			@Override public int getTotalNumber(){ return 3; }
			@Override public Double getTotal(){ return 585.87; }
		},
	
/*
		MOVISTAR_1 ("/solutions/aon/in/invoice/pdf/MOVISTAR_1.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2018, 11, 19, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 1; }
			@Override public int getDatesNumber(){ return 6; }
			@Override public int getAmountNumber(){ return 11; }
			@Override public Double getTotal(){ return 20.66; }
			@Override public int getTotalNumber(){ return 1; }
			@Override public int getTaxNumber(){ return 2; }
		},

		MOVISTAR_2 ("/solutions/aon/in/invoice/pdf/MOVISTAR_2.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2018, 11, 19, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 1; }
			@Override public int getDatesNumber(){ return 5; }
			@Override public int getAmountNumber(){ return 9; }
			@Override public Double getTotal(){ return 100.18; }
			@Override public int getTotalNumber(){ return 1; }
			@Override public int getTaxNumber(){ return 1; }
		},
		MOVISTAR_3 ("/solutions/aon/in/invoice/pdf/MOVISTAR_3.pdf") {
			public int getDocumentsNumber(){ return 1; }
			public int getDatesNumber(){ return 6; }
			public int getAmountNumber(){ return 9; }
			public Double getTotal(){ return 84.58; }
			public int getTotalNumber(){ return 1; }
			public int getTaxNumber(){ return 1; }
		},
		MOVISTAR_4 ("/solutions/aon/in/invoice/pdf/MOVISTAR_4.pdf") {
			public int getDocumentsNumber(){ return 1; }
			public int getDatesNumber(){ return 5; }
			public int getAmountNumber(){ return 7; }
			public Double getTotal(){ return 89.12; }
			public int getTotalNumber(){ return 1; }
			public int getTaxNumber(){ return 1; }
		},
		MOVISTAR_5 ("/solutions/aon/in/invoice/pdf/MOVISTAR_5.pdf") {
			public int getDocumentsNumber(){ return 1; }
			public int getDatesNumber(){ return 4; }
			public int getAmountNumber(){ return 8; }
			public Double getTotal(){ return 87.99; }
			public int getTotalNumber(){ return 1; }
			public int getTaxNumber(){ return 1; }
		},
		
//		[ISSUE: Total factura inferior a total a pagar #2440]
		MOVISTAR_6 ("/solutions/aon/in/invoice/pdf/MOVISTAR_6.pdf") {
			public int getDocumentsNumber(){ return 1; }
			public int getDatesNumber(){ return 5; }
			public int getAmountNumber(){ return 8; }
			public Double getTotal(){ return 96.59; }
			public int getTotalNumber(){ return 1; }
			public int getTaxNumber(){ return 1; }
		},
		
		MOVISTAR_7 ("/solutions/aon/in/invoice/pdf/MOVISTAR_7.pdf") {
			public int getDocumentsNumber(){ return 1; }
			public int getDatesNumber(){ return 5; }
			public int getAmountNumber(){ return 28; }
			public Double getTotal(){ return 258.50; }
			public int getTotalNumber(){ return 1; }
			public int getTaxNumber(){ return 1; }
		},
*/
		
		NATURGY_1 ("/solutions/aon/in/invoice/pdf/NATURGY_1.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2018, 11, 16, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 2; }
			@Override public int getDatesNumber(){ return 34; }
			@Override public int getAmountNumber(){ return 89; }
			@Override public Double getTotal(){ return 429.86; }
			@Override public int getTotalNumber(){ return 1; }
			@Override public int getTaxNumber(){ return 1; }
		},
		NATURGY_2 ("/solutions/aon/in/invoice/pdf/NATURGY_2.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2018, 11, 16, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 2; }
			@Override public int getDatesNumber(){ return 10; }
			@Override public int getAmountNumber(){ return 77; }
			@Override public Double getTotal(){ return 421.39; }
			@Override public int getTotalNumber(){ return 1; }
			@Override public int getTaxNumber(){ return 1; }
		},
//		[ISSUE: Total factura negativo #2441]
//		NATURGY_3 ("/solutions/aon/in/invoice/pdf/NATURGY_3.pdf") {
//			public int getDocumentsNumber(){ return 2; }
//			public int getDatesNumber(){ return 9; }
//			public int getAmountNumber(){ return 77; }
//			public Double getTotal(){ return -428.46; }
//			public int getTotalNumber(){ return 1; }
//			public int getTaxNumber(){ return 2; }
//		},
		
		ORANGE_1 ("/solutions/aon/in/invoice/pdf/ORANGE_1.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2018, 9, 5, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public int getDocumentsNumber(){ return 5; }
			@Override public int getDatesNumber(){ return 5; }
			@Override public int getAmountNumber(){ return 452; }
			@Override public Double getTotal(){ return null; }	// 247.61
			@Override public int getTotalNumber(){ return 6; }
			@Override public int getTaxNumber(){ return 2; }
		},
		
/*		
		ORANGE_2 ("/solutions/aon/in/invoice/pdf/ORANGE_2.pdf") {
			public int getDocumentsNumber(){ return 6; }
			public int getDatesNumber(){ return 5; }
			public int getAmountNumber(){ return 495; }
			public Double getTotal(){ return null; }	// 151.71
			public int getTotalNumber(){ return 7; }
			public int getTaxNumber(){ return 12; }
		},
		ORANGE_3 ("/solutions/aon/in/invoice/pdf/ORANGE_3.pdf") {
			public int getDocumentsNumber(){ return 5; }
			public int getDatesNumber(){ return 5; }
			public int getAmountNumber(){ return 444; }
			public Double getTotal(){ return null; }	// 151.71
			public int getTotalNumber(){ return 5; }
			public int getTaxNumber(){ return 3; }
		},

	
		
//		RETENCION_1 ("/solutions/aon/in/invoice/pdf/RETENCION_1.pdf") {
//			public int getDocumentsNumber(){ return 2; }
//			public int getDatesNumber(){ return 1; }
//			public int getAmountNumber(){ return 5; }
//			public double getTotal(){ return 867.00; }
//			public int getVatsNumber(){ return 2; }
//		}

 */
		;

		private String file;
		
		private TestTemplates(String file) {
			this.file = file;
		}
		
		public String getFile() {
			return file;
		}
		
		public abstract Date getIssueDate();
		public abstract int getDocumentsNumber();
		public abstract int getDatesNumber();
		public abstract int getAmountNumber();
		public abstract int getTaxNumber();
		public abstract int getTotalNumber();
		public abstract Double getTotal();
	}

	@Test
	public void testTemplates() throws IOException, UnknownInvoiceException {
		int templ = 1;
		TediInvoiceContext ctx = new TediInvoiceContext()
				.setDocument("F01131978")
				.setName("UDAPA SOCIEDAD COOPERATIVA");
		
		for (TestTemplates template : TestTemplates.values()) {
			try (InputStream is = TediInvoicePDFParserTestCase.class.getResourceAsStream(template.getFile())) {
				System.out.println("-----------------------------------------------");
				System.out.println("\t\tParsing " + template.getFile() + " ....");
				System.out.println("-----------------------------------------------");
				TediInvoiceBuilder tediInvoiceBuilder = new TediInvoiceBuilder( ctx );
				InvoicePDFParser.parse(is,tediInvoiceBuilder);
				System.out.println( 
						TediInvoiceJSON.toJSON(tediInvoiceBuilder.getInvoice()).toString(1)
					);
				System.out.println("-----------------------------------------------");
				
				TediInvoice tedi = tediInvoiceBuilder.getInvoice();
				String file = "["+ template.getFile() +"]. "; 
				assertNotNull(file + "Not parsed!",tedi);
				assertNotNull(file + "Invoice has no receiver!",tedi.getReceiver());
				assertNotNull(file + "Invoice has no sender!",tedi.getSender());
				
				assertNotNull(file + "Invoice has no date!",tedi.getDate());
				assertEquals(file + " date does not match!", template.getIssueDate(),tedi.getDate());

				
				assertNotNull(file + "Invoice has no total!",tedi.getTotal());
				assertNotNull(file + "Invoice has no taxes!",tedi.getTaxes());
				double total = 0.0;
				for ( TediInvoiceTax tax : tedi.getTaxes()) {
					if (tax.getTaxType() == TediTaxType.IVA) {
						total = total + tax.getBase() + tax.getQuota();
					} else {
						total = total - tax.getQuota();
					}
				}
				assertEquals(file + "Total attribute does not match calculated total",tedi.getTotal(),total, 0.001);
/*				
				
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
*/				
				templ++;
			}
			System.out.println( "\t\t " + templ + " INVOICES PARSED.");
		}
	}
}
