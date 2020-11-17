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
import es.translogia.tedi.ewok.TediNif;
import es.translogia.tedi.ewok.TediTaxType;
import es.translogia.tedi.json.TediInvoiceJSON;
import solutions.aon.in.invoice.UnknownInvoiceException;
import solutions.aon.in.invoice.pdf.InvoicePDFParser;
import solutions.aon.in.invoice.tedi.TediInvoiceBuilder;
import solutions.aon.in.invoice.tedi.TediInvoiceContext;

public class TediInvoicePDFParserAonDemoTestCase {
	
	private enum TestTemplates {

		
		AON_01_AUSARTA ("/solutions/aon/in/invoice/pdf/AON-01-AUSARTA.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2020, 10, 2, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public String getReference(){ return "PR2020023542"; }
			@Override public int getDocumentsNumber(){ return 2; }
			@Override public int getDatesNumber(){ return 4; }
			@Override public int getAmountNumber(){ return 64; }
			@Override public int getTaxNumber(){ return 1; }
			@Override public int getTotalNumber(){ return 1; }
			@Override public Double getTotal(){ return 641.77; }
		},
		AON_02_BNP ("/solutions/aon/in/invoice/pdf/AON-02-BNP.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2020, 10, 1, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public String getReference(){ return "FBF71670"; }
			@Override public int getDocumentsNumber(){ return 2; }
			@Override public int getDatesNumber(){ return 3; }
			@Override public int getAmountNumber(){ return 10; }
			@Override public int getTaxNumber(){ return 1; }
			@Override public int getTotalNumber(){ return 1; }
			@Override public Double getTotal(){ return 120.94; }
		},
		AON_04_BIP_DRIVE ("/solutions/aon/in/invoice/pdf/AON-04-BIP-DRIVE.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2020, 10, 31, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public String getReference(){ return "CI0003829550-1020"; }
			@Override public int getDocumentsNumber(){ return 2; }
			@Override public int getDatesNumber(){ return 3; }
			@Override public int getAmountNumber(){ return 4; }
			@Override public int getTaxNumber(){ return 1; }
			@Override public int getTotalNumber(){ return 1; }
			@Override public Double getTotal(){ return 2.11; }
		},
		AON_05_BIP_DRIVE ("/solutions/aon/in/invoice/pdf/AON-05-BIP-DRIVE.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2020, 9, 30, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public String getReference(){ return "CI0003726395-0920"; }
			@Override public int getDocumentsNumber(){ return 2; }
			@Override public int getDatesNumber(){ return 5; }
			@Override public int getAmountNumber(){ return 7; }
			@Override public int getTaxNumber(){ return 1; }
			@Override public int getTotalNumber(){ return 1; }
			@Override public Double getTotal(){ return 46.74; }
		},
		AON_06_TRANSLOGIA("/solutions/aon/in/invoice/pdf/AON-06-TRANSLOGIA.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2020, 2, 1, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public String getReference(){ return "CENIT/000023"; }
			@Override public int getDocumentsNumber(){ return 2; }
			@Override public int getDatesNumber(){ return 2; }
			@Override public int getAmountNumber(){ return 7; }
			@Override public int getTaxNumber(){ return 1; }
			@Override public int getTotalNumber(){ return 1; }
			@Override public Double getTotal(){ return 2480.50; }
		},
		AON_07_TRANSLOGIA("/solutions/aon/in/invoice/pdf/AON-07-TRANSLOGIA.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2020, 8, 1, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public String getReference(){ return "CENIT/000035"; }
			@Override public int getDocumentsNumber(){ return 2; }
			@Override public int getDatesNumber(){ return 2; }
			@Override public int getAmountNumber(){ return 4; }
			@Override public int getTaxNumber(){ return 2; }
			@Override public int getTotalNumber(){ return 1; }
			@Override public Double getTotal(){ return 2091.00; }
		},
		AON_08_TOLEDO("/solutions/aon/in/invoice/pdf/AON-08-TOLEDO.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2020, 10, 13, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public String getReference(){ return "C/023452"; }
			@Override public int getDocumentsNumber(){ return 2; }
			@Override public int getDatesNumber(){ return 3; }
			@Override public int getAmountNumber(){ return 3; }
			@Override public int getTaxNumber(){ return 1; }
			@Override public int getTotalNumber(){ return 1; }
			@Override public Double getTotal(){ return 223.85; }
		},
		AON_09_VODAFONE ("/solutions/aon/in/invoice/pdf/AON-09-VODAFONE.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2020, 11, 1, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public String getReference(){ return "ZB20-000748377"; }
			@Override public int getDocumentsNumber(){ return 2; }
			@Override public int getDatesNumber(){ return 6; }
			@Override public int getAmountNumber(){ return 17; }
			@Override public int getTaxNumber(){ return 1; }
			@Override public int getTotalNumber(){ return 1; }
			@Override public Double getTotal(){ return 105.50; }
		},
		AON_10_VUELING ("/solutions/aon/in/invoice/pdf/AON-10-VUELING.pdf") {
			@Override public Date getIssueDate() {return Date.from(LocalDateTime.of(2020, 10, 9, 0, 0).atZone(ZoneId.systemDefault()).toInstant());}
			@Override public String getReference(){ return "C202000000327308"; }
			@Override public int getDocumentsNumber(){ return 2; }
			@Override public int getDatesNumber(){ return 3; }
			@Override public int getAmountNumber(){ return 17; }
			@Override public int getTaxNumber(){ return 1; }
			@Override public int getTotalNumber(){ return 1; }
			@Override public Double getTotal(){ return 163.98; }
		},
		;
//		file:///home/ecastellano/TRABAJO/FACTURAS%20DEMO/AON-04-BIP-DRIVE.pdf
//		file:///home/ecastellano/TRABAJO/FACTURAS%20DEMO/AON-05-BIP-DRIVE.pdf

