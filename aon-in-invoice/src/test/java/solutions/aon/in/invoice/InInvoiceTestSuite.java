package solutions.aon.in.invoice;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import solutions.aon.in.invoice.img.tedi.TediInsightInvoiceIMGParserTestCase;
import solutions.aon.in.invoice.pdf.AmountParserTestCase;
import solutions.aon.in.invoice.pdf.DateParserTestCase;
import solutions.aon.in.invoice.pdf.DocumentParserTestCase;
import solutions.aon.in.invoice.pdf.DocumentTypeParserTestCase;
import solutions.aon.in.invoice.pdf.tedi.TediInsightInvoicePDFParserTestCase;

@RunWith(Suite.class)
@SuiteClasses({
	DateParserTestCase.class,
	AmountParserTestCase.class,
	DocumentParserTestCase.class,
	DocumentTypeParserTestCase.class,
	TediInsightInvoicePDFParserTestCase.class,
	TediInsightInvoiceIMGParserTestCase.class,
})
public class InInvoiceTestSuite {

	
}
