package com.esferalia.aon.occam.test.fiscal.invoice;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonRandom;

public class InsertRandomInvoicesTest extends AbstractOccamTest {

	@Test
	public void test() {
		AonRandom.generateRandomInvoices(ctx, getTestDate(), AonRandom.getInt(1, 10));
		assertTrue( 
			AON.getInvoiceHeaders(ctx, p -> p.getDomainProperty().eq(DOMAIN_ID), 0, 1)
				.findFirst()
				.isPresent()
		);
	}

}
