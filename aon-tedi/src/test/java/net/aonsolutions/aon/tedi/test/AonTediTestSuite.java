package net.aonsolutions.aon.tedi.test;


import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import net.aonsolutions.aon.tedi.test.img.TediInvoiceIMGParserAonDemoTestCase;
import net.aonsolutions.aon.tedi.test.pdf.PDF2AONTestCase;
import net.aonsolutions.aon.tedi.test.pdf.TediInvoicePDFParserAonDemoTestCase;

@RunWith(Suite.class)
@SuiteClasses({
	TediValidationTest.class,
	TediInvoicePDFParserAonDemoTestCase.class,
	PDF2AONTestCase.class,
	TediInvoiceIMGParserAonDemoTestCase.class,
})
public class AonTediTestSuite {

	
}
