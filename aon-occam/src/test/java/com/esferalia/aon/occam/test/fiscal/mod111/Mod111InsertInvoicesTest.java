package com.esferalia.aon.occam.test.fiscal.mod111;

import java.text.MessageFormat;

import org.junit.Assert;
import org.junit.Test;

import com.esferalia.aon.occam.api.AON;
import com.esferalia.aon.occam.test.AbstractOccamTest;
import com.esferalia.aon.occam.test.faker.AonRandom;

public class Mod111InsertInvoicesTest extends AbstractOccamTest {

	@Test
	public void test() {
		int times = AonRandom.getInt(1, 50);
		for ( int i = 0; i < times; i++) {
			AonRandom.generateRandomRetentionInvoices(ctx,getOccam(),getConfiguration(),AonRandom.getRandomWithholdingType());	
		}
		System.out.println(MessageFormat.format("\t\t {0} Invoices inserted ", times ));
		
		Assert.assertTrue( AON.getInvoiceStream(getOccam(), p -> p.getDomainProperty().eq(DOMAIN_ID))
				.findFirst()
				.isPresent()
			);
	}

}
