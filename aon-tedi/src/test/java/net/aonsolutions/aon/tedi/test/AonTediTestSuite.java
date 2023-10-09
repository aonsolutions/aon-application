package net.aonsolutions.aon.tedi.test;


import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

import net.aonsolutions.aon.tedi.test.pdf.PDF2AONTestCase;
import net.aonsolutions.aon.tedi.test.pdf.TediInvoicePDFParserAonDemoTestCase;
import net.aonsolutions.aon.tedi.test.invofox.InvofoxToAonTestCase;

@Suite
@SelectClasses({
	TediValidationTest.class,
	TediInvoicePDFParserAonDemoTestCase.class,
	PDF2AONTestCase.class,
	InvofoxToAonTestCase.class,
//	TediInvoiceIMGParserAonDemoTestCase.class,
})
public class AonTediTestSuite {

	
}
