package com.esferalia.aon.occam.impl.jooq.dao.invoice;



import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.model.InvoiceCalculator;
import com.esferalia.aon.occam.api.model.finance.Invoice;
import com.esferalia.aon.occam.api.model.finance.InvoiceDetail;
import com.esferalia.aon.occam.impl.jooq.dao.accounting.invoice.InvoiceTextPrinter;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.InvoiceFaker;
import com.esferalia.aon.watson.util.AonCollectionUtils;


class InvoiceCalculatorTest extends AbstractOccamTest {

	@Test
	void testCalculate() {
		Invoice inv = InvoiceFaker.getRandom(ctx);
InvoiceTextPrinter.print(inv);
System.out.println();
System.out.println("----------------------------------------------------------------------------------------------");
System.out.println();
		assertTrue(AonCollectionUtils.isNotEmpty(inv.getDetails()));
		InvoiceDetail detail = InvoiceFaker.getRandomDetail(inv);
		assertNotNull(detail);
		detail.setPrice(detail.getPrice() + 10);
		InvoiceCalculator.calculate(inv);
InvoiceTextPrinter.print(inv);
	}
}