		private String file;
		
		private TestTemplates(String file) {
			this.file = file;
		}
		
		public String getFile() {
			return file;
		}
		
		public abstract Date getIssueDate();
		public abstract String getReference();
		public abstract int getDocumentsNumber();
		public abstract int getDatesNumber();
		public abstract int getAmountNumber();
		public abstract int getTaxNumber();
		public abstract int getTotalNumber();
		public abstract Double getTotal();
	}

	@Test
	public void testTemplates() throws IOException, UnknownInvoiceException {
		int templ = 0;
		TediInvoiceContext ctx = new TediInvoiceContext()
				.setDocument("B01487271")
				.setName("AON SOLUTIONS SL");
		
		for (TestTemplates template : TestTemplates.values()) {
			try (InputStream is = TediInvoicePDFParserAonDemoTestCase.class.getResourceAsStream(template.getFile())) {
				System.out.println("-----------------------------------------------");
				System.out.println("\t\tParsing " + template.getFile() + " ....");
				System.out.println("-----------------------------------------------");
				TediInvoiceBuilder tediInvoiceBuilder = new TediInvoiceBuilder( ctx );
				if ( template == TestTemplates.AON_07_TRANSLOGIA) {
					System.out.println("d----");
				}
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

				assertEquals(template.getFile() + " must parse " + template.getReference() + " reference!"
						,template.getReference(),tedi.getReference());

				System.out.println( "\tDocuments: (expected: " + template.getDocumentsNumber() + ")" );
				int i = 1;
				for (TediNif doc : tedi.getInsight().getNifs()) {
					System.out.println( "\t\t"+i+++".-\t"+doc.getStr() );
				}
				assertEquals(template.getFile() + " must parse " + template.getDocumentsNumber() + " documents!"
						,template.getDocumentsNumber(), tedi.getInsight().getNifs().length);
				
				assertNotNull(template.getFile() + " has no dates!",tedi.getInsight().getDates());
				System.out.println( "\tDates: (expected: " + template.getDatesNumber() + ")" );
				i = 1;
				for (Date date : tedi.getInsight().getDates()) {
					System.out.println( "\t\t"+i+++".-\t"+date);
				}
				assertEquals(template.getFile() + " must parse " + template.getDatesNumber() + " dates!"
						,template.getDatesNumber(),tedi.getInsight().getDates().length);
				
				assertNotNull(template.getFile() + " has no amounts!",tedi.getInsight().getAmounts());
				System.out.println( "\tAmounts: (expected: " + template.getAmountNumber() + ")" );
				i = 1;
				for (Double amount: tedi.getInsight().getAmounts()) {
					System.out.println( "\t\t"+i+++".-\t"+amount);
				}
				assertEquals(template.getFile() + " must parse " + template.getAmountNumber() + " amounts!"
						,template.getAmountNumber(),tedi.getInsight().getAmounts().length);

				System.out.println( "\tTAXES: (expected: " + template.getTaxNumber() + ")" );
				assertNotNull(template.getFile() + " has no taxes!",tedi.getTaxes());
				i = 1;
				for (TediInvoiceTax vat: tedi.getTaxes()) {
					System.out.println( "\t\t"+i+++".-\t"
						+vat.getTaxType() + " \t"
						+vat.getBase() + " \t"
						+vat.getPercentage() +"% \t"
						+vat.getQuota() + " \t"
					);
				}
				assertEquals(template.getFile() + " must parse " + template.getTaxNumber() + " taxes!"
						,template.getTaxNumber(),tedi.getTaxes().size());

				System.out.println( "\tTOTAL: (expected: " + template.getTotal() + ")" );
				assertNotNull(template.getFile() + " has no total!",tedi.getTotal());
				assertEquals(template.getFile() + " total not match: ",template.getTotal() , tedi.getTotal().doubleValue(),0);
				System.out.println( "\t\tTOTAL ...: " +tedi.getTotal());

				double total = 0.0;
				for ( TediInvoiceTax tax : tedi.getTaxes()) {
					if (tax.getTaxType() == TediTaxType.IVA) {
						total = total + tax.getBase() + tax.getQuota();
					} else {
						total = total - tax.getQuota();
					}
				}
				assertEquals(file + "Total attribute does not match calculated total",tedi.getTotal(),total, 0.001);
				
				System.out.println("-----------------------------------------------");
				System.out.println();

				templ++;
			}
			System.out.println("-----------------------------------------------");
			System.out.println( "\t " + templ + " INVOICES PARSED.");
		}
	}
}
